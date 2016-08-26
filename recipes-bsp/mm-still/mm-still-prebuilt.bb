inherit qti-proprietary-prebuilt

DESCRIPTION = "MM Image processing library for MSM/QSD"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-still.tar.gz"

S = "${WORKDIR}/mm-still"

PV = "1.0"
PR = "r0"

PACKAGES += " ${PN}-dbg"

PROVIDES = "mm-still"
RPROVIDES_${PN} = "mm-still"

