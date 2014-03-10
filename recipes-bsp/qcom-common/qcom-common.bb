DESCRIPTION = "Qualcomm common include files."
AUTHOR = "Gene W. Marsh <gmarsh@qti.qualcomm.com>"

LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PROVIDES += "common"
PN = "qcom-common"
PV = "1.0"
PR = "r0"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/common;protocol=git;tag=AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"

PACKAGES = "${PN}"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install() {
    install -d ${IMAGE_ROOTFS}${includedir}
    install -m 644 ${S}/inc/* ${IMAGE_ROOTFS}${includedir}
}

common_include_sysroot() {
   install -d ${SYSROOT_DESTDIR}${includedir}
   install -m 644 ${S}/inc/* ${SYSROOT_DESTDIR}${includedir}
}

SYSROOT_PREPROCESS_FUNCS = "common_include_sysroot"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
