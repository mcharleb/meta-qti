inherit qti-proprietary-prebuilt

DESCRIPTION = "ftmdaemon"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/ftmdaemon.tar.gz"

S = "${WORKDIR}/ftmdaemon"

PV = "1.0"
PR = "r0"

PROVIDES = "ftmdaemon"
RPROVIDES_${PN} = "ftmdaemon"

FILES_${PN} = "/usr/bin/*"

