inherit qti-proprietary-prebuilt

DESCRIPTION = "Video encoder applications"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-video.tar.gz;subdir=mm-video"

S = "${WORKDIR}/mm-video"

PROVIDES += " ${PN}-firmware "
RPROVIDES_${PN}-firmware = "${PN}-firmware"

files_${PN}-firmware = "/lib/firmware/*"

PV = "1.0"
PR = "r0"

