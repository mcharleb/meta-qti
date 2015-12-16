#!/bin/sh
###############################################################################
#
# This script is used for administration of the Hexagon DSP
#
# Copyright (c) 2012-2015 Qualcomm Technologies, Inc.  All Rights Reserved.
# Qualcomm Technologies Proprietary and Confidential.
#
###############################################################################

# Wait for adsp.mdt to show up
while [ ! -s /lib/firmware/adsp.mdt ]; do
  sleep 0.1
done

# FIXME: See ATL-3054
echo 1 > /sys/module/subsystem_restart/parameters/enable_debug
# Bring adsp out of reset
echo "[INFO] Bringing ADSP out of reset"
echo 1 > /sys/kernel/boot_adsp/boot

# Don't leave until ADSP is up
while [ "`cat /sys/kernel/debug/msm_subsys/adsp`" != "2" ]; do
  sleep 0.1
done

# Emit adsp
initctl emit adsp
