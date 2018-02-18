!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Constants_mod

   ! File assignment
   !  Inp  : Input file
   !  Iout : Output file
   Integer, parameter :: Inp=5, Iout=6

   ! Physical constants
   Real(8), parameter :: elmass=1822.88853D+00, &
                         B2A=0.52917724924d+00, &
                       vlight=1.3703599918E+02, &
                       avogadro=6.02214129e+23, &
                            atu=2.41888433E-17, &
                          H2wvn=2.194746E+05
                          !midas H2wvn=2.19474631370499E+05

   ! Maximum number of atoms/modes
   Integer, parameter :: MaxNat=200, MaxNfree=MaxNat*3-6

   ! Maximum number of states (VSCF, VPT)
   Integer, parameter :: MaxState=1000

   ! Maximum number of modes to excite for the target states (VPT)
   Integer, parameter :: MaxTarget_cup=10

   ! Maximum number of P-space configurations for each group
   Integer, parameter :: maxpCnf=5000

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

