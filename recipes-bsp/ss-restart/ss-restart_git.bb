inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "Subsystem Restart utilities"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "glib-2.0 android-tools"

PV = "1.0"
PR = "r0"

SRC_URI += "file://ssr.conf"

PACKAGES = "${PN}"

FILES_${PN} += "/usr/bin/*"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

EXTRA_OECONF += "--with-glib --with-common-includes=${STAGING_INCDIR}"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../ss-restart'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
       install -m 0755 ${WORKDIR}/ssr.conf -D ${D}${sysconfdir}/init/ssr.conf
}
