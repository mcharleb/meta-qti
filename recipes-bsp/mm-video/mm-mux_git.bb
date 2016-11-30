DESCRIPTION = "mm-mux muxer application and libs"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PACKAGES = "${PN} ${PN}-dbg"

PV = "1.0"
PR = "r0"

DEPENDS += "kernel-dev mm-video-oss mm-mux-noship autogen-native glib-2.0 libnl"

inherit autotools

FILES_${PN} = "\
    ${libdir}/*.so* \
    ${bindir}/*"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"
EXTRA_OECONF += "--with-glib"

INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"

# mm-mux includes

CFLAGS += "-I${STAGING_INCDIR}/libnl3"
CFLAGS += "-I${STAGING_KERNEL_DIR}/include"
CFLAGS += "-I${STAGING_INCDIR}/mm-mux-noship"
CFLAGS += "-I${STAGING_INCDIR}/omx"
CFLAGS += "-I${STAGING_INCDIR}"
CFLAGS += "-L${STAGING_LIBDIR}/mm-mux-noship"

CXXFLAGS += "-I${STAGING_INCDIR}/libnl3"
CXXFLAGS += "-I${STAGING_KERNEL_DIR}/include"
CXXFLAGS += "-I${STAGING_INCDIR}/mm-mux-noship"
CXXFLAGS += "-I${STAGING_INCDIR}/omx"
CXXFLAGS += "-I${STAGING_INCDIR}"
CXXFLAGS += "-L${STAGING_LIBDIR}/mm-mux-noship"

do_unpack_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../mm-video/mm-mux'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
