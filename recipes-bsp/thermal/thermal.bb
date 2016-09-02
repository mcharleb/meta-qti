inherit qti-proprietary-prebuilt

DESCRIPTION = "Thermal Daemon"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/thermal.tar.gz;subdir=thermal"

S = "${WORKDIR}/thermal"

PV = "1.0"
PR = "r0"

pkg_prerm_thermal() {
   stop thermald
   echo "Stopped thermald if necessary"
}
