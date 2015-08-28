/*****************************************************************************
* FILE: reboot2fastboot.c
* DESCRIPTION: Reboot the device in bootloader/fastboot mode
*
* Copyright (c) 2013, Qualcomm Technologies Inc. All rights reserved.
*****************************************************************************/
#include <linux/reboot.h>
#include <stdio.h>
#include <errno.h>
#include <unistd.h>

void print_usage(char *command) {
    printf("Usage:\n%s <boot_mode>\n", command);
    printf("List of boot modes:\n");
    printf("bootloader: reboot to fastboot mode\n");
    printf("recovery: reboot to recovery mode\n");
    printf("edl: reboot to emergency download mode\n");
}

/*****************************************************************************
* Reboot device and wait for fastboot processing
* Invokes reboot systemcall with string option passed from the command line
* Options available are:
* - bootloader: Reboots to fastboot mode (Default)
* - edl: Reboots to emergency download mode for firmware programming
* - recovery: Reboots to recovery mode for applying system updates
*****************************************************************************/
int main(int argc, char *argv[])
{
	int ret = -1;
    int c;
    char *mode = NULL;
    char *bootloader = "bootloader";

    while ((c = getopt (argc, argv, "h")) != -1) {
        switch (c) {
            case 'h':
                print_usage(argv[0]);
                return 0;
                break;
        }
    }

	if (argc < 2) {
		mode = bootloader;
	} else if (strcmp(argv[1], "bootloader") == 0 ||
               strcmp(argv[1], "recovery") == 0 ||
               strcmp(argv[1], "edl") == 0) {
        mode = argv[1];
	} else {
        print_usage(argv[0]);
        return -EINVAL;
    }

	printf("Rebooting to %s mode..\n", mode);
	sleep(1);
	ret = __rfastboot(LINUX_REBOOT_MAGIC1, LINUX_REBOOT_MAGIC2,
			  LINUX_REBOOT_CMD_RESTART2, mode);

	if(ret)
		printf("ERROR: Reboot failed errno(%d)\n", errno);

	return ret;
}

/*****************************************************************************
* Callback routine if reboot fails.
* Set errno
*****************************************************************************/
int __cb_reboot_failed(int n)
{
    errno = n;
    return -1;
}

