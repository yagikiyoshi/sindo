
include ../../src/make.inc

LIB= ../../../lib/libsindo_$(TARGET).a
MOD= ../../src

all: main.o
	$(LINKER) $(LINKFLAG) -o memtest.exe main.o $(LIB) $(LAPACK)

clean:
	$(RM) *.o *.mod memtest.exe

