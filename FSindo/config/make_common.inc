 RM = rm 

.f.o:
	$(F77C) $(F77OPT) -c $< -o $@ -I$(SINDO_ROOT)/src
.F.o:
	$(F77C) $(F77OPT) $(FPP) -c $< -o $@ -I$(SINDO_ROOT)/src
.f90.o:
	$(F90C) $(F90OPT) -c $< -o $@ -I$(SINDO_ROOT)/src
.F90.o:
	$(F90C) $(F90OPT) $(FPP) -c $< -o $@ -I$(SINDO_ROOT)/src
 
.SUFFIXES: .f .F .f90 .F90 .o

