inherit autotools

DESCRIPTION = "Qualcomm MSM Interface (QMI) Library"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

DEPENDS = "configdb diag"

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
inherit qti-proprietary-binary

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../qmi'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
       install -m 0755 ${S}/qmuxd/start_qmuxd_le -D ${D}${sysconfdir}/init.d/qmuxd
}
