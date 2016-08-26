inherit qti-proprietary-prebuilt

DESCRIPTION = "Adreno libraries, firmware and headers"
SECTION = "base"

SRC_URI += "file://${QTI_PREBUILT_DIR}/adreno200.tar.gz"

S = "${WORKDIR}/adreno200"

PV = "1.0"
PR = "r0"

PACKAGES += "${PN}-firmware "

RDEPENDS_${PN} = "zlib glib-2.0"

PROVIDES = "adreno200"
RPROVIDES_${PN} = "adreno200"

FILES_${PN} = "/usr/lib/*"
FILES_${PN} += "/usr/include/*"
FILES_${PN}-firmware = "/lib/firmware/*"
