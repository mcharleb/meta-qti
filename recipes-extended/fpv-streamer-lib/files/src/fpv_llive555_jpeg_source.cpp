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
#include "fpv_live555_jpeg_source.hpp"
#include "fpv_dbg.hpp"
#include "fpv_utils.hpp"

MJPEGVideoSource* MJPEGVideoSource::createNew (UsageEnvironment& env, fpvJpegReader* source) {
    return new MJPEGVideoSource(env,source);
}

void MJPEGVideoSource::doGetNextFrame() {
    CDBG_INFO(debuginfo, " do get next frame \n");
    if (m_inputSource) {
        int sz;
        struct timezone info;//timezone info
        struct timeval camtm; 
        m_inputSource->readJpegFrame(&sz,&camtm, fTo, &m_quality);
        gettimeofday(&fLastJpegtime, &info); 
        afterGettingFrame(sz, 0, fLastJpegtime, durationms);
//        afterGettingFrame(sz, 0, camtm, durationms);
        CDBG_INFO(debuginfo, "get frame, cam tm s %ld, us %ld, curr tm s %ld, us %ld \n", camtm.tv_sec, camtm.tv_usec, fLastJpegtime.tv_sec, fLastJpegtime.tv_usec);
    }
}

void MJPEGVideoSource::doStopGettingFrames() {
    FramedSource::doStopGettingFrames();
    // should we add a callback here? when does this triggered?
    CDBG_INFO(debuginfo, " do stop getting frames in jpeg source! \n");
    fpv_stoppreview();
}
/*
void MJPEGVideoSource::afterGettingFrameSub(void* clientData, unsigned frameSize,unsigned numTruncatedBytes,struct timeval presentationTime,unsigned durationInMicroseconds) 
{
    MJPEGVideoSource* source = (MJPEGVideoSource*)clientData;
    source->afterGettingFrame(frameSize, numTruncatedBytes, presentationTime, durationInMicroseconds);
}
  */      
void MJPEGVideoSource::afterGettingFrame(unsigned frameSize,unsigned numTruncatedBytes,struct timeval presentationTime,unsigned durationInMicroseconds)
{
    int headerSize = 0;
    bool headerOk = false;

    fFrameSize = 0;
    for (unsigned int i = 0; i < frameSize ; ++i) {
    // SOF
        if ( (i+8) < frameSize  && fTo[i] == 0xFF && fTo[i+1] == 0xC0 )  {
            m_height = (fTo[i+5]<<5)|(fTo[i+6]>>3);
            m_width = (fTo[i+7]<<5)|(fTo[i+8]>>3);
        }
        // DQT
        if ( (i+5+64) < frameSize && fTo[i] == 0xFF && fTo[i+1] == 0xDB)
        {
            if (fTo[i+4] ==0) {
                memcpy(m_qTable, fTo + i + 5, 64);
                m_qTable0Init = true;
            }  else if (fTo[i+4] ==1) {
                memcpy(m_qTable + 64, fTo + i + 5, 64);
                m_qTable1Init = true;
            }
        }
        // End of header
        if ( (i+1) < frameSize && fTo[i] == 0x3F && fTo[i+1] == 0x00 ) {
            headerOk = true;
            headerSize = i+2;
            break;
        }
    }

    if (headerOk) {
        fFrameSize = frameSize - headerSize;
        memmove( fTo, fTo + headerSize, fFrameSize );
    }

    fNumTruncatedBytes = numTruncatedBytes;
//    fPresentationTime = presentationTime;
    if ( presentationTime.tv_usec > 999990) {
        fPresentationTime.tv_usec = (presentationTime.tv_usec + 10)%1000000;
        fPresentationTime.tv_sec = presentationTime.tv_sec + 1;
    } else {
        fPresentationTime = presentationTime;
        fPresentationTime.tv_usec += 10;
    }
    fDurationInMicroseconds = durationInMicroseconds;

    afterGetting(this);
}
unsigned MJPEGVideoSource::maxFrameSize() {
     return 100*1024;
}
u_int8_t MJPEGVideoSource::type() { return 1; };
u_int8_t MJPEGVideoSource::qFactor() { 
    return (u_int8_t) m_quality;//m_inputSource->getJpegRTPQuality(); 
};
u_int8_t MJPEGVideoSource::width() 
{ 
    return (u_int8_t) m_width;//(m_inputSource->getJpegRTPWidth()); 
};
u_int8_t MJPEGVideoSource::height() 
{

    return (u_int8_t) m_height;//(m_inputSource->getJpegRTPHeight()); 
};

u_int8_t const* MJPEGVideoSource::quantizationTables( u_int8_t& precision, u_int16_t& length )
{
    length = 0;
    precision = 0;
    if ( m_qTable0Init && m_qTable1Init )
    {
        precision = 8;
        length = sizeof(m_qTable);
    }
    return m_qTable;            
}

MJPEGVideoSource::MJPEGVideoSource(UsageEnvironment& env, fpvJpegReader* source) : JPEGVideoSource(env),
                m_inputSource(source),m_qTable0Init(false),m_qTable1Init(false)
{
    durationms = 1000000/30;
    memset(&m_qTable,0,sizeof(m_qTable));
    return;
}
MJPEGVideoSource::~MJPEGVideoSource() 
{ 
        m_inputSource = NULL; 
}



