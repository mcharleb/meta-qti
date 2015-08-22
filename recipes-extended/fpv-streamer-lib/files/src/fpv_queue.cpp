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
#include <stdlib.h>
#include <string.h>
#include "fpv_queue.hpp"
#include "fpv_utils.hpp"
#include "fpv_dbg.hpp"

/*
 *   shared data queue, this data queue is designed particularly used for preview frame
 *   or jpeg pic data movement beween threads. The memory in this data queue should get
 *   from a memory cache
**/
typedef struct QUEUE_S {

 void** q_ptr; // buf for queues, this is for data
 int depth; // curr queue depth
 int max_depth; // max queue depth
 int h; // head
 int t; // tail

 /* shared msg queue, this is for msg exchange between threads. memory are alloced/free dynamicly as we don't have limitation on memory queue depth */
 queue_msg *msg_q;

 pthread_mutex_t m_Mutex;
 pthread_cond_t  m_Cv;
}shared_queue;

typedef struct MEM_CACHE_S {
    unsigned char * buf;
    int sz;
    int ele_len;
    bool *state;
} mem_cache;

pthread_mutex_t cache_Mutex;


/**
 * @brief initialize the shared queue. 
 * @params: depth the queue lenth
 *
 * @return Queue instance pointer
 */
void* init_queue(int depth)
{
    shared_queue * myqueue = (shared_queue *) malloc( sizeof(shared_queue));
    myqueue->t = 0;
    myqueue->h = 0;

    myqueue->depth = 0;
    myqueue->max_depth = depth;
    myqueue->q_ptr = (void **) malloc( sizeof(void*) * depth);

    myqueue->msg_q = NULL;

    pthread_cond_init(&myqueue->m_Cv, NULL);
    pthread_mutex_init(&myqueue->m_Mutex,0);
    return (void *) myqueue;
}

/**
 * @brief lock the queue
 * @params: pinst queue instance
 *
 * @return void
 */
void fpv_lock_queue(void *pinst) {
    shared_queue * inst = ( shared_queue*) pinst;
    pthread_mutex_lock(&inst->m_Mutex);
}
/**
 * @brief unlock the queue
 * @params: pinst queue instance
 *
 * @return void
 */
void fpv_unlock_queue(void * pinst) {
    shared_queue * inst = ( shared_queue*) pinst;
    pthread_mutex_unlock(&inst->m_Mutex);
}

/**
 * @brief wait for cond-var for queu. 
 * @params: pinst queue inst
 *
 * @return void
 */
void fpv_cvwait_queue(void * pinst) {
    shared_queue * inst = ( shared_queue*) pinst;
    pthread_cond_wait(&inst->m_Cv, &inst->m_Mutex);
}
/**
 * @brief send out signal for cond-var
 * @params: pinst queue inst
 *
 * @return void
 */
void fpv_cvsignal_queue(void * pinst){
    shared_queue * inst = ( shared_queue*) pinst;
    pthread_cond_signal(&inst->m_Cv);
}
/**
 * @brief put a pinter into the queue. 
 * @params: data the pointer, pinst queue inst
 *
 * @return 0 OK, others: error
 */
int __put_in_queue(unsigned char * data, void * pinst)
{
    shared_queue * inst = ( shared_queue*) pinst;

    // wrong input
    if ( inst == NULL || data == NULL)
        return FPV_WRONG_INPUT;

    // queue full.
    if ( inst->depth == inst->max_depth) { 
            return FPV_NO_MEMORY;
    } else
    {
        inst->depth++;
        inst->q_ptr[ inst->h] = data;
        inst->h = (inst->h+1)%inst->max_depth;
    }
    return FPV_NO_ERROR;
}


/**
 * @brief read a msg from queue. 
 * @params: msgid: msg id, errval: error resutl, pinst  queue inst
 *
 * @return msg buf or NULL for non msg
 */
