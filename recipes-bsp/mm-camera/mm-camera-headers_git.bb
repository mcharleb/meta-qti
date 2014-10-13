DESCRIPTION = "MM Camera headers for MSM/QSD"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.1"
PR = "r16"

SRC_URI_append_som8064 = " file://0001-som8064-makefile-configure-scripts-for-linux-build.patch"
SRC_URI_append_som8064 = " file://0002-som8064-baseline-to-linux-platform.patch"

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

# The mm-camera package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"

#do_eztune_patch() {
#	if [ -a ${S}/server/core/eztune/eztune_vfe_diagnostics.h ]
#	then
#		rm ${S}/server/core/eztune/eztune_vfe_diagnostics.h
#	fi
#}

#do_patch_append(){
#	bb.build.exec_func('do_eztune_patch',d)
#}

do_fetch_append() {
    import shutil
    import os    
    src = d.getVar('COREBASE', True)+'/../'+d.getVar('MACHINE', True)+'/mm-camera'
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

