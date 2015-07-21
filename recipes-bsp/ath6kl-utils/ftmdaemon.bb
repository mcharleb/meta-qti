DESCRIPTION = "ftmdaemon"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "glib-2.0 libnl diag ath6kl-utils"

PV = "1.0"
PR = "r0"

SRC_URI += ""

PACKAGES = "${PN}"

inherit autotools
inherit qti-proprietary-binary

FILES_${PN} += "/usr/bin/*"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

EXTRA_OECONF += "--with-glib --enable-wlan --enable-debug"

CFLAGS_append = " -I${STAGING_INCDIR}/diag -I${STAGING_INCDIR}/ath6kl-utils \
    `pkg-config --cflags glib-2.0`"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../ftm'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
