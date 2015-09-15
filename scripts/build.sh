#!/bin/bash
###############################################################################
## Copyright (c) 2012-2015 Qualcomm Technologies, Inc.
## All Rights Reserved.
## Confidential and Proprietary - Qualcomm Technologies, Inc.
###############################################################################

MACHINE_EAGLE8074=eagle8074
MACHINES="${MACHINE_EAGLE8074}"
DEFAULT_MACHINE=${MACHINE_EAGLE8074}

IMAGE_QRL_BINARIES=qrl-binaries
IMAGE_PERSIST=persist
IMAGE_SDK=meta-ide-support
IMAGES="${IMAGE_QRL_BINARIES} ${IMAGE_PERSIST}"
DEFAULT_IMAGE=${IMAGE_QRL_BINARIES}

DPKG_CMD="dpkg-query"

optMachine=${DEFAULT_MACHINE}
optImage=
optVersion=			# The build version
optOverrideVersionWarning=

imagesToBuild=

# Exit on error
set -e

# Source the release version includes. This sets the variables needed by configureBuildVersion
. meta-qti/recipes-extended/qrl-version/files/qrl-version.inc

################################################################################
## usage
################################################################################
usage ()
{
    cat << EOF
This scripts builds the QR-Linux kernel and other necessary images
USAGE: $0 options
OPTIONS:
   -h      Show this message
   -b      Skip building the bootloader
   -m      The machine to build (default: ${DEFAULT_MACHINE})
   -i      The image to build (default: ${DEFAULT_IMAGE})
   -v      The build version (default: current time)
   -d      Ignore release version and date mismatch warning
EOF

    echo
    echo "Valid machine values are:"
    for machine in ${MACHINES}
    do
        echo "    $machine"
    done
    echo
    echo
    echo "Valid image values are:"
    for image in ${IMAGES}
    do
        echo "    $image"
    done
}

################################################################################
## handleCommandLine
################################################################################
handleCommandLine () {
    while getopts "hdm:i:v:" o
    do
        case $o in
        h)
            usage
            exit 1
            ;;
        d)
            optOverrideVersionWarning=1
            ;;
        m)
            optMachine=$OPTARG
            ;;
        i)
            optImage=$OPTARG
            ;;
        v)
            optVersion=$OPTARG
            ;;
        ?)
            echo "Unknown arg $o"
            usage
            exit
            ;;
        esac
    done

    foundMachine=0
    for machine in ${MACHINES}
    do
        if [[ ${optMachine} = ${machine} ]]
        then
            foundMachine=1
        fi
    done

    if [[ $foundMachine -eq 0 ]]
    then
        echo "[ERROR] Unsupported machine: ${optMachine}"
        echo
        echo "Valid machine values are:"
        for machine in ${MACHINES}
        do
            echo "    $machine"
        done
        exit 1
    fi

    if [[ -z ${optImage} ]]
    then
        imagesToBuild=(${IMAGE_QRL_BINARIES} ${IMAGE_PERSIST})
        x="${IMAGE_QRL_BINARIES} -c update_package"
        IFS=""
        imagesToBuild=("$x")
        if [[ ${optMachine} = ${MACHINE_EAGLE8074} ]]
        then
            # Add SDK as another build product
            x="${IMAGE_SDK}"
            imagesToBuild=("${imagesToBuild[@]}" "$x")
        fi
    else
        foundImage=0
        for image in ${IMAGES}
        do
            if [[ ${optImage} = ${image} ]]
            then
                foundImage=1
                imagesToBuild=(${optImage})
            fi
        done

        if [[ $foundImage -eq 0 ]]
        then
            echo "[ERROR] Unsupported image: ${optImage}"
            echo
            echo "Valid image values are:"
            for image in ${IMAGES}
            do
                echo "    $image"
            done
            exit 1
        fi
    fi
}

