
include ../../src/make.inc

LIB= ../../lib/libsindo_$(TARGET).a
MOD= ../../src
EXE= mrpes

all: main.o
	$(LINKER) $(LINKFLAG) -o $(EXE) main.o $(LIB) $(LAPACK)

clean:
	$(RM) *.o $(EXE)

