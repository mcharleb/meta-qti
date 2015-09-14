//==============================================================================
//
//  @@-COPYRIGHT-START-@@
//
//  Copyright 2015 Qualcomm Technologies, Inc. All rights reserved.
//  Confidential & Proprietary - Qualcomm Technologies, Inc. ("QTI")
//
//  The party receiving this software directly from QTI (the "Recipient")
//  may use this software as reasonably necessary solely for the purposes
//  set forth in the agreement between the Recipient and QTI (the
//  "Agreement"). The software may be used in source code form solely by
//  the Recipient's employees (if any) authorized by the Agreement. Unless
//  expressly authorized in the Agreement, the Recipient may not sublicense,
//  assign, transfer or otherwise provide the source code to any third
//  party. Qualcomm Technologies, Inc. retains all ownership rights in and
//  to the software
//
//  This notice supersedes any other QTI notices contained within the software
//  except copyright notices indicating different years of publication for
//  different portions of the software. This notice does not supersede the
//  application of any third party copyright notice to that third party's
//  code.
//
//  @@-COPYRIGHT-END-@@
//
//==============================================================================

#include <cstring>
#include <unistd.h>
#include <pthread.h>
#include <sched.h>
#include <sys/time.h>
#include <stdlib.h>
#include <signal.h>
#include <assert.h>

#include "camera_parameters.h"
#include "fpv_stream.hpp"
#include "fpv_encoder.hpp"
#include "fpv_queue.hpp"
#include "fpv_rtsp.hpp"
#include "fpv_utils.hpp"
#include "fpv_dbg.hpp"

#define STD_OFFSETOF(type,member)     (((char*)(&((type*)1)->member))-((char*)1))

#define STD_RECOVER_REC(type,member,p) ((void)((p)-&(((type*)1)->member)),\
                                        (type*)(void*)(((char*)(void*)(p))-STD_OFFSETOF(type,member)))

////////internal structs ///////////////////////////
struct FPV_FLOWCONTROL_S {
    bool      m_Initialized;
    bool      m_Started;    

    void*  encoderqueue;
    void*  encodermemcache;

    void*  rtspqueue;
    void*  rtspmemcache;

    void* fpvqueue;
    pthread_t m_StreamThreadId;
    fpv_rtsp_type  stream_type;

    fpv_jpeg_params  jpg_param;

    camera::ICameraDevice* icd_;
    class fpvListener : public camera::ICameraListener {
        struct FPV_FLOWCONTROL_S* getme(void) {
            return STD_RECOVER_REC(struct FPV_FLOWCONTROL_S, listener_, this);
        }
    public:
        fpvListener() {}
        virtual void onPreviewFrame(camera::ICameraFrame*);
    } listener_;

    char  ifacename[16];
    int   default_encoder_thread_pri;
    int   default_rtsp_thread_pri;
};

typedef struct FPV_FLOWCONTROL_S fpv_flowcontrol;

typedef struct FPV_CAM_SETTING_S {
    /* the pic sz for encoder */
    int width;
    int height;
    int stride;

    float fps;
  // camera_type_t  cam_id; // 0 back, 1 front
    int frame_sz;
    bool  do_scale; // do we need do soft scale for cam input to fit encoder input
} fpv_cam_params;


typedef struct ENCODER_SETTING_S {
    int width;
    int height;
    int stride;
    int fps;
    unsigned char quality; // for jpeg
}encoder_setting;

typedef struct cfg_map_s {
    char name[32];
    int  len;
    int  type;// 0 int, 1 str
}cfgmap;


#define  PAYLOAD_CAM_TIME_OFFSET   4
#define  PAYLOAD_CAM_YUV_OFFSET   20

#define CFG_ITEM_NUM  6

#define DEFAULT_CAM_WIDTH   640
#define DEFAULT_CAM_HEIGHT   480
#define DEFAULT_JPEG_QUALITY   40
#define DEFAULT_CAM_FPS        30
/////////////////global var ////////////////////////////////////////////
//static const   camera_type_t CAMERA_TYPE = CAMERA_BYKUGAN;
static timeval starttm;
int framecount = 0;
int debuglv = 0;
int debuginfo = 0;
int debugerror = 0;
// TBD will read this from env vars in the future. 
const char* config_file_name="/etc/fpv.cfg";

