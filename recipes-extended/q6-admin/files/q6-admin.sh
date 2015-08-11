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
Q6_WAIT_FOR_RESET_TIME=5
Q6_RESET_MSG="ADSP image is loaded"

do_start () {
        echo "Will load adsp firmware"
        mkdir -p /firmware
        mount -t vfat /dev/mmcblk0p1 /firmware
        cd /firmware/image/
        fwfiles=$(ls adsp*)
        cd /lib/firmware
        for fwfile in $fwfiles
        do
                fw_file=$(ls $fwfile)
                if ("$fw_file"=="$fwfile")then
                        echo "links already exist"
                        continue
                else
                        cd /firmware/image
                        for imgfile in adsp*
                        do
              ln -s /firmware/image/$imgfile /lib/firmware/$imgfile 2>/dev/null
                #cp -rf /firmware/image/$imgfile /lib/firmware/$imgfile
            done
                fi
        done
        cat /dev/subsys_adsp
}

resetQ6 () {
    if [  ! -f "${Q6_DEST}/${Q6_MDT}" ]
    then
       echo "[ERROR] Firmware file not found: ${Q6_DEST}/${Q6_MDT}. Aborting DSP bring up"
       return
    fi
    #At boot up adsp is not brought out of reset. Until we do below set of operations.
    echo "[INFO] Reseting DSP"     
	echo 1 > /sys/module/subsystem_restart/parameters/enable_debug  #hack. See ATL-3054
    echo ${Q6_INIT_STRING} > ${Q6_DEVICE}
    sleep ${Q6_WAIT_FOR_RESET_TIME}
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
do_start


