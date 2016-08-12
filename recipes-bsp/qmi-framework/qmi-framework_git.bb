inherit autotools pkgconfig

DESCRIPTION = "QMI Framework Library"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI += "file://start_irsc_util"

# Must build in src dir
B = "${S}"

DEPENDS = "qmi"
DEPENDS += "glib-2.0"

EXTRA_OECONF = "--with-qmux-libraries=${STAGING_LIBDIR}"
EXTRA_OECONF += "--with-glib"
EXTRA_OECONF += "QMI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi"


#re-use non-perf settings
BASEMACHINE = "${@d.getVar('MACHINE', True).replace('-perf', '')}"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../qmi-framework'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
