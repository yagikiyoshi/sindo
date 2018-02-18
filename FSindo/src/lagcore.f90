!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
      Function lagcore_p0(nn,xg,x)

      Implicit None

         Integer :: i,nn
         Real(8) :: lagcore_p0,x,xg(nn)

         lagcore_p0=1.D+00
         Do i=1,nn
            lagcore_p0=lagcore_p0*(x-xg(i))
         End do

      End Function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
      Function lagcore_p1(nn,xg,i,x)

      Implicit None

         Integer :: i,j,nn
         Real(8) :: lagcore_p1,x,xg(nn)

         lagcore_p1=1.D+00
         Do j=1,nn
            if(j/=i) lagcore_p1=lagcore_p1*(x-xg(j))
         End do

      End Function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
      Function lagcore_p2(nn,xg,i,j,x)

      Implicit None

         Integer :: i,j,k,nn
         Real(8) :: lagcore_p2,x,xg(nn)

         lagcore_p2=1.D+00
         Do k=1,nn
            if(k/=i .and. k/=j) lagcore_p2=lagcore_p2*(x-xg(k))
         End do

      End Function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
      Function lagcore_p3(nn,xg,i,j,k,x)

      Implicit None

         Integer :: i,j,k,l,nn
         Real(8) :: lagcore_p3,x,xg(nn)

         lagcore_p3=1.D+00
         Do l=1,nn
            if(l/=i .and. l/=j .and. l/=k) lagcore_p3=lagcore_p3*(x-xg(l))
         End do

      End Function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
