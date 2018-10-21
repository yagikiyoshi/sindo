
include make.inc

MODULES= constants.o
OBJECTS= memory.o \
         molecule.o readMinfo.o nma.o mf.o \
         vib.o prpt.o target.o \
         modal.o dvr.o ho.o choReader.o \
         pes.o pes_qff.o pes_grid.o \
         mrpes.o mrpes_qff.o mrpes_mcs.o mrpes_grid.o getfname.o lag1.o lag2.o lag3.o lagcore.o \
         vscf.o ocvscf_coeff.o optcoord.o ocvscf.o kinmat.o vci.o vpt.o vqdpt.o hmat.o \
         grid_surface.o \
         infrared.o vscfprpt.o vciprpt.o vptprpt.o vqdptprpt.o pmat.o \
         calc_vib.o calc_prpt.o \
         tools.o fse.o

sindo: $(MODULES) $(OBJECTS) main.o
	mkdir -p ../bin
	$(LINKER) $(LINKFLAG) -o ../bin/sindo_$(TARGET) main.o $(OBJECTS) $(LAPACK)
	$(RM) -f ../bin/sindo
	ln -s sindo_$(TARGET) ../bin/sindo
	mkdir -p ../lib
	$(AR) $(ARFLAG) ../lib/libsindo_$(TARGET).a $(OBJECTS)

veryclean: clean
	$(RM) -r make.inc ../bin ../lib

clean:
	$(RM) -f *.o *.mod

