!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012 
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Modal_mod 

        ! -----------------------------------------------------------------------
        !     Nfree  :: Number of vibrational degrees of freedom
        Integer :: Nfree

        ! -- (BASIS FUNCTION parameters) ----------------------------------------
        !     nCHO(Nfree)   :: Num. of contracted HO wfn 
        !     maxCHO        :: Maximum of nCHO
        !     state         :: VSCF configuration
        !     Ntype         :: Number of types of grid
        Integer :: maxCHO
        Integer, dimension(:), allocatable :: nCHO
        Integer, dimension(:), allocatable :: state
        Integer :: Ntype

        ! -- (DVR and Modal coefficients) ---------------------------------------
        !     memsz         :: size of block
        !     ptblock(maxCHO,Nfree) :: Pointer
        !     block(memsz)  :: qq, xdvr, Cwfn, and Xwfn
        Integer :: memsz
        Integer, dimension(:,:), allocatable :: ptblock
        Real(8), dimension(:), allocatable :: block

        ! -- (Modal energies) ---------------------------------------------------
        !     ptEne0        :: Pointer
        !     Ene0          :: Modal energy
        Integer, dimension(:), allocatable :: ptEne0
        Real(8), dimension(:), allocatable :: Ene0

End module
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_construct(NNfree,inCHO)

   USE Modal_mod

   Implicit None

   Integer :: NNfree,inCHO(NNfree)

   Integer :: rCHO,ierr,i,j,k,mHO,mCHO
   Real(8), allocatable :: CHOi(:,:)

      Nfree=NNfree

      Call Mem_alloc(-1,ierr,'I',Nfree)
      Allocate(nCHO(Nfree))
      maxCHO=-1
      Do i=1,Nfree
         nCHO(i)=inCHO(i)
         if(nCHO(i)>maxCHO) maxCHO=nCHO(i)
      End do

      Call Mem_alloc(-1,ierr,'I',Nfree)
      Allocate(state(Nfree))

    ! Setup ptEne0, Ene0
      Call Mem_alloc(-1,ierr,'I',Nfree)
      allocate(ptEne0(Nfree))
      ptEne0(1)=0
      Do i=2,Nfree
         ptEne0(i)=ptEne0(i-1)+nCHO(i-1)
      End do

      i=ptEne0(Nfree)+nCHO(Nfree)
      Call Mem_alloc(-1,ierr,'D',i)
      allocate(Ene0(i))

    ! Setup ptblock
      Call Mem_alloc(-1,ierr,'I',Nfree*maxCHO)
      allocate(ptblock(maxCHO,Nfree))

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Modal_open()

   USE Modal_mod

   Implicit None

    ! Initialize
      Ntype=0
      memsz=0
      ptblock=-1

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Modal_add(mode,nGrid)

   USE Modal_mod

   Implicit None

   Integer :: mode,nGrid,pt

      if(nGrid == 0) return
      if(ptblock(nGrid,mode) >= 0) return

      ptblock(nGrid,mode)=memsz

      ! qq, xdvr, Cwfn, and Xwfn
      memsz=memsz + nGrid + nGrid*nGrid*2 + nGrid*nGrid*nGrid

      Ntype=Ntype+1

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Modal_close()

   USE Modal_mod

   Implicit None

   Integer :: ierr
   Integer :: i,j,k, pt

      Call Mem_alloc(-1,ierr,'D',memsz)
      allocate(block(memsz))

      Call Modal_init()
      Call Modal_genDVR()
      Call Modal_update(0)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_destruct()

   USE Modal_mod

   Implicit None

   Real(8) :: rsz

      Call Mem_dealloc('I',size(nCHO))
      Deallocate(nCHO)

      Call Mem_dealloc('I',size(state))
      Deallocate(state)

      Call Mem_dealloc('I',size(ptblock))
      Deallocate(ptblock)
      Call Mem_dealloc('D',size(block))
      Deallocate(block)

      Call Mem_dealloc('I',size(ptEne0))
      Deallocate(ptEne0)
      Call Mem_dealloc('D',size(Ene0))
      Deallocate(Ene0)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_genDVR()

   USE Modal_mod

   Implicit None

   Integer :: mode,nGrid,pt
   Real(8), allocatable, dimension(:) :: xdvr,qq

      Do mode=1,Nfree
      Do nGrid=1,nCHO(mode)
         if(ptblock(nGrid,mode)<0) cycle
         
         Allocate(xdvr(nGrid*nGrid),qq(nGrid))

         Call DVR_genGrid(mode,nGrid,xdvr,qq)
         pt=ptblock(nGrid,mode)
         block(pt+1:pt+nGrid)=qq
         pt=pt+nGrid
         block(pt+1:pt+nGrid*nGrid)=xdvr

         Deallocate(xdvr,qq)

      End do
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!   iopt <  0 ... only diagonals:  Xwfn(:,j,j)
!        >= 0 ... all elements  :  Xwfn(:,j,k)
!

   Subroutine Modal_update(iopt)

   USE Modal_mod

   Implicit None

   Integer :: iopt,i,j,k,l,j1,j2,nGi,nG
   Real(8), dimension(:,:), allocatable :: Cwfn,Dwfn,xdvr,Cwfns
   Real(8), dimension(:,:,:), allocatable :: Xwfn

      Do i=1,Nfree
         if(nCHO(i)==0) cycle
         nGi=nCHO(i)
         Allocate(Cwfn(nGi,nGi),Dwfn(nGi,nGi),xdvr(nGi,nGi))
         Allocate(Xwfn(nGi,nGi,nGi))

         Call Modal_getCwfn(i,Cwfn)
         Call Modal_getxdvr(i,nGi,xdvr)
         Do j=1,nGi
            Do k=1,nGi
               Dwfn(k,j)=0.D+00
               Do l=1,nGi
                  Dwfn(k,j)=Dwfn(k,j) + xdvr(l,k)*Cwfn(l,j)
               End do
            End do
         End do
         if(iopt >= 0) then
            Do j1=1,nGi
            Do j2=1,nGi
               Do k=1,nGi
                  Xwfn(k,j1,j2)=Dwfn(k,j1)*Dwfn(k,j2)
               End do
            End do
            End do
         else
            Do j1=1,nGi
               Do k=1,nGi
                  Xwfn(k,j1,j1)=Dwfn(k,j1)*Dwfn(k,j1)
               End do
            End do
         endif
         Call Modal_setXwfn(i,nGi,Xwfn)

         Deallocate(Xwfn)
         Deallocate(Dwfn)
         Deallocate(xdvr)

         Do nG=1,nGi-1
            if(ptblock(nG,i)<0) cycle

            Allocate(Cwfns(nG,nG),Dwfn(nG,nG),xdvr(nG,nG))
            Allocate(Xwfn(nG,nG,nG))
            Call Modal_getxdvr(i,nG,xdvr)
            Do j=1,nG
            Do k=1,nG
               Cwfns(k,j)=Cwfn(k,j)
            End do
            End do
            Call orthonormalize(nG,Cwfns)

            Do j=1,nG
               Do k=1,nG
                  Dwfn(k,j)=0.D+00
                  Do l=1,nG
                     Dwfn(k,j)=Dwfn(k,j) + xdvr(l,k)*Cwfns(l,j)
                  End do
               End do
            End do
            Do j1=1,nG
            Do j2=1,nG
               Do k=1,nG
                  Xwfn(k,j1,j2)=Dwfn(k,j1)*Dwfn(k,j2)
               End do
            End do
            End do

            Call Modal_setXwfn(i,nG,Xwfn)

            Deallocate(Cwfns,xdvr,Dwfn,Xwfn)

         End do

         Deallocate(Cwfn)

      End do

   Contains

   Subroutine orthonormalize(nG,Cwfns)

   Implicit None

   Integer :: nG
   Real(8) :: Cwfns(nG,nG)
   Integer :: i,j,k
   Real(8) :: vec1(nG),vec2(nG),pp,ss

      ! Normalize
      Do i=1,nG
         vec1=Cwfns(:,i)
         ss=0.D+00
         Do j=1,nG
            ss=ss + vec1(j)*vec1(j)
         End do
         Cwfns(:,i)=vec1/sqrt(ss)
      End do

      ! Orthogonalize
      Do i=2,nG
         vec1=Cwfns(:,i)
         Do j=1,i-1
            vec2=Cwfns(:,j)
            pp=0.D+00
            Do k=1,nG
               pp=pp + vec1(k)*vec2(k)
            End do
            vec1=vec1 - pp*vec2
         End do

         ss=0.D+00
         Do j=1,nG
            ss=ss + vec1(j)*vec1(j)
         End do
         Cwfns(:,i)=vec1/sqrt(ss)
      End do

      !dbg write(6,'(i4)') nG
      !dbg Do i=1,nG
      !dbg    vec1=Cwfns(:,i)
      !dbg    Do j=1,i
      !dbg       vec2=Cwfns(:,j)
      !dbg       pp=0.D+00
      !dbg       Do k=1,nG
      !dbg          pp=pp + vec1(k)*vec2(k)
      !dbg       End do
      !dbg       write(6,'(2i4,f20.14)') i,j,pp
      !dbg    End do
      !dbg End do
      !dbg write(6,*)
      !dbg Stop

   End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getnCHO(nCHOin)

   USE Modal_mod

   Implicit None

   Integer :: nCHOin(Nfree)

      nCHOin=nCHO

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_setVSCFref(statein)

   USE Modal_mod

   Implicit None

   Integer :: statein(Nfree)

      statein=state

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getVSCFref(statein)

   USE Modal_mod

   Implicit None

   Integer :: statein(Nfree)

      statein=state

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_setCwfn(mode,Cwfn)

   USE Modal_mod

   Implicit None

   Integer :: mode, nG, nG2, pt
   Real(8) :: Cwfn(nCHO(mode)*nCHO(mode))

      nG=nCHO(mode)
      nG2=nG*nG
      pt=ptblock(nG,mode)+nG+nG2
      block(pt+1:pt+nG2)=Cwfn

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getCwfn(mode,Cwfn)

   USE Modal_mod

   Implicit None

   Integer :: mode,nG,nG2,pt
   Real(8) :: Cwfn(nCHO(mode)*nCHO(mode))

      nG=nCHO(mode)
      nG2=nG*nG
      pt=ptblock(nG,mode)+nG+nG2
      Cwfn=block(pt+1:pt+nG2)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_setXwfn(mode,nG,Xwfn)

   USE Modal_mod

   Implicit None

   Integer :: mode,nG,nG2,nG3,pt
   Real(8) :: Xwfn(nG*nG*nG)

      nG2=nG*nG
      nG3=nG2*nG
      pt=ptblock(nG,mode)+nG+nG2*2
      block(pt+1:pt+nG3)=Xwfn

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Input
!    mode  : mode
!      nG  : number of grid
!    qm,qn : quantum number of the modals (0 =< qm,qn =< nCHO(mode))
!  Output
!    Xwfn  : phi_{qm} (Qp) phi_{qn} (Qp)  (p=1 - nGrid)
!
   Subroutine Modal_getXwfn(mode,nG,qm,qn,Xwfn)

   USE Modal_mod

   Implicit None

   Integer :: mode,nG,nG2,qm,qn,pt
   Real(8) :: Xwfn(nG)

      if(qm>=nG .or. qn>=nG) then
         Xwfn=0.D+00
         return
      endif
      nG2=nG*nG
      pt=ptblock(nG,mode)+nG+nG2*2+nG2*qm+nG*qn
      Xwfn=block(pt+1:pt+nG)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_setEne(mode,Ein)

   USE Modal_mod

   Implicit None

   Integer :: mode, pt
   Real(8) :: Ein(nCHO(mode))

      pt=ptEne0(mode)
      Ene0(pt+1:pt+nCHO(mode))=Ein

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getEne(mode,Ein)

   USE Modal_mod

   Implicit None

   Integer :: mode, pt
   Real(8) :: Ein(nCHO(mode))

      pt=ptEne0(mode)
      Ein=Ene0(pt+1:pt+nCHO(mode))

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getEne_i(mode,qi,Ein)

   USE Modal_mod

   Implicit None

   Integer :: mode, qi
   Real(8) :: Ein

      Ein=Ene0(ptEne0(mode)+qi+1)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getxdvr(mode,nGrid,xdvr)

   USE Modal_mod

   Implicit None

   Integer :: mode,nGrid,nGrid2,pt
   Real(8) :: xdvr(nGrid*nGrid)

      nGrid2=nGrid*nGrid
      pt=ptblock(nGrid,mode)+nGrid
      xdvr=block(pt+1:pt+nGrid2)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getQ(mode,nGrid,qq)

   USE Modal_mod

   Implicit None

   Integer :: mode,nGrid,pt
   Real(8) :: qq(nGrid)

      pt=ptblock(nGrid,mode)
      qq=block(pt+1:pt+nGrid)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_getQ4(mode,qq)

   USE Modal_mod

   Implicit None

   Integer :: mode,nGrid,pt,i
   Real(8) :: qq(4*nCHO(mode))

      nGrid=nCHO(mode)
      Call Modal_getQ(mode,nGrid,qq(1:nGrid))
      Do i=1,nGrid
         qq(nGrid+i) = qq(i)*qq(i)
         qq(nGrid*2+i) = qq(nGrid+i)*qq(i)
         qq(nGrid*3+i) = qq(nGrid+i)*qq(nGrid+i)
      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Modal_isGrid(mode,nG,ex)

   USE Modal_mod

   Implicit None

   Integer :: mode,nG
   Logical :: ex

      if(ptblock(nG,mode) < 0) then
         ex=.false.
      else
         ex=.true.
      endif

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Modal_gettypeTable1(table)

   USE Modal_mod

   Implicit None

   Integer :: table(*)
   Integer :: i,j,nG

      j=1
      Do i=1,Nfree
      Do nG=1,nCHO(i)
         table(j)=ptblock(nG,i)
         j=j+1
      End do
      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_readVSCF(ierr,vscfFile)

   USE Modal_mod

   Implicit None

   Integer :: ierr,Nstate
   Integer :: i,nGi,rwf
   Real(8), allocatable :: Cwfn(:,:),Ene(:)
   Character(12) :: vscfFile

      ierr=0

      Call file_indicator(10,rwf)
      Open(rwf,file=trim(vscfFile),status='OLD',form='FORMATTED',err=100)
      Read(rwf,*)
      Read(rwf,*) state
      Read(rwf,*)
      Read(rwf,*) 
      Do i=1,Nfree
         Read(rwf,*)
         Read(rwf,*) nGi
         if(nGi==0) cycle
         Allocate(Cwfn(nGi,nGi),Ene(nGi))
         Read(rwf,*) Ene
         Read(rwf,*) 
         Read(rwf,*) Cwfn
         Call Modal_setCwfn(i,Cwfn)
         Call Modal_setEne(i,Ene)
         Deallocate(Cwfn,Ene)
      End do
      Close(rwf)
      Call Modal_update(0)

      return

  100 Continue
      ierr=-1

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Modal_init()

   USE Constants_mod
   USE Vib_mod, only : omegaf
   USE Modal_mod

   Implicit None

   Integer :: i,j,nGi
   Real(8) :: ww
   Real(8), allocatable :: Cwfn(:,:),Ene(:)

      Do i=1,Nfree
         nGi=nCHO(i)
         if(nGi==0) cycle
         Allocate(Cwfn(nGi,nGi),Ene(nGi))
         Cwfn=0.D+00
         Do j=1,nGi
            Cwfn(j,j)=1.D+00
         End do

         ww=omegaf(i)/H2wvn
         Do j=1,nGi
            Ene(j)=ww*(j-0.5D+00)
         End do
         !Ene=0.D+00

         Call Modal_setCwfn(i,Cwfn)
         Call Modal_setEne(i,Ene)
         Deallocate(Cwfn,Ene)
      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

