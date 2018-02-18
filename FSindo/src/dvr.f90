!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/07
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    mode  :: Number of mode
!    nGrid :: Number of grid points
!    xdvr  :: Transformation matrix from FBR -> DVR
!    qq    :: Grid points / au

   Subroutine DVR_genGrid(mode,nGrid,xdvr,qq)

   USE Vib_mod, only : omegaf

   Implicit None

   Integer :: mode,nGrid
   Real(8) :: xdvr(nGrid,nGrid),qq(nGrid)

      Call genHODVR(nGrid,omegaf(mode),xdvr,qq)

   Contains

   Subroutine genHODVR(nGrid,omega,xdvr,qq)

   Implicit None

   Integer :: nGrid
   Real(8) :: omega,xdvr(nGrid,nGrid),qq(nGrid)

   Real(8), dimension(nGrid,nGrid) :: xx

  !-------------------------------------

      Call HO_xmat(nGrid,omega,xx)
      Call huckeler(nGrid,nGrid,xx,qq,xdvr)

  !-------------------------------------

   End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
