!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2009/03/02
!   Copyright 2009
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module choReader_mod

   Integer, allocatable :: nHO(:),nCHO(:),ptCHO(:)
   Real(8), allocatable :: CHO(:)

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine choReader_Const(Nfree)

USE choReader_mod

   Implicit None

   Integer :: Nfree
   Integer :: rCHO,mHO,mCHO

   Integer :: i,j,k,l
   Real(8), allocatable :: CHOi(:,:)

      Allocate(nHO(Nfree),nCHO(Nfree),ptCHO(Nfree+1))

    ! Read nHO, nCHO
      ptCHO(1)=0
      Call file_indicator(50,rCHO)
      Open(rCHO,file='cho-r.wfn',status='OLD',form='UNFORMATTED')
      Do i=1,Nfree
         Read(rCHO) mHO,mCHO
         if(mCHO==0) cycle
         Allocate(CHOi(mHO,mCHO))
         Read(rCHO) CHOi
         Deallocate(CHOi)

         nHO(i)=mHO
         nCHO(i)=mCHO
         ptCHO(i+1)=ptCHO(i)+ nHO(i)*nCHO(i)

      End do

    ! Setup CHO
      Allocate(CHO(ptCHO(Nfree+1)))

      Rewind(rCHO)

      Do i=1,Nfree
         Read(rCHO) mHO,mCHO
         if(mCHO==0) cycle
         Allocate(CHOi(mHO,mCHO))
         Read(rCHO) CHOi
         l=1
         Do j=1,mCHO
         Do k=1,mHO
            CHO(ptCHO(i)+l)=CHOi(k,j)
            l=l+1
         End do
         End do
         Deallocate(CHOi)

      End do
      Close(rCHO)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine choReader_Dest()

USE choReader_mod

   Deallocate(nHO,nCHO,ptCHO,CHO)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine choReader_getnHO(m,mHO)

USE choReader_mod

Implicit None

   Integer :: m,mHO

   mHO=nHO(m)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine choReader_getnCHO(m,mCHO)

USE choReader_mod

Implicit None

   Integer :: m,mCHO

   mCHO=nCHO(m)

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
Subroutine choReader_getCHO(m,CHOm)

USE choReader_mod

Implicit None

   Integer :: m
   Real(8) :: CHOm(1:nHO(m)*nCHO(m))

   CHOm=CHO(ptCHO(m)+1:ptCHO(m+1))

End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

