!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/30
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module PES_mod

   ! -- (POTENTIAL parameters) --------------------------------------------- 
   !     o  MR         :: Mode Coupling Representation
   !     o  QFF
   !        nQx        :: Num. of xMR terms
   !        mQx(x,nQx) :: Mode of each terms (mQx(1,n) > mQx(2,n) >  .. )
   !     o  Grid
   !        nSx        :: Num. of xMR terms
   !        mSx(x,nSx) :: Mode of each terms (mSx(1,n) > mSx(2,n) >  .. )

   Integer :: MR

   Logical :: qff
   Integer :: nQ1,nQ2,nQ3,nQ4
   Integer, dimension(:,:), allocatable :: mQ1,mQ2,mQ3,mQ4

   Logical :: grid
   Integer :: nS1,nS2,nS3,nS4
   Integer, dimension(:,:), allocatable :: mS1,mS2,mS3,mS4

   Integer, dimension(:,:), allocatable :: idx1MR,idx2MR,idx3MR

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Construct PES via mrpes routines
!

   Subroutine PES_construct(Nfree,MRin)

   USE PES_mod

   Implicit None

   Integer :: MRin,Nfree
   Integer :: mrpes_getNumOfTerm
   Integer, parameter :: tqff=1,tgrid=2
   Integer :: i,j,k,l,n,ierr

      MR=MRin
      qff=.false.
      nQ1=0; nQ2=0; nQ3=0; nQ4=0
      grid=.false.
      nS1=0; nS2=0; nS3=0; nS4=0

      ! 1MR-PEF
      nQ1=mrpes_getNumOfTerm(tqff,1)
      nS1=mrpes_getNumOfTerm(tgrid,1)

      Call Mem_alloc(-1,ierr,'I',Nfree*2)
      Allocate(idx1MR(2,Nfree))
      idx1MR=0

      if(nQ1/=0) then
         qff=.true.
         Call Mem_alloc(-1,ierr,'I',nQ1)
         Allocate(mQ1(1,nQ1))
         Do i=1,nQ1
            Call mrpes_getMode1(tqff,i,mQ1(1,i))

            idx1MR(1,mQ1(1,i))=tqff
            idx1MR(2,mQ1(1,i))=i
         End do
      endif

      if(nS1/=0) then
         grid=.true.
         Call Mem_alloc(-1,ierr,'I',nS1)
         Allocate(mS1(1,nS1))
         Do i=1,nS1
            Call mrpes_getMode1(tgrid,i,mS1(1,i))

            idx1MR(1,mS1(1,i))=tgrid
            idx1MR(2,mS1(1,i))=i
         End do
      endif

      if(MR==1) goto 1000

      ! 2MR-PEF
      nQ2=mrpes_getNumOfTerm(tqff,2)
      nS2=mrpes_getNumOfTerm(tgrid,2)

      Call Mem_alloc(-1,ierr,'I',Nfree*(Nfree-1))
      Allocate(idx2MR(2,Nfree*(Nfree-1)/2))
      idx2MR=0

      if(nQ2/=0) then
         qff=.true.
         Call Mem_alloc(-1,ierr,'I',nQ2*2)
         Allocate(mQ2(2,nQ2))
         Do n=1,nQ2
            Call mrpes_getMode2(tqff,n,mQ2(:,n))
            k=(mQ2(1,n)-1)*(mQ2(1,n)-2)/2 + mQ2(2,n)

            idx2MR(1,k)=tqff
            idx2MR(2,k)=n
         End do
      endif

      if(nS2/=0) then
         grid=.true.
         Call Mem_alloc(-1,ierr,'I',nS2*2)
         Allocate(mS2(2,nS2))
         Do n=1,nS2
            Call mrpes_getMode2(tgrid,n,mS2(:,n))
            k=(mS2(1,n)-1)*(mS2(1,n)-2)/2 + mS2(2,n)

            idx2MR(1,k)=tgrid
            idx2MR(2,k)=n
         End do
      endif

      if(MR==2) goto 1000

    ! 3MR-PEFs
      nQ3=mrpes_getNumOfTerm(tqff,3)
      nS3=mrpes_getNumOfTerm(tgrid,3)

      Call Mem_alloc(-1,ierr,'I',Nfree*(Nfree-1)*(Nfree-2)/3)
      Allocate(idx3MR(2,Nfree*(Nfree-1)*(Nfree-2)/6))
      idx3MR=0

      if(nQ3/=0) then
         qff=.true.
         Call Mem_alloc(-1,ierr,'I',nQ3*3)
         Allocate(mQ3(3,nQ3))
         Do n=1,nQ3
            Call mrpes_getMode3(tqff,n,mQ3(:,n))
            k=(mQ3(1,n)-1)*(mQ3(1,n)-2)*(mQ3(1,n)-3)/6 &
             +(mQ3(2,n)-1)*(mQ3(2,n)-2)/2 &
             + mQ3(3,n)

            idx3MR(1,k)=tqff
            idx3MR(2,k)=n
         End do
      endif

      if(nS3/=0) then
         grid=.true.
         Call Mem_alloc(-1,ierr,'I',nS3*3)
         Allocate(mS3(3,nS3))
         Do n=1,nS3
            Call mrpes_getMode3(tgrid,n,mS3(:,n))
            k=(mS3(1,n)-1)*(mS3(1,n)-2)*(mS3(1,n)-3)/6 &
             +(mS3(2,n)-1)*(mS3(2,n)-2)/2 &
             + mS3(3,n)

            idx3MR(1,k)=tgrid
            idx3MR(2,k)=n
         End do
      endif

      if(MR==3) goto 1000

    ! 4MR-PEFs
      nQ4=mrpes_getNumOfTerm(tqff,4)
      nS4=mrpes_getNumOfTerm(tgrid,4)

      if(nQ4/=0) then
         qff=.true.
         Call Mem_alloc(-1,ierr,'I',nQ4*4)
         Allocate(mQ4(4,nQ4))
         Do n=1,nQ4
            Call mrpes_getMode4(tqff,n,mQ4(:,n))
         End do
      endif

      if(nS4/=0) then
         grid=.true.
         Call Mem_alloc(-1,ierr,'I',nS4*4)
         Allocate(mS4(4,nS4))
         Do n=1,nS4
            Call mrpes_getMode4(tgrid,n,mS4(:,n))
         End do
      endif
      if(MR==4) goto 1000

 1000 Continue

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Construct polynomial PES 
!

   Subroutine PES_construct_qff(Nfree,MRin)

   USE PES_mod

   Implicit None

   Integer :: Nfree,MRin
   Integer :: mrpes_getNumOfTerm
   Integer, parameter :: tqff=1,tgrid=2
   Integer :: i,j,k,l,n,ierr

      MR=MRin
      qff=.true.
      nQ1=0; nQ2=0; nQ3=0; nQ4=0
      grid=.false.
      nS1=0; nS2=0; nS3=0; nS4=0

      ! 1MR-PEF
      nQ1=Nfree

      Call Mem_alloc(-1,ierr,'I',Nfree*2)
      Allocate(idx1MR(2,Nfree))
      Call Mem_alloc(-1,ierr,'I',nQ1)
      Allocate(mQ1(1,nQ1))
      Do i=1,nQ1
         mQ1(1,i)=i
         idx1MR(1,mQ1(1,i))=tqff
         idx1MR(2,mQ1(1,i))=i
      End do
      if(MR==1) goto 1000

      ! 2MR-PEF
      nQ2=Nfree*(Nfree-1)/2

      Call Mem_alloc(-1,ierr,'I',Nfree*(Nfree-1))
      Allocate(idx2MR(2,Nfree*(Nfree-1)/2))
      Call Mem_alloc(-1,ierr,'I',nQ2*2)
      Allocate(mQ2(2,nQ2))
      n=1
      Do i=2,Nfree
      Do j=1,i-1
         mQ2(1,n)=i
         mQ2(2,n)=j

         k=(mQ2(1,n)-1)*(mQ2(1,n)-2)/2 + mQ2(2,n)
         idx2MR(1,k)=tqff
         idx2MR(2,k)=n
         n=n+1
      End do
      End do
      if(MR==2) goto 1000

    ! 3MR-PEFs
      nQ3=Nfree*(Nfree-1)*(Nfree-2)/6

      if(nQ3>0) then
         Call Mem_alloc(-1,ierr,'I',nQ3*3)
         Allocate(mQ3(3,nQ3))
         n=1
         Do i=3,Nfree
         Do j=2,i-1
         Do k=1,j-1
            mQ3(1,n)=i
            mQ3(2,n)=j
            mQ3(3,n)=k
            n=n+1
         End do
         End do
         End do
      endif
      if(MR==3) goto 1000

    ! 4MR-PEFs
      nQ4=Nfree*(Nfree-1)*(Nfree-2)*(Nfree-3)/24

      if(nQ4>0) then
      Call Mem_alloc(-1,ierr,'I',nQ4*4)
         Allocate(mQ4(4,nQ4))
         n=1
         Do i=4,Nfree
         Do j=3,i-1
         Do k=2,j-1
         Do l=1,k-1
            mQ4(1,n)=i
            mQ4(2,n)=j
            mQ4(3,n)=k
            mQ4(4,n)=l
            n=n+1
         End do
         End do
         End do
         End do
      endif

 1000 Continue

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_destruct()

   USE PES_mod

   Implicit None

      Call Mem_dealloc('I',size(idx1MR))
      Deallocate(idx1MR)
      if(allocated(mQ1)) then
         Call Mem_dealloc('I',size(mQ1))
         Deallocate(mQ1)
      endif
      if(allocated(mS1)) then
         Call Mem_dealloc('I',size(mS1))
         Deallocate(mS1)
      endif

      if(MR==1) return

      Call Mem_dealloc('I',size(idx2MR))
      Deallocate(idx2MR)
      if(allocated(mQ2)) then
         Call Mem_dealloc('I',size(mQ2))
         Deallocate(mQ2)
      endif
      if(allocated(mS2)) then
         Call Mem_dealloc('I',size(mS2))
         Deallocate(mS2)
      endif

      if(MR==2) return

      if(allocated(mQ3)) then
         Call Mem_dealloc('I',size(mQ3))
         Deallocate(mQ3)
      endif
      if(allocated(mS3)) then
         Call Mem_dealloc('I',size(mS3))
         Deallocate(mS3)
      endif

      if(MR==3) return

      if(allocated(mQ4)) then
         Call Mem_dealloc('I',size(mQ4))
         Deallocate(mQ4)
      endif
      if(allocated(mS4)) then
         Call Mem_dealloc('I',size(mS4))
         Deallocate(mS4)
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function PES_isQFF()

   USE PES_mod

   Implicit None

   Logical PES_isQFF

      PES_isQFF = qff

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function PES_isGRID()

   USE PES_mod

   Implicit None

   Logical PES_isGRID

      PES_isGRID = grid

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

