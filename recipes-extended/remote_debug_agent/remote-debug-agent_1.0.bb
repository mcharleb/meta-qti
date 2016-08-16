inherit autotools

DESCRIPTION = "Remote debug agent"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/remote_debug_agent"
B = "${S}"

SRC_URI  = "file://remote_debug_agent/"
SRC_URI += "file://0001-ATL-4106-Autotooling-for-remote_debug_agent.patch"

PACKAGES = "${PN}"

PR = "r0"

INSANE_SKIP_${PN} += "installed-vs-shipped"
