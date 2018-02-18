!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

Subroutine lag3_Const(n1,n2,n3,xg,yg,zg,vg,vp1)

   Implicit None

   Integer :: n1,n2,n3,i,j,k
   Real(8) :: xg(n1),yg(n2),zg(n3),vg(n1,n2,n3),vp1(n1,n2,n3),py,pz,lagcore_p1

      Do i=1,n3
         pz=lagcore_p1(n3,zg,i,zg(i))
         Do j=1,n2
            py=lagcore_p1(n2,yg,j,yg(j))
            Do k=1,n1
               Vp1(k,j,i)=Vg(k,j,i)/pz/py/lagcore_p1(n1,xg,k,xg(k))
            End do
         End do
      End do

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine lag3_getV(n1,n2,n3,xxg,yyg,zzg,vg,vp1,xx,yy,zz,vv)

   Implicit None

   Integer, parameter :: ndim=3

   Integer :: n1,n2,n3,nmax
   Real(8) :: xxg(n1),yyg(n2),zzg(n3),vg(n1,n2,n3),vp1(n1,n2,n3)
   Real(8) :: xx,yy,zz,vv

   Integer :: ixy,i,j,k,l,nn(ndim)
   Integer :: option
   Real(8), allocatable :: Aa(:,:),xg(:,:)
   Real(8) :: Bb(ndim),C0,C1,qq(ndim)
   Real(8) :: lagcore_p0,lagcore_p1

      nn(1)=n1
      nn(2)=n2
      nn(3)=n3
      qq(1)=xx
      qq(2)=yy
      qq(3)=zz

      nmax=maxval(nn)
      Allocate(Aa(nmax,ndim),xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do
      Do i=1,n3
         xg(i,3)=zzg(i)
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
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*Aa(k,1)
            End do
            C1=C1 + C0*Aa(j,2)
         End do
         vv=vv + C1*Aa(i,3)
      End do
      vv=vv*Bb(1)*Bb(2)*Bb(3)

      Deallocate(Aa,xg)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine lag3_getVG(n1,n2,n3,xxg,yyg,zzg,vg,vp1,xx,yy,zz,vv,gg)

   Implicit None

   Integer, parameter :: ndim=3

   Integer :: n1,n2,n3,nmax
   Real(8) :: xxg(n1),yyg(n2),zzg(n3),vg(n1,n2,n3),vp1(n1,n2,n3)
   Real(8) :: xx,yy,zz,vv,gg(ndim)

   Integer :: ixy,i,j,k,l,nn(ndim)
   Integer :: option
   Real(8), allocatable :: A1(:,:),A2(:,:),xg(:,:)
   Real(8) :: qq(ndim)
   Real(8) :: Bb(ndim),C0,C1,S0
   Real(8) :: lagcore_p0,lagcore_p1,lagcore_p2,lagcore_p3

      nn(1)=n1
      nn(2)=n2
      nn(3)=n3
      qq(1)=xx
      qq(2)=yy
      qq(3)=zz

      nmax=maxval(nn)
      Allocate(A1(nmax,ndim),A2(nmax,ndim),xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do
      Do i=1,n3
         xg(i,3)=zzg(i)
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
                  A2(i,ixy)=A2(i,ixy) + &
                     lagcore_p2(nn(ixy),xg(1:nn(ixy),ixy),i,j,qq(ixy))
               End do
            End do

            Bb(ixy)=1.D+00
  
         endif
      End do

      vv=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         vv=vv + C1*A1(i,3)
      End do
      vv=vv*Bb(1)*Bb(2)*Bb(3)

      gg(1)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A2(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         gg(1)=gg(1) + C1*A1(i,3)
      End do
      gg(1)=gg(1)*Bb(1)*Bb(2)*Bb(3)

      gg(2)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A2(j,2)
         End do
         gg(2)=gg(2) + C1*A1(i,3)
      End do
      gg(2)=gg(2)*Bb(1)*Bb(2)*Bb(3)

      gg(3)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         gg(3)=gg(3) + C1*A2(i,3)
      End do
      gg(3)=gg(3)*Bb(1)*Bb(2)*Bb(3)

      Deallocate(A1,A2,xg)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
Subroutine lag3_getVGH(n1,n2,n3,xxg,yyg,zzg,vg,vp1,xx,yy,zz,vv,gg,hh)

   Implicit None

   Integer, parameter :: ndim=3

   Integer :: n1,n2,n3,nmax
   Real(8) :: xxg(n1),yyg(n2),zzg(n3),vg(n1,n2,n3),vp1(n1,n2,n3)
   Real(8) :: xx,yy,zz,vv,gg(ndim),hh(ndim,ndim)

   Integer :: ixy,i,j,k,l,nn(3)
   Integer :: option
   Real(8), allocatable :: A1(:,:),A2(:,:),A3(:,:),xg(:,:)
   Real(8) :: qq(ndim)
   Real(8) :: Bb(ndim),C0,C1,S0
   Real(8) :: lagcore_p0,lagcore_p1,lagcore_p2,lagcore_p3

      nn(1)=n1
      nn(2)=n2
      nn(3)=n3
      qq(1)=xx
      qq(2)=yy
      qq(3)=zz

      nmax=maxval(nn)
      Allocate(A1(nmax,ndim),A2(nmax,ndim),A3(nmax,ndim),xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do
      Do i=1,n3
         xg(i,3)=zzg(i)
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
                  A2(i,ixy)=A2(i,ixy) +  &
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
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         vv=vv + C1*A1(i,3)
      End do
      vv=vv*Bb(1)*Bb(2)*Bb(3)

      gg(1)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A2(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         gg(1)=gg(1) + C1*A1(i,3)
      End do
      gg(1)=gg(1)*Bb(1)*Bb(2)*Bb(3)

      gg(2)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A2(j,2)
         End do
         gg(2)=gg(2) + C1*A1(i,3)
      End do
      gg(2)=gg(2)*Bb(1)*Bb(2)*Bb(3)

      gg(3)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         gg(3)=gg(3) + C1*A2(i,3)
      End do
      gg(3)=gg(3)*Bb(1)*Bb(2)*Bb(3)

      hh(1,1)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A3(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         hh(1,1)=hh(1,1) + C1*A1(i,3)
      End do
      hh(1,1)=hh(1,1)*Bb(1)*Bb(2)*Bb(3)

      hh(2,2)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A3(j,2)
         End do
         hh(2,2)=hh(2,2) + C1*A1(i,3)
      End do
      hh(2,2)=hh(2,2)*Bb(1)*Bb(2)*Bb(3)

      hh(3,3)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         hh(3,3)=hh(3,3) + C1*A3(i,3)
      End do
      hh(3,3)=hh(3,3)*Bb(1)*Bb(2)*Bb(3)

      hh(1,2)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A2(k,1)
            End do
            C1=C1 + C0*A2(j,2)
         End do
         hh(1,2)=hh(1,2) + C1*A1(i,3)
      End do
      hh(1,2)=hh(1,2)*Bb(1)*Bb(2)*Bb(3)
      hh(2,1)=hh(1,2)

      hh(1,3)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A2(k,1)
            End do
            C1=C1 + C0*A1(j,2)
         End do
         hh(1,3)=hh(1,3) + C1*A2(i,3)
      End do
      hh(1,3)=hh(1,3)*Bb(1)*Bb(2)*Bb(3)
      hh(3,1)=hh(1,3)

      hh(2,3)=0.D+00
      Do i=1,nn(3)
         C1=0.D+00
         Do j=1,nn(2)
            C0=0.D+00
            Do k=1,nn(1)
               C0=C0 + Vp1(k,j,i)*A1(k,1)
            End do
            C1=C1 + C0*A2(j,2)
         End do
         hh(2,3)=hh(2,3) + C1*A2(i,3)
      End do
      hh(2,3)=hh(2,3)*Bb(1)*Bb(2)*Bb(3)
      hh(3,2)=hh(2,3)

      Deallocate(A1,A2,A3,xg)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine lag3_getCoeff(n1,n2,n3,xxg,yyg,zzg,vp1,coeff)

   Implicit None

   Integer, parameter :: ndim=3

   Integer :: n1,n2,n3,nmax
   Real(8) :: xxg(n1),yyg(n2),zzg(n3),vp1(n1,n2,n3)
   Real(8) :: coeff(0:n1-1,0:n2-1,0:n3-1)

   Integer :: i,i1,j1,i2,j2,i3,j3,nn(3)
   Real(8), allocatable :: xg(:,:)
   Real(8) :: Cx(0:n1-1),Cy(0:n2-1),Cz(0:n3-1)

      nn(1)=n1
      nn(2)=n2
      nn(3)=n3

      nmax=maxval(nn)
      Allocate(xg(nmax,ndim))
      Do i=1,n1
         xg(i,1)=xxg(i)
      End do
      Do i=1,n2
         xg(i,2)=yyg(i)
      End do
      Do i=1,n3
         xg(i,3)=zzg(i)
      End do

      coeff=0.D+00
      Do i3=1,nn(3)
         Call getC(3,i3,Cz)
         Do i2=1,nn(2)
            Call getC(2,i2,Cy)
            Do i1=1,nn(1)
               Call getC(1,i1,Cx)
               Do j3=0,nn(3)-1
               Do j2=0,nn(2)-1
               Do j1=0,nn(1)-1
                  coeff(j1,j2,j3)=coeff(j1,j2,j3) & 
                      + VP1(i1,i2,i3) * Cx(j1)*Cy(j2)*Cz(j3)
               End do
               End do
               End do
            End do
         End do
      End do

   Contains

   Subroutine getC(xyz,ii,c1)

   Implicit None

   Integer :: xyz,ii,j,k,n
   Real(8) :: c1(nn(xyz)),c2(nn(xyz))

      n=nn(xyz)

      c1=0.D+00
      c1(n)=1.D+00
      Do j=1,ii-1
         c2=0.D+00
         Do k=1,j
            c2(n+1-k)=c2(n+1-k)+c1(n+1-k)
            c2(n-k)=c2(n-k)-c1(n+1-k)*xg(j,xyz)
         End do
         c1=c2
      End do
      Do j=ii+1,n
         c2=0.D+00
         Do k=1,j-1
            c2(n+1-k)=c2(n+1-k)+c1(n+1-k)
            c2(n-k)=c2(n-k)-c1(n+1-k)*xg(j,xyz)
         End do
         c1=c2
      End do

   End subroutine

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!     Program test
!
!      Implicit None
!
!      Integer,parameter :: n1=7,n2=9,n3=11,mi=31
!      Integer :: ifl,i,j,k,l
!      Real(8) :: q1(n1),q2(n2),q3(n3)
!      Real(8) :: V21(n1,n2),V21p(n1,n2)
!      Real(8) :: V321(n1,n2,n3),V321p(n1,n2,n3)
!      Real(8) :: fac,elmass,b2a,H2wvn
!      Real(8) :: dx,dy,dz,vpx,vmx,vpy,vmy,vpz,vmz,gn(3),hn(3,3)
!      Real(8) :: vxy1,vxy2,vxy3,vxy4
!      Real(8) :: vxz1,vxz2,vxz3,vxz4
!      Real(8) :: vyz1,vyz2,vyz3,vyz4
!      Real(8) :: xx,yy,zz,vv,gg(3),hh(3,3)
!      Real(8) :: coeff(0:n1-1,0:n2-1,0:n3-1),xni(0:n1-1),yni(0:n2-1),zni(0:n3-1)
!
!         ifl=100
!
!         Open(unit=ifl,file='q2q1.pot',status='OLD')
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Do i=1,n2
!         Do j=1,n1
!            Read(ifl,*) q1(j),q2(i),V21(j,i)
!         End do
!         End do
!
!         Open(unit=ifl,file='q3q2q1.pot',status='OLD')
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Read(ifl,*)
!         Do i=1,n3
!         Do j=1,n2
!         Do k=1,n1
!            Read(ifl,*) q1(k),q2(j),q3(i),V321(k,j,i)
!         End do
!         End do
!         End do
!         Close(ifl)
!
!         Call lag3_Const(n1,n2,n3,q1,q2,q3,V321,V321p)

!dbg1         Do i=1,n1
!dbg1            Call lag3_getV(n1,n2,n3,q1,q2,q3,V321,V321p,q1(i),q2(1),q3(1),vv)
!dbg1            write(6,'(f10.4,2f12.6)') q1(i),V321(i,1,1),vv
!dbg1         End do
!dbg1         xx=q1(1)
!dbg1         yy=q2(1)
!dbg1         zz=-0.26D+00
!dbg1         Do i=1,27
!dbg1            Call lag3_getV(n1,n2,n3,q1,q2,q3,V321,V321p,xx,yy,zz,vv)
!dbg1            write(6,'(f10.4,2f12.6)') zz,vv
!dbg1            zz=zz+0.02
!dbg1         End do

!dbg2         xx= 0.10D+00
!dbg2         yy=-0.26D+00
!dbg2         zz= 0.10D+00
!dbg2         dx=0.002D+00
!dbg2         dy=0.002D+00
!dbg2         dz=0.002D+00
!dbg2         Do i=1,27
!dbg2            Call lag3_getVGH(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy,zz,vv,gg,hh)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx+dx,yy,zz,vpx)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx-dx,yy,zz,vmx)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy+dy,zz,vpy)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy-dy,zz,vmy)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy,zz+dz,vpz)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy,zz-dz,vmz)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx+dx,yy+dy,zz,vxy1)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx+dx,yy-dy,zz,vxy2)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx-dx,yy+dy,zz,vxy3)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx-dx,yy-dy,zz,vxy4)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx+dx,yy,zz+dz,vxz1)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx+dx,yy,zz-dz,vxz2)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx-dx,yy,zz+dz,vxz3)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx-dx,yy,zz-dz,vxz4)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy+dy,zz+dz,vyz1)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy+dy,zz-dz,vyz2)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy-dy,zz+dz,vyz3)
!dbg2            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy-dy,zz-dz,vyz4)
!dbg2            gn(1)=(vpx-vmx)/dx*0.5D+00
!dbg2            gn(2)=(vpy-vmy)/dy*0.5D+00
!dbg2            gn(3)=(vpz-vmz)/dz*0.5D+00
!dbg2            hn(1,1)=(vpx+vmx-2.D+00*vv)/dx/dx
!dbg2            hn(2,2)=(vpy+vmy-2.D+00*vv)/dy/dy
!dbg2            hn(3,3)=(vpz+vmz-2.D+00*vv)/dz/dz
!dbg2            hn(1,2)=(vxy1-vxy2-vxy3+vxy4)/dx/dy*0.25D+00
!dbg2            hn(1,3)=(vxz1-vxz2-vxz3+vxz4)/dx/dz*0.25D+00
!dbg2            hn(2,3)=(vyz1-vyz2-vyz3+vyz4)/dy/dz*0.25D+00
!dbg2            hn(2,1)=hn(1,2)
!dbg2            hn(3,1)=hn(1,3)
!dbg2            hn(3,2)=hn(2,3)
!dbg2            write(6,'(f8.2,f12.6)') yy,vv
!dbg2            write(6,'(8x,3f12.6)') gg
!dbg2            write(6,'(8x,3f12.6)') gg-gn
!dbg2            write(6,*)
!dbg2            write(6,'(8x,3f12.6)') hh
!dbg2            write(6,'(8x,3f12.6)') hh-hn
!dbg2            write(6,*)
!dbg2            xx=xx+0.02D+00
!dbg2         End do

