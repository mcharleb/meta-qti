FILESEXTRAPATHS_append := "${THISDIR}:"

LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://LICENCE.atheros_firmware;md5=30a14c7823beedac9fa39c64fdd01a13"

SRC_URI += "file://files/PS_ASIC.pst"

PACKAGES = "${PN}-ar3k ${PN}-atheros-license"

RDEPENDS_${PN} = ""
 
do_install() {
    install -d ${D}/lib/firmware/ar3k/1020201
    install -m 0644 ar3k/1020201/RamPatch.txt    ${D}/${base_libdir}/firmware/ar3k/1020201/RamPatch.txt
    install -m 0644 ${WORKDIR}/files/PS_ASIC.pst ${D}/${base_libdir}/firmware/ar3k/1020201/PS_ASIC.pst
    install -m 0644 ${S}/LICENCE.atheros_firmware ${D}/${base_libdir}/firmware/LICENCE.atheros_firmware
}
