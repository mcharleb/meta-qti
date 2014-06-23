DESCRIPTION = "MM Image processing library for MSM/QSD"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.1"
PR = "r10"

SRC_URI_append_som8064 = " file://0001-som8064-makefiles-for-linux.patch"
SRC_URI_append_som8064 = " file://0002-som8064-baseline-for-linux.patch"

SRC_URI_append_ifc6410 = " file://0001-ifc6410-makefiles-for-linux.patch"
SRC_URI_append_ifc6410 = " file://0002-ifc6410-baseline-for-linux.patch"

PACKAGES = "${PN}"

inherit autotools qti-proprietary-binary

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "glib-2.0"
#DEPENDS += "system-core"
#DEPENDS += "mm-image-codec"
DEPENDS += "mm-video-oss"
DEPENDS += "camera-hal-headers"
#DEPENDS += "linux-libc-headers"

CAMERA_TARGET= "msm8960"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"
EXTRA_OECONF_append = " --with-common-includes=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --with-camera-hal-includes=${STAGING_INCDIR}/libcamera2"
EXTRA_OECONF_append = " --with-omx-includes=${STAGING_INCDIR}/mm-core"
EXTRA_OECONF_append = " --enable-jelly-bean=no"
EXTRA_OECONF_append = " --enable-target=${CAMERA_TARGET}"

FILES_${PN} += "\
    /usr/lib/* "

# The mm-still package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../'+d.getVar('MACHINE', True)+'/mm-still'
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
