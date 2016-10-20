inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm XML Library"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/xmllib.tar.gz;subdir=xmllib"

S = "${WORKDIR}/xmllib"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} = "glib-2.0"
