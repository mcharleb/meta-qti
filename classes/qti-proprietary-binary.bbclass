LICENSE          = "QUALCOMM-Proprietary"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti/files/qcom-licenses/${LICENSE};md5=400dd647645553d955b1053bbbfcd2de"

# For non-opensource packages, we can't install them into the rootfs.
# We need to copy them into a pre-defined output directory where they
# will get picked up and packaged for release through regular channels
QTI_BINARY_PKG_LOC = "/images/${MACHINE}/deb"

do_package_write_deb_append() {
    import shutil

    # Compute 
    #  - src: package name and its location
    #  - dest: directory where to output this package
    pkg = 'PKG_' + d.getVar('PN', True)
    pkgName = d.getVar(pkg, True)
    #debFile = d.getVar('PN', True) + '_'  + d.getVar('PV', True) + '-' + d.getVar('PR', True) + '_'  + d.getVar('DPKG_ARCH',True) + '.deb' # The full name of the package
    debFile = pkgName + '_'  + d.getVar('PV', True) + '-' + d.getVar('PR', True) + '_'  + d.getVar('DPKG_ARCH',True) + '.deb' # The full name of the package
    pkgDir = d.getVar('PKGWRITEDIRDEB',True) + '/' + d.getVar('PACKAGE_ARCH', True) # Package's location
    pkgSrcPath = pkgDir + '/' + debFile
    installedPkgDir = d.getVar('DEPLOY_DIR', True) + "/deb/" + d.getVar('PACKAGE_ARCH', True) # Where stuff gets installed
    pkgDest = d.getVar('DEPLOY_DIR', True) + d.getVar('QTI_BINARY_PKG_LOC', True)

    bb.debug(1, ". Copying proprietary package " + pkgSrcPath + " -> " + pkgDest)
    if not os.path.isdir(pkgDest):
        bb.debug(2, "Creating proprietary package directory " + pkgDest)
        try:
            os.makedirs(pkgDest)
        except Exception, e:
            bb.fatal("Error creating package directory " + pkgDest + " : " + str(e) )
    try:
        shutil.copy(pkgSrcPath, pkgDest)
    except Exception, e:
        bb.fatal("Error copying " + pkgSrcPath + " -> " + pkgDest + " : " + str(e) )
}

