
# Fortran77 compiler & option with and without optimization
 F77C = xlf
 F77OPT= -q64 -qintsize=8 -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O -qfloat=rsqrt:fltint -qunroll
 F77NOOPT= -q64 -qintsize=8 -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O

# Fortran90 compiler & option with and without optimization
 F90C = xlf90
 F90OPT= -q64 -qintsize=8 -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O -qfloat=rsqrt:fltint -qunroll -qsuffix=f=f90
 F90NOOPT= -q64 -qintsize=8 -qEXTNAME -qmaxmem=-1 -qnosave -qalign=4k -O -qsuffix=f=f90

# Linker and link flags
 LINKER = xlf90
 LINKFLAG = -q64

# Archiver and flags
 AR = ar
 ARFLAG = -rv -X64

# Fortran preprocess options
 FPP =

