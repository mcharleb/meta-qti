DESCRIPTION = "MP Decision library for MSM/QSD"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r6"
SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/mp-decision;protocol=git"
SRC_URI += "file://0001-Assignment-of-O2-changed-to-to-remove-warnings.patch"
SRC_URI += "file://0001-Add-init-script-start_mpdecision.patch"

PACKAGES = "${PN}"
SRCREV = "AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

inherit autotools

INITSCRIPT_NAME = "mpdecision"
INITSCRIPT_PARAMS = "start 40 2 3 4 5 . stop 80 0 1 6 ."

inherit qr-update-rc.d

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install_append() {
       install -m 0755 ${S}/start_mpdecision -D ${D}${sysconfdir}/init.d/mpdecision
}

# The mpdecision package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"
