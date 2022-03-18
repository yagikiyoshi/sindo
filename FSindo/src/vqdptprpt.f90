!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/05/07
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Vqdptprpt_mod

   USE Prpt_mod

   Integer :: ngrp,numconf,maxCup

   ! nCnf(ngrp) :: The number of configuration of each group
   Integer, allocatable :: nCnf(:)

   ! cup(nn)
   Integer, allocatable :: cup(:),mm(:,:),vv(:,:)

   Contains

   Subroutine setupConf()

   Implicit None

   Integer :: ifl
   Integer :: cp
   Character(120) :: line

   Integer :: i,j,k

      Call file_indicator(30,ifl)
      Open(ifl,file='vqdpt-w.wfn',status='old')
      line=''
      Do while(index(line,'THE NUMBER OF GROUPS')==0)
         Read(ifl,'(a)') line
      End do
      Read(ifl,*) ngrp

      Call Mem_alloc(-1,i,'I',ngrp)
      Allocate(nCnf(ngrp))
      Read(ifl,*)
      Read(ifl,*) nCnf

      maxCup=0
      Do i=1,ngrp
         line=''
         Do while(index(line,'THE P-SPACE CONFIGURATIONS')==0)
            Read(ifl,'(a)') line
         End do
         Do j=1,nCnf(i)
            Read(ifl,*) cp
            if(cp > maxCup) maxCup=cp
         End do
      End do

      numConf=0
      Do i=1,ngrp
         numConf=numConf + nCnf(i)
      End do

      Call Mem_alloc(-1,i,'I',numConf+maxCup*numConf*2)
      Allocate(cup(numConf),mm(maxCup,numConf),vv(maxCup,numConf))

      Rewind(ifl)
      k=0
      Do i=1,ngrp
         line=''
         Do while(index(line,'THE P-SPACE CONFIGURATIONS')==0)
            Read(ifl,'(a)') line
         End do
         Do j=1,nCnf(i)
            k=k+1
            Read(ifl,*) cup(k),mm(1:cup(k),k),vv(1:cup(k),k)
         End do
      End do
      !dbg k=0
      !dbg Do i=1,ngrp
      !dbg    Do j=1,nCnf(i)
      !dbg       k=k+1
      !dbg       write(6,'(30i3)') cup(k),mm(1:cup(k),k),vv(1:cup(k),k)
      !dbg    End do
      !dbg    write(6,*)
      !dbg End do

      Close(ifl)

   End subroutine

   Subroutine finalz()

      Call Mem_dealloc('I',size(cup))
      Deallocate(cup)
      Call Mem_dealloc('I',size(mm))
      Deallocate(mm)
      Call Mem_dealloc('I',size(vv))
      Deallocate(vv)
      Call Mem_dealloc('I',size(nCnf))
      Deallocate(nCnf)

   End subroutine

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Vqdptprpt_calcPrptmatrix(idPrpt)

   USE Vqdptprpt_mod
   USE Grid_surface_mod, only : nData
   USE Constants_mod

   Implicit None

   Integer :: idPrpt
   Integer :: iCnf(Nfree),jCnf(Nfree)
   Real(8) :: mat(nData)

   Integer :: ifl,jfl
   Character(30)  :: matrixDataFile
   Character(120) :: line

   Integer :: nst,nn,n,cp
   Integer, allocatable :: mij(:),vij(:)
   Real(8), allocatable :: Pmat(:,:,:),coeff(:,:),aa(:,:),bb(:)
   Real(8) :: ddot

   Integer :: i,j,k,l,m,mode(1)

      mode(1)=0

      Call setupConf()
      Allocate(mij(maxCup),vij(maxCup))

      matrixDataFile = 'vqdpt-prptMatrix'//trim(extn(idPrpt))
      Call file_indicator(30,ifl)
      Open(ifl,file=matrixDataFile,status='unknown',form='FORMATTED')

      Call file_indicator(30,jfl)
      Open(jfl,file='vqdpt-w.wfn',status='old')

      iCnf=0; jCnf=0
      Call Pmat_getMatrix(0,mode,iCnf,iCnf,mat)
      write(ifl,'(''GROUP    0'')') 
      write(ifl,'(i5)') nData
      write(ifl,'(5e17.8)') mat

      nst=0
      Do n=1,ngrp
         write(ifl,'(''GROUP '',i4)') n 
         if(nCnf(n)==1) then
            nst=nst+1
            Do i=1,cup(nst)
               iCnf(mm(i,nst))=vv(i,nst)
            End do
            Call Pmat_getMatrix(0,mode,iCnf,iCnf,mat)
            write(ifl,'(i5)') nData
            write(ifl,'(5e17.8)') mat
            Do i=1,cup(nst)
               iCnf(mm(i,nst))=0
            End do

         else
            nn=nCnf(n)
            Allocate(Pmat(nn,nn,nData),coeff(nn,nn))
            line=''
            Do while(index(line,'STATE=')==0)
               Read(jfl,'(a)') line
            End do
            Backspace(jfl)
            Do i=1,nn
               Read(jfl,*)
               Read(jfl,*)
               Read(jfl,*)
               Read(jfl,*)
               Read(jfl,*) coeff(:,i)
            End do

            Do i=1,nn
               Do k=1,cup(nst+i)
                  iCnf(mm(k,nst+i))=vv(k,nst+i)
               End do

               Do j=1,i-1
                  Do k=1,cup(nst+j)
                     jCnf(mm(k,nst+j))=vv(k,nst+j)
                  End do

                  Call mvMinus(cup(nst+i),mm(:,nst+i),vv(:,nst+i), &
                               cup(nst+j),mm(:,nst+j),vv(:,nst+j), &
                               cp,mij,vij)

                  Call Pmat_getMatrix(cp,mij,iCnf,jCnf,mat)
                  Pmat(i,j,:)=mat
                  Pmat(j,i,:)=mat

                  Do k=1,cup(nst+j)
                     jCnf(mm(k,nst+j))=0
                  End do
               End do

               Call Pmat_getMatrix(0,mode,iCnf,iCnf,mat)
               Pmat(i,i,:)=mat

               Do k=1,cup(nst+i)
                  iCnf(mm(k,nst+i))=0
               End do

            End do

            if(matrix(idPrpt)>0) then
               Allocate(aa(nn,nn))
               Do m=1,nData
                  ! Calc P = C^t * P * C
                  Call dgemm('N','N',nn,nn,nn,1.0D+00,Pmat(:,:,m),nn,coeff,nn,0.0D+00,aa,nn)
                  Call dgemm('T','N',nn,nn,nn,1.0D+00,coeff,nn,aa,nn,0.0D+00,Pmat(:,:,m),nn)
               End do

               Do i=1,nn
               Do j=1,i
                  write(ifl,'(2i5)') i,j
                  write(ifl,'(i5)') nData
                  write(ifl,'(5e17.8)') Pmat(j,i,:)
               End do
               End do
               Deallocate(aa)

            else
               Allocate(aa(nn,nData),bb(nn))
               Do m=1,nData
                  Do i=1,nn
                     Call dgemv('N',nn,nn,1.0D+00,Pmat(:,:,m),nn,coeff(:,i),1,0.0D+00,bb,1)
                     aa(i,m)=ddot(nn,coeff(:,i),1,bb,1)
                  End do
               End do

               Do i=1,nn
                  write(ifl,'(2i5)') i,i
                  write(ifl,'(i5)') nData
                  write(ifl,'(5e17.8)') aa(i,:)
               End do
               Deallocate(aa,bb)

            endif
            
            Deallocate(Pmat,coeff)

            nst=nst+nCnf(n)

         endif
      End do

      Close(ifl)
      Close(jfl)

      write(iout,100) trim(matrixDataFile)
  100 Format(6x,'-> DONE FOR VQDPT PROPERTY : [ ',a,' ]',/)

      Call finalz()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Vqdptprpt_infrared()

   USE Vqdptprpt_mod
   USE Grid_surface_mod, only : nData
   USE Constants_mod
   USE IR_mod

   Implicit None

   Integer :: ifl,jfl
   Character(13) :: IRdataFile
   Character(17) :: IRspectrumFile

   Integer, allocatable :: mij(:),vij(:)
   Character(120) :: line

   Integer :: iCnf(Nfree),zCnf(Nfree)
   Real(8) :: mat(nData)

   Integer :: n,nn,nst
   Real(8) :: ddot
   Real(8) :: PI,mufi,dE,E0
   Real(8), allocatable :: Pmat(:,:),coeff(:,:),aa(:,:),Evqdpt(:),Wi(:)
   Integer, allocatable :: lbl(:)

   Integer :: i,j,k,l,m

      PI = Acos(-1.0D+00)

      Call setupConf()
      Allocate(mij(maxCup),vij(maxCup))

      Call file_indicator(30,jfl)
      Open(jfl,file='vqdpt-w.wfn',status='old')
      line=''
      Do while(index(line,'ZERO-POINT ENERGY')==0)
         Read(jfl,'(a)') line
      End do
      Read(jfl,*) E0

      zCnf=0

      Call file_indicator(30,ifl)
      IRdataFile = 'vqdpt-IR.data'
      Open(ifl,file=IRdataFile,status='unknown',form='FORMATTED')
      write(ifl,'(''     Omega (cm-1)     IR (km mol-1)      Config.'')')

      iCnf=0

      nst=0
      Do n=1,ngrp
         Allocate(Evqdpt(nCnf(n)))

         if(nCnf(n)==1) then
            nst=nst+1

            line=''
            Do while(index(line,'ENERGY')==0)
               Read(jfl,'(a)') line
            End do
            Read(jfl,*) Evqdpt(1)
            dE=Evqdpt(1)-E0

            Do i=1,cup(nst)
               iCnf(mm(i,nst))=vv(i,nst)
            End do

            Call Pmat_getMatrix(cup(nst),mm(:,nst),iCnf,zCnf,mat)
            mufi=0.D+00
            Do k=1,nData
               mufi=mufi + mat(k)*mat(k)
            End do
            mufi=2.0D+00/3.D+00*PI*dE/H2wvn/vlight/vlight*B2A*1d-13*avogadro*mufi

            write(ifl,'(f15.4,e20.6,5x,10(i3,''_'',i1,2x))') &
                    dE,mufi,(mm(m,nst),vv(m,nst),m=1,cup(nst))

            Do i=1,cup(nst)
               iCnf(mm(i,nst))=0
            End do

         else
            nn=nCnf(n)

            Allocate(Pmat(nn,nData),coeff(nn,nn),aa(nn,nData))
            line=''
            Do while(index(line,'STATE=')==0)
               Read(jfl,'(a)') line
            End do
            Backspace(jfl)
            Do i=1,nn
               Read(jfl,*)
               Read(jfl,*)
               Read(jfl,*) Evqdpt(i)
               Read(jfl,*)
               Read(jfl,*) coeff(:,i)
            End do

            Do i=1,nn
               Do k=1,cup(nst+i)
                  iCnf(mm(k,nst+i))=vv(k,nst+i)
               End do

               Call Pmat_getMatrix(cup(nst+i),mm(:,nst+i),iCnf,zCnf,mat)
               Pmat(i,:)=mat

               Do k=1,cup(nst+i)
                  iCnf(mm(k,nst+i))=0
               End do

            End do

            Do m=1,nData
               Do i=1,nn
                  aa(i,m)=ddot(nn,coeff(:,i),1,Pmat(:,m),1)
               End do
            End do

            Do i=1,nn
               dE=Evqdpt(i)-E0
               mufi=0.D+00
               Do k=1,nData
                  mufi=mufi + aa(i,k)*aa(i,k)
               End do
               mufi=2.0D+00/3.D+00*PI*dE/H2wvn/vlight/vlight*B2A*1d-13*avogadro*mufi

               Allocate(Wi(nn),lbl(nn))
               Do j=1,nn
                  Wi(j)=coeff(j,i)*coeff(j,i)
               End do
               Call sort(nn,lbl,Wi)

               write(ifl,'(f15.4,e20.6,5x,10(i3,''_'',i1,2x))') &
                       dE,mufi,(mm(m,nst+lbl(1)),vv(m,nst+lbl(1)),m=1,cup(nst+lbl(1)))

               Deallocate(Wi,lbl)

            End do

            Deallocate(Pmat,coeff,aa)

            nst=nst+nCnf(n)

         endif
         Deallocate(Evqdpt)
      End do

      Close(ifl)
      Close(jfl)

      IRspectrumFile = 'vqdpt-IR.spectrum'
      Call IR_genSpectrum(IRdataFile,IRspectrumFile)

      Call finalz()

      write(iout,100) 
  100 Format(6x,'-> DONE FOR VQDPT SPECTRUM : [ vqdpt-IR.data / vqdpt-IR.spectrum ]',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

