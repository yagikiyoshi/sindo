!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   Implicit None

   Integer :: istat

      Call Version()
      Call Mem_construct()

      Call Mem_alloc(-1,istat,'d',500000)
      Call Mem_printInfo()

      Call Mem_alloc(-1,istat,'d',500000)
      Call Mem_dealloc('d',200000)
      Call Mem_printInfo()

      Call Mem_alloc(-1,istat,'d',500000)

      Call Mem_finalInfo()

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

