inherit autotools 
   
DESCRIPTION = "GPS startup script" 
SECTION = "base" 
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary" 
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de" 
 
PV = "1.0" 
PR = "r0" 

SRC_URI += "file://gps.conf" 
   
PACKAGES = "${PN}" 
   
INITSCRIPT_NAME = "gps" 
INSANE_SKIP_${PN} = "installed-vs-shipped" 
do_install() { 
   dest=/etc/init 
   install -m 0644 ${WORKDIR}/gps.conf -D ${D}${dest}/gps.conf 
} 

