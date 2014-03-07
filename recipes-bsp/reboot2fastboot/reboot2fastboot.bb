inherit autotools

DESCRIPTION = "reboot2fastboot causes target system to reboot into fastboot mode."
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV="1.0"
PN = "reboot2fastboot"

PROVIDES = "reboot2fastboot"

#PACKAGE_ARCH = "armhf"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
            file://reboot2fastboot.c \
            file://__rfastboot.S \
            file://COPYING \
	    "


#EXTRA_OECONF = "--host=${ELT_TARGET_PREFIX}"

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
    shutil.copy(wd+'/COPYING', s)
}
