inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm Data Configdb Module"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/configdb.tar.gz"

S = "${WORKDIR}/configdb"

PV = "1.0"
PR = "r0"

PROVIDES = "configdb"
RPROVIDES_${PN} = "configdb"

RDEPENDS_${PN} = "diag xmllib dsutils glib-2.0"
