DESCRIPTION = "Video encoder applications"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PACKAGES = "${PN}"

PV = "1.0"
PR = "r0"

DEPENDS += "virtual/kernel mm-video-oss camera-hal"
DEPENDS += "fpv-streamer-lib"

inherit autotools

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include \
                CPPFLAGS='-I${STAGING_INCDIR}/camera-hal'"

INSANE_SKIP_${PN} += "installed-vs-shipped"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../mm-video'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}
