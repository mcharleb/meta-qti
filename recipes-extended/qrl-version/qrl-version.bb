DESCRIPTION = "Versioning information"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV = "1.0"

PROVIDES = "${PN}"
# This recipe creates a version file and installs it on-target
PACKAGES = "${PN}"
FILES_${PN} = "/etc/qrl-version"

do_install() {
    dest=/etc
    install -d ${D}${dest}
    install -m 644 ${COREBASE}/.qrlBuildVersion  ${D}${dest}/qrl-version
}

