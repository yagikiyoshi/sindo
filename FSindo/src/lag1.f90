!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine lag1_Const(n,xg,vg,vp1)

      Implicit None

      Integer :: n,i
      Real(8) :: xg(n),vg(n),vp1(n),lagcore_p1

         Do i=1,n
            Vp1(i)=Vg(i)/lagcore_p1(n,xg,i,xg(i))
         End do

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine lag1_getV(nn,xg,vg,vp1,xx,vv)

      Implicit None

      Integer :: nn
      Real(8) :: xg(nn),vg(nn),vp1(nn)
      Real(8) :: xx,vv

      Integer :: i,j,k,l
      Integer :: option
      Real(8) :: P0,S0
      Real(8) :: lagcore_p0,lagcore_p1

         option=1
         Do i=1,nn
            if(abs(xx-xg(i))<1.D-08) then
               option=-1
               exit
            endif
         End do

         if(option>0) then
            P0=lagcore_p0(nn,xg,xx)
            vv=0.D+00
            Do i=1,nn
               vv=vv + Vp1(i)/(xx-xg(i))
            End do
            vv=vv*P0

         else
            vv=0.D+00
            Do i=1,nn
               vv=vv + Vp1(i)*lagcore_p1(nn,xg,i,xx)
            End do

         endif

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine lag1_getVG(nn,xg,vg,vp1,xx,vv,vd1)

      Implicit None

      Integer :: nn
      Real(8) :: xg(nn),vg(nn),vp1(nn)

      Real(8) :: xx,vv,vd1

      Integer :: i,j,k,l
      Integer :: option
      Real(8) :: P0,S0,dx(nn),dd
      Real(8) :: lagcore_p0,lagcore_p1,lagcore_p2

         option=1
         Do i=1,nn
            if(abs(xx-xg(i))<1.D-08) then
               option=-1
               exit
            endif
         End do

         if(option>0) then
            Do i=1,nn
               dx(i)=xx-xg(i)
            End do

            P0=lagcore_p0(nn,xg,xx)
            vv=0.D+00
            Do i=1,nn
               vv=vv + Vp1(i)/dx(i)
            End do
            vv=vv*P0

            S0=0.D+00
            Do i=1,nn
               S0=S0 + 1.D+00/dx(i)
            End do
            vd1=0.D+00
            Do i=1,nn
               vd1=vd1 + Vp1(i)/dx(i)*(S0-1.D+00/dx(i))
            End do
            vd1=vd1*P0

         else
            vv=0.D+00
            Do i=1,nn
               vv=vv + Vp1(i)*lagcore_p1(nn,xg,i,xx)
            End do

            vd1=0.D+00
            Do i=1,nn
               dd=0.D+00
               Do j=1,nn
                  if(j/=i) then
                    dd=dd + lagcore_p2(nn,xg,i,j,xx)
                  endif
               End do
               vd1=vd1 + Vp1(i)*dd
            End do

         endif

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine lag1_getVGH(nn,xg,vg,vp1,xx,vv,vd1,vd2)

      Implicit None

      Integer :: nn
      Real(8) :: xg(nn),vg(nn),vp1(nn)

      Real(8) :: xx,vv,vd1,vd2

      Integer :: i,j,k,l
      Integer :: option
      Real(8) :: P0,S0,dx(nn),dd,Si
      Real(8) :: lagcore_p0,lagcore_p1,lagcore_p2,lagcore_p3

         option=1
         Do i=1,nn
            if(abs(xx-xg(i))<1.D-08) then
               option=-1
               exit
            endif
         End do

         if(option>0) then
            Do i=1,nn
               dx(i)=xx-xg(i)
            End do

            P0=lagcore_p0(nn,xg,xx)
            vv=0.D+00
            Do i=1,nn
               vv=vv + Vp1(i)/dx(i)
            End do
            vv=vv*P0

            S0=0.D+00
            Do i=1,nn
               S0=S0 + 1.D+00/dx(i)
            End do
            vd1=0.D+00
            Do i=1,nn
               vd1=vd1 + Vp1(i)/dx(i)*(S0-1.D+00/dx(i))
            End do
            vd1=vd1*P0

            vd2=0.D+00
            Do i=1,nn
               Si=S0-1.D+00/dx(i)
               dd=0.D+00
               Do j=1,nn
                  if(j/=i) then
                     dd=dd + (Si-1.D+00/dx(j))/dx(j)
                  endif
               End do

               vd2=vd2 + Vp1(i)/dx(i)*dd
            End do
            vd2=vd2*P0

         else
            vv=0.D+00
            Do i=1,nn
               vv=vv + Vp1(i)*lagcore_p1(nn,xg,i,xx)
            End do

            vd1=0.D+00
            Do i=1,nn
               dd=0.D+00
               Do j=1,nn
                  if(j/=i) then
                    dd=dd + lagcore_p2(nn,xg,i,j,xx)
                  endif
               End do
               vd1=vd1 + Vp1(i)*dd
            End do

            vd2=0.D+00
            Do i=1,nn
               dd=0.D+00
               Do j=1,nn
                  if(j==i) cycle
                  Do k=1,nn
                     if(k==i .or. k==j) cycle
                     dd=dd + lagcore_p3(nn,xg,i,j,k,xx)
                  End do
               End do
               vd2=vd2 + Vp1(i)*dd
            End do

         endif

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine lag1_getCoeff(nn,xg,vp1,coeff)

      Implicit None

         Integer :: nn
         Real(8) :: xg(nn),vp1(nn)

         Integer :: i,j,k,n1
         Real(8) :: coeff(nn),c1(nn),c2(nn)

            coeff=0.D+00 
            Do i=1,nn
               c1=0.D+00
               c1(nn)=1.D+00
               Do j=1,i-1
                  c2=0.D+00
                  Do k=1,j   
                     c2(nn+1-k)=c2(nn+1-k)+c1(nn+1-k)
                     c2(nn-k)=c2(nn-k)-c1(nn+1-k)*xg(j)
                  End do
                  !write(6,'(11f10.4)') c2
                  c1=c2
               End do
               Do j=i+1,nn
                  c2=0.D+00
                  Do k=1,j-1   
                     c2(nn+1-k)=c2(nn+1-k)+c1(nn+1-k)
                     c2(nn-k)=c2(nn-k)-c1(nn+1-k)*xg(j)
                  End do
                  !write(6,'(11f10.4)') c2
                  c1=c2
               End do
               coeff=coeff + c1*Vp1(i)
            End do

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!      Program test
!
!      Implicit None
!
!      Integer,parameter :: ni=5,mi=31
!      Integer :: ifl,i,j,k
!      Real(8), dimension(ni) :: q1,q2,q3,V1,V2,V3,Vp1,Vp2,Vp3
!      Real(8) :: fac,elmass,b2a,H2wvn,dx
!      Real(8) :: xx,xn,vv,gg,hh,vp,vm,gn,hn,omg(3)
!      Real(8) :: coeff(0:ni-1)
!
!         ifl=100
!
!         Open(unit=ifl,file='q1.pot',status='OLD')
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Do i=1,ni
!            Read(ifl,*) q1(i),V1(i)
!         End do
!         Close(ifl)
!
!         Open(unit=ifl,file='q2.pot',status='OLD')
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Do i=1,ni
!            Read(ifl,*) q2(i),V2(i)
!         End do
!         Close(ifl)
!
!         Open(unit=ifl,file='q3.pot',status='OLD')
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Do i=1,ni
!            Read(ifl,*) q3(i),V3(i)
!         End do
!         Close(ifl)

