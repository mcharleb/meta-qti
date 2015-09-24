/*
 * Copyright (c) 2015 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <sys/syscall.h>
#include <sys/param.h>
#include <CL/opencl.h>

#define BUFFER_SIZE (1024*1024*2)

#define INPUT_FLOAT_VALUE 1.0f

cl_device_id       gDevice      = NULL;
cl_context         gContext     = NULL;
cl_command_queue   gQueue       = NULL;
cl_mem             gInBuffer    = NULL;
cl_mem             gOutBuffer   = NULL;
cl_kernel          gKernel      = NULL;
cl_program         gProgram     = NULL;

const char *kernelSource = "__kernel void math_kernel( __global float *out, __global float *in )\n"
                            "{\n"
                            "   int i = get_global_id(0);\n"
                            "   out[i] = acos( in[i] );\n"
                            "}\n";

int initCL()
{
    cl_platform_id     platform = NULL;
    cl_int             error    = CL_SUCCESS;

    /* Get the platform */
    error = clGetPlatformIDs(1, &platform, NULL);
    if (error) {
        printf( "clGetPlatformIDs failed: %d\n", error );
        return error;
    }

    /* Get the requested device */
    error = clGetDeviceIDs(platform,  CL_DEVICE_TYPE_GPU, 1, &gDevice, NULL );
    if (error) {
        printf( "clGetDeviceIDs failed: %d\n", error );
        return error;
    }

    gContext = clCreateContext( NULL, 1, &gDevice, NULL, NULL, &error );

    gQueue = clCreateCommandQueue(gContext, gDevice, CL_QUEUE_PROFILING_ENABLE, &error);
    if( NULL == gQueue || error )
    {
        printf( "clCreateCommandQueue failed. (%d)\n", error );
        return -2;
    }

    /* Create input and output buffers*/
    gInBuffer = clCreateBuffer(gContext, CL_MEM_READ_WRITE, BUFFER_SIZE, NULL, &error);
    if( gInBuffer == NULL || error )
    {
        printf( "clCreateBuffer failed for input (%d)\n", error );
        return -4;
    }

    gOutBuffer = clCreateBuffer( gContext, CL_MEM_READ_WRITE, BUFFER_SIZE, NULL, &error );
    if( gOutBuffer == NULL  || error)
    {
        printf( "clCreateBuffer failed for input (%d)\n", error );
        return -4;
    }
    return error;
}

void initCLKernel()
{
    cl_int err = CL_FALSE;

    gProgram = clCreateProgramWithSource(gContext, 1, (const char**)&kernelSource, NULL, &err);
    if(CL_SUCCESS != err)
    {
        printf("clCreateProgramWithSource failed: error: %d\n", err);
        exit(err);
    }
    else
    {
        printf("clCreateProgramWithSource succeeded: gProgram: %x\n", (unsigned int)gProgram);
    }

    err = clBuildProgram(gProgram,
                         0,
                         NULL,
                         " -cl-fast-relaxed-math",
                         NULL,
                         NULL);
    if(CL_SUCCESS != err)
    {
        printf("clBuildProgram failed: error: %d\n", err);
        exit(err);
    }
    else
    {
        printf("clBuildProgram succeeded\n");
    }

    /* Create the kernel */
    gKernel = clCreateKernel(gProgram, "math_kernel", &err);
    if(CL_SUCCESS != err)
    {
        printf("clCreateKernel failed: error: %d\n", err);
        exit(err);
    }
    else
    {
        printf("clCreateKernel succeeded: kernelID: %x\n", (unsigned int)gKernel);
    }

}

void memset_pattern4(void *dest, const void *src_pattern, size_t bytes )
{
    uint32_t pat = ((uint32_t*) src_pattern)[0];
    size_t count = bytes / 4;
    size_t i;
    uint32_t *d = (uint32_t*)dest;

    for( i = 0; i < count; i++ )
        d[i] = pat;

    d += i;

    bytes &= 3;
    if( bytes )
        memcpy( d, src_pattern, bytes );
}

