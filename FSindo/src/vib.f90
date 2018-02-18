!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/05/01
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Vib_mod

   ! -- (Number of DOF) ----------------------------------------------------
   !     Nfree :: Number of vibrational degrees of freedom
   !     Mfree :: Number of "active" vibrational degrees of freedom

   Integer :: Nfree,Mfree
   Logical, allocatable :: activemodes(:)

   ! -- (RUN OPTION paramters) --------------------------------------------- 
   !     locvscf :: Run oc-VSCF if true. (default=.t.)
   !     lvscf   :: Run VSCF if true. (default=.t.)
   !     lvci    :: Run VCI if true. (default=.f.)
   !     lvpt    :: Run VPT if true. (default=.f.)
   !     lvqdpt  :: Run VQDPT if true. (default=.f.)
   !     lprpt   :: Run Property if true. (default=.f.)

   Logical :: locvscf,lvscf,lvci,lvpt,lvqdpt,lprpt

   ! -- (BASIS FUNCTION parameters) ----------------------------------------
   !     nCHO(Nfree)   :: Num. of contracted HO wfn 
   !     maxCHO        :: Maximum of nCHO
   !     omegaf(Nfree) :: Frequency of the HO wfn (cm-1)
   Integer, dimension(:), allocatable :: nCHO
   Integer :: maxCHO
   Real(8), dimension(:), allocatable :: omegaf

   !     MR      :: Mode Coupling Representation of the PES
   Integer :: MR

   Contains

   Subroutine dumpCHO

   Integer :: wCHO

      Call file_indicator(40,wCHO)
      Open(wCHO,file='cho.basis',status='unknown',form='FORMATTED')
      write(wCHO,*) 'PRIMITIVE HO BASIS FUNCTION'
      write(wCHO,'(i5)') Nfree
      write(wCHO,'(5i17)') nCHO
      write(wCHO,'(i5)') Nfree
      write(wCHO,'(5e17.8)') omegaf
      Close(wCHO)

   End subroutine

   Subroutine readCHO

   Integer :: rCHO,Nf

      Call file_indicator(40,rCHO)
      Open(rCHO,file='cho.basis',status='old',form='FORMATTED')
      read(rCHO,*)
      read(rCHO,*) Nf
      read(rCHO,*) nCHO
      read(rCHO,*) Nf
      read(rCHO,*) omegaf
      Close(rCHO)

   End subroutine

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_construct(NNfree)

   USE Constants_mod
   USE Vib_mod

   Implicit None

   Integer :: NNfree

   Integer :: i,j,k,l,n

   Integer :: vmax_base,vmaxAll,vmaxDefault
   Integer, dimension(MaxNfree) :: vmax
   Logical :: ocvscf,vscf,vci,vpt,vqdpt,prpt
   Logical :: readBasis

   Namelist /vib/Nfree,MR,vmax,vmax_base,vmaxAll,ocvscf,vscf,vci,vpt,vqdpt, &
                 prpt,readBasis

      ! >> Read input

      ! --- DEFAULT ---
      ! - Number of degrees of freedom
      Nfree = -1

      ! - Run option 
      ocvscf=.false. 
      vscf=.false. 
      vci=.false.
      vpt=.false.
      vqdpt=.false.
      prpt=.false.

      ! - Read basis functions from file
      readBasis=.false.

      ! - Mode Coupling 
      MR = 3

      ! - Maximum quanta of basis functions.
      vmax=-2
      vmaxDefault=10
      vmax_base=vmaxDefault
      vmaxAll=vmax_base

      Rewind(inp)
      Read(inp,vib,end=10)
   10 Continue

      if(Nfree<0) Nfree=NNfree
      if(Nfree>MaxNfree) then
         Write(iout,'(''  ERROR: MAXIMUM NUMBER OF MODE IS '',i4)') MaxNfree
         Write(iout,'(''  ERROR: TERMINATED IN VIB_CONSTRUCT '')')
         Stop
      endif

      ! MR=1,2,3,4
      if(MR>4) then
         Write(iout,'(''  ERROR: TERMINATED IN VIB_CONSTRUCT '')')
         Write(iout,*) 'ERROR: '
         Write(iout,*) 'ERROR: SORRY! MR>4 IS NOT READY.'
         Write(iout,*) 'ERROR: MR =',MR
         Write(iout,*) 'ERROR: '
         Stop
      endif

      !  Run option
      locvscf=ocvscf
      lvscf=vscf
      lvci=vci
      lvpt=vpt
      lvqdpt=vqdpt
      lprpt=prpt

      write(iout,100)
      write(iout,110) vscf,ocvscf,vci,vpt,vqdpt,prpt
  100 Format(/,'(  SETUP VIB MODULE  )',/)
  110 Format(3x,'>> RUN OPTIONS',/ &
      6x,'VSCF    =',l6,/, &
      6x,'OC-VSCF =',l6,/, &
      6x,'VCI     =',l6,/, &
      6x,'VPT     =',l6,/, &
      6x,'VQDPT   =',l6,/, &
      6x,'PRPT    =',l6,/)

      !  TODO : We'd better separate out the basis function setup from 
      !         this routine...
      !  Setup of basis function
      Call Mem_alloc(-1,i,'I',Nfree)
      Call Mem_alloc(-1,i,'D',Nfree)
      allocate(nCHO(Nfree),omegaf(Nfree))

      write(iout,120)

      if((.not. lvscf) .and. lprpt) readBasis=.true.
      if(readBasis) then
         Call readCHO()
         write(iout,121)

      else
         if(vmaxAll/=vmaxDefault) vmax_base=vmaxAll
         if(vmax_base==0) vmax_base=-1
         nCHO=vmax_base
         Do i=1,Nfree
            if(vmax(i)==0) then
               nCHO(i)=-1
               cycle
            endif
            if(vmax(i)/=-2) nCHO(i)=vmax(i)
         End do
         nCHO=nCHO+1
  
         Call Mol_getFreq(omegaf)
      endif
      maxCHO=maxVal(nCHO)

      i=mod(Nfree,6)
      j=(Nfree-i)/6
      Do k=1,j
         write(iout,122) (6*(k-1)+l,l=1,6)
         write(iout,123) nCHO(6*(k-1)+1:6*k)-1
         write(iout,124) omegaf(6*(k-1)+1:6*k)
      End do
      if(i/=0) then
         write(iout,122) (6*j+k,k=1,i)
         write(iout,123) nCHO(6*j+1:Nfree)-1
         write(iout,124) omegaf(6*j+1:Nfree)
      endif

  120 Format(/,3x,'>> BASIS FUNCTIONS')
  121 Format(6x,'READ FROM FILE  [ cho.basis ]',/)
  122 Format(6x,'MODE : ',6i9)
  123 Format(6x,'MAXV : ',6i9)
  124 Format(6x,'FREQ : ',6f9.2,/)

      Call Mem_alloc(-1,i,'L',Nfree)
      Allocate(activemodes(Nfree))
      activemodes=.true.

      Mfree=Nfree
      Do i=1,Nfree
         if(nCHO(i)==0) then
            Mfree=Mfree-1
            activemodes(i)=.false.
         endif
      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_destruct()

   USE Constants_mod
   USE Vib_mod

      Call dumpCHO
      Call Mem_dealloc('I',size(nCHO))
      Deallocate(nCHO)
      Call Mem_dealloc('L',size(activemodes))
      Deallocate(activemodes)

      Write(iout,100)
  100 Format(/,'(  FINALIZE VIB MODULE  )',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getNfree()

   USE Vib_mod

   Implicit None

   Integer Vib_getNfree

      Vib_getNfree=Nfree

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getActiveNfree()

   USE Vib_mod

   Implicit None

   Integer Vib_getActiveNfree

      Vib_getActiveNfree=Mfree

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_getActiveModes(activemodes0)

   USE Vib_mod

   Implicit None

   Logical :: activemodes0(Nfree)

      activemodes0 = activemodes

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getMRforPES()

   USE Vib_mod

   Implicit None

   Integer Vib_getMRforPES

      Vib_getMRforPES=MR

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_setnCHO(nCHO0)

   USE Vib_mod

   Implicit None

   Integer :: nCHO0(Nfree)

      nCHO=nCHO0
      maxCHO=maxVal(nCHO)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_getnCHO(nCHO0)

   USE Vib_mod

   Implicit None

   Integer :: nCHO0(Nfree)

      nCHO0=nCHO

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_setFreq(omega)

   USE Vib_mod

   Implicit None

   Real(8) :: omega(Nfree)

      omegaf=omega

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vib_getFreq(omega)

   USE Vib_mod

   Implicit None

   Real(8) :: omega(Nfree)

      omega=omegaf

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getMaxCHO()

   USE Vib_mod

   Implicit None

   Integer Vib_getMaxCHO

      Vib_getMaxCHO=maxCHO

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getlvscf()

   USE Vib_mod

   Implicit None

   Logical Vib_getlvscf

      Vib_getlvscf=lvscf

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getlocvscf()

   USE Vib_mod

   Implicit None

   Logical Vib_getlocvscf

      Vib_getlocvscf=locvscf

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getlvci()

   USE Vib_mod

   Implicit None

   Logical Vib_getlvci

      Vib_getlvci=lvci

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getlvpt()

   USE Vib_mod

   Implicit None

   Logical Vib_getlvpt

      Vib_getlvpt=lvpt

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getlvqdpt()

   USE Vib_mod

   Implicit None

   Logical Vib_getlvqdpt

      Vib_getlvqdpt=lvqdpt

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vib_getlprpt()

   USE Vib_mod

   Implicit None

   Logical Vib_getlprpt

      Vib_getlprpt=lprpt

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
