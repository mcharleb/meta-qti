DESCRIPTION = "Qualcomm common include files."
AUTHOR = "Gene W. Marsh <gmarsh@codeaurora.org"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../COPYING.MIT;md5=838c366f69b72c5df05c96dff79b35f2"

PROVIDES += "common"
PN = "qcom-common"
PR = "r0"

SRC_URI = "git://git.quicinc.com:29418/platform/vendor/qcom-proprietary/ship/common;protocol=ssh;tag=AU_LINUX_ANDROID_JB_2.5.6.04.02.02.093.144"
SRC_URI += "file://COPYING.MIT"

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
