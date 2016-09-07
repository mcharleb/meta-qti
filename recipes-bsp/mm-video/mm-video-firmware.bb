inherit qti-proprietary-prebuilt

DESCRIPTION = "Video encoder HW firmware"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-video-firmware.tar.gz;subdir=mm-video-firmware"

S = "${WORKDIR}/mm-video-firmware"

PV = "1.0"
PR = "r0"
