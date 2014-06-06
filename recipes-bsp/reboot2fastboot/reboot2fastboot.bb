inherit autotools

DESCRIPTION = "reboot2fastboot causes target system to reboot into fastboot mode."
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV="1.0"
PN = "reboot2fastboot"

PROVIDES = "reboot2fastboot"

FILES_{PN} += "${prefix}/share/reboot2fastboot/NOTICE"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
            file://reboot2fastboot.c \
            file://__rfastboot.S \
	    file://NOTICE \
	    "


#EXTRA_OECONF = "--host=${ELT_TARGET_PREFIX}"
inherit qti-proprietary-binary

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if not os.path.exists(s):
        os.makedirs(s)
    shutil.copy(wd+'/configure.ac', s)
    shutil.copy(wd+'/Makefile.am', s)
    shutil.copy(wd+'/reboot2fastboot.c', s)
    shutil.copy(wd+'/__rfastboot.S', s)
}

do_install_append() {
    install -d ${D}${prefix}/share/reboot2fastboot
    install ${WORKDIR}/NOTICE ${D}${prefix}/share/reboot2fastboot/NOTICE
}
