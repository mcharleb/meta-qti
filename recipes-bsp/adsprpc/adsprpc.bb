inherit qti-proprietary-prebuilt

SUMMARY = "Hexagon RPC daemon"
SECTION = "core"

SRC_URI += "file://${QTI_PREBUILT_DIR}/adsprpc.tar.gz;subdir=adsprpc"

S = "${WORKDIR}/adsprpc"

PR = "r0"
PV = "1.0"

