inherit autotools qti-proprietary-binary

DESCRIPTION = "fpv Streamer app."
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
#LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-intermal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV="1.0"
PN = "fpv-streamer-app"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
	    file://fpv.cfg \
	    file://fpv.conf \
	    file://fpv.override \
            file://src \
            "

PACKAGES = "${PN}"

#OPEN_SOURCE_LIB = "/prj/atlanticus/software/fpv-deps"
DEPENDS += "fpv-streamer-lib"

do_install_append() {
   install -d ${D}/usr/bin
   install -m 755 ${S}/fpv-streamer-app ${D}/usr/bin

   dest=/etc/init
   install -d ${D}${dest}
   install -m 755 ${WORKDIR}/fpv.conf ${D}${dest}
   install -m 755 ${WORKDIR}/fpv.override ${D}${dest}

   dest=/etc
   install -d ${D}${dest}
   install -m 755 ${WORKDIR}/fpv.cfg ${D}${dest}

#   dest=/usr/lib
#   install -d ${D}${dest}
#   cp  ${OPEN_SOURCE_LIB}/* ${D}/usr/lib

} 

do_unpack_append() {
    import shutil
    import os
    
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    slib=d.getVar('OPEN_SOURCE_LIB',True);
    dstdir=d.getVar('STAGING_DIR_TARGET',True);
    
    if not os.path.exists(s):
        os.makedirs(s)
    shutil.copy(wd+'/configure.ac', s)
    shutil.copy(wd+'/Makefile.am', s)
    shutil.copy(wd+'/fpv.conf', s)
    shutil.copy(wd+'/fpv.override', s)
    shutil.copy(wd+'/fpv.cfg', s)
    if os.path.exists(s+'/src'):
       shutil.rmtree(s+'/src')
    shutil.copytree(wd+'/src', s+'/src')
    
#    shutil.copy(slib+'/libliveMedia.so',dstdir+'/usr/lib')
#    shutil.copy(slib+'/libgroupsock.so',dstdir+'/usr/lib')
#    shutil.copy(slib+'/libBasicUsageEnvironment.so',dstdir+'/usr/lib')
#    shutil.copy(slib+'/libUsageEnvironment.so',dstdir+'/usr/lib')
#    shutil.copy(slib+'/libturbojpeg.so',dstdir+'/usr/lib')
#    if os.path.exists(s+'/libs'):
#       shutil.rmtree(s+'/libs')
#    shutil.copytree(slib,s+'/libs')

}
       
do_configure_prepend() {

}  
                     
do_configure_append () {
  
#    cp ${OPEN_SOURCE_LIB}/*  ${STAGING_DIR_TARGET}/usr/lib

}
