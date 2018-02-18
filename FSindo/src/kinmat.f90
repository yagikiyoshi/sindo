!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/09
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine KE_genTmat(mode,nn,Tmat)

   USE Vib_mod, only : omegaf

   Implicit None

   Integer :: mode,nn
   Real(8) :: Tmat(nn,nn)

      Call HO_kinmat(nn-1,omegaf(mode),Tmat)

      ! TODO: Contracted HO basis

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
