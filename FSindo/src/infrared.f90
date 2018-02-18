!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/31
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module IR_mod

   Real(8) :: minOmega,maxOmega,delOmega,fwhm,cutoff

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine IR_construct()

   USE IR_mod
   USE Constants_mod

   Namelist /IRspectrum/minOmega,maxOmega,delOmega,fwhm,cutoff

      ! -- Default --
      minOmega=100D+00
      maxOmega=4000.0D+00
      delOmega=1.0D+00
      fwhm = 20.D+00
      cutoff = -1.0D+00
      Rewind(inp)
      Read(inp,IRspectrum,end=10)
   10 Continue

      write(iout,100) minOmega, maxOmega, delOmega, fwhm
  100 Format(3x,'>> INFRARED SPECTRUM',/, &
             3x,'   MIN_OMEGA =',f12.2,/, &
             3x,'   MAX_OMEGA =',f12.2,/, &
             3x,'   DEL_OMEGA =',f12.2,/, &
             3x,'   FWHM      =',f12.2,/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine IR_genSpectrum(IRdata,IRspectrum)

   USE IR_mod

   Implicit None

   Character(*) :: IRdata,IRspectrum

   Integer :: i,j,k,ifl,nst,num
   Real(8) :: aa,bb,cc,ww,Intty

!   Real(8) :: cutoff
   Real(8), allocatable :: Ene(:),tty(:)

      Call file_indicator(30,ifl)
      Open(ifl,file=IRdata,status='old')
      Read(ifl,*)
      nst=0
      cc=-1.0D+00
      Do while(.true.)
         Read(ifl,*,end=100) aa,bb 
         if(bb>cc) cc=bb
         nst=nst+1
      End do
  100 Continue

      Allocate(Ene(nst),tty(nst))

      Rewind(ifl)
      Read(ifl,*)
      num=1
      Do i=1,nst
         Read(ifl,*) Ene(num),tty(num)
         if(tty(num) .gt. cutoff) num=num+1
      End do
      num=num-1

      Close(ifl)

      !chk Write(6,'(i4)') num
      !chk Write(6,'(2f12.4)') (Ene(i),tty(i),i=1,num)

      Open(ifl,file=IRspectrum,status='unknown')
      ww=minOmega
      Do while(ww .lt. maxOmega)
         Intty=0.D+00
         Do i=1,num
            if(abs(Ene(i)-ww) .gt. fwhm*50) cycle
            Intty=Intty + Lorentzian(ww,Ene(i),tty(i),fwhm)
         End do
         !Intty=Intty*100  ! km cm -> cm^2
         Write(ifl,'(2f12.4)') ww,Intty
         ww=ww+delOmega
      End do
      Close(ifl)

   Contains

   Function Lorentzian(ww,w0,A,G)

   Implicit None

      Real(8), parameter :: PI=3.14159265358979312D+00
      Real(8) :: Lorentzian,ww,w0,A,G,G2

      G2 = G*G
      !Lorentzian = A*G2 / ((ww-w0)*(ww-w0) + G2)
      Lorentzian = 2.0D+00 * A*G / (4.D+00*(ww-w0)*(ww-w0) + G2) / PI

   End Function

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
