!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/08/01
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Module Hmat_mod

   USE Vib_mod, only : Nfree,nCHO,maxCHO

   ! -- (Kinetic energy) ---------------------------------------------------

   Integer, allocatable :: ptTmat(:)
   Real(8), allocatable :: Tmat(:)

   ! -- (Q-Matrix) ---------------------------------------------------------
   !     Qmat(4,maxCHO,maxCHO,Nfree) :: Matrix elements of Q,Q^2,Q^3,Q^4
   !                                    in terms of modals

   Real(8), dimension(:,:,:,:), allocatable :: Qmat


   Contains

   Subroutine genKinmat(mm,nCHOm,Tm)

   Implicit None 

   Integer :: mm,nCHOm
   Integer :: i,j,k,l
   Real(8) :: Tm(nCHOm,nCHOm)
   Real(8), allocatable :: CHOm(:,:),Thm(:,:)

      Allocate(CHOm(nCHOm,nCHOm),Thm(nCHOm,nCHOm))
      Call KE_genTmat(mm,nCHOm,Thm)
      Call Modal_getCwfn(mm,CHOm)

      !  Tm = CHOm*Thm*CHOm
      Do i=1,nCHOm
         Tm(i,i)=0.D+00
         Do k=1,nCHOm
         Do l=1,nCHOm
            Tm(i,i)=Tm(i,i) + CHOm(l,i)*Thm(l,k)*CHOm(k,i)
         End do
         End do
      End do

      Do i=1,nCHOm
      Do j=1,i-1
         Tm(j,i)=0.D+00
         Do k=1,nCHOm
         Do l=1,nCHOm
            Tm(j,i)=Tm(j,i) + CHOm(l,j)*Thm(l,k)*CHOm(k,i)
         End do
         End do
         Tm(i,j)=Tm(j,i)
      End do
      End do

      Deallocate(CHOm,Thm)

      !dbg write(6,*) mm
      !dbg Do i=1,nCHOm
      !dbg    write(6,'(11f8.4)') Tm(:,i)
      !dbg End do

   End subroutine

   End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Hmat_construct()

   USE Hmat_mod

   Implicit None

   Integer :: memsz,i,j
   Logical :: qff,PES_isQFF

   Integer :: q1,q2,qi,nGi
   Real(8) :: Di(maxCHO),tmp
   Real(8), allocatable :: qdi(:,:)

      Call Mem_alloc(-1,i,'I',Nfree)
      Allocate(ptTmat(Nfree))

      ptTmat(1)=0
      Do i=2,Nfree
         ptTmat(i)=ptTmat(i-1) + nCHO(i-1)*nCHO(i-1)
      End do
      memsz = ptTmat(Nfree) + nCHO(Nfree)*nCHO(Nfree)

      Call Mem_alloc(-1,i,'D',memsz)
      Allocate(Tmat(memsz))
      Do i=1,Nfree
         if(nCHO(i)==0) cycle
         Call genKinmat(i,nCHO(i),Tmat(ptTmat(i)+1:ptTmat(i)+nCHO(i)*nCHO(i)))
      End do


      qff = PES_isQFF()
      if(qff) then
         Call Mem_alloc(-1,i,'D',4*maxCHO*maxCHO*Nfree)
         Allocate(Qmat(4,0:maxCHO-1,0:maxCHO-1,Nfree))

         Do i=1,Nfree
            if(nCHO(i)==0) cycle
            nGi=nCHO(i)
            Allocate(qdi(nGi,4))
            Call Modal_getQ4(i,qdi)
  
            Do q1=0,nCHO(i)-1
               Call Modal_getXwfn(i,nGi,q1,q1,Di)
  
               ! <Qi>,<Qi^2>,<Qi^3>,<Qi^4>
               Do j=1,4
                  tmp=0.D+00
                  Do qi=1,nGi
                     tmp=tmp + Di(qi)*qdi(qi,j)
                  End do
                  Qmat(j,q1,q1,i)=tmp
               End do
  
               Do q2=0,q1-1
                  Call Modal_getXwfn(i,nGi,q1,q2,Di)
                  ! <Qi>,<Qi^2>,<Qi^3>,<Qi^4>
                  Do j=1,4
                     tmp=0.D+00
                     Do qi=1,nGi
                        tmp=tmp + Di(qi)*qdi(qi,j)
                     End do
                     Qmat(j,q2,q1,i)=tmp
                     Qmat(j,q1,q2,i)=tmp
                  End do
  
               End do
            End do
  
            Deallocate(qdi)
  
         End do

      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Hmat_destruct()

   USE Hmat_mod

      Call Mem_dealloc('I',size(ptTmat))
      Deallocate(ptTmat)
      Call Mem_dealloc('D',size(Tmat))
      Deallocate(Tmat)
      if(allocated(Qmat)) then
         Call Mem_dealloc('D',size(Qmat))
         Deallocate(Qmat)
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  cp       ... the number modes with different quantum number
!  mode(cp) ... the modes with different quantum number
!               mode(1) < mode(2) <...
!
   Subroutine Hmat_getHmat(cp,mode,mCnf,nCnf,Hmat)

   USE Hmat_mod
   USE PES_mod
   USE PES_qff_mod
   USE PES_grid_mod

   Implicit None

   Integer :: cp
   Integer :: nCnf(Nfree),mCnf(Nfree),mode(cp)
   Real(8) :: Hmat

   Integer :: i,j,k
   Integer :: mij,mijk,mi,mj,mk,ml,nGi,nGj,nGk,nGl,qi,qj,qk,ql,m,n,itype
   Real(8), allocatable :: Di(:),Dj(:),Dk(:)
   Real(8), allocatable :: Vi(:),Vij(:,:),Vijk(:,:,:)
   Real(8) :: Ekin,Epot,Vcor_getKinmat
   Real(8) :: tmp,tmp2,tmp3
   Logical :: done

      Hmat=0.D+00

      Ekin=0.D+00; Epot=0.D+00
      Select Case(cp)

     ! [ Diagonal ]----------------------------------------------------------- 
         Case(0) 
            Do mi=1,Nfree
               if(nCHO(mi)/=0) Ekin=Ekin + Tmat(ptTmat(mi)+nCHO(mi)*mCnf(mi)+nCnf(mi)+1)
            End do

          ! 1-mode terms
            Do n=1,nQ1
               Epot=Epot + qff1(mQ1(1,n),n)
            End do
            Do n=1,nS1
               Epot=Epot + grid1(mS1(1,n),n)
            End do

            if(MR==1) goto 100

          ! 2-mode terms
            Do n=1,nQ2
               Epot=Epot + qff2(mQ2(1,n),mQ2(2,n),n)
            End do
            Do n=1,nS2
               Epot=Epot + grid2(mS2(1,n),mS2(2,n),n)
            End do

            if(MR==2) goto 100

          ! 3-mode terms
            Do n=1,nQ3
               Epot=Epot + qff3(mQ3(1,n),mQ3(2,n),mQ3(3,n),n)
            End do
            Do n=1,nS3
               Epot=Epot + grid3(mS3(1,n),mS3(2,n),mS3(3,n),n)
            End do

            if(MR==3) goto 100

          ! 4-mode terms
            Do n=1,nQ4
               Epot=Epot + qff4(mQ4(1,n),mQ4(2,n),mQ4(3,n),mQ4(4,n),n)
            End do

            !Do n=1,nS4
            !End do
            if(MR==4) goto 100

        100 Continue
            Hmat=Ekin+Epot

     ! [ 1-mode exc. ]-------------------------------------------------------- 
         Case(1) 
            mi=mode(1)
            Ekin=Tmat(ptTmat(mi)+nCHO(mi)*mCnf(mi)+nCnf(mi)+1)

          ! 1-mode terms
            mi=mode(1)
            itype=idx1MR(1,mi)
            n=idx1MR(2,mi)

            Select Case(itype)
            Case(1)
               Epot=Epot + qff1(mi,n)
            Case(2)
               Epot=Epot + grid1(mi,n)
            End select

            if(MR==1) goto 200

          ! 2-mode terms
            mi=mode(1)
            Do mj=1,mi-1
               mij=(mi-1)*(mi-2)/2 + mj
               itype=idx2MR(1,mij)
               n=idx2MR(2,mij)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff2(mi,mj,n)
               Case(2)
                  Epot=Epot + grid2(mi,mj,n)
               End select
            End do

            mj=mode(1)
            Do mi=mj+1,Nfree
               mij=(mi-1)*(mi-2)/2 + mj
               itype=idx2MR(1,mij)
               n=idx2MR(2,mij)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff2(mi,mj,n)
               Case(2)
                  Epot=Epot + grid2(mi,mj,n)
               End select
            End do

            if(MR==2) goto 200

          ! 3-mode terms
            mi=mode(1)
            Do mj=1,mi-1
            Do mk=1,mj-1
               mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                   +(mj-1)*(mj-2)/2 &
                   +mk
               itype=idx3MR(1,mijk)
               n=idx3MR(2,mijk)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff3(mi,mj,mk,n)
               Case(2)
                  Epot=Epot + grid3(mi,mj,mk,n)
               End select

            End do
            End do

            mj=mode(1)
            Do mi=mj+1,Nfree
            Do mk=1,mj-1
               mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                   +(mj-1)*(mj-2)/2 &
                   +mk
               itype=idx3MR(1,mijk)
               n=idx3MR(2,mijk)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff3(mi,mj,mk,n)
               Case(2)
                  Epot=Epot + grid3(mi,mj,mk,n)
               End select

            End do
            End do

            mk=mode(1)
            Do mi=mk+2,Nfree
            Do mj=mk+1,mi-1
               mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                   +(mj-1)*(mj-2)/2 &
                   +mk
               itype=idx3MR(1,mijk)
               n=idx3MR(2,mijk)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff3(mi,mj,mk,n)
               Case(2)
                  Epot=Epot + grid3(mi,mj,mk,n)
               End select

            End do
            End do

