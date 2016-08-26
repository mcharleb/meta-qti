inherit qti-proprietary-prebuilt

SUMMARY = "Hexagon RPC daemon"
SECTION = "core"

SRC_URI += "file://${QTI_PREBUILT_DIR}/adsprpc.tar.gz"

S = "${WORKDIR}/adsprpc"

PR = "r0"
PV = "1.0"

PROVIDES = "adsprpc"
RPROVIDES_${PN} = "adsprpc"

FILES_${PN} += "/etc/init/adsprpcd.conf"
FILES_${PN} += "/usr/lib/*.so"

# packages created automatically by bitbake based on what 'make install' installs
# just had to modify makefile to install the necessary headers into ${includedir}
# bitbake package creation controlled by PACKAGES var, pkg contents controlled by
# FILES_<pkgname> vars, all default values for these vars OK

