inherit qti-proprietary-prebuilt

DESCRIPTION = "MP Decision library for MSM/QSD"
HOMEPAGE         = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mp-decision.tar.gz;subdir=mp-decision"

S = "${WORKDIR}/mp-decision"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} = "perf-tools"

INSANE_SKIP_${PN} = "dev-deps"

INITSCRIPT_NAME = "mpdecision"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

pkg_prerm_mp-decision() {
   stop mpdecision
   echo "Stopped mpdecision if necessary"
}

