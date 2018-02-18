!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine lag2_Const(n1,n2,xg,yg,vg,vp1)

   Implicit None

   Integer :: n1,n2,i,j
   Real(8) :: xg(n1),yg(n2),vg(n1,n2),vp1(n1,n2),py,lagcore_p1

      Do i=1,n2
         py=lagcore_p1(n2,yg,i,yg(i))
         Do j=1,n1
            Vp1(j,i)=vg(j,i)/py/lagcore_p1(n1,xg,j,xg(j))
         End do
      End do

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine lag2_getV(n1,n2,xxg,yyg,vg,vp1,xx,yy,vv)

   Implicit None

   Integer, parameter :: ndim=2

   Integer :: n1,n2,nmax
   Real(8) :: xxg(n1),yyg(n2),vg(n1,n2),vp1(n1,n2)
   Real(8) :: xx,yy,vv

   Integer :: nn(ndim),ixy,i,j,k,l
   Integer :: option
   Real(8), allocatable :: Aa(:,:),xg(:,:)
   Real(8) :: Bb(ndim),C0,qq(ndim)
   Real(8) :: lagcore_p0,lagcore_p1

      nn(1)=n1
      nn(2)=n2
      qq(1)=xx
      qq(2)=yy

      nmax=maxval(nn)
      Allocate(Aa(nmax,ndim),xg(nmax,2))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do

      Do ixy=1,ndim
         option=1
         Do i=1,nn(ixy)
            if(abs(qq(ixy)-xg(i,ixy))<1.D-08) then
               option=-1
               exit
            endif
         End do
         if(option>0) then
            Do i=1,nn(ixy)
               Aa(i,ixy)=1.D+00/(qq(ixy)-xg(i,ixy))
            End do
            Bb(ixy)=lagcore_p0(nn(ixy),xg(1:nn(ixy),ixy),qq(ixy))
  
         else
            Do i=1,nn(ixy)
               Aa(i,ixy)=lagcore_p1(nn(ixy),xg(1:nn(ixy),ixy),i,qq(ixy))
            End do
            Bb(ixy)=1.D+00
  
         endif
      End do

      vv=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*Aa(j,1)
         End do
         vv=vv + C0*Aa(i,2)
      End do
      vv=vv*Bb(1)*Bb(2)

      Deallocate(Aa,xg)

End subroutine

