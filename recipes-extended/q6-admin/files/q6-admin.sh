#!/bin/sh
###############################################################################
#
# This script is used for administration of the Hexagon DSP
#
# Copyright (c) 2012-2015 Qualcomm Technologies, Inc.  All Rights Reserved.
# Qualcomm Technologies Proprietary and Confidential.
#
###############################################################################

# FIXME: See ATL-3054
echo 1 > /sys/module/subsystem_restart/parameters/enable_debug
# Bring adsp out of reset
echo "[INFO] Bringing ADSP out of reset"
echo 1 > /sys/kernel/boot_adsp/boot

# Don't leave until ADSP is up
watch -n 1 --precise -g grep -m 1 "2" /sys/kernel/debug/msm_subsys/adsp && true

# Emit adsp
initctl emit adsp
