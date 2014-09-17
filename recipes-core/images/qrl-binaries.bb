DESCRIPTION = "This recipe includes the binary packages needed for a qrl system."
AUTHOR = "Gene W. Marsh <gmarsh@qti.qualcomm.com>"

LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
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
    configdb \
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
"


inherit image_types

do_copy_packages() {
  pkgList="libglib-2.0-0_2.38.2-r0 libz1 libgcc-s1 libconfigdb0 libdsutils1 diag mp-decision qmi qmi-framework thermal libxml0 reboot2fastboot hci-qcomm-init mm-camera-lib-prebuilt mm-camera mm-still libcamera0"

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

# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"

do_rootfs[dirs] = "${TOPDIR} ${WORKDIR}"
do_rootfs[lockfiles] += "${IMAGE_ROOTFS}.lock"
do_rootfs[cleandirs] += "${S} ${IMAGE_ROOTFS}"
do_rootfs[deptask] += "do_package_write_deb"
do_rootfs[rdeptask] += "do_package_write_deb"

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

addtask copy_packages after do_build

