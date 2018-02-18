!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!      Program main
!
!      Implicit None
!
!         Integer, parameter :: Nat=9,Nml=3
!         Integer :: Naim(Nml)
!         Real(8) :: x(3,Nat), Ms(Nat), xg(3,Nml), z(4,Nml), In(3,Nml), &
!                    x0(3,Nat), ang(3,Nml),PI
!
!         Integer :: i,j,n,m
!
!         Data Naim / 3,3,3 /
!         Data x /  -.655407,    1.509072,    -.087636, &
!                    .272949,    1.194580,    -.064956, &
!                   -.789753,    1.944149,     .766571, &
!                   1.634783,    -.186891,    -.087571, &
!                   2.079599,    -.289786,     .765984, &
!                    .897961,    -.833339,    -.065347, &
!                   -.979308,   -1.322115,    -.087586, &
!                  -1.290691,   -1.655417,     .766213, &
!                  -1.170616,    -.360707,    -.066121 /
!
!         Data Ms / 15.9949146, 1.00782504, 1.00782504, &
!                   15.9949146, 1.00782504, 1.00782504, &
!                   15.9949146, 1.00782504, 1.00782504 /
!
!         PI=Acos(-1.0D+00)
!         x0=x
!
!         n=1
!         Do i=1,Nml
!            m=n+Naim(i)-1
!            Call x2MF(Naim(i),Ms(n:m),x0(:,n:m),xg(:,i),z(:,i),In(:,i), &
!                      ang(:,i))
!
!            Write(6,*)
!            Write(6,'(''N='',i3)') i
!            Write(6,'(''xg'',/,3f12.6)') xg(:,i)
!            Write(6,'(''Ang'',/,3f12.6)') ang(:,i)/PI*180D+00
!            Write(6,'(''I'',/,3f12.6)') In(:,i)
!            Write(6,'(''x'')')
!            Write(6,'(3f12.6)') x0(:,n:m)
!            n=m+1
!
!         End do
!
!      End
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    > Convert coordinates and velocities in laboratory frame into 
!      molecular frame
!
!    - Input
!     Nat : Number of atoms
!       m : Mass of each atoms
!       x : Cart. coord. in laboratory frame
!       v : Velocity in laboratory frame
!
!    - Output
!      tx : C.O.M. position
!      tv : Velocity with respect to C.O.M. 
!      rI : Principle moment of inertia
!      rZ : Quaternions (Cayley-Klein parameters)
!      rW : Angular velocity
!      x0 : Cart. coord. in molecular frame
!

      Subroutine xv2MF(Nat,m,x,v,tx,tv,rI,rZ,rW,x0)

      Implicit None

         Integer :: i,j,k,Nat
         Real(8) :: m(Nat),x(3,Nat),v(3,Nat),x1(3,Nat),v1(3,Nat), &
                    tx(3),tv(3),rI(3),rZ(4),rW(3),x0(3,Nat), &
                    In(3,3),Im(3,3), &
                    p(3),a(3)

         !----+----2----+----3----+----4----+----5----+----6----+----7----+----80
         !
         ! Translation
         !  >tx :: Position of C.O.M.
         !  >tv :: Velocity of C.O.M.
         x1=x; v1=v
         Call CM(Nat,m,x1,tx)
         Call CM(Nat,m,v1,tv)
         !dbg write(6,'(3f12.6)') tx,tv
         !dbg write(6,*)

         !----+----2----+----3----+----4----+----5----+----6----+----7----+----80
         !
         ! Rotation
         !  >rI :: Principal moment of inertia
         Call inrt_tensor(Nat,m,x1,In)
         Call huckeler(3,3,In,rI,Im)
         Do i=1,3
            Do j=1,3
               In(i,j)=Im(j,i)
            End do
         End do
         !Call diag(3,3,In,Im,rI)    < -- unstable ?
         !dbg write(6,'(3f12.6)') rI
         !dbg write(6,*)
         !dbg write(6,'(3f12.6)') ((In(j,i),i=1,3),j=1,3)
         !dbg write(6,*)

         !----+----2----+----3----+----4----+----5----+----6----+----7----+----80
         !
         !  >rZ :: Quaternions (Cayley-Klein parameters)
         Call Quaternions(In,rZ)
         Call get_RotMat(1,In,rZ)
         !dbg write(6,'(3f12.6)') ((In(j,i),i=1,3),j=1,3)
         !dbg write(6,*)

         !----+----2----+----3----+----4----+----5----+----6----+----7----+----80
         !
         !  >x0 :: Cartesian coordinates in principal axes of inertia 
         Do i=1,Nat
            a=0.D+00
            Do j=1,3
            Do k=1,3
               a(j)=a(j)+In(j,k)*x1(k,i)
            End do
            End do
            x0(:,i)=a
         End do
         !dbg write(6,'(3f12.6)') x0
         !dbg write(6,*)
 
         !----+----2----+----3----+----4----+----5----+----6----+----7----+----80
         !
         !  >rW :: Angular velocity

         !  Total angular mementum in principle axis of inertia
         rW=0.D+00
         Do i=1,Nat
            p=v1(:,i)*m(i)
            Call vec_product(x1(:,i),p,a)
            rW=rW+a
         End do
         p=0.D+00
         Do j=1,3
         Do k=1,3
            p(j)=p(j)+In(j,k)*rW(k)
         End do
         End do

         !  Angular velocity 
         Do j=1,3
            if(abs(rI(j)) > 1.D-08) then
               rW(j)=p(j)/rI(j)
            else
               rW(j)=0.D+00
            endif
         End do
         !dbg write(6,'(3f12.6)') rW
         !dbg write(6,*)

         return

      End subroutine

