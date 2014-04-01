inherit autotools

DESCRIPTION = "Qualcomm MSM Interface (QMI) Library"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r10"

DEPENDS = "configdb diag"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/qmi;protocol=git;tag=AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"

CFLAGS += "${CFLAGS_EXTRA}"
CFLAGS_EXTRA_append_arm = " -fforward-propagate"

EXTRA_OECONF = "--with-qxdm \
                --with-common-includes=${STAGING_INCDIR} \
		DIAG_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/diag \
		"

EXTRA_OECONF_append_msm8960 = " --enable-auto-answer=yes"


INITSCRIPT_NAME = "qmuxd"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

inherit qr-update-rc.d
inherit qti_proprietary_binary

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install_append() {
       install -m 0755 ${S}/qmuxd/start_qmuxd_le -D ${D}${sysconfdir}/init.d/qmuxd
}
