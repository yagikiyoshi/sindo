!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   USE Constants_mod

   Implicit None

   Integer :: i,Nfree,cp
   Real(8), allocatable :: omega(:)
   Integer, allocatable :: zp(:),ex1(:)
   Integer :: mode(4)
   Real(8) :: hmat

      Call Version()
      Call Mem_construct()
      Call Mol_construct()

      Call Mol_getNfree(Nfree)
      Allocate(omega(Nfree))
      Call Mol_getFreq(omega)

      Call Vib_construct(Nfree)
      Call Vib_setFreq(omega)

      Call PES_Modal_construct(Nfree)

      Call Vscf_run()
      Call Modal_update(0)
      Call Hmat_construct()

      cp=0
      mode=0
      allocate(zp(Nfree))
      zp=0
      Call Hmat_getHmat(cp,mode,zp,zp,Hmat)
      write(Iout,'(f15.8)') Hmat
      deallocate(zp)

      cp=1
      allocate(zp(Nfree),ex1(Nfree))
      zp=0
      ex1=0
      Do i=1,Nfree
         ex1(i)=1
         mode(1)=i
         write(Iout,*)
         Call Hmat_getHmat(cp,mode,ex1,zp,Hmat)
         write(Iout,'(10i4)') zp
         write(Iout,'(10i4)') ex1
         write(Iout,'(f15.8)') Hmat
         ex1(i)=0
      End do
      deallocate(zp,ex1)

      Call Hmat_destruct()

      Call PES_Modal_destruct()

      Call Vib_destruct()
      Call Mol_destruct()
      Call Mem_finalInfo()

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

