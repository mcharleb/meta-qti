inherit qti-proprietary-prebuilt

DESCRIPTION = "Remote debug agent"

SRC_URI += "file://${QTI_PREBUILT_DIR}/remote-debug-agent.tar.gz;subdir=remote-debug-agent"

S = "${WORKDIR}/remote-debug-agent"

PR = "r0"
