DESCRIPTION = "Application to stream encoded video"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

SRC_URI  = "file://0001-qcamvid-application-for-camera-and-video-encoder-int.patch"
SRC_URI += "file://0002-qcamvid-add-error-notification-for-camera-failures.patch"

#SRC_URI += "file://work.patch"

PACKAGES = "${PN} ${PN}-dbg"

PV = "1.0"
PR = "r0"

DEPENDS  = "mm-venc-omx-test"
DEPENDS += "camera-hal"

inherit autotools qti-proprietary-binary

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"

#INSANE_SKIP_${PN} += "installed-vs-shipped"
