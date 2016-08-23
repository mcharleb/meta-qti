DESCRIPTION = "mm-video (venus) firmware"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

FILES_${PN} += "/lib/firmware/*"

PACKAGES = "${PN}"

FILES_${PN} = "/lib/firmware/*"

do_install() {
    prebuilt_src=${COREBASE}/../prebuilt_HY11/target/${MACHINE}/mm-video
    install -d ${D}/lib/firmware
    cp -r ${prebuilt_src}/* ${D}/lib/firmware/
}
