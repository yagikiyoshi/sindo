!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Mol_mod

   ! Nat  : The number of atoms
   ! Nat3 : The number of atoms * 3
   ! Nfree: The number of internal degrees of freedom, i.e. Nat3-6(or Nat3-5)
   ! Zmass: The mass of each atom
   ! xin  : Input structure (Angs)
   ! Freq : Harmonic Frequencies at xin (cm-1)
   ! CL   : Normal displacement vector at xin

   Integer :: Nat,Nat3,Nfree
   Real(8), dimension(:), allocatable :: Zmass,xin,Freq
   Real(8), dimension(:,:), allocatable :: CL

   ! isNMA: True when NMA module is allocated
   Logical :: isNMA

   ! minfoFile: The name of the minfo file
   Character(80) :: minfoFile

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Mol_construct()

   USE Constants_mod
   USE Mol_mod

   Implicit None

   Integer :: i,j,k
   Real(8) :: mass(MaxNat),x(3*MaxNat),omega(MaxNfree),L(3*MaxNat*MaxNfree)
   Logical :: ex,au

   Namelist /mol/Nat,mass,x,omega,L,minfoFile

      !  >> Read &mol
      !  >> initialize
      Nat=0; Nfree=0
      mass=0.D+00
      x=0.D+00
      omega=0.D+00
      L=0.D+00
      minfoFile=''
      au=.false.

      Rewind(Inp)
      Read(Inp,mol,end=10)
   10 Continue

      Write(Iout,100)
  100 Format(/,'(  SETUP MOL MODULE  )',/)

      if(len_trim(minfoFile)>0) then
         Inquire(file=minfoFile,Exist=ex)
         if(ex) then
            write(Iout,110) trim(minfoFile)
  110       Format(/,3x,'READ DATA FROM A MINFO FILE : [ ',a,' ]'//)

            Call ReadMinfo(minfoFile,Nat,Nfree,x,mass,omega,L)
            au=.true.

         else
            Write(Iout,*) 'ERROR IN MOL_CONSTRUCT'
            Write(Iout,*) 'ERROR:: MINFOFILE = ',trim(minfoFile)
            Write(Iout,*) 'ERROR:: IS NOT FOUND!!'
            Stop
         endif

      endif

      if(Nat>MaxNat) then 
         Write(Iout,*) 'ERROR IN MOL_CONSTRUCT'
         Write(Iout,*) 'ERROR:: LIMITATION EXCEEDED'
         Write(Iout,*) 'ERROR:: NAT =',Nat
         Write(Iout,*) 'ERROR:: MAX NAT =',MaxNat
         Stop
      endif
      Nat3=Nat*3
      if(Nfree==0) Nfree=Nat3-6

      if(mass(1)/=0.D+00) then
         if(Nat==0) then 
            Write(Iout,*) 'ERROR IN MOL_CONSTRUCT'
            Write(Iout,*) 'ERROR::  "MASS" REQUIRES NAT FOR INPUT.'
            Write(Iout,*) 'ERROR::  >   NAT=',Nat
            Stop
         else
            if(.not. au) then
               Do i=1,Nat
                  mass(i)=mass(i)*elmass
               End do
            endif
            Call Mem_alloc(-1,i,'D',Nat)
            Allocate(Zmass(Nat))
            Zmass=mass(1:Nat)
         endif
      endif

      if(any(x/=0.D+00)) then
         if(Nat==0) then 
            Write(Iout,*) 'ERROR IN MOL_CONSTRUCT'
            Write(Iout,*) 'ERROR::  "X" REQUIRES NAT FOR INPUT.'
            Write(Iout,*) 'ERROR::  >   NAT=',Nat
            Stop
         else
            if(.not. au) then
               Do i=1,Nat3
                  x(i)=x(i)/B2A
               End do
            endif
            Call Mem_alloc(-1,i,'D',Nat3)
            Allocate(xin(Nat3))
            xin=x(1:Nat3)
         endif
      endif

      if(any(omega/=0.D+00)) then
         Call Mem_alloc(-1,i,'D',Nfree)
         Allocate(Freq(Nfree))
         Freq=omega(1:Nfree)
      endif

      if(any(L/=0.D+00)) then
         if(Nat==0) then 
            Write(Iout,*) 'ERROR IN MOL_CONSTRUCT'
            Write(Iout,*) 'ERROR::  "L" REQUIRES NAT AND NFREE FOR INPUT.'
            Write(Iout,*) 'ERROR::  >   NAT=',Nat
            Write(Iout,*) 'ERROR::  > NFREE=',Nfree
            Stop
         else
            Call Mem_alloc(-1,i,'D',Nat3*Nfree)
            Allocate(CL(Nat3,Nfree))
            k=1
            Do i=1,Nfree
            Do j=1,Nat3
               CL(j,i)=L(k)
               k=k+1
            End do
            End do

         endif
      endif

      isNMA=.false.
      if(allocated(Zmass) .and. allocated(xin) .and. allocated(CL)) then
         Call nma_Construct(Nat,Nfree,Zmass,xin,CL)
         Call nma_Print(Iout)
         isNMA=.true.
      endif

      return


   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_destruct

   USE Constants_mod
   USE Mol_mod

      if(allocated(Zmass) .and. allocated(xin) .and. allocated(CL)) then
         Call nma_Destruct(Iout)
         isNMA=.false.
      endif

      if(allocated(Zmass)) then
         Call Mem_dealloc('D',size(Zmass))
         Deallocate(Zmass)
      endif
      if(allocated(xin)) then 
         Call Mem_dealloc('D',size(xin))
         Deallocate(xin)
      endif
      if(allocated(Freq)) then 
         Call Mem_dealloc('D',size(Freq))
         Deallocate(Freq)
      endif
      if(allocated(CL)) then 
         Call Mem_dealloc('D',size(CL))
         Deallocate(CL)
      endif


      write(Iout,100)
  100 Format(/,'(  FINALIZE MOL MODULE  )',//)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_getNat(NNat)

   USE Mol_mod

   Implicit None
   Integer :: NNat

      NNat=Nat

    return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_getNfree(NNfree)

   USE Mol_mod

   Implicit None
   Integer :: NNfree

      NNfree=Nfree

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_getMass(M)

   USE Mol_mod

   Implicit None
   Real(8), dimension(Nat) :: M

      M = Zmass

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_getxin(x)

   USE Mol_mod

   Implicit None
   Real(8), dimension(Nat3) :: x

      x=xin

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_getFreq(F)

   USE Mol_mod

   Implicit None
   Real(8), dimension(Nfree) :: F

      F=Freq

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Mol_getL(L)

   USE Mol_mod

   Implicit None
   Real(8), dimension(Nat3,Nfree) :: L

      L = CL

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Mol_isNMA()

   USE Mol_mod

   Implicit None
   Logical :: Mol_isNMA

      Mol_isNMA = isNMA

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Mol_getMinfoFile(Mol_minfoFile)

   USE Mol_mod

   Implicit None

   Character(80) :: Mol_minfoFile

      Mol_minfoFile = minfoFile

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
