inherit qti-proprietary-prebuilt

DESCRIPTION = "MM Camera headers for MSM"
SECTION = "base"

PV = "1.0"
PR = "r0"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-camera-headers.tar.gz;subdir=mm-camera-headers"

S = "${WORKDIR}/mm-camera-headers"

