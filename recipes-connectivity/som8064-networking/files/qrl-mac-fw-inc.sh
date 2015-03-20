#!/bin/sh
###############################################################################
## This script copies the firmware from a target-specific location to
## where the kernel expects it (/lib/firmware)
##
## Copyright (c) 2012-2014 Qualcomm Technologies, Inc.  All Rights Reserved.
## Qualcomm Technologies Proprietary and Confidential
##
###############################################################################

QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-common-inc.sh

PRINTF=/usr/bin/printf
DD=/bin/dd

QRL_NV_FILE_LOC=/lib/firmware/wlan/prima
QRL_RAND_MAC_ROOT='00:0A:F5:89:89:' # The last (random) byte gets appended to this
globalWlanMAC= 

QRL_FW_SUBDIR=image		# The subdir in eMMC partition where the fw is
QRL_FW_WIFI_FILE_NAME=wcnss	# The root of thw WCN fw filename
QRL_FW_WIFI_FILE_EXT=mdt	# The extension of the WCN fw filename

QRL_NV_FILE_NAME=WCNSS_qcom_wlan_nv # The NV.bin file name and extension
QRL_NV_FILE_EXT=bin
QRL_NV_FILE_DEST_DIR=${QRL_LIB_FIRMWARE}/wlan/prima # Where to copy NV file

QRL_BT_NV_FILE_NAME=bt_nv # The NV.bin file name

##
## createBluetoothUser:
##    btnvtool requires the user bluetooth:bluetooth to update the .bt_nv.bin file
##    
createBluetoothUser() {
    userBT=bluetooth
    createGroup="-g $userBT"
    id $userBT > /dev/null || {
       echo "User $userBT doesn't exist. Creating."
       grep $userBT /etc/group || {
          echo "Creating group $userBT"
          createGroup="-U"
       }
       useradd $createGroup $userBT || {
          echo "[ERROR] could not create user/group $userBT"
          exit 1
       }
    }
}
##
## setAutoBTMacInNVFile:
##    Use btnvtool to set a random MAC addr if none exists, otherwise
##    use the existing MAC addr.
setAutoBTMACInNVFile() {
    createBluetoothUser
    /usr/bin/btnvtool -O
    globalBTMAC=`btnvtool -p 2>&1 | sed -e /board/\!d -e s/--board-address\:\ //`
}
##
## setBTMACInNVFile:
##   Use btnvtool to set a MAC address.
setBTMACInNVFile() {
    createBluetoothUser
    /usr/bin/btnvtool -b $1
    globalBTMAC=`btnvtool -p 2>&1 | sed -e /board/\!d -e s/--board-address\:\ //`
}
##
## getEthMAC
##   Use ifconfig to get a MAC address.
getEthMAC() {
    globalEthMAC=`ifconfig eth0 2>&1 | sed -e "/HWaddr/!d" -e "s/.*HWaddr\ //"`
}
##
## getBTMACFromNVFile:
##   Use btnvtool to get a MAC address.
getBTMACFromNVFile() {
    globalBTMAC=`btnvtool -p 2>&1 | sed -e /board/\!d -e s/--board-address\:\ //`
}
##
## getWlanMACFromNVFile:
##    Read the WCNSS NV file and extract the MAC address from it.
##    Returns 0 or 1, and sets globalWlanMAC
##    
getWlanMACFromNVFile() {
    nvFile=${QRL_NV_FILE_LOC}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    if [ ! -r ${nvFile} ] 
    then
	echo "[ERROR] Can't read from file ${nvFile}"
	return 1
    fi
    globalWlanMAC=`od -A n -j 10 -N 6 -t xC ${nvFile} | sed -e "s/ //" -e "s/ /:/g"`
    if [ ${globalWlanMAC} = "00:00:00:00:00:00" ]
    then
	echo "[INFO] No MAC address set in NV file"
	return 1
    fi
    return 0
}
##
## setWlanMACInNVFile:
##    Set the provided MAC address to the WCNSS NV file in /lib/firmware.
##    Don't touch the persist partition
##    
setWlanMACInNVFile() {
    nvFile=${QRL_NV_FILE_LOC}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    if [ ! -w ${nvFile} ] 
    then
	echo "[ERROR] Can't write file ${nvFile}, or not present"
	return 1
    fi
    existingMAC=`od -A n -j 10 -N 6 -t xC ${nvFile} | sed -e "s/ //" -e "s/ /:/g"`
    if [ ${existingMAC} != "00:00:00:00:00:00" ]
    then
	echo "[WARNING] Overwriting existing MAC address: ${existingMAC}"
    fi
    
    echo "[INFO] Setting MAC address to ${macToUse} in ${nvFile}"
    macToUse=":${1}" # Prepend a :, for the sed step next
    macToUseHex=$( echo ${macToUse} | sed 's/:/\\x/g' )
    ${PRINTF} ${macToUseHex} | ${DD} of=${nvFile} bs=1 seek=10 count=6 conv=notrunc > /dev/null
}

##
## displayMACAddr:
##    Display the MAC address for interface ($1)
##    
displayMACAddr() {
    local interface=$1

    case $interface in
	wlan)
		getWlanMACFromNVFile
		if [ $? -eq 0 ]
		then
			echo "[INFO] Read MAC from NV file: ${globalWlanMAC}"
		else
			echo "[ERROR] Could not get MAC from NV file"
			return 1
		fi
	    ;;
	eth)
		getEthMAC
		if [ $? -eq 0 ]
		then
			echo "[INFO] Read MAC from ifconfig: ${globalEthMAC}"
		else
			echo "[ERROR] Could not get MAC"
			return 1
		fi
	    ;;
	bt)
		getBTMACFromNVFile
		if [ $? -eq 0 ]
		then
			echo "[INFO] Read MAC from NV file: ${globalBTMAC}"
		else
			echo "[ERROR] Could not get MAC from NV file"
			return 1
		fi
	    ;;
	*)
	    echo "[ERROR] Don't undertand interface: $interface"
	    return 1
	    ;;
    esac
}

