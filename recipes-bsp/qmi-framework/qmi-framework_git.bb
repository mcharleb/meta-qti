DESCRIPTION = "QMI Framework Library"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r6"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/qmi-framework;protocol=git;tag=AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"
SRC_URI += "file://start_irsc_util"

DEPENDS = "qmi"
DEPENDS += "glib-2.0"

EXTRA_OECONF = "--with-qmux-libraries=${STAGING_LIBDIR}"
EXTRA_OECONF += "--with-glib"
EXTRA_OECONF += "QMI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi"


inherit autotools

#re-use non-perf settings
BASEMACHINE = "${@d.getVar('MACHINE', True).replace('-perf', '')}"

INITSCRIPT_NAME = "init_irsc_util"
INITSCRIPT_PARAMS = "start 29 2 3 4 5 . stop 71 0 1 6 ."

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
       install -m 0755 ${WORKDIR}/start_irsc_util -D ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
}

pkg_postinst_qmi-framework () {
          [ -n "$D" ] && OPT="-r $D" || OPT="-s"
          update-rc.d $OPT -f ${INITSCRIPT_NAME} remove
          update-rc.d $OPT ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
}