################################################################################
## checkRequiredPkgs
################################################################################
checkRequiredPkgs () {
    type ${DPKG_CMD} > /dev/null 2>&1 || {
        echo "[ERROR] ${DPKG_CMD} not found. Can't check for installed packages"
        return 1
    }

    pkgMissing=0
    pkgsMissing="Packages missing: "
    for pkg in diffstat texinfo gawk chrpath
    do
        ${DPKG_CMD} --status ${pkg} > /dev/null 2>&1
        if [ $? -ne 0 ]
        then
            pkgMissing=1
            pkgsMissing="${pkgsMissing} ${pkg}"
        fi
    done

    if [ ${pkgMissing} = 1 ]
    then
        echo "[ERROR] Check for required packages failed. ${pkgsMissing}"
        return 1
    fi
    return ${pkgMissing}
}

################################################################################
## configureLayers
##    This is slightly tricky.
##    We add additional layers to the bblayers.conf file. We also add
##    a comment string to that file. When the script is re-run, it
##    checks for the existence of the comment line, and skips adding
##    the layers all over again.
################################################################################
configureLayers () {
   bblayersFile=build/conf/bblayers.conf
   commentString="# Additional layers for proprietary use -- autogenerated by build script"

    if [ ! -f ${bblayersFile} ]
    then
        echo "[ERROR] Unable to find ${bblayersFile}. Are you running from the top of the tree?"
        exit 1
    fi

    grep "${commentString}" ${bblayersFile} > /dev/null 2>&1 || {
        echo ${commentString} >> ${bblayersFile}
        for layer in meta-qti meta-qti-prebuilt
        do
            echo "BBLAYERS += \"\${TOPDIR}/../${layer}\"" >> ${bblayersFile}
        done
        return 0
    }
    return 0
}

################################################################################
## configureBuildVersion
##    Configure a build version
################################################################################
configureBuildVersion () {
   # Check if the QRL_RELEASE_YEAR and QRL_RELEASE_MONTH match today's date
   yr=`date +%y`
   if [ ${QRL_RELEASE_YEAR} != ${yr} ]
   then
      echo "[INFO] Mismatch between today's date and release version (year): ${QRL_RELEASE_YEAR}"
      if [ -z ${optOverrideVersionWarning} ]
      then
         echo "[ERROR] Use -d to override"
	 exit 1
      fi
   fi
   mo=`date +%m`
   if [ ${QRL_RELEASE_MONTH} != ${mo} ]
   then
      echo "[INFO] Mismatch between today's date and release version (month): ${QRL_RELEASE_MONTH}"
      if [ -z ${optOverrideVersionWarning} ]
      then
         echo "[ERROR] Use -d to override"
	 exit 1
      fi
   fi

   buildId=${QRL_RELEASE}
   if [ -z ${optVersion} ]
   then
      buildId="${buildId}_`date +%F-%H%M%S`"
   else
      buildId=${buildId}_${optVersion}
   fi
   echo -n > ${BUILD_VERSION_FILE} # Truncate the file
   echo ${buildId} >> ${BUILD_VERSION_FILE}
   echo `date +"%Y-%m-%d %H:%M:%S"` >> ${BUILD_VERSION_FILE}
   return 0
}

################################################################################
## main
################################################################################

export TEMPLATECONF=meta-qr-linux/conf

handleCommandLine $@
echo "[INFO] Building machine: ${optMachine}. Images: ${imagesToBuild[@]}"
checkRequiredPkgs || {
    echo "[ERROR] Can't build because of missing build tools"
    exit 1
}

echo "[INFO] Fetching toolchain"
meta-qr-linux/scripts/linaro-fetch.sh

configureLayers
configureBuildVersion

source ./oe-init-build-env build

MACHINE=${optMachine} bitbake -f -C compile qrl-version
for img in "${imagesToBuild[@]}"
do
    echo "[INFO] Building image: ${img}"
    eval MACHINE=${optMachine} bitbake ${img}
done
