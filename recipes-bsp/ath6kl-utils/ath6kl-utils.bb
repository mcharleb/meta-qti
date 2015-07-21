DESCRIPTION = "ath6kl utilities"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "glib-2.0 libnl diag"

PV = "1.0"
PR = "r0"

SRC_URI += "file://qrl-mac-fw-inc.sh \
    file://qrl-wlan-test-mode.sh"

PACKAGES = "${PN}"

inherit autotools
inherit qti-proprietary-binary

FILES_${PN} += "/usr/bin/* \
    /usr/local/qr-linux/*sh"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

INSANE_SKIP_${PN} = "installed-vs-shipped"

EXTRA_OECONF += "--with-glib"
CFLAGS_append = " -I${STAGING_INCDIR}/libnl3"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../ath6kl-utils'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
    dest=/usr/local/qr-linux
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/qrl-mac-fw-inc.sh ${D}${dest}
    install -m 755 ${WORKDIR}/qrl-wlan-test-mode.sh ${D}${dest}
}
