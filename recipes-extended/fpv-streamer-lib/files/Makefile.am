

lib_LTLIBRARIES = libqc_fpv_streamer.la

#libqc_fpv_streamer_la_SYSROOT_USR_INCLUDE = @SYSROOT_USR_INCLUDE@

libqc_fpv_streamer_la_CFLAGS = $(DEBUG_CFLAGS) -DLOG_DEBUG
libqc_fpv_streamer_la_CFLAGS += -std=c++0x -I ./inc -I ./src 
libqc_fpv_streamer_la_CXXFLAGS = $(libqc_fpv_streamer_la_CFLAGS)

$(info CFLAGS $(libqc_fpv_streamer_la_CFLAGS))

libqc_fpv_streamer_la_SOURCES = ./src/fpv_utils.cpp \
                                ./src/fpv_stream.cpp \
                                ./src/fpv_encoder_jpeg.cpp \
                                ./src/fpv_rtsp.cpp \
                                ./src/fpv_queue.cpp \
                                ./src/fpv_llive555_jpeg_source.cpp 

                                 
#$(info LDFLAGS $(libqc_fpv_streamer_la_LDFLAGS))
#libqc_fpv_streamer_la_SYSROOT_USR_LIB = @SYSROOT_USR_LIB@

#$(info SYSROOT_USR_LIB $(libqc_fpv_streamer_la_SYSROOT_USR_LIB))
                                 
#libqc_fpv_streamer_la_LIBADD = $(libqc_fpv_streamer_la_SYSROOT_USR_LIB)

#$(info LIBADD $(libqc_fpv_streamer_la_LIBADD))
  
