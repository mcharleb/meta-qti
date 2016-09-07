inherit qti-proprietary-prebuilt

DESCRIPTION = "math-cl tests OpenCL fast-math"

PR = "r0"
PV="1.0"

RDEPENDS_${PN} += "adreno200 \
	    "

#INSANE_SKIP_${PN} = "dev-deps"

SRC_URI += "file://${QTI_PREBUILT_DIR}/math-cl.tar.gz;subdir=math-cl"

S = "${WORKDIR}/math-cl"