static fpv_cam_params  __cam_params;
static fpv_flowcontrol __fpv_fc = {
                        .m_Initialized = false,
                        .m_Started = false,
                        .encoderqueue = NULL,
                        .encodermemcache = NULL,
                        .rtspqueue = NULL,
                        .rtspmemcache = NULL
};

static cfgmap km[CFG_ITEM_NUM]={"width",5,0,
                                "height",6,0,
                                "quality",7,0,
                                "bitrate",7,0,
                                "debug",5,0,
                                "net",3,1};

///////////////functions ///////////////////////////////////////////////////
/**
 * @brief conver frame from yuv420sp to yuv420p
 *
 * @return NULL
 */
int __copy_frame_to_buf(camera::ICameraFrame* frame, uint8_t *buf)
{
    int i, j;
    int stride = PAD_TO_SIZE(__cam_params.width, 16);
    int scanline = PAD_TO_SIZE(__cam_params.height, 2);
    int sz = __cam_params.width*__cam_params.height;
    int plane0_offset = 0;
    int plane1_offset = stride*scanline;
    unsigned char * src = frame->data  + plane0_offset;
    unsigned char * uvsrc = frame->data + plane1_offset;
    unsigned char * udst = buf + sz;
    unsigned char * vdst = udst+((sz)>>2);
    memcpy(buf, src, sz);// y copy directly
    for ( int l = 0;l< (__cam_params.height>>1);l++) {
       int c = (__cam_params.width>>1); 
       while( c--) {
           *vdst++=*uvsrc++;
           *udst++= *uvsrc++;
        }

    }
    return 0;
}

/**
 * @brief main entry point for encoder thread. 
 *        it has a msg loop to get msgs and data.
 *        if data comes, it encode it into bit streams and 
 *        send to sink thread
 * @return NULL
 */
void* fpv_encoder_thread(void * data) {
   CDBG_INFO(debuginfo, "In %s\n", __PRETTY_FUNCTION__);
   fpv_encoder_interface *encoder_api = (fpv_encoder_interface*) data;
   int msgid;
   void* msgbuf;
   int msgrc = 0;
   int datarc = 0;
   if (!__fpv_fc.m_Initialized)
        return NULL;

    while(1)   {
        unsigned char *buf = NULL;
        CDBG_INFO(debuginfo, "encoder handler msg, waitting dequeue !!!!!!!!!!!!!!!!! \n");
        msgbuf = dequeue_msg_data(__fpv_fc.encoderqueue, &msgrc, &msgid, &datarc);
        CDBG_INFO(debuginfo, "encoder handler msg, done__________ dequeue !!!!!!!!!!!!!!!!! \n");
        if ( msgrc != FPV_NO_ERROR && datarc == FPV_NO_ERROR)
            buf = (unsigned char *) msgbuf;

        if ( msgrc == FPV_NO_ERROR) {
            //handle message 
            CDBG_INFO(debuginfo, "encoder handler msg %d \n", msgid);
            encoder_api->msg_handler( msgid, msgbuf);
                continue;
        } else {
            if ( buf == NULL) {// error happened!
                CDBG_ERROR(debugerror, "error happened in encoder queue, no msg and data fetched ! \n");
                continue; 
            } else {
                reportTimestart(starttm);
                int fmcount = *((int*)buf);
                CDBG_INFO(debuginfo, "encoder take yuv frame from queue %d at sec %ld , usec %ld \n", fmcount, starttm.tv_sec, starttm.tv_usec);                    
               
                struct timeval camtm = *((struct timeval*)(buf+PAYLOAD_CAM_TIME_OFFSET));
                encoder_api->add_frame(buf+PAYLOAD_CAM_YUV_OFFSET, fmcount, camtm);
                putback_cached_mem(__fpv_fc.encodermemcache, buf);
                reportTimestart(starttm);
                CDBG_INFO(debuginfo, "encoder done jpg %d at sec %ld , usec %ld \n", fmcount, starttm.tv_sec, starttm.tv_usec);
            }
        }
    }
    printf(" the encoder thread done \n");
   
    return NULL;
}


