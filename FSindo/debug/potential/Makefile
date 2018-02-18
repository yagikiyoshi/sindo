
include ../../src/make.inc

LIB= ../../../lib/libsindo_$(TARGET).a
MOD= ../../src

all: main.o
	$(LINKER) $(LINKFLAG) -o pestest.exe main.o $(LIB) $(LAPACK)

clean:
	$(RM) *.o *.mod pestest.exe

