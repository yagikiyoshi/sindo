!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/05/01
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Prpt_mod

   USE Vib_mod, only: Nfree

   ! Maximum number of property
   Integer, parameter :: MaxNprpt=30

   ! MR            :: Mode coupling rep. for the property surface
   ! Nprpt         :: Number of property
   ! extn(Nprpt)   :: Extension of the file
   ! matrix(Nprpt) :: =0, calc. only the average
   !                  >0, calc. the matrix, <m|P|n>
   Integer :: MR, Nprpt
   Character(10) :: extn(MaxNprpt)
   Integer :: matrix(MaxNprpt)

   ! vscfprpt   :: calc. propoerties for VSCF wavefunction
   ! vciprpt    :: calc. propoerties for VCI wavefunction
   ! vptprpt    :: calc. propoerties for VPT wavefunction
   ! vqdptprpt  :: calc. propoerties for VPT wavefunction
   Logical :: vscfprpt,vciprpt,vptprpt,vqdptprpt

   ! infrared    :: Calculate the infrared spectrum if true
   Logical :: infrared

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Prpt_construct()

   USE Constants_mod
   USE Prpt_mod

   Implicit None

   Integer :: i,j

   Namelist /prpt/vscfprpt,vciprpt,vptprpt,vqdptprpt,MR,extn,matrix,infrared

      write(iout,100)
  100 Format(/,'(  ENTER PROPERTY MODULE  )',/)

      ! --- DEFAULT ---
      vscfprpt=.false.
      vciprpt=.false.
      vptprpt=.false.
      vqdptprpt=.false.

      MR=3
      matrix=0
      extn=''

      infrared=.false.

      Rewind(inp)
      Read(inp,prpt,end=10)
   10 Continue

      if(Nprpt>MaxNprpt+1) then
         write(iout,'(''  ERROR: EXCEEDED MAXIMUM NUMBER OF PROPERTY'')')
         write(iout,'(''  ERROR: MAX_NPRPT ='',i4)') MaxNprpt+1
         write(iout,'(''  ERROR: TERMINATED IN PRPT_CONSTRUCT '')')
         Stop
      endif
      if((.not. vscfprpt) .and. &
         (.not. vciprpt)  .and. &
         (.not. vptprpt)  .and. &
         (.not. vqdptprpt)) then
         write(iout,'(''  ERROR: WAVEFUNCTION IS NOT SPECIFIED '')')
         write(iout,'(''  ERROR: AVAILABLE OPTIONS ARE'')')
         write(iout,'(''  ERROR:    o VSCFPRPT'')')
         write(iout,'(''  ERROR:    o VCIPRPT'')')
         write(iout,'(''  ERROR:    o VPTPRPT'')')
         write(iout,'(''  ERROR:    o VQDPTPRPT'')')
         Stop
      endif

      Nprpt=0
      Do i=1,MaxNprpt
         if(extn(i) /= '') then
            Nprpt=Nprpt+1
            extn(i)='.'//extn(i)
         else
            exit
         endif
      End do
      if(Nprpt==0 .and. .not. infrared) then
         write(iout,'(''  ERROR: PROPERTY INFO NOT FOUND IN THE INPUT'')')
         write(iout,'(''  ERROR: TERMINATED IN PRPT_CONSTRUCT '')')
         Stop
      endif

      write(iout,110) MR,Nprpt,vscfprpt,vciprpt,vptprpt,vqdptprpt
  110 Format(3x,'>> RUN OPTIONS',/ &
             6x,'MR    = ',i6,/, &
             6x,'NPRPT = ',i6,/, &
             6x,'VSCF  = ',l6,/, &
             6x,'VCI   = ',l6,/, &
             6x,'VPT   = ',l6,/, &
             6x,'VQDPT = ',l6,/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Prpt_destruct()

   USE Constants_mod

      Write(iout,100)
  100 Format(/,'(  FINALIZE PROPERTY MODULE  )',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

