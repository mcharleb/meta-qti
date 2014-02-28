inherit autotools

DESCRIPTION = "Qualcomm Data Modules (Excluding ConfigDB and DSUtils)"
HOMEPAGE = "http://support.cdmatech.com"
LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"
DEPENDS = "diag dsutils glib-2.0 qmi qmi-framework xmllib virtual/kernel"

DEPENDS_append_9615-cdp += "mcm-core"

PR = "r16"


#re-use non-perf settings
BASEMACHINE = "${@d.getVar('MACHINE', True).replace('-perf', '')}"
EXTRA_OECONF = "--with-lib-path=${STAGING_LIBDIR} \
                --with-common-includes=${STAGING_INCDIR} \
                --with-stderr \
		--with-glib \
                CFLAGS=-I${STAGING_KERNEL_DIR}/include \
		QMI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/qmi \
		DSUTILS_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/dsutils \
		GLIB_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/glib-2.0 \
		XMLLIB_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/xmllib \
		DIAG_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/diag \
		"

SRC_URI = "git://git.quicinc.com/platform/vendor/qcom-proprietary/ship/data;protocol=git;tag=AU_LINUX_ANDROID_JB_2.5.6.04.02.02.093.144"
SRC_URI += "file://data-init"

FILES_${PN}-dbg += "/tmp/tests/.debug"
FILES_${PN}-dbg_append_mdm9625 += "/WEBSERVER/www/cgi-bin/.debug"
FILES_${PN} += "/tmp"
FILES_${PN}_append_mdm9625 += "/WEBSERVER/*"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

pkg_postinst_data () {
        [ -n "$D" ] && OPT="-r $D" || OPT="-s"
        update-rc.d $OPT -f netmgrd remove
        update-rc.d $OPT netmgrd start 45 2 3 4 5 . stop 80 0 1 6 .

        update-rc.d $OPT -f data-init remove
        update-rc.d $OPT data-init start 97 2 3 4 5 . stop 15 0 1 6 .

        update-rc.d $OPT -f start_QCMAP_ConnectionManager_le remove
        update-rc.d $OPT start_QCMAP_ConnectionManager_le start 60 2 3 4 5 . stop 40 0 1 6 .

        update-rc.d $OPT -f start_qti_le remove
        update-rc.d $OPT start_qti_le start 90 2 3 4 5 . stop 10 0 1 6 .

        update-rc.d $OPT -f start_MCM_MOBILEAP_ConnectionManager_le remove
        update-rc.d $OPT start_MCM_MOBILEAP_ConnectionManager_le start 60 2 3 4 5 . stop 40 0 1 6 .

        update-rc.d $OPT -f start_mcm_data_srv_le remove
        update-rc.d $OPT start_mcm_data_srv_le start 52 2 3 4 5 . stop 85 0 1 6 .
}
pkg_postinst_append_mdm9625 () {
        update-rc.d $OPT -f start_QCMAP_Web_CLIENT_le remove
        update-rc.d $OPT start_QCMAP_Web_CLIENT_le start 92 2 3 4 5 . stop 10 0 1 6 .
}

do_install_append() {
        install -m 0755 ${WORKDIR}/data/netmgr/src/start_netmgrd_le -D ${D}${sysconfdir}/init.d/netmgrd
        install -m 0755 ${WORKDIR}/data/netmgr/src/udhcpc.script -D ${D}${sysconfdir}/udhcpc.d/udhcpc.script
        install -m 0755 ${WORKDIR}/data-init -D ${D}${sysconfdir}/init.d/data-init
}

sysroot_stage_all_append() {
    sysroot_stage_dir ${D}/tmp ${SYSROOT_DESTDIR}${STAGING_DIR_HOST}/tmp
}
