inherit qti-proprietary-prebuilt

DESCRIPTION = "mm-mux-noship libs prebuilt"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-mux-noship.tar.gz;subdir=mm-mux-noship"

S = "${WORKDIR}/mm-mux-noship"

PV = "1.0"
PR = "r0"

