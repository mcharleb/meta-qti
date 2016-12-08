LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

QTI_PREBUILT_DIR = "${COREBASE}/../prebuilt_${MACHINE}/${PREBUILT_VERSION}/"

inherit bin_package

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

S = "${WORKDIR}/${PN}-${PV}"

INSANE_SKIP_${PN} = "build-deps"
