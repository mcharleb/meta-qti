inherit autotools update-rc.d

DESCRIPTION = "Thermal Engine"
SECTION = "base"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r4"

SRC_URI = "git://git.quicinc.com:29418/platform/vendor/qcom-proprietary/thermal-engine;protocol=ssh;tag=AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"


DEPENDS = "qmi-framework glib-2.0"

EXTRA_OECONF = "--with-qmi-framework  --with-glib \
     	        QMI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi \
     	        QMIF_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi-framework \
		GLIB_CFLAGS='-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 -I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/glib-2.0/include' \
		"

#re-use non-perf settings

INITSCRIPT_NAME = "thermal-engine"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 60 0 1 6 ."

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
       install -m 0755 ${WORKDIR}/thermal-engine/start_thermal-engine_le -D ${D}${sysconfdir}/init.d/thermal-engine
}
