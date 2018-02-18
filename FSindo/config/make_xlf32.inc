
# Fortran77 compiler & option with and without optimization
 F77C = xlf
 F77OPT = -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O -qfloat=rsqrt:fltint -qunroll
 F77NOOPT = -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O

# Fortran90 compiler & option with and without optimization
 F90C = xlf90
 F90OPT = -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O -qfloat=rsqrt:fltint -qunroll -qsuffix=f=f90
 F90NOOPT = -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O -qsuffix=f=f90

# Linker and link flags
 LINKER = xlf90
 LINKFLAG =

# Archiver and flags
 AR = ar
 ARFLAG = -rv

# Fortran preprocess options
 FPP =