!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine lag2_getVG(n1,n2,xxg,yyg,vg,vp1,xx,yy,vv,gg)

   Implicit None

   Integer, parameter :: ndim=2

   Integer :: n1,n2,nmax
   Real(8) :: xxg(n1),yyg(n2),vg(n1,n2),vp1(n1,n2)
   Real(8) :: xx,yy,vv,gg(ndim)

   Integer :: nn(ndim),ixy,i,j,k,l
   Integer :: option
   Real(8) :: qq(ndim)
   Real(8), allocatable :: A1(:,:),A2(:,:),xg(:,:)
   Real(8) :: Bb(ndim),C0,S0
   Real(8) :: lagcore_p0,lagcore_p1,lagcore_p2

      nn(1)=n1
      nn(2)=n2
      qq(1)=xx
      qq(2)=yy

      nmax=maxval(nn)
      Allocate(A1(nmax,ndim),A2(nmax,ndim),xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do

      Do ixy=1,ndim
         option=1
         Do i=1,nn(ixy)
            if(abs(qq(ixy)-xg(i,ixy))<1.D-08) then
               option=-1
               exit
            endif
         End do
         if(option>0) then
            Do i=1,nn(ixy)
               A1(i,ixy)=1.D+00/(qq(ixy)-xg(i,ixy))
            End do

            S0=0.D+00
            Do i=1,nn(ixy)
               S0=S0 + A1(i,ixy)
            End do

            Do i=1,nn(ixy)
               A2(i,ixy)=A1(i,ixy)*(S0-A1(i,ixy))
            End do

            Bb(ixy)=lagcore_p0(nn(ixy),xg(1:nn(ixy),ixy),qq(ixy))
  
         else
            Do i=1,nn(ixy)
               A1(i,ixy)=lagcore_p1(nn(ixy),xg(1:nn(ixy),ixy),i,qq(ixy))
            End do

            Do i=1,nn(ixy)
               A2(i,ixy)=0.D+00
               Do j=1,nn(ixy)
                  if(j==i) cycle
                  A2(i,ixy)=A2(i,ixy) +  &
                     lagcore_p2(nn(ixy),xg(1:nn(ixy),ixy),i,j,qq(ixy))
               End do
            End do

            Bb(ixy)=1.D+00
  
         endif
      End do

      vv=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A1(j,1)
         End do
         vv=vv + C0*A1(i,2)
      End do
      vv=vv*Bb(1)*Bb(2)

      gg(1)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A2(j,1)
         End do
         gg(1)=gg(1) + C0*A1(i,2)
      End do
      gg(1)=gg(1)*Bb(1)*Bb(2)

      gg(2)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A1(j,1)
         End do
         gg(2)=gg(2) + C0*A2(i,2)
      End do
      gg(2)=gg(2)*Bb(1)*Bb(2)

      Deallocate(A1,A2,xg)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
Subroutine lag2_getVGH(n1,n2,xxg,yyg,vg,vp1,xx,yy,vv,gg,hh)

   Implicit None

   Integer, parameter :: ndim=2

   Integer :: n1,n2,nmax
   Real(8) :: xxg(n1),yyg(n2),vg(n1,n2),vp1(n1,n2)
   Real(8) :: xx,yy,vv,gg(ndim),hh(ndim,ndim)

   Integer :: ixy,i,j,k,l,nn(ndim)
   Integer :: option
   Real(8) :: qq(ndim)
   Real(8), allocatable :: A1(:,:),A2(:,:),A3(:,:),xg(:,:)
   Real(8) :: Bb(ndim),C0,S0
   Real(8) :: lagcore_p0,lagcore_p1,lagcore_p2,lagcore_p3

      nn(1)=n1
      nn(2)=n2
      qq(1)=xx
      qq(2)=yy

      nmax=maxval(nn)
      Allocate(A1(nmax,ndim),A2(nmax,ndim),A3(nmax,ndim),xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do

      Do ixy=1,ndim
         option=1
         Do i=1,nn(ixy)
            if(abs(qq(ixy)-xg(i,ixy))<1.D-08) then
               option=-1
               exit
            endif
         End do
         if(option>0) then
            Do i=1,nn(ixy)
               A1(i,ixy)=1.D+00/(qq(ixy)-xg(i,ixy))
            End do

            S0=0.D+00
            Do i=1,nn(ixy)
               S0=S0 + A1(i,ixy)
            End do

            Do i=1,nn(ixy)
               A2(i,ixy)=A1(i,ixy)*(S0-A1(i,ixy))
            End do

            Do i=1,nn(ixy)
               C0=0.D+00
               Do j=1,nn(ixy)
                  if(i==j) cycle
                  C0=C0 + A1(j,ixy) * (S0 - A1(j,ixy) - A1(i,ixy)) 
               End do
               A3(i,ixy)=C0*A1(i,ixy)
            End do

            Bb(ixy)=lagcore_p0(nn(ixy),xg(1:nn(ixy),ixy),qq(ixy))
  
         else
            Do i=1,nn(ixy)
               A1(i,ixy)=lagcore_p1(nn(ixy),xg(1:nn(ixy),ixy),i,qq(ixy))
            End do

            Do i=1,nn(ixy)
               A2(i,ixy)=0.D+00
               Do j=1,nn(ixy)
                  if(j==i) cycle
                  A2(i,ixy)=A2(i,ixy) + &
                       lagcore_p2(nn(ixy),xg(1:nn(ixy),ixy),i,j,qq(ixy))
               End do
            End do

            Do i=1,nn(ixy)
               A3(i,ixy)=0.D+00
               Do j=1,nn(ixy)
                  if(j==i) cycle
                  Do k=1,nn(ixy)
                     if(k==i .or. k==j) cycle
                     A3(i,ixy)=A3(i,ixy) +  &
                     lagcore_p3(nn(ixy),xg(1:nn(ixy),ixy),i,j,k,qq(ixy))
                  End do
               End do
            End do

            Bb(ixy)=1.D+00
  
         endif
      End do

      vv=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A1(j,1)
         End do
         vv=vv + C0*A1(i,2)
      End do
      vv=vv*Bb(1)*Bb(2)

      gg(1)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A2(j,1)
         End do
         gg(1)=gg(1) + C0*A1(i,2)
      End do
      gg(1)=gg(1)*Bb(1)*Bb(2)

      gg(2)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A1(j,1)
         End do
         gg(2)=gg(2) + C0*A2(i,2)
      End do
      gg(2)=gg(2)*Bb(1)*Bb(2)

      hh(1,1)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A3(j,1)
         End do
         hh(1,1)=hh(1,1) + C0*A1(i,2)
      End do
      hh(1,1)=hh(1,1)*Bb(1)*Bb(2)

      hh(2,2)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A1(j,1)
         End do
         hh(2,2)=hh(2,2) + C0*A3(i,2)
      End do
      hh(2,2)=hh(2,2)*Bb(1)*Bb(2)

      hh(2,1)=0.D+00
      Do i=1,nn(2)
         C0=0.D+00
         Do j=1,nn(1)
            C0=C0 + Vp1(j,i)*A2(j,1)
         End do
         hh(2,1)=hh(2,1) + C0*A2(i,2)
      End do
      hh(2,1)=hh(2,1)*Bb(1)*Bb(2)
      hh(1,2)=hh(2,1)

      Deallocate(A1,A2,A3,xg)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine lag2_getCoeff(n1,n2,xxg,yyg,Vp1,coeff)

   Implicit None

   Integer, parameter :: ndim=2

   Integer :: n1,n2,nmax
   Real(8) :: xxg(n1),yyg(n2),vp1(n1,n2)
   Real(8) :: coeff(0:n1-1,0:n2-1)

   Integer :: i,i1,j1,i2,j2,nn(ndim)
   Real(8), allocatable :: xg(:,:)
   Real(8) :: Cx(0:n1-1),Cy(0:n2-1)

      nn(1)=n1
      nn(2)=n2

      nmax=maxval(nn)
      Allocate(xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do

      coeff=0.D+00
      Do i2=1,nn(2)
         Call getC(2,i2,Cy)
         Do i1=1,nn(1)
            Call getC(1,i1,Cx)
            Do j2=0,nn(2)-1
            Do j1=0,nn(1)-1
               coeff(j1,j2)=coeff(j1,j2) + Vp1(i1,i2)*Cx(j1)*Cy(j2)
            End do
            End do
         End do
      End do

      Deallocate(xg)

   Contains

   Subroutine getC(xy,ii,c1)

   Implicit None

   Integer :: xy,ii,j,k,n
   Real(8) :: c1(nn(xy)),c2(nn(xy))

      n=nn(xy)

      c1=0.D+00
      c1(n)=1.D+00
      Do j=1,ii-1
         c2=0.D+00
         Do k=1,j
            c2(n+1-k)=c2(n+1-k)+c1(n+1-k)
            c2(n-k)=c2(n-k)-c1(n+1-k)*xg(j,xy)
         End do
         c1=c2
      End do
      Do j=ii+1,n
         c2=0.D+00
         Do k=1,j-1
            c2(n+1-k)=c2(n+1-k)+c1(n+1-k)
            c2(n-k)=c2(n-k)-c1(n+1-k)*xg(j,xy)
         End do
         c1=c2
      End do

   End subroutine

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!      Program test
!
!      Implicit None
!
!      Integer,parameter :: ni=11,mi=31
!      Integer :: ifl,i,j,k
!      Real(8), dimension(ni) :: q2,q3,V2,V3
!      Real(8), dimension(ni,2) :: qq
!      Real(8), dimension(ni,ni) :: V32,V32p
!      Real(8) :: fac,elmass,b2a,H2wvn
!      Real(8) :: dx,dy,vpx,vmx,vpy,vmy,vd1,vd2,vd3,vd4,gn(2),hn(2,2)
!      Real(8) :: xx,yy,vv,gg(2),hh(2,2)
!      Real(8) :: coeff(0:ni-1,0:ni-1),xni(0:ni-1),yni(0:ni-1)
!
!         ifl=100
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
!
!         Open(unit=ifl,file='q3q2.pot',status='OLD')
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Do i=1,ni
!         Do j=1,ni
!            Read(ifl,*) q2(j),q3(i),V32(j,i)
!         End do
!         End do
!         Close(ifl)
!
!         Do i=1,ni
!         Do j=1,ni
!            V32(j,i)=V32(j,i) + V2(j)+V3(i)
!         End do
!         End do
!
!         !Do i=1,ni
!         !   write(6,'(f10.4,f12.6)') q2(i),V32(i,3)
!         !End do
!
!         Call lag2_Const(ni,ni,q2,q3,V32,V32p)

!dbg1         xx=-0.15D+00
!dbg1         yy=-0.26D+00
!dbg1         dx=0.001D+00
!dbg1         dy=0.001D+00
!dbg1         Do i=1,16
!dbg1            Call lag2_getVGH(ni,ni,q2,q3,V32,V32p,xx,yy,vv,gg,hh)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx+dx,yy,vpx)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx-dx,yy,vmx)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx,yy+dy,vpy)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx,yy-dy,vmy)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx+dx,yy+dy,vd1)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx+dx,yy-dy,vd2)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx-dx,yy+dy,vd3)
!dbg1            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx-dx,yy-dy,vd4)
!dbg1            gn(1)=(vpx-vmx)/dx*0.5D+00
!dbg1            gn(2)=(vpy-vmy)/dy*0.5D+00
!dbg1            hn(1,1)=(vpx+vmx-2.D+00*vv)/dx/dx
!dbg1            hn(2,2)=(vpy+vmy-2.D+00*vv)/dy/dy
!dbg1            hn(1,2)=(vd1-vd2-vd3+vd4)/dx/dy*0.25D+00
!dbg1            hn(2,1)=hn(1,2)
!dbg1            write(6,'(f8.2,f12.6)') xx,vv
!dbg1            write(6,'(8x,2f12.6)') gg
!dbg1            write(6,'(8x,2f12.6)') gg-gn
!dbg1            write(6,*)
!dbg1            write(6,'(8x,2f12.6)') hh
!dbg1            write(6,'(8x,2f12.6)') hh-hn
!dbg1            write(6,*)
!dbg1            xx=xx+0.02D+00
!dbg1         End do

!dbg2         Call lag2_getCoeff(ni,ni,q2,q3,V32p,coeff)
!dbg2         dx=(q2(ni)-q2(1))/(mi-1)
!dbg2         dy=(q3(ni)-q3(1))/(mi-1)
!dbg2         Do i=1,mi
!dbg2            xx=q2(1)+(i-1)*dx
!dbg2            yy=q3(1)+(i-1)*dy
!dbg2            Call lag2_getV(ni,ni,q2,q3,V32,V32p,xx,yy,vv)
!dbg2            xni(0)=1.D+00
!dbg2            yni(0)=1.D+00
!dbg2            Do j=1,ni-1
!dbg2               xni(j)=xni(j-1)*xx
!dbg2               yni(j)=yni(j-1)*yy
!dbg2            End do
!dbg2            vpx=0.D+00
!dbg2            Do j=0,ni-1
!dbg2            Do k=0,ni-1
!dbg2               vpx=vpx + coeff(k,j)*xni(k)*yni(j)
!dbg2            End do
!dbg2            End do
!dbg2            write(6,'(2f8.2,2f12.6)') xx,yy,vv,vpx
!dbg2         End do

!      End
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
