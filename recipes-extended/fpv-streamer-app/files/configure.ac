AC_PREREQ(2.61)

AC_INIT([fpv_streamer_app],1.0)

AM_INIT_AUTOMAKE([subdir-objects])
AC_CONFIG_SRCDIR([src/fpv_streamer_app.cpp])

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

# Checks for typedefs, structures, and compiler characteristics.
AC_TYPE_SIZE_T
AC_TYPE_SSIZE_T
AC_TYPE_UINT16_T
AC_TYPE_UINT32_T
AC_TYPE_UINT64_T
AC_TYPE_UINT8_T


# Checks for library functions.
AC_SUBST(SYSROOT_USR_INCLUDE)

AC_CHECK_TOOL(OBJCOPY, objcopy, false)
AC_CONFIG_FILES([Makefile])
AC_OUTPUT