void* __get_msg_from_queue(  int* msgid, int* errval, void* pinst) {
    shared_queue * inst = ( shared_queue*) pinst;
    if ( inst == NULL) {
        *errval = FPV_WRONG_INPUT;
        return NULL;
    }
    if ( inst->msg_q == NULL) {
        *errval =  FPV_DATA_NOT_EXIST;
        return NULL;
    }
    queue_msg *pmsg = inst->msg_q;
    inst->msg_q = pmsg->next;
    *msgid = pmsg->id;
    void * buf = pmsg->buf;
    free(pmsg);
    *errval = FPV_NO_ERROR;
    return buf;
}

/**
 * @brief put a msg into the queue. 
 * @params: msgid: id for msg, msg: msg buf, pinst queue inst
 *
 * @return 0 OK, others error
 */
int put_msg_into_queue(int msg_id, void * msg, void* pinst) {
    shared_queue * inst = ( shared_queue*) pinst;    

    if ( inst == NULL) return FPV_WRONG_INPUT;

    queue_msg *pcurr;// = inst->msg_q;
    queue_msg *pprev = NULL;
    queue_msg *pmsg = (queue_msg*)malloc(sizeof(queue_msg));
    pmsg->id = msg_id;
    pmsg->buf = msg;
    pmsg->next = NULL;
    fpv_lock_queue(pinst);
    pcurr = inst->msg_q;
    if ( inst->msg_q == NULL)
        inst->msg_q = pmsg;
    else {
        while( pcurr != NULL) {
            pprev = pcurr;
            pcurr = pcurr->next;
        }
        pprev->next = pmsg;
    }
    CDBG_INFO(debuginfo, " enqueue a msg, signal >>>>>>>>>>> %x  %x  %x \n", inst, inst->m_Mutex,inst->m_Cv);
    pthread_cond_signal(&inst->m_Cv);
    fpv_unlock_queue(pinst);
    return FPV_NO_ERROR;
}

/**
 * @brief get a data from queue. 
 * @params: pinst queue inst
 *
 * @return the shared queue data put by other threads or components
 */
unsigned char* get_from_queue(void * pinst)
{
    shared_queue * inst = ( shared_queue*) pinst;

    // empty queue
    if ( pinst == NULL || inst->depth == 0 )  {
        return NULL;
    }

    int i = inst->t;
    inst->t = (inst->t+1) % inst->max_depth;
    inst->depth--;

    return (unsigned char *)inst->q_ptr[i] ;
}

/**
 * @brief remove queue info. 
 * @params: pinst queue inst
 *
 * @return void
 */
void  remove_queue(void * pinst)
{
    void* buf;
    int msgid, errval;
    shared_queue * inst = ( shared_queue*) pinst;
    fpv_lock_queue( pinst);
    free(inst->q_ptr);

    buf = __get_msg_from_queue( &msgid, &errval, pinst);
    while ( NULL != buf) {
        free(buf);
        buf = __get_msg_from_queue( &msgid, &errval, pinst);
    }
    fpv_unlock_queue( pinst);

    pthread_mutex_destroy(&inst->m_Mutex);
    pthread_cond_destroy(&inst->m_Cv);

    free(pinst);
}

/**
 * @brief put a data pointer into the queue
 * @params: queue: queue inst, data: the data buf
 *
 * @return 0 OK, others error.
 */
int enqueue_data(void * queue, void * data) {
    int rc = FPV_NO_ERROR;
    if ( queue == NULL || data == NULL) {
        CDBG_ERROR(debugerror, "enqueue_data,queue empty or enqueu null data \n");
        return FPV_WRONG_INPUT;
    }

    shared_queue *myq = (shared_queue *)queue;
    pthread_mutex_lock(&myq->m_Mutex);
    CDBG_INFO(debuginfo, " enqueue a data, get lock >>>>>>>>>>>  %x  %x  %x \n", myq, myq->m_Mutex,myq->m_Cv);
    rc = __put_in_queue((unsigned char *) data, queue);

    if ( rc == FPV_NO_ERROR) {
        CDBG_INFO(debuginfo, " enqueue a data, do signal >>>>>>>>>>>  %x  %x  %x \n", myq, myq->m_Mutex,myq->m_Cv);
        pthread_cond_signal(&myq->m_Cv);
    }
    pthread_mutex_unlock(&myq->m_Mutex);
    
    return rc;
}
/**
 * @brief put a data pointer into the queue and do not wait if can not get lock
 * @params: queue: queue inst, data: the data buf
 *
 * @return 0 OK, others error.
 */