! Loop without idx3MR
!            mi=mode(1)
!            Do n=1,nQ3
!               Do i=1,3
!                  if(mi == mQ3(i,n)) then
!                     Epot=Epot + qff3(mQ3(1,n),mQ3(2,n),mQ3(3,n),n)
!                     exit
!                  endif
!               End do
!            End do
!
!            Do n=1,nS3
!               Do i=1,3
!                  if(mi == mS3(i,n)) then
!                     Epot=Epot + grid3(mS3(1,n),mS3(2,n),mS3(3,n),n)
!                     exit
!                  endif
!               End do
!            End do

            if(MR==3) goto 200

          ! 4-mode terms
            mi=mode(1)
            Do n=1,nQ4
               Do i=1,4
                  if(mi == mQ4(i,n)) then
                     Epot=Epot + qff4(mQ4(1,n),mQ4(2,n),mQ4(3,n),mQ4(4,n),n)
                     exit
                  endif
               End do
            End do

            if(MR==4) goto 200

        200 Continue
            Hmat=Ekin + Epot

     ! [ 2-mode exc. ]-------------------------------------------------------- 
         Case(2) 

            if(MR==1) goto 300

          ! 2-mode terms
            mi=mode(2); mj=mode(1)
            mij=(mi-1)*(mi-2)/2 + mj
            itype=idx2MR(1,mij)
            n=idx2MR(2,mij)
            Select Case(itype)
            Case(1)
               Epot=Epot + qff2(mi,mj,n)
            Case(2)
               Epot=Epot + grid2(mi,mj,n)
            End select

            if(MR==2) goto 300

          ! 3-mode terms
            mi=mode(2)
            mj=mode(1)
            Do mk=1,mj-1
               mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                   +(mj-1)*(mj-2)/2 &
                   +mk
               itype=idx3MR(1,mijk)
               n=idx3MR(2,mijk)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff3(mi,mj,mk,n)
               Case(2)
                  Epot=Epot + grid3(mi,mj,mk,n)
               End select

            End do

            mi=mode(2)
            mk=mode(1)
            Do mj=mk+1,mi-1
               mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                   +(mj-1)*(mj-2)/2 &
                   +mk
               itype=idx3MR(1,mijk)
               n=idx3MR(2,mijk)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff3(mi,mj,mk,n)
               Case(2)
                  Epot=Epot + grid3(mi,mj,mk,n)
               End select

            End do

            mj=mode(2)
            mk=mode(1)
            Do mi=mj+1,Nfree
               mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                   +(mj-1)*(mj-2)/2 &
                   +mk
               itype=idx3MR(1,mijk)
               n=idx3MR(2,mijk)

               Select Case(itype)
               Case(1)
                  Epot=Epot + qff3(mi,mj,mk,n)
               Case(2)
                  Epot=Epot + grid3(mi,mj,mk,n)
               End select

            End do

