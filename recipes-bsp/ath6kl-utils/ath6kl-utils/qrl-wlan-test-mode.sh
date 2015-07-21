#!/bin/sh
## Copyright (c) 2015 Qualcomm Technologies, Inc.  All Rights Reserved.
## Qualcomm Technologies Proprietary and Confidential.

if [ "$1" = "start" ]; then
	modprobe -r wlan
	insmod /lib/modules/$(uname -r)/kernel/drivers/net/wireless/wlan.ko testmode=2
	ftmdaemon
elif [ "$1" = "stop" ]; then
	modprobe -r wlan
	modprobe wlan
	pkill ftmdaemon
fi
