bin_PROGRAMS = fpv-streamer-app

#fpv_streamer_app_SYSROOT_USR_INCLUDE = @SYSROOT_USR_INCLUDE@

fpv_streamer_app_CFLAGS = $(DEBUG_CFLAGS)
fpv_streamer_app_CXXFLAGS = -std=c++0x  $(fpv_streamer_app_CFLAGS)
fpv_streamer_app_SOURCES = src/fpv_streamer_app.cpp

fpv_streamer_app_LDADD = -lcamera -lqc_fpv_streamer -lpthread -ldl  -lturbojpeg -lBasicUsageEnvironment -lgroupsock -lliveMedia -lUsageEnvironment