!dbg1         Call lag1_Const(ni,q2,V2,Vp2)
!dbg1         xx=-0.26D+00
!dbg1         dx=0.005D+00
!dbg1         Do i=1,27
!dbg1            Call lag1_getVGH(ni,q2,V2,Vp2,xx,vv,gg,hh)
!dbg1            Call lag1_getV(ni,q2,V2,Vp2,xx+dx,vp)
!dbg1            Call lag1_getV(ni,q2,V2,Vp2,xx-dx,vm)
!dbg1            gn=(vp-vm)/dx*0.5D+00
!dbg1            hn=(vp+vm-2.D+00*vv)/dx/dx
!dbg1            write(6,'(f8.2,f12.6,2(2x,2f12.6))') xx,vv,gg,gn,hh,hn
!dbg1            xx=xx+0.02D+00
!dbg1         End do

!dbg2         fac=SQRT(elmass())/b2a()
!dbg2         Call lag1_Const(ni,q1,V1,Vp1)
!dbg2         Call lag1_getVGH(ni,q1,V1,Vp1,0.D+00,vv,gg,hh)
!dbg2         omg(1)=SQRT(hh)/fac*H2wvn()
!dbg2         Call lag1_Const(ni,q2,V2,Vp2)
!dbg2         Call lag1_getVGH(ni,q2,V2,Vp2,0.D+00,vv,gg,hh)
!dbg2         omg(2)=SQRT(hh)/fac*H2wvn()
!dbg2         Call lag1_Const(ni,q3,V3,Vp3)
!dbg2         Call lag1_getVGH(ni,q3,V3,Vp3,0.D+00,vv,gg,hh)
!dbg2         omg(3)=SQRT(hh)/fac*H2wvn()
!dbg2         write(6,'(3f12.4)') omg

!dbg3         Call lag1_Const(ni,q1,v1,vp1)
!dbg3         Call lag1_getCoeff(ni,q1,vp1,coeff)
!dbg3         dx=(q1(ni)-q1(1))/(mi-1)
!dbg3         Do i=1,mi
!dbg3            xx=q1(1)+(i-1)*dx
!dbg3            Call lag1_getV(ni,q1,v1,vp1,xx,vv)
!dbg3            vp=coeff(0)
!dbg3            xn=xx
!dbg3            Do j=1,ni-1
!dbg3               vp=vp + coeff(j)*xn
!dbg3               xn=xn*xx
!dbg3            End do
!dbg3            write(6,'(f8.2,2f12.6)') xx,vv,vp
!dbg3         End do
!
!      End
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
