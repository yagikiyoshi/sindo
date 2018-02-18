!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/30
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Mem_mod

   ! Maxmem : Maximum size of memory (byte)
   !   imem : Current size of memory (byte)
   !   jmem : Max memory used so far (byte)
   !   kmem : Max memory used in the last step (byte)

   Integer(8) :: Maxmem,imem,jmem,kmem

   ! I_byteSize  : Byte length of Integer
   ! R_byteSize  : Byte length of Real
   ! D_byteSize  : Byte length of Dble
   ! C_byteSite  : Byte length of Character
   Integer, parameter :: I_byteSize=kind(1)       , &
                         R_byteSize=kind(1.0e+00) , &
                         D_byteSize=kind(1.0d+00) , &
                         C_byteSize=kind('a')

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Mem_construct()

   USE Constants_mod
   USE Mem_mod

   Namelist /sys/Maxmem

      !  >> Read &sys
      !  >> default memory: 300 MB.
      Maxmem=300
      Read(Inp,sys,end=10)
   10 Continue

      write(Iout,100)
      if(Maxmem>0) then
         write(Iout,110) Maxmem
         Maxmem=Maxmem*10**6
      else
         write(Iout,120)
      endif

      imem=0.D+00
      jmem=0.D+00
      kmem=0.D+00

  100 Format('(  SETUP MEM MODULE  )',/)
  110 Format(3x,'o MAXIMUM MEMORY : ',i8,' MB',/)
  120 Format(3x,'o MAXIMUM MEMORY : UNLIMITED',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! -- Print memory information
!
   Subroutine Mem_printInfo

   USE Constants_mod
   USE Mem_mod

   Real(8) :: rmem,smem,rmaxmem

      rmem=dble(imem)
      smem=dble(kmem)
      rmaxmem=dble(Maxmem)
      kmem=imem

      Write(Iout,100) rmem/1.0E+06,rmaxmem/1.0D+06,rmem/rmaxmem*1.0E+02, &
                      smem/1.0E+06,rmaxmem/1.0D+06,smem/rmaxmem*1.0E+02

  100 Format(12x,'(MEMORY INFO)',/, &
  12x,'-- CURRENT USAGE    [ ',f8.2,' MB / ',f8.2,' MB (',f5.1,' % ) ]  -- ',/, &
  12x,'-- IN THE LAST STEP [ ',f8.2,' MB / ',f8.2,' MB (',f5.1,' % ) ]  -- ',/)

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Mem_finalInfo

   USE Constants_mod
   USE Mem_mod

   Real(8) :: smem,rmaxmem

      smem=dble(jmem)
      rmaxmem=dble(Maxmem)

      Write(Iout,100) smem/1.0E+06,rmaxmem/1.0D+06,smem/rmaxmem*1.0E+02

  100 Format(/, &
      3x,'(TOTAL MEMORY USAGE)   [ ',f8.2,' MB / ',f8.2,' MB (',f5.1,' % ) ] ', &
      /)

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! iopt  :: =0, Silent mode
!          =1, Output (but don't stop)
!          =-1, Output and stop when error.
!
! ctype  :: Type of the variable
!            'i' or 'I' :: Integer
!            'r' or 'R' :: Real(4)
!            'd' or 'D' :: Dble
!            'c' or 'C' :: Character
!
! iSize :: Size of the array
!
! istat ::  = 0, Success
!           =-1, Fail (not enough space)
!
   Subroutine Mem_alloc(iopt,istat,ctype,iSize)

   USE Mem_mod

   Integer   :: iopt,istat,iSize
   Character :: ctype

      if(ctype=='i' .or. ctype=='I') then 
         Call Mem_alloc2(iopt,istat,I_byteSize,iSize)
      elseif(ctype=='r' .or. ctype=='R') then 
         Call Mem_alloc2(iopt,istat,R_byteSize,iSize)
      elseif(ctype=='d' .or. ctype=='D') then
         Call Mem_alloc2(iopt,istat,D_byteSize,iSize)
      elseif(ctype=='c' .or. ctype=='C') then
         Call Mem_alloc2(iopt,istat,C_byteSize,iSize)
      endif

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! iopt  :: =0, Silent mode
!          =1, Output (but don't stop)
!          =-1, Output and stop when error.
!
! ikind :: Kind of the variable
!
! iSize :: Size of the array
!
! istat ::  = 0, Success
!           =-1, Fail (not enough space)
!
   Subroutine Mem_alloc2(iopt,istat,ikind,iSize)

   USE Constants_mod
   USE Mem_mod

   Integer   :: iopt,istat,ikind,iSize
   Integer(8):: ri
   Real(8)   :: rj

      ri=iSize*ikind
      if(imem+ri < Maxmem .or. Maxmem<0) then
         istat=0
         imem=imem+ri
         if(imem>jmem) jmem=imem
         if(imem>kmem) kmem=imem

      else
         istat=-1
         if(iopt/=0) then 
            Write(Iout,*) 'ERROR WHILE MEMORY ALLOCATION'
            Write(Iout,*) 
            Call Mem_printInfo
            rj=dble(ri)*1.D-06
            Write(Iout,*) 'ERROR::  NOT ENOUGH MEMORY SPACE TO ALLOCATE'
            Write(Iout,'('' ERROR::'',f12.1)') rj
            Write(Iout,*) 'ERROR::  MEGA BYTE.'
            Write(Iout,*) 
            if(iopt==-1) Stop
         endif
           
      endif

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! Request of the memory deallocation
!
! ctype  :: Type of the variable
!            'i' or 'I' :: Integer
!            'r' or 'R' :: Real(4)
!            'd' or 'D' :: Real(8)
!            'c' or 'C' :: Character
!
! iSize :: Size of the array
!
   Subroutine Mem_dealloc(ctype,iSize)

   USE Mem_mod

   Integer   :: iSize
   Character :: ctype
   Integer(8):: ri

      if(ctype=='i' .or. ctype=='I') then 
         Call Mem_dealloc2(I_byteSize,iSize)
      elseif(ctype=='r' .or. ctype=='R') then 
         Call Mem_dealloc2(R_byteSize,iSize)
      elseif(ctype=='d' .or. ctype=='D') then
         Call Mem_dealloc2(D_byteSize,iSize)
      elseif(ctype=='c' .or. ctype=='C') then
         Call Mem_dealloc2(C_byteSize,iSize)
      endif

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! Request of the memory deallocation
!
! ikind :: Kind of the variable
! iSize :: Size of the array
!
   Subroutine Mem_dealloc2(ikind,iSize)

   USE Mem_mod

   Integer   :: ikind,iSize
   Integer(8):: ri

      ri=iSize*ikind
      imem=imem-ri

      return

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
