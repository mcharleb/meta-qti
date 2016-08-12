inherit autotools pkgconfig

DESCRIPTION = "MM Camera headers for MSM/QSD"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"

do_fetch_append() {
    import shutil
    import os
    mach = d.getVar('MACHINE', True)

    src = d.getVar('COREBASE', True)+'/../mm-camera'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_configure() {
}

do_compile() {
}

do_install() {
   install -d ${D}/usr/include
   install -d ${D}/usr/include/mm-camera
   # Copy all headers
   rsync -av --include '*.h' --include '*/' --exclude '*' ${S}/. ${D}/usr/include/mm-camera/.
}