!dbg3         Call lag3_getCoeff(ni,ni,ni,q3,q2,q1,V321p,coeff)
!dbg3         dx=(q1(ni)-q1(1))/(mi-1)
!dbg3         dy=(q2(ni)-q2(1))/(mi-1)
!dbg3         dz=(q3(ni)-q3(1))/(mi-1)
!dbg3         Do i=1,mi
!dbg3            xx=q1(1)+(i-1)*dx
!dbg3            yy=q2(1)+(i-1)*dy
!dbg3            zz=q3(1)+(i-1)*dz
!dbg3            Call lag3_getV(ni,ni,ni,q3,q2,q1,V321,V321p,xx,yy,zz,vv)
!dbg3
!dbg3            xni(0)=1.D+00
!dbg3            yni(0)=1.D+00
!dbg3            zni(0)=1.D+00
!dbg3            Do j=1,ni-1
!dbg3               xni(j)=xni(j-1)*xx
!dbg3               yni(j)=yni(j-1)*yy
!dbg3               zni(j)=zni(j-1)*zz
!dbg3            End do
!dbg3            vpx=0.D+00
!dbg3            Do j=0,ni-1
!dbg3            Do k=0,ni-1
!dbg3            Do l=0,ni-1
!dbg3               vpx=vpx + coeff(l,k,j)*xni(l)*yni(k)*zni(j)
!dbg3            End do
!dbg3            End do
!dbg3            End do
!dbg3            write(6,'(3f8.2,2f12.6)') xx,yy,zz,vv,vpx
!dbg3         End do

!      End
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
