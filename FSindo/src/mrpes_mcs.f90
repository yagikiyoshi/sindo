!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

 Module mc_mod

   Real(8), parameter :: dd=1.D-08
   Real(8), allocatable :: omg(:),omginv(:),omginv2(:)

 End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine mc_construct(Nf)

      USE mc_mod

      Implicit None

      Integer :: Nf
      Integer :: i,j,k
      Real(8) :: qff_GetHii

       Allocate(omg(Nf),omginv(Nf),omginv2(Nf))

       Do i=1,Nf
          omg(i)=sqrt(2.D+00*qff_GetHii(i))
       End do
       !write(6,'(f18.10)') omg

!       Do i=2,Nf
!          if(omg(i)<omg(i-1)) then
!              write(6,*) ' ERROR in mc_Const '
!              write(6,'(3x,'' >> '',i4,f12.6)') i-1,omg(i-1)
!              write(6,'(3x,'' >> '',i4,f12.6)') i,omg(i)
!              write(6,*) ' omega must be listed in an increasing order'
!              stop
!          endif
!       End do

       Do i=1,Nf
          omginv(i)=1.D+00/(2.D+00*omg(i))
          omginv2(i)=omginv(i)*omginv(i)
       End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine mc_destruct()

      USE mc_mod

       if(allocated(omg)) Deallocate(omg)
       if(allocated(omginv)) Deallocate(omginv)
       if(allocated(omginv2)) Deallocate(omginv2)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function mc_getInt2(i0,j0)

      USE Constants_mod
      USE mc_mod

      Implicit None

      Integer :: i0,j0,i,j,k
      Real(8) :: mc_getInt2
      Real(8) :: aa(5),bb,cc
      Real(8) :: qff_GetHij,qff_GetTiij, qff_GetUiijj, qff_GetUiiij

      common / aa2 / aa

         if(i0>j0) then
            i=i0; j=j0
         else
            i=j0; j=i0
         endif
         k=(i-1)*(i-2)/2 + j

         ! First-order PT
         aa(1)=abs(qff_GetUiijj(k))*omginv(i)*omginv(j)

         ! 2:1 (Fermi) resonance
         cc=qff_GetTiij(k,1)
         aa(2)=2.D+00*cc*cc/abs(omg(i)-2.D+00*omg(j)+dd) &
              *omginv(i)*omginv2(j)

         cc=qff_GetTiij(k,0)
         aa(2)=aa(2)+ 2.D+00*cc*cc/abs(omg(j)-2.D+00*omg(i)+dd) &
              *omginv(j)*omginv2(i)

         ! 1:1 resonance
         cc=qff_GetUiiij(k,0)
         aa(3)=9.D+00*cc*cc/abs(omg(i)-omg(j)+dd) &
              *omginv2(i)*omginv(i)*omginv(j)

         cc=qff_GetUiiij(k,1)
         aa(3)=aa(3)+9.D+00*cc*cc/abs(omg(i)-omg(j)+dd) &
              *omginv(i)*omginv2(j)*omginv(j)

         cc=qff_GetHij(k)
         aa(3)=aa(3)+cc*cc/abs(omg(i)-omg(j)+dd) &
              *omginv(i)*omginv(j)

         ! 3:1 resonance
         cc=qff_GetUiiij(k,1)
         aa(4)=6.D+00*cc*cc/abs(omg(i)-3.D+00*omg(j)+dd) &
              *omginv(i)*omginv2(j)*omginv(j)

         cc=qff_GetUiiij(k,0)
         aa(4)=aa(4)+ 6.D+00*cc*cc/abs(omg(j)-3.D+00*omg(i)+dd) &
              *omginv(j)*omginv2(i)*omginv(i)

         ! 2:2 resonance
         cc=qff_GetUiijj(k)
         aa(5)=2.D+00*cc*cc/abs(omg(j)-omg(i)+dd) &
              *omginv2(j)*omginv2(i)

         aa=aa*H2wvn
         bb=aa(1)+aa(2)+aa(3)+aa(4)+aa(5)
         !write(6,'(6f12.6)') aa(1:5),bb

         mc_getInt2=bb

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  n=1   : 1st-order PT
!   =2-4 : 2nd-order PT
!          =2, 2:1 Resonance
!          =3, 1:1 Resonance
!          =4, 3:1 Resonance
!          =5, 2:2 Resonance

   Function mc_getA2(n)

      Integer :: n
      Real(8) :: mc_getA2,aa(5)

      common / aa2 / aa

         mc_getA2=aa(n)

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Function mc_getInt3(i0,j0,k0)

      USE Constants_mod
      USE mc_mod

      Implicit None

      Integer :: i0,j0,k0,i,j,k,l
      Real(8) :: mc_getInt3
      Real(8) :: aa(3),cijk,ciijk,cijjk,cijkk
      Real(8) :: qff_GetTijk, qff_GetUiijk

      common / aa3 / aa

       if(i0<j0) then
          if(k0<i0) then
             ! j0>i0>k0
             i=j0; j=i0; k=k0
          elseif(k0>j0) then
             ! k0>j0>i0
             i=k0; j=j0; k=i0
          else
             ! j0>k0>i0
             i=j0; j=k0; k=i0
          endif

       else
          if(k0<j0) then
             ! i0>j0>k0
             i=i0; j=j0; k=k0
          elseif(k0>i0) then
             ! k0>i0>j0
             i=k0; j=i0; k=j0
          else
             ! i0>k0>j0
             i=i0; j=k0; k=j0
          endif

       endif

       l=(i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k

       cijk=qff_GetTijk(l)
       ciijk=qff_GetUiijk(l,0)
       cijjk=qff_GetUiijk(l,1)
       cijkk=qff_GetUiijk(l,2)
       l=l+1

       ! 1:1:1 resonance
       aa(1)=cijk*cijk/abs(omg(i)-omg(j)-omg(k)+dd) &
                 *omginv(i)*omginv(j)*omginv(k)
       aa(1)=aa(1) + cijk*cijk/abs(omg(j)-omg(i)-omg(k)+dd) &
                         *omginv(i)*omginv(j)*omginv(k)
       aa(1)=aa(1) + cijk*cijk/abs(omg(k)-omg(j)-omg(i)+dd) &
                         *omginv(i)*omginv(j)*omginv(k)

       ! 2:1:1 resonance
       aa(2)=2.D+00*ciijk*ciijk/abs(omg(j)-omg(i)*2.D+00-omg(k)+dd) &
                   *omginv2(i)*omginv(j)*omginv(k)
       aa(2)=aa(2) + 2.D+00*ciijk*ciijk/abs(omg(k)-omg(i)*2.D+00-omg(j)+dd) &
                           *omginv2(i)*omginv(j)*omginv(k)
       aa(2)=aa(2) + 2.D+00*ciijk*ciijk/abs(omg(i)*2.D+00-omg(j)-omg(k)+dd) &
                           *omginv2(i)*omginv(j)*omginv(k)

       aa(2)=aa(2) + 2.D+00*cijjk*cijjk/abs(omg(i)-omg(j)*2.D+00-omg(k)+dd) &
                           *omginv(i)*omginv2(j)*omginv(k)
       aa(2)=aa(2) + 2.D+00*cijjk*cijjk/abs(omg(k)-omg(j)*2.D+00-omg(i)+dd) &
                           *omginv(i)*omginv2(j)*omginv(k)
       aa(2)=aa(2) + 2.D+00*cijjk*cijjk/abs(omg(j)*2.D+00-omg(i)-omg(k)+dd) &
                           *omginv(i)*omginv2(j)*omginv(k)

       aa(2)=aa(2) + 2.D+00*cijkk*cijkk/abs(omg(i)-omg(k)*2.D+00-omg(j)+dd) &
                           *omginv(i)*omginv(j)*omginv2(k)
       aa(2)=aa(2) + 2.D+00*cijkk*cijkk/abs(omg(j)-omg(k)*2.D+00-omg(i)+dd) &
                           *omginv(i)*omginv(j)*omginv2(k)
       aa(2)=aa(2) + 2.D+00*cijkk*cijkk/abs(omg(k)*2.D+00-omg(i)-omg(j)+dd) &
                           *omginv(i)*omginv(j)*omginv2(k)

       aa(3)=aa(1)+aa(2)
       aa=aa*H2wvn

       mc_getInt3=aa(3)

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  n=1 : 1:1:1 Resonance
!   =2 : 2:1:1 Resonance

   Function mc_getA3(n)

      Integer :: n
      Real(8) :: mc_getA3,aa(3)

      common / aa3 / aa

         mc_getA3=aa(n)

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Function mc_getInt4(i0,j0,k0,l0)

      USE Constants_mod
      USE mc_mod

      Implicit None

      Integer :: i0,j0,k0,l0,ii(4),jj(1),i,j,k,l,m
      Real(8) :: mc_getInt4
      Real(8) :: aa,cijkl
      Real(8) :: qff_GetUijkl

       ii(1)=i0
       ii(2)=j0
       ii(3)=k0
       ii(4)=l0

       Do i=1,4
          jj=MaxLoc(ii(i:4))
          j=jj(1)+i-1

          k=ii(i)
          ii(i)=ii(j)
          ii(j)=k
          
       End do
       !write(6,'(4i4)') ii

       m=(ii(1)-1)*(ii(1)-2)*(ii(1)-3)*(ii(1)-4)/24 &
        +(ii(2)-1)*(ii(2)-2)*(ii(2)-3)/6 &
        +(ii(3)-1)*(ii(3)-2)/2 &
        + ii(4)

       i=ii(1)
       j=ii(2)
       k=ii(3)
       l=ii(4)
       cijkl=qff_GetUijkl(m)
       cijkl=cijkl*cijkl

       aa = 1.0D+00/abs(omg(i) - omg(j) - omg(k) - omg(l) +dd) &
          + 1.0D+00/abs(omg(j) - omg(i) - omg(k) - omg(l) +dd) &
          + 1.0D+00/abs(omg(k) - omg(i) - omg(j) - omg(l) +dd) &
          + 1.0D+00/abs(omg(l) - omg(i) - omg(j) - omg(k) +dd) &
          + 1.0D+00/abs(omg(i) + omg(j) - omg(k) - omg(l) +dd) &
          + 1.0D+00/abs(omg(i) + omg(k) - omg(j) - omg(l) +dd) &
          + 1.0D+00/abs(omg(i) + omg(l) - omg(j) - omg(k) +dd) 
       aa = aa*cijkl*omginv(i)*omginv(j)*omginv(k)*omginv(l)
       aa=aa*H2wvn
       mc_getInt4=aa

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
