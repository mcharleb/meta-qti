inherit qti-proprietary-prebuilt

DESCRIPTION = "Thermal Engine"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/thermal-engine.tar.gz;subdir=thermal-engine"

S = "${WORKDIR}/thermal-engine"

PV = "1.0"
PR = "r0"

INITSCRIPT_NAME = "thermal-engine"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 60 0 1 6 ."
