inherit qti-proprietary-prebuilt

DESCRIPTION = "Video encoder applications"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-video.tar.gz;subdir=mm-video"

PV = "1.0"
PR = "r0"

S = "${WORKDIR}/mm-video"
