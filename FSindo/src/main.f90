!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/03/03
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   Implicit None

   Integer :: Nfree

      Call Version()
      Call Mem_construct()
      Call Mol_construct()

      Call Mol_getNfree(Nfree)
      Call Vib_construct(Nfree)

      Call calc_vib()
      Call calc_prpt()

      Call Vib_destruct()
      Call Mol_destruct()

      Call Finalize()

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Finalize

   USE Constants_mod

      Call Mem_finalInfo()
      Call timer(2,Iout)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

