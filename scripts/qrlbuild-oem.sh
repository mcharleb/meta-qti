#!/bin/bash
###############################################################################
## Author: Rahul Anand (ranand@qti.qualcomm.com)
## 
## This is the build script for the QR-Linux project used by OEMs
##
## Copyright (c) 2012-2014 Qualcomm Technologies, Inc.  All Rights Reserved.
## Qualcomm Technologies Proprietary and Confidential.
###############################################################################

MACHINE_IFC6410=ifc6410
MACHINE_SOM8064=som8064
MACHINES="${MACHINE_IFC6410} ${MACHINE_SOM8064}"
DEFAULT_MACHINE=${MACHINE_IFC6410}

IMAGE_QRL_BINARIES=qrl-binaries
IMAGE_PERSIST=persist
IMAGES="${IMAGE_QRL_BINARIES} ${IMAGE_PERSIST}"
DEFAULT_IMAGE=${IMAGE_QRL_BINARIES}

DPKG_CMD="dpkg-query"

optMachine=${DEFAULT_MACHINE}
optImage=

do_buildBootloader=1
imagesToBuild=


# Exit on error
set -e


################################################################################
## usage
################################################################################
usage ()
{
    cat << EOF
This script builds the QR-Linux kernel and proprietary binaries, generating the
necessary images for fastboot.
INSTRUCTIONS TO BUILD:
----------------------
If you are looking at these instructions, you have already completed the 
steps to download the code from the appropriate site. 
Assuming your top-level directory is "workdir", follow these steps:

$ cd workdir
$ cd apss_proc/src/
$ ls
    data diag ... oe-core ...
$ cd oe-core
Sync repo from CAF
$ repo init -u git://codeaurora.org/quic/lx/qr-linux/manifest -b release -m xxx.xml
$ repo sync
$ ./meta-qti/qrlbuild-oem.sh <options>

USAGE: $0 options
OPTIONS:
   -h      Show this message
   -b      Skip building the bootloader
   -m      The machine to build (default: ${DEFAULT_MACHINE})
   -i      The image to build (default: ${DEFAULT_IMAGE})
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
   while getopts "bhm:i:" o
   do
      case $o in
         h)
             usage
             exit 1
             ;;
          b)
              do_buildBootloader=0
              ;;
          m)
              optMachine=$OPTARG
              ;;
          i)
              optImage=$OPTARG
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
      if [[ ${optMachine} = ${MACHINE_IFC6410} ]]
      then
         x="${IMAGE_QRL_BINARIES} -c image"
         imagesToBuild=("$x")
      elif [[ ${optMachine} = ${MACHINE_SOM8064} ]] 
      then
         x="${IMAGE_QRL_BINARIES} -c image"
         imagesToBuild=("$x")
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
## buildBootloader
################################################################################
buildBootloader () {
  echo "***** Starting to build the LK bootloader *****"

  # Set up the bitbake environment
  if [ ! -d buildlk.${optMachine} ]; then
    source oe-init-build-env buildlk.${optMachine}
    grep -vEe "EXTERNAL_TOOLCHAIN|TCMODE" ../build/conf/local.conf > conf/local.conf
    cp  ../build/conf/bblayers.conf conf/bblayers.conf
  else
    source oe-init-build-env buildlk.${optMachine}
  fi

  # Compile the cross compiler
  MACHINE=${optMachine} bitbake gcc-cross 

  # Compile the libgcc.a
  # Note there is an error during this phase, but the libgcc.a is copmiled and seems to work

  LIBGCCFILE=$BUILDDIR/tmp-eglibc/sysroots/x86_64-linux/usr/include/gcc-build-internal-cortexa8hf-vfp-neon-linux-gnueabi/arm-linux-gnueabi/libgcc/libgcc.a

  if [ ! -f $LIBGCCFILE ]; then
    set +e
    MACHINE=${optMachine} bitbake libgcc
    set -e
  fi

  if [ ! -f $LIBGCCFILE ]; then
    echo "Error generating libgcc.a"
    exit 1
  fi

  # Update the lk sources
  MACHINE=${optMachine} bitbake -c patch lk

  LKBOOTLOADERFILE=$BUILDDIR/tmp-eglibc/work/arm-linux-gnueabi/lk/1.0-r9/lk-1.0/build-msm8960/emmc_appsboot.mbn

  # Compile lk bootloader
  if [ ! -f $LKBOOTLOADERFILE ]; then
    (cd $BUILDDIR/tmp-eglibc/work/arm-linux-gnueabi/lk/1.0-r9/lk-1.0;
    export PATH=$BUILDDIR/tmp-eglibc/sysroots/x86_64-linux/usr/bin/cortexa8hf-vfp-neon-linux-gnueabi:$PATH;
    make -j 1 TOOLCHAIN_PREFIX='arm-linux-gnueabi-' msm8960 EMMC_BOOT=1 SIGNED_KERNEL=0 ENABLE_THUMB=false LIBGCC=$LIBGCCFILE)
  fi

  if [ ! -f $LKBOOTLOADERFILE ]; then
    echo "Error generating lk bootloader"
    exit 1
  else
    # Install in the proper place
    mkdir -p $BUILDDIR/../build/tmp-eglibc/deploy/images/${optMachine}/out
    cp $LKBOOTLOADERFILE $BUILDDIR/../build/tmp-eglibc/deploy/images/${optMachine}/out
  fi

  cd ..
  echo "***** Finished building the LK bootloader *****"
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
   for pkg in diffstat texinfo gawk chrpath multistrap
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
   commentString="# Additional layers for proprietary components -- autogenerated by build script"

   if [ ! -f ${bblayersFile} ]
   then
      echo "[ERROR] Unable to find ${bblayersFile}. Are you running from the top of the tree?"
      exit 1
   fi

   grep "${commentString}" ${bblayersFile} > /dev/null 2>&1 || {

      echo ${commentString} >> ${bblayersFile}
      for layer in meta-qti 
      do
         echo "BBLAYERS += \"\${TOPDIR}/../${layer}\"" >> ${bblayersFile}
      done
      return 0
   }
   return 0
}

################################################################################
## main
################################################################################

handleCommandLine $@
echo "[INFO] Building machine: ${optMachine}"
checkRequiredPkgs || {
   echo "[ERROR] Can't build because of missing build tools"
   exit 1
}


echo "[INFO] Fetching toolchain"
meta-qr-linux/scripts/linaro-fetch.sh

configureLayers

source ./oe-init-build-env build
for ((i=0; i < ${#imagesToBuild[@]}; i++))
do
   echo "[INFO] Building ${imagesToBuild[$i]}"
   MACHINE=${optMachine} bitbake ${imagesToBuild[$i]} || true

done
