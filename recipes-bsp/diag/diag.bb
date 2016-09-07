inherit useradd qti-proprietary-prebuilt

DESCRIPTION = "Library and routing applications for diagnostic traffic"
HOMEPAGE    = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/diag.tar.gz;subdir=diag"

S = "${WORKDIR}/diag"

PV = "1.0"
PR = "r1"

RDEPENDS_${PN} = "glib-2.0"

# Must add new users and groups
# Create user and group named diag. And give id 53 same as in the executable diag_mdlog
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-g 53 diag" 
USERADD_PARAM_${PN} = "-u 53 -g diag diag; -G diag linaro"

do_install_append() {
    chown -R diag:diag ${D}/var/log/diag_logs
}
