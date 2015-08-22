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
#include <stdlib.h>
#include <string.h>
#include <stdio.h>      
#include <sys/types.h>
#include <ifaddrs.h>
#include <netinet/in.h> 
#include <string.h> 
#include <arpa/inet.h>

//live555 rtsp library
#include "liveMedia.hh"
#include "GroupsockHelper.hh"
#include "BasicUsageEnvironment.hh"
#include "fpv_live555_jpeg_source.hpp"

#include "fpv_encoder.hpp"
#include "fpv_rtsp.hpp"
#include "fpv_queue.hpp"
#include "fpv_utils.hpp"
#include "fpv_dbg.hpp"

typedef struct FPV_RTSP_JPEG_PARAM_S {
  unsigned char w;
  unsigned char h;
  unsigned char q;
}fpv_rtsp_jpeg_params;

typedef struct FPV_RTSP_INSTANCE_S {
    unsigned char *membuf;
    bool           start;
    fpv_encoder_interface* encoder_api;
    void*    shareq;
    void *   rtspmemcache;
    void*    fcqueue; // used to send msg to fpv fc
    fpv_rtsp_jpeg_params rtpjpgparam;
    pthread_t tid;
}fpv_rtsp_instance;

typedef struct FPV_RTSP_PACKET {
    int framesz; 
    int framecount;
    struct timeval  tm;
    unsigned char   quality;
    unsigned char resv[7];
    unsigned char data;
}fpv_rtsp_packet;
static int dbgcount = 0;

static struct timeval starttm;

static int count = 0;

static char rtspstopped = 0;

static fpv_rtsp_instance __fpv_rtsp_data = {
                         .membuf = NULL,
                         .start = false,
                         };

/**
 * @brief debug function to write data into sd card
 * @param c: file name prefix. buf: the data, sz: the size for buf
 *
 * @return true for normal quit. false for abnormal conditions
 */
void debwritefile(int c, unsigned char * buf, int sz)
{
    char filename[128];

    CDBG_ERROR(debugerror,"In %s \n", __PRETTY_FUNCTION__);  

    sprintf( filename, "/sdcard/test/jpg%d.jpg", c);
    FILE *fp = fopen( filename, "wb+");
    fwrite(buf, 1, sz, fp);
    fclose(fp);
}
/**
 * @brief loop though all the network device in system and select one for rtsp streaming.
 * @param  addr_res : selected device. ifacename: desired device name.
 *
 *
 * @return 0: not found, 1 : found
 */
int  getPreferInterfaceAddr(struct sockaddr_in* addr_res, char* ifacename)
{
    CDBG_INFO(debuginfo, "get interface Addr now \n");

    struct ifaddrs * ifAddrStruct=NULL;

    int found = 0;
    getifaddrs(&ifAddrStruct);
    CDBG_INFO(debuginfo, "input str %s, len %d \n", ifacename, strlen(ifacename));
    for ( int i = 0; i< strlen(ifacename);i++) printf("%x ", ifacename[i]);
    
    while (ifAddrStruct!=NULL) {
        if (ifAddrStruct->ifa_addr->sa_family==AF_INET) { // check it is IP4
            // is a valid IP4 Address
            CDBG_INFO(debuginfo, "loop one face %s sz  %d \n", ifAddrStruct->ifa_name, strlen(ifAddrStruct->ifa_name));
            if ( strcmp(ifacename, ifAddrStruct->ifa_name) ==  0) {
                
                memcpy(addr_res, ifAddrStruct->ifa_addr, sizeof( struct sockaddr_in));
                found = 1;
                break;
            }
        }
        ifAddrStruct = ifAddrStruct->ifa_next;
    }
    if ( !found)
        CDBG_ERROR(debugerror, "error, no matching interface found for %s \n", ifacename);
    return found;
}


UsageEnvironment * env;
char * progName;

struct sessionState_t {
    FramedSource * source;
    RTPSink * sink;
    RTSPServer* rtspServer;
} sessionState;


