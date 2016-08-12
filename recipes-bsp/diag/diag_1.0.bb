inherit autotools pkgconfig qti-proprietary-binary

DESCRIPTION = "Library and routing applications for diagnostic traffic"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "common glib-2.0 android-tools"

PV = "1.0"
PR = "r1"

SRC_URI += "file://diag.conf"
SRC_URI += "file://diag_mdlog.conf"
SRC_URI += "file://diag.override"
SRC_URI += "file://diag_mask.cfg"
SRC_URI += "file://diag_mdlog-logrotate.conf"
SRC_URI += "file://diag_mdlog-logrotate-cron"

# Must be built in place
B = "${S}"

PACKAGES = "${PN}"
FILES_${PN} += "/etc/init/diag.conf"
FILES_${PN} += "/etc/init/diag_mdlog.conf"
FILES_${PN} += "/etc/diag_mask.cfg"
FILES_${PN} += "/etc/diag_mdlog-logrotate.conf"
FILES_${PN} += "/etc/cron.d/diag_mdlog-logrotate-cron"

EXTRA_OECONF += "--with-glib --with-common-includes=${STAGING_INCDIR}"

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
    cp ${S}/include/diaglogi.h ${STAGING_INCDIR}/diag
    dest=/etc/init
    install -d ${D}${dest}
    install -m 0644 ${WORKDIR}/diag.conf -D ${D}${dest}
    install -m 0644 ${WORKDIR}/diag_mdlog.conf -D ${D}${dest}

    dest=/etc/cron.d
    install -d ${D}${dest}
    install -m 0644 ${WORKDIR}/diag_mdlog-logrotate-cron -D ${D}${dest}

    dest=/etc
    install -d ${D}${dest}
    install -m 0755 ${WORKDIR}/diag_mask.cfg -D ${D}${dest}
    install -m 0755 ${WORKDIR}/diag_mdlog-logrotate.conf -D ${D}${dest}
}

#diag_mdlog executable downgrades its permission to diag user and group.
#So create user and group named diag. And give id 53 same as in the executable diag_mdlog
pkg_postinst_${PN}_append() {
    groupadd -g 53 diag
    useradd -u 53 -g diag diag
    usermod -a -G diag linaro
    mkdir -p /var/log/diag_logs
    chown -R diag:diag /var/log/diag_logs
    chmod -R 755 /var/log/diag_logs
    chown root:root /etc/diag_mdlog-logrotate.conf
    chmod 644 /etc/diag_mdlog-logrotate.conf
}
