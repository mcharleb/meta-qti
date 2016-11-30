DESCRIPTION = "Sensor Header Files"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += "installed-vs-shipped"

do_unpack_append() {
    import shutil
    import os

    src = d.getVar('COREBASE', True)+'/../sensors'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install() {
   install -d ${D}/usr/include/sensors
   cp -a ${S}/dsps/api/*.h ${D}/usr/include/sensors
}
