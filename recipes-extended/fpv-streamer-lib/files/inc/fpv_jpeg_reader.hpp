#ifndef FPV_JPEG_READER_HPP
#define FPV_JPEG_READER_HPP

#include <sys/time.h>
#include <time.h>

class fpvJpegReader
{
public:
    int readJpegFrame(int* sz,struct timeval* tm, unsigned char * data, unsigned char * quality);

};

#endif
