DEPENDS += " \
    reboot2fastboot \
    diag \
    mp-decision \
    qmi \
    qmi-framework \
    thermal-engine \
    ath6kl-firmware \
    ath6kl-utils \
    q6-admin \
    adsprpc \
    ss-restart \
    mm-camera \
    mm-video-firmware-prebuilt \
    mm-video \
    ftmdaemon \
    gps \
    fpv-streamer-app \
"

PKGLIST_PROP = " \
    libconfigdb0 \
    libdsutils1 \
    libxml0 \
    reboot2fastboot \
    diag \
    mp-decision \
    qmi \
    qmi-framework \
    thermal-engine \
    ath6kl-utils \
    q6-admin \
    adsprpc \
    ss-restart \
    mm-camera \
    mm-camera-core-prebuilt \
    mm-camera-lib-prebuilt \
    mm-video \
    ftmdaemon \
    gps \
    qrl-version \
    libqc-fpv-streamer0 \
    fpv-streamer-app \
"

QRL_BINARIES_FW_LOCATION = "${STAGING_DIR}/${MACHINE}/lib/firmware"
PARTITION_CACHE_SIZE = "32M"

# The cache image contains the firmware files needed by Linaro's Ubuntu rootfs.
# It expects QRL_BINARIES_FW_LOCATION to contain the filesystem from which
# the image is built and placed in the out dir.
create_cache_image() {
    ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_CACHE_SIZE} \
        -L qcom-firmware ${DEPLOY_DIR_IMAGE}/out/cache.img \
        ${QRL_BINARIES_FW_LOCATION}
}

# target_folder and file_config are provided by base function
target_files_extension_append() {
    firmware_folder="$target_folder/QCOM-FIRMWARE"
    # Firmware folder ${target_folder}
    if [ -d ${STAGING_DIR}/${MACHINE}/lib/firmware ]; then
        install -d ${firmware_folder}
        cp -r ${STAGING_DIR}/${MACHINE}/lib/firmware/* ${firmware_folder}
    fi
    # Append to the filesystem config
    echo "qcom-firmware 0 0 755" >> $file_config
    for f in `find $firmware_folder/*`; do
        echo qcom-firmware/${f/$firmware_folder\//} 0 0 `stat --format=%a $f` >> $file_config
    done
}

create_release_structure() {
    # Create LINUX and sub-folder in the directory structure as required for build metabuild
    mkdir -p ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/

    # Copy all images in the LINUX folder in the directory structure as required to build metabuild
    cp ${DEPLOY_DIR_IMAGE}/out/*.img ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
    cp ${DEPLOY_DIR_IMAGE}/out/emmc_appsboot.mbn ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
    cp ${DEPLOY_DIR_IMAGE}/out/lk ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
    #cp ${DEPLOY_DIR_IMAGE}/out/vmlinux ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
}

copy_packages_append() {
    copy_packages_aux prop ${PKGLIST_PROP}
}

do_image_append() {
    create_cache_image
    create_release_structure
}

FASTBOOT_PATH = "${COREBASE}/meta-qti/recipes-core/images"

do_image_append() {
    cp ${FASTBOOT_PATH}/fastboot* ${DEPLOY_DIR_IMAGE}/out
}