/**
 * @brief get the str vale from a config file line
 * @params  msg: src line str.
 *          res: dst str.
 *          maxreslen: max length to retrieve from src       
 * @return NULL
 */
int __getstrdata(char *msg, char* res, int maxreslen) {
    int l = strlen(msg);
    int k = 0;
    char *buf = NULL;
    for ( int i = 0;i< l-1;i++) {
        if ( msg[i]=='=') {
           buf = &msg[i+1];
           k=i+1;
           break;
        }
    }    
    int j = maxreslen > (l-k)? (l-k):maxreslen;

    if( buf != NULL) {
        for ( int i = 0; i< j;i++)
            res[i] = buf[i];
    } else return 1;
    if ( res[j-1] ==  0x0a && j> 1)
        res[j-1] = 0;
    if ( res[j-2] == 0x0d && j > 2)
        res[j-2] = 0;
    return 0;
}

/**
 * @brief get the int vale from a config file line
 * @params  msg: src line str.
 *
 * @return int value
 */
int __getinitdata(char *msg) {
    int l = strlen(msg);
    char *buf = NULL;
    for ( int i = 0;i< l-1;i++) {
        if ( msg[i]=='=') {
           buf = &msg[i+1];
           break;
        }
    }
    if( buf != NULL) {
        int res = strtol( buf, NULL, 0);
        CDBG_INFO(debuginfo, "parse config return val %d \n", res);
        return res;//atoi(buf);
    }
    
    return 0;
}

/**
 * @brief read values from config file.
 * @params  id: config file map id.
 *          data: config file data
 * @return  void
 */
void __assigncfgvalue(int id, char* data)
{
    int val;
    if ( id == 0) {
        val = __getinitdata(data);
        if ( val > 0) {
            __cam_params.width= val;        
            __cam_params.stride = __cam_params.width;
        }
    } else if ( id == 1) {
        val = __getinitdata(data);
        if ( val > 0) {
            __cam_params.height= val;        
        }
    } else if ( id == 2) {
        int val = __getinitdata(data);
        if ( val <= 100) {
            __fpv_fc.jpg_param.start_quality = (unsigned char) val;
        } else
            __fpv_fc.jpg_param.start_quality = 90;
    } else if ( id == 4) {
        debuglv = __getinitdata(data);
        if ( debuglv >=2) debugerror =1;
        if ( debuglv >=1) debuginfo = 1;
        CDBG_INFO(debuginfo, "parse config debug thres = %d \n", debuglv);
    } else if ( id == 5) {
        __getstrdata( data, __fpv_fc.ifacename , 16);
        CDBG_INFO(debuginfo, "parse config use iface %s \n", __fpv_fc.ifacename);
    }

}

/**
 * @brief parse the config file, the file is in fixed pos /etc/fpv.cfg
 *  *
 * @return void
 */
void __parse_config_file()
{
    //char *filename="/etc/fpv.cfg";
    FILE *fp = fopen(config_file_name, "r");
    char linemsg[128];

    if ( fp == NULL )
        return;

    while (NULL != fgets( linemsg, 127,fp)) {
        if ( linemsg[0] == '#') 
            continue;
        bool found = false;
        for ( int i = 0;i< CFG_ITEM_NUM;i++) {
            found = true;
            for( int j = 0;j< km[i].len;j++) {
                if ( km[i].name[j]!= linemsg[j]) {
                    found = false;
                    break;
                }
            }
            if ( found == true) {
                __assigncfgvalue(i, linemsg);
                break;
            } 
        } 
    }
    return;
}

/**
 * @brief send out start preview command to fpv fc thread
 * 
 * @return void
 */
void fpv_startpreview(){
    CDBG_INFO(debuginfo,"start preview now \n");
    put_msg_into_queue( FLOW_MSG_START_PREVIEW, NULL, __fpv_fc.fpvqueue);
}

