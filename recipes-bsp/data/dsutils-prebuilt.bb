inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm Data DSutils Module"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/dsutils.tar.gz"

S = "${WORKDIR}/dsutils"

PV = "1.0"
PR = "r0"

PROVIDES = "dsutils"
RPROVIDES_${PN} = "dsutils"

RDEPENDS_${PN} = "glib-2.0 diag"
