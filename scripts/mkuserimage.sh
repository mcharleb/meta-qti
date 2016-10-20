#!/bin/bash
###############################################################################
## This script builds a linux root filesystem image.
## 
## Creating a rootfs image necessarily requires root privilege. This script uses "sudo" to execute certain commands
## as root. The user running this script should have sudo access, and will be prompted to enter their password at
## when the script runs.
## The following commands in this script are run with sudo:
##   - /bin/mount
##   - /bin/cp
##   - /bin/umount
##   - /bin/chown
##   - /usr/bin/find
##   - /usr/bin/stat
##   - /tmp/make_ext4fs
##
## The last one, /tmp/make_ext4fs is the make_ext4fs copied by this script, from where it was compiled
## in the source tree. This copying is needed for easier automation  (see below)
##
## Automation:
## sudo requires a password to be entered at run time, which means this script requires user intervention. To
## automate this, the sudo permission can be added with NOPASSWD option for the above commands (notice that
## copying make_ext4fs to /tmp makes it independent of the path where the source tree was built, so you don't
## need to keep changing the sudo permission for every new source tree).
## E.g.
##   To give allow user "johndoe", on machine  "builder", add the following line to /etc/sudoers
##   johndoe builder = NOPASSWD: /bin/mount, /bin/cp, /bin/umount, /tmp/make_ext4fs
##
##
## Copyright (c) 2015 Qualcomm Technologies, Inc.
## All Rights Reserved.
## Confidential and Proprietary - Qualcomm Technologies, Inc.
###############################################################################

if [ "$1" = "" ]; then
    echo "Error: board name not provided"
    echo "Usage: $0 BOARD_NAME (e.g.: $0 eagle8074)"
    exit 1
fi

cat <<EOF

[INFO] This script uses "sudo".
Please enter your password (for sudo) when prompted. The following commands are run under sudo:
- /bin/mount
- /bin/cp
- /bin/umount
- /bin/chown
- /usr/bin/find
- /usr/bin/stat
- /tmp/make_ext4fs
- rm
EOF

BOARD_NAME="$1"

LINARO_IMG_NAME="linaro-trusty-developer-ifc6410-20140922-27"
MOUNT_PATH="/tmp/linaro-rootfs"
STAGING_BINDIR_NATIVE="tmp-eglibc/sysroots/x86_64-linux/usr/bin"
STAGING_LIBDIR_NATIVE="tmp-eglibc/sysroots/x86_64-linux/usr/lib"
STAGING_DIR="tmp-eglibc/sysroots/${BOARD_NAME}"
DEPLOY_DIR_IMAGE="tmp-eglibc/deploy/images/${BOARD_NAME}"
MAKE_EXT4FS_DIR="/tmp"

# Need to chdir to oe-core
cd ${WS}/oe-core

if  /bin/mount  | grep ${MOUNT_PATH}/rootfs; then
   sudo /bin/umount ${MOUNT_PATH}/rootfs || {
      echo "[ERROR] Could not unmount already mounted ${MOUNT_PATH}/rootfs: $?"
      exit 1
   }
fi

mkdir -p ${MOUNT_PATH} || {
   echo "[ERROR] Could not create path ${MOUNT_PATH}"
   exit 1
}

echo "[INFO] Fetching toolchain"
./meta-qr-linux/scripts/linaro-fetch.sh

source ./oe-init-build-env build

echo "[INFO] Downloading Linaro rootfs"

# Download rootfs and files to $MOUNT_PATH
MACHINE=${BOARD_NAME} bitbake -f -c image linaro-rootfs
set -x
echo "[INFO] Patching the rootfs"

# Mount and modify the rootfs
sudo /bin/mount -o loop ${MOUNT_PATH}/${LINARO_IMG_NAME}.ext4.img ${MOUNT_PATH}/rootfs || {
   echo "[ERROR] Could not mount image on loop device: $?"
   exit 1
}
sudo /bin/cp -R ${MOUNT_PATH}/copy/* ${MOUNT_PATH}/rootfs/ || {
   echo "[ERROR] Could not copy: $?"
   exit 1
}

echo "[INFO] Packaging the modified rootfs"

# Create userdata.img
mk_ext="${MAKE_EXT4FS_DIR}/make_ext4fs"
if [[ -e  ${mk_ext} ]] 
then
   rm ${mk_ext} || {
      echo "[ERROR] Could not clean ${mk_ext}: $?"
      exit 1
   }
fi
mk_ext_executable=`readlink -f ${STAGING_BINDIR_NATIVE}/make_ext4fs`
# An actual copy is needed, not a symlink, since sudo won't work with symlink
/bin/cp ${mk_ext_executable} ${mk_ext} || {
   echo "[ERROR] Could not copy from ${STAGING_BINDIR_NATIVE}/make_ext4fs to ${mk_ext}: $?"
   exit 1
}

mkdir -p ${DEPLOY_DIR_IMAGE}/out
sudo LD_LIBRARY_PATH=${STAGING_LIBDIR_NATIVE} ${mk_ext} -s -l 8G ${DEPLOY_DIR_IMAGE}/out/userdata.img ${MOUNT_PATH}/rootfs || {
   echo "[ERROR] Could not create file system image: $?"
   exit 1
}

mkdir -p ${STAGING_DIR}

# Create the list of files (required by update scripts)
echo "[INFO] Creating the list of files in the rootfs"
set +x
file_config=${PWD}/${STAGING_DIR}/filesystem_config.txt
rm -f $file_config
cd ${MOUNT_PATH}/rootfs
sudo /usr/bin/find * -exec /usr/bin/stat --format="linaro-rootfs/%n 0 0 %a" "{}" \; >> $file_config
cd -
set -x

# Copy the rootfs to the staging dir (needed for factory reset)
echo "[INFO] Copying the rootfs to the staging dir"
rootfs_copy=${STAGING_DIR}/linaro-rootfs
if [ -d $rootfs_copy ]; then
    rm -rf $rootfs_copy
fi
sudo /bin/cp -R ${MOUNT_PATH}/rootfs $rootfs_copy

# Change owner for using this in the build process
sudo /bin/chown -R ${USER} $rootfs_copy

# Clean up
sudo umount ${MOUNT_PATH}/rootfs || {
   echo "[WARNING] Could not unmount file system: $?"
}
sudo rm -rf ${MOUNT_PATH}
