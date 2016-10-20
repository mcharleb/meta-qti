#!/bin/sh
#################################################################################################
## Author: Kaustubh Gondkar (kgondkar@qti.qualcomm.com)
## 
## This script configures bluetooth during kernel booting sequence.
##
## v0.2: dbus start is required. Its not being started during boot
## v0.1: Basic draft
#################################################################################################
SCRIPT_VERSION="v0.2"
echo "BT Init Script: $SCRIPT_VERSION"

QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-mac-fw-inc.sh

QC_NO_HCI_INIT=0
if [ 1 -eq $# ]
then
    QC_NO_HCI_INIT=$1
fi

MKDIR=/bin/mkdir
CHOWN=/bin/chown
CHMOD=/bin/chmod

DBUS=/etc/init.d/dbus
BT_DAEMON=/usr/sbin/bluetoothd
BT_DAEMON_OPTIONS="-d -n"
HCICONFIG=/usr/sbin/hciconfig

QC_BT_NV_TOOL=/usr/bin/btnvtool
QC_BT_NV_TOOL_OPTIONS="-O"
QC_BT_HCICONFIG_TOOL=/usr/bin/hci-qcomm-init
QC_BT_HCICONFIG_TOOL_OPTIONS="-e -v -v -v -v -v"

QC_FS_PERSIST_DIR=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}

isMounted ${QRL_DEVICE_PERSIST} ${QC_FS_PERSIST_DIR}
if [ $? -ne 0 ]
then
   mountPartition ${QRL_PARTITION_NAME_PERSIST}
fi

myrandom=$(date +%S)

ERROR_FLAG=0;

checkFile () {
    fileName=$1
    echo -n "  Checking $fileName..." 
    if [ ! -f $fileName ]
    then
        echo "FAILED"
        ERROR_FLAG=1
    else
        echo "OK"
    fi
}

checkErrorDie () {
    if [ 1 -eq $ERROR_FLAG ]
    then
        echo "ERROR: Fix above error first... Exiting.."
        exit;
    fi
}

echo "Verifying BT configuration dependencies.."
checkFile $QC_BT_NV_TOOL
checkFile $QC_BT_HCICONFIG_TOOL
checkFile $DBUS
checkFile $BT_DAEMON
checkFile $HCICONFIG

echo -n "  Checking WiFi hardware initialization status..." 
cfgModule=`grep cfg80211 /proc/modules`
cfgModule=${#cfgModule}
if [ $cfgModule -eq 0 ]
then
    ifup wlan0
    cfgModule=`grep cfg80211 /proc/modules`
    cfgModule=${#cfgModule}
    if [ $cfgModule -eq 0 ]
    then
        echo "FAILED"
        echo -n "    "
        echo "NOTE: WiFi harware initilization must be done before bluetooth configuration"
        ERROR_FLAG=1
    else
       echo "loaded wlan0"
    fi
else
    echo "OK"
fi

checkErrorDie

if [ ! -d "$QC_FS_PERSIST_DIR" ]
then
    echo "Creating $QC_FS_PERSIST_DIR directory.."
    ${MKDIR} -p "$QC_FS_PERSIST_DIR"
fi

if [ 0 -eq $QC_NO_HCI_INIT ]
then
    echo "Getting BT address.."
    # This also copies it to /lib/firmware
    createBluetoothUser
    copyMACAddr

    echo "Downloading BT QSoC firmware.."
    ${QC_BT_HCICONFIG_TOOL} ${QC_BT_HCICONFIG_TOOL_OPTIONS}
    if [ $? -ne 0 ]; then
        echo "ERROR: BT QSoC firmware download failed.." 
        exit 1
    else
        echo "BT QSoC firmware download sucessful.."
    fi
else
    echo "Skipping hardware init"
fi

echo "Updating permissions of BT config parms"
${CHOWN} bluetooth:bluetooth /sys/module/hci_smd/parameters/hcismd_set
${CHMOD} 0660 /sys/module/hci_smd/parameters/hcismd_set
${CHOWN} bluetooth:bluetooth /dev/ttyHSL0
${CHMOD} 0660 /dev/ttyHSL0

echo "Initializing BT hci interface.."
echo 1 > /sys/module/hci_smd/parameters/hcismd_set
sleep 2

echo "Updating permissions of minor BT config parms"
${CHOWN} bluetooth:bluetooth /sys/class/rfkill/rfkill0/type
${CHOWN} bluetooth:bluetooth /sys/class/rfkill/rfkill0/state
${CHMOD} 0660 /sys/class/rfkill/rfkill0/state
${CHOWN} bluetooth:bluetooth /sys/devices/platform/msm_serial_hs.0/clock
${CHMOD} 0660 /sys/devices/platform/msm_serial_hs.0/clock

echo "Restarting dbus.."
${DBUS} "restart"
sleep 2

echo "Stopping bluetooth /etc/init.d/bluetooth service"
/etc/init.d/bluetooth stop

echo "Starting manually bluetoothd"
nohup ${BT_DAEMON} ${BT_DAEMON_OPTIONS} &
sleep 2

#${HCICONFIG} hci0 up
#sleep 2
${HCICONFIG} hci0 class 0x5a020c
${HCICONFIG} hci0 piscan
${HCICONFIG} hci0 noauth
${HCICONFIG} hci0 noencrypt
myhostname="clarence-bt"
btname=$myhostname$myrandom
${HCICONFIG} hci0 name $btname

#BT DUN port-bridge
${CHMOD} 0660 /dev/smd3
${CHOWN} bluetooth:bluetooth /dev/smd3

isMounted ${QRL_DEVICE_PERSIST} ${QC_FS_PERSIST_DIR}
if [ $? -ne 0 ]
then
   umount ${QRL_PARTITION_NAME_PERSIST}
fi

echo "Done."
