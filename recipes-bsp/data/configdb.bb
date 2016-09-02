inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm Data Configdb Module"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/configdb.tar.gz;subdir=configdb"

S = "${WORKDIR}/configdb"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} = "diag xmllib dsutils glib-2.0"
