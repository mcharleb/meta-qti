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
#include <stdio.h>
#include "fpv_utils.hpp"
#include "fpv_dbg.hpp"
/**
 * @brief got the curr system time
 *
 * @return 
 */
void reportTimestart(timeval &starttm) {
    gettimeofday(&starttm, NULL);
}

/**
 * @brief got the time duration
 *
 * @return void
 */
void reportTimeend(const char *tag, timeval starttm) {
    timeval endtm;
    gettimeofday(&endtm,NULL);
    float elptm = (endtm.tv_sec - starttm.tv_sec)*1000.0f;
    elptm+= ( endtm.tv_usec -  starttm.tv_usec)/1000.0f;
    CDBG_INFO("%s cost time %3.2f \n", tag, elptm);
}

/**
 * @brief dump a data to sd card
 *
 * @return void
 */
void dumpfile( int c, void* buf, int sz)
{
    char filename[128];
    sprintf( filename, "/sdcard/test/%ddump.jpg",c);
    FILE *fp =fopen(filename, "wb+");
    fwrite( buf, 1, sz, fp);
    fclose(fp);
}

typedef void* (*thread_func)(void*);

/**
 * @brief spawn a thread
 *
 * @return void
 */
int startPriorityThread(pthread_t* threadid, int pri, thread_func start_rtn, 
                        void* data, const char* threadname) {
    pthread_attr_t thread_attr;
    int thread_policy;
    struct sched_param thread_param;
    int status, pri_low, pri_high;

    pthread_attr_init(&thread_attr);

//#define POSIX_THREAD_PRIORITY_SCHEDULING
#if defined(POSIX_THREAD_PRIORITY_SCHEDULING)

    pthread_attr_getschedpolicy(&thread_attr, &thread_policy);
    pthread_attr_getschedparam(&thread_attr, &thread_param);

    status = pthread_attr_setschedpolicy(&thread_attr, SCHED_FIFO);
    if(status != 0) {
        CDBG_ERROR(debugerror, "Unable to set SCHED_FF policy. \n");
    }
    else{
        pri_low = sched_get_priority_min(SCHED_FIFO);
        if(pri_low == -1)
            CDBG_ERROR(debugerror, "Get SCHED_ff min priority");
        pri_high = sched_get_priority_max(SCHED_FIFO);
        if(pri_high == -1)
            CDBG_ERROR(debugerror,"Get SCHED_ff max priority");

        thread_param.sched_priority = (pri_low+pri_high)/2;
        
        thread_param.sched_priority = pri;
        
        CDBG_INFO(debuginfo,"SCHED_RR priority range is %d to %d: using %d\n",
            pri_low, pri_high, thread_param.sched_priority);
        pthread_attr_setschedparam(&thread_attr, &thread_param);
        CDBG_INFO(debuginfo,"Creating thread at FF/%d\n", thread_param.sched_priority);
        pthread_attr_setinheritsched(&thread_attr, PTHREAD_EXPLICIT_SCHED);
    }
#else
    CDBG_INFO(debuginfo, "the thread scheduling do not support priority based schedule \n");

#endif
    int rc = pthread_create(threadid,&thread_attr, start_rtn, data);
    if ( rc ) {
        CDBG_ERROR(debugerror, "ERROR create new thread for %s \n", threadname);
    } else
      pthread_setname_np(*threadid, threadname); 

    return 0;
}
