DESCRIPTION = "MM Camera libraries for MSM"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI += "file://mm-qcamera.conf"

PACKAGES = "${PN}"

inherit autotools qti-proprietary-binary

DEPENDS = "virtual/kernel"
DEPENDS += "glib-2.0"
DEPENDS += "camera-hal-headers"
DEPENDS += "sensors-headers"
DEPENDS += "qmi-framework"
DEPENDS += "adreno200-prebuilt"
DEPENDS += "mm-camera-core-prebuilt"
DEPENDS += "mm-camera-lib-prebuilt"
DEPENDS += "android-tools"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"
EXTRA_OECONF_append = " --with-mm-still=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --with-camera-hal-headers=${STAGING_INCDIR}/camera-hal"
EXTRA_OECONF_append = " --with-adreno200-headers=${STAGING_INCDIR}/adreno200"
EXTRA_OECONF_append = " --with-sensors-headers=${STAGING_INCDIR}/sensors"
EXTRA_OECONF_append = " --with-qmi-framework-headers=${STAGING_INCDIR}/qmi-framework"
EXTRA_OECONF_append = " --host=${HOST_SYS}"
EXTRA_OECONF_append = " --enable-target=msm8974"
EXTRA_OECONF_append = " --with-extra-cflags=-I${STAGING_INCDIR}/mm-camera-lib/tintless"

CFLAGS += "-I ${STAGING_INCDIR}/glib-2.0"
CFLAGS += "-I ${STAGING_LIBDIR}/glib-2.0/include"

LDFLAGS += "-lglib-2.0"

FILES_${PN} += "\
    /usr/lib/* \
    /usr/bin/*"

# The mm-camera package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "textrel"
INSANE_SKIP_${PN} += "already-stripped"
INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN} += "staticdev"

INITSCRIPT_NAME = "mm-qcamera"

do_fetch_append() {
    import shutil
    import os

    src = d.getVar('COREBASE', True)+'/../mm-camera'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
   install -m 0644 ${WORKDIR}/mm-qcamera.conf -D ${D}${sysconfdir}/init/mm-qcamera.conf
}

pkg_prerm_mmcamera() {
   stop mm-qcamera
   echo "Stopped mm-qcamera if necessary"
}
