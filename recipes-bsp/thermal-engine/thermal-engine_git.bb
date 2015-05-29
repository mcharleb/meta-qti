inherit autotools

DESCRIPTION = "Thermal Engine"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI += "file://thermal-engine.conf"
SRC_URI += "file://0001-fix-compilation-on-db8074-baseline.patch"


PACKAGES = "${PN}"

DEPENDS = "qmi-framework glib-2.0"

EXTRA_OECONF = "--with-glib \
    	        QMI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi \
     	        QMIF_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi-framework \
		GLIB_CFLAGS='-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 -I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/glib-2.0/include' \
		"
EXTRA_OECONF += "--enable-target-msm8974=yes"

INITSCRIPT_NAME = "thermal-engine"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 60 0 1 6 ."
INSANE_SKIP_${PN} = "installed-vs-shipped"

inherit qti-proprietary-binary

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../thermal-engine'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
       install -m 0755 ${WORKDIR}/thermal-engine.conf -D ${D}${sysconfdir}/init/thermal-engine.conf
}
