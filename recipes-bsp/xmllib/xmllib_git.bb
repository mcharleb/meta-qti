inherit autotools

DESCRIPTION = "Qualcomm XML Library"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS = "common diag glib-2.0"
PV = "1.0"
PR = "r7"

EXTRA_OECONF = "--with-common-includes=${STAGING_INCDIR} \
                --with-glib \
                --with-qxdm"

inherit qti-proprietary-binary repo-source

do_unpack_append() {
    import shutil
    import os.path

    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

