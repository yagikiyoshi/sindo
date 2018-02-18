!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   USE PES_mod
   USE PES_grid_mod

   Implicit None

   Integer :: Nfree
   Integer :: n,i,j,k,nGi,nGj,nGk,mi,mj,mk
   Real(8), allocatable :: qi(:),qj(:),qk(:),Vi(:),Vij(:,:),Vijk(:,:,:)

      Call Version()
      Call Mem_construct()
      Call Mol_construct()

      Call Mol_getNfree(Nfree)
      Call Vib_construct(Nfree)

      Call PES_Modal_construct(Nfree)

      Do n=1,nS1
         mi=mS1(1,n)
         nGi=nGrid1(1,n)

         write(6,'(''MODE='',i3)') mi
         Allocate(qi(nGi),Vi(nGi))
         Call Modal_getQ(mi,nGi,qi)
         Call PES_grid_getV1(n,Vi)
         Do i=1,nGi
            write(6,'(f12.4,e20.10)') qi(i),Vi(i)
         End do
         Deallocate(qi,Vi)
      End do

      Do n=1,nS2
         mi=mS2(1,n)
         mj=mS2(2,n)
         nGi=nGrid2(1,n)
         nGj=nGrid2(2,n)
         write(6,'(''MODE='',2i3)') mi,mj
         Allocate(qi(nGi),qj(nGj),Vij(nGj,nGi))
         Call Modal_getQ(mi,nGi,qi)
         Call Modal_getQ(mj,nGj,qj)
         Call PES_grid_getV2(n,Vij)
         Do i=1,nGi
         Do j=1,nGj
            write(6,'(2f12.4,e20.10)') qj(j),qi(i),Vij(j,i)
         End do
         End do
         Deallocate(qi,qj,Vij)
      End do

      Do n=1,nS3
         mi=mS3(1,n)
         mj=mS3(2,n)
         mk=mS3(3,n)
         nGi=nGrid3(1,n)
         nGj=nGrid3(2,n)
         nGk=nGrid3(3,n)
         write(6,'(''MODE='',3i3)') mi,mj,mk
         Allocate(qi(nGi),qj(nGj),qk(nGk),Vijk(nGk,nGj,nGi))
         Call Modal_getQ(mi,nGi,qi)
         Call Modal_getQ(mj,nGj,qj)
         Call Modal_getQ(mk,nGk,qk)
         Call PES_grid_getV3(n,Vijk)
         Do i=1,nGi
         Do j=1,nGj
         Do k=1,nGk
            write(6,'(3f12.4,e20.10)') qk(k),qj(j),qi(i),Vijk(k,j,i)
         End do
         End do
         End do
         Deallocate(qi,qj,qk,Vijk)
      End do

      Call PES_Modal_destruct()

      Call Vib_destruct()
      Call Mol_destruct()
      Call Mem_finalInfo()

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

