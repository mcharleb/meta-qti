DESCRIPTION = "Library and routing applications for diagnostic traffic"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "common glib-2.0 android-tools"

PV = "1.0"
PR = "r7"

SRC_URI += "file://chgrp-diag"
PACKAGES = "${PN}"

EXTRA_OECONF += "--with-glib --with-common-includes=${STAGING_INCDIR}"


INITSCRIPT_NAME = "chgrp-diag"
INITSCRIPT_PARAMS = "start 15 2 3 4 5 ."

inherit autotools qr-update-rc.d qti-proprietary-binary repo-source

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
    install -m 0755 ${WORKDIR}/chgrp-diag -D ${D}${sysconfdir}/init.d/chgrp-diag
}
