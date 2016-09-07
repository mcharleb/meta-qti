inherit autotools pkgconfig qti-proprietary-prebuilt

DESCRIPTION = "Android performance core libraries prebuilt"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

LIBV = "1"

SRC_URI += "file://${QTI_PREBUILT_DIR}/perf-tools.tar.gz;subdir=perf-tools"

S = "${WORKDIR}/perf-tools"

PROVIDES += "perf-tools-dev"
RPROVIDES_${PN} = "perf-tools"

RDEPENDS_${PN} = "glib-2.0"

FILES_${PN} = "/usr/lib/*.so.1"
FILES_${PN}-dev = "/usr/lib/*.so"
FILES_${PN}-dev += "/usr/include/*"

# The .so files should be symlinks to the library so version (i.e. libx.so.1)
do_install_append() {
    for f in `ls ${D}/usr/lib/*.so`; do mv $f $f.1; ln -sf `basename $f.1` $f; done
}

do_populate_sysroot() {
    install -d ${STAGING_LIBDIR}
    install -m 0644 ${D}/usr/lib/*.so ${STAGING_LIBDIR}
}
