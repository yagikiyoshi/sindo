
# Fortran77 compiler & option with and without optimization
 F77C = ifc
 F77OPT= -w -cm -mp1 -O2 -prefetch -unroll 
 F77NOOPT= -w -cm -mp1 -O

# Fortran90 compiler & option with and without optimization
 F90C = ifc
 F90OPT= -w -cm -mp1 -O2 -prefetch -unroll 
 F90NOOPT= -w -cm -mp1 -O

# Linker and link flags
 LINKER = ifc
 LINKFLAG = -Vaxlib

# Archiver and flags
 AR = ar
 ARFLAG = -rv

# Fortran preprocess options
 FPP =

