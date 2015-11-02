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
    mm-still \
    mm-video-firmware-prebuilt \
    mm-video \
    ftmdaemon \
    remote-debug-agent \
    perf-tools-prebuilt \
    adreno200-prebuilt \
    math-cl \
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
    mm-still \
    mm-camera-core-prebuilt \
    mm-camera-lib-prebuilt \
    mm-video \
    ftmdaemon \
    qrl-version \
    remote-debug-agent \
    perf-tools-prebuilt \
    adreno200-prebuilt \
    math-cl \
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
    if [ -d ${STAGING_DIR}/${MACHINE}/lib/firmware ]; then
        # Create stub firmware folders
        echo "qcom-firmware 0 0 755" >> $file_config
        firmware_folder=$target_folder/QCOM-FIRMWARE
        install -d ${firmware_folder}
        # Firmware folder ${target_folder}
        cp -r ${STAGING_DIR}/${MACHINE}/lib/firmware/* ${firmware_folder}
        # Append to the filesystem config
        cd $firmware_folder
        find * -exec stat --format="qcom-firmware/%n 0 0 %a" "{}" \; >> $file_config
        cd -
    fi

    if [ -d ${STAGING_DIR}/${MACHINE}/linaro-rootfs ]; then
        # Create stub userdata folder
        echo "linaro-rootfs 0 0 755" >> $file_config
        linaro_rootfs_folder=${target_folder}/LINARO-ROOTFS
        install -d ${linaro_rootfs_folder}
        # Copy the Linaro userdata contents
        cp -r ${STAGING_DIR}/${MACHINE}/linaro-rootfs/* ${linaro_rootfs_folder}
        # Create placeholders in empty folders
        cd ${linaro_rootfs_folder}
        empty_folders=`find * -depth -empty -type d`
        for f in $empty_folders; do
            touch $f/.empty
            echo "linaro-rootfs/$f 0 0 755" >> $file_config
            echo "linaro-rootfs/$f/.empty 0 0 644" >> $file_config
        done
        cd -
        # Append to the filesystem config
        cat ${STAGING_DIR}/${MACHINE}/filesystem_config.txt >> $file_config
    fi
}

create_release_structure() {
    # Create LINUX and sub-folder in the directory structure as required for build metabuild
    mkdir -p ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/

    # Copy all images in the LINUX folder in the directory structure as required to build metabuild
    cp ${DEPLOY_DIR_IMAGE}/out/*.img ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
    cp ${DEPLOY_DIR_IMAGE}/out/emmc_appsboot.mbn ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
    cp ${DEPLOY_DIR_IMAGE}/out/lk ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
}

copy_packages_append() {
    copy_packages_aux prop ${PKGLIST_PROP}
}

do_image_append() {
    create_cache_image
    create_release_structure
}

do_update_package_append() {
    cp ${DEPLOY_DIR_IMAGE}/out/*.zip ${DEPLOY_DIR_IMAGE}/out/LINUX/android/out/target/product/msm8974/
}

FASTBOOT_PATH = "${COREBASE}/meta-qti/recipes-core/images"

do_image_append() {
    cp ${FASTBOOT_PATH}/fastboot* ${DEPLOY_DIR_IMAGE}/out
}
