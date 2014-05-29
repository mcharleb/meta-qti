inherit autotools

DESCRIPTION = "Qualcomm Data Configdb Module"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS = "common dsutils diag xmllib glib-2.0"
PV = "1.0"
PR = "r0"

EXTRA_OECONF = "--with-lib-path=${STAGING_LIBDIR} \
                --with-common-includes=${STAGING_INCDIR} \
                --with-glib \
                --with-qxdm \
		XMLLIB_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/xmllib \
		DIAG_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/diag \
		DSUTILS_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/dsutils \
		GLIB_CFLAGS='-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 -I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/glib-2.0/include' \
		"
inherit qti-proprietary-binary

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../data/configdb'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
