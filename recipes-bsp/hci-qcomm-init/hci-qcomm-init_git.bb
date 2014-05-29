inherit autotools

DESCRIPTION = "hci_qcomm_init proprietary binary to configure bluetooth"
SECTION = "base"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI = "file://Makefile.am file://configure.ac"
SRC_URI += "file://0000-hci_qcomm_init-and-btnvtool.patch"
SRC_URI += "file://bt.init.sh"
PACKAGES = "${PN}"

PROVIDES = "hci-qcomm-init btnvtool"
#DEPENDS += "rt"

EXTRA_OECONF += "--with-common-includes=${STAGING_INCDIR}"
EXTRA_OECONF += "CPPFLAGS='-I${PKG_CONFIG_SYSROOT_DIR}/usr/include' "

INITSCRIPT_NAME = "bt.init.sh"
#INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../hci-qcomm-init'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    shutil.copy(wd+'/configure.ac', s)
    shutil.copy(wd+'/Makefile.am', s)
    shutil.copy(wd+'/bt.init.sh', s)
}

do_install_append() {
       install -m 0755 ${WORKDIR}/bt.init.sh -D ${D}${sysconfdir}/bt.init.sh
}
