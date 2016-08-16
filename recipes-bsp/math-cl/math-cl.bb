inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "math-cl tests OpenCL fast-math"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV="1.0"
PN = "math-cl"

PROVIDES = "math-cl"

DEPENDS += "adreno200 \
	    adreno200-prebuilt \
	    "

FILES_{PN} += "${prefix}/share/math-cl/NOTICE"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
	    file://math.c \
	    file://NOTICE \
	    file://NEWS \
	    file://AUTHORS \
	    file://README \
	    file://ChangeLog \
	    file://math-cl-1.0/NEWS \
	    file://math-cl-1.0/AUTHORS \
	    file://math-cl-1.0/README \
	    file://math-cl-1.0/ChangeLog \
	    "

# Must be built in stc dir
B = "${S}"

LDFLAGS += "-Wl,--no-as-needed -lOpenCL -lm -L${STAGING_LIBDIR}"

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
