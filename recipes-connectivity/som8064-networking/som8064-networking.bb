DESCRIPTION = "Networking configuration for som8064"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV = "1.0"

SRC_URI = "file://qrl-mac-fw-inc.sh"
SRC_URI += "file://wpa_supplicant.conf"

PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/qr-linux/*sh"
FILES_${PN} += "/etc/*"

do_install() {
    dest=/usr/local/qr-linux
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/qrl-mac-fw-inc.sh ${D}${dest}
    dest=/etc/wpa_supplicant
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/wpa_supplicant.conf ${D}${dest}
}
