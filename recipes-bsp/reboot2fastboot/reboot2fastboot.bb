inherit qti-proprietary-prebuilt

DESCRIPTION = "reboot2fastboot causes target system to reboot into fastboot mode."

PR = "r0"
PV="1.0"

SRC_URI += "file://${QTI_PREBUILT_DIR}/reboot2fastboot.tar.gz;subdir=reboot2fastboot"

S = "${WORKDIR}/reboot2fastboot"

PV = "1.0"
PR = "r0"
