AC_PREREQ([2.61])

AC_INIT([fpv-streamer-lib],1.0)
AM_INIT_AUTOMAKE([subdir-objects])
AC_CONFIG_SRCDIR([src/fpv_stream.cpp])


AM_MAINTAINER_MODE

AC_CONFIG_HEADERS([config.h])
AC_CONFIG_MACRO_DIR([m4])

# Checks for programs.
AM_PROG_AS
AC_PROG_CC
AC_PROG_LIBTOOL
AC_PROG_CPP
AC_PROG_CXX
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_MAKE_SET

# Checks for libraries.

# Checks for header files.

AC_CHECK_HEADERS([pthread.h])
AC_CHECK_HEADERS([sys/socket.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_TYPE_SIZE_T
AC_TYPE_SSIZE_T
AC_TYPE_UINT16_T
AC_TYPE_UINT32_T
AC_TYPE_UINT64_T
AC_TYPE_UINT8_T


# Checks for library functions.

# Checks for packages

PKG_PROG_PKG_CONFIG

echo "PKG config path: $PKG_CONFIG_PATH"
echo "PKG_CONFIG_SYSROOT_DIR: $PKG_CONFIG_SYSROOT_DIR"

ORIG_PKG_CONFIG=$PKG_CONFIG

#export PKG_CONFIG="$PKG_CONFIG --define-variable=prefix=${PKG_CONFIG_SYSROOT_DIR}/usr"
#echo $PKG_CONFIG

#PKG_CHECK_MODULES([x264], [x264])

AC_SUBST(SYSROOT_USR_LIB)
AC_SUBST(SYSROOT_USR_INCLUDE)

AC_CHECK_TOOL(OBJCOPY, objcopy, false)
AC_CONFIG_FILES([Makefile])
AC_OUTPUT


