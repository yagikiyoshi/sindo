!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/30
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine calc_vib()

   USE Constants_mod
   USE Vib_mod

   Implicit None

      if(locvscf) then
         Call Target_setNstate(0)
         Call Ocvscf_run(Nfree)
         return
      endif 

      if( (.not. lvscf) .and. &
          (.not. lvci)  .and. &
          (.not. lvpt)  .and. &
          (.not. lvqdpt)) return

      Call Target_construct(Nfree,activemodes)
      Call PES_Modal_construct(Nfree,MR,activemodes)

      Call Mem_printInfo
      Call timer(1,Iout)

      if(lvscf) then 
         Call Vscf_run()
         Call Mem_printInfo
         Call timer(1,Iout)
      endif

      if(lvci) then 
         Call Vci_run()
         Call Mem_printInfo
         Call timer(1,Iout)
      endif

      if(lvpt) then
         Call Vpt_run()
         Call Mem_printInfo
         Call timer(1,Iout)
      endif

      if(lvqdpt) then
         Call Vqdpt_run()
         Call Mem_printInfo
         Call timer(1,Iout)
      endif

      Call PES_Modal_destruct()
      Call Target_destruct()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

!  Setup the potential and the modals

   Subroutine PES_Modal_construct(Nfree,MR,activemodes)

   Implicit None

   Integer :: Nfree
   Integer :: Vib_getMRforPES,MR
   Logical :: activemodes(Nfree),PES_isQFF,PES_isGrid

      !MR=Vib_getMRforPES()
      !Call Vib_getActiveModes(activemodes)

      Call mrpes_Construct(Nfree,MR,activemodes)
      Call mrpes_printSettings()

      Call PES_construct(Nfree,MR)
      if(PES_isQFF()) Call PES_qff_construct()
      if(PES_isGrid()) Call PES_grid_construct()

      Call setupModal_for_PES(Nfree)
      if(PES_isGrid()) Call setupPES_gridValue(Nfree)

      Call mrpes_destruct()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

!  Setup the modals

   Subroutine setupModal_for_PES(Nfree)

   USE PES_mod
   USE PES_grid_mod

   Implicit None

   Integer :: Nfree

   Integer :: nCHO(Nfree)
   Integer :: i,n, mi,mj,mk, nGi,nGj,nGk

      Call Vib_getnCHO(nCHO)

      Call Modal_construct(Nfree,nCHO)
      Call Modal_open()

      Do i=1,Nfree
         Call Modal_add(i,nCHO(i))
      End do
      Do n=1,nS2
         mi=mS2(1,n)
         mj=mS2(2,n)
         nGi=nGrid2(1,n)
         nGj=nGrid2(2,n)
         Call Modal_add(mi,nGi)
         Call Modal_add(mj,nGj)
      End do
      Do n=1,nS3
         mi=mS3(1,n)
         mj=mS3(2,n)
         mk=mS3(3,n)
         nGi=nGrid3(1,n)
         nGj=nGrid3(2,n)
         nGk=nGrid3(3,n)
         Call Modal_add(mi,nGi)
         Call Modal_add(mj,nGj)
         Call Modal_add(mk,nGk)
      End do

      Call Modal_close()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

!  Set the PES values on the grid points

   Subroutine setupPES_gridValue(Nfree)

   USE PES_mod
   USE PES_grid_mod

   Implicit None

   Integer :: Nfree
   Real(8) :: qq(Nfree)

   Integer :: i,j,k, mi,mj,mk, nGi,nGj,nGk, n,pt,ii(4)
   Real(8), allocatable :: qi(:),qj(:),qk(:)

      qq=0.D+00

      Do n=1,nS1
         mi=mS1(1,n)
         nGi=nGrid1(1,n)

         Allocate(qi(nGi))
         Call Modal_getQ(mi,nGi,qi)
         Do i=1,nGi
            qq(mi)=qi(i)
            Call grid_getV1(n,qq,V1(ptV1(n)+i),ii(1))
         End do
         qq(mi)=0.D+00
         Deallocate(qi)

      End do

      Do n=1,nS2
         mi=mS2(1,n)
         mj=mS2(2,n)
         nGi=nGrid2(1,n)
         nGj=nGrid2(2,n)

         Allocate(qi(nGi),qj(nGj))
         Call Modal_getQ(mi,nGi,qi)
         Call Modal_getQ(mj,nGj,qj)
         pt=1
         Do i=1,nGi
            qq(mi)=qi(i)
         Do j=1,nGj
            qq(mj)=qj(j)
            Call grid_getV2(n,qq,V2(ptV2(n)+pt),ii(1:2))
            pt=pt+1
         End do
         End do
         qq(mi)=0.D+00
         qq(mj)=0.D+00
         Deallocate(qi,qj)

      End do

      Do n=1,nS3
         mi=mS3(1,n)
         mj=mS3(2,n)
         mk=mS3(3,n)
         nGi=nGrid3(1,n)
         nGj=nGrid3(2,n)
         nGk=nGrid3(3,n)

         Allocate(qi(nGi),qj(nGj),qk(nGk))
         Call Modal_getQ(mi,nGi,qi)
         Call Modal_getQ(mj,nGj,qj)
         Call Modal_getQ(mk,nGk,qk)
         pt=1
         Do i=1,nGi
            qq(mi)=qi(i)
         Do j=1,nGj
            qq(mj)=qj(j)
         Do k=1,nGk
            qq(mk)=qk(k)
            Call grid_getV3(n,qq,V3(ptV3(n)+pt),ii(1:3))
            pt=pt+1
         End do
         End do
         End do
         qq(mi)=0.D+00
         qq(mj)=0.D+00
         qq(mk)=0.D+00
         Deallocate(qi,qj,qk)

      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_Modal_destruct

   Implicit None

   Logical :: PES_isQFF,PES_isGrid

      Call PES_destruct()
      if(PES_isQFF()) Call PES_qff_destruct()
      if(PES_isGRID()) Call PES_grid_destruct()

      Call Modal_destruct()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