// this is the interface how live555 receive data and msg from encoder. basically 
int __read_jpeg_frame(int* sz, struct timeval * tm,  unsigned char * rtpsinkbuf, unsigned char * quality) {
    CDBG_INFO(debuginfo, "In %s \n", __PRETTY_FUNCTION__);  

    fpv_lock_queue( __fpv_rtsp_data.shareq);

    unsigned char *buf = get_from_queue(__fpv_rtsp_data.shareq);
    if ( buf == NULL ) {
        fpv_cvwait_queue( __fpv_rtsp_data.shareq);
        buf = get_from_queue(__fpv_rtsp_data.shareq);
    }
/*    int * pint = (int*)buf;
    *sz = *pint;
    int fmcount = *(pint+1);
    *tm = *((struct timeval *) (buf+8));
    *quality = *(buf+8+sizeof( struct timeval));
*/
    fpv_rtsp_packet *pack = (fpv_rtsp_packet *) buf;
    *sz = pack->framesz;
    int fmcount = pack->framecount;
    *tm = pack->tm;
    *quality = pack->quality;
    
    fpv_unlock_queue( __fpv_rtsp_data.shareq);
    if ( *sz > 0 ) {
        //memcpy( rtpsinkbuf, buf+24, *sz);
        memcpy( rtpsinkbuf, &pack->data, *sz); 
        putback_cached_mem( __fpv_rtsp_data.rtspmemcache, buf);
        reportTimestart(starttm);
        CDBG_INFO(debuginfo, "read jpg sz %d, addr %x, count %d , quality %d , at sec %ld, usec %ld \n", *sz, buf, fmcount, *quality,starttm.tv_sec, starttm.tv_usec);
    } else CDBG_ERROR(debugerror, "error , read negative length values from queue at addr %x \n", buf);

    return FPV_NO_ERROR;    
}
/**
 * @brief stub function for live555 to read in jpeg data from shared queue. 
 *
 * @return 0: no error, others: error
 */
int fpvJpegReader::readJpegFrame( int *sz,struct timeval * tm,  unsigned char * rtpsinkbuf, unsigned char * quality) {
        return __read_jpeg_frame(sz, tm, rtpsinkbuf, quality);
}


/**
 * @brief this class is used to add a jpeg stream into rtsp server. 
 *
 */
class fpvOndemandMediaSubsession:public OnDemandServerMediaSubsession {
    public:
        static fpvOndemandMediaSubsession* createNew ( UsageEnvironment &env, fpvJpegReader* jpgreader) {
            CDBG_INFO(debuginfo, " create new jpeg reader %x \n", jpgreader);
            return new fpvOndemandMediaSubsession(env, jpgreader);
        }
    protected:
        fpvOndemandMediaSubsession(UsageEnvironment &env, fpvJpegReader *jpgreader):OnDemandServerMediaSubsession(env, true) {
        sourcereader = jpgreader;
    }
    virtual RTPSink *createNewRTPSink ( Groupsock *rtpsock, unsigned char type, FramedSource *source) {
        return JPEGVideoRTPSink::createNew(envir(), rtpsock);
     }

    virtual FramedSource *createNewStreamSource(unsigned sid, unsigned &bitrate) {
            CDBG_INFO(debuginfo, " create source jpeg reader %x \n", sourcereader);
        fpv_startpreview();
        return MJPEGVideoSource::createNew(envir(), sourcereader);
    }

    virtual void startStream( unsigned clientSessionId, void* streamToken, TaskFunc* rtcptthandler, void* rtcphandlerclidata,
                   unsigned short& rtpseqnum, unsigned& rtptimestamp, ServerRequestAlternativeByteHandler *handler , void * clidata) {
        OnDemandServerMediaSubsession::startStream( clientSessionId, streamToken, rtcptthandler, rtcphandlerclidata, 
                                                  rtpseqnum,  rtptimestamp,  *handler ,  clidata);
        fpv_startpreview();
    }
    private:
        fpvJpegReader *sourcereader;
};
/**
 * @brief main entry point for the rtsp server. 
 *        it has a initite loop to handle msgs
 *
 *
 * @return NULL
 */
