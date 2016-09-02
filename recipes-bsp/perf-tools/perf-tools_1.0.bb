inherit autotools pkgconfig qti-proprietary-prebuilt

DESCRIPTION = "Android performance core libraries prebuilt"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI += "file://${QTI_PREBUILT_DIR}/perf-tools.tar.gz;subdir=perf-tools"

S = "${WORKDIR}/perf-tools"

do_populate_sysroot() {
    install -d ${STAGING_LIBDIR}
    install -m 0644 ${D}/usr/lib/*.so ${STAGING_LIBDIR}
}
