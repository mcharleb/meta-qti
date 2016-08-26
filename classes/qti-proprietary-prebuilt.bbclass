LICENSE          = "QUALCOMM-TECHNOLOGY-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

QTI_PREBUILT_DIR = "${COREBASE}/../prebuilt_HY11/target/${MACHINE}/"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

S = "${WORKDIR}/${PN}-${PV}"

inherit autotools

PACKAGES = "${PN}"

FILES_${PN} = "/*"

do_install() {
    pushd .
    cd ${S}
    for f in `find .`; do
        if [ -d $f ]; then 
            install -d ${D}/$f
        else
            install $f ${D}/$f
        fi
    done
    popd
}
