DESCRIPTION = "Qualcomm common include files."
AUTHOR = "Gene W. Marsh <gmarsh@qti.qualcomm.com>"

LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PROVIDES += "common"
PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../qcom-common'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
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
