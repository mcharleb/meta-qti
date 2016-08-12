inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "MP Decision library for MSM/QSD"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI += "file://mpdecision.conf"

PACKAGES = "${PN}"

# Must build in src dir
B = "${S}"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

DEPENDS += "perf-tools-prebuilt"

EXTRA_OECONF_append = " --enable-target-msm8974=yes"
#EXTRA_OECONF_append = " --with-dlog"

FILES_${PN} += "\
    /usr/lib/* \
    /usr/bin/*"

INITSCRIPT_NAME = "mpdecision"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../mp-decision'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
       install -m 0755 ${WORKDIR}/mpdecision.conf -D ${D}${sysconfdir}/init/mpdecision.conf
}

# The mpdecision package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"

pkg_prerm_mp-decision() {
   stop mpdecision
   echo "Stopped mpdecision if necessary"
}

