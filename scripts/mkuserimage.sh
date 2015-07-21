#!/bin/bash
###############################################################################
## Copyright (c) 2015 Qualcomm Technologies, Inc.
## All Rights Reserved.
## Confidential and Proprietary - Qualcomm Technologies, Inc.
###############################################################################

if [ "$1" = "" ]; then
    echo "Error: board name not provided"
    echo "Usage: $0 BOARD_NAME (e.g.: $0 eagle8074)"
    exit 1
fi

BOARD_NAME="$1"

LINARO_IMG_NAME="linaro-trusty-developer-ifc6410-20140922-27"
MOUNT_PATH="/tmp/linaro-rootfs"
STAGING_BINDIR_NATIVE="tmp-eglibc/sysroots/x86_64-linux/usr/bin"
DEPLOY_DIR_IMAGE="tmp-eglibc/deploy/images/${BOARD_NAME}"

rm -rf ${MOUNT_PATH}

source ./oe-init-build-env build

# Download rootfs and files to $MOUNT_PATH
MACHINE=${BOARD_NAME} bitbake -f -c image linaro-rootfs

# Mount and modify the rootfs
sudo mount -o loop ${MOUNT_PATH}/${LINARO_IMG_NAME}.ext4.img ${MOUNT_PATH}/rootfs
sudo cp -R ${MOUNT_PATH}/copy/* ${MOUNT_PATH}/rootfs/

# Create userdata.img
mkdir -p ${DEPLOY_DIR_IMAGE}/out
sudo ${STAGING_BINDIR_NATIVE}/make_ext4fs -s -l 8G \
        ${DEPLOY_DIR_IMAGE}/out/userdata.img ${MOUNT_PATH}/rootfs

# Clean up
sudo umount ${MOUNT_PATH}/rootfs
rm -rf ${MOUNT_PATH}