# Copyright (c) 2015 Qualcomm Technologies, Inc.
# All Rights Reserved.
# Confidential and Proprietary - Qualcomm Technologies, Inc.

adb reboot bootloader || true
if [ -e emmc_appsboot.mbn ]; then
	fastboot flash aboot emmc_appsboot.mbn
fi
if [ -e boot.img ]; then
	fastboot flash boot boot.img
fi
if [ -e cache.img ]; then
	fastboot flash cache cache.img
fi
if [ -e system.img ]; then
	fastboot flash system system.img
fi
if [ -e recovery.img ]; then
	fastboot flash recovery recovery.img
fi
if [ -e update.img ]; then
	fastboot flash update update.img
fi
if [ -e factory.img ]; then
	fastboot flash factory factory.img
fi
if [ -e userdata.img ]; then
	fastboot flash userdata userdata.img
fi
fastboot reboot
echo Flashing done, rebooting...
