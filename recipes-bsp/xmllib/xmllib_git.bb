inherit autotools

DESCRIPTION = "Qualcomm XML Library"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"
DEPENDS = "common diag glib-2.0"
PR = "r7"

EXTRA_OECONF = "--with-common-includes=${STAGING_INCDIR} \
                --with-glib \
                --with-qxdm"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/xmllib;protocol=git;tag=AU_LINUX_ANDROID_JB_2.5.6.04.02.02.093.144"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