##
## configMACAddr:
##    Configure the MAC address for interface ($1) to value ($2)
##    For the SOM, we can't set the ethernet address. The WLAN
##    MAC address can be set only from the NV file.
##    Random value and specified value may also be specified
##    
configMACAddr() {
    local interface=$1
    local macAddr=$2

    case $interface in
	wlan)
	    macToUse=
	    case $macAddr in
		auto)
		    getWlanMACFromNVFile
		    if [ $? -eq 0 ]
		    then
			echo "[INFO] Read MAC from NV file: ${globalWlanMAC}"
			macToUse=${globalWlanMAC}
		    else
			echo "[ERROR] Could not get MAC from NV file"
			return 1
		    fi
		    ;;
		random)
                    # Get a 2-digit random nunmber for changing the MAC address
		    mac=$(( 1+$(od -An -N2 -i /dev/random)%(100) ))
		    randMac=${QRL_RAND_MAC_ROOT}${mac}
		    echo "[INFO] Generated random Wi-Fi MAC address: $randMac"
		    macToUse=${randMac}
		    ;;
		*)
		    macToUse=${macAddr}
		    ;;
	    esac
	    setWlanMACInNVFile ${macToUse}
	    return $?
	    ;;
	eth)
	    echo "[ERROR] Can't set ethernet MAC address for this device type"
	    return 1
	    ;;
	bt)
	    case $macAddr in
		auto)
		    setAutoBTMACInNVFile
                    retval=$?
		    echo "[INFO] BT Mac address: ${globalBTMAC}"
		    ;;
		random)
                    # Get a 2-digit random nunmber for changing the MAC address
		    mac=$(( 1+$(od -An -N2 -i /dev/random)%(100) ))
		    randMac=${QRL_RAND_MAC_ROOT}${mac}
		    echo "[INFO] Generated random MAC address: $randMac"
		    setBTMACInNVFile ${randMac}
                    retval=$?
		    ;;
		*)
		    setBTMACInNVFile $macAddr
                    retval=$?
		    echo "[INFO] BT Mac address: ${globalBTMAC}"
		    ;;
	    esac
	    return $retval
	    ;;
	*)
	    echo "[ERROR] Don't undertand interface: $interface"
	    return 1
	    ;;
    esac
}


# Where to copy the firmware files from
qrlFwImgDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_MODEM}/${QRL_FW_SUBDIR}

# The .mdt file's full source and destination paths
wifiMdtSrcFile=${qrlFwImgDir}/${QRL_FW_WIFI_FILE_NAME}.${QRL_FW_WIFI_FILE_EXT}
wifiMdtDstFile=${QRL_LIB_FIRMWARE}/${QRL_FW_WIFI_FILE_NAME}.${QRL_FW_WIFI_FILE_EXT}

##
## copyFirmware
##    Function called by target-independent script to copy firmware
##    Copies the firmware from target-specific source, to target-specfic location
##    under /lib/firmware
##    
copyFirmware() {
    mountPartition ${QRL_PARTITION_NAME_MODEM}
    if [ $? -gt 0 ]
    then
	echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_MODEM}"
	return 1
    fi
    
    if [ ! -e ${wifiMdtSrcFile} ]
    then
	echo "[ERROR] Firmware not found or mount failed"
	return 1
    fi

    # Copy the wcnss* files from eMMC partition to /lib/firmware
    /bin/cp ${qrlFwImgDir}/${QRL_FW_WIFI_FILE_NAME}* ${QRL_LIB_FIRMWARE}
    
    if [ ! -f ${wifiMdtDstFile} ]
    then
	echo "[ERROR] Firmware  not found. Giving up"
	return 1
    fi
    echo "[INFO] Copied firmware"
    return 0
}

##
## copyMACAddr
##    Function called by target-independent script to copy MAC Address
##    Copies the MAC address files to appropriate target-specific location
##    
copyMACAddr() {

    qrlNVFileDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}
    wifiMdtNVFile=${qrlNVFileDir}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    wifiMdtNVDstFile=${QRL_NV_FILE_DEST_DIR}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    btNVFile=${qrlNVFileDir}/.${QRL_BT_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    btNVDstFile=${QRL_LIB_FIRMWARE}/${QRL_BT_NV_FILE_NAME}.${QRL_NV_FILE_EXT}

    mountPartition ${QRL_PARTITION_NAME_PERSIST}
    if [ $? -gt 0 ]
    then
	echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_PERSIST}"
	return 1
    fi
    
    if [ -f ${wifiMdtNVFile} ]; then
	/bin/cp ${wifiMdtNVFile} ${wifiMdtNVDstFile}
    else
	echo "[WARNING] WLAN NV File not found or mount failed"
	return 1
    fi
    
    if [ ! -f ${wifiMdtNVDstFile} ]; then
	echo "[ERROR] WLAN NV File not found, Giving up"
	return 1
    else
        echo "[INFO] Copied WLAN MAC address"
    fi

    if [ ! -f ${btNVFile} ]; then
        # Generate a random BT MAC if file does not exist yet
        setAutoBTMACInNVFile
    fi

    if [ -f ${btNVFile} ]; then
	/bin/cp ${btNVFile} ${btNVDstFile}
    else
	echo "[WARNING] BT NV File not found or mount failed"
	return 1
    fi
    
    if [ ! -f ${btNVDstFile} ]; then
	echo "[ERROR] BT NV File not found, Giving up"
	return 1
    else
        echo "[INFO] Copied BT MAC address"
    fi
    return 0
}

