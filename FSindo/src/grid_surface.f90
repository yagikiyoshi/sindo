!   Last modified  2013/04/30
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Grid_surface_mod

   ! -- (Property surface parameters) --------------------------------------- 
   !  MR    :: Mode Coupling Representation
   !  ext   :: Extension of the datafile
   !  nData :: Number of data
   !  nSx   :: Num. of xMR terms
   !  mSx   :: Mode of each terms

   Integer :: MR
   Character(10) :: ext
   Integer :: nData
   Integer :: nS1,nS2,nS3,nS4
   Integer, dimension(:,:), allocatable :: mS1,mS2,mS3,mS4
   Character(80) :: PotDir

   ! Value at the origin
   !      V0nData) :: The values on Grid
   Real(8), dimension(:), allocatable :: V0

   ! 1-mode terms
   !   nG1(1,nS1*nData) :: Number of grid points
   !    ptV1(nS1*nData) :: Pointer of V1
   !      V1(nS1*nData) :: The values on Grid
   Integer, dimension(:,:), allocatable :: nG1
   Integer, dimension(:), allocatable :: ptV1
   Real(8), dimension(:), allocatable :: V1

   ! 2-mode terms
   !   nG2(2,nS2*nData) :: Number of grid points
   !    ptV2(nS2*nData) :: Pointer of V2
   !      V2(nS2*nData) :: The values on Grid
   Integer, dimension(:,:), allocatable :: nG2
   Integer, dimension(:), allocatable :: ptV2
   Real(8), dimension(:), allocatable :: V2

   ! 3-mode terms
   !   nG3(3,nS3*nData) :: Number of grid points
   !    ptV3(nS3*nData) :: Pointer of V3
   !      V3(nS3*nData) :: The values on Grid
   Integer, dimension(:,:), allocatable :: nG3
   Integer, dimension(:), allocatable :: ptV3
   Real(8), dimension(:), allocatable :: V3

   ! 4-mode terms
   !   nG4(4,nS4*nData) :: Number of grid points
   !    ptV4(nS4*nData) :: Pointer of V4
   !      V4(nS4*nData) :: The values on Grid
   Integer, dimension(:,:), allocatable :: nG4
   Integer, dimension(:), allocatable :: ptV4
   Real(8), dimension(:), allocatable :: V4


   Contains

   Subroutine setNumOfTerms(Nfree,nCHO)

   USE Constants_mod

   Implicit None

   Integer :: Nfree,nCHO(Nfree)
   Integer :: i1,i2,i3,i4,ierr,ifl,ii,n
   Character(30) :: fname
   Logical :: ex

    ! Location of DATA files
      Call GetEnv('POTDIR',PotDir)
      i1=Len_Trim(PotDir)
      if(i1/=0) then
         PotDir=trim(PotDir)//'/'
      else
         PotDir='./'
      endif

      nS1=0; nS2=0; nS3=0; nS4=0
      nData=-1

      Do i1=1,Nfree
         if(nCHO(i1) <= 0) cycle
         Call get_fnameExt1(i1,fname,trim(ext))
         Inquire(file=trim(PotDir)//fname,exist=ex)
         if(ex) then
            nS1=nS1+1 
            if(nData<0) then
               Call file_indicator(10,ifl)
               Open(ifl,file=trim(PotDir)//fname,status='old')
               Read(ifl,*)
               Read(ifl,*)
               Read(ifl,*) ii,nData
               Close(ifl)
            endif
         endif
         !write(6,'(a)') fname
      End do

      if(nS1==0) then
         Write(iout,'(''  ERROR: NO DATA FOUND FOR THE PROPERTY '',a10)') trim(ext)
         Write(iout,'(''  ERROR: TERMINATED IN GRID_SURFACE_CONSTRUCT '')')
         Stop
      endif

      Call Mem_alloc(-1,ierr,'I',nS1)
      Allocate(mS1(1,nS1))
      n=1
      Do i1=1,Nfree
         if(nCHO(i1) <= 0) cycle
         Call get_fnameExt1(i1,fname,trim(ext))
         Inquire(file=trim(PotDir)//fname,exist=ex)
         if(ex) then
            mS1(1,n)=i1
            n=n+1
         endif
      End do

      if(MR==1) goto 100

      nS2=0
      Do i1=2,Nfree
      Do i2=1,i1-1
         if(nCHO(i1) <= 0 .or. nCHO(i2) <= 0) cycle
         Call get_fnameExt2(i1,i2,fname,trim(ext))
         Inquire(file=trim(PotDir)//fname,exist=ex)
         if(ex) then
            nS2=nS2+1 
         endif
      End do
      End do

      if(nS2 /= 0) then
         Call Mem_alloc(-1,ierr,'I',nS2*2)
         Allocate(mS2(2,nS2))
         n=1
         Do i1=2,Nfree
         Do i2=1,i1-1
            if(nCHO(i1) <= 0 .or. nCHO(i2) <= 0) cycle
            Call get_fnameExt2(i1,i2,fname,trim(ext))
            Inquire(file=trim(PotDir)//fname,exist=ex)
            if(ex) then
               mS2(1,n)=i1
               mS2(2,n)=i2
               n=n+1
            endif
         End do
         End do
      endif

      if(MR==2) goto 100

      nS3=0
      Do i1=3,Nfree
      Do i2=2,i1-1
      Do i3=1,i2-1
         if(nCHO(i1) <= 0 .or. nCHO(i2) <= 0 .or. nCHO(i3) <=0) cycle
         Call get_fnameExt3(i1,i2,i3,fname,trim(ext))
         Inquire(file=trim(PotDir)//fname,exist=ex)
         if(ex) then
            nS3=nS3+1 
         endif
      End do
      End do
      End do

      if(nS3 /= 0) then
         Call Mem_alloc(-1,ierr,'I',nS3*3)
         Allocate(mS3(3,nS3))
         n=1
         Do i1=3,Nfree
         Do i2=2,i1-1
         Do i3=1,i2-1
            if(nCHO(i1) <= 0 .or. nCHO(i2) <= 0 .or. nCHO(i3) <=0) cycle
            Call get_fnameExt3(i1,i2,i3,fname,trim(ext))
            Inquire(file=trim(PotDir)//fname,exist=ex)
            if(ex) then
               mS3(1,n)=i1
               mS3(2,n)=i2
               mS3(3,n)=i3
               n=n+1 
            endif
         End do
         End do
         End do
      endif

      if(MR==3) goto 100

      nS4=0
      Do i1=4,Nfree
      Do i2=3,i1-1
      Do i3=2,i2-1
      Do i4=1,i3-1
         Call get_fnameExt4(i1,i2,i3,i4,fname,trim(ext))
         Inquire(file=trim(PotDir)//fname,exist=ex)
         if(ex) then
            nS4=nS4+1 
         endif
      End do
      End do
      End do
      End do

      if(nS4 /= 0) then
         Call Mem_alloc(-1,ierr,'I',nS4*4)
         Allocate(mS4(4,nS4))
         n=1
         Do i1=4,Nfree
         Do i2=3,i1-1
         Do i3=2,i2-1
         Do i4=1,i3-1
            Call get_fnameExt4(i1,i2,i3,i4,fname,trim(ext))
            Inquire(file=trim(PotDir)//fname,exist=ex)
            if(ex) then
               mS4(1,n)=i1
               mS4(2,n)=i2
               mS4(3,n)=i3
               mS4(4,n)=i4
               n=n+1 
            endif
         End do
         End do
         End do
         End do
      endif

      if(MR==4) goto 100

  100 Continue

   End subroutine


   Subroutine setGrid(Nfree,nCHO)

   Implicit None

      Integer :: Nfree,nCHO(Nfree)
      Integer :: ii,i,j,k,l,n,ifl,ierr
      Integer :: ng1a,ng2a,ng3a,ng4a
      Character(30) :: fname,title
      Real(8), allocatable :: qa1(:),qa2(:),qa3(:),qa4(:)

      Call file_indicator(10,ifl)

      Call Mem_alloc(-1,ierr,'I',nS1)
      Call Mem_alloc(-1,ierr,'I',(nS1+1))
      Allocate(nG1(1,nS1),ptV1(nS1+1))

      Do n=1,nS1
         Call get_fnameExt1(mS1(1,n),fname,trim(ext))
         Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
         Read(ifl,'(a)') title
         Read(ifl,*)
         Read(ifl,*) ng1a,nData
         Read(ifl,*)

         Allocate(qa1(ng1a))
         Do i=1,ng1a
            Read(ifl,*) qa1(i)
         End do
         Close(ifl)

         Call optGrid(mS1(1,n),ng1a,qa1(1),qa1(ng1a))
         if(ng1a > nCHO(mS1(1,n))) ng1a=nCHO(mS1(1,n))
         nG1(1,n)=ng1a

         Deallocate(qa1)
      End do

      ptV1(1)=0
      Do i=2,nS1+1
         ptV1(i)=ptV1(i-1) + nG1(1,i-1)*nData
      End do
      i=ptV1(nS1+1)
      Call Mem_alloc(-1,ierr,'D',i)
      Allocate(V1(i))

      if(nS2 /= 0) then
         Call Mem_alloc(-1,ierr,'I',nS2*2)
         Call Mem_alloc(-1,ierr,'I',(nS2+1))
         Allocate(nG2(2,nS2),ptV2(nS2+1))
         Do n=1,nS2
            Call get_fnameExt2(mS2(1,n),mS2(2,n),fname,trim(ext))
            Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ng2a,ng1a,nData
            Read(ifl,*)
  
            Allocate(qa1(ng1a),qa2(ng2a))
            Do i=1,ng1a
            Do j=1,ng2a
               Read(ifl,*) qa2(j),qa1(i)
            End do
            End do
            Close(ifl)

            if(ng1a > nCHO(mS2(1,n))) ng1a=nCHO(mS2(1,n))
            if(ng2a > nCHO(mS2(2,n))) ng2a=nCHO(mS2(2,n))
  
            Call optGrid(mS2(1,n),ng1a,qa1(1),qa1(ng1a))
            Call optGrid(mS2(2,n),ng2a,qa2(1),qa2(ng2a))
            nG2(1,n)=ng1a
            nG2(2,n)=ng2a
  
            Deallocate(qa1,qa2)
           
         End do

         ptV2(1)=0
         Do i=2,nS2+1
            ptV2(i)=ptV2(i-1) + nG2(1,i-1)*nG2(2,i-1)*nData
         End do
         i=ptV2(nS2+1)
         Call Mem_alloc(-1,ierr,'D',i)
         Allocate(V2(i))

      endif

      if(nS3 /= 0) then
         Call Mem_alloc(-1,ierr,'I',nS3*3)
         Call Mem_alloc(-1,ierr,'I',(nS3+1))
         Allocate(nG3(3,nS3),ptV3(nS3+1))
         Do n=1,nS3
            Call get_fnameExt3(mS3(1,n),mS3(2,n),mS3(3,n),fname,trim(ext))
            Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ng3a,ng2a,ng1a,nData
            Read(ifl,*)
  
            Allocate(qa1(ng1a),qa2(ng2a),qa3(ng3a))
            Do i=1,ng1a
            Do j=1,ng2a
            Do k=1,ng3a
               Read(ifl,*) qa3(k),qa2(j),qa1(i)
            End do
            End do
            End do
            Close(ifl)
  
            if(ng1a > nCHO(mS3(1,n))) ng1a=nCHO(mS3(1,n))
            if(ng2a > nCHO(mS3(2,n))) ng2a=nCHO(mS3(2,n))
            if(ng3a > nCHO(mS3(3,n))) ng3a=nCHO(mS3(3,n))
            Call optGrid(mS3(1,n),ng1a,qa1(1),qa1(ng1a))
            Call optGrid(mS3(2,n),ng2a,qa2(1),qa2(ng2a))
            Call optGrid(mS3(3,n),ng3a,qa3(1),qa3(ng3a))
            nG3(1,n)=ng1a
            nG3(2,n)=ng2a
            nG3(3,n)=ng3a
  
            Deallocate(qa1,qa2,qa3)
           
         End do

         ptV3(1)=0
         Do i=2,nS3+1
            ptV3(i)=ptV3(i-1) + nG3(1,i-1)*nG3(2,i-1)*nG3(3,i-1)*nData
         End do
         i=ptV3(nS3+1)
         Call Mem_alloc(-1,ierr,'D',i)
         Allocate(V3(i))

      endif

      if(nS4 /= 0) then
         Call Mem_alloc(-1,ierr,'I',nS4*4)
         Call Mem_alloc(-1,ierr,'I',(nS4+1))
         Allocate(nG4(4,nS4),ptV4(nS4+1))
         Do n=1,nS4
            Call get_fnameExt4(mS4(1,n),mS4(2,n),mS4(3,n),mS4(4,n), &
                               fname,trim(ext))
            Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ng4a,ng3a,ng2a,ng1a,nData
            Read(ifl,*)
  
            Allocate(qa1(ng1a),qa2(ng2a),qa3(ng3a),qa4(ng4a))
            Do i=1,ng1a
            Do j=1,ng2a
            Do k=1,ng3a
            Do l=1,ng4a
               Read(ifl,*) qa4(l),qa3(k),qa2(j),qa1(i)
            End do
            End do
            End do
            End do
            Close(ifl)
  
            if(ng1a > nCHO(mS4(1,n))) ng1a=nCHO(mS4(1,n))
            if(ng2a > nCHO(mS4(2,n))) ng2a=nCHO(mS4(2,n))
            if(ng3a > nCHO(mS4(3,n))) ng3a=nCHO(mS4(3,n))
            if(ng4a > nCHO(mS4(4,n))) ng4a=nCHO(mS4(4,n))
  
            Call optGrid(mS4(1,n),ng1a,qa1(1),qa1(ng1a))
            Call optGrid(mS4(2,n),ng2a,qa2(1),qa2(ng2a))
            Call optGrid(mS4(3,n),ng3a,qa3(1),qa3(ng3a))
            Call optGrid(mS4(4,n),ng4a,qa4(1),qa4(ng4a))
            nG4(1,n)=ng1a
            nG4(2,n)=ng2a
            nG4(3,n)=ng3a
            nG4(4,n)=ng4a
  
            Deallocate(qa1,qa2,qa3,qa4)
  
         End do

         ptV4(1)=0
         Do i=2,nS4+1
            ptV4(i)=ptV4(i-1) + nG4(1,i-1)*nG4(2,i-1)*nG4(3,i-1)*nG4(4,i-1)*nData
         End do
         i=ptV4(nS4+1)
         Call Mem_alloc(-1,ierr,'D',i)
         Allocate(V4(i))

      endif

   End subroutine

   Subroutine optGrid(mode,nG,b1min,b1max)

   Implicit None 

   Integer :: mode,nG,nGm
   Real(8) :: b1min,b1max,b2min,b2max
   Real(8), parameter :: allow_extrapolation=1.10D+00
   Real(8) :: qm(nG),xdvr(nG,nG)

      b1min=b1min*allow_extrapolation
      b1max=b1max*allow_extrapolation
      nGm=nG

   10 Continue
      Call DVR_genGrid(mode,nGm,xdvr,qm)
      b2max=qm(1)
      b2min=qm(nGm)
      if(b2min < b1min .or. b1max < b2max) then
         nGm=nGm-1
         goto 10
      endif

      nG=nGm

   End subroutine


End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Grid_surface_construct(Nfree,MRin,extn,nCHO)

   USE Grid_surface_mod

   Implicit None

   Integer :: Nfree,MRin,nCHO(Nfree),i
   Character(10) :: extn

      MR=MRin
      ext=extn
      Call setNumOfTerms(Nfree,nCHO)
      Call setGrid(Nfree,nCHO)
      Call Grid_surface_printSettings()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Grid_surface_destruct()

   USE Grid_surface_mod

   Implicit None

      Call Mem_dealloc('D',size(V0))
      Deallocate(V0)

      if(nS1 /= 0) then
         Call Mem_dealloc('I',size(mS1))
         Deallocate(mS1)
         Call Mem_dealloc('I',size(nG1))
         Deallocate(nG1)
         Call Mem_dealloc('I',size(ptV1))
         Deallocate(ptV1)
         Call Mem_dealloc('D',size(V1))
         Deallocate(V1)
      endif

      if(nS2 /= 0) then
         Call Mem_dealloc('I',size(mS2))
         Deallocate(mS2)
         Call Mem_dealloc('I',size(nG2))
         Deallocate(nG2)
         Call Mem_dealloc('I',size(ptV2))
         Deallocate(ptV2)
         Call Mem_dealloc('D',size(V2))
         Deallocate(V2)
      endif

      if(nS3 /= 0) then
         Call Mem_dealloc('I',size(mS3))
         Deallocate(mS3)
         Call Mem_dealloc('I',size(nG3))
         Deallocate(nG3)
         Call Mem_dealloc('I',size(ptV3))
         Deallocate(ptV3)
         Call Mem_dealloc('D',size(V3))
         Deallocate(V3)
      endif

      if(nS4 /= 0) then
         Call Mem_dealloc('I',size(mS4))
         Deallocate(mS4)
         Call Mem_dealloc('I',size(nG4))
         Deallocate(nG4)
         Call Mem_dealloc('I',size(ptV4))
         Deallocate(ptV4)
         Call Mem_dealloc('D',size(V2))
         Deallocate(V4)
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Grid_surface_setValue

   USE Grid_surface_mod

   Implicit None

   Character(30) :: fname
   Integer :: ifl

   Integer :: m1,m2,m3,m4
   Integer :: ng1a,ng2a,ng3a,ng4a
   Integer :: ng1b,ng2b,ng3b,ng4b
   Real(8), allocatable :: qa1(:),qa2(:),qa3(:),qa4(:)
   Real(8), allocatable :: qb1(:),qb2(:),qb3(:),qb4(:)
   Real(8), allocatable, dimension(:,:)       :: dat1a,dat1p
   Real(8), allocatable, dimension(:,:,:)     :: dat2a,dat2p
   Real(8), allocatable, dimension(:,:,:,:)   :: dat3a,dat3p
   Real(8), allocatable, dimension(:,:,:,:,:) :: dat4a,dat4p

   Integer :: ii,i,j,k,l,n,pt
   Logical :: ex

      Call file_indicator(10,ifl)

      Call Mem_alloc(-1,i,'D',nData)
      Allocate(V0(nData))
      Call get_fnameExt0(fname,trim(ext))
      Inquire(file=trim(PotDir)//fname,exist=ex)
      if(.not. ex) then
         Call get_oldfnameExt0(fname,trim(ext))
      endif
      Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
      Read(ifl,*)
      Read(ifl,*)
      Read(ifl,*)
      Read(ifl,*) V0
      Close(ifl)

      Do n=1,nS1

         m1=mS1(1,n)

         Call get_fnameExt1(m1,fname,trim(ext))
         Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
         Read(ifl,*)
         Read(ifl,*)
         Read(ifl,*) ng1a,nData
         Read(ifl,*)

         Allocate(qa1(ng1a),dat1a(ng1a,nData),dat1p(ng1a,nData))
         Do i=1,ng1a
            Read(ifl,*) qa1(i),(dat1a(i,j),j=1,nData)
         End do
         Do i=1,nData
            Call lag1_Const(ng1a,qa1,dat1a(:,i),dat1p(:,i))
         End do

         Close(ifl)

         ng1b=nG1(1,n)
         Allocate(qb1(ng1b))

         Call Modal_getQ(m1,ng1b,qb1)
         pt=1
         Do i=1,nData
         Do j=1,ng1b
            Call lag1_getV(ng1a,qa1,dat1a(:,i),dat1p(:,i),qb1(j),V1(ptV1(n)+pt))
            pt=pt+1
         End do
         End do

         Deallocate(qa1,qb1,dat1a,dat1p)

      End do

      Do n=1,nS2

         m1=mS2(1,n)
         m2=mS2(2,n)

         Call get_fnameExt2(m1,m2,fname,trim(ext))
         Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
         Read(ifl,*)
         Read(ifl,*)
         Read(ifl,*) ng2a,ng1a,nData
         Read(ifl,*)

         Allocate(qa1(ng1a),qa2(ng2a),dat2a(ng2a,ng1a,nData),dat2p(ng2a,ng1a,nData))
         Do i=1,ng1a
         Do j=1,ng2a
            Read(ifl,*) qa2(j),qa1(i),(dat2a(j,i,k),k=1,nData)
         End do
         End do
         Do i=1,nData
            Call lag2_Const(ng2a,ng1a,qa2,qa1,dat2a(:,:,i),dat2p(:,:,i))
         End do

         Close(ifl)

         ng1b=nG2(1,n)
         ng2b=nG2(2,n)
         Allocate(qb1(ng1b),qb2(ng2b))

         Call Modal_getQ(m1,ng1b,qb1)
         Call Modal_getQ(m2,ng2b,qb2)
         pt=1
         Do i=1,nData
         Do j=1,ng1b
         Do k=1,ng2b
            Call lag2_getV(ng2a,ng1a,qa2,qa1,dat2a(:,:,i),dat2p(:,:,i), &
                           qb2(k),qb1(j),V2(ptV2(n)+pt))
            pt=pt+1
         End do
         End do
         End do

         Deallocate(qa1,qa2,qb1,qb2,dat2a,dat2p)
        
      End do

      Do n=1,nS3

         m1=mS3(1,n)
         m2=mS3(2,n)
         m3=mS3(3,n)

         Call get_fnameExt3(m1,m2,m3,fname,trim(ext))
         Open(unit=ifl,file=trim(PotDir)//fname,status='OLD')
         Read(ifl,*)
         Read(ifl,*)
         Read(ifl,*) ng3a,ng2a,ng1a,nData
         Read(ifl,*)

         Allocate(qa1(ng1a),qa2(ng2a),qa3(ng3a))
         Allocate(dat3a(ng3a,ng2a,ng1a,nData),dat3p(ng3a,ng2a,ng1a,nData))
         Do i=1,ng1a
         Do j=1,ng2a
         Do k=1,ng3a
            Read(ifl,*) qa3(k),qa2(j),qa1(i),(dat3a(k,j,i,l),l=1,nData)
         End do
         End do
         End do
         Do i=1,nData
            Call lag3_Const(ng3a,ng2a,ng1a,qa3,qa2,qa1,dat3a(:,:,:,i),dat3p(:,:,:,i))
         End do

         Close(ifl)

         ng1b=nG3(1,n)
         ng2b=nG3(2,n)
         ng3b=nG3(3,n)
         Allocate(qb1(ng1b),qb2(ng2b),qb3(ng3b))

         Call Modal_getQ(mS3(1,n),ng1b,qb1)
         Call Modal_getQ(mS3(2,n),ng2b,qb2)
         Call Modal_getQ(mS3(3,n),ng3b,qb3)

         pt=1
         Do ii=1,nData
         Do i=1,ng1b
         Do j=1,ng2b
         Do k=1,ng3b
            Call lag3_getV(ng3a,ng2a,ng1a,qa3,qa2,qa1,dat3a(:,:,:,ii),dat3p(:,:,:,ii), &
                           qb3(k),qb2(j),qb1(i),V3(ptV3(n)+pt))
            pt=pt+1
         End do
         End do
         End do
         End do

         Deallocate(qa1,qa2,qa3,qb1,qb2,qb3,dat3a,dat3p)

      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Grid_surface_printSettings()

   USE Grid_surface_mod
   USE Constants_mod

   Implicit None

   Integer :: n,ifl
   Character(30) :: fname,title

      write(iout,100) MR,ext
  100 Format(6x,'>> PROPERTY SURFACE',/,    &
             9x,'MR         = ',i8,/,       &
             9x,'EXT        = ',a10)

      write(iout,110) trim(PotDir)
  110 Format(9x,'POTDIR     = ',a)

    ! 1MR
      write(iout,'(/,9x,''1MR-PRPT SURFACE'',/)')

      if(nS1/=0) then
         Do n=1,nS1
            Call get_fnameExt1(mS1(1,n),fname,trim(ext))
            Call file_indicator(12,ifl)
            Open(ifl,file=trim(PotDir)//fname,status='old')
            Read(ifl,'(a)') title
            Close(ifl)
            write(iout,121) mS1(1,n),nG1(1,n),trim(title)
         End do
         write(iout,*)
      endif

  121 Format(11x,'MODE=',i4,', GRID=',i4,3x,a)

    ! 2MR
      write(iout,'(/,9x,''2MR-PRPT SURFACE'',/)')

      if(nS2/=0) then
         Do n=1,nS2
            Call get_fnameExt2(mS2(1,n),mS2(2,n),fname,trim(ext))
            Call file_indicator(12,ifl)
            Open(ifl,file=trim(PotDir)//fname,status='old')
            Read(ifl,'(a)') title
            Close(ifl)
            write(iout,122) mS2(1,n),mS2(2,n),nG2(1,n),nG2(2,n),trim(title)
         End do
         write(iout,*)
      endif

  122 Format(11x,'MODE=',2i4,', GRID=',2i4,3x,a)


    ! 3MR
      write(iout,'(/,9x,''3MR-PRPT SURFACE'',/)')

      if(nS3/=0) then
         Do n=1,nS3
            Call get_fnameExt3(mS3(1,n),mS3(2,n),mS3(3,n),fname,trim(ext))
            Call file_indicator(12,ifl)
            Open(ifl,file=trim(PotDir)//fname,status='old')
            Read(ifl,'(a)') title
            Close(ifl)
            write(iout,123) mS3(1,n),mS3(2,n),mS3(3,n),nG3(1,n),nG3(2,n),nG3(3,n),trim(title)
         End do
         write(iout,*)
      endif

  123 Format(11x,'MODE=',3i4,', GRID=',3i4,3x,a)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
