DESCRIPTION = "Library and routing applications for diagnostic traffic"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "common glib-2.0 android-tools"

PV = "1.0"
PR = "r0"

SRC_URI += "file://chgrp-diag"
PACKAGES = "${PN}"

EXTRA_OECONF += "--with-glib --with-common-includes=${STAGING_INCDIR}"

INITSCRIPT_NAME = "chgrp-diag"
INITSCRIPT_PARAMS = "start 15 2 3 4 5 ."

inherit autotools qr-update-rc.d qti-proprietary-binary

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../diag'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

# We know there's a bunch of installed files that we don't ship
# so ignore that check
INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install_append() {
    install -m 0755 ${WORKDIR}/chgrp-diag -D ${D}${sysconfdir}/init.d/chgrp-diag
}
