inherit autotools pkgconfig

DESCRIPTION = "Qualcomm MSM Interface (QMI) Library"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

DEPENDS = "configdb diag"

# Must be built in src dir
B = "${S}"

CFLAGS += "${CFLAGS_EXTRA}"
CFLAGS_EXTRA_append_arm = " -fforward-propagate"

EXTRA_OECONF = "--with-qxdm \
                --with-common-includes=${STAGING_INCDIR} \
		DIAG_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/diag \
		"

EXTRA_OECONF_append_msm8960 = " --enable-auto-answer=yes"

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
