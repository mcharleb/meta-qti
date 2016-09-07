inherit qti-proprietary-prebuilt

DESCRIPTION = "Adreno libraries, firmware and headers"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/adreno200.tar.gz;subdir=adreno200"

S = "${WORKDIR}/adreno200"

PV = "1.0"
PR = "r0"

LIBV = "1"

PACKAGES += "${PN}-firmware"

RDEPENDS_${PN} = "zlib glib-2.0"

PROVIDES += "adreno200-firmware adreno200-dev"
RPROVIDES_${PN} = "adreno200"
RPROVIDES_${PN}-firmware = "adreno200-firmware"

# The .so files should be symlinks to the library so version (i.e. libx.so.1)
do_install_append() {
    for f in `ls ${D}/usr/lib/*.so`; do mv $f $f.1; ln -sf `basename $f.1` $f; done
}

FILES_${PN} = "/usr/lib/*.so.1"
FILES_${PN}-dev = "/usr/lib/*.so"
FILES_${PN}-dev += "/usr/include/*"
FILES_${PN}-firmware = "/lib/firmware/*"
