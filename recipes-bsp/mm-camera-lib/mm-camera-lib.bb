inherit qti-proprietary-prebuilt

DESCRIPTION = "mm-camera-lib prebuilt libraries"
SECTION = "base"

SRC_URI = "file://${QTI_PREBUILT_DIR}/mm-camera-lib.tar.gz;subdir=mm-camera-lib"

S = "${WORKDIR}/mm-camera-lib"

PV = "1.0"
PR = "r0"

DEBIAN_NOAUTONAME_${PN} = "1" 

INSANE_SKIP_${PN} = "arch"

# FIXME - links to build filesystem
INSANE_SKIP_${PN} = "symlink-to-sysroot"
