#!/bin/sh
###############################################################################
# Author: Rahul Anand (ranand@codeaurora.org)
#
# This file contains machine-specific functions to set the MAC addresses for
# various interfaces
#
# This file contains common functions and variables included in many scripts
#
# Copyright (c) 2014-2015, The Linux Foundation.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted (subject to the limitations in the
# disclaimer below) provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright
#   notice, this list of conditions and the following disclaimer.
#
# * Redistributions in binary form must reproduce the above copyright
#   notice, this list of conditions and the following disclaimer in the
#   documentation and/or other materials provided with the distribution.
#
# * Neither the name of the Linux Foundation nor the names of its
#   contributors may be used to endorse or promote products derived from
#   this software without specific prior written permission.
#
# NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
# GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
# HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
# BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
# OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
# IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
###############################################################################

. ${QRL_COMMON_INCS_DIR}/qrl-common-inc.sh

# We check this file to bypass configuring MAC addr
QRL_CHECK_FILE=/usr/local/qr-linux/.qrl_mac_done

QRL_DEFAULT_SOFTMAC_LOC=/lib/firmware/ath6k/AR6004/hw3.0
QRL_RAND_MAC_ROOT="00:03:7f:20" # Fixed first 4 bytes of MAC address
				# The last two (random) byte gets appended to this

QRL_PERSIST_PATH=/mnt/persist
QRL_WIFI_MAC_FILE=softmac.bin
QRL_UDEV_RULE=/etc/udev/rules.d/70-persistent-net.rules

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
	if [  ${optRunOnce} -eq 0 ]; then
		if [ -e ${QRL_CHECK_FILE} ]; then
			# Without the force option, we don't do anything if
			# a specific file is present
			echo "[INFO] MAC for WLAN already set, do nothing"
			return 0
		fi
	fi
	doConfigMACAddr $1 $2
}

##
## doRandomMac:
##    Generates a random MAC address using base 4 bytes
##    and appending 2 random bytes to it.
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
		touch ${QRL_CHECK_FILE}
		doRestartWifi
		;;
	*)
		echo "[ERROR] Don't understand interface: $interface"
		;;
	esac
}
