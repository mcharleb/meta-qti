#!/bin/sh
###############################################################################
# 
# This script is used for administration of the Hexagon DSP
# 
# Copyright (c) 2012-2014 Qualcomm Technologies, Inc.  All Rights Reserved.
# Qualcomm Technologies Proprietary and Confidential.
# 
###############################################################################

Q6_DEST=/lib/firmware
Q6_MDT=adsp.mdt
Q6_DEVICE=/sys/kernel/boot_adsp/boot
Q6_INIT_STRING=1
Q6_WAIT_FOR_RESET_TIME=10
Q6_RESET_MSG="adsp: Brought out of reset"

resetQ6 () {
    if [  ! -f "${Q6_DEST}/${Q6_MDT}" ]
    then
       echo "[ERROR] Firmware file not found: ${Q6_DEST}/${Q6_MDT}. Aborting DSP bring up"
       return
    fi
    #These three lines are commented out from db8074 because kernel resets adsp while starting all subsystems.
    #echo "[INFO] Reseting DSP"     
    #echo ${Q6_INIT_STRING} > ${Q6_DEVICE}
    #sleep ${Q6_WAIT_FOR_RESET_TIME}
    resetMsgs=`dmesg | grep "${Q6_RESET_MSG}" | wc -l`
    if [ ${resetMsgs} = 0 ] 
    then
	echo "[ERROR] DSP not brought of reset"
        return 1
    elif [ ${resetMsgs} = 1 ]
    then
        echo "[INFO] DSP Successfully brought out of reset"
    else 
	echo "[WARNING] DSP status ambiguous. Please confirm using command below"
	echo "[WARNING]    dmesg | grep \"${Q6_RESET_MSG}\""
	echo "[WARNING] that it was brought out of reset recently"
    fi
    return 0
}

resetQ6



