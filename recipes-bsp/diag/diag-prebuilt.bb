inherit qti-proprietary-prebuilt

DESCRIPTION = "Library and routing applications for diagnostic traffic"
HOMEPAGE    = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/diag.tar.gz"

S = "${WORKDIR}/diag"

PV = "1.0"
PR = "r1"

RDEPENDS_${PN} = "glib-2.0"

PROVIDES = "diag"
RPROVIDES_${PN} = "diag"

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
