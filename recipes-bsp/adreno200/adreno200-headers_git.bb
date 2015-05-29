DESCRIPTION = "HAL libraries for camera"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} += "installed-vs-shipped"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../adreno200'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install() {
   install -d ${D}/usr/include/adreno200
   cp -a ${S}/include/private/C2D/*.h ${D}/usr/include/adreno200
   cp -a ${S}/c2d30/include/*.h ${D}/usr/include/adreno200
}
