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
#include <cstring>
#include <unistd.h>
#include "turbojpeg.h"

#include "fpv_encoder.hpp"
#include "fpv_dbg.hpp"
#include "fpv_utils.hpp"

#define TJPEG_ENCODER_PAD 4

typedef enum JPEG_ENCODER_STATUE_S{
    JE_STATE_NULL = 0,
    JE_STATE_INITIALIZED
} jpeg_encoder_status;

typedef struct  FPV_ENCODER_JPEG_CONTEXT_S{
    jpeg_encoder_status status;
    unsigned char* buf;       // hold the encoded bitstream
    encoder_callback callback;
    int              lastFramesz;
    bool             isrunning;
    fpv_jpeg_params  params;
}fpv_encoder_jpeg_instance;

static fpv_encoder_jpeg_instance __fpv_encoder_data={ 
                                      .status = JE_STATE_NULL,
                                      .buf = NULL,
                                      .callback = NULL,
                                      .lastFramesz = 0,
                                      .isrunning = false};

static fpv_encoder_interface     fpv_encoder_jpeg_int;


/**
 * @brief adjust quality. for rate adapt usage
 *
 * @return void
 */
int adjustQuality(int quality)
{
    if ( quality < __fpv_encoder_data.params.lowest_quality)
        return __fpv_encoder_data.params.lowest_quality;

    return quality;
}


/**
 * @brief call third party function to do the encoding
 *
 * @return void
 */
int __TJpegCompress(const unsigned char *pSrc, int srcWid, int srcHei, unsigned char **pDest,int jpeg_quality)
{
    long unsigned int _jpegSize=0;
    const int JPEG_QUALITY = jpeg_quality; //= adjustQuality(jpeg_quality);

    tjhandle _jpegCompressor = tjInitCompress();

    tjCompressFromYUV(_jpegCompressor, (unsigned char *)pSrc, srcWid, TJPEG_ENCODER_PAD, srcHei, TJSAMP_420, pDest,
            &_jpegSize, JPEG_QUALITY, TJFLAG_FASTDCT);
    
    tjDestroy(_jpegCompressor);
    
    return (int)_jpegSize;
}

/**
 * @brief start the encoder
 *
 * @return void
 */
int __fpv_jpeg_start_encoder(void* data)
{
    CDBG_INFO(debuginfo,"In %s \n", __PRETTY_FUNCTION__);    
 
    if ( __fpv_encoder_data.status == JE_STATE_NULL) {
        return FPV_WRONG_STATE;
    }

    __fpv_encoder_data.isrunning = true;
    return FPV_NO_ERROR;
}

/**
 * @brief stop the encoder
 *
 * @return void
 */
int __fpv_jpeg_stop_encoder(void* data)
{
    CDBG_INFO(debuginfo,"In %s \n", __PRETTY_FUNCTION__);    
    if ( __fpv_encoder_data.status == JE_STATE_NULL) {
        return 1;
    }

    __fpv_encoder_data.isrunning = false;

    return FPV_NO_ERROR;
}

/**
 * @brief set up jpeg encoder params
 *
 * @return void
 */
int __fpv_jpeg_set_params(void* data)
{
    CDBG_INFO(debuginfo, "In %s \n", __PRETTY_FUNCTION__);    

    if ( data == NULL)
        return FPV_WRONG_INPUT;

    fpv_jpeg_params *pparams = (fpv_jpeg_params *) data;
    if ( NULL == memcpy( &__fpv_encoder_data.params, data, sizeof( fpv_jpeg_params))) {
         return FPV_NO_MEMORY;
    }
    __fpv_encoder_data.lastFramesz = pparams->target_frm_sz;
    
    // jpeg encoder only need the params as initialize
    __fpv_encoder_data.status = JE_STATE_INITIALIZED;

    return FPV_NO_ERROR;
}

/**
 * @brief encoder get a new frame and encode it into bitstreams
 *
 * @return void
 */
