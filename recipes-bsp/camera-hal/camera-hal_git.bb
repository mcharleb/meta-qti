# This recipe is different from the other recipes in meta-qti in that it downloads
# and builds open source code, whereas all other recipes build proprietary code.
# Thus this recipe actually has a SRC_URI for downloading from CAF
DESCRIPTION = "HAL libraries for camera"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI = "git://codeaurora.org/platform/hardware/qcom/camera;protocol=git;nobranch=1"

SRC_URI_append_som8064 = " file://0001-som8064-baseline-for-linux.patch"
SRC_URI_append_som8064 = " file://0002-stereo-3d-mode.patch"

SRC_URI_append_som8064-revB = " file://0001-som8064-baseline-for-linux.patch"
SRC_URI_append_som8064-revB = " file://0002-stereo-3d-mode.patch"

SRC_URI_append_som8064-const = " file://0001-som8064-baseline-for-linux.patch"
SRC_URI_append_som8064-const = " file://0002-stereo-3d-mode.patch"

SRC_URI_append_ifc6410 = " file://0001-ifc6410-baseline-for-linux.patch"

PACKAGES = "${PN}"

SRCREV_som8064 = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_som8064-revB = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_som8064-const = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_ifc6410 = "AU_LINUX_ANDROID_JB_2.5_AUTO.04.02.02.115.005"

inherit autotools qti-proprietary-binary

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "mm-camera-headers"
DEPENDS += "mm-still"
DEPENDS += "mm-video-oss"

BASEMACHINE = "msm8960"

CFLAGS += "-I./mm-camera-interface"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include/media"
CFLAGS += "-I${STAGING_INCDIR}/jpeg2/inc"
CFLAGS += "-I${STAGING_INCDIR}/mm-camera/common"
CFLAGS += "-I${STAGING_INCDIR}/mm-core"
CFLAGS += "-I${STAGING_INCDIR}/omx/inc"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/include"
EXTRA_OECONF_append = " --enable-target=${BASEMACHINE}"


FILES_${PN}_append += "/usr/lib/hw/*"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install_append() {

   dest=${D}/usr/lib/hw
   mkdir -p ${dest}

   # Move and rename libcamera.so files to hw/machine-specific names.
   cp ${D}/usr/lib/libcamera.so.0.0.0 ${dest}/libcamera.so
   cp ${D}/usr/lib/hw/libcamera.so ${dest}/camera.msm8960.so
}
