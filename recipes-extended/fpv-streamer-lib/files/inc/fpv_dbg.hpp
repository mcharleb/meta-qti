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
#ifndef FPV_DEB_HPP
#define FPV_DEB_HPP

#include <syslog.h>
#include <stdio.h>
#include <stdarg.h>

#define FPV_DEBUG 

#ifdef FPV_DEBUG

/*   #define CDBG_ERROR(fmt,args...) syslog(LOG_INFO, fmt,##args)
   #define CDBG_INFO(fmt,args...) syslog(LOG_INFO, fmt,##args)
   #define CDBG(fmt,args...) syslog(LOG_INFO, fmt,##args)
*/
/*
   #define CDBG_ERROR(lv, fmt,args...)  { \
        if ( lv > 0)   \
            {printf( fmt,##args);} \
        } 

   #define CDBG_INFO(lv, fmt,args...) { \
        if ( lv > 0)  \
            { printf(fmt,##args); }\
        } 
*/
   #define CDBG_ERROR(lv, fmt,args...)  { \
        if ( lv > 0)   \
            {syslog(LOG_INFO,fmt,##args);} \
        } 

   #define CDBG_INFO(lv, fmt,args...) { \
        if ( lv > 0)  \
            { syslog(LOG_INFO,fmt,##args); }\
        } 


#else
   #define CDBG_ERROR(lv, fmt,args...) do{} while(0)
   #define CDBG_INFO(lv, fmt,args...)  do{} while(0)

#endif



extern int debuginfo ;
extern int debugerror;


#endif

