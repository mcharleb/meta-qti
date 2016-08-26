inherit qti-proprietary-prebuilt

DESCRIPTION = "ath6kl utilities"
HOMEPAGE = "http://support.cdmatech.com"

SRC_URI += "file://${QTI_PREBUILT_DIR}/ath6kl-utils.tar.gz"

S = "${WORKDIR}/ath6kl-utils"

PV = "1.0"
PR = "r0"

PROVIDES = "ath6kl-utils"
RPROVIDES_${PN} = "ath6kl-utils"

RDEPENDS_${PN} = "libnl-route libnl glib-2.0 libnl-nf libnl-genl"
