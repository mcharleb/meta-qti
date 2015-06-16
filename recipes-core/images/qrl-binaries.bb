DESCRIPTION = "This recipe includes the binary packages needed for a qrl system."
AUTHOR = "Gene W. Marsh <gmarsh@qti.qualcomm.com>"

LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r1"

QRL_BINARIES_FW_LOCATION = "${STAGING_DIR}/${MACHINE}/lib/firmware"
QRL_BINARIES_TOOLS_LOCATION = "${STAGING_BINDIR_NATIVE}"
BLUR_META_PKG_LOCATION = "/prj/atlanticus/software/blur"
DEPENDS_append_ifc6410 = "ifc6410-networking"
DEPENDS_append_som8064 = "som8064-networking"
DEPENDS_append_som8064-revB = "som8064-networking"
DEPENDS_append_som8064-const = "som8064-networking"

# APQ8074 partition sizes
PARTITION_PERSIST_SIZE = "32M"
PARTITION_SYSTEM_SIZE = "1G"
PARTITION_CACHE_SIZE = "32M"

DEPENDS += " \
   android-tools \
   reboot2fastboot \
   virtual/kernel \
   diag \
   mp-decision \
   qmi \
   qmi-framework \
   thermal-engine \
   compat-wireless-${MACHINE} \
   ath6kl-firmware \
   q6-admin \
   flight-dsp-image \
   adsprpc \
   qrl-networking \
   ss-restart \
   camera-hal \
   mm-camera \
   ntpdate-internal \
   hostapd  \
   libnetfilter-conntrack3 \
   libmnl0 \
   libnfnetlink0 \
   dnsmasq-base \
   dnsmasq \
   setup-softap \
   lk \
   mm-video-firmware-prebuilt \
   mm-video-oss \
   mm-venc-omx-test \
   "

OLD_DEPENDS += " \
   kernel-module-wlan \
   e2fsprogs-native \
   configdb \
   dsutils \
   diag \
   mp-decision \
   qmi \
   qmi-framework \
   thermal-engine \
   xmllib \
   reboot2fastboot \
   btnvtool \
   hci-qcomm-init \
   mm-camera \
   mm-still \
   mm-camera-lib-prebuilt \
   camera-hal \
   qrl-networking \
   adsprpc \
   q6-admin \
   flight-dsp-image \
   paramgen \
   muorb \
   imu \
   libimufastrpc \
   pixhawk \
"

inherit base

copy_package() {
  pkg=$1
  dest=$2

  foundPkg=false
  # Non arch dependent os package check
  if [ -f ${DEPLOY_DIR}/deb/all/${pkg}_*all.deb ]; then
    foundPkg=true
    install -m 644 ${DEPLOY_DIR}/deb/all/${pkg}_*all.deb ${IMAGE_ROOTFS}/${dest}
  fi

  # Arch dependent os package check
  if [ -f ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/${pkg}_*${DPKG_ARCH}.deb ]; then
    foundPkg=true
    install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/${pkg}_*${DPKG_ARCH}.deb ${IMAGE_ROOTFS}/${dest}
  fi

  # Nothing found at all
  if ( ! $foundPkg ); then
    bberror "${pkg} not found"
    exit 1;
  fi
}

