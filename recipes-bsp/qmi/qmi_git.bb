inherit autotools

DESCRIPTION = "Qualcomm MSM Interface (QMI) Library"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r10"

DEPENDS = "configdb diag"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/qmi;protocol=git;tag=AU_LINUX_ANDROID_JB_2.5.6.04.02.02.093.144"

CFLAGS += "${CFLAGS_EXTRA}"
CFLAGS_EXTRA_append_arm = " -fforward-propagate"

EXTRA_OECONF = "--with-stderr \
                --with-common-includes=${STAGING_INCDIR}"

EXTRA_OECONF_append_msm8960 = " --enable-auto-answer=yes"


INITSCRIPT_NAME = "qmuxd"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

inherit update-rc.d

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
