inherit qti-proprietary-prebuilt

DESCRIPTION = "ATH6KL (QCA6234) WLAN Firmware"

PV = "1.0"
PR = "r0"

SRC_URI = "file://${COREBASE}/../prebuilt_HY11/target/${MACHINE}/ath6kl-utils/ath6kl_fw/AR6004/hw3.0"

FILES_${PN} += "/lib/firmware/ath6k/*"

