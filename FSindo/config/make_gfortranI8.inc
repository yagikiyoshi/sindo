
# Fortran77 compiler & option with and without optimization
 F77C = gfortran
 F77OPT= -fdefault-integer-8 -O2 -funroll-loops -fomit-frame-pointer
 F77NOOPT= -fdefault-integer-8 -O0

# Fortran90 compiler & option with and without optimization
 F90C = gfortran
 F90OPT= -fdefault-integer-8 -O2 -funroll-loops -fomit-frame-pointer
# F90OPT= -O0 -fdefault-integer-8 -fcheck=all
 F90NOOPT= -fdefault-integer-8 -O0

# Linker and link flags
 LINKER = gfortran
 LINKFLAG =

# Archiver and flags
 AR = ar
 ARFLAG = -rv

# Fortran preprocess options
 FPP =

