inherit pkgconfig qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm Data DSutils Module"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/dsutils.tar.gz;subdir=dsutils"

S = "${WORKDIR}/dsutils"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} = "glib-2.0 diag"
