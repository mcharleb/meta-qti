inherit qti-proprietary-prebuilt

DESCRIPTION = "MM Image processing library for MSM/QSD"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-still.tar.gz;subdir=mm-still"

S = "${WORKDIR}/mm-still"

PV = "1.0"
PR = "r0"

PACKAGES += " ${PN}-dbg"

