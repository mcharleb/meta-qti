DESCRIPTION = "Create a meta-package to install all required binary deliverables."
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-internal/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"
PR = "r0"

inherit packagegroup qti-proprietary-binary

PROVIDES = "${PACKAGES}"
PACKAGES = "packagegroup-qrl-binaries"

RDEPENDS_packagegroup-qrl-binaries = " \
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
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
