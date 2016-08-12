inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "MM Image processing library for MSM/QSD"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN} ${PN}-dbg"

# Must be built in src dir
B = "${S}"

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "glib-2.0"
#DEPENDS += "system-core"
#DEPENDS += "mm-image-codec"
DEPENDS += "mm-video-oss"
DEPENDS += "camera-hal"
DEPENDS += "camera-hal-headers"
#DEPENDS += "linux-libc-headers"

CAMERA_TARGET= "msm8974"

CFLAGS += "-I ${STAGING_INCDIR}/camera-hal/"
CXXFLAGS += "-I ${STAGING_INCDIR}/camera-hal/"
CFLAGS += "-I ${STAGING_INCDIR}/glib-2.0"
CFLAGS += "-I ${STAGING_LIBDIR}/glib-2.0/include"
LDFLAGS += "-lglib-2.0"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_DIR_TARGET}/usr/src/${MACHINE}/include"
EXTRA_OECONF_append = " --with-common-includes=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --with-camera-hal-includes=${STAGING_INCDIR}/libcamera2"
EXTRA_OECONF_append = " --with-omx-includes=${STAGING_INCDIR}/omx"
EXTRA_OECONF_append = " --enable-jelly-bean=no"
EXTRA_OECONF_append = " --enable-target=${CAMERA_TARGET}"

FILES_${PN} += "\
    /usr/lib/* "

INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"
INSANE_SKIP_${PN} += "textrel"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../mm-still'

    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append () {
    install -d ${D}/usr/include
    install -d ${D}/usr/include/jpeg2/inc
    install -m 0644 ${S}/jpeg2/inc/*.h ${D}/usr/include/jpeg2/inc
    install -d ${D}/usr/include/omx/inc
    install -m 0644 ${S}/omx/inc/*.h ${D}/usr/include/omx/inc
}
