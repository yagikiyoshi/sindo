!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/05/01
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vptprpt_infrared()

   USE Prpt_mod
   USE Grid_surface_mod, only : nData
   USE Constants_mod
   USE IR_mod

   Implicit None

   Integer :: ifl
   Character(11) :: IRdataFile
   Character(15) :: IRspectrumFile
   Character(30) :: vmpWfn,matrixDataFile

   Integer :: Nstate,Target_getNstate
   Integer :: maxCup,Target_getMaxCup,cp,ci,cj
   Integer, allocatable :: mi(:),vi(:)
   Real(8), allocatable :: Evmp(:)
   Character(120) :: line

   Integer :: iCnf(Nfree),zCnf(Nfree)
   Real(8) :: mat(nData)

   Real(8) :: PI,mufi,dE

   Integer :: i,j,k,l,m
   Real(8) :: E0,E1

      PI = Acos(-1.0D+00)

      ! The IR calc. is designed for zero-point and virtual VSCF
      vmpWfn='vmp-w.wfn'
      Call Target_read(vmpWfn)
      Nstate=Target_getNstate()
      maxCup=Target_getMaxCup()
      Allocate(mi(maxCup),vi(maxCup))

      Allocate(Evmp(0:Nstate))
      
      Call file_indicator(30,ifl)
      Open(ifl,file=vmpWfn,status='old',form='FORMATTED')
      line=''
      Do while(index(line,'OPTIONS: maxSum, maxEx, nCUP')==0)
         Read(ifl,'(a)') line
      End do
      Read(ifl,*)
      Do i=0,Nstate
         Read(ifl,*)
         Read(ifl,*)
         Read(ifl,*)
         Read(ifl,*) E0,E1,Evmp(i)
      End do
      Close(ifl)

      zCnf=0

      Call file_indicator(30,ifl)
      IRdataFile = 'vmp-IR.data'
      Open(ifl,file=IRdataFile,status='unknown',form='FORMATTED')
      write(ifl,'(''     Omega (cm-1)     IR (km mol-1)      Config.'')')

      iCnf=0
      Do i=1,Nstate

         dE = (Evmp(i)-Evmp(0))*H2wvn
         if(dE < minOmega) cycle
         if(maxOmega > 0.0D+00 .and. dE > maxOmega) cycle

         Call Target_getConf(i,ci,mi,vi)
         Do m=1,ci
            iCnf(mi(m))=vi(m)
         End do

         Call Pmat_getMatrix(ci,mi,iCnf,zCnf,mat)
         mufi=0.D+00
         Do k=1,nData
            mufi=mufi + mat(k)*mat(k)
         End do
         mufi=2.0D+00/3.D+00*PI*dE/H2wvn/vlight/vlight*B2A*1d-13*avogadro*mufi

         write(ifl,'(f15.4,e20.6,5x,10(i3,''_'',i1,2x))') &
                 dE,mufi,(mi(m),vi(m),m=1,ci)

         Do m=1,ci
            iCnf(mi(m))=0
         End do

      End do

      Close(ifl)

      IRspectrumFile = 'vmp-IR.spectrum'
      Call IR_genSpectrum(IRdataFile,IRspectrumFile)

      Call Target_destruct()

      Deallocate(Evmp,mi,vi)

      write(iout,100) 
  100 Format(6x,'-> DONE FOR VMP SPECTRUM : [ vmp-IR.data / vmp-IR.spectrum ]',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
