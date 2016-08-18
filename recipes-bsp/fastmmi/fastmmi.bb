inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "fastmmi"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "glib-2.0 diag libxml2 camera-hal"

RDEPENDS_${PN} += "sdk-add-on"

PV = "1.0"
PR = "r0"

SRC_URI += ""

PACKAGES = "${PN}"
FILES_${PN} += "/usr/lib/*.so"
FILES_${PN} += "/home/linaro/FTM_AP"

# Must be built in src dir
B = "${S}"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

INSANE_SKIP_${PN} = "installed-vs-shipped"

EXTRA_OECONF += "--with-glib"

CFLAGS_append = " -I${STAGING_INCDIR}/diag \
    `pkg-config --cflags glib-2.0`"

CXXFLAGS_append = " -I${STAGING_INCDIR}/libxml2 -I${STAGING_LIBDIR}/glib-2.0/include -I${STAGING_INCDIR}/diag \
    `pkg-config --cflags glib-2.0`"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../vendor/qcom/proprietary/fastmmi'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s, ignore=shutil.ignore_patterns('.git*'))
}

do_install_append() {
    dest=/etc/mmi
    install -d ${D}${dest}
    install -dm0664 ${D}/home/linaro/FTM_AP
    install -m 0644 ${S}/res/config/mmi-pcba-le.cfg -D ${D}${dest}/mmi.cfg
    install -m 0644 ${S}/res/values/path_config_le.xml -D ${D}${dest}/path_config.xml
    install -m 0554 ${S}/res/config/init -D ${D}${dest}
    install -m 0554 ${S}/res/config/start_mmi -D ${D}${dest}
    install -m 0644 ${S}/res/config/qti-sysd.conf -D ${D}${sysconfdir}/init/qti-sysd.conf
    install -d ${D}${dest}/layout
    install -m 0644 ${S}/res/layout/* -D ${D}${dest}/layout
    install -m 0644 ${S}/res/drawable/* -D ${D}${dest}
    install -m 0644 ${S}/res/values/* -D ${D}${dest}
    install -m 0644 ${S}/res/raw/* -D ${D}${dest}
}

