DESCRIPTION = "QMI Framework Library"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

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

inherit qr-update-rc.d

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../qmi-framework'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
       install -m 0755 ${WORKDIR}/start_irsc_util -D ${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}
}

pkg_postinst_qmi-framework () {
          update-rc.d -f ${INITSCRIPT_NAME} remove
          update-rc.d ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
}
