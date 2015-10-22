DESCRIPTION = "Android performance core libraries prebuilt"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"
FILES_${PN} +=  "/usr/lib/*"

inherit autotools

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install() {
    prebuilt_src=${COREBASE}/../prebuilt_HY11/target/${MACHINE}/perf-tools
    install -d ${D}/usr/lib
    install -m 0644 ${prebuilt_src}/usr/lib/*.so ${D}/usr/lib
}
