DESCRIPTION = "Video encoder applications"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PACKAGES = "${PN} ${PN}-dbg"

PV = "1.0"
PR = "r0"


SRC_URI += "file://fpv.cfg \
        file://fpv.conf \
        file://fpv.override \
            "

DEPENDS += "kernel-dev mm-video-oss mm-mux camera-hal libnl"
DEPENDS += "live555 libjpeg-turbo libopenh264"

inherit autotools

# This must come after "inherit autotools"
B = "${S}"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_DIR_TARGET}/usr/src/${MACHINE}/include \
                CPPFLAGS='-I${STAGING_INCDIR}/camera-hal'"

INSANE_SKIP_${PN} += "installed-vs-shipped"

# FPV includes
CXXFLAGS += "-I ${STAGING_INCDIR}/live555"
CFLAGS += "-I ${STAGING_INCDIR}/live555"

CXXFLAGS += "-I ${STAGING_INCDIR}/libnl3"
CFLAGS += "-I ${STAGING_INCDIR}/libnl3"

CXXFLAGS  += "-I${WORKSPACE}/hardware/qcom/display/libcopybit"
CXXFLAGS  += "-I${WORKSPACE}/hardware/qcom/display/libgralloc"
CXXFLAGS  += "-I${WORKSPACE}/hardware/qcom/media/libc2dcolorconvert"
CXXFLAGS  += "-I${STAGING_INCDIR}/mm-mux-noship"
CXXFLAGS  += "-I${STAGING_INCDIR}/omx"
CXXFLAGS  += "-L${STAGING_LIBDIR}/mm-mux-noship"

do_unpack_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../mm-video'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
   # install fpv upstart scripts
   dest=/etc/init
   install -d ${D}${dest}
   install -m 755 ${WORKDIR}/fpv.conf ${D}${dest}
   install -m 755 ${WORKDIR}/fpv.override ${D}${dest}

   # install FPV config file
   dest=/etc
   install -d ${D}${dest}
   install -m 755 ${WORKDIR}/fpv.cfg ${D}${dest}
}
