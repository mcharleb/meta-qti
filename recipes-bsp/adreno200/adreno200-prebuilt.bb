inherit autotools

DESCRIPTION = "c2d headers for mm-camera"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_fetch_append() {
    import shutil
    import os
    machine = d.getVar('MACHINE', True)
    src = d.getVar('COREBASE', True)+'/../prebuilt_HY11/target/'+machine+'/adreno200/'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install() {
	install -d ${D}/usr/include
	install -d ${D}/usr/include/C2D
	cp -r ${S}/include/C2D/* ${D}/usr/include/C2D/
}
