!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
Module fse_mod

   Integer :: pp,MM
   Real(8) :: PI,PI2,T0,dt,dt2
   Complex(8) :: ii

   Real(8), allocatable :: fn(:)
   Complex(8), allocatable :: c1(:)

End Module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine fse_init(p,t)

   USE fse_mod

   Implicit None

   Integer :: p
   Real(8) :: t

      PI=Acos(-1.0D+00)
      PI2=PI*2.D+00
      ii=(0.D+00,1.D+00)

      pp=p
      MM=2*pp+1
      T0=t
      dt=T0/MM
      dt2=dt/2.D+00

      Allocate(c1(0:MM-1),fn(-pp:pp))

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine fse_setFunc(ffn)

   USE fse_mod

   Implicit None

   Integer :: m,n
   Real(8) :: ffn(-pp:pp)

      fn=ffn
      ! Numerical Fourier expansion
      Do m=0,MM-1
         c1(m)=0.D+00
         Do n=-pp,pp
            c1(m)=c1(m)+fn(n)*exp(-ii*PI2*dble(m*n)/dble(MM))
         End do
         c1(m)=c1(m)/dble(MM)
      End do
      !write(6,'(i5,2f12.4)') (m,c1(m),m=0,MM-1)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine fse_getFunc(tt,ff)

   USE fse_mod

   Implicit None

   Integer :: m
   Real(8) :: tt,ff
   Complex(8) :: ft,d0

      d0=ii*PI2*tt/T0
      ft=0.D+00
      Do m=1,pp
         ft=ft+c1(m)*exp(d0*dble(m))
      End do
      ff=dble(c1(0))+2.D+00*dble(ft)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine fse_getGrad(tt,gg)

   USE fse_mod

   Implicit None

   Integer :: m
   Real(8) :: tt,gg
   Complex(8) :: ft,d0,d1

      d0=ii*PI2/T0
      d1=d0*tt
      ft=0.D+00
      Do m=1,pp
         ft=ft+d0*dble(m)*c1(m)*exp(dble(m)*d1)
      End do
      gg=2.D+00*dble(ft)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine fse_getHess(tt,hh)

   USE fse_mod

   Implicit None

   Integer :: m
   Real(8) :: tt,hh
   Complex(8) :: ft,d0,d1

      d0=PI2/T0
      d0=d0*d0
      d1=ii*PI2*tt/T0
      ft=0.D+00
      Do m=1,pp
         ft=ft+d0*dble(m*m)*c1(m)*exp(dble(m)*d1)
      End do
      hh=-2.D+00*dble(ft)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine fse_getMinVal(thresh,tt,ff)

   USE fse_mod

   Implicit None

   Real(8) :: thresh
   Real(8) :: tt,ff
   Integer :: loc(1)
   Real(8) :: gg,hh

      loc=MinLoc(fn)
      tt=dt*(loc(1)-1-pp)

      Do while(.true.)
         Call fse_getFunc(tt,ff)
         Call fse_getGrad(tt,gg)

         if(abs(gg)<thresh) then
            exit
         endif
         Call fse_getHess(tt,hh)

         tt=tt-gg/hh

      End do

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