/**
 * @brief send out stop preview command to fpv fc thread
 * 
 * @return void
 */
void fpv_stoppreview() {
    CDBG_INFO(debuginfo,"stop preview now \n");
    put_msg_into_queue( FLOW_MSG_STOP_PREVIEW, NULL, __fpv_fc.fpvqueue);      
}

/**
 * @brief send out quit command to fpv fc thread
 * 
 * @return void
 */
void __send_quit_msg_to_fc() {
    put_msg_into_queue( FLOW_MSG_QUIT, NULL, __fpv_fc.fpvqueue);      
}
/**
 * @brief send out stop encoder command to fpv encoder thread
 * 
 * @return void
 */   
void __startEncoder() {
    CDBG_INFO(debuginfo,"start encoder cmd now \n");
   if ( __fpv_fc.stream_type == RTP_JPEG) {
       put_msg_into_queue( ENCODER_CMD_START, NULL, __fpv_fc.encoderqueue);       
   }
   return;
}
/**
 * @brief send out quit encoder command to fpv encoder thread
 * 
 * @return void
 */
void __quitEncoder() {
   CDBG_INFO(debuginfo,"quit encoder cmd now \n");
   if ( __fpv_fc.stream_type == RTP_JPEG) {
       put_msg_into_queue( ENCODER_CMD_QUIT, NULL, __fpv_fc.encoderqueue);       
   }
}

/**
 * @brief send out setup encoder params command to fpv encoder thread
 * 
 * @return void
 */
void __setupEncoderParams() {
   if ( __fpv_fc.stream_type == RTP_JPEG) {
       fpv_jpeg_params* jp = (fpv_jpeg_params*) malloc( sizeof(fpv_jpeg_params));
       jp->start_quality = __fpv_fc.jpg_param.start_quality;
       jp->lowest_quality = 30;
       jp->target_frm_sz = 40*1024;
       jp->width = __cam_params.width;
       jp->height = __cam_params.height;       
       put_msg_into_queue( ENCODER_CMD_SETPARAS, jp, __fpv_fc.encoderqueue);
   } else {
       CDBG_INFO(debuginfo," format other than jpeg not support yet! \n");
   }
}

/**
 * @brief callback function for camera
 * @params the frame
 * @return void
 */
void FPV_FLOWCONTROL_S::fpvListener::onPreviewFrame(camera::ICameraFrame* pframe)
{
    struct timeval camstm;
    reportTimestart(camstm);
    CDBG_INFO(debuginfo, "cam get frame %d at sec %ld , usec %ld, report tm is usec %lld\n",
      ++framecount, camstm.tv_sec, camstm.tv_usec, pframe->timeStamp);
    
    if (getme() != &__fpv_fc) {
        CDBG_ERROR(debugerror, "!!!!! unexpected pointer !!!!!\n");
        assert(0);
    }
    uint8_t * cachedbuf = (uint8_t *)get_cached_mem_nowait(__fpv_fc.encodermemcache);
    if ( cachedbuf == NULL) { // drop a frame at camera
        CDBG_INFO(debuginfo, "cam callback , can not get cached memory, drop a frame \n");
        return;
    } else {
        int* fmcount = (int*)cachedbuf;
        *fmcount = framecount;
        struct timeval *ptv = (struct timeval *) (cachedbuf + PAYLOAD_CAM_TIME_OFFSET);
        memcpy(ptv, &camstm, sizeof(struct timeval));

        #if 1
        /* TODO: Skip __copy_frame_to_buf once yuv420p preview format is working */
        __copy_frame_to_buf(pframe, cachedbuf+PAYLOAD_CAM_YUV_OFFSET);
        #else
        memcpy(cachedbuf+PAYLOAD_CAM_YUV_OFFSET,
               pframe->data,
               YUV420_BUF_SIZE(__cam_params.width, __cam_params.height));
        #endif
    }

    int rc = enqueue_data_nowait(__fpv_fc.encoderqueue, cachedbuf);
    if ( rc != FPV_NO_ERROR) {
         CDBG_INFO(debuginfo, "cam queue full or can not get lock, drop frame %d at camera \n", framecount);
         putback_cached_mem_nowait( __fpv_fc.encodermemcache, cachedbuf);
    }
    release_cached_mem_lock();
}


