
# Fortran77 compiler & option with and without optimization
 F77C = gfortran
 F77OPT= -O2 -funroll-loops -fomit-frame-pointer
 F77NOOPT= -O0

# Fortran90 compiler & option with and without optimization
 F90C = gfortran
 F90OPT= -O2 -funroll-loops -fomit-frame-pointer
# F90OPT= -O0 -fcheck=all
 F90NOOPT= -O0

# Linker and link flags
 LINKER = gfortran
 LINKFLAG =

# Archiver and flags
 AR = ar
 ARFLAG = -rv

# Fortran preprocess options
 FPP =

