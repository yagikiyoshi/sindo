!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

PROGRAM main

   USE Constants_mod

   Implicit None

   Integer :: Nfree
   Integer, allocatable :: nCHO(:)
   Real(8), allocatable :: omega(:)

   Integer :: i,nGi

      Call Version()
      Call Mem_construct()
      Call Mol_construct()

      Call Mol_getNfree(Nfree)
      Allocate(nCHO(Nfree))
      Call Vib_construct(Nfree)
      Call Vib_getnCHO(nCHO)
      Call Modal_construct(Nfree,nCHO)
      Call Modal_open()
      Do i=1,Nfree
         Call Modal_add(i,nCHO(i))
      End do
      Call Modal_close()

      !Do i=1,Nfree
      !   nGi=nCHO(i)
      !   if(nGi==0) cycle
      !   write(Iout,210) i,nGi
      !   Call printDwfn(i,nGi)
      !End do

      Call Modal_readVSCF(i,'vscf-001.wfn')  

      Do i=1,Nfree
         nGi=nCHO(i)
         if(nGi==0) cycle

         write(Iout,210) i,nGi
         Call printDwfn(i,nGi)

      End do

      Call Modal_destruct()

      Call Vib_destruct()
      Call Mol_destruct()
      Call Mem_finalInfo()

  210 Format(9x,'> MODE  = ',i4,',  NGRID = ',i4,/)

      CONTAINS

      Subroutine printDwfn(i,ni)

          Integer :: i,j,k,l,n,ni
          Real(8), dimension(:), allocatable :: qq,Dwfn

          Allocate(qq(ni),Dwfn(ni))
          Call Modal_getQ(i,ni,qq)
          write(iout,200) qq

          j=mod(ni,9)
          if(j/=0) then
             j=(ni-j)/9
          else
             j=ni/9-1
          endif
          Do k=0,ni-1
             Call Modal_getXwfn(i,ni,k,k,Dwfn)
             write(iout,210) Dwfn
          End do
          write(Iout,*)
          Deallocate(qq,Dwfn)

      200 Format(9x,10f8.2)
      210 Format(9x,10f8.4)

      End subroutine

End 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