/**
 * @brief set up for preview
 * @return void
 */
void __setupCameraParams() {
    camera::CameraParams params;

    __fpv_fc.icd_->addListener(&__fpv_fc.listener_);

    params.init(__fpv_fc.icd_);

/*  This will be set in qcamvid
    params.setPreviewSize(camera::ImageSize(__cam_params.width,
                                            __cam_params.height));
*/
    // TODO: Causing failure in commit(). Need to fix support for FPS range.
    //params.setPreviewFpsRange(camera::Range(camera::VIDEO_FPS_30, 
    //                                        camera::VIDEO_FPS_30, 0));
    //params.set(std::string("preview-format"), std::string("yuv420p"));

    if (0 != params.commit()) {
        CDBG_ERROR(debugerror, "!!!!! camera configure failed:  !!!!!\n");
    }

    camera::ImageSize imgSize = params.getPreviewSize();

    __cam_params.width = imgSize.width;
    __cam_params.height = imgSize.height;
    __cam_params.frame_sz = YUV420_BUF_SIZE(__cam_params.width, __cam_params.height);
    __cam_params.stride = __cam_params.width;
}

/**
 * @brief signal handler for fpv
 * @params signal no
 * @return void
 */
void __sigroutine(int signo) {
    CDBG_INFO(debuginfo,"signal received \n");

    __fpv_fc.icd_->stopPreview();
    sleep(1);  /* to ensure the callbacks complete before shutting down. 
               ** todo: implementation of camera service should handle this better. */
    __fpv_fc.icd_->removeListener(&__fpv_fc.listener_);
    camera::ICameraDevice::deleteInstance(&__fpv_fc.icd_);

    stop_rtsp_server();
    __quitEncoder();
    __send_quit_msg_to_fc();
    
}

/**
 * @brief install signal handler for fpv
 * 
 * @return void
 */
void __installsig() {
    struct sigaction act;  

    sigemptyset(&act.sa_mask);

    act.sa_flags=SA_RESETHAND;
    act.sa_handler= __sigroutine;

    if(sigaction(SIGTERM,&act,NULL) < 0)  {
        CDBG_INFO(debuginfo,"install sigal error\n");
    } else {
        CDBG_INFO(debuginfo,"install for sigTERM \n"); 
    }
}

/**
 * @brief set up the fpv app with proper params and do all initialize work
 *
 * @return 0 for success. other values for errors
 */
