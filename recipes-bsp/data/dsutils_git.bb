inherit autotools

DESCRIPTION = "Qualcomm Data DSutils Module"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS = "common diag glib-2.0"
PV = "1.0"
PR = "r0"

inherit qti-proprietary-binary


EXTRA_OECONF = "--with-lib-path=${STAGING_LIBDIR} \
                --with-common-includes=${STAGING_INCDIR} \
                --with-glib \
                --with-qxdm \
		DIAG_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/diag \
		GLIB_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 \		
		"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../data/dsutils'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
