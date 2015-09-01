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
#include <unistd.h>
#include "fpv_stream.hpp"
#include "camera.h"
#include "camera_parameters.h"

using namespace camera;

enum CamFunction {
    CAM_FUNC_HIRES = 0,
    CAM_FUNC_OPTIC_FLOW = 1,
};

/**
 * @brief main entry point for the application. 
 *
**/
int main( int argc, char ** argv)
{
    ICameraDevice* mCamera;
    int camId = -1;

    /* Find and select the camera to use */
    int numCameras = getNumberOfCameras();
    printf("Number of cameras = %d\n", getNumberOfCameras());

    struct CameraInfo info;
    for (int i = 0 ; i < numCameras ; i++) {
        getCameraInfo(i, info);

        if ( CAM_FUNC_HIRES == info.func ) {
            camId = i;
            break;
        }
    }

    if (camId == -1 ) {
        printf("Failed to open camera\n");
        return 1;
    }

    int rc = ICameraDevice::createInstance(camId, &mCamera);
    if (rc != 0) {
        printf("Could not open camera %d\n", camId);
        return 1;
    }

    //Initialize the fpv instance
    if (0!= fpv_initialize(mCamera)) {
        printf( "error initialize the fpv module \n");
    } else {
        fpv_start(NULL);
    }

    return 0;
}


