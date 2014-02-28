inherit autotools update-rc.d

DESCRIPTION = "Thermal Engine"
SECTION = "base"
LICENSE = "Qualcomm Technologies Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qcom/files/qcom-licenses/\
QUALCOMM-Proprietary;md5=92b1d0ceea78229551577d4284669bb8"

PR = "r3"

SRC_URI = "file://${WORKSPACE}/thermal-engine"

S = "${WORKDIR}/thermal-engine"
DEPENDS = "qmi-framework glib-2.0"

EXTRA_OECONF = "--with-qmi-framework  --with-glib"

#re-use non-perf settings
BASEMACHINE = "${@d.getVar('MACHINE', True).replace('-perf', '')}"

EXTRA_OECONF += "${@base_conditional('BASEMACHINE', 'mdm9625', '--enable-target-mdm9625=yes', '', d)}"
EXTRA_OECONF += "${@base_conditional('BASEMACHINE', 'msm8974', '--enable-target-msm8974=yes', '', d)}"

INITSCRIPT_NAME = "thermal-engine"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 60 0 1 6 ."


do_install_append() {
       install -m 0755 ${WORKDIR}/thermal-engine/start_thermal-engine_le -D ${D}${sysconfdir}/init.d/thermal-engine
}
