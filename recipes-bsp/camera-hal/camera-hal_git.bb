# This recipe is different from the other recipes in meta-qti in that it downloads
# and builds open source code, whereas all other recipes build proprietary code.
# Thus this recipe actually has a SRC_URI for downloading from CAF
DESCRIPTION = "HAL libraries for camera"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

SRC_URI = "file://0001-camera-hal-compilation-changes-for-QR-Linux.patch"
SRC_URI += "file://0002-camera-hal-limit-number-of-frame-dump-to-100-and-dump-at-mod30.patch"
SRC_URI += "file://0003-camera-hal-create-qcamlib-wrapper-library.patch"
SRC_URI += "file://0004-camera-hal-reduce-logging-in-camera-app.patch"
SRC_URI += "file://0005-camera-hal-remove-android-includes-references-from-M.patch"

inherit autotools qti-proprietary-binary

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "mm-camera-headers"
DEPENDS += "android-tools"
DEPENDS += "mm-video-oss"
DEPENDS += "libhardware-headers"
DEPENDS += "system-headers"

CFLAGS += "-I./mm-camera-interface"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include/media"
CFLAGS += "-I${STAGING_INCDIR}/mm-core"
CFLAGS += "-I${STAGING_INCDIR}/omx/inc"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/include"
EXTRA_OECONF_append = " --enable-target=msm8974"


FILES_${PN}_append += "/usr/lib/hw/*"
FILES_${PN} += "/usr/lib/*.so"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"


do_fetch_append() {
    import shutil
    import os

    src = d.getVar('COREBASE', True)+'/../camera-hal'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
