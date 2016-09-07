inherit qti-proprietary-prebuilt

DESCRIPTION = "ATH6KL (QCA6234) WLAN Firmware"

PV = "1.0"
PR = "r0"

SRC_URI += "file://${QTI_PREBUILT_DIR}/ath6kl-firmware.tar.gz;subdir=ath6kl-firmware"

S = "${WORKDIR}/ath6kl-firmware"

