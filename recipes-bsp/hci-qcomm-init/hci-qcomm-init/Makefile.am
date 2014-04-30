#bin_SCRIPTS = scripts/bt.init.sh

bin_PROGRAMS = hci-qcomm-init btnvtool

btnvtool_SOURCES = btnvtool.cpp bt_nv.cpp

btnvtool_CFLAGS = -static -g
btnvtool_LDFLAGS = -lrt

hci_qcomm_init_SOURCES = \
	bthci_qcomm_linux.cpp \
	bthci_qcomm_linux_uart.c \
	bthci_qcomm_common.c \
	btqsocnvmplatform_linux.c \
	btqsocnvm.c \
	btqsocnvmefsmode.c \
	btqsocnvmtags.c \
	btqsocnvmprsr.c \
	btqsocnvmutils.c \
	bt_qsoc_nvm_BTS4020_BDB0_19P2Mhz.c \
	bt_qsoc_nvm_BTS4020_BDB0_32Mhz.c \
	bt_qsoc_nvm_BTS4020_BDB1_19P2Mhz.c \
	bt_qsoc_nvm_BTS4020_BDB1_32Mhz.c \
	bt_qsoc_nvm_BTS4020_R3_19P2Mhz.c \
	bt_qsoc_nvm_BTS4020_R3_32Mhz.c \
	bt_qsoc_nvm_BTS4021_B1_19P2Mhz.c \
	bt_qsoc_nvm_BTS4021_B1_32Mhz.c \
	bt_qsoc_nvm_BTS4025_B0_19P2Mhz.c \
	bt_qsoc_nvm_BTS4025_B0_32Mhz.c \
	bt_qsoc_nvm_BTS4025_B1_19P2Mhz.c \
	bt_qsoc_nvm_BTS4025_B1_32Mhz.c \
	bt_qsoc_nvm_BTS4025_B2_19P2Mhz.c \
	bt_qsoc_nvm_BTS4025_B2_32Mhz.c \
	bt_qsoc_nvm_BTS4025_B3_19P2Mhz.c \
	bt_qsoc_nvm_BTS4025_B3_32Mhz.c \
	bt_qsoc_nvm_MARIMBA_A0.c \
	bt_qsoc_nvm_MARIMBA_B0.c \
	bt_qsoc_nvm_MARIMBA_B1.c \
	bt_qsoc_nvm_BAHAMA_A0.c \
	bt_qsoc_nvm_BAHAMA_B0.c \
	bt_qsoc_nvm_BAHAMA_B1.c 

hci_qcomm_init_CFLAGS = -static -g -Wall -Wundef
hci_qcomm_init_CFLAGS += -Wstrict-prototypes -Wno-trigraphs

hci_qcomm_init_CXXFLAGS = -fpermissive
hci_qcomm_init_CPPFLAGS = -I $(srcdir)/hci-qcomm-init \
	-D__packed__= \
	-DIMAGE_APPS_PROC \
	-DFEATURE_Q_SINGLE_LINK \
	-DFEATURE_Q_NO_SELF_QPTR \
	-DFEATURE_LINUX \
	-DFEATURE_NATIVELINUX \
	-DFEATURE_DSM_DUP_ITEMS \
	-DFEATURE_BT_QSOC \
	-DFEATURE_BT_QSOC_SLEEP \
	-DFEATURE_BT_QSOC_NVM_EFS_MODE \
	-DFEATURE_BT_QSOC_BTS4020_BDB0 \
	-DFEATURE_BT_QSOC_BTS4020_BDB1 \
	-DFEATURE_BT_QSOC_BTS4020_R3 \
	-DFEATURE_BT_QSOC_BTS4021_B1 \
	-DFEATURE_BT_QSOC_BTS4025_B0 \
	-DFEATURE_BT_QSOC_BTS4025_B1 \
	-DFEATURE_BT_QSOC_BTS4025_B2 \
	-DFEATURE_BT_QSOC_BTS4025_B3 \
	-DFEATURE_BT_QSOC_MARIMBA_A0 \
	-DFEATURE_BT_QSOC_MARIMBA_B0 \
	-DFEATURE_BT_QSOC_MARIMBA_B1 \
	-DFEATURE_BT_QSOC_BAHAMA_A0 \
	-DFEATURE_BT_QSOC_BAHAMA_B0 \
	-DFEATURE_BT_QSOC_BAHAMA_B1 \
	-DBT_QSOC_HCI_DEVICE="/dev/ttyS0" \
	-DBT_QSOC_HCI_DEVICE=115200 \
	-DBT_QSOC_REF_CLOCK=32000000

# Enable SW in band sleep when qualcomm-ibs device type is part
# of hciattach.c
hci_qcomm_init_CPPFLAGS += \
	-DFEATURE_BT_QSOC_CLASS2 \
	-DPLATFORM_LINUX_PC \
	-DFEATURE_BT_SYSTEM_CLOCK_XO_SUPPORT \
	-DFEATURE_BT_QSOC_SW_INBAND_SLEEP \
	-DBT_NVM_MBA_B0_LOWER_TX_POWER \
	-DBT_QSOC_ENABLE_CLOCK_SHARING \
	-DBT_QSOC_HCI_BAUD_RATE=3000000 \
	-DBT_QSOC_REF_CLOCK=19200000

#BT_QSOC_REF_CLOCK = 32000000
#BT_QSOC_HCI_DEVICE = /dev/ttyS0
#BT_QSOC_HCI_BAUD_RATE = 115200

hci_qcomm_init_LDFLAGS = -lrt -lpthread

