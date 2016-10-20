DESCRIPTION = "Linaro-rootfs for QR-Linux"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

DEPENDS += "libsparse-native ext4-utils-native"

PR = "r0"
PV = "1.0"

inherit native

LINARO_IMG_NAME = "linaro-trusty-developer-ifc6410-20140922-27"
MOUNT_PATH = "/tmp/linaro-rootfs"

SRC_URI = "https://releases.linaro.org/14.09/ubuntu/ifc6410/${LINARO_IMG_NAME}.img.gz \
    file://fstab \
    file://qrlConfig.conf \
    file://qrlNetwork.conf \
    file://interfaces \
    file://60-persistent-v4l.rules"
SRC_URI[sha256sum] = "5c617d8445ce60e168ff237291af79ce28e674fc11434763cdaaf3ab41faf8f5"
SRC_URI[md5sum] = "34990264f30f09d76c79c4da6b403538"

do_image() {
    mkdir -p ${MOUNT_PATH}/rootfs
    ${STAGING_BINDIR_NATIVE}/simg2img ${WORKDIR}/${LINARO_IMG_NAME}.img \
        ${MOUNT_PATH}/${LINARO_IMG_NAME}.ext4.img
    mkdir -p ${MOUNT_PATH}/copy/etc/init
    mkdir -p ${MOUNT_PATH}/copy/etc/network
    mkdir -p ${MOUNT_PATH}/copy/lib/udev/rules.d
    cp ${WORKDIR}/fstab ${MOUNT_PATH}/copy/etc/fstab
    cp ${WORKDIR}/qrlConfig.conf ${MOUNT_PATH}/copy/etc/init/qrlConfig.conf
    cp ${WORKDIR}/qrlNetwork.conf ${MOUNT_PATH}/copy/etc/init/qrlNetwork.conf
    cp ${WORKDIR}/interfaces ${MOUNT_PATH}/copy/etc/network/interfaces
    cp ${WORKDIR}/60-persistent-v4l.rules ${MOUNT_PATH}/copy/lib/udev/rules.d/60-persistent-v4l.rules
}

do_image[depends] = "libsparse-native:do_populate_sysroot"
do_image[depends] += "ext4-utils-native:do_populate_sysroot"

addtask image after do_unpack
