#!/bin/sh
## Copyright (c) 2014-2015 Qualcomm Technologies, Inc.  All Rights Reserved.
## Qualcomm Technologies Proprietary and Confidential.

. ${QRL_COMMON_INCS_DIR}/qrl-common-inc.sh

QRL_DEFAULT_SOFTMAC_LOC=/lib/firmware/ath6k/AR6004/hw3.0
QRL_RAND_MAC_ROOT="00:03:7F" # Fixed first 3 OUI bytes of MAC address
			# The last three (random) byte gets appended to this

QRL_PERSIST_PATH=/mnt/persist
QRL_WIFI_MAC_FILE=softmac.bin
QRL_UDEV_RULE=/etc/udev/rules.d/70-persistent-net.rules

QRL_DEFAULT_BTMAC_LOC=/lib/firmware/ar3k/1020201
QRL_BT_RAND_MAC_ROOT="00037F" # Fixed first 3 OUI bytes of MAC address
                # The last three (random) bytes gets appended to this
QRL_BT_MAC_FILE=ar3kbdaddr.pst

##
## createSoftmacBin
##    Create a binary softmac file for use with ATH6K WLAN adapter.
##    Takes a colon separated hex MAC address, the location and name of
##    file to create
##
createSoftmacBin() {
	local mac=$1
	local location=$2
	local name=$3

	file=${location}/${name}
	echo "[INFO] Setting WLAN MAC addr to ${mac}"

	mac="$( humanMACToNumber ${mac} )"
	if [ -d ${location} ] && [ -w ${location} ]; then
		if [ -e ${name} ]; then
			if [ -w ${name} ]; then
				echo "[WARNING] Overwriting existing ${file}"
			else
				echo "[ERROR] File ${file} exists but is not writable"
				return 1
			fi
		fi
	else
		echo "[ERROR] Can't write to location ${location}"
		return 1
	fi
	# Everything is ok, create the file
	echo ${mac} | xxd -r -p > ${file}
	echo "[INFO] Wrote file ${file}"
}

##
## configMACAddr:
##    Function called by target-independent script to configure MAC address
##    Configure the MAC address for interface ($1) to value ($2)
##    The MAC address can be set either from random value
##    or the specified : separated hex value
##
configMACAddr() {
	# Configure the MAC only if the file in persist and rootfs do
	# not match
	if [  ${optRunOnce} -eq 0 ] && [ "${1}" = "wlan" ]; then
		# Without the force option, we don't do anything if
		# the MAC is already set
		cmp --silent ${QRL_PERSIST_PATH}/${QRL_WIFI_MAC_FILE} \
			${QRL_DEFAULT_SOFTMAC_LOC}/${QRL_WIFI_MAC_FILE} && \
			echo "[INFO] MAC for WLAN already set, do nothing" && \
			return 0
	fi
    if [  ${optRunOnce} -eq 0 ] && [ "${1}" = "bt" ]; then
		# Without the force option, we don't do anything if
		# the MAC is already set
		cmp --silent ${QRL_PERSIST_PATH}/${QRL_BT_MAC_FILE} \
			${QRL_DEFAULT_BTMAC_LOC}/${QRL_BT_MAC_FILE} && \
			echo "[INFO] MAC for BT already set, do nothing" && \
			return 0
	fi
	doConfigMACAddr $1 $2
}

##
## doRandomMac:
##    Generates a random MAC address using base 3 OUI bytes
##    and appending 3 random bytes to it.
##
doRandomMac() {
	# Get a 2-digit random number for changing the MAC address
	mac=$(( $(od -An -N2 -i /dev/urandom)%(100) ))
	if [ $mac -lt 10 ]; then
		mac=0$mac
	fi
	randMac="${QRL_RAND_MAC_ROOT}:${mac}"
	mac=$(( $(od -An -N2 -i /dev/urandom)%(100) ))
	if [ $mac -lt 10 ]; then
		mac=0$mac
	fi
	randMac="${randMac}:${mac}"
	mac=$(( $(od -An -N2 -i /dev/urandom)%(100) ))
	if [ $mac -lt 10 ]; then
		mac=0$mac
	fi
	randMac="${randMac}:${mac}"
	echo "[INFO] Generated random WLAN MAC address: $randMac"
	createSoftmacBin ${randMac} ${QRL_DEFAULT_SOFTMAC_LOC} ${QRL_WIFI_MAC_FILE}
}

##
## doPersistSave:
##    Save the generated softmac.bin from QRL_DEFAULT_SOFTMAC_LOC
##    to the persist partition.
##
doPersistSave() {
	echo "[INFO] Save MAC file to persist partition"
	if [ -d ${QRL_PERSIST_PATH} ]; then
		cp ${QRL_DEFAULT_SOFTMAC_LOC}/${QRL_WIFI_MAC_FILE} \
			${QRL_PERSIST_PATH}/${QRL_WIFI_MAC_FILE}
	else
		echo "[ERROR] Persist partition does not exist"
		return 1
	fi
}

