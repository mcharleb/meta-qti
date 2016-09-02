inherit qti-proprietary-prebuilt

DESCRIPTION = "Remote debug agent"

SRC_URI += "file://${QTI_PREBUILT_DIR}/remote-debug-agent.tar.gz"

S = "${WORKDIR}/remote-debug-agent"

PACKAGES = "${PN}"

PR = "r0"

PROVIDES = "remote-debug-agent"
RPROVIDES_${PN} = "remote-debug-agent"