! Loop without idx3MR
!            mi=mode(2); mj=mode(1)
!            Do n=1,nQ3
!               Do i=1,2
!               if(mi == mQ3(i,n)) then
!                  Do j=i+1,3
!                  if(mj == mQ3(j,n)) then
!                     Epot=Epot + qff3(mQ3(1,n),mQ3(2,n),mQ3(3,n),n)
!                     exit
!                  endif
!                  End do
!                  exit
!               endif
!               End do
!            End do
!
!            Do n=1,nS3
!               Do i=1,2
!               if(mi == mS3(i,n)) then
!                  Do j=i+1,3
!                  if(mj == mS3(j,n)) then
!                     Epot=Epot + grid3(mS3(1,n),mS3(2,n),mS3(3,n),n)
!                     exit
!                  endif
!                  End do
!                  exit
!               endif
!               End do
!            End do

            if(MR==3) goto 300

          ! 4-mode terms
            mi=mode(2); mj=mode(1)
            Do n=1,nQ4
               Do i=1,3
               if(mi == mQ4(i,n)) then
                  Do j=i+1,4
                  if(mj == mQ4(j,n)) then
                     Epot=Epot + qff4(mQ4(1,n),mQ4(2,n),mQ4(3,n),mQ4(4,n),n)
                     exit
                  endif
                  End do
                  exit
               endif
               End do
            End do

            if(MR==4) goto 300

        300 Continue
            Hmat=Epot

     !-[ 3-mode exc. ]-------------------------------------------------------- 
         Case(3) 

            if(MR<3) goto 400

          ! 3-mode terms
            mi=mode(3)
            mj=mode(2)
            mk=mode(1)
            mijk=(mi-1)*(mi-2)*(mi-3)/6 &
                +(mj-1)*(mj-2)/2 &
                +mk
            itype=idx3MR(1,mijk)
            n=idx3MR(2,mijk)

            Select Case(itype)
            Case(1)
               Epot=Epot + qff3(mi,mj,mk,n)
            Case(2)
               Epot=Epot + grid3(mi,mj,mk,n)
            End select

