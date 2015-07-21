rem Copyright (c) 2015 Qualcomm Technologies, Inc.
rem All Rights Reserved.
rem Confidential and Proprietary - Qualcomm Technologies, Inc.

adb reboot bootloader
if exist emmc_appsboot.mbn fastboot flash aboot emmc_appsboot.mbn
if exist boot.img fastboot flash boot boot.img
if exist cache.img fastboot flash cache cache.img
if exist persist.img fastboot flash persist persist.img
if exist system.img fastboot flash system system.img
if exist userdata.img fastboot flash userdata userdata.img
fastboot reboot
echo Flashing done, rebooting...
PAUSE
