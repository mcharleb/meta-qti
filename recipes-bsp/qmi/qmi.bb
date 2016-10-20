inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm MSM Interface (QMI) Library"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI = "file://${QTI_PREBUILT_DIR}/qmi.tar.gz;subdir=qmi"

S = "${WORKDIR}/qmi"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} = "dsutils glib-2.0 diag xmllib configdb"
