inherit qti-proprietary-prebuilt

DESCRIPTION = "mm-video (venus) firmware"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-video-firmware.tar.gz"

S = "${WORKDIR}/mm-video-firmware"

PV = "1.0"
PR = "r0"

FILES_${PN} += "/lib/firmware/*"

PROVIDES = "mm-video-firmware"
RPROVIDES_${PN} = "mm-video-firmware"

