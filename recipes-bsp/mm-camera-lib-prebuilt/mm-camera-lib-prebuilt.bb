inherit autotools

DESCRIPTION = "mm-camera-lib prebuilt libraries"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"
FILES_${PN} +=  "/usr/lib/* \
                 /usr/include/*"

INSANE_SKIP_${PN} = "installed-vs-shipped"

DEBIAN_NOAUTONAME_${PN} = "1" 

do_install() {
	prebuilt_src=${COREBASE}/../prebuilt_HY11/target/${MACHINE}/mm-camera-lib

    install -d ${D}/usr
	install -d ${D}/usr/lib
	install -d ${D}/usr/include
	install -d ${D}/usr/include/mm-camera
	install  ${prebuilt_src}/include/tintless/*.h ${D}/usr/include/mm-camera/

	install -d ${D}/usr/lib	
    install ${prebuilt_src}/lib/*.so ${D}/usr/lib/ 
	
}

