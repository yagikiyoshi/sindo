
# Fortran77 compiler & option with and without optimization
 F77C = ifort
 F77OPT= -i8 -w -cm -static -O3 -funroll-loops
 F77NOOPT= -w -cm -O0

# Fortran90 compiler & option with and without optimization
 F90C = ifort
 F90OPT= -i8 -w -cm -static -O3 -funroll-loops
# F90OPT= -i8 -w -cm -O0 -check -traceback
 F90NOOPT= -i8 -w -cm -O0

# Linker and link flags
 LINKER = ifort
 LINKFLAG =

# Archiver and flags
 AR = ar
 ARFLAG = -rv

# Fortran preprocess options
 FPP =
