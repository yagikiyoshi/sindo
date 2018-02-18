!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012 
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module mrpes_mod

     !     Nfree  :: Number of vibrational degrees of freedom
     !     Mfree  :: Number of active vibrational degrees of freedom
     !     MR     :: Mode Representation
     Integer :: Nfree
     Integer :: Mfree
     Integer :: MR
     Integer, parameter :: maxMR=4

     ! -- (Input parameters) ---------------------------------------------
     !     PotDir     :: Location of PEF data files
     !     Len_PotDir :: Length of PotDir
     !     mcs_cutoff :: Cutoff threshold for the mode coupling terms to be
     !                   removed from the Hamiltonian
     !     mcs_grid   :: Cutoff threshold for the grid function
     !             au :: if false the grid data is in Ang(amu)1/2 
     !                   [default = .true.]
     !        mopfile :: The name of the mop file

     Character(80) :: PotDir
     Integer       :: Len_PotDir
     Real(8)       :: mcs_cutoff, mcs_grid
     Logical       :: au
     Character(80) :: mopfile

     ! -- (MODE_TYPE FILE ) --------------------------------------------------
     !  type = 1 (QFF)
     !       = 2 (Grid PEF with Lagrange Interpolation)
     Integer, parameter :: maxtype=2,  &
                           type_qff=1, &
                           type_grd=2

     !  True if qff/gridPEF exists
     Logical :: qff
     Logical :: grid

     !  ntot     :: Number of nMR coupling terms
     !  nMR_type :: Number of nMR coupling terms for each PEF type
     Integer :: ntot(maxMR)
     Integer :: nMR_type(maxtype,maxMR)

     !  type_mode1(ntot(1)) :: Map of term number -> mode number
     !  type_mode_idx1      :: index for PEF type
     !  Modetype1(2,Nfree)  :: 1: PEF type, 2: term number
     Integer, allocatable :: type_mode1(:)
     Integer :: type_mode_idx1(maxtype+1)
     Integer, allocatable :: Modetype1(:,:)

     !  type_mode2(2,ntot(2)) :: Map of term number -> mode number
     !  type_mode_idx2        :: index for PEF type
     !  Modetype2, imt2
     Integer, allocatable :: type_mode2(:,:)
     Integer :: type_mode_idx2(maxtype+1)
     Integer, allocatable :: Modetype2(:,:)
     Integer :: imt2

     !  type_mode3(3,ntot(3)) :: Map of term number -> mode number
     !  type_mode_idx3        :: index for PEF type
     !  Modetype3, imt3
     Integer, allocatable :: type_mode3(:,:)
     Integer :: type_mode_idx3(maxtype+1)
     Integer, allocatable :: Modetype3(:,:)
     Integer :: imt3

     !  type_mode4(4,ntot(4)) :: Map of term number -> mode number
     !  type_mode_idx4        :: index for PEF type
     !  Modetype4, imt4
     Integer, allocatable :: type_mode4(:,:)
     Integer :: type_mode_idx4(maxtype+1)
     Integer, allocatable :: Modetype4(:,:)
     Integer :: imt4

End Module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_construct(NNfree,MR0,ActiveMode)

USE Constants_mod
USE mrpes_mod

