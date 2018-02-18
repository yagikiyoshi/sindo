!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/07
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module PES_grid_mod

   USE PES_mod

   ! 1-mode terms
   !  nGrid1(1,Nfree) :: Number of grid points
   !    ptV1(Nfree)   :: Pointer of V1
   !      V1(Nfree)   :: Potential energy values on Grid
   Integer, dimension(:,:), allocatable :: nGrid1
   Integer, dimension(:), allocatable :: ptV1
   Real(8), dimension(:), allocatable :: V1

   ! 2-mode terms
   !  nGrid2(2,nS2) :: Number of grid points
   !    ptV2(nS2)   :: Pointer of V2
   !      V2(nS2)   :: Potential energy values on Grid
   Integer, dimension(:,:), allocatable :: nGrid2
   Integer, dimension(:), allocatable :: ptV2
   Real(8), dimension(:), allocatable :: V2

   ! 3-mode terms
   !  nGrid3(3,nS3) :: Number of grid points
   !    ptV3(nS3)   :: Pointer of V3
   !      V3(nS3)   :: Potential energy values on Grid
   Integer, dimension(:,:), allocatable :: nGrid3
   Integer, dimension(:), allocatable :: ptV3
   Real(8), dimension(:), allocatable :: V3

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_grid_construct()

   USE Constants_mod
   USE PES_grid_mod

   Implicit None

   Integer :: ierr,n
   Integer :: Nfree,Vib_getNfree
   Integer :: i,j,k,l,nGi,nGj,nGk,nGl,mi,mj,mk,ml
   Real(8) :: bmin_i,bmax_i,bmin_j,bmax_j,bmin_k,bmax_k
   Real(8), dimension(:), allocatable :: qsi,qsj,qsk
   Integer, allocatable :: nCHO(:)

    ! 1MR
      if(nS1 /=0) then
         Nfree=Vib_getNfree()
         Allocate(nCHO(Nfree))
         Call Vib_getnCHO(nCHO)

         Call Mem_alloc(-1,ierr,'I',nS1)
         Call Mem_alloc(-1,ierr,'I',(nS1+1))
         Allocate(nGrid1(1,nS1),ptV1(nS1+1))
   
         ! Determine optimal nCHO
         Do n=1,nS1
            Call grid_getnG1(n,nGi)
            Allocate(qsi(nGi))
            Call grid_getGridpoints1(n,qsi)
            bmin_i=qsi(1)
            bmax_i=qsi(nGi)
            Deallocate(qsi)
   
            Call optGrid(mS1(1,n),nGi,bmin_i,bmax_i)
            if(nGi > nCHO(mS1(1,n))) nGi=nCHO(mS1(1,n))
            if(nGi < nCHO(mS1(1,n))) then
               write(Iout,100) mS1(1,n),nCHO(mS1(1,n))-1,nGi-1
               write(Iout,*)
               nCHO(mS1(1,n))=nGi
            endif

            nGrid1(1,n)=nGi
   
         End do
     100 Format(9x,'WARNING:  MISMATCH OF PEF-GRID AND DVR-GRID IS DETECTED',/, &
                9x,'WARNING:  RUNNING WITH SMALLER BASIS SETS FOR',/, &
                9x,'WARNING:        MODE=',i4,/, &
                9x,'WARNING:        VMAX=',i4,' ->',i4)
   
         ptV1(1)=0
         Do i=2,nS1+1
            ptV1(i)=ptV1(i-1) + nGrid1(1,i-1)
         End do
         i=ptV1(nS1+1)
         Call Mem_alloc(-1,ierr,'D',i)
         Allocate(V1(i))

         Call Vib_setnCHO(nCHO)

      endif

    ! 2MR
      if(nS2 /=0) then
         Call Mem_alloc(-1,ierr,'I',nS2*2)
         Call Mem_alloc(-1,ierr,'I',(nS2+1))
         Allocate(nGrid2(2,nS2),ptV2(nS2+1))
         Do n=1,nS2
            Call grid_getnG2(n,nGi,nGj)
            Allocate(qsi(nGi),qsj(nGj))
            Call grid_getGridpoints2(n,qsi,qsj)
            bmin_i=qsi(1)
            bmax_i=qsi(nGi)
            bmin_j=qsj(1)
            bmax_j=qsj(nGj)
            Deallocate(qsi,qsj)
  
            !dbg write(6,'(2i3)') mS2(:,n)
            !dbg write(6,'(2i3)') nGi,nGj
            !dbg write(6,'(2f8.4)') bmin_i,bmax_i
            !dbg write(6,'(2f8.4)') bmin_j,bmax_j
  
            if(nGi > nCHO(mS2(1,n))) nGi=nCHO(mS2(1,n))
            if(nGj > nCHO(mS2(2,n))) nGj=nCHO(mS2(2,n))
  
            Call optGrid(mS2(1,n),nGi,bmin_i,bmax_i)
            Call optGrid(mS2(2,n),nGj,bmin_j,bmax_j)
  
            nGrid2(1,n)=nGi
            nGrid2(2,n)=nGj
  
         End do

         ptV2(1)=0
         Do i=2,nS2+1
            ptV2(i)=ptV2(i-1) + nGrid2(1,i-1)*nGrid2(2,i-1)
         End do
         i=ptV2(nS2+1)
         Call Mem_alloc(-1,ierr,'D',i)
         Allocate(V2(i))

      endif

    ! 3MR
      if(nS3 /=0) then
         Call Mem_alloc(-1,ierr,'I',nS3*3)
         Call Mem_alloc(-1,ierr,'I',(nS3+1))
         Allocate(nGrid3(3,nS3),ptV3(nS3+1))

         Do n=1,nS3
            Call grid_getnG3(n,nGi,nGj,nGk)
            Allocate(qsi(nGi),qsj(nGj),qsk(nGk))
            Call grid_getGridpoints3(n,qsi,qsj,qsk)
            bmin_i=qsi(1)
            bmax_i=qsi(nGi)
            bmin_j=qsj(1)
            bmax_j=qsj(nGj)
            bmin_k=qsk(1)
            bmax_k=qsk(nGk)
            Deallocate(qsi,qsj,qsk)
  
            if(nGi > nCHO(mS3(1,n))) nGi=nCHO(mS3(1,n))
            if(nGj > nCHO(mS3(2,n))) nGj=nCHO(mS3(2,n))
            if(nGk > nCHO(mS3(3,n))) nGk=nCHO(mS3(3,n))
  
            Call optGrid(mS3(1,n),nGi,bmin_i,bmax_i)
            Call optGrid(mS3(2,n),nGj,bmin_j,bmax_j)
            Call optGrid(mS3(3,n),nGk,bmin_k,bmax_k)
  
            nGrid3(1,n)=nGi
            nGrid3(2,n)=nGj
            nGrid3(3,n)=nGk
  
         End do

         ptV3(1)=0
         Do i=2,nS3+1
            ptV3(i)=ptV3(i-1) + nGrid3(1,i-1)*nGrid3(2,i-1)*nGrid3(3,i-1)
         End do
         i=ptV3(nS3+1)
         Call Mem_alloc(-1,ierr,'D',i)
         Allocate(V3(i))

      endif

   Contains

      Subroutine optGrid(mode,nG,b1min,b1max)

      Implicit None 

      Integer :: mode,nG,nGm
      Real(8) :: b1min,b1max,b2min,b2max
      Real(8), parameter :: allow_extrapolation=1.10D+00
      Real(8) :: qm(nG),xdvr(nG,nG)

         b1min=b1min*allow_extrapolation
         b1max=b1max*allow_extrapolation
         nGm=nG

      10 Continue
         Call DVR_genGrid(mode,nGm,xdvr,qm)
         b2max=qm(1)
         b2min=qm(nGm)
         if(b2min < b1min .or. b1max < b2max) then
            nGm=nGm-1
            goto 10
         endif

         nG=nGm

      End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_grid_destruct()

   USE PES_grid_mod

   Implicit None

      if(nS1/=0) then
         Call Mem_dealloc('I',size(nGrid1))
         Call Mem_dealloc('I',size(ptV1))
         Call Mem_dealloc('D',size(V1))
         Deallocate(nGrid1,ptV1,V1)
      endif

      if(nS2/=0) then
         Call Mem_dealloc('I',size(nGrid2))
         Call Mem_dealloc('I',size(ptV2))
         Call Mem_dealloc('D',size(V2))
         Deallocate(nGrid2,ptV2,V2)
      endif

      if(nS3/=0) then
         Call Mem_dealloc('I',size(nGrid3))
         Call Mem_dealloc('I',size(ptV3))
         Call Mem_dealloc('D',size(V3))
         Deallocate(nGrid3,ptV3,V3)
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_grid_getV1(n,VV1)

   USE PES_grid_mod

   Implicit None

      Integer :: n,i,j
      Real(8) :: VV1(*)

      j=ptV1(n)
      Do i=1,nGrid1(1,n)
         j=j+1
         VV1(i)=V1(j)
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_grid_getV2(n,VV2)

   USE PES_grid_mod

   Implicit None

      Integer :: n,i,j
      Real(8) :: VV2(nGrid2(1,n)*nGrid2(2,n))

      VV2=V2(ptV2(n)+1:ptV2(n)+nGrid2(1,n)*nGrid2(2,n))

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_grid_getV3(n,VV3)

   USE PES_grid_mod

   Implicit None

      Integer :: n,i,j
      Real(8) :: VV3(nGrid3(1,n)*nGrid3(2,n)*nGrid3(3,n))

      VV3=V3(ptV3(n)+1:ptV3(n)+nGrid3(1,n)*nGrid3(2,n)*nGrid3(3,n))

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
