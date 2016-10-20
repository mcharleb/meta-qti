inherit qti-proprietary-prebuilt

DESCRIPTION = "Subsystem Restart utilities"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/ss-restart.tar.gz;subdir=ss-restart"

S = "${WORKDIR}/ss-restart"

RDEPEND_${PN} = "glib-2.0"

PV = "1.0"
PR = "r0"
