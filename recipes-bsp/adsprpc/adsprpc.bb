inherit qti-proprietary-prebuilt

SUMMARY = "Hexagon RPC daemon"
SECTION = "core"

SRC_URI += "file://${QTI_PREBUILT_DIR}/adsprpc.tar.gz;subdir=adsprpc"

S = "${WORKDIR}/adsprpc"

PR = "r0"
PV = "1.0"

FILES_${PN}-dev = "/usr/include/*"
FILES_${PN}-dev += "/usr/lib/*.la"
#FILES_${PN}-dev += "/usr/lib/*.so"

RPROVIDES_${PN} += "libadsprpc.so libadsprpc.so(ADSPRPC) libmdsprpc.so libmdsprpc.so(MDSPRPC)"

#do_install_append() {
#    install -d ${D}${libdir}/adsprpc
#    install ${S}/${libdir}/libadsprpc.so.0.0.0 ${D}${libdir}/adsprpc/libadsprpc.so
#}

INSANE_SKIP_${PN} = "dev-so"
