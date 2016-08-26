inherit qti-proprietary-prebuilt

DESCRIPTION = "MM Camera libraries for MSM"
SECTION = "base"

PV = "1.0"
PR = "r0"

SRC_URI += "file://${QTI_PREBUILT_DIR}/mm-camera.tar.gz"

S = "${WORKDIR}/mm-camera"

PACKAGES = "${PN} ${PN}-firmware"
PROVIDES = "mm-camera mm-camera-firmware"

RPROVIDES_${PN} = "mm-camera"
RPROVIDES_${PN}-firmware = "mm-camera-firmware"

FILES_${PN}-firmware = "/lib/firmware/*"

INITSCRIPT_NAME = "mm-qcamera"

pkg_prerm_mmcamera() {
   stop mm-qcamera
   echo "Stopped mm-qcamera if necessary"
}