int __fpv_jpeg_add_frame(void* data, int fmcount, struct timeval camtm)
{
    unsigned char* buf = (unsigned char *) data;
    CDBG_INFO(debuginfo, "In %s \n", __PRETTY_FUNCTION__);

    if ( data == NULL)
        return FPV_WRONG_INPUT;

    int sz =      __TJpegCompress(buf, 
                  __fpv_encoder_data.params.width, 
                  __fpv_encoder_data.params.height, 
                  &__fpv_encoder_data.buf,
                  __fpv_encoder_data.params.start_quality);
    CDBG_INFO(debuginfo, " jpeg encoder : framesz %d \n", sz);

    if ( __fpv_encoder_data.callback != NULL) {
       __fpv_encoder_data.callback(__fpv_encoder_data.buf, sz, fmcount, camtm, &__fpv_encoder_data.params.start_quality);
    }

    free(__fpv_encoder_data.buf);

    __fpv_encoder_data.lastFramesz = sz;

    return FPV_NO_ERROR;
}

/**
 * @brief rtsp module call this to install a callback function to get data
 *
 * @return void
 */
int __fpv_jpeg_install_callback(encoder_callback sink_callback)
{
    CDBG_INFO(debuginfo, "In %s \n", __PRETTY_FUNCTION__);

    __fpv_encoder_data.callback = sink_callback;
    return FPV_NO_ERROR;
}


bool __fpv_jpeg_unstall_callback() {
    if ( __fpv_encoder_data.callback != NULL ) {
        __fpv_encoder_data.callback  =NULL;
        return true;
    }
    
    return false;
}
/**
 * @brief tell if the encoder is running
 *
 * @return void
 */
bool __fpv_jpeg_encoder_isrunning()
{
    return __fpv_encoder_data.isrunning;
}


/**
 * @brief msg handler for jpg encoder
 *
 * @return 0 for success. others error
 */
int __fpv_jpeg_encoder_msg_handler(int msgid, void* buf)
{
    CDBG_INFO(debuginfo, "In %s, msg id %d  \n", __PRETTY_FUNCTION__, msgid);
    if ( msgid ==ENCODER_CMD_SETPARAS) {
        if ( buf != NULL) {
            __fpv_jpeg_set_params( buf);
            free(buf);
            return FPV_NO_ERROR;
        } else {
            CDBG_ERROR(debugerror, "error , jpeg set params no buf ! \n");
        }
    } else if ( msgid == ENCODER_CMD_START) {
        __fpv_jpeg_start_encoder(NULL);
        return FPV_NO_ERROR;
    } else if ( msgid == ENCODER_CMD_STOP ) {
        __fpv_jpeg_stop_encoder(NULL);
        return FPV_NO_ERROR;
    } else if ( msgid == ENCODER_CMD_QUIT ) {
        return FPV_ERROR_START; // when we quit, we force to return a error
    } else {
        CDBG_ERROR(debugerror, "jpeg encoder handler:unknow msg id %d \n", msgid);
    }
    return FPV_NO_ERROR;
}

/**
 * @brief return the jpeg encoder's api function pointer
 *
 * @return function pointers for encoder
 */
fpv_encoder_interface* fpv_jpeg_encoder_get_interface()
{
    CDBG_INFO(debuginfo, "In %s \n", __PRETTY_FUNCTION__);

    __fpv_encoder_data.callback = NULL;
    __fpv_encoder_data.buf = NULL;

    fpv_encoder_jpeg_int.msg_handler = __fpv_jpeg_encoder_msg_handler;
    fpv_encoder_jpeg_int.add_frame = __fpv_jpeg_add_frame;
    fpv_encoder_jpeg_int.install_callback = __fpv_jpeg_install_callback;
    fpv_encoder_jpeg_int.uninstall_callback = __fpv_jpeg_unstall_callback;
    fpv_encoder_jpeg_int.isrunning = __fpv_jpeg_encoder_isrunning;
    return &fpv_encoder_jpeg_int;
}


