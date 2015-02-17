DESCRIPTION = "Q6 Admin Utilities"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r1"
PV = "1.0"

PROVIDES = "q6-admin"

SRC_URI  = "file://q6-admin.sh"
SRC_URI += "file://q6.conf"


PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/qr-linux/*sh"
FILES_${PN} += "/etc/init/*conf"

do_install() {
    dest=/usr/local/qr-linux
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/q6-admin.sh ${D}${dest}
    dest=/etc/init
    install -d ${D}${dest}
    install -m 0644 ${WORKDIR}/q6.conf -D ${D}${dest}
}