int enqueue_data_nowait(void * queue, void * data) {
    int rc = FPV_NO_ERROR;
    if ( queue == NULL || data == NULL) {
        CDBG_ERROR(debugerror, "enqueue no wait: queue empty or enqueu null data \n");
        return FPV_WRONG_INPUT;
    }

    shared_queue *myq = (shared_queue *)queue;
    if ( 0 != pthread_mutex_trylock(&myq->m_Mutex)) {
        CDBG_ERROR(debugerror, "enqueue lock failed  \n");
        return FPV_WRONG_STATE;
    }
  
    rc = __put_in_queue((unsigned char *) data, queue);

    if ( rc == FPV_NO_ERROR)
        pthread_cond_signal(&myq->m_Cv);
    pthread_mutex_unlock(&myq->m_Mutex);
    
    return rc;
}

/**
 * @brief get out data or msg from queue
 * @params: queue: queue inst, msgrc : msg result, msgid: id for msg,datarc: the data result
 *
 * @return buf for msg or data
 */
void* dequeue_msg_data( void* queue, int* msgrc, int* msgid, int* datarc) {
    int rc = FPV_NO_ERROR;
    
    if ( queue == NULL || datarc == NULL || msgid == NULL || msgrc == NULL) {
        CDBG_ERROR(debugerror, "dequeue msg data: queue empty or enqueu null data \n");
        return NULL;
    }
    *datarc = FPV_DATA_NOT_EXIST;
    *msgrc = FPV_DATA_NOT_EXIST;

    shared_queue *qinst = (shared_queue *)queue;
    CDBG_INFO(debuginfo, " dequque msg data fun try get lock<<<-----<<<<<<<<<< %x   %x    %x\n",qinst, qinst->m_Mutex, qinst->m_Cv );
    pthread_mutex_lock(&qinst->m_Mutex);

    void * buf = __get_msg_from_queue( msgid, msgrc, qinst);           
    if ( *msgrc != FPV_NO_ERROR) { // no msg exist , do data fetch.
        buf = (void*) get_from_queue(qinst);
        if ( buf == NULL) { // no msg and data, wait for cv signal
            CDBG_INFO(debuginfo, " dequque msg data func get lock, wait on cv  <<<<<<<<<<<<< %x   %x    %x\n",qinst, qinst->m_Mutex, qinst->m_Cv );
            pthread_cond_wait(&qinst->m_Cv, &qinst->m_Mutex);
            // check first msg exist?
            buf = __get_msg_from_queue( msgid, msgrc, qinst);                     
            if ( *msgrc != FPV_NO_ERROR) { // no msg, do data fetch
                buf = (void *)get_from_queue(qinst);
                *datarc = FPV_NO_ERROR;
            }
        } else 
            *datarc = FPV_NO_ERROR; 
    }
    pthread_mutex_unlock(&qinst->m_Mutex);
    return buf;
}
/**
 * @brief initial the memory cache
 * 
 *
 * @return void
 */
void mem_cache_create()
{
    pthread_mutex_init(&cache_Mutex,0);
    return;
}
/**
 * @brief del the memory cache
 * 
 *
 * @return void
 */
void mem_cache_release()
{
    pthread_mutex_destroy(&cache_Mutex);
}

/**
 * @brief create a memory instance
 * @params: sz item no. in the cache. ele_len, each item sz
 *
 * @return mem cache instance pointer
 */
