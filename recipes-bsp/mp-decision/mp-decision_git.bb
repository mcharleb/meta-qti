DESCRIPTION = "MP Decision library for MSM/QSD"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r6"
SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/mp-decision;protocol=git"
SRC_URI += "file://0001-Assignment-of-O2-changed-to-to-remove-warnings.patch"
PACKAGES = "${PN}"
SRCREV_som8064 = "AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"
SRCREV_liquid8064 = "AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.189"
SRCREV_ifc6410 = "AU_LINUX_BASE_HORSESHOE_TARGET_ALL.04.00.116"

inherit autotools

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
   bbnote "Doing do_install: ${WORKDIR} ${D} ${S} ${IMAGE_ROOTFS}"
}

# The mpdecision package contains symlinks that trip up insane
INSANE_SKIP_${PN} = "dev-so"
