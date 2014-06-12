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
DEFAULT_MACHINE=${MACHINE_IFC6410}

TOOLCHAIN_NAME=gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux

DPKG_CMD="dpkg-query"

optMachine=${DEFAULT_MACHINE}
do_buildBootloader=1
use_local=0

#################################################################################################
## usage
#################################################################################################
usage ()
{
    cat << EOF
This script builds the QR-Linux kernel and proprietary binaries, generating the
necessary images for fastboot.
Instructions to build:
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
$ ./meta-qti/qrlbuild.sh <options>

USAGE: $0 options
OPTIONS:
   -h      Show this message
   -b      Skip building the bootloader
   -m      The machine to build (default: ${DEFAULT_MACHINE})
EOF
}

################################################################################
## handleCommandLine
################################################################################
handleCommandLine () {
   while getopts "bhlm:" o
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
          ?)
              echo "Unknown arg $o"
              usage
              exit
              ;;
      esac
   done
   if [[ ${optMachine} != ${MACHINE_IFC6410} && ${optMachine} != ${MACHINE_SOM8064} ]]
   then
      echo "[ERROR] Unsupported machine: ${optMachine}"
      exit 1
   fi
}

################################################################################
## buildBootloader
################################################################################
buildBootloader () {
  echo "***** Starting to build the LK bootloader *****"

  # Set up the bitbake environment
  if [ ! -d buildlk ]; then
    source oe-init-build-env buildlk
    grep -vEe "EXTERNAL_TOOLCHAIN|TCMODE" ../build/conf/local.conf > conf/local.conf
    cp  ../build/conf/bblayers.conf conf/bblayers.conf
  else
    source oe-init-build-env buildlk
  fi

  # Compile the cross compiler
  bitbake gcc-cross 

  # Compile the libgcc.a
  # Note there is an error during this phase, but the libgcc.a is copmiled and seems to work

  LIBGCCFILE=$BUILDDIR/tmp-eglibc/sysroots/x86_64-linux/usr/include/gcc-build-internal-cortexa8hf-vfp-neon-linux-gnueabi/arm-linux-gnueabi/libgcc/libgcc.a

  if [ ! -f $LIBGCCFILE ]; then
    set +e
    bitbake libgcc
    set -e
  fi

  if [ ! -f $LIBGCCFILE ]; then
    echo "Error generating libgcc.a"
    exit 1
  fi

  # Update the lk sources
  bitbake -c patch lk

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
##    Add meta-qti layer
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

if [ ! -d  ${TOOLCHAIN_NAME} ]
then
   echo "[INFO] Fetching toolchain"
   meta-qr-linux/scripts/linaro-fetch.sh
fi

if [[ ${optMachine} = ${MACHINE_SOM8064} && ${do_buildBootloader} = 1 ]]
then
   buildBootloader
fi
configureLayers
source ./oe-init-build-env build
MACHINE=${optMachine} bitbake core-image-qrl
MACHINE=${optMachine} bitbake qrl-binaries
MACHINE=${optMachine} bitbake persist

