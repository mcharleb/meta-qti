inherit qti-proprietary-prebuilt

DESCRIPTION = "QMI Framework Library"

SRC_URI += "file://${QTI_PREBUILT_DIR}/qmi-framework.tar.gz;subdir=qmi-framework"

S = "${WORKDIR}/qmi-framework"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} +="qmi "
