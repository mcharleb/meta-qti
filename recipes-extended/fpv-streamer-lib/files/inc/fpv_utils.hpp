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
#ifndef FPV_UTILS_HPP
#define FPV_UTILS_HPP

#include <sys/time.h>
#include <syslog.h>
#include <stdarg.h>
#include <pthread.h>
#include <stdint.h>

#define FPV_NO_ERROR     0

#define FPV_ERROR_START  1
#define FPV_WRONG_INPUT  FPV_ERROR_START+1
#define FPV_NULL_POINTER FPV_ERROR_START+2
#define FPV_NO_MEMORY FPV_ERROR_START+3
#define FPV_WRONG_STATE  FPV_ERROR_START+4
#define FPV_DATA_NOT_EXIST FPV_ERROR_START+5
#define PAD_TO_SIZE(size, padding) ((size + padding - 1) & ~(padding - 1))

typedef void* (*thread_func)(void*);

/* dubug purpose */
   void dumpfile( int c, void* buf, int sz);
   void reportTimeend(const char *tag, timeval starttm);
   void reportTimestart(timeval &starttm);
   void fpv_startpreview();
   void fpv_stoppreview();

// pri : 0: low pri, 1: normal pri, 2: high pri
int startPriorityThread(pthread_t* threadid, int pri, thread_func start_rtn, 
                        void* data, const char* threadname);

//msg definition for the flow control thread
#define FLOW_MSG_QUIT           1
#define FLOW_MSG_START_PREVIEW  2
#define FLOW_MSG_STOP_PREVIEW  3

static inline uint32_t YUV420_BUF_SIZE(uint32_t w, uint32_t h)
{
    return w * h * 3/2;
}

#endif
