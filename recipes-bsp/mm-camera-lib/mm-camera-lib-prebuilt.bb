inherit autotools

DESCRIPTION = "hci_qcomm_init proprietary binary to configure bluetooth"
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
    src = d.getVar('COREBASE', True)+'/../prebuilt_HY11/target/'+machine+'/mm-camera-lib/'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install() {
	install -d ${D}/usr/include
	install -d ${D}/usr/include/mm-camera-lib
	cp -r ${S}/include/* ${D}/usr/include/mm-camera-lib/

	install -d ${D}/usr/lib
    install ${S}/lib/libmmcamera*.so ${D}/usr/lib/
}
