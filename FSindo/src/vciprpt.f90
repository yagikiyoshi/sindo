!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/05/01
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Vciprpt_mod

   USE Prpt_mod

   ! INPUT Parameters
   !    Nst  :: Number of VCI states
   Integer :: Nst

   Real(8), allocatable :: Pmat(:,:,:)

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vciprpt_readInput()

   USE Vciprpt_mod
   USE Constants_mod

   Implicit None

   Integer :: Nstate

   Namelist /prptvci/Nstate

      Nstate=1
      Rewind(inp)
      Read(inp,prptvci,end=10)
   10 Continue

      write(iout,100)
  100 Format(6x,'o VCI PROPERTY OPTIONS')

      Nst=Nstate
      if (Nst > 1) then
         write(iout,110) Nstate
      else
         write(iout,120) 
      end if
  110 Format(9x,'VCI_STATES       = ',i12,/)
  120 Format(9x,'VCI_STATES       =  ALL',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
! 
   Subroutine Vciprpt_setup()

   USE Vciprpt_mod
   USE Grid_surface_mod, only : nData
   USE Vci_mod
   USE Constants_mod

   Implicit None

   Integer :: cp
   Integer, allocatable :: mij(:),vij(:)
   Integer :: iCnf(Nfree),jCnf(Nfree)

   Integer :: i,j,mode(1)
   Real(8) :: mat(nData)

      Call Vciprpt_readInput()

      Call Vci_read()
      if(Nst > Nstate) then
         write(Iout,'(6x,''WARNING: # OF STATES IN INPUT IS LARGER THAN '', &
                         ''THAT IN VCI DATA FILE'')')
         write(Iout,'(6x,''WARNING:  INPUT = '',i5)') Nst
         write(Iout,'(6x,''WARNING:  VCI STATES = '',i5)') Nstate
         write(Iout,'(6x,''WARNING:  # OF STATES IS RESET'')') 
         Nst = Nstate

      else if(Nst == 1) then
         Nst = Nstate

      endif

      Call Mem_alloc(-1,i,'D',nCI*nCI*nData)
      Allocate(Pmat(nCI,nCI,nData))

      Allocate(mij(nCUP*2),vij(nCUP*2))

      iCnf=0
      jCnf=0

      Do i=1,nCI
         Do cp=1,lcup(i)
            iCnf(mm(cp,i))=vv(cp,i)
         End do

         Do j=1,i-1
            Do cp=1,lcup(j)
               jCnf(mm(cp,j))=vv(cp,j)
            End do

            Call mvMinus(lcup(i),mm(:,i),vv(:,i),lcup(j),mm(:,j),vv(:,j), &
                         cp,mij,vij)

            Call Pmat_getMatrix(cp,mij,iCnf,jCnf,mat)
            Pmat(i,j,:)=mat
            Pmat(j,i,:)=mat

            Do cp=1,lcup(j)
               jCnf(mm(cp,j))=0
            End do
         End do

         Call Pmat_getMatrix(0,mode,iCnf,iCnf,mat)
         Pmat(i,i,:)=mat

         Do cp=1,lcup(i)
            iCnf(mm(cp,i))=0
         End do

      End do
 
      Deallocate(mij,vij)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
! 
   Subroutine Vciprpt_finalize()

   USE Vciprpt_mod

   Implicit None

      Call Vci_destruct()

      Call Mem_dealloc('D',size(Pmat))
      Deallocate(Pmat)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
! 
   Subroutine Vciprpt_calcPrptMatrix(idPrpt)

   USE Vciprpt_mod
   USE Grid_surface_mod, only : nData
   USE Vci_mod
   USE Constants_mod

   Implicit None

   Integer :: idPrpt

   Real(8), allocatable :: pcMat(:,:),pmatVCI(:,:,:)
   Real(8), allocatable :: pcVec(:),pvecVCI(:,:)

   Integer :: ifl
   Character(30) :: matrixDataFile

   Integer :: i,j,ii,ss
   Real(8) :: ddot

      Call Vciprpt_setup()

      matrixDataFile = 'vci-prptMatrix'//trim(extn(idPrpt))
      Call file_indicator(30,ifl)
      Open(ifl,file=matrixDataFile,status='unknown',form='FORMATTED')

      if(matrix(idPrpt)>0) then

         Call Mem_alloc(-1,i,'D',nCI*Nst)
         Call Mem_alloc(-1,i,'D',Nst*Nst*nData)
         Allocate(pcMat(nCI,Nst),pmatVCI(Nst,Nst,nData))

         Do ss=1,nData
            Call dgemm('N','N',nCI,Nst,nCI,          &
                       1.0D+00,Pmat(:,:,ss),nCI,     &
                               CIwfn,nCI,            &
                       0.0D+00,pcMat,nCI)
            Call dgemm('T','N',Nst,Nst,nCI,          &
                       1.0D+00,CIwfn,nCI,            &
                               pcMat,nCI,            &
                       0.0D+00,pmatVCI(:,:,ss),Nst)
         End do
         Do i=1,Nst
         Do j=1,i
            write(ifl,'(2i5)') i,j
            write(ifl,'(i5)') nData
            write(ifl,'(5e17.8)') pmatVCI(j,i,:)
         End do
         End do

         Call Mem_dealloc('D',size(pcMat))
         Call Mem_dealloc('D',size(pmatVCI))
         Deallocate(pcMat,pmatVCI)

      else

         Call Mem_alloc(-1,i,'D',Nst*nData+nCI)
         Allocate(pcVec(nCI),pvecVCI(Nst,nData))

         Do ss=1,nData
            Do i=1,Nst
               ii=(i-1)*nCI
               Call dgemv('N',nCI,nCI, &
                          1.0D+00,Pmat(:,:,ss),nCI, CIwfn(ii+1:ii+nCI),1, &
                          0.0D+00,pcVec,1)
               pvecVCI(i,ss)=ddot(nCI,CIwfn(ii+1:ii+nCI),1,pcVec,1)
            End do
         End do

         Do i=1,Nst
            write(ifl,'(2i5)') i,i
            write(ifl,'(i5)') nData
            write(ifl,'(5e17.8)') pvecVCI(i,:)
         End do

         Call Mem_dealloc('D',size(pcVec))
         Call Mem_dealloc('D',size(pvecVCI))
         Deallocate(pcVec,pvecVCI)

      endif

      Close(ifl)

      Call Vciprpt_finalize()

      write(iout,100) trim(matrixDataFile)
  100 Format(6x,'-> DONE FOR VCI PROPERTY : [ ',a,' ]',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
! 
   Subroutine Vciprpt_infrared()

   USE Vciprpt_mod
   USE Grid_surface_mod, only : nData
   USE Constants_mod
   USE Vci_mod
   USE IR_mod

   Implicit None

   Integer :: ifl
   Character(11) :: IRdataFile
   Character(15) :: IRspectrumFile

   Integer :: ii,ss,cp
   Real(8) :: mat(nData),ddot
   Real(8), allocatable :: pcVec(:)

   Integer, allocatable :: Lbl(:)
   Real(8), allocatable :: Wi(:)

   Real(8) :: PI,mufi,dE
   Integer :: i,j

      PI = Acos(-1.0D+00)

      Call Vciprpt_setup()

      Allocate(pcVec(nCI))
      Allocate(Wi(nCI),Lbl(nCI))

      Call file_indicator(30,ifl)
      IRdataFile = 'vci-IR.data'
      Open(ifl,file=IRdataFile,status='unknown',form='FORMATTED')

      write(ifl,'(''     Omega (cm-1)     IR (km mol-1)   Coeff.  Weight      Config.'')')
      Do i=2,Nst
         dE = (CIene(i)-CIene(1))*H2wvn
         if(dE < minOmega) cycle
         if(maxOmega > 0.0D+00 .and. dE > maxOmega) exit

         ii=(i-1)*nCI
         Do ss=1,nData
               Call dgemv('N',nCI,nCI, &
                          1.0D+00,Pmat(:,:,ss),nCI, CIwfn(ii+1:ii+nCI),1, &
                          0.0D+00,pcVec,1)
               mat(ss)=ddot(nCI,CIwfn(1:nCI),1,pcVec,1)
         End do

         mufi=0.D+00
         Do ss=1,nData
            mufi=mufi + mat(ss)*mat(ss)
         End do
         mufi=2.0D+00/3.D+00*PI*(CIene(i)-CIene(1)) &
              /vlight/vlight*B2A*1d-13*avogadro*mufi

         Do j=1,nCI
            Wi(j)=CIwfn(ii+j)*CIwfn(ii+j)
         End do
         Call sort(nCI,Lbl,Wi)

         write(ifl,'(f15.4,e20.6,3x,f6.3,2x,f6.3,6x,10(i3,''_'',i1,2x))') &
                 dE,mufi,CIwfn(ii+Lbl(1)),Wi(1), &
                 (mm(cp,Lbl(1)),vv(cp,Lbl(1)),cp=1,lcup(Lbl(1)))

      End do

      Close(ifl)

      Deallocate(Lbl,Wi)
      Deallocate(pcVec)

      IRspectrumFile = 'vci-IR.spectrum'
      Call IR_genSpectrum(IRdataFile,IRspectrumFile)

      Call Vciprpt_finalize()

      write(iout,100) 
  100 Format(6x,'-> DONE FOR VCI SPECTRUM : [ vci-IR.data / vci-IR.spectrum ]',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
! 
   Subroutine Vciprpt_getMatrix(ii,jj,mat)

   USE Vciprpt_mod
   USE Grid_surface_mod, only : nData
   USE Vci_mod

   Implicit None

   Integer :: ii,jj
   Real(8) :: mat(nData)

   Integer :: ist,jst,i,j,k1,k2
   Real(8) :: wgt
   Integer :: cp,lbl1(Nfree),lbl2(Nfree),m12(nCUP*2),v12(nCUP*2)
   Real(8) :: mkk(nData)

      ist=(ii-1)*nCI
      jst=(jj-1)*nCI

      mat=0.D+00

      Do k1=1,nCI
         wgt=CIwfn(k1+ist)*CIwfn(k1+jst)
         !if(abs(wgt)<thresh_vciweight) cycle

         Call getLabel(k1,lbl1)
         Call Pmat_getMatrix(0,m12,lbl1,lbl1,mkk)
         mat=mat + mkk*wgt
      End do

      Do k1=1,nCI
      Do k2=1,k1-1
         wgt=CIwfn(k1+ist)*CIwfn(k2+jst)+CIwfn(k2+ist)*CIwfn(k1+jst)
         !if(abs(wgt)*0.5D+00<thresh_vciweight) cycle

         Call getLabel(k1,lbl1)
         Call getLabel(k2,lbl2)

         Call mvMinus(lcup(k1),mm(1:lcup(k1),k1),vv(1:lcup(k1),k1), &
                      lcup(k2),mm(1:lcup(k2),k2),vv(1:lcup(k2),k2), &
                      cp,m12,v12)

         Call Pmat_getMatrix(cp,m12,lbl1,lbl2,mkk)
         mat=mat + mkk*wgt

      End do
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
