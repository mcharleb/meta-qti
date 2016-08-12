inherit autotools pkgconfig

DESCRIPTION = "Adreno libraries, firmware and headers"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"
INSANE_SKIP_${PN} = "installed-vs-shipped"

DEPENDS += "glib-2.0"

FILES_${PN} = "/usr/lib/*"

do_install() {
    prebuilt_src=${COREBASE}/../prebuilt_HY11/target/${MACHINE}/adreno200
    install -d ${D}/usr/include
    cp -r ${prebuilt_src}/usr/include/* ${D}/usr/include/
    install -d ${D}/lib/firmware
    cp -r ${prebuilt_src}/lib/firmware/* ${D}/lib/firmware/
    install -d ${D}/usr/lib
    cp -r ${prebuilt_src}/usr/lib/* ${D}/usr/lib/
}
