!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/04/30
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   USE Constants_mod

   Implicit None

   Integer, parameter :: Nfree=6
   Integer :: Nstate,Target_getNstate,label(Nfree)
   Integer :: maxCup,Target_getMaxCup,cup
   Integer, allocatable :: mm(:),vv(:)
   Integer :: i,j

      Call Version()
      Call Mem_construct()
      Call Vib_construct(Nfree)

      Call Target_construct(Nfree)
      Nstate = Target_getNstate()
      Do i=1,Nstate
         Call Target_getLabel(i,Nfree,label)
         write(6,'(3x,i3,3x,6i1)') i,label
      End do
      write(6,*)

      maxCup = Target_getMaxCUP()
      allocate(mm(maxCup),vv(maxCup))
      Do i=1,Nstate
         Call Target_getConf(i,cup,mm,vv)
         write(6,'(3x,i3,3x,10(i3,''_'',i1,2x))') i, (mm(j),vv(j),j=1,cup)
      End do
      deallocate(mm,vv)

      Call Target_destruct()

      Call Vib_destruct()
      Call Mem_finalInfo()

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