! Loop without idx3MR
!            mi=mode(3); mj=mode(2); mk=mode(1)
!            done=.false.
!            Do n=1,nQ3
!               if(mi == mQ3(1,n)) then
!               if(mj == mQ3(2,n)) then
!               if(mk == mQ3(3,n)) then
!                  Epot=Epot + qff3(mQ3(1,n),mQ3(2,n),mQ3(3,n),n)
!                  done=.true.
!                  exit
!               endif
!               endif
!               endif
!            End do
!
!            if(.not. done) then
!               Do n=1,nS3
!                  if(mi == mS3(1,n)) then
!                  if(mj == mS3(2,n)) then
!                  if(mk == mS3(3,n)) then
!                     Epot=Epot + grid3(mS3(1,n),mS3(2,n),mS3(3,n),n)
!                     exit
!                  endif
!                  endif
!                  endif
!               End do
!            endif

            if(MR==3) goto 400

          ! 4-mode terms
            mi=mode(3); mj=mode(2); mk=mode(1)
            Do n=1,nQ4
               Do i=1,2
               if(mi == mQ4(i,n)) then
                  Do j=i+1,3
                  if(mj == mQ4(j,n)) then
                     Do k=j+1,4
                     if(mk == mQ4(k,n)) then
                        Epot=Epot + qff4(mQ4(1,n),mQ4(2,n),mQ4(3,n),mQ4(4,n),n)
                        exit
                     endif
                     End do
                     exit
                  endif
                  End do
                  exit
               endif
               End do
            End do

            if(MR==4) goto 400

        400 Continue
            Hmat=Epot

     !-[ 4-mode exc. ]-------------------------------------------------------- 
         Case(4) 

            if(MR<4) goto 500

          ! 4-mode terms
            mi=mode(4); mj=mode(3); mk=mode(2); ml=mode(1)
            Do n=1,nQ4
               if(mi == mQ4(1,n)) then
                  if(mj == mQ4(2,n)) then
                     if(mk == mQ4(3,n)) then
                        if(ml == mQ4(4,n)) then
                           Epot=Epot + qff4(mQ4(1,n),mQ4(2,n),mQ4(3,n),mQ4(4,n),n)
                           exit
                        endif
                     endif
                  endif
               endif
            End do


        500 Continue
            Hmat=Epot

      End select

      Contains

      Function qff1(mi,n)

         Integer :: mi,n
         Real(8) :: qff1

         qff1= Qmat(1,mCnf(mi),nCnf(mi),mi)*coeff1(1,n) &
             + Qmat(2,mCnf(mi),nCnf(mi),mi)*coeff1(2,n) &
             + Qmat(3,mCnf(mi),nCnf(mi),mi)*coeff1(3,n) &
             + Qmat(4,mCnf(mi),nCnf(mi),mi)*coeff1(4,n)

      End Function


      Function qff2(mi,mj,n)

         Integer :: mi,mj,n
         Real(8) :: qff2

         qff2= Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *coeff2(1,n) &
             + Qmat(2,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *coeff2(2,n) &
             + Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(2,mCnf(mj),nCnf(mj),mj) &
              *coeff2(3,n) &
             + Qmat(2,mCnf(mi),nCnf(mi),mi) &
              *Qmat(2,mCnf(mj),nCnf(mj),mj) &
              *coeff2(4,n) &
             + Qmat(3,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *coeff2(5,n) &
             + Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(3,mCnf(mj),nCnf(mj),mj) &
              *coeff2(6,n) 

      End Function

      Function qff3(mi,mj,mk,n)
         Integer :: mi,mj,mk,n
         Real(8) :: qff3

         qff3= Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *Qmat(1,mCnf(mk),nCnf(mk),mk) &
              *coeff3(1,n) &
             + Qmat(2,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *Qmat(1,mCnf(mk),nCnf(mk),mk) &
              *coeff3(2,n) &
             + Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(2,mCnf(mj),nCnf(mj),mj) &
              *Qmat(1,mCnf(mk),nCnf(mk),mk) &
              *coeff3(3,n) &
             + Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *Qmat(2,mCnf(mk),nCnf(mk),mk) &
              *coeff3(4,n) 

      End Function

      Function qff4(mi,mj,mk,ml,n)
         Integer :: mi,mj,mk,ml,n
         Real(8) :: qff4

         qff4= Qmat(1,mCnf(mi),nCnf(mi),mi) &
              *Qmat(1,mCnf(mj),nCnf(mj),mj) &
              *Qmat(1,mCnf(mk),nCnf(mk),mk) &
              *Qmat(1,mCnf(ml),nCnf(ml),ml) &
              *coeff4(1,n)

      End Function

      Function grid1(mi,n)

         Integer :: mi,n
         Real(8) :: grid1

         Integer :: qi

         nGi=nGrid1(1,n)
         Allocate(Vi(nGi),Di(nGi))

         Call PES_grid_getV1(n,Vi)
         Call Modal_getXwfn(mi,nGi,mCnf(mi),nCnf(mi),Di)

         grid1=0.D+00
         Do qi=1,nGi
            grid1=grid1 + Di(qi)*Vi(qi)
         End do

         Deallocate(Vi,Di)

      End Function

      Function grid2(mi,mj,n)

         Integer :: mi,mj,n
         Real(8) :: grid2

         Integer :: qi,qj
         Real(8) :: tmp

         nGi=nGrid2(1,n)
         nGj=nGrid2(2,n)
         Allocate(Vij(nGj,nGi),Di(nGi),Dj(nGj))
         Call PES_grid_getV2(n,Vij)
         Call Modal_getXwfn(mi,nGi,mCnf(mi),nCnf(mi),Di)
         Call Modal_getXwfn(mj,nGj,mCnf(mj),nCnf(mj),Dj)

         grid2=0.D+00
         Do qi=1,nGi
            tmp=0.D+00
            Do qj=1,nGj
               tmp=tmp + Dj(qj)*Vij(qj,qi)
            End do
            grid2=grid2 + Di(qi)*tmp
         End do

         Deallocate(Vij,Di,Dj)

      End Function

      Function grid3(mi,mj,mk,n)

         Integer :: mi,mj,mk,n
         Real(8) :: grid3

         Integer :: qi,qj,qk
         Real(8) :: tmp2,tmp3

         nGi=nGrid3(1,n)
         nGj=nGrid3(2,n)
         nGk=nGrid3(3,n)
         Allocate(Vijk(nGk,nGj,nGi))
         Allocate(Di(nGi),Dj(nGj),Dk(nGk))
         Call PES_grid_getV3(n,Vijk)
         Call Modal_getXwfn(mi,nGi,mCnf(mi),nCnf(mi),Di)
         Call Modal_getXwfn(mj,nGj,mCnf(mj),nCnf(mj),Dj)
         Call Modal_getXwfn(mk,nGk,mCnf(mk),nCnf(mk),Dk)

         grid3=0.D+00
         Do qi=1,nGi
            tmp2=0.D+00
            Do qj=1,nGj
               tmp3=0.D+00
               Do qk=1,nGk
                  tmp3=tmp3 + Dk(qk)*Vijk(qk,qj,qi)
               End do
               tmp2=tmp2 + Dj(qj)*tmp3
            End do
            grid3=grid3 + Di(qi)*tmp2
         End do

         Deallocate(Vijk,Di,Dj,Dk)

      End Function

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