int fpv_initialize(camera::ICameraDevice* icd)
{
    // write log to syslog
   openlog(NULL, LOG_CONS|LOG_PID, LOG_USER);
   CDBG_INFO(debuginfo, "START THE FPV INITIALIZE \n");

   if(__fpv_fc.m_Initialized == true)
   {
      CDBG_ERROR(debugerror, "fpv_stream::initialize Already initialized");
      return FPV_WRONG_STATE;
   }
   __fpv_fc.default_encoder_thread_pri = 120;
   __fpv_fc.default_rtsp_thread_pri = 120;
   
   //initialize the camera with default vals
   __cam_params.width=DEFAULT_CAM_WIDTH;
   __cam_params.height = DEFAULT_CAM_HEIGHT;
   __cam_params.fps = DEFAULT_CAM_FPS;
   __fpv_fc.jpg_param.start_quality = DEFAULT_JPEG_QUALITY;

   __parse_config_file();
   __fpv_fc.m_Initialized = true;

   __fpv_fc.icd_ = icd;
   if(NULL == __fpv_fc.icd_) {
      CDBG_ERROR(debugerror, "!!!!! camera init failed:  !!!!!\n");
      return FPV_WRONG_STATE;
   }
   
   // set up camera with right params.
   __setupCameraParams();

   // init the global memory cache instance
   mem_cache_create();
  
   // queue size to 1 frame, max to 3 frame for encoder. otherwise it will introduce too much delay
   __fpv_fc.encoderqueue  =init_queue(1);
   // cached memory chunk size to 3 frames or more, as encoder, queue, framecallback both may use cache memory
   __fpv_fc.encodermemcache = mem_cache_init( 3, __cam_params.frame_sz+128);

   __fpv_fc.fpvqueue = init_queue(3);
   
   
   fpv_encoder_interface* encoder_api  =NULL;
   CDBG_INFO(debuginfo, "create thread for rtsp \n");
   //Create the thread for rtsp
   if ( __fpv_fc.stream_type == RTP_JPEG) {
       encoder_api = fpv_jpeg_encoder_get_interface();
       __fpv_fc.rtspqueue = init_queue(1);
       __fpv_fc.rtspmemcache =  mem_cache_init(3, __cam_params.width*__cam_params.height);
       // spawn the rtsp thread
       start_rtsp_server(RTP_JPEG, __fpv_fc.rtspqueue, __fpv_fc.fpvqueue, __fpv_fc.rtspmemcache, __fpv_fc.ifacename, __fpv_fc.default_rtsp_thread_pri);
   } else
   {
        ;
   }
   
   CDBG_INFO(debuginfo, "create thread for encoder \n");
   //create thread for encoder
   startPriorityThread(&__fpv_fc.m_StreamThreadId, 
     __fpv_fc.default_encoder_thread_pri, 
     &(fpv_encoder_thread), 
     (void*)encoder_api, 
     "fpv_enc");
       
   // install customized sigterm function for exit
   __installsig();
   return FPV_NO_ERROR;
}

/**
 * @brief stop the fpv instance.
 *
**/
bool fpv_stop()
{
    put_msg_into_queue( FLOW_MSG_QUIT, NULL, __fpv_fc.fpvqueue);
    return true;
}

/**
 * @brief main entry point for the application. 
 *        it has a initite loop to handle msgs
 *
 *
 * @return true for normal quit. false for abnormal conditions
 */
void* fpv_start(void*)
{
   int datarc, msgrc, msgid;

   CDBG_INFO(debuginfo, "In %s\n",__PRETTY_FUNCTION__);
   
   // set up encoder with right params. encoder waiting for work
   __setupEncoderParams();
   __startEncoder();

   // fc queue did not created. quit the function
   if ( __fpv_fc.fpvqueue == NULL ) {
       return NULL;
   }

   // the message loop for future rate control and receive msgs from other
   // modules and layers
   while (true) {
       // msg loop.
       (void)dequeue_msg_data(__fpv_fc.fpvqueue, &msgrc, &msgid, &datarc);
       if (msgrc != FPV_NO_ERROR) {
           continue;
       }
       //handle message
       CDBG_INFO(debuginfo, "fpv fc msg handler %d \n", msgid);

       if (FLOW_MSG_QUIT == msgid) {
           break;
       }
       else if (FLOW_MSG_START_PREVIEW == msgid) {
           if ( !__fpv_fc.m_Started ) {
               CDBG_INFO(debuginfo, "fpv fc msg handler: start preview \n");
               __fpv_fc.icd_->startPreview();
               __fpv_fc.m_Started = true;
           }
       }
       else if (FLOW_MSG_STOP_PREVIEW == msgid) {
           if ( __fpv_fc.m_Started ) {
               CDBG_INFO(debuginfo, "fpv fc msg handler: stop preview \n");
               __fpv_fc.icd_->stopPreview();
               __fpv_fc.m_Started = false;
           }
       }
       else {
           CDBG_INFO(debuginfo, "fpv fc msg handler, unhandled msg %d \n", msgid);
       }
   }

   // the upper while loop should be inifite loop. unless it got a command to quit.
   mem_cache_destroy(__fpv_fc.rtspmemcache);
   mem_cache_destroy(__fpv_fc.encodermemcache);
   remove_queue(__fpv_fc.rtspqueue);
   remove_queue(__fpv_fc.encoderqueue);
   remove_queue(__fpv_fc.fpvqueue);
   mem_cache_release();
   return NULL;
}



