adb reboot bootloader
if exist emmc_appsboot.mbn fastboot flash aboot emmc_appsboot.mbn
if exist boot.img fastboot flash boot boot.img
if exist cache.img fastboot flash cache cache.img
if exist system.img fastboot flash system system.img
if exist userdata.img fastboot flash userdata userdata.img
fastboot reboot
echo Flashing done, rebooting...
PAUSE
