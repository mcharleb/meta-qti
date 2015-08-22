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
#ifndef FPV_RTSP_H
#define FPV_RTSP_H

typedef enum FPV_RTSP_TYPE_S{
   RTP_JPEG
} fpv_rtsp_type;

int start_rtsp_server(fpv_rtsp_type  type, void* rtspqueue, void* fcqueue,void * cachemem, char* ifacename, int pri);

int read_jpeg_frame(int* sz, struct timeval *camtm, unsigned char * dst, unsigned char * quality);

void * fpv_rtsp_thread(void* data);

int stop_rtsp_server();
#endif
