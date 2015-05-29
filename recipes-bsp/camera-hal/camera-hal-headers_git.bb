DESCRIPTION = "HAL libraries for camera"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"
SRC_URI = "file://0001-camera-hal-compilation-changes-for-QR-Linux.patch"
PACKAGES = "${PN}"

INSANE_SKIP_${PN} += "installed-vs-shipped"

do_fetch_append() {
    import shutil
    import os

    src = d.getVar('COREBASE', True)+'/../camera-hal'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install() {
   install -d ${D}/usr/include/camera-hal
   cp -a ${S}/QCamera2/stack/common/*.h ${D}/usr/include/camera-hal
}