Implicit None

   Integer :: NNfree,MR0
   Logical :: ActiveMode(NNfree)

   Integer :: ierr,ifl,ifw1(maxtype),ifw2(maxtype),ifw3(maxtype),ifw4(maxtype)
   Character :: fw*80,fp*80,num*2
   Character*80 :: Modetype2file,Modetype3file,Modetype4file

   Integer :: i,j,k,l,n,ij,ijk,ijkl,length,len_fw
   Real(8) :: mc_Int, mc_getInt2, mc_getInt3, mc_getInt4
   Logical :: ex

   Integer :: nn,ri,rj,b1,b2,b3
   Namelist /mrpes/MR,mcs_cutoff,mcs_grid,au,mopfile

      Nfree=NNfree

    ! - Mode coupling representation (default is the input arguement)
      MR=MR0
    ! - Cutoff value for the mode coupling term
      mcs_cutoff = 1.D-04
      mcs_grid = -1.D+00
    ! - The grid data is given in atomic unit
      au = .true.
    ! - Filename of the .mop file
      mopfile = ''
      Rewind(inp)
      Read(inp,mrpes,end=5)
    5 Continue

    ! Initialize
      nMR_type=0

    ! Location of WORK folder
      Call GetEnv('SINDOWORK',fw)
      len_fw=Len_Trim(fw)
      if(len_fw /=0) then
         fw(len_fw+1:)='/'
         len_fw=len_fw+1
      else
         fw='./'
         len_fw=2
      endif

    ! Location of DATA files
      Call GetEnv('POTDIR',PotDir)
      Len_PotDir=Len_Trim(PotDir)
      if(Len_PotDir /=0) then
         PotDir(Len_PotDir+1:)='/'
         Len_PotDir=Len_PotDir+1
      else
         PotDir='./'
         Len_PotDir=2
      endif
      fp=PotDir(:Len_PotDir)

    ! Setup QFF
      qff=.true.
      i=len_trim(mopfile)
      if(i /= 0) then
         Inquire(file=PotDir(:Len_PotDir)//mopfile(:i),exist=ex)
         if(.not. ex) then
            write(Iout,*) 'ERROR WHILE CONSTRUCTING MRPES MODULE'
            write(Iout,*) '   mopfile = ',mopfile(:i)
            write(Iout,*) 'IS NOT FOUND'
            Stop
         endif
         Call qff_construct()
         
      else
         ! Check if 001.hs exists
         Inquire(file=PotDir(:Len_PotDir)//'001.hs',exist=ex)
         if(.not. ex) then
            qff=.false.
         else
            if(MR == 4) MR=3
            Call qff_construct()
         endif

      endif

      if(qff) then
         Call mc_construct(Nfree)
      else
         mcs_grid = -1.D+00
      endif

    ! 1MR-PEFs

    ! Allocate Modetype
      Call Mem_alloc(-1,ierr,'I',Nfree)
      Allocate(Modetype1(2,Nfree))
      Modetype1=0

      Do i=1,maxtype
         Call file_indicator(200+i,ifw1(i))
         write(num,'(i2.2)') i
         Open(unit=ifw1(i),file=fw(:len_fw)//'mode_type'//num, &
              status='UNKNOWN', form='UNFORMATTED')
      End do

      Do i=1,Nfree
       ! Disable Mode i
         if(.not. ActiveMode(i)) cycle

       ! GRID function (Lagrange Interpolation)
         Call get_fname1(i,fp(Len_PotDir+1:))
         Call file_indicator(12,ifl)
         Inquire(file=fp,exist=ex)
         if(ex) then
            nMR_type(type_grd,1)=nMR_type(type_grd,1)+1
            write(ifw1(type_grd)) i
            Modetype1(1,i)=type_grd 
            Modetype1(2,i)=nMR_type(type_grd,1)
            cycle
         endif

       ! QFF
         if(qff) then
            nMR_type(type_qff,1)=nMR_type(type_qff,1)+1
            write(ifw1(type_qff)) i
            Modetype1(1,i)=type_qff 
            Modetype1(2,i)=nMR_type(type_qff,1)
            cycle
          endif

       ! Disable current Mode
         ActiveMode(i)=.false.

      End do

      type_mode_idx1(1)=0
      Do i=2,maxtype+1
         type_mode_idx1(i)=type_mode_idx1(i-1)+nMR_type(i-1,1)
      End do
      ntot(1)=type_mode_idx1(maxtype+1)

      if(ntot(1)==0) then
         Write(Iout,*) ' ERROR while constructing the MRPES module.'
         Write(Iout,*) ' No mode is defined.'
         Stop
      endif

      Call Mem_alloc(-1,ierr,'I',ntot(1))
      Allocate(type_mode1(ntot(1)))

      k=1
      Do i=1,maxtype
         if(nMR_type(i,1)/=0) then
            rewind(ifw1(i))
            Do j=1,nMR_type(i,1)
               Read(ifw1(i)) type_mode1(k)
               k=k+1
            End do
         endif
         Close(ifw1(i),status='DELETE')
      End do

      !dbg write(6,*) '1MR-PES'
      !dbg write(6,'(i4)') ntot(1)
      !dbg write(6,'(4i4)') nMR_type(:,1)
      !dbg Do i=1,maxtype
      !dbg    if(nMR_type(i,1)==0) cycle
      !dbg    write(6,'(''type='',i4)') i
      !dbg    write(6,'(i4)') type_mode1(type_mode_idx1(i)+1:type_mode_idx1(i+1))
      !dbg End do

      if(MR==1) goto 1000

    ! 2MR-PEFs

      Modetype2file=fw(:len_fw)//'mode_type2'
      Inquire(iolength=length) i,j
      Call file_indicator(100,imt2)
      Open(unit=imt2, file=Modetype2file, status='REPLACE', &
           access='DIRECT',recl=length)

      Do i=1,maxtype
         Call file_indicator(200+i,ifw2(i))
         write(num,'(i2.2)') i
         Open(unit=ifw2(i),file=fw(:len_fw)//'mode_type2_'//num, &
              status='UNKNOWN', form='UNFORMATTED')
      End do

      Do i=1,Nfree
       ! Cycle if Mode i does not exist
         if(.not. ActiveMode(i)) cycle

      Do j=1,i-1
       ! Cycle if Mode j does not exist
         if(.not. ActiveMode(j)) cycle

       ! Disable if mode coupling is weak
         if(qff) then
            mc_Int=mc_getInt2(i,j)
            if(mc_Int < mcs_cutoff) cycle
         else
            mc_Int=0.D+00
         endif

         ij=(i-1)*(i-2)/2 + j

       ! GRID function
         Call get_fname2(i,j,fp(Len_PotDir+1:))
         Inquire(file=fp,exist=ex)
         !if(ex .and. mc_Int > mcs_grid) then
         if(ex) then
            nMR_type(type_grd,2)=nMR_type(type_grd,2)+1
            write(ifw2(type_grd)) i,j
            write(imt2,rec=ij) type_grd,nMR_type(type_grd,2)
            cycle
         endif

       ! QFF
         if(qff) then
            nMR_type(type_qff,2)=nMR_type(type_qff,2)+1
            write(ifw2(type_qff)) i,j
            write(imt2,rec=ij) type_qff,nMR_type(type_qff,2)
            cycle
         endif

       ! DISABLE
       !  write(imt2,rec=ij) 0,0

      End do
      End do

      type_mode_idx2(1)=0
      Do i=2,maxtype+1
         type_mode_idx2(i)=type_mode_idx2(i-1)+nMR_type(i-1,2)
      End do
      ntot(2)=type_mode_idx2(maxtype+1)

      if(ntot(2)>0) then
         Call Mem_alloc(-1,ierr,'I',ntot(2)*2)
         Allocate(type_mode2(2,ntot(2)))
         k=1
         Do i=1,maxtype
            if(nMR_type(i,2)/=0) then
               rewind(ifw2(i))
               Do j=1,nMR_type(i,2)
                  Read(ifw2(i)) type_mode2(:,k)
                  k=k+1
               End do
            endif
            Close(ifw2(i),status='DELETE')
         End do
      else
         Do i=1,maxtype
            Close(ifw2(i),status='DELETE')
         End do
      endif

      !dbg write(6,*) '2MR-PES'
      !dbg write(6,'(i4)') ntot(2)
      !dbg write(6,'(4i4)') nMR_type(:,2)
      !dbg Do i=1,maxtype
      !dbg    if(nMR_type(i,2)==0) cycle
      !dbg    write(6,'(''type='',i4)') i
      !dbg    write(6,'(2i4)') type_mode2(:,type_mode_idx2(i)+1:type_mode_idx2(i+1))
      !dbg End do

      if(MR==2) goto 1000

    ! 3MR-PEFs

      Modetype3file=fw(:len_fw)//'mode_type3'
      Inquire(iolength=length) i,j
      Call file_indicator(100,imt3)
      Open(unit=imt3, file=Modetype3file, status='REPLACE', &
           access='DIRECT',recl=length)

      Do i=1,maxtype
         Call file_indicator(200+i,ifw3(i))
         write(num,'(i2.2)') i
         Open(unit=ifw3(i),file=fw(:len_fw)//'mode_type3_'//num, &
              status='UNKNOWN', form='UNFORMATTED')
      End do

      Do i=3,Nfree
       ! Cycle if Mode i does not exist
         if(.not. ActiveMode(i)) cycle

      Do j=2,i-1
       ! Cycle if Mode j does not exist
         if(.not. ActiveMode(j)) cycle

      Do k=1,j-1
       ! Cycle if Mode k does not exist
         if(.not. ActiveMode(k)) cycle

       ! Disable if mode coupling is weak
         if(qff) then
            mc_Int=mc_getInt3(i,j,k)
            if(mc_Int < mcs_cutoff)  cycle
         else
            mc_Int=0.D+00
         endif

         ijk=(i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k

       ! GRID function
         Call get_fname3(i,j,k,fp(Len_PotDir+1:))
         Inquire(file=fp,exist=ex)
         !if(ex .and. mc_Int > mcs_grid) then
         if(ex) then
            nMR_type(type_grd,3)=nMR_type(type_grd,3)+1
            write(ifw3(type_grd)) i,j,k
            write(imt3,rec=ijk) type_grd,nMR_type(type_grd,3)

            cycle
         endif

       ! QFF
         if(qff) then
            nMR_type(type_qff,3)=nMR_type(type_qff,3)+1
            write(ifw3(type_qff)) i,j,k
            write(imt3,rec=ijk) type_qff,nMR_type(type_qff,3)
            cycle
         endif

       ! DISABLE
       !  write(imt3,rec=ijk) 0,0

      End do
      End do
      End do

      type_mode_idx3(1)=0
      Do i=2,maxtype+1
         type_mode_idx3(i)=type_mode_idx3(i-1)+nMR_type(i-1,3)
      End do
      ntot(3)=type_mode_idx3(maxtype+1)

      if(ntot(3)>0) then
         Call Mem_alloc(-1,ierr,'I',ntot(3)*3)
         Allocate(type_mode3(3,ntot(3)))
         k=1
         Do i=1,maxtype
            if(nMR_type(i,3)/=0) then
               rewind(ifw3(i))
               Do j=1,nMR_type(i,3)
                  Read(ifw3(i)) type_mode3(:,k)
                  k=k+1
               End do
            endif
            Close(ifw3(i),status='DELETE')
         End do

      else
         Do i=1,maxtype
            Close(ifw3(i),status='DELETE')
         End do

      endif

      !dbg write(6,*) '3MR-PES'
      !dbg write(6,'(i4)') ntot(3)
      !dbg write(6,'(4i4)') nMR_type(:,3)
      !dbg Do i=1,maxtype
      !dbg    if(nMR_type(i,3)==0) cycle
      !dbg    write(6,'(''type='',i4)') i
      !dbg    write(6,'(3i4)') type_mode3(:,type_mode_idx3(i)+1:type_mode_idx3(i+1))
      !dbg End do

      if(MR==3) goto 1000

    ! 4MR-PEFs

      Modetype4file=fw(:len_fw)//'mode_type4'
      Inquire(iolength=length) i,j
      Call file_indicator(100,imt4)
      Open(unit=imt4, file=Modetype4file, status='REPLACE', &
           access='DIRECT',recl=length)

      Do i=1,maxtype
         Call file_indicator(200+i,ifw4(i))
         write(num,'(i2.2)') i
         Open(unit=ifw4(i),file=fw(:len_fw)//'mode_type'//num, &
              status='UNKNOWN', form='UNFORMATTED')
      End do

      Do i=4,Nfree
       ! Cycle if Mode i does not exist
         if(.not. ActiveMode(i)) cycle

      Do j=3,i-1
       ! Cycle if Mode j does not exist
         if(.not. ActiveMode(j)) cycle

      Do k=2,j-1
       ! Cycle if Mode k does not exist
         if(.not. ActiveMode(k)) cycle

      Do l=1,k-1
       ! Cycle if Mode k does not exist
         if(.not. ActiveMode(l)) cycle

       ! Disable if mode coupling is weak
         if(qff) then
            mc_Int=mc_getInt4(i,j,k,l)
            if(mc_Int < mcs_cutoff)  cycle
         endif

         ijkl=(i-1)*(i-2)*(i-3)*(i-4)/24 + (j-1)*(j-2)*(j-3)/6 &
            + (k-1)*(k-2)/2 + l

       ! GRID function
         if(mcs_grid > 0.0d+00) then
            if(qff .and. mc_int < mcs_grid) then
               goto 400
            endif
         endif
         Call get_fname4(i,j,k,l,fp(Len_PotDir+1:))
         Call file_indicator(12,ifl)
         Open(unit=ifl,file=fp,status='OLD',err=400)
         nMR_type(type_grd,4)=nMR_type(type_grd,4)+1
         write(ifw4(type_grd)) i,j,k,l
         write(imt4,rec=ijkl) type_grd,nMR_type(type_grd,4)
         Close(ifl)
         cycle

         400 Continue

       ! QFF
         if(qff) then
            nMR_type(type_qff,4)=nMR_type(type_qff,4)+1
            write(ifw4(type_qff)) i,j,k,l
            write(imt4,rec=ijkl) type_qff,nMR_type(type_qff,4)
            cycle
         endif

       ! DISABLE
       !  write(imt4,rec=ijkl) 0,0

      End do
      End do
      End do
      End do

      type_mode_idx4(1)=0
      Do i=2,maxtype+1
         type_mode_idx4(i)=type_mode_idx4(i-1)+nMR_type(i-1,4)
      End do
      ntot(4)=type_mode_idx4(maxtype+1)

      if(ntot(4)>0) then
         Call Mem_alloc(-1,ierr,'I',ntot(4)*4)
         Allocate(type_mode4(4,ntot(4)))
         k=1
         Do i=1,maxtype
            if(nMR_type(i,4)/=0) then
               rewind(ifw4(i))
               Do j=1,nMR_type(i,4)
                  Read(ifw4(i)) type_mode4(:,k)
                  k=k+1
               End do
            endif
            Close(ifw4(i),status='DELETE')
         End do

      else
         Do i=1,maxtype
            Close(ifw4(i),status='DELETE')
         End do

      endif

      !write(6,*) '4MR-PES'
      !write(6,'(i4)') ntot(4)
      !write(6,'(4i4)') nMR_type(:,4)
      !Do i=1,maxtype
      !   if(nMR_type(i,4)==0) cycle
      !   write(6,'(''type='',i4)') i
      !   write(6,'(4i4)') type_mode4(:,type_mode_idx4(i)+1:type_mode_idx4(i+1))
      !End do

      if(MR==4) goto 1000

 1000 Continue

      grid=.false.
      Do i=1,maxMR
         if(nMR_type(type_grd,i) > 0) then
            grid=.true.
            exit
         endif
      End do
      if(grid) Call grid_construct()

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_destruct()

USE Constants_mod
USE mrpes_mod

   Logical :: op

     Call Mem_dealloc('I',size(Modetype1))
     Deallocate(Modetype1)
     Call Mem_dealloc('I',size(type_mode1))
     Deallocate(type_mode1)

     if(allocated(type_mode2)) then
        Call Mem_dealloc('I',size(type_mode2))
        Deallocate(type_mode2)
     endif
     Inquire(imt2,opened=op)
     if(op) Close(imt2)

     if(allocated(type_mode3)) then
        Call Mem_dealloc('I',size(type_mode3))
        Deallocate(type_mode3)
     endif
     Inquire(imt3,opened=op)
     if(op) Close(imt3)

     if(allocated(type_mode4)) then
        Call Mem_dealloc('I',size(type_mode4))
        Deallocate(type_mode4)
     endif
     Inquire(imt4,opened=op)
     if(op) Close(imt4)

     if(qff) then 
        Call qff_destruct()
        Call mc_destruct()
     endif
     if(grid) Call grid_destruct()

     Write(iout,100)
 100 Format(/,'(  FINALIZE MRPES MODULE  )',/)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_printSettings()

USE Constants_mod
USE mrpes_mod

Implicit None

   Integer :: i,j,k,l,ifl,mi,mj,mk,ml,ni,nj,nk,nl
   Character :: title*40,fp*80,tQ1*80,tQ2*80,tQ3*80,tQ4*80

      fp=PotDir(:Len_PotDir)

      if(qff) Call qff_getTitle(tQ1,tQ2,tQ3,tQ4)

      write(iout,'(/,''(  SETUP MRPES MODULE  )'',/)')
      write(iout,100) MR,mcs_cutoff,mcs_grid
  100 Format(3x,'>> POTENTIAL',/,       &
             /,6x,'[  OPTIONS  ]',//,     &
               9x,'MR         = ',i8,/,   &
               9x,'MCS_CUTOFF = ',e8.2,/, &
               9x,'MCS_GRID   = ',e8.2)

      write(iout,110) trim(PotDir)
  110 Format(9x,'POTDIR     = ',a)

      if(qff) then
         if(len_trim(mopfile) /= 0) then
            write(iout,112) trim(mopfile)
         else
            write(iout,114)
         endif
      else
         write(iout,*)
      endif
  112 Format(9x,'MOPFILE    = ',a,/)
  114 Format(9x,'QFF DATA READ FROM 001.hs',/)

    ! 1MR-PEF
      write(iout,'(/,9x,''1MR-PEF'',/)')

      ! GRID PEF
      if(nMR_type(type_grd,1)/=0) then
         write(iout,120)
         Do i=1,nMR_type(type_grd,1)
            mi=type_mode1(type_mode_idx1(type_grd)+i)
            Call get_fname1(mi,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ni
            Close(ifl)
            write(iout,121) mi,ni,trim(title)
         End do
         write(iout,*)
      endif

      ! QFF
      if(nMR_type(type_qff,1)/=0) then
         write(iout,130) trim(tQ1)
         if(nMR_type(type_qff,1)<30) then
            Do i=1,nMR_type(type_qff,1)
               write(iout,131) type_mode1(type_mode_idx1(type_qff)+i)
            End do
         else
            write(iout,135) nMR_type(type_qff,1)
         endif
         write(iout,*)

      endif

      ! DISABLED
      Mfree=Nfree
      if(ntot(1)/=Nfree) then
         write(iout,140) 
         Do i=1,Nfree
            if(Modetype1(1,i)==0) then
               write(iout,131) i
               Mfree=Mfree-1
            endif
         End do
      endif

      if(MR==1) goto 1000

      write(iout,'(/,9x,''2MR-PEF'',/)')
      if(nMR_type(type_grd,2)/=0) then
         write(iout,120)
         Do i=1,nMR_type(type_grd,2)
            mi=type_mode2(1,type_mode_idx2(type_grd)+i)
            mj=type_mode2(2,type_mode_idx2(type_grd)+i)
            Call get_fname2(mi,mj,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ni,nj
            close(ifl)
            write(iout,122) mi,mj,ni,nj,trim(title)
         End do
         write(iout,*)

      endif

      if(nMR_type(type_qff,2)/=0) then
         write(iout,130) trim(tQ2)
         if(nMR_type(type_qff,2)<30) then
            Do i=1,nMR_type(type_qff,2)
               write(iout,132) type_mode2(:,type_mode_idx2(type_qff)+i)
            End do
         else
            write(iout,135) nMR_type(type_qff,2)
         endif
         write(iout,*)

      endif

      j=Mfree*(Mfree-1)/2 - ntot(2)
      if(j>0) then
         write(iout,150)
         write(iout,155) j
      endif

      if(MR==2) goto 1000

      write(iout,'(/,9x,''3MR-PEF'',/)')
      if(nMR_type(type_grd,3)/=0) then
         write(iout,120)
         Do i=1,nMR_type(type_grd,3)
            mi=type_mode3(1,type_mode_idx3(type_grd)+i)
            mj=type_mode3(2,type_mode_idx3(type_grd)+i)
            mk=type_mode3(3,type_mode_idx3(type_grd)+i)
            Call get_fname3(mi,mj,mk,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ni,nj,nk
            close(ifl)
            write(iout,123) mi,mj,mk,ni,nj,nk,trim(title)
         End do
         write(iout,*)
      endif

      if(nMR_type(type_qff,3)/=0) then
         write(iout,130) trim(tQ3)
         if(nMR_type(type_qff,3)<30) then
            Do i=1,nMR_type(type_qff,3)
               write(iout,133) type_mode3(:,type_mode_idx3(type_qff)+i)
            End do
         else
            write(iout,135) nMR_type(type_qff,3)
         endif
         write(iout,*)

      endif

      j=Mfree*(Mfree-1)*(Mfree-2)/6 - ntot(3)
      if(j>0) then
         write(iout,150)
         write(iout,155) j
      endif

      if(MR==3) goto 1000

      write(iout,'(/,9x,''4MR-PEF'',/)')
      if(nMR_type(type_grd,4)/=0) then
         write(iout,120)
         Do i=1,nMR_type(type_grd,4)
            mi=type_mode4(1,type_mode_idx4(type_grd)+i)
            mj=type_mode4(2,type_mode_idx4(type_grd)+i)
            mk=type_mode4(3,type_mode_idx4(type_grd)+i)
            ml=type_mode4(4,type_mode_idx4(type_grd)+i)
            Call get_fname4(mi,mj,mk,ml,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') title
            Read(ifl,*)
            Read(ifl,*) ni,nj,nk,nl
            close(ifl)
            write(iout,124) mi,mj,mk,ml,ni,nj,nk,nl,trim(title)
         End do
         write(iout,*)
      endif

      if(nMR_type(type_qff,4)/=0) then
         write(iout,130) trim(tQ4)
         if(nMR_type(type_qff,4)<30) then
            Do i=1,nMR_type(type_qff,4)
               write(iout,134) type_mode4(:,type_mode_idx4(type_qff)+i)
            End do
         else
            write(iout,135) nMR_type(type_qff,4)
         endif
         write(iout,*)

      endif

      j=Mfree*(Mfree-1)*(Mfree-2)*(Mfree-3)/24 - ntot(4)
      if(j>0) then
         write(iout,150)
         write(iout,155) j
      endif

      if(MR==4) goto 1000

      1000 Continue
 
  120 Format(9x,'o GRID PEF',/)
  121 Format(11x,'MODE=',i4,', GRID=',i4,3x,a)
  122 Format(11x,'MODE=',2i4,', GRID=',2i4,3x,a)
  123 Format(11x,'MODE=',3i4,', GRID=',3i4,3x,a)
  124 Format(11x,'MODE=',4i4,', GRID=',4i4,3x,a)
  130 Format(9x,'o QFF  ',a,/)
  131 Format(11x,'MODE=',i4)
  132 Format(11x,'MODE=',2i4)
  133 Format(11x,'MODE=',3i4)
  134 Format(11x,'MODE=',4i4)
  135 Format(11x,'NUMBER OF TERMS=',i8)
  140 Format(9x,'o DISABLED',/)
  150 Format(9x,'o CUTOFF: ')
  155 Format(11x,'NEGLECTED TERMS=',i8,/)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    xx :: Bohr
!     V :: Hartree
!
!Subroutine mrpes_getVcart(xx,V)
!
!USE mrpes_mod
!
!Implicit None
!
!   Real(8) :: xx(3,Nat),qq(Nfree),V
!
!      Call nma_x2q(xx,qq)
!      Call mrpes_getV(qq,V)
!
!End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    qq :: Bohr(emu)^1/2
!     V :: Hartree

Subroutine mrpes_getV(qq,V)

USE mrpes_mod

Implicit None

   Real(8) :: qq(Nfree),V

   Integer :: i,j,k,mm(4)
   Real(8) :: Vg,Vq

      V=0.D+00

    ! Grid
      Do i=1,nMR_type(type_grd,1)
         Call grid_getV1(i,qq,Vg,mm(1))
         V=V+Vg
      End do
      !write(6,'(i4,f12.6)') 1,V

    ! QFF
      Do i=1,nMR_type(type_qff,1)
         Call qff_getV1(i,qq,Vq,mm(1))
         V=V+Vq
      End do
      !write(6,'(i4,f12.6)') 2,V

      if(MR==1) goto 1000

      Do i=1,nMR_type(type_grd,2)
         Call grid_getV2(i,qq,Vg,mm(1:2))
         !if(Vg<-1.D-02) write(6,'(2i4,f12.6)') mm(1:2),Vg
         V=V+Vg
      End do
      !write(6,'(i4,f12.6)') 3,V

      Do i=1,nMR_type(type_qff,2)
         Call qff_getV2(i,qq,Vq,mm(1:2))
         !if(Vq<-1.D-02) write(6,'(2i4,f12.6)') mm(1:2),Vq
         V=V+Vq
      End do
      !write(6,'(i4,f12.6)') 4,V

      if(MR==2) goto 1000

      Do i=1,nMR_type(type_grd,3)
         Call grid_getV3(i,qq,Vg,mm)
         !if(Vg<-1.D-02) write(6,'(3i4,f12.6)') mm,Vg
         V=V+Vg
      End do
      !write(6,'(i4,f12.6)') 5,V

      Do i=1,nMR_type(type_qff,3)
         Call qff_getV3(i,qq,Vq,mm)
         !if(Vq<-1.D-02) write(6,'(3i4,f12.6)') mm,Vq
         V=V+Vq
      End do
      !write(6,'(i4,f12.6)') 6,V

      if(MR==3) goto 1000

      Do i=1,nMR_type(type_grd,4)
         !ToDo
         !Call grid_getV4(i,qq,Vg,mm)
         !if(Vg<-1.D-02) write(6,'(3i4,f12.6)') mm,Vg
         V=V+Vg
      End do
      !write(6,'(i4,f12.6)') 7,V

      Do i=1,nMR_type(type_qff,4)
         Call qff_getV4(i,qq,Vq,mm)
         !if(Vq<-1.D-02) write(6,'(3i4,f12.6)') mm,Vq
         V=V+Vq
      End do
      !write(6,'(i4,f12.6)') 8,V

 1000 Continue

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    qq :: Bohr(emu)^1/2
!     V :: Hartree
!     G :: Hartree/Bohr(emu)^1/2

Subroutine mrpes_getVG(qq,V,G)

USE mrpes_mod

Implicit None

   Real(8) :: qq(Nfree),V,G(Nfree)

   Integer :: i,j,k,mm(4)
   Real(8) :: Vg,Vq,g1,g2(2),g3(3),g4(4)

      V=0.D+00
      G=0.D+00

    ! Grid
      Do i=1,nMR_type(type_grd,1)
         Call grid_getVG1(i,qq,Vg,g1,mm(1))
         V=V+Vg
         G(mm(1))=G(mm(1)) + g1
      End do

    ! QFF
      Do i=1,nMR_type(type_qff,1)
         Call qff_getVG1(i,qq,Vq,g1,mm(1))
         V=V+Vq
         G(mm(1))=G(mm(1)) + g1
      End do

      if(MR==1) goto 1000

      Do i=1,nMR_type(type_grd,2)
         Call grid_getVG2(i,qq,Vg,g2,mm(1:2))
         V=V+Vg
         G(mm(1))=G(mm(1)) + g2(1)
         G(mm(2))=G(mm(2)) + g2(2)
      End do

      Do i=1,nMR_type(type_qff,2)
         Call qff_getVG2(i,qq,Vq,g2,mm(1:2))
         V=V+Vq
         G(mm(1))=G(mm(1)) + g2(1)
         G(mm(2))=G(mm(2)) + g2(2)
      End do

      if(MR==2) goto 1000

      Do i=1,nMR_type(type_grd,3)
         Call grid_getVG3(i,qq,Vg,g3,mm)
         V=V+Vg
         G(mm(1))=G(mm(1)) + g3(1)
         G(mm(2))=G(mm(2)) + g3(2)
         G(mm(3))=G(mm(3)) + g3(3)
      End do

      Do i=1,nMR_type(type_qff,3)
         Call qff_getVG3(i,qq,Vq,g3,mm)
         V=V+Vq
         G(mm(1))=G(mm(1)) + g3(1)
         G(mm(2))=G(mm(2)) + g3(2)
         G(mm(3))=G(mm(3)) + g3(3)
      End do

      if(MR==3) goto 1000

      Do i=1,nMR_type(type_grd,4)
         !ToDo
         !Call grid_getVG4(i,qq,Vg,g4,mm)
         V=V+Vg
         G(mm(1))=G(mm(1)) + g4(1)
         G(mm(2))=G(mm(2)) + g4(2)
         G(mm(3))=G(mm(3)) + g4(3)
         G(mm(4))=G(mm(4)) + g4(4)
      End do

      Do i=1,nMR_type(type_qff,4)
         Call qff_getVG4(i,qq,Vq,g4,mm)
         V=V+Vq
         G(mm(1))=G(mm(1)) + g4(1)
         G(mm(2))=G(mm(2)) + g4(2)
         G(mm(3))=G(mm(3)) + g4(3)
         G(mm(4))=G(mm(4)) + g4(4)
      End do

 1000 Continue

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    qq :: Bohr(emu)^1/2
!     V :: Hartree
!     G :: Hartree/Bohr(emu)^1/2

Subroutine mrpes_getVGH(qq,V,G,H)

USE mrpes_mod

Implicit None

   Real(8) :: qq(Nfree),V,G(Nfree),H(Nfree,Nfree)

   Integer :: i,j,k,mm(4)
   Real(8) :: Vg,Vq,g1,g2(2),g3(3),g4(4),h1,h2(2,2),h3(3,3),h4(4,4)

      V=0.D+00
      G=0.D+00
      H=0.D+00

    ! Grid
      Do i=1,nMR_type(type_grd,1)
         Call grid_getVGH1(i,qq,Vg,g1,h1,mm(1))
         V=V+Vg
         G(mm(1))=G(mm(1)) + g1
         H(mm(1),mm(1))=H(mm(1),mm(1)) + h1
      End do

    ! QFF
      Do i=1,nMR_type(type_qff,1)
         Call qff_getVGH1(i,qq,Vq,g1,h1,mm(1))
         V=V+Vq
         G(mm(1))=G(mm(1)) + g1
         H(mm(1),mm(1))=H(mm(1),mm(1)) + h1
      End do

      if(MR==1) goto 1000

      Do i=1,nMR_type(type_grd,2)
         Call grid_getVGH2(i,qq,Vg,g2,h2,mm(1:2))
         V=V+Vg
         Call addGH(2,mm(1:2),g2,h2)
      End do

      Do i=1,nMR_type(type_qff,2)
         Call qff_getVGH2(i,qq,Vq,g2,h2,mm(1:2))
         V=V+Vq
         Call addGH(2,mm(1:2),g2,h2)
      End do

      if(MR==2) goto 1000

      Do i=1,nMR_type(type_grd,3)
         Call grid_getVGH3(i,qq,Vg,g3,h3,mm(1:3))
         V=V+Vg
         Call addGH(3,mm(1:3),g3,h3)
      End do

      Do i=1,nMR_type(type_qff,3)
         Call qff_getVGH3(i,qq,Vq,g3,h3,mm(1:3))
         V=V+Vq
         Call addGH(3,mm(1:3),g3,h3)
      End do

      if(MR==3) goto 1000

      Do i=1,nMR_type(type_grd,4)
         !ToDo
         !Call grid_getVGH4(i,qq,Vg,g4,h4,mm)
         V=V+Vg
         Call addGH(4,mm,g4,h4)
      End do

      Do i=1,nMR_type(type_qff,4)
         Call qff_getVGH4(i,qq,Vq,g4,h4,mm)
         V=V+Vq
         Call addGH(4,mm,g4,h4)
      End do

 1000 Continue

   Contains

   Subroutine addGH(n,mm,gn,hn)

   Implicit None

      Integer :: n,mm(n),j,k
      Real(8) :: gn(n),hn(n,n)

         Do j=1,n
            G(mm(j))=G(mm(j)) + gn(j)
            H(mm(j),mm(j))=H(mm(j),mm(j)) + hn(j,j)
            Do k=1,j-1
               H(mm(j),mm(k))=H(mm(j),mm(k)) + hn(j,k)
               H(mm(k),mm(j))=H(mm(j),mm(k))
            End do
         End do

   End subroutine

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Function mrpes_getNumOfTerm(itype,nMR)

USE mrpes_mod

Implicit None

   Integer :: itype,nMR,mrpes_getNumOfTerm

      mrpes_getNumOfTerm = nMR_type(itype,nMR)

End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_getMode1(itype,ll,mi)

USE mrpes_mod

Implicit None

   Integer :: itype,ll,mi

      mi=type_mode1(type_mode_idx1(itype)+ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_getMode2(itype,ll,mm)

USE mrpes_mod

Implicit None

   Integer :: itype,ll,mm(2)

      mm=type_mode2(:,type_mode_idx2(itype)+ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_getMode3(itype,ll,mm)

USE mrpes_mod

Implicit None

   Integer :: itype,ll,mm(3)

      mm=type_mode3(:,type_mode_idx3(itype)+ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine mrpes_getMode4(itype,ll,mm)

USE mrpes_mod

Implicit None

   Integer :: itype,ll,mm(4)

      mm=type_mode4(:,type_mode_idx4(itype)+ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Get the type of PES and the index of term for mi
!
Subroutine mrpes_getidx1(mi,itype,iterm)

USE mrpes_mod

Implicit None

   Integer :: mi,itype,iterm

      itype=Modetype1(1,mi)
      iterm=Modetype1(2,mi)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Get the type of PES and the index of term for (mi>mj)
!
Subroutine mrpes_getidx2(mi,mj,itype,iterm)

USE mrpes_mod

Implicit None

   Integer :: mi,mj,mij,itype,iterm

      mij=(mi-1)*(mi-2)/2 + mj
      Read(imt2,rec=mij,err=10) itype,iterm
      return

   10 Continue
      itype=0
      iterm=0

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Get the type of PES and the index of term for (mi>mj>mk)
!
Subroutine mrpes_getidx3(mi,mj,mk,itype,iterm)

USE mrpes_mod

Implicit None

   Integer :: mi,mj,mk,mijk,itype,iterm

      mijk=(mi-1)*(mi-2)*(mi-3)/6 + (mj-1)*(mj-2)/2 + mk
      Read(imt3,rec=mijk,err=10) itype,iterm
      return

   10 Continue
      itype=0
      iterm=0

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

!  Get the type of PES and the index of term for (mi>mj>mk>ml)
!
Subroutine mrpes_getidx4(mi,mj,mk,ml,itype,iterm)

USE mrpes_mod

Implicit None

   Integer :: mi,mj,mk,ml,mijkl,itype,iterm

      mijkl=(mi-1)*(mi-2)*(mi-3)*(mi-4)/24 + (mj-1)*(mj-2)*(mj-3)/6 &
          + (mk-1)*(mk-2)/2 + ml
      Read(imt4,rec=mijkl,err=10) itype,iterm
      return

   10 Continue
      itype=0
      iterm=0

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