float genrand_float()
{
    float f = ((float)rand()/(float)(RAND_MAX)) * 0.5;
    return f;
}

void runCL()
{
    cl_int          err         = CL_FALSE;
    cl_uint         *outp       = NULL;
    uint32_t        pattern     = 0xffffdead;
    cl_float*       hostptr     = NULL;
    int             i           = 0;
    cl_event        evt         = 0;
    cl_ulong        startTime   = 0;
    cl_ulong        endTime     = 0;
    cl_ulong        execTime    = 0;

    /** Set the kernel arguments */
    err  = clSetKernelArg(gKernel, 0, sizeof(cl_mem), &gOutBuffer);
    err &= clSetKernelArg(gKernel, 1, sizeof(cl_mem), &gInBuffer);
    if(CL_SUCCESS != err)
    {
        printf("clSetKernelArg failed: error: %d\n", err);
        exit(err);
    }
    else
    {
        printf("clSetKernelArg success\n");
    }

    /* Fill output buffer with 0xffffdead */
    outp = (cl_uint*) clEnqueueMapBuffer( gQueue, gOutBuffer, CL_TRUE, CL_MAP_WRITE, 0, BUFFER_SIZE, 0, NULL, NULL, &err);
    if( err || NULL == outp)
    {
        printf( "Error: clEnqueueMapBuffer failed! err: %d\n", err );
        return err;
    }
    memset_pattern4(outp, &pattern, BUFFER_SIZE);
    if( (err = clEnqueueUnmapMemObject( gQueue, gOutBuffer, outp, 0, NULL, NULL) ))
    {
        printf( "Error: clEnqueueMapBuffer failed! err: %d\n", err );
        exit(err);
    }

    /* Fill input buffer with random float values*/
    srand((unsigned int)time(NULL));
    hostptr = clEnqueueMapBuffer(gQueue, gInBuffer, CL_TRUE, CL_MAP_WRITE, 0, BUFFER_SIZE,
            0, NULL, NULL, &err);
    for(i = 0; i < BUFFER_SIZE/sizeof(cl_float); i++)
    {
        hostptr[i] = genrand_float();
    }
    if( (err = clEnqueueUnmapMemObject( gQueue, gInBuffer, hostptr, 0, NULL, NULL) ))
    {
        printf( "Error: clEnqueueMapBuffer failed! err: %d\n", err );
        exit(err);
    }

    size_t vectorCount = BUFFER_SIZE/sizeof(float);
    printf("Global work dimension = %d\n", vectorCount);
    if( (err = clEnqueueNDRangeKernel(gQueue, gKernel, 1, NULL, &vectorCount, NULL, 0, NULL, &evt)))
    {
        printf( "Error:  could not execute kernel %d\n", err );
        exit(err);
    }
    printf("clEnqueueNDRangeKernel success\n");

    if( (err = clWaitForEvents(1, &evt) ))
    {
        printf( "clWaitForEvents failed %d\n", err );
        exit(err);
    }

    err = clGetEventProfilingInfo(evt, CL_PROFILING_COMMAND_START, sizeof(startTime), &startTime, NULL);
    err |= clGetEventProfilingInfo(evt, CL_PROFILING_COMMAND_END, sizeof(endTime), &endTime, NULL);
    execTime = endTime - startTime;
    printf("exec time: %llu ns\n", execTime);

    clReleaseEvent(evt);
}

void cleanupCL()
{
    clReleaseMemObject(gInBuffer);
    clReleaseMemObject(gOutBuffer);
    clReleaseKernel(gKernel);
    clReleaseProgram(gProgram);
    clReleaseCommandQueue(gQueue);
    clReleaseContext(gContext);
}

int main(int argc, char *argv[])
{
    initCL();
    initCLKernel();
    runCL();
    cleanupCL();

    return 0;
}