##
## doRestartWifi:
##    Delete de udev rules so we still use wlan0 and
##    restart the host driver
##
doRestartWifi() {
	# Reset the udev script to keep using wlan0
	if [ -f ${QRL_UDEV_RULE} ]; then
		echo "[INFO] Delete the udev rule"
		rm ${QRL_UDEV_RULE}
	fi
	# Reset the driver for the change to take effect
	echo "[INFO] Restart the host driver"
	modprobe -r wlan && modprobe wlan
}

##
## createBtMac
##    Create a binary mac file for use with AR3K BT adapter.
##    Takes a colon separated hex MAC address, the location and name of
##    file to create
##
createBtMac() {
	local mac=$1
	local location=$2
	local name=$3

	file=${location}/${name}
	echo "[INFO] Setting BT MAC addr to ${mac}"
    mac=$(awk -v mac="$mac" 'BEGIN { split(mac, arr, ":"); \
        { print arr[6]arr[5]arr[4]arr[3]arr[2]arr[1] } }')
	if [ -d ${location} ] && [ -w ${location} ]; then
		if [ -e ${name} ]; then
			if [ -w ${name} ]; then
				echo "[WARNING] Overwriting existing ${file}"
			else
				echo "[ERROR] File ${file} exists but is not writable"
				return 1
			fi
		fi
	else
		echo "[ERROR] Can't write to location ${location}"
		return 1
	fi
	# Everything is ok, create the file
	echo ${mac} > ${file}
	echo "[INFO] Wrote file ${file}"
}

##
## doBtRandomMac:
##    Generates a random BT MAC address using base 3 OUI bytes
##    and appending 3 random bytes to it.
##
doBtRandomMac() {
	# Get a 3-digit random number for changing the MAC address
	mac=$(( $(od -An -N2 -i /dev/urandom)%(100) ))
	if [ $mac -lt 10 ]; then
		mac=0$mac
	fi
	randMac="${QRL_BT_RAND_MAC_ROOT}${mac}"

	mac=$(( $(od -An -N2 -i /dev/urandom)%(100) ))
	if [ $mac -lt 10 ]; then
		mac=0$mac
	fi
	randMac="${randMac}${mac}"
	mac=$(( $(od -An -N2 -i /dev/urandom)%(100) ))
	if [ $mac -lt 10 ]; then
		mac=0$mac
	fi
	randMac="${randMac}${mac}"
	echo "[INFO] Generated random BT MAC address: $randMac"
	createBtMac ${randMac} ${QRL_DEFAULT_BTMAC_LOC} ${QRL_BT_MAC_FILE}
}

##
## doBtPersistSave:
##    Save the generated ar3kbdaddr.pst from QRL_DEFAULT_BTMAC_LOC
##    to the persist partition.
##
doBtPersistSave() {
	echo "[INFO] Save MAC file to persist partition"
	if [ -d ${QRL_PERSIST_PATH} ]; then
		cp ${QRL_DEFAULT_BTMAC_LOC}/${QRL_BT_MAC_FILE} \
			${QRL_PERSIST_PATH}/${QRL_BT_MAC_FILE}
	else
		echo "[ERROR] Persist partition does not exist"
		return 1
	fi
}

doConfigMACAddr() {
	local interface=$1
	local macAddr=$2

	retVal=
	macToUse=
	case $interface in
	wlan)
		case $macAddr in
		auto)
			echo "[INFO] Try persist partition first"
			if [ -f ${QRL_PERSIST_PATH}/${QRL_WIFI_MAC_FILE} ]; then
				echo "[INFO] Copy MAC file from persist partition"
				cp ${QRL_PERSIST_PATH}/${QRL_WIFI_MAC_FILE} \
					${QRL_DEFAULT_SOFTMAC_LOC}/${QRL_WIFI_MAC_FILE}
			else
				echo "[INFO] Persist file not found, generating random MAC"
				doRandomMac
				doPersistSave
			fi
			;;
		random)
			doRandomMac
			doPersistSave
			;;
		*)
			createSoftmacBin ${macAddr} ${QRL_DEFAULT_SOFTMAC_LOC} ${QRL_WIFI_MAC_FILE}
			doPersistSave
			;;
		esac
		# Save file to persist partition and restart Wi-Fi
		doRestartWifi
		;;
    bt)
        case $macAddr in
        auto)
			echo "[INFO] Try persist partition first"
			if [ -f ${QRL_PERSIST_PATH}/${QRL_BT_MAC_FILE} ]; then
				echo "[INFO] Copy MAC file from persist partition"
				cp ${QRL_PERSIST_PATH}/${QRL_BT_MAC_FILE} \
					${QRL_DEFAULT_BTMAC_LOC}/${QRL_BT_MAC_FILE}
            else
				echo "[INFO] Persist file not found, generating random MAC"
				doBtRandomMac
				doBtPersistSave
            fi
            ;;
        random)
            doBtRandomMac
            doBtPersistSave
            ;;
        *)
			createBtMac ${macAddr} ${QRL_DEFAULT_BTMAC_LOC} ${QRL_BT_MAC_FILE}
			doBtPersistSave
			;;
        esac
        ;;
	*)
		echo "[ERROR] Don't understand interface: $interface"
		;;
	esac
}
