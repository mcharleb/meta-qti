DESCRIPTION = "Networking configuration for som8064"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

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
