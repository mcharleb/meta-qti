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
#ifndef FPV_LIVE555_JPEG_SOURCE_HPP
#define FPV_LIVE555_JPEG_SOURCE_HPP

#include "JPEGVideoSource.hh"
#include "fpv_jpeg_reader.hpp"

class MJPEGVideoSource : public JPEGVideoSource
{
    public:
        static MJPEGVideoSource* createNew (UsageEnvironment& env, fpvJpegReader* source);
    
        virtual void doGetNextFrame();
        virtual void doStopGettingFrames();
    
        static void afterGettingFrameSub(
                      void* clientData, unsigned frameSize,unsigned numTruncatedBytes,
                      struct timeval presentationTime,unsigned durationInMicroseconds);
        void afterGettingFrame(unsigned frameSize,unsigned numTruncatedBytes,
                       struct timeval presentationTime,unsigned durationInMicroseconds);
        u_int8_t const* quantizationTables( u_int8_t& precision, u_int16_t& length );

        virtual u_int8_t type() ;
        virtual u_int8_t qFactor() ;
        virtual u_int8_t width();
        virtual u_int8_t height();

    protected:
        MJPEGVideoSource(UsageEnvironment& env, fpvJpegReader* source);
        virtual unsigned maxFrameSize();
        virtual ~MJPEGVideoSource();

    protected:
        fpvJpegReader* m_inputSource;
        struct timeval fLastJpegtime;
        u_int8_t      m_qTable[128];
        bool          m_qTable0Init;
        bool          m_qTable1Init;
        u_int8_t      m_width;
        u_int8_t      m_height;
        u_int8_t      m_quality;
        unsigned durationms;
};
#endif
