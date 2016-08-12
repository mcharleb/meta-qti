inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "LPASS/ADSP RPC library"
SECTION = "base"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PR = "r0"
PV = "1.0"
SRC_URI_append = " file://adsprpcd.conf"

# Express dependency on kernel headers and pass header path to configure
DEPENDS += "virtual/kernel"
EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_DIR_TARGET}/usr/src/${MACHINE}/include"
PACKAGES = "${PN}"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "dev-so"
FILES_${PN} += "/etc/init/adsprpcd.conf"
FILES_${PN} += "/usr/lib/*.so"

# packages created automatically by bitbake based on what 'make install' installs
# just had to modify makefile to install the necessary headers into ${includedir}
# bitbake package creation controlled by PACKAGES var, pkg contents controlled by
# FILES_<pkgname> vars, all default values for these vars OK

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../adsprpc'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
    dest=/etc/init
    install -d ${D}${dest}
    install -m 0644 ${WORKDIR}/adsprpcd.conf ${D}${dest}
}
