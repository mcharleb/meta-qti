inherit autotools

DESCRIPTION = "math-cl tests OpenCL fast-math"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV="1.0"
PN = "math-cl"

PROVIDES = "math-cl"

FILES_{PN} += "${prefix}/share/math-cl/NOTICE"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
	    file://math.c \
	    file://NOTICE \
	    "

LDFLAGS += "-lOpenCL -lm -L${STAGING_LIBDIR}"

inherit qti-proprietary-binary

DEPENDS += "adreno200"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if not os.path.exists(s):
        os.makedirs(s)
    shutil.copy(wd+'/configure.ac', s)
    shutil.copy(wd+'/Makefile.am', s)
    shutil.copy(wd+'/math.c', s)
}

do_install_append() {
    install -d ${D}${prefix}/share/math-cl
    install ${WORKDIR}/NOTICE ${D}${prefix}/share/math-cl/NOTICE
}
