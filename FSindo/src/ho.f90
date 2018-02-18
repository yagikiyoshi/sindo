!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/07
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine HO_xmat(n,omega,x)

      USE Constants_mod

      Implicit None

         Integer :: n,j,k
         Real(8) :: omega,x(0:n-1,0:n-1)
         Real(8) :: const

        !-------------------------------------
        ! >>  Qmat in Bohr(emu)1/2
        !-------------------------------------

         const=SQRT(1.0D+00/omega*H2wvn)

         x=0.D+00
         Do j=1,n-1
            k=j-1
            x(k,j)=Sqrt(dble(j)*0.5D+00)*const
            x(j,k)=x(k,j)
         End do

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine HO_kinmat(nx,omega,T)

      USE Constants_mod

      Implicit None

         Integer :: i,j,k,l
         Real(8) :: omgh
         Real(8), dimension(0:nx,0:nx) :: T

         Integer:: nx
         Real(8) :: omega

         omgh=omega/H2wvn*0.5D+00

         T=0.D+00
         Do i=0,nx

            T(i,i)=(dble(i)+0.5D+00)*omgh
            if(i<nx-1) then
               T(i+2,i)=-0.5D+00*SQRT(dble((i+1)*(i+2)))*omgh
               T(i,i+2)=T(i+2,i)
            endif

         End do

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
