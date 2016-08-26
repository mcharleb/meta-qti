inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm MSM Interface (QMI) Library"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI = "file://${QTI_PREBUILT_DIR}/qmi.tar.gz"

S = "${WORKDIR}/qmi"

PV = "1.0"
PR = "r0"

PROVIDES = "qmi"
RPROVIDES_${PN} = "qmi"

RDEPENDS_${PN} = "dsutils glib-2.0 diag xmllib configdb"
