inherit autotools qti-proprietary-binary

DESCRIPTION = "FPV Streamer Lib."
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
#LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV="1.0"
PN = "fpv-streamer-lib"

PROVIDES = "fpv-streamer-lib"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
	    file://src \
	    file://inc \
            "


PACKAGES = "${PN}"

DEPENDS = "camera-hal live555 libjpeg-turbo"
#DEPENDS += "live555"
#DEPENDS += "libjpeg-turbo"

CXXFLAGS += "-I ${STAGING_INCDIR}/live555"
CFLAGS += "-I ${STAGING_INCDIR}/live555"

do_compile_prepend() {
#    cp ./live/* ./inc
#    cp ./busage/* ./inc
#    cp ./usage/* ./inc
#    cp ./groupsock/* ./inc
}

do_install_append() {
   install -d ${D}/usr/lib
   install -m 755 ${S}/.libs/libqc_fpv_streamer.so* ${D}/usr/lib
  
   install -d ${D}/usr/include
   install -m 0644 ${S}/inc/* ${D}/usr/include
   
   rm -rf ${D}/usr/src/debug/fpv_streamer-lib
} 

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if not os.path.exists(s):
        os.makedirs(s)
    shutil.copy(wd+'/configure.ac', s)
    shutil.copy(wd+'/Makefile.am', s)
    if os.path.exists(s+'/src'):
       shutil.rmtree(s+'/src',ignore_errors=True)
    shutil.copytree(wd+'/src', s+'/src', symlinks=True)
    if os.path.exists(s+'/inc'):
       shutil.rmtree(s+'/inc',ignore_errors=True)
    shutil.copytree(wd+'/inc', s+'/inc', symlinks=True)

}

do_configure_prepend() {


}

