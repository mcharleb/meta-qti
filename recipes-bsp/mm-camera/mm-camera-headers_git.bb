DESCRIPTION = "MM Camera headers for MSM/QSD"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI_append_som8064 = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064 = " file://0002-som8064-baseline-to-linux-platform.patch"
SRC_URI_append_som8064-revB = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064-revB = " file://0002-som8064-baseline-to-linux-platform.patch"
SRC_URI_append_som8064-const = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064-const = " file://0002-som8064-baseline-to-linux-platform.patch"

SRC_URI_append_ifc6410 = " file://0001-ifc6410-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_ifc6410 = " file://0002-ifc6410-baseline-to-linux-platform.patch"

SRC_URI_append = " file://0004-enable-yuv-preview-snapshot-dump.patch"

PACKAGES = "${PN}"

inherit autotools

CAMERA_TARGET= "msm8960"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"
EXTRA_OECONF_append = " --with-mm-still=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --with-common-includes=${STAGING_INCDIR}"
EXTRA_OECONF_append = " --host=${HOST_SYS}"
EXTRA_OECONF_append = " --enable-target=${CAMERA_TARGET}"
EXTRA_OECONF_append = " --with-extra-cflags=-I${STAGING_INCDIR}/mm-camera-lib/tintless"
#EXTRA_OECONF_append = " --enable-debug=yes"

FILES_${PN} += "\
    /usr/lib/* \
    /usr/bin/* \
    /lib/firmware/*.fw "

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"

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

do_configure() {
}

do_compile() {
}

do_install() {
   install -d ${D}/usr/include
   install -d ${D}/usr/include/mm-camera
   # Copy all headers
   rsync -av --include '*.h' --include '*/' --exclude '*' ${S}/. ${D}/usr/include/mm-camera/.
}

