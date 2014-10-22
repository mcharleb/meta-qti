DESCRIPTION = "This recipe includes the binary packages needed for a qrl system."
AUTHOR = "Gene W. Marsh <gmarsh@qti.qualcomm.com>"

LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r1"


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
	mm-camera \
	mm-still \
	mm-camera-lib-prebuilt \
	camera-hal \
	qrl-networking \
	ifc-6410-networking \
"

inherit base

copy_packages() {
  pkgList="libglib-2.0-0_2.38.2-r0 libz1 libgcc-s1 libconfigdb0 libdsutils1 diag mp-decision qmi qmi-framework thermal libxml0 reboot2fastboot hci-qcomm-init mm-camera-lib-prebuilt mm-camera mm-still libcamera0 ifc6410-networking qrl-networking android-tools"

  if [ -e ${IMAGE_ROOTFS} ]; then
    rm -rf ${IMAGE_ROOTFS}
  fi
  mkdir -p ${IMAGE_ROOTFS}

  if [ -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
    install -m 644 ${DEPLOY_DIR}/persist/${MACHINE}/* ${IMAGE_ROOTFS}
  fi
  mkdir -p ${IMAGE_ROOTFS}/deb
  for pkg in ${pkgList}
  do
    install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/${pkg}_*armhf.deb ${IMAGE_ROOTFS}/deb
  done
  cd ${IMAGE_ROOTFS}/deb
  tar zcf qrlPackages.tgz *
  cp qrlPackages.tgz ${DEPLOY_DIR_IMAGE}/out
  echo "[INFO] Copied qrlPackages.tgz to ${DEPLOY_DIR_IMAGE}/out"
}

do_copy_packages() {
   copy_packages
}

do_image() {
IMAGE_FSTYPES="ext4"
ROOTFS_SIZE="64000"
EXTRA_IMAGECMD_ext4="-O ^has_journal -i 8192"
  copy_packages
#  ${IMAGE_CMD_ext4}
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_build[noexec] = "0"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

addtask image after do_build
addtask copy_packages after do_build
