
include ../../src/make.inc

LIB= ../../../lib/libsindo_$(TARGET).a
MOD= ../../src

all: main.o
	$(LINKER) $(LINKFLAG) -o modaltest.exe main.o $(LIB) $(LAPACK)

clean:
	$(RM) *.o *.mod modaltest.exe

