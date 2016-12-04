inherit qti-proprietary-prebuilt

DESCRIPTION = "mm-camera-core prebuilt libraries"
SECTION = "base"

SRC_URI = "file://${QTI_PREBUILT_DIR}/mm-camera-core.tar.gz;subdir=mm-camera-core"

S = "${WORKDIR}/mm-camera-core"

PV = "1.0"
PR = "r0"

FILES_${PN} = "/usr/lib/*.so.*"
