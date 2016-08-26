DESCRIPTION = "ATH6KL (QCA6234) WLAN Firmware"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI = "file://${COREBASE}/../prebuilt_HY11/target/${MACHINE}/ath6kl-utils/ath6kl_fw/AR6004/hw3.0"

FILES_${PN} += "/lib/firmware/ath6k/*"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "installed-vs-shipped"

# This recipe is only for populating cache image, no deb package will be shipped
do_install_append() {
   install -d ${D}/lib/firmware/ath6k/AR6004/hw3.0/
   install -m 0644 ${S}/bdata.bin_sdio ${D}/lib/firmware/ath6k/AR6004/hw3.0/bdata.bin
   install -m 0644 ${S}/fw.ram.bin ${D}/lib/firmware/ath6k/AR6004/hw3.0/fw.ram.bin
   install -m 0644 ${S}/otp.bin ${D}/lib/firmware/ath6k/AR6004/hw3.0/otp.bin
   install -m 0644 ${S}/utf.bin ${D}/lib/firmware/ath6k/AR6004/hw3.0/utf.bin
}
