inherit autotools

DESCRIPTION = "Qualcomm Data DSutils Module"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS = "common diag glib-2.0"
PV = "1.0"
PR = "r6"

SRC_URI = "git://${COREBASE}/../data;protocol=file;tag=AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"

inherit qti-proprietary-binary


EXTRA_OECONF = "--with-lib-path=${STAGING_LIBDIR} \
                --with-common-includes=${STAGING_INCDIR} \
                --with-glib \
                --with-qxdm \
		DIAG_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/diag \
		GLIB_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 \		
		"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git/dsutils', s)
}