copy_packages() {
  # tar up the necessary packages and put them in out
  # These files will be copied to the stock Ubuntu image,
  # extracted, and installed on-target

  if [ -e ${IMAGE_ROOTFS} ]; then
    rm -rf ${IMAGE_ROOTFS}
  fi
  mkdir -p ${IMAGE_ROOTFS}

  if [ -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
     install -m 644 ${DEPLOY_DIR}/persist/${MACHINE}/* ${IMAGE_ROOTFS}
  fi

  # Open source packages
  old_pkgList_os="libglib-2.0-0_2.38.2-r0 \
              libz1 \
              libgcc-s1 \
              qrl-networking \ 
              android-tools \
              q6-admin"

  pkgList_os="libglib-2.0-0_2.38.2-r0 \
              libz1 \
              libgcc-s1 \
              android-tools \
              q6-admin \
              qrl-networking \
              hostapd \
              libcrypto1.0.0 \
              libnetfilter-conntrack3 \
              libmnl0 \
              libnfnetlink0 \
              dnsmasq-base \
              dnsmasq \
              setup-softap \
              mm-video-oss \
"

  if [ ${MACHINE} = "ifc6410" ]
  then
    old_pkgList_os="${pkgList_os} ifc6410-networking"
  else
    old_pkgList_os="${pkgList_os} som8064-networking"
  fi

  # Proprietary packages
  pkgList_prop=" libconfigdb0 \
                 libdsutils1 \
                reboot2fastboot \
                diag \
                mp-decision \
                qmi \
                qmi-framework \
                thermal-engine \
                libxml0 \
                flight-dsp-image \
                adsprpc \
                ss-restart \
                camera-hal \
                mm-camera \
                mm-camera-core-prebuilt \
                mm-camera-lib-prebuilt \
                ntpdate-internal \
                mm-venc-omx-test \
                ath6kl-firmware \
               "

  old_pkgList_prop="libconfigdb0 \
                libdsutils1 \
                diag \
		mp-decision \
		qmi \
		qmi-framework \
		thermal \
        thermal-engine \
		libxml0 \
		reboot2fastboot \
		hci-qcomm-init \
		mm-camera-lib-prebuilt \
		mm-camera \
		mm-still \
		libcamera0 \
		adsprpc \
		flight-dsp-image \
                paramgen \
                imu \
                muorb \
		libimufastrpc \                
                pixhawk \
"

  #
  # For each of them, copy the packages to a temp directory, and create
  # a tar file. Then copy this file to out
  #
  mkdir -p ${IMAGE_ROOTFS}/deb_os
  for pkg in ${pkgList_os}
  do
    copy_package ${pkg} deb_os
  done
  cd ${IMAGE_ROOTFS}/deb_os
  tar zcf qrlPackages_os.tgz *
  mv qrlPackages_os.tgz ${DEPLOY_DIR_IMAGE}/out

  mkdir -p ${IMAGE_ROOTFS}/deb_prop
  for pkg in ${pkgList_prop}
  do
    copy_package ${pkg} deb_prop
  done
  cd ${IMAGE_ROOTFS}/deb_prop
  tar zcf qrlPackages_prop.tgz *
  mv qrlPackages_prop.tgz ${DEPLOY_DIR_IMAGE}/out

  # Now create a tgz of the kernel modules
  cd ${DEPLOY_DIR}/deb/${TUNE_ARCH}
  tar zcf qrlPackages_kernel.tgz *.deb
  mv qrlPackages_kernel.tgz ${DEPLOY_DIR_IMAGE}/out
  echo "[INFO] Copied qrlPackages_{os,kernel,prop}.tgz to ${DEPLOY_DIR_IMAGE}/out"

}

# Cache image contains the firmware files needed by Linaro's Ubuntu rootfs.
# It expects QRL_BINARIES_FW_LOCATION to contain the filesystem from which
# the image is built and placed in the out dir.
create_cache_image() {
  ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_CACHE_SIZE} -L qcom-firmware ${DEPLOY_DIR_IMAGE}/out/cache.img ${QRL_BINARIES_FW_LOCATION}
}

# System image is created by take all the qrlPackage*tgz files from the out directory,
# untarring them in system_rootfs directory, and then creating an image out of it,
# placing it in the out dir.
# It contains all the QRL packages we need to install on a stock Ubuntu rootfs.
create_system_image() {
  mkdir -p ${IMAGE_ROOTFS}/system_rootfs/qrlPackages
  for tgz in `ls ${DEPLOY_DIR_IMAGE}/out/qrlPackage*tgz`
  do
    tar xzf $tgz -C ${IMAGE_ROOTFS}/system_rootfs/qrlPackages
  done

  # Add the Blur meta package
  #cp ${BLUR_META_PKG_LOCATION}/blur-meta-pkg_0.1_all.deb ${IMAGE_ROOTFS}/system_rootfs/qrlPackages

  ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_SYSTEM_SIZE} ${DEPLOY_DIR_IMAGE}/out/system.img ${IMAGE_ROOTFS}/system_rootfs
}

# Persist image contains NV items.
create_persist_image() {
  # If persist directory doesnt exist, we should create one for empty image file.
  if [ ! -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
    mkdir -p ${DEPLOY_DIR}/persist/${MACHINE}
    echo "" > ${DEPLOY_DIR}/persist/${MACHINE}/readme.txt
  fi

  ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_PERSIST_SIZE} ${DEPLOY_DIR_IMAGE}/out/persist.img ${DEPLOY_DIR}/persist/${MACHINE}
}

# Create the release structure required for metabuild
create_release_structure() {
  #Create LINUX and subfolder in the directory structure as required for build metabuild
  mkdir -p ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
  
  #Copy all images in the LINUX folder in the directory structure as required for build metabuild
  cp ${DEPLOY_DIR_IMAGE}/out/*.img ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
  cp ${DEPLOY_DIR_IMAGE}/out/emmc_appsboot.mbn ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
  
  cp ${DEPLOY_DIR_IMAGE}/out/lk ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
  cp ${DEPLOY_DIR_IMAGE}/out/vmlinux ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
}

do_copy_packages() {
   copy_packages
}

do_image() {
  copy_packages
  create_system_image
  create_cache_image
  create_persist_image
  cp ${THISDIR}/fastboot* ${DEPLOY_DIR_IMAGE}/out
  create_release_structure
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
do_image[depends] = "make-ext4fs-native:do_populate_sysroot"

addtask image after do_build
addtask copy_packages after do_build
