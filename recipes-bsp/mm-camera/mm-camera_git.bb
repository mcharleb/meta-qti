DESCRIPTION = "MM Camera libraries for MSM/QSD"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI_append_som8064 = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064 = " file://0002-som8064-baseline-to-linux-platform.patch"
SRC_URI_append_som8064 = " file://0005-1-customer-YUV-sensor-module-AndroidMakefile.patch"
SRC_URI_append_som8064 = " file://0005-2-customer-YUV-sensor-module-Makefile.patch"
SRC_URI_append_som8064 = " file://0005-3-customer-YUV-sensor-module-source.patch"
SRC_URI_append_som8064 = " file://0004-enable-yuv-preview-snapshot-dump.patch"
SRC_URI_append_som8064 = " file://0006-lib-3a-legacy.patch"
SRC_URI_append_som8064 = " file://0007-stereo-sensor-imx111.patch"
SRC_URI_append_som8064 = " file://0008-stereo-sensor-imx111-csiparams.patch"
SRC_URI_append_som8064 = " file://0009-stereo-3d-mode.patch"
SRC_URI_append_som8064 = " file://0010-stereo-applib.patch"

SRC_URI_append_som8064-revB = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064-revB = " file://0002-som8064-baseline-to-linux-platform.patch"
SRC_URI_append_som8064-revB = " file://0005-1-customer-YUV-sensor-module-AndroidMakefile.patch"
SRC_URI_append_som8064-revB = " file://0005-2-customer-YUV-sensor-module-Makefile.patch"
SRC_URI_append_som8064-revB = " file://0005-3-customer-YUV-sensor-module-source.patch"
SRC_URI_append_som8064-revB = " file://0004-enable-yuv-preview-snapshot-dump.patch"
SRC_URI_append_som8064-revB = " file://0006-lib-3a-legacy.patch"
SRC_URI_append_som8064-revB = " file://0007-stereo-sensor-imx111.patch"
SRC_URI_append_som8064-revB = " file://0008-stereo-sensor-imx111-csiparams.patch"
SRC_URI_append_som8064-revB = " file://0009-stereo-3d-mode.patch"
SRC_URI_append_som8064-revB = " file://0010-stereo-applib.patch"


SRC_URI_append_som8064-const = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064-const = " file://0002-som8064-baseline-to-linux-platform.patch"
SRC_URI_append_som8064-const = " file://0005-1-customer-YUV-sensor-module-AndroidMakefile.patch"
SRC_URI_append_som8064-const = " file://0005-2-customer-YUV-sensor-module-Makefile.patch"
SRC_URI_append_som8064-const = " file://0005-3-customer-YUV-sensor-module-source.patch"
SRC_URI_append_som8064-const = " file://0004-enable-yuv-preview-snapshot-dump.patch"
SRC_URI_append_som8064-const = " file://0006-lib-3a-legacy.patch"
SRC_URI_append_som8064-const = " file://0007-stereo-sensor-imx111.patch"
SRC_URI_append_som8064-const = " file://0008-stereo-sensor-imx111-csiparams.patch"
SRC_URI_append_som8064-const = " file://0009-stereo-3d-mode.patch"
SRC_URI_append_som8064-const = " file://0010-stereo-applib.patch"
SRC_URI_append_som8064-const = " file://0011-stereo-aec-ev-api.patch"
SRC_URI_append_som8064-const = " file://0012-stereo-manual-exposure-control-api.patch"
SRC_URI_append_som8064-const = " file://0013-stereo-resolution-api.patch"
SRC_URI_append_som8064-const = " file://0014-stereo-autofocus-mode-api.patch"
SRC_URI_append_som8064-const = " file://0015-stereo-resolution-generic-dimensions-api.patch"
SRC_URI_append_som8064-const = " file://0016-stereo-auto-exposure-on-off-api.patch"
SRC_URI_append_som8064-const = " file://0017-stereo-camera-bykugan-wideeye-selction-api.patch"
SRC_URI_append_som8064-const = " file://0018-stereo-cam-mode-3d.patch"

SRC_URI_append_ifc6410 = " file://0001-ifc6410-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_ifc6410 = " file://0002-ifc6410-baseline-to-linux-platform.patch"
SRC_URI_append_ifc6410 = " file://0005-1-customer-YUV-sensor-module-AndroidMakefile.patch"
SRC_URI_append_ifc6410 = " file://0005-2-customer-YUV-sensor-module-Makefile.patch"
SRC_URI_append_ifc6410 = " file://0005-3-customer-YUV-sensor-module-source.patch"

SRC_URI_append = " file://mm-qcamera.conf"

PACKAGES = "${PN}"

inherit autotools qti-proprietary-binary

DEPENDS += "virtual/kernel"
DEPENDS += "glib-2.0"
#DEPENDS += "sensors-headers"
#DEPENDS += "qmi-framework"
DEPENDS += "adreno200-prebuilt"
DEPENDS += "camera-hal"
DEPENDS += "mm-camera-lib-prebuilt"
#DEPENDS += "mm-camera-core"

CAMERA_TARGET= "msm8960"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"
EXTRA_OECONF_append = " --with-mm-still=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --with-common-includes=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --host=${HOST_SYS}"
EXTRA_OECONF_append = " --enable-target=${CAMERA_TARGET}"
EXTRA_OECONF_append = " --with-extra-cflags=-I${STAGING_INCDIR}/mm-camera-lib/tintless"

FILES_${PN} += "\
    /usr/lib/* \
    /usr/bin/* \
    /lib/firmware/*.fw "

# The mm-camera package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "textrel"
INSANE_SKIP_${PN} += "already-stripped"
INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN} += "staticdev"

INITSCRIPT_NAME = "mm-qcamera"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

do_fetch_append() {
    import shutil
    import os
    mach = d.getVar('MACHINE', True)
    dirToUse = ""
    src = ""
    if mach.find('som8064') != -1:
        dirToUse = "som8064"
    else:
        dirToUse = mach

    src = d.getVar('COREBASE', True)+'/../'+dirToUse+'/mm-camera'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
   install -d ${D}/data
   install -m 0644 ${WORKDIR}/mm-qcamera.conf -D ${D}/data
   install -d ${D}/usr/lib
   install -m 755 ${S}/server/frameproc/face_proc/engine/libmmcamera_faceproc.so ${D}/usr/lib
   install -d ${D}/usr/bin
   
   install -m 755 ${S}/apps/v4l2-qcamera-app/.libs/v4l2-qcamera-app ${D}/usr/bin
   install -m 755 ${S}/apps/appslib/.libs/mm-qcamera-daemon ${D}/usr/bin

   install -m 0644 ${WORKDIR}/mm-qcamera.conf -D ${D}${sysconfdir}/init/mm-qcamera.conf
   install -m 0644 ${S}/server/hardware/sensor/cust_sens/cust_sens_params.conf ${D}/etc
}

pkg_postinst_${PN}_append() {
   install -m 0777 -d /data
}

pkg_prerm_mmcamera() {
   stop mm-qcamera
   echo "Stopped mm-qcamera if necessary"
}
