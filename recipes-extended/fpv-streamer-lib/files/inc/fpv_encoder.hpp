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
#ifndef  FPV_ENCODER_H
#define  FPV_ENCODER_H

#include <sys/time.h>
#include <time.h>

// first param is the encoder's bitstream, the second is the size for bitstream
typedef int (*encoder_callback)(unsigned char * buf, int sz , int fmcount, struct timeval tm, unsigned char* params);

typedef int (*encoder_msg_handler_func)(int,void*);
typedef int (*encoder_addnewframe)(void*, int, struct timeval tm);
typedef int (*encoder_installcallback)(encoder_callback);
typedef bool (*encoder_isrunning)();
typedef struct FPV_ENCODER_S {
    encoder_msg_handler_func  msg_handler;   // set encoder params
    encoder_addnewframe      add_frame;    // add a new frame to encoder for encoding
    encoder_installcallback  install_callback; // sink install this callback to get encoding result.
    encoder_isrunning        uninstall_callback; // de reg the sink callback func
    encoder_isrunning        isrunning;
} fpv_encoder_interface;

typedef enum ENCODER_CMD_S{
    ENCODER_CMD_START = 1,
    ENCODER_CMD_STOP  = 2,
    ENCODER_CMD_QUIT = 3,
    ENCODER_CMD_SETPARAS = 4
}encoder_commands;

#define MAX_JPEPG_BUF_SZ    100*1024
typedef struct FPV_JPG_PARAMS {
    int target_frm_sz;
    int width;
    int height;
    unsigned char start_quality;
    unsigned char lowest_quality;
} fpv_jpeg_params;

fpv_encoder_interface* fpv_jpeg_encoder_get_interface();

#endif
