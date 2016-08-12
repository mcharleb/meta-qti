inherit autotools pkgconfig

DESCRIPTION = "mm-camera-core prebuilt libraries"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"
PN = "mm-camera-core-prebuilt"

DEBIAN_NOAUTONAME_${PN} = "1"

PACKAGES = "${PN}"
FILES_${PN} +=  "/usr/lib/*"
INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install() {
	prebuilt_src=${COREBASE}/../prebuilt_HY11/target/${MACHINE}/mm-camera-core

  install -d ${D}/usr
  install -d ${D}/usr/lib
  install ${prebuilt_src}/lib/*  ${D}/usr/lib/
}
