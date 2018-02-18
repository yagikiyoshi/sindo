!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/30
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Pmat_getMatrix(cp,mode,mCnf,nCnf,dmat)

   USE Prpt_mod, only : Nfree
   USE Grid_surface_mod

   Implicit None

   Integer :: cp,mode(cp),mCnf(Nfree),nCnf(Nfree)
   Real(8) :: dmat(nData)

   Integer :: mi,mj,mk,ml,n,i,j,k,l
   Real(8) :: aaa(nData)

      aaa=0.0D+00
      dmat=0.D+00

      Select Case(cp)

     ! [ Diagonal ]----------------------------------------------------------- 
         Case(0) 

            dmat = V0

            Do n=1,nS1
               Call grid1(mS1(1,n),n,aaa)
               dmat=dmat + aaa
            End do

            if(MR==1) goto 100

            Do n=1,nS2
               Call grid2(mS2(1,n),mS2(2,n),n,aaa)
               dmat=dmat + aaa
            End do

            if(MR==2) goto 100

            Do n=1,nS3
               Call grid3(mS3(1,n),mS3(2,n),mS3(3,n),n,aaa)
               dmat=dmat + aaa
            End do

            if(MR==3) goto 100

     100 Continue

     !-[ 1-mode exc. ]-------------------------------------------------------- 
         Case(1) 

            mi=mode(1)

            Do n=1,nS1
               if(mi == mS1(1,n)) then
                  Call grid1(mi,n,aaa)
                  dmat=dmat + aaa
                  exit
               endif
            End do

            if(MR==1) goto 200

            Do n=1,nS2
               if(mi == mS2(1,n) .or. mi == mS2(2,n)) then
                  Call grid2(mS2(1,n),mS2(2,n),n,aaa)
                  dmat=dmat + aaa
               endif
            End do

            if(MR==2) goto 200

            Do n=1,nS3
               if(mi == mS3(1,n) .or. mi == mS3(2,n) .or. mi == mS3(3,n)) then
                  Call grid3(mS3(1,n),mS3(2,n),mS3(3,n),n,aaa)
                  dmat=dmat + aaa
               endif
            End do

            if(MR==3) goto 200

     200 Continue

     !-[ 2-mode exc. ]-------------------------------------------------------- 
         Case(2) 

            if(MR==1) goto 300

            mj=mode(1)
            mi=mode(2)

            Do n=1,nS2
               if(mi == mS2(1,n) .and. mj == mS2(2,n)) then
                  Call grid2(mS2(1,n),mS2(2,n),n,aaa)
                  dmat=dmat + aaa
                  exit
               endif
            End do

            if(MR==2) goto 300

            Do n=1,nS3
               Do i=1,2
               if(mi == mS3(i,n)) then
                  Do j=i+1,3
                  if(mj == mS3(j,n)) then
                     Call grid3(mS3(1,n),mS3(2,n),mS3(3,n),n,aaa)
                     dmat=dmat + aaa
                     exit
                  endif
                  End do
                  exit
               endif
               End do
            End do

            if(MR==3) goto 300

     300 Continue

     !-[ 3-mode exc. ]-------------------------------------------------------- 
         Case(3) 

            if(MR<3) goto 400

            mk=mode(1)
            mj=mode(2)
            mi=mode(3)

            Do n=1,nS3
               if(mi == mS3(1,n)) then
               if(mj == mS3(2,n)) then
               if(mk == mS3(3,n)) then
                  Call grid3(mS3(1,n),mS3(2,n),mS3(3,n),n,aaa)
                  dmat=dmat + aaa
               endif
               endif
               endif
            End do

            if(MR==3) goto 400

     400 Continue

     !-[ 4-mode exc. ]-------------------------------------------------------- 
         Case(4) 

     500 Continue

         End select


   Contains

   Subroutine grid1(mi,n,mat)

      Integer :: mi,n
      Real(8) :: mat(nData)

      Integer :: nGi,qi,nd,pt
      Real(8), allocatable :: Vi(:,:),Di(:)

      !Call prptGrid_getnGrid1(nGi,n)
      nGi=nG1(1,n)
      Allocate(Vi(nGi,nData),Di(nGi))

      !Call prptGrid_getData1(n,Vi)
      pt=1
      Do nd=1,nData
      Do qi=1,nGi
         Vi(qi,nd)=V1(ptV1(n)+pt)
         pt=pt+1
      End do
      End do

      Call Modal_getXwfn(mi,nGi,mCnf(mi),nCnf(mi),Di)

      mat=0.D+00
      Do qi=1,nGi
         mat=mat + Di(qi)*Vi(qi,:)
      End do
      !write(6,'(i4)') mi
      !Do qi=1,nGi
      !   write(6,'(3e15.6)') Vi(qi,:)
      !End do

      Deallocate(Vi,Di)

   End subroutine

   Subroutine grid2(mi,mj,n,mat)

      Integer :: mi,mj,n
      Real(8) :: mat(nData)

      Integer :: nGi,nGj,qi,qj,nd,pt
      Real(8), allocatable :: Vij(:,:,:),Di(:),Dj(:)

      !Call prptGrid_getnGrid2(nGi,nGj,n)
      nGi=nG2(1,n)
      nGj=nG2(2,n)
      Allocate(Vij(nGj,nGi,nData),Di(nGi),Dj(nGj))

      !Call prptGrid_getData2(n,Vij)
      pt=1
      Do nd=1,nData
      Do qi=1,nGi
      Do qj=1,nGj
         Vij(qj,qi,nd)=V2(ptV2(n)+pt)
         pt=pt+1
      End do
      End do
      End do
      Call Modal_getXwfn(mi,nGi,mCnf(mi),nCnf(mi),Di)
      Call Modal_getXwfn(mj,nGj,mCnf(mj),nCnf(mj),Dj)

      mat=0.D+00
      Do qi=1,nGi
      Do qj=1,nGj
         mat=mat + Di(qi)*Dj(qj)*Vij(qj,qi,:)
      End do
      End do

      Deallocate(Vij,Di,Dj)

   End subroutine

   Subroutine grid3(mi,mj,mk,n,mat)

      Integer :: mi,mj,mk,n
      Real(8) :: mat(nData)

      Integer :: nGi,nGj,nGk,qi,qj,qk,nd,pt
      Real(8), allocatable :: Vijk(:,:,:,:),Di(:),Dj(:),Dk(:)

      !Call prptGrid_getnGrid3(nGi,nGj,nGk,n)
      nGi=nG3(1,n)
      nGj=nG3(2,n)
      nGk=nG3(3,n)
      Allocate(Vijk(nGk,nGj,nGi,nData),Di(nGi),Dj(nGj),Dk(nGk))

      !Call prptGrid_getData3(n,Vijk)
      pt=1
      Do nd=1,nData
      Do qi=1,nGi
      Do qj=1,nGj
      Do qk=1,nGk
         Vijk(qk,qj,qi,nd)=V3(ptV3(n)+pt)
         pt=pt+1
      End do
      End do
      End do
      End do
      Call Modal_getXwfn(mi,nGi,mCnf(mi),nCnf(mi),Di)
      Call Modal_getXwfn(mj,nGj,mCnf(mj),nCnf(mj),Dj)
      Call Modal_getXwfn(mk,nGk,mCnf(mk),nCnf(mk),Dk)

      mat=0.D+00
      Do qi=1,nGi
      Do qj=1,nGj
      Do qk=1,nGk
         mat=mat + Di(qi)*Dj(qj)*Dk(qk)*Vijk(qk,qj,qi,:)
      End do
      End do
      End do

      !write(6,'(3i4)') mi,mj,mk
      !Do qi=1,nGi
      !Do qj=1,nGj
      !Do qk=1,nGk
      !   write(6,'(3e15.6)') Vijk(qk,qj,qi,:)
      !End do
      !End do
      !End do

      Deallocate(Vijk,Di,Dj,Dk)

   End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

