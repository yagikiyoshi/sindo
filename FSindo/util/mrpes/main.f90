!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2020/01/09
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   Implicit None

   Integer :: Nfree
   Integer :: n,i,j,k,nGi,mi
   Integer, allocatable :: nCHO(:)
   Real(8), allocatable :: qi(:),qj(:),qk(:),qq(:)
   Real(8) :: Vgi, Vqi
   Logical, allocatable :: activemodes(:)

      Call Version()
      Call Mem_construct()
      Call Mol_construct()
      Call Mol_getNfree(Nfree)

      Call Vib_construct(Nfree)
      allocate(nCHO(Nfree))
      Call Vib_getnCHO(nCHO)

      Call Modal_construct(Nfree,nCHO)
      Call Modal_open()
      Do i=1,Nfree
         Call Modal_add(i,nCHO(i))
      End do
      Call Modal_close()

      allocate(activemodes(Nfree), qq(Nfree))
      activemodes = .true.
      Call mrpes_Construct(Nfree,activemodes)
      Call mrpes_printSettings()

      qq = 0.D+00
      Do i=1,Nfree
         mi=i
         nGi=nCHO(i)

         write(6,'(''MODE='',i3)') mi
         Allocate(qi(nGi))
         Call Modal_getQ(mi,nGi,qi)

         Do n=1,nGi
            qq(mi) = qi(n)
            Call mrpes_getV(qq,Vgi)
            Call qff_PES(qq,Vqi)
            write(6,'(f12.4,2e20.10)') qi(n),Vgi,Vqi
         End do
         qq(mi) = 0.0D+00
         Deallocate(qi)
      End do

      Call mrpes_destruct()

      Call Modal_destruct()
      Call Vib_destruct()
      Call Mol_destruct()
      Call Mem_finalInfo()

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

