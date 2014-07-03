inherit autotools

DESCRIPTION = "c2d headers for mm-camera"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"
#FILES_${PN} +=  "/usr/lib/*"
INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install() {
	prebuilt_src=${COREBASE}/../prebuilt_HY11/target/${MACHINE}/adreno200
	install -d ${D}/usr/include
	install -d ${D}/usr/include/C2D
	cp -r ${prebuilt_src}/include/C2D/* ${D}/usr/include/C2D/
}