void * fpv_rtsp_thread(void* data) {
    CDBG_INFO(debuginfo, "enter rtsp thread main func \n");
    TaskScheduler* scheduler = BasicTaskScheduler::createNew();
    env = BasicUsageEnvironment::createNew(*scheduler);

    sessionState.rtspServer = RTSPServer::createNew(*env, 554);

    if ( sessionState.rtspServer == NULL){
        CDBG_ERROR(debugerror, "failed to create rtsp server \n");
        put_msg_into_queue( FLOW_MSG_QUIT, NULL, __fpv_rtsp_data.fcqueue);  
        return NULL; 
    }
    // should be initialized from message
    __fpv_rtsp_data.rtpjpgparam.w = 80;//160;
    __fpv_rtsp_data.rtpjpgparam.h = 60;//120;
    __fpv_rtsp_data.rtpjpgparam.q = 40;

    const char * inputName = "fpvview";
    struct sockaddr_in  preferaddr;
    if ( 1 == getPreferInterfaceAddr(&preferaddr, (char *)data)) {
        ReceivingInterfaceAddr = preferaddr.sin_addr.s_addr;
    }

    OutPacketBuffer::maxSize=1024*1024;

//    setPreferInterfaceName((char*) data, 5);
//    printf("set the wlan0 name now \n");

    ServerMediaSession* sms = 
          ServerMediaSession::createNew(*env, inputName, 0,"session stream for fpv", true);

    fpvJpegReader *fpvreader = new fpvJpegReader();

    sms->addSubsession(fpvOndemandMediaSubsession::createNew(*env, fpvreader));
    sessionState.rtspServer->addServerMediaSession(sms);

    char *url = sessionState.rtspServer->rtspURL(sms);
    CDBG_INFO(debuginfo ," play this stream using url: %s \n", url);
    /* TODO: return url to app instead of printing here */
    printf("FPV stream: %s\n", url);
    delete[] url;
    CDBG_INFO(debuginfo, "rtsp server ready for streaming \n");
  
    env->taskScheduler().doEventLoop(&rtspstopped);
    printf("rtsp finished , quit !!!!!!!");
}


/**
 * @brief callback function for rtsp thread to save data into the shared queue. 
 * @params: buf jpg buf, sz: jpg buf size. framecount: global frame count, camtm: time from cam. quality: jpg quality
 *
 * @return 0: success, others: error
 */
int jpeg_callback(unsigned char * buf, int sz, int framecount, struct timeval camtm, unsigned char* quality) {
//    CDBG("In %s \n", __PRETTY_FUNCTION__);  
    int rc = FPV_NO_ERROR;

    unsigned char * data = get_cacheed_mem(__fpv_rtsp_data.rtspmemcache);
    if ( data == NULL) {
        CDBG_ERROR(debugerror, " can not get jpg rtp buffer from cache, drop a frame \n");
        return FPV_NO_MEMORY;
    } else {
        /* memcpy( data+24, buf, sz);
        int * pint = (int*) data; // offset 0  for frame size              
        *pint++ = sz;
        *pint = framecount;       // offset 1  for frame count
        memcpy(data+8, &camtm, sizeof(struct timeval)); // offset 8 for cam time
        *(data+8 + sizeof( struct timeval)) = *quality; // offset 16 for quality
    */
        fpv_rtsp_packet *pack = ( fpv_rtsp_packet *) data;
        pack->framesz = sz;
        pack->framecount = framecount;
        pack->tm = camtm;
        pack->quality = *quality;
      
        memcpy( &pack->data, buf, sz);

    }
    
    rc = enqueue_data(__fpv_rtsp_data.shareq, data);
    
    if (rc != FPV_NO_ERROR) {
        CDBG_ERROR(debugerror, "rtsp jpg queue full , drop a jpg \n");
        putback_cached_mem( __fpv_rtsp_data.rtspmemcache, data);
    }

    reportTimestart(starttm);
    return FPV_NO_ERROR;
    
}


/**
 * @brief main entry point for the rtsp. 
 *        it will create the rtsp thread.
 *
 *
 * @return 0 or success. others: error
 */
int start_rtsp_server(fpv_rtsp_type  type, void * shareq, void* fcqueue,  void * cachebuf , char * ifacename, int pri) {

    CDBG_INFO(debuginfo, "In %s \n", __PRETTY_FUNCTION__);  
    if ( __fpv_rtsp_data.start == true) {
        CDBG_ERROR(debugerror, "the fpv rtsp instance already started quit ");
        return FPV_WRONG_STATE;
    	}
    __fpv_rtsp_data.shareq = shareq ;
    __fpv_rtsp_data.rtspmemcache = cachebuf;
    __fpv_rtsp_data.fcqueue = fcqueue;
    
    if ( type == RTP_JPEG) {
        __fpv_rtsp_data.encoder_api = fpv_jpeg_encoder_get_interface();
        __fpv_rtsp_data.encoder_api->install_callback(jpeg_callback);

       pthread_t tid;

       startPriorityThread(&__fpv_rtsp_data.tid, pri, &(fpv_rtsp_thread), (void*) ifacename, "fpv_rtsp");
    }


    return FPV_NO_ERROR;
}
/**
 * @brief stop the rtsp server and quit the thread. 
 *
 *
 * @return true for normal quit. false for abnormal conditions
 */
int stop_rtsp_server()
{
    rtspstopped  = 1;
    __fpv_rtsp_data.encoder_api->install_callback(NULL);
}