!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
      Subroutine x2MF(Nat,Ms,x,xg,z,In,ang)

      Implicit None

         Integer :: Nat
         Real(8) :: Ms(Nat),x(3,Nat),xg(3),z(4),In(3),ang(3)

         Integer :: i,j,k
         Real(8) :: Im(3,3),C0(3,3),C(3,3),a(3)

            Call CM(Nat,Ms,x,xg)
            Call inrt_tensor(Nat,Ms,x,Im)
            Call huckeler(3,3,Im,In,C0)
            Do i=1,3
               Do j=1,3
                  C(j,i)=C0(i,j)
               End do
            End do
            !dbg Write(6,'(3f12.6)') ((C(j,i),i=1,3),j=1,3)
            !dbg Write(6,*)
            Call EulrAng(C,ang)
            Call Quaternions(C,z)
            Call get_RotMat(1,C,z)
            !dbg Write(6,'(3f12.6)') ((C(j,i),i=1,3),j=1,3)
            !dbg Write(6,*)

            Do i=1,Nat
               a=0.D+00
               Do j=1,3
                  Do k=1,3
                     a(j)=a(j)+C(j,k)*x(k,i)
                  End do
               End do
               x(:,i)=a
            End do

            return

      End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
      Subroutine MF2x(Nat,x,xg,z)

      Implicit None

         Integer :: Nat
         Real(8) :: x(3,Nat),xg(3),z(4)

         Integer :: i,j,k
         Real(8) :: C(3,3),a(3)

            Call get_RotMat(1,C,z)
            Do i=1,Nat
               a=0.D+00
               Do j=1,3
               Do k=1,3
                  a(j)=a(j)+C(k,j)*x(k,i)
               End do
               End do
               x(:,i)=a+xg
            End do

            return

      End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
      SUBROUTINE CM(Nat,Zmass,x1,xg)
!
      Implicit None
!
!        Integer, intent(in) :: Nat
        Integer :: Nat
        Double Precision, dimension(Nat)::Zmass
        Double Precision, dimension(3,Nat)::x1
        Double Precision, dimension(3)::xg
!
        Integer :: i,j
        Double Precision :: sumw, sumx
!
!--------------------------------------------------------------------------------
!
!   >>  initialize  <<
!
      sumw=0.D+00
!
!   >>  Total Mass <<
      Do i=1,Nat
         sumw = sumw + Zmass(i)
      End do
!
!   >>  Center of mass <<
      Do j=1,3
         xg(j)=0.D+00
         Do i=1,Nat
            xg(j) = xg(j) + x1(j,i)*Zmass(i)
         End do
         xg(j)=xg(j)/sumw
      End do
!
!   >> Center of mass coordinate <<
      Do i=1,Nat
         x1(:,i) = x1(:,i) - xg
      End do
!
      return
!
      End subroutine CM
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

      Subroutine inrt_tensor(Nat,Mass,xeq,In)

         Implicit None

         Integer :: Nat
         Real(8) :: Mass(Nat),xeq(3,Nat)

         Integer :: i,j,k,l
         Real(8), dimension(3) :: Id,x1,x2
         Real(8), dimension(3,3) :: In,Im,tmp

            !dbg write(6,'(3f12.6)') xeq
            !dbg write(6,'(4f12.4)') Mass
            !dbg write(6,*)

            In=0.D+00
            Do i=1,Nat
               In(1,1)=In(1,1)+Mass(i)*(xeq(2,i)*xeq(2,i)+xeq(3,i)*xeq(3,i))
               In(2,2)=In(2,2)+Mass(i)*(xeq(3,i)*xeq(3,i)+xeq(1,i)*xeq(1,i))
               In(3,3)=In(3,3)+Mass(i)*(xeq(1,i)*xeq(1,i)+xeq(2,i)*xeq(2,i))

               In(2,1)=In(2,1)-Mass(i)*xeq(2,i)*xeq(1,i)
               In(3,1)=In(3,1)-Mass(i)*xeq(3,i)*xeq(1,i)
               In(3,2)=In(3,2)-Mass(i)*xeq(3,i)*xeq(2,i)
            End do
            In(1,2)=In(2,1)
            In(1,3)=In(3,1)
            In(2,3)=In(3,2)
            !dbg write(6,'(3f12.6)') In
            !dbg write(6,*)

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
      Subroutine Quaternions(x,Z)

      Implicit None

         Integer :: i,j
         Real(8) :: x(3,3),Z(4),a(3),p,q,r

            Call EulrAng(x,a)
            ! Call get_RotMat(0,x,a)
            ! write(6,'(3f12.6)') ((x(j,i),i=1,3),j=1,3)
            ! write(6,*)

            p=a(2)*0.5D+00
            q=(a(3)+a(1))*0.5D+00
            r=(a(3)-a(1))*0.5D+00

            Z(1)=sin(p)*sin(r)
            Z(2)=sin(p)*cos(r)
            Z(3)=cos(p)*sin(q)
            Z(4)=cos(p)*cos(q)

            return

      End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!     a(1) :: 0 =< phi < 2PI
