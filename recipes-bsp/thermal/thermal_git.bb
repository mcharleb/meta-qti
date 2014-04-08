inherit autotools

DESCRIPTION = "Thermal Daemon"
SECTION = "base"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r4"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/thermal;protocol=git;tag=AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"
SRC_URI += "file://0001-init-script-mods-to-correctly-call-start-stop-daemon.patch"
SRC_URI += "file://thermald.conf"
SRC_URI += "file://thermald-8064.conf"
SRC_URI += "file://thermald-8064ab.conf"

DEPENDS = "qmi-framework glib-2.0"

EXTRA_OECONF = "--with-glib \
     	        QMI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi \
     	        QMIF_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi-framework \
		GLIB_CFLAGS='-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 -I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/glib-2.0/include' \
		"

INITSCRIPT_NAME = "thermald"
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
       install -m 0755 ${WORKDIR}/thermald.conf -D ${D}${sysconfdir}/init/thermald.conf
       install -m 0755 ${WORKDIR}/thermald-8064.conf -D ${D}${sysconfdir}/thermald-8064.conf
       install -m 0755 ${WORKDIR}/thermald-8064ab.conf -D ${D}${sysconfdir}/thermald-8064ab.conf
}
