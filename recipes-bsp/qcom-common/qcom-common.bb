inherit qti-proprietary-prebuilt

DESCRIPTION = "Qualcomm common include files."

LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

SRC_URI += "file://${QTI_PREBUILT_DIR}/qcom-common.tar.gz;subdir=qcom-common"

S = "${WORKDIR}/qcom-common"

PROVIDES += "common"
PV = "1.0"
PR = "r0"

PROVIDES += "qcom-common-dev"

FILES_${PN} = ""
FILES_${PN}-dev = "/usr/include/*"
