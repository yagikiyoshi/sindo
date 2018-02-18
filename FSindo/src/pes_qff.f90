!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/07
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module PES_qff_mod

   USE PES_mod

   !  coeff1  :: Coefficients
   Real(8), dimension(:,:), allocatable :: coeff1,coeff2,coeff3,coeff4

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Setup through QFF module
!

   Subroutine PES_qff_construct()

   USE PES_qff_mod

   Implicit None

   Integer :: ierr

   Integer :: i,j,k,l,m,n
   Real(8) :: qff_getGi, qff_getHii, qff_getTiii, qff_getUiiii,    &
              qff_getHij, qff_getTiij, qff_getUiijj, qff_getUiiij, &
              qff_getTijk, qff_getUiijk, qff_getUijkl


      if(nQ1/=0) then
         Call Mem_alloc(-1,ierr,'D',nQ1*4)
         Allocate(coeff1(4,nQ1))
         Do i=1,nQ1
            coeff1(1,i)=qff_getGi(mQ1(1,i))
            coeff1(2,i)=qff_getHii(mQ1(1,i))
            coeff1(3,i)=qff_getTiii(mQ1(1,i))
            coeff1(4,i)=qff_getUiiii(mQ1(1,i))
         End do
      endif

      if(MR==1) goto 1000

      if(nQ2/=0) then
         Call Mem_alloc(-1,ierr,'D',nQ2*6)
         allocate(coeff2(6,nQ2))
         Do n=1,nQ2
            i=mQ2(1,n)
            j=mQ2(2,n)
            k=(i-1)*(i-2)/2 + j
            coeff2(1,n)=qff_getHij(k)
            coeff2(2,n)=qff_getTiij(k,0)
            coeff2(3,n)=qff_getTiij(k,1)
            coeff2(4,n)=qff_getUiijj(k)
            coeff2(5,n)=qff_getUiiij(k,0)
            coeff2(6,n)=qff_getUiiij(k,1)
         End do
      endif

      if(MR==2) goto 1000

      if(nQ3/=0) then
         Call Mem_alloc(-1,ierr,'D',nQ3*4)
         allocate(coeff3(4,nQ3))
         Do n=1,nQ3
            i=mQ3(1,n)
            j=mQ3(2,n)
            k=mQ3(3,n)
            l=(i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k
            coeff3(1,n)=qff_getTijk(l)
            coeff3(2,n)=qff_getUiijk(l,0)
            coeff3(3,n)=qff_getUiijk(l,1)
            coeff3(4,n)=qff_getUiijk(l,2)
         End do
      endif

      if(MR==3) goto 1000

    ! 4MR-PEFs
      if(nQ4/=0) then
         Call Mem_alloc(-1,ierr,'D',nQ4)
         allocate(coeff4(1,nQ4))
         Do n=1,nQ4
            i=mQ4(1,n)
            j=mQ4(2,n)
            k=mQ4(3,n)
            l=mQ4(4,n)
            m=(i-1)*(i-2)*(i-3)*(i-4)/24 + (j-1)*(j-2)*(j-3)/6 &
             +(k-1)*(k-2)/2 + l
            coeff4(1,n)=qff_getUijkl(m)
         End do
      endif

 1000 Continue

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine PES_qff_destruct()

   USE PES_qff_mod

      if(allocated(coeff1)) then
         Call Mem_dealloc('D',size(coeff1))
         Deallocate(coeff1)
      endif
      if(MR==1) return

      if(allocated(coeff2)) then
         Call Mem_dealloc('D',size(coeff2))
         Deallocate(coeff2)
      endif
      if(MR==2) return

      if(allocated(coeff3)) then
         Call Mem_dealloc('D',size(coeff3))
         Deallocate(coeff3)
      endif
      if(MR==3) return

      if(allocated(coeff4)) then
         Call Mem_dealloc('D',size(coeff4))
         Deallocate(coeff4)
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
