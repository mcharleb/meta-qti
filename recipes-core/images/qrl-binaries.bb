DESCRIPTION = "This recipe includes the binary packages needed for a qrl system."
AUTHOR = "Gene W. Marsh <gmarsh@qti.qualcomm.com>"

LICENSE = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

IMAGE_FSTYPES = "ext4"
IMAGE_LINGUAS = " "
IMAGE_ROOTFS_SIZE = "128"

DEPENDS += " \
	kernel-module-wlan \
	e2fsprogs-native \
 	configdb \
	dsutils \
	diag \
	mp-decision \
	qmi \
	qmi-framework \
	thermal \
	xmllib \
	reboot2fastboot \
	btnvtool \
	hci-qcomm-init \
"

EXTRA_IMAGECMD_ext4 += "-O ^has_journal -i 8192"

inherit image_types

copy_packages() {
  if [ -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
    install -m 644 ${DEPLOY_DIR}/persist/${MACHINE}/* ${IMAGE_ROOTFS}
  fi
  mkdir -p ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/libconfigdb*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/libdsutils*armhf.deb  ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/diag*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/mp-decision*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/qmi*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/qmi-framework*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/thermal*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/libxml*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/reboot2fastboot*armhf.deb ${IMAGE_ROOTFS}/deb
  install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/hci-qcomm-init*armhf.deb ${IMAGE_ROOTFS}/deb
}

IMAGE_PREPROCESS_COMMAND = "copy_packages"

copy_image() {
  install -d ${DEPLOY_DIR_IMAGE}/out
  cp ${DEPLOY_DIR_IMAGE}/${PN}-${MACHINE}.ext4 ${DEPLOY_DIR_IMAGE}/out/cache.img
}

IMAGE_POSTPROCESS_COMMAND = "copy_image"

# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"

do_rootfs[dirs] = "${TOPDIR} ${WORKDIR}"
do_rootfs[lockfiles] += "${IMAGE_ROOTFS}.lock"
do_rootfs[cleandirs] += "${S} ${IMAGE_ROOTFS}"
do_rootfs[deptask] += "do_package_write_deb"
do_rootfs[rdeptask] += "do_package_write_deb"

# Must call real_do_rootfs() from inside here, rather than as a separate
# task, so that we have a single fakeroot context for the whole process.
do_rootfs[umask] = "022"

fakeroot do_rootfs() {
        if [ -e ${IMAGE_ROOTFS} ]; then
            rm -rf ${IMAGE_ROOTFS}
        fi
        install -d ${IMAGE_ROOTFS}

	echo "Foo.."

        # Create the image directory
        install -d ${DEPLOY_DIR_IMAGE}

	echo "Bar..."

        ${IMAGE_PREPROCESS_COMMAND}

        ${@get_imagecmds(d)}

        ${IMAGE_POSTPROCESS_COMMAND}

        ${MACHINE_POSTPROCESS_COMMAND}

}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

addtask rootfs before do_build

