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

#ifndef FPV_STREAM_HPP_
#define FPV_STREAM_HPP_
#include <string>
#include <stdint.h>

#include "camera.h"

// max frame size defined with 1920x1080x1.5 for full HD yuv420 format
#define MAX_FRAME_SZ     3110400

/**
 * @brief initialize the fpv instance.
 *
**/

int fpv_initialize(camera::ICameraDevice*);
/**
 * @brief start the fpv instance. set the fpv with right parameters.
 *
**/
void* fpv_start(void*);

/**
 * @brief stop the fpv instance.
 *
**/
bool fpv_stop();

#endif /* FPV_STREAM_HPP_ */

