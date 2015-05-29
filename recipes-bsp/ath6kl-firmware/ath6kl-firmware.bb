DESCRIPTION = "ATH6KL (QCA6234) WLAN Firmware"
LICENSE = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

PV = "1.0"
PR = "r0"

SRC_URI = "file://qrl-mac-fw-inc.sh"

PACKAGES = "${PN}"

FILES_${PN} += " \
    /usr/local/qr-linux/*sh \
    "

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_fetch_append() {
    import shutil
    import os
    src = d.getVar('COREBASE', True)+'/../ath6kl-utils/ath6kl_fw/AR6004/hw3.0'
    s = d.getVar('S', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.copytree(src, s)
}

do_install_append() {
   install -d ${D}/lib/firmware/ath6k/AR6004/hw3.0/
   install -m 0644 ${S}/bdata.bin_sdio ${D}/lib/firmware/ath6k/AR6004/hw3.0/bdata.bin
   install -m 0644 ${S}/fw.ram.bin ${D}/lib/firmware/ath6k/AR6004/hw3.0/fw.ram.bin
   install -m 0644 ${S}/otp.bin ${D}/lib/firmware/ath6k/AR6004/hw3.0/otp.bin
   install -m 0644 ${S}/utf.bin ${D}/lib/firmware/ath6k/AR6004/hw3.0/utf.bin
   dest=/usr/local/qr-linux
   install -d ${D}${dest}
   install -m 644 ${WORKDIR}/qrl-mac-fw-inc.sh ${D}${dest}
}
