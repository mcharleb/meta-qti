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
#ifndef FPV_QUEUE_H
#define FPV_QUEUE_H

#include <pthread.h>
typedef struct QUEUE_MSG
{
   int id;
   void * buf;
   struct QUEUE_MSG *next;
} queue_msg;


// crate a new queue and alloc memory
void* init_queue(int depth);

//msg receiver free the msg memory
//void* get_msg_from_queue(  int* msgid, int* errval, void* inst);

// msg sender alloc msg memory.
int put_msg_into_queue(int msgid, void* buf, void* inst);


// put a data into the queue's tail
//int put_in_queue(unsigned char * data, void* inst);

//read data from queue's head
unsigned char * get_from_queue(void* inst);

int enqueue_data(void * queue, void * data);

int enqueue_data_nowait(void * queue, void * data);

void* dequeue_msg_data( void* queue, int* msgrc, int* msgid, int* datarc); 

//destroy the queue and related memory.
void  remove_queue(void * inst);

void fpv_lock_queue(void *inst);

void fpv_unlock_queue(void *inst);

void fpv_cvwait_queue(void *inst);

void fpv_cvsignal_queue(void *inst);


void mem_cache_create();

void mem_cache_release();

unsigned char * get_cacheed_mem(void * inst);

unsigned char * get_cached_mem_nowait(void * pinst);

void  putback_cached_mem_nowait(void * inst, unsigned char * data);

void release_cached_mem_lock();

void* mem_cache_init( int sz, int ele_len);

void putback_cached_mem(void * inst, unsigned char * data);

void mem_cache_destroy( void * inst);

#endif
