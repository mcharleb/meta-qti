inherit qti-proprietary-prebuilt

DESCRIPTION = "fastmmi"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/fastmmi.tar.gz;subdir=fastmmi"

S = "${WORKDIR}/fastmmi"

PV = "1.0"
PR = "r0"

RDEPENDS_${PN} += "frameworks-av libxml2 zlib glib-2.0 diag camera-hal sdk-add-on"

INSANE_SKIP_${PN} = "dev-deps"
