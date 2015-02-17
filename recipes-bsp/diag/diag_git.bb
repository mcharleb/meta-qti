DESCRIPTION = "Library and routing applications for diagnostic traffic"
HOMEPAGE         = "http://support.cdmatech.com"
LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "common glib-2.0 android-tools"

PV = "1.0"
PR = "r0"

SRC_URI += "file://0001-Fix-include-for-ptt_socket_app.patch"
SRC_URI += "file://0002-Add-missing-header-to-Makefile.am.patch"
SRC_URI += "file://diag.conf"
SRC_URI += "file://diag.override"
SRC_URI += "file://001-diag-mdlog-in-txt.patch"
SRC_URI += "file://002-diag_mdlog-can-print-timestamp-to-text-file.patch"
SRC_URI += "file://003-do-not-print-warning-log-packet-dropped.patch"
SRC_URI += "file://004-fix-missing-qxdm-packets.patch"
SRC_URI += "file://005-log-roate-change.patch"
SRC_URI += "file://006-change-permissions-while-creating-log-files.patch"
SRC_URI += "file://diag_mask.cfg"
SRC_URI += "file://diag_mdlog-logrotate.conf"
SRC_URI += "file://diag_mdlog-logrotate-cron"

PACKAGES = "${PN}"
FILES_${PN} += "/etc/init/diag.conf"
FILES_${PN} += "/etc/init/*override"
FILES_${PN} += "/root/diag_mask.cfg"
FILES_${PN} += "/root/diag_mdlog-logrotate.conf"
FILES_${PN} += "/root/diag_mdlog-logrotate-cron"

EXTRA_OECONF += "--with-glib --with-common-includes=${STAGING_INCDIR}"


inherit autotools qti-proprietary-binary

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
    #install -m 0644 ${WORKDIR}/diag.override -D ${D}${dest} /* enable diag_mdlog by default if this line is commented out*/
    
    dest=/etc/cron.d
    install -d ${D}${dest}
    install -m 0644 ${WORKDIR}/diag_mdlog-logrotate-cron -D ${D}${dest}

    dest=/root/
    install -d ${D}${dest}    
    install -m 0755 ${WORKDIR}/diag_mask.cfg -D ${D}${dest}
    install -m 0755 ${WORKDIR}/diag_mdlog-logrotate.conf -D ${D}${dest}
}

#diag_mdlog executable downgrades its permission to diag user and group. 
#So create user and group named diag. And give id 53 same as in the executable diag_mdlog
#diag_mdlog writes log to default location /sdcard/diag_logs. And has some bugs will using -o paramters  
#Hence create /sdcard/diag_logs and give right permission to diag user
#Copy default mask to /sdcard/diag_logs/ location. Used to run diag_mdlog at startup
pkg_postinst_${PN}_append() {
    groupadd -g 53 diag
    useradd -u 53 -g diag diag
    usermod -a -G diag linaro
    
    mkdir -p /sdcard/diag_logs            
    mv /root/diag_mask.cfg /sdcard/diag_logs/

    mkdir -p /sdcard/diag_logs/rotate
    
    mkdir -p /sdcard/diag_logs/conf
    mv /root/diag_mdlog-logrotate.conf /sdcard/diag_logs/conf
         
    chown -R diag:diag /sdcard/diag_logs
    chmod -R 755 /sdcard/diag_logs
    
    chown root:root /sdcard/diag_logs/conf/diag_mdlog-logrotate.conf
    chmod 644 /sdcard/diag_logs/conf/diag_mdlog-logrotate.conf    
    
}
