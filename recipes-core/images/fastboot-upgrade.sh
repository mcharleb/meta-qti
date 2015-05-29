adb reboot bootloader
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
if [ -e userdata.img ]; then
	fastboot flash userdata userdata.img
fi
fastboot reboot
echo Flashing done, rebooting...
