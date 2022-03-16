!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/04/29
!   Copyright 2013 
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscfprpt_calcPrptMatrix(idPrpt)

   USE Prpt_mod
   USE Grid_surface_mod, only : nData
   USE Constants_mod

   Implicit None

   Integer :: idPrpt
   Integer :: iCnf(Nfree),jCnf(Nfree)
   Real(8) :: mat(nData)

   Integer :: ifl
   Character(30) :: vscfWfn,matrixDataFile

   Integer :: Nstate,Target_getNstate
   Integer :: maxCup,Target_getMaxCup,cp,ci,cj
   Integer, allocatable :: mi(:),vi(:),mj(:),vj(:),m12(:),v12(:)

   Integer :: i,j,m,mm(1)

      ! The property calc. is designed for zero-point and virtual VSCF
      vscfWfn='vscf-000.wfn'
      Call Target_read(vscfWfn)

      Nstate=Target_getNstate()
      maxCup=Target_getMaxCup()
      Allocate(mi(maxCup),vi(maxCup))
      Allocate(mj(maxCup),vj(maxCup))
      Allocate(m12(maxCup),v12(maxCup))

      matrixDataFile = 'vscf-prptMatrix'//trim(extn(idPrpt))
      Call file_indicator(30,ifl)
      Open(ifl,file=matrixDataFile,status='unknown',form='FORMATTED')

      iCnf=0
      jCnf=0
      mat=0.D+00
      mm=0
      if(matrix(idPrpt)>0) then
         ! i=0, j=0
         Call Pmat_getMatrix(0,mm,iCnf,iCnf,mat)
         write(ifl,'(2i5)') 1,1
         write(ifl,'(i5)') nData
         write(ifl,'(5e17.8)') mat

         Do i=1,Nstate
            Call Target_getConf(i,ci,mi,vi)
            Do m=1,ci
               iCnf(mi(m))=vi(m)
            End do

            ! j=0
            Call Pmat_getMatrix(ci,mi,iCnf,jCnf,mat)
            write(ifl,'(2i5)') i+1,1
            write(ifl,'(i5)') nData
            write(ifl,'(5e17.8)') mat

            Do j=1,i-1
               Call Target_getConf(j,cj,mj,vj)
               Do m=1,cj
                  jCnf(mj(m))=vj(m)
               End do

               Call mvMinus(ci,mi,vi,cj,mj,vj,cp,m12,v12)

               Call Pmat_getMatrix(cp,m12,iCnf,jCnf,mat)
               write(ifl,'(2i5)') i+1,j+1
               write(ifl,'(i5)') nData
               write(ifl,'(5e17.8)') mat

               Do m=1,cj
                  jCnf(mj(m))=0
               End do
            End do

            ! j=i
            Call Pmat_getMatrix(0,mm,iCnf,iCnf,mat)
            write(ifl,'(2i5)') i+1,i+1
            write(ifl,'(i5)') nData
            write(ifl,'(5e17.8)') mat

            Do m=1,ci
               iCnf(mi(m))=0
            End do

         End do

      else
         ! i=0, j=0
         Call Pmat_getMatrix(0,mm,iCnf,iCnf,mat)
         write(ifl,'(2i5)') 1,1
         write(ifl,'(i5)') nData
         write(ifl,'(5e17.8)') mat

         Do i=1,Nstate
            Call Target_getConf(i,ci,mi,vi)
            Do m=1,ci
               iCnf(mi(m))=vi(m)
            End do

            Call Pmat_getMatrix(0,mm,iCnf,iCnf,mat)
            write(ifl,'(2i5)') i+1,i+1
            write(ifl,'(i5)') nData
            write(ifl,'(5e17.8)') mat

            Do m=1,ci
               iCnf(mi(m))=0
            End do

         End do

      endif

      Close(ifl)

      Deallocate(mi,vi)
      Deallocate(mj,vj)
      Deallocate(m12,v12)

      write(iout,100) trim(matrixDataFile)
  100 Format(6x,'-> DONE FOR VSCF PROPERTY : [ ',a,' ]',/)

      Call Target_destruct()

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
! 
   Subroutine Vscfprpt_infrared()

   USE Prpt_mod
   USE Grid_surface_mod, only : nData
   USE Constants_mod
   USE IR_mod

   Implicit None

   Integer :: ifl
   Character(12) :: IRdataFile
   Character(16) :: IRspectrumFile
   Character(30) :: vscfWfn

   Integer :: Nstate,Target_getNstate
   Integer :: maxCup,Target_getMaxCup,cp,ci,cj
   Integer, allocatable :: mi(:),vi(:)
   Real(8), allocatable :: Evscf(:)
   Character(120) :: line

   Integer :: iCnf(Nfree),zCnf(Nfree)
   Real(8) :: mat(nData)

   Real(8) :: PI,mufi,dE

   Integer :: i,j,k,l,m

      PI = Acos(-1.0D+00)

      ! The IR calc. is designed for zero-point and virtual VSCF
      vscfWfn='vscf-000.wfn'
      Call Target_read(vscfWfn)
      Nstate=Target_getNstate()
      maxCup=Target_getMaxCup()
      Allocate(mi(maxCup),vi(maxCup))

      Allocate(Evscf(0:Nstate))
      
      Call file_indicator(30,ifl)
      Open(ifl,file=vscfWfn,status='old',form='FORMATTED')
      Read(ifl,*)
      Read(ifl,*)
      Read(ifl,*)
      Read(ifl,*) Evscf(0)

      line=''
      Do while(index(line,'VIRTUAL VSCF ENERGIES')==0) 
         Read(ifl,'(a)') line
      end do
      Read(ifl,*)
      Read(ifl,*) Evscf(1:Nstate)
      Close(ifl)

      zCnf=0

      Call file_indicator(30,ifl)
      IRdataFile = 'vscf-IR.data'
      Open(ifl,file=IRdataFile,status='unknown',form='FORMATTED')
      write(ifl,'(''     Omega (cm-1)     IR (km mol-1)      Config.'')')

      iCnf=0
      Do i=1,Nstate

         dE = (Evscf(i)-Evscf(0))*H2wvn
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

      IRspectrumFile = 'vscf-IR.spectrum'
      Call IR_genSpectrum(IRdataFile,IRspectrumFile)

      Call Target_destruct()

      Deallocate(mi,vi,Evscf)

      write(iout,100) 
  100 Format(6x,'-> DONE FOR VSCF SPECTRUM : [ vscf-IR.data / vscf-IR.spectrum ]',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
