inherit qti-proprietary-prebuilt

DESCRIPTION = "MM Camera headers for MSM/QSD"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-camera-headers.tar.gz"

S = "${WORKDIR}/mm-camera-headers"

PV = "1.0"
PR = "r0"

FILES_${PN} = "/usr/include/*"

PROVIDES = "mm-camera-headers"
RPROVIDES_${PN} = "mm-camera-headers"