void* mem_cache_init( int sz, int ele_len) {
    mem_cache  *inst = (mem_cache*) malloc( sizeof( mem_cache));
    inst->sz = sz;
    inst->ele_len = ele_len;

    inst->buf = (unsigned char *) malloc( ele_len * sz);
    inst->state = (bool *) malloc( sz * sizeof(bool));
    for ( int i = 0; i< sz;i++)
        inst->state[i] = false;

     return (void *) inst;
}
/**
 * @brief get a new cache item
 * @params: pinst the cache instance
 *
 * @return a cache buf 
 */
unsigned char * get_cacheed_mem(void * pinst) {
    mem_cache *inst = ( mem_cache *) pinst;
    if ( pinst == NULL) return NULL;
 
    pthread_mutex_lock(&cache_Mutex);

    for ( int i = 0; i < inst->sz;i++) {
        if (inst->state[i] == false) {
            inst->state[i] = true;
            pthread_mutex_unlock(&cache_Mutex); // found one, rel mutex
            return &inst->buf[inst->ele_len * i];
        }
    }

    pthread_mutex_unlock(&cache_Mutex);

    return NULL;
}
/**
 * @brief get a new cache item immediately, and no wait
 * @params: pinst the cache instance
 *
 * @return a cache buf 
 */
unsigned char * get_cached_mem_nowait(void * pinst) {
    mem_cache *inst = ( mem_cache *) pinst;
    if ( pinst == NULL) return NULL;
 
    if ( 0 != pthread_mutex_trylock(&cache_Mutex)) {
        CDBG_ERROR(debugerror, "get cache mmory lock failed \n");
        return NULL;
    }
    for ( int i = 0; i < inst->sz;i++) {
        if (inst->state[i] == false) {
            inst->state[i] = true;
            // success, no unlock. caller will do the unlock if necessary
            //pthread_mutex_unlock(&cache_Mutex); // found one, rel mutex
            return &inst->buf[inst->ele_len * i];
        }
    }
// fail , unlock
    pthread_mutex_unlock(&cache_Mutex);

    return NULL;
}

void release_cached_mem_lock() {
    pthread_mutex_unlock(&cache_Mutex);
}
/**
 * @brief give a memory back to the cache instance
 * @params: pinst the cache instance, data, the cache memory
 *
 * @return void
 */
void putback_cached_mem(void * pinst, unsigned char * data) {
    mem_cache *inst = ( mem_cache *) pinst;
    if ( pinst == NULL) {
        printf(" put back cached memory: wrong instance \n");
        return;
    }

    pthread_mutex_lock(&cache_Mutex);

    for ( int i = 0; i < inst->sz;i++) {
        if (&inst->buf[i * inst->ele_len] == data) {
            inst->state[i] = false;

            pthread_mutex_unlock(&cache_Mutex);

            return;
        }
    }
    pthread_mutex_unlock(&cache_Mutex);

    return;
}

/**
 * @brief give a memory back to the cache instance, caller must already get the lock
 * @params: pinst the cache instance, data, the cache memory
 *
 * @return void
 */
void putback_cached_mem_nowait(void * pinst, unsigned char * data) {
    mem_cache *inst = ( mem_cache *) pinst;
    if ( pinst == NULL) {
        printf(" put back cached memory: wrong instance \n");
        return;
    }

    for ( int i = 0; i < inst->sz;i++) {
        if (&inst->buf[i * inst->ele_len] == data) {
            inst->state[i] = false;

            //pthread_mutex_unlock(&cache_Mutex);

            return;
        }
    }
    //pthread_mutex_unlock(&cache_Mutex);

    return;
}
/**
 * @brief release a cache instance.
 * @params: pinst the cache instance
 *
 * @return void
 */
void mem_cache_destroy( void * pinst) {
    mem_cache *inst = ( mem_cache *) pinst;
    
    pthread_mutex_lock(&cache_Mutex);
    free( inst->buf);
    free( inst->state);
    free(inst);
    pthread_mutex_unlock(&cache_Mutex);
    return;
}
