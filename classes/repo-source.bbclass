SRC_DIR ?= "${COREBASE}/../${PN}"

do_fetch() {
  cp -r ${SRC_DIR} ${WORKDIR}/git
}