!     a(2) :: 0 =< theta =< PI
!     a(3) :: 0 =< psi < 2PI
!
      Subroutine EulrAng(X,a)

      Implicit None

         Real(8) :: X(3,3),a(3),PI,tht,phi,psi,s,c

            PI=Acos(-1.0D+00)

            tht=Acos(x(3,3))
            if(abs(sin(tht)) > 1.0D-06) then 
               s= x(3,1)/sin(tht)
               c=-x(3,2)/sin(tht)
               Call sub(s,c,phi)

               s= x(1,3)/sin(tht)
               c= x(2,3)/sin(tht)
               Call sub(s,c,psi)
            endif
            !dbg write(6,'(3f12.6)') phi/PI,tht/PI,psi/PI
            !dbg write(6,*)

            a(1)=phi
            a(2)=tht
            a(3)=psi
            return

      Contains 

        Subroutine sub(sn,cs,a)

        Implicit None

           Real(8) :: sn,cs,a,PI

              PI=Acos(-1.0D+00)

              a=Acos(cs)
              if(sn<0.0D+00) then 
                 a=2.D+00*PI-a
              endif

        End subroutine 

      End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!   Euler angles (i=0)
!     a(1) :: 0 =< phi < 2PI)
!     a(2) :: 0 =< theta =< PI
!     a(3) :: 0 =< psi < 2PI)
!
!   Quaternions (i/=0) 
!
      Subroutine get_RotMat(i,x,a)

      Implicit None

         Integer :: i,j,k,l
         Real(8) :: x(3,3),a(4),PI,tht,phi,psi

            if(i==0) then
               phi=a(1);tht=a(2);psi=a(3)

               x(1,1)=cos(psi)*cos(phi)-cos(tht)*sin(phi)*sin(psi)
               x(2,1)=-sin(psi)*cos(phi)-cos(tht)*sin(phi)*cos(psi)
               x(3,1)=sin(tht)*sin(phi)

               x(1,2)=cos(psi)*sin(phi)+cos(tht)*cos(phi)*sin(psi)
               x(2,2)=-sin(psi)*sin(phi)+cos(tht)*cos(phi)*cos(psi)
               x(3,2)=-sin(tht)*cos(phi)
 
               x(1,3)=sin(psi)*sin(tht)
               x(2,3)=cos(psi)*sin(tht)
               x(3,3)=cos(tht)

            else
               x(1,1)= 2.D+00*(a(2)*a(2)+a(4)*a(4))-1.D+00
               x(2,1)=-2.D+00*(a(1)*a(2)+a(3)*a(4))
               x(3,1)= 2.D+00*(a(2)*a(3)-a(1)*a(4))
 
               x(1,2)= 2.D+00*(a(3)*a(4)-a(1)*a(2))
               x(2,2)= 2.D+00*(a(1)*a(1)+a(4)*a(4))-1.D+00
               x(3,2)=-2.D+00*(a(1)*a(3)+a(2)*a(4))

               x(1,3)= 2.D+00*(a(2)*a(3)+a(1)*a(4))
               x(2,3)= 2.D+00*(a(2)*a(4)-a(1)*a(3))
               x(3,3)= 2.D+00*(a(3)*a(3)+a(4)*a(4))-1.D+00

            endif

            return

      End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!     - Input
!     Nat : Number of atoms
!      Ms : Mass of the molecule
!       x : Cart. coord. in molecular frame
!       f : Force
!       Z : Quaternion
!
!     - Output
!      rN : Torque
!

      Subroutine Torque(Nat,x,f,Z,rN)

      Implicit None

         Integer :: i,j,k,Nat
         Real(8) :: x(3,Nat),f(3,Nat),Z(4),rN(3), & 
                    a(3),c(3,3)

            Call get_RotMat(1,c,Z)
            Do i=1,Nat
               a=0.D+00
               Do j=1,3
               Do k=1,3
                  a(j)=a(j)+c(j,k)*f(k,i)
               End do
               End do
               f(:,i)=a
            End do
            !dbg write(6,'(3d15.6)') f
            !dbg write(6,*)

            ! Torque
            rN=0.D+00
            Do i=1,Nat
               Call vec_product(x(:,i),f(:,i),a)
               rN=rN+a
            End do
            !dbg write(6,'(3d15.6)') rN
            !dbg write(6,*)

            return
 
      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
