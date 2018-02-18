!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
Module nma_private
!
!     ----------------------------------(Parameters)----------------------------------
!    |    Nat  : The number of atoms                                                  |
!    |    Nat3 : The number of atoms * 3                                              |
!    |    Nfree: The number of internal degrees of freedom, i.e. Nat3-6(or Nat3-5)    |
!     --------------------------------------------------------------------------------
!
  Integer :: Nat, Nat3, Nfree
!
! MASS / AU
  Real(8), dimension(:), Allocatable :: Zmass
!
! GEOMETRY / AU
  Real(8), dimension(:,:), Allocatable:: x0,x1
  Real(8) :: xg(3)
!
! NORMAL DISPLACEMENT VECTOR
  Real(8), dimension(:,:), Allocatable:: CL
! TRANSLATIONAL VECTOR
  Real(8), dimension(:,:), Allocatable:: CT
! ROTATIONAL VECTOR
  Real(8), dimension(:,:), Allocatable:: CR
! HARMONIC FREQUENCIES / CM-1
  Real(8), dimension(:), Allocatable:: Freq
! PI,DELTA-THETA
!  Real(8) :: PI,dth,theta(3),dvec(3),L1(3,3),L2(3,3)
  Real(8) :: PI,dth,theta(3),dvec(3),L1(3,3)
  Integer :: iatm,jatm,katm,atm1,atm2
!
  Logical :: setup,rotvec
!
  Integer :: linear

  Contains

  Subroutine commonSetting()

    Implicit None

    Integer :: i,j,k,ax(3)
    Real(8) :: aa,bb,xl(3),yl(3)
    logical :: ll

      PI=Acos(-1.D+00)
      dth=0.01D+00/180D+00*PI

      Allocate(x1(3,Nat))
      x1=x0
      Call CM(Nat,Zmass,x1,xg)
      !write(6,'(3f12.6)') x1
      !write(6,*)

      ! Set dvec
      ax=0
      Do i=1,3
         Do j=1,Nat
            if(abs(x1(i,j))>1.D-08) then
               ax(i)=1
               exit
            endif
         End do
      End do
      !write(6,'(3i3)') ax

      atm1=0
      bb=0.D+00
      Do i=1,Nat
      Do j=1,i-1
         dvec=x1(:,i)-x1(:,j)
         ll=.true.
         Do k=1,3
            if(abs(dvec(k))<1.D-08 .and. ax(k)==1) ll=.false. 
         End do
         if(ll) then
            atm1=i; atm2=j
            goto 10
         endif
      End do
      End do
      if(atm1==0) then
         write(6,*) 'Error in NMA_MODULE'
         write(6,*) 'atm1 is no defined!'
         stop
      endif

   10 Continue
      Call NormVec(3,dvec)
      !write(6,'(2i3)') iatm,jatm
      !write(6,'(3f8.4)') dvec

      ! Set iatm,jatm,katm
      iatm=1; jatm=2
      xl=x1(:,jatm)-x1(:,iatm)

      Do k=3,Nat
         katm=k
         yl=x1(:,katm)-x1(:,iatm)
         Call OvlpVec(3,xl,yl,aa)
         if(abs(aa)>1.D-08) exit
      End do
      !write(6,'(3i3)') iatm,jatm,katm
      Call getLmatrix(x1,L1)

  End subroutine

  Subroutine getLmatrix(xx,L0)

    Implicit None

    Real(8) :: xx(3,Nat),L0(3,3),xl(3),yl(3),zl(3),aa

      xl=xx(:,jatm)-xx(:,iatm)
      Call NormVec(3,xl)

      yl=xx(:,katm)-xx(:,iatm)
      Call OvlpVec(3,xl,yl,aa)
      yl=yl - aa*xl
      Call NormVec(3,yl)

      !write(6,'(3i3)') iatm,jatm,katm
      Call vec_product(xl,yl,zl)

      L0(1,:)=xl
      L0(2,:)=yl
      L0(3,:)=zl

      !Do i=1,Nat
      !   xl=0.D+00
      !   Do j=1,3
      !   Do k=1,3
      !      xl(j)=xl(j) + L1(j,k)*xx(k,i)
      !   End do
      !   End do
      !   write(6,'(3x,3f12.6)') xl
      !End do

  End subroutine

  Subroutine getCT()

    Implicit None

    Integer :: i,j
    Real(8) :: sumw

      Allocate(CT(Nat3,3))
      sumw=0.D+00
      Do i=1,Nat
         sumw=sumw + Zmass(i)
      End do
      sumw=sqrt(sumw)

      CT=0.D+00
      Do i=1,Nat
      Do j=1,3
         CT(3*(i-1)+j,j)=sqrt(Zmass(i))/sumw
      End do
      End do

  End subroutine

  Subroutine getCR()

    Implicit None

    Integer :: i,j,k,nR,idx(2)
    Real(8) :: ams,aa,bb

      nR=Nat3-Nfree-3
      if(nR/=2) then
         Allocate(CR(Nat3,3))
         CR=0.D+00
         rotvec=.true.
         linear=0
         Do i=1,3
            Do j=1,Nat
               ams=sqrt(Zmass(j))
               Select case(i)
               case(1)
                  CR(3*(j-1)+1,i)=0.D+00
                  CR(3*(j-1)+2,i)=-x1(3,j)*ams
                  CR(3*(j-1)+3,i)=x1(2,j)*ams
               case(2)
                  CR(3*(j-1)+1,i)=x1(3,j)*ams
                  CR(3*(j-1)+2,i)=0.D+00
                  CR(3*(j-1)+3,i)=-x1(1,j)*ams
               case(3)
                  CR(3*(j-1)+1,i)=-x1(2,j)*ams
                  CR(3*(j-1)+2,i)=x1(1,j)*ams
                  CR(3*(j-1)+3,i)=0.D+00
               End select
               
            End do

            Call NormVec(Nat3,CR(:,i))

         End do

         Call OvlpVec(Nat3,CR(:,1),CR(:,2),aa)
         CR(:,1)=CR(:,1)-aa*CR(:,2)
         Call NormVec(Nat3,CR(:,1))

         Call OvlpVec(Nat3,CR(:,1),CR(:,3),aa)
         Call OvlpVec(Nat3,CR(:,2),CR(:,3),bb)
         CR(:,3)=CR(:,3)-aa*CR(:,1)-bb*CR(:,2)
         Call NormVec(Nat3,CR(:,3))

      else
         !rotvec=.false.
         Allocate(CR(Nat3,2))
         CR=0.D+00
         rotvec=.true.

         Do i=1,3
            aa=0.D+00
            Do j=1,Nat
               aa=aa+abs(x1(i,j))
            End do
            if(aa>1.D-04) then
               Select case(i)
               case(1)
                  linear=1
                  idx(1)=2; idx(2)=3
               case(2)
                  linear=2
                  idx(1)=1; idx(2)=3
               case(3)
                  linear=3
                  idx(1)=1; idx(2)=2
               End select
            endif
         End do

         Do i=1,2
            Do j=1,Nat
               ams=sqrt(Zmass(j))
               Select case(idx(i))
               case(1)
                  CR(3*(j-1)+1,i)=0.D+00
                  CR(3*(j-1)+2,i)=-x1(3,j)*ams
                  CR(3*(j-1)+3,i)=x1(2,j)*ams
               case(2)
                  CR(3*(j-1)+1,i)=x1(3,j)*ams
                  CR(3*(j-1)+2,i)=0.D+00
                  CR(3*(j-1)+3,i)=-x1(1,j)*ams
               case(3)
                  CR(3*(j-1)+1,i)=-x1(2,j)*ams
                  CR(3*(j-1)+2,i)=x1(1,j)*ams
                  CR(3*(j-1)+3,i)=0.D+00
               End select
               
            End do

            Call NormVec(Nat3,CR(:,i))

         End do

         Call OvlpVec(Nat3,CR(:,1),CR(:,2),aa)
         CR(:,1)=CR(:,1)-aa*CR(:,2)
         Call NormVec(Nat3,CR(:,1))

      endif
!
  End subroutine

  Subroutine OvlpVec(N,C1,C2,aa)
    Implicit None
    Integer :: N,i,j,k
    Real(8) :: aa,C1(N),C2(N)

       aa=0.D+00
       Do i=1,N
          aa=aa + C1(i)*C2(i)
       End do

  End subroutine

  Subroutine NormVec(N,Cc)
    Implicit None
    Integer :: N,i
    Real(8) :: aa,Cc(N)

       aa=0.D+00
       Do i=1,N
          aa=aa + Cc(i)*Cc(i)
       End do
       Cc=Cc/sqrt(aa)

  End subroutine

!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
End Module
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! Z(Nat)        :: mass
! x(3,Nat)      :: geometry
! L(Nat3,Nfree) :: Normal displacement vector
!
  SUBROUTINE nma_Construct(N,Nf,Z,x,L)

  USE nma_private

  Implicit None
!
    Integer, intent(in):: N,Nf
    Real(8), dimension(N) :: Z
    Real(8), dimension(3,N) :: x
    Real(8), dimension(N*3,Nf) :: L

!
      Nat=N
      Nat3=N*3
      Nfree=Nf
!
!     if(allocated(Zmass)) return
      Allocate(Zmass(Nat),x0(3,Nat),CL(Nat3,Nfree))
 
      Zmass=Z
      x0=x
      CL=L
      setup=.true. 

      Call commonSetting()
      Call getCT()
      Call getCR()

  End SUBROUTINE nma_Construct
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  SUBROUTINE nma_Destruct(io)

  USE nma_private

  Implicit None
!
     Integer :: io

       Deallocate(Zmass,x0,x1,CL,CT)
       if(allocated(CR)) Deallocate(CR)
       if(allocated(Freq)) Deallocate(Freq)
       setup=.false.

       write(io,100)
   100 Format(/,'(  FINALIZE NMA MODULE  )',/)

       return

!
  End SUBROUTINE nma_Destruct
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  SUBROUTINE nma_Print(io)

  USE Constants_mod
  USE nma_private

  Implicit None
 
    Integer :: io,i,nm

      if(.NOT.setup) Call nma_Error(1)

      Write(io,'(''(  ENTER NMA MODULE  )'',//)')

      Write(io,'(6x,''o ATOMIC MASS'')')
      Write(io,'(6x,5f12.4)') Zmass/elmass
      Write(io,*)
      Write(io,'(6x,''o REFERENCE GEOMETRY'')')
      Write(io,'(6x,3f12.6)') x0
      Write(io,*)
      Write(io,'(6x,''o NORMAL DISPLACEMENT VECTOR'')')
      if(allocated(Freq)) then
         Do i=1,Nfree
            Write(io,'(6x,''  FREQ = '',f8.2)') Freq(i)
            Write(io,'(6x,3f12.6)') CL(:,i)
            Write(io,*)
         End do
      else
         Do i=1,Nfree
            Write(io,'(6x,3f12.6)') CL(:,i)
            Write(io,*)
         End do
      endif
      Write(io,*)
      Write(io,'(6x,''o TRANSLATIONAL VECTOR'')')
      Do i=1,3
         Write(io,'(6x,3f12.6)') CT(:,i)
         Write(io,*)
      End do
      if(rotvec) then
         nm=Nat3-Nfree-3
         if(nm/=2) nm=3
         Write(io,*)
         Write(io,'(6x,''o ROTATIONAL VECTOR'')')
         Do i=1,nm
            Write(io,'(6x,3f12.6)') CR(:,i)
            Write(io,*)
         End do
      endif

      Write(io,'(''(  SETUP OF NMA COMPLETED  )'',//)')

  End SUBROUTINE nma_Print
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  SUBROUTINE nma_ChkCL(Ierr)

  USE nma_private

  Implicit None

    Integer, intent(in):: Ierr

    Integer :: i,j,k
    Real(8):: x,tmp,prcn, err

      if(.NOT.setup) Call nma_Error(1)
!
!
! ===== Check : precision criteria is 'prcn' = 1.d-05 =====
!
      prcn=1.D-05
      err =0.D+00
      Write(IERR,*)
      Write(IERR,*) '====( NMA_CHKCL::  CHECKING THE PRECISION OF CL )===='

      Do i=1,Nfree
         Do j=1,i

            tmp=0.D+00
            Do k=1,Nat3
               tmp = tmp + CL(k,j)*CL(k,i)
            End do

            if(i==j) then
               x=abs(tmp-1.D+00)
               if(x>prcn) then
                  Write(Ierr,*) '== WARNING == NORMAL DISPLACEMENT VECTOR IS NOT NORMALIZED'
                  Write(Ierr,'(3x,''|L('',i2,'')| = '',f8.6)') i,tmp 
               endif
            else
               x=abs(tmp)
               if(x>prcn) then
                  Write(Ierr,*) '== WARNING == NORMAL DISPLACEMENT VECTOR IS NOT ORTHOGONAL'
                  Write(Ierr,'(3x,''L('',i2,'')*L('',i2,'') = '',f8.6)') i,j,tmp 
               endif
            endif
            if(x>err) err = x

         End do
      End do

      Do i=1,Nfree
         Do j=1,3

            tmp=0.D+00
            Do k=1,Nat3
               tmp = tmp + CT(k,j)*CL(k,i)
            End do

            x=abs(tmp)
            if(x>prcn) then
               Write(Ierr,*) '== WARNING == NORMAL DISPLACEMENT VECTOR IS NOT ORTHOGONAL TO TRANSLATIONAL VECTOR'
               Write(Ierr,'(3x,''L('',i2,'')*T('',i2,'') = '',f8.6)') i,j,tmp 
            endif
            if(x>err) err = x

         End do
      End do

      if(rotvec) then
         Do i=1,Nfree
            Do j=1,3
  
               tmp=0.D+00
               Do k=1,Nat3
                  tmp = tmp + CR(k,j)*CL(k,i)
               End do
  
               x=abs(tmp)
               if(x>prcn) then
                  Write(Ierr,*) '== WARNING == NORMAL DISPLACEMENT VECTOR IS NOT ORTHOGONAL TO ROTATIONAL VECTOR'
                  Write(Ierr,'(3x,''L('',i2,'')*R('',i2,'') = '',f8.6)') i,j,tmp 
               endif
               if(x>err) err = x
  
            End do
         End do
      endif
!
!
      Write(Ierr,*)
      if(err<1.D-04) then
         Write(Ierr,100) err
      else
         Write(Ierr,200) 
      endif
      Write(Ierr,*) '====( NMA_CHKCL::  END )===='
      Write(IERR,*)
!
      return

  100 Format(' MAX ERRROR IS ',e10.3,' :    [PASSED]',/)
  200 Format(' MAX ERRROR IS LARGER THAN 1.D-04:    [ERROR]  ',/)
!
!
  END SUBROUTINE
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! x :  Geometry in Cartesian coordinate (Bohr)
! q :  Geometry in Normal coordinate (Bohr emu1/2)
!
  SUBROUTINE nma_x2q(x,q)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k

    Real(8):: x(3,Nat),q(Nfree)
    Real(8):: xag(3),L(3,3)

       Call nma_x2qtL(x,xag,L,q)

  END SUBROUTINE nma_x2q
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! Input
!   x(3,Nat) :  Geometry in Cartesian coordinate (Bohr)
! Output
!   xag(3)   :  C.O.M. in Cartesian coordinate (Bohr)
!   L2(3,3)  :  Transformation from lab frame to molecular frame
!                   x1(j,i) = Sum_k L2(j,k) x(k,i)
!   q(Nfree) :  Geometry in Normal coordinate (Bohr emu1/2)
!
  SUBROUTINE nma_x2qtL(x,xag,L2,q)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k

    Real(8):: x(3,Nat),q(Nfree),xag(3),L2(3,3)

    Real(8) :: xa(3,Nat),qa(3),q1(3),q2(3),rot(3,3),th(3), &
               xb(3,Nat),Jac(3,3),J1(3,3),aa,dvc(3)
    Integer :: iter,ipiv(3),info,idx(2)
    Integer, parameter :: nsiz=3
    Real(8), parameter :: conv_th=1.D-08
    Logical :: conv
!
      if(.NOT.setup) Call nma_Error(1)

      xa=x
      Call CM(Nat,Zmass,xa,xag)
      !write(6,*) '------------------'
      !write(6,*) '** Input coordinates in COM'
      !write(6,'(3x,3f12.6)') xa
      !write(6,*)

!
      if(linear/=0) then

      Select Case(linear)
      Case(1)
         idx(1)=2; idx(2)=3
      Case(2)
         idx(1)=1; idx(2)=3
      Case(3)
         idx(1)=1; idx(2)=2
      End Select

      Call trans(0,xa,qa)
      ! write(6,*) '** Initial qrot'
      ! write(6,'(3x,3f12.6)') qa
      Call trans(1,xa,q)
      ! write(6,*) '** Initial qvib'
      ! write(6,'(3x,3f12.6)') q
      xb=xa

      aa=0.D+00
      Do i=1,3
         aa=aa + abs(qa(i))
      End do
      if(aa/3.D+00 < conv_th) then
         conv=.true.
      else
         !write(6,*) '** qrot,theta'
         conv=.false.
         theta=0.2D+00
      endif

      iter=0
      Do while(.not.conv)
         iter=iter+1
         th=theta
         Do i=1,2
            !write(6,'(i4)') i
            th(idx(i))=theta(idx(i))+dth
            Call get_RotMat(0,rot,th)
            Call rotate(xa,xb,rot)
            Call trans(0,xb,q1)
            !write(6,'(3f12.6)') q1
  
            th(idx(i))=theta(idx(i))-dth
            Call get_RotMat(0,rot,th)
            Call rotate(xa,xb,rot)
            Call trans(0,xb,q2)
            !write(6,'(3f12.6)') q2
  
            Jac(:,i)=(q1(1:3)-q2(1:3))/dth*0.5D+00
  
            th(idx(i))=theta(idx(i))
         End do
         ! write(6,*) '** Jacobian'
         ! write(6,'(3f12.6)') Jac
  
         q1=qa
         J1=Jac
         Call dgesv(2,1,J1(1:2,1:2),2,ipiv,q1(1:2),2,info)
         if(info==0) then
            theta(1:2)=theta(1:2)-q1(1:2)
         else
            !Error in dgesv
            ipiv=0
            Do i=1,3
               k=0
               Do j=1,3
                  if(abs(Jac(i,j))>1.D-06) then
                     ipiv(i)=j
                     k=k+1
                  endif
               End do
               if(k/=1) ipiv(i)=0
            End do
            Write(6,'(3i4)') ipiv
            if(abs(qa(1))>1.D-04 .and. ipiv(1)/=0) then
               theta(ipiv(1))=theta(ipiv(1))-qa(1)/Jac(1,ipiv(1))
            elseif(abs(qa(2))>1.D-04 .and. ipiv(2)/=0) then
               theta(ipiv(2))=theta(ipiv(2))-qa(2)/Jac(2,ipiv(2))
            elseif(abs(qa(3))>1.D-04 .and. ipiv(3)/=0) then
               theta(ipiv(3))=theta(ipiv(3))-qa(3)/Jac(3,ipiv(3))
            else
               write(6,*) '** Error in x2q'
               Stop
            endif
         endif

         !write(6,*) '** theta'
         !write(6,'(3f12.6)') theta
  
         Call get_RotMat(0,rot,theta)
         Call rotate(xa,xb,rot)
         Call trans(0,xb,qa)
         !write(6,'(i3,2f12.6,2x,2f8.4)') iter,qa(1:2),theta(1:2)

         aa=0.D+00
         Do i=1,3
            aa=aa + abs(qa(i))
         End do
         if(aa/3.D+00 < conv_th) conv=.true.

      End do
  
      Call trans(1,xb,q)
      ! write(6,*) '** Final qvib'
      ! write(6,'(3x,3f12.6)') q
      ! write(6,*) '** Final Cartesian coord.'
      ! write(6,'(3x,3f12.6)') xb
      ! write(6,*) '** Reference Cartesian coord.'
      ! write(6,'(3x,3f12.6)') x
      ! write(6,*)

      write(6,*) 'Error:: x2qtL for linear molecule is not ready'
      !Check if this is correct
      L2=rot
      Stop

      else
      ! Nonlinear molecule

      Call getLmatrix(xa,rot)
      L2=0.D+00
      Do i=1,3
      Do j=1,3
         Do k=1,3
            L2(j,i)=L2(j,i)+L1(k,j)*rot(k,i)
         End do
      End do
      End do

      Call rotate(xa,xb,L2)

      dvc=xb(:,atm1)-xb(:,atm2)
      aa=0.D+00
      Do i=1,3
         aa=aa + dvc(i)*dvc(i)
      End do
      dvc=dvc/sqrt(aa)
      !write(6,'(3f8.4)') dvc
      !write(6,'(3f8.4)') dvec
      Do i=1,3
         if(dvc(i)*dvec(i)<0.D+00) then
            xb(i,:)=-xb(i,:)
            L2(i,:)=-L2(i,:)
         endif
      End do
      xa=xb
      !write(6,*) '** After guess transformation'
      !write(6,'(3x,3f12.6)') xa
      !write(6,*)

      Call trans(0,xa,qa)
      !write(6,*) '** Initial qrot'
      !write(6,'(3x,3f12.6)') qa
      Call trans(1,xa,q)
      !write(6,*) '** Initial qvib'
      !write(6,'(3x,3f12.6)') q

      aa=0.D+00
      Do i=1,3
         aa=aa + abs(qa(i))
      End do
      if(aa/3.D+00 < conv_th) then
         conv=.true.
      else
         !write(6,*) '** qrot,theta'
         conv=.false.
         theta=0.2D+00
      endif

      iter=0
      Do while(.not.conv)
         iter=iter+1
         th=theta
         Do i=1,3
            !write(6,'(i4)') i
            th(i)=theta(i)+dth
            Call get_RotMat(0,rot,th)
            Call rotate(xa,xb,rot)
            Call trans(0,xb,q1)
            !write(6,'(3f12.6)') q1
  
            th(i)=theta(i)-dth
            Call get_RotMat(0,rot,th)
            Call rotate(xa,xb,rot)
            Call trans(0,xb,q2)
            !write(6,'(3f12.6)') q2
  
            Jac(:,i)=(q1(1:3)-q2(1:3))/dth*0.5D+00
  
            th(i)=theta(i)
         End do
         ! write(6,*) '** Jacobian'
         ! write(6,'(3f12.6)') Jac
  
         q1=qa
         J1=Jac
         Call dgesv(nsiz,1,J1,nsiz,ipiv,q1,nsiz,info)
         if(info==0) then
            theta=theta-q1
         else
            !Error in dgesv
            ipiv=0
            Do i=1,3
               k=0
               Do j=1,3
                  if(abs(Jac(i,j))>1.D-06) then
                     ipiv(i)=j
                     k=k+1
                  endif
               End do
               if(k/=1) ipiv(i)=0
            End do
            Write(6,'(3i4)') ipiv
            if(abs(qa(1))>1.D-04 .and. ipiv(1)/=0) then
               theta(ipiv(1))=theta(ipiv(1))-qa(1)/Jac(1,ipiv(1))
            elseif(abs(qa(2))>1.D-04 .and. ipiv(2)/=0) then
               theta(ipiv(2))=theta(ipiv(2))-qa(2)/Jac(2,ipiv(2))
            elseif(abs(qa(3))>1.D-04 .and. ipiv(3)/=0) then
               theta(ipiv(3))=theta(ipiv(3))-qa(3)/Jac(3,ipiv(3))
            else
               write(6,*) '** Error in x2q'
               Stop
            endif
         endif

         !write(6,*) '** theta'
         !write(6,'(3f12.6)') theta
  
         Call get_RotMat(0,rot,theta)
         Call rotate(xa,xb,rot)
         Call trans(0,xb,qa)
         !write(6,'(i3,3f12.6,2x,3f8.4)') iter,qa,theta

         aa=0.D+00
         Do i=1,3
            aa=aa + abs(qa(i))
         End do
         if(aa/3.D+00 < conv_th) conv=.true.

      End do
  
      Call trans(1,xb,q)
      !write(6,*) '** Final qvib'
      !write(6,'(3x,3f12.6)') q
      !write(6,*) '** Final Cartesian coord.'
      !write(6,'(3x,3f12.6)') xb
      !write(6,*) '** Reference Cartesian coord.'
      !write(6,'(3x,3f12.6)') x
      !write(6,*)

      Jac=L2
      L2=0.D+00
      Do i=1,3
      Do j=1,3
         Do k=1,3
            L2(j,i)=L2(j,i) + rot(j,k)*Jac(k,i)
         End do
      End do
      End do
      !xa=x
      !Call CM(Nat,Zmass,xa,xag)
      !Do i=1,Nat
      !   q1=0.D+00
      !   Do j=1,3
      !   Do k=1,3
      !      q1(j)=q1(j)+L2(j,k)*xa(k,i)
      !   End do
      !   End do
      !   write(6,'(3x,3f12.6)') q1
      !End do

      endif
      
    Contains

    Subroutine trans(iopt,xx,qq)

    Integer :: i,j,k,kk,k0,iopt
    Real(8) :: xx(3,Nat),qq(*)
    Real(8) :: AMS,TMP

      if(iopt==0) then
         if(linear/=0) then
            k0=2
         else
            k0=3
         endif
         qq(1:3)=0.D+00
         kk=0
         Do i=1,Nat
            AMS = SQRT(Zmass(i))
            Do j=1,3
               kk=kk+1
               TMP = (xx(j,i)-x1(j,i))*AMS
               Do k=1,k0
                  qq(k) = qq(k)+TMP*CR(kk,k)
               End do
            End do
         End do

      elseif(iopt==-1) then
         qq(1:3)=0.D+00
         kk=0
         Do i=1,Nat
            AMS = SQRT(Zmass(i))
            Do j=1,3
               kk=kk+1
               TMP = (xx(j,i)-x1(j,i))*AMS
               Do k=1,3
                  qq(k) = qq(k)+TMP*CT(kk,k)
               End do
            End do
         End do

      else
         qq(1:Nfree)=0.D+00
         kk=0
         Do i=1,Nat
            AMS = SQRT(Zmass(i))
            Do j=1,3
               kk=kk+1
               TMP = (xx(j,i)-x1(j,i))*AMS
               Do k=1,Nfree
                  qq(k) = qq(k)+TMP*CL(kk,k)
               End do
            End do
         End do

      endif

    End subroutine

    Subroutine rotate(xxa,xxb,rot)

    Integer :: i,j,k
    Real(8) :: xxa(3,Nat),xxb(3,Nat),rot(3,3)

       Do i=1,Nat
          xxb(:,i)=0.D+00
          Do j=1,3
             Do k=1,3
                xxb(j,i)=xxb(j,i) + rot(j,k)*xxa(k,i)
             End do
          End do
       End do

    End subroutine

  END SUBROUTINE nma_x2qtL
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! Output
!   x(3,Nat) :  Geometry in Cartesian coordinate (Bohr)
!
! Input
!   xag(3)   :  C.O.M. in Cartesian coordinate (Bohr)
!   L2(3,3)  :  Transformation from lab frame to molecular frame
!                 x(j,i) = Sum_k L2(k,j) x1(k,i)
!   q(Nfree) :  Geometry in Normal coordinate (Bohr emu1/2)
!
  SUBROUTINE nma_qtL2x(x,xag,L2,q)

  USE nma_private

  Implicit None

    Real(8) :: x(3,Nat),xag(3),L2(3,3),q(Nfree),xa(3)

    Integer :: i,j,k

    Call nma_q2x(x,q)

    Do i=1,Nat
       xa=0.D+00
       Do j=1,3
       Do k=1,3
          xa(j)=xa(j) + L2(k,j)*x(k,i)
       End do
       End do
       x(:,i)=xa+xag
    End do

  END SUBROUTINE nma_qtL2x
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! x :  Geometry in Cartesian coordinate (Bohr)
! q :  Geometry in Normal coordinate (Bohr emu1/2)
!
  SUBROUTINE nma_q2x(x,q)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k,kk
    Real(8)  :: AMS

    Real(8):: x(3,Nat),q(Nfree)
!
    if(.NOT.setup) Call nma_Error(1)
    x=x0
!
    kk=0
    Do i=1,Nat
       AMS = SQRT(Zmass(i))
       Do j=1,3
          kk=kk+1
          Do k=1,Nfree
             x(j,i)=x(j,i) + q(k)*CL(kk,k)/AMS
          End do
       End do
    End do
 
    return

  END SUBROUTINE nma_q2x
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  v: velocity in Cartesian ( Bohr/time )
!  p: velocity/momentum in normal Coordinate ( Bohr emu1/2/time )
!
  SUBROUTINE nma_v2p(v,p)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k,kk
    Real(8)  :: AMS,TMP

    Real(8), intent(in):: v(3,Nat)
    Real(8), intent(out):: p(Nfree)
!
!
    if(.NOT.setup) Call nma_Error(1)
    p = 0.D+00

    kk=0
    Do i=1,Nat
       AMS = SQRT(Zmass(i))
       Do j=1,3
          kk=kk+1
          TMP = v(j,i)*AMS
          Do k=1,Nfree
             p(k) = p(k)+TMP*CL(kk,k)
          End do
       End do
    End do
!
    return
!
  END SUBROUTINE nma_v2p
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  v: velocity in Cartesian ( length/time )
!  p: velocity/momentum in normal Coordinate ( length emu1/2/time )
!
  SUBROUTINE nma_p2v(v,p)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k,kk
    Real(8)  :: AMS

    Real(8), intent(out):: v(3,Nat)
    Real(8), intent(in):: p(Nfree)
!
!
    if(.NOT.setup) Call nma_Error(1)
    v = 0.D+00
!
    kk=0
    Do i=1,Nat
       AMS = SQRT(Zmass(i))
       Do j=1,3
          kk=kk+1
          Do k=1,Nfree
             v(j,i)=v(j,i) + p(k)*CL(kk,k)/AMS
          End do
       End do
    End do
!
    return
!
  END SUBROUTINE nma_p2v
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! g : Gradient in Cartesian coordinate ( energy/length )
! qg: Gradient in Normal coordinate ( energy/length emu1/2 )
!
  SUBROUTINE nma_g2qg(g,qg)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k,kk
    Real(8)  :: AMS,TMP

    Real(8), intent(in):: g(3,Nat)
    Real(8), intent(out):: qg(Nfree)
!
    if(.NOT.setup) Call nma_Error(1)
!
! initialize
    qg=0.D+00
!
    kk=0
    Do i=1,Nat
       AMS = SQRT(Zmass(i))
       Do j=1,3
          kk=kk+1
          TMP = g(j,i)/AMS
          Do k=1,Nfree
             qg(k)= qg(k)+TMP*CL(kk,k)
          End do
       End do
    End do
 
    return
!
  END SUBROUTINE nma_g2qg
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! g : Gradient in Cartesian coordinate ( energy/length )
! qg: Gradient in Normal coordinate ( energy/length emu1/2 )
!
  SUBROUTINE nma_qg2g(g,qg)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k,kk
    Real(8)  :: AMS

    Real(8), intent(out):: g(3,Nat)
    Real(8), intent(in):: qg(Nfree)
!
    if(.NOT.setup) Call nma_Error(1)
!
! initialize
    g=0.D+00
!
    kk=1
    Do i=1,Nat
       AMS=SQRT(ZMASS(i))
       Do j=1,3
          Do k=1,Nfree
             g(j,i)=g(j,i)+qg(k)*CL(kk,k)
          End Do
          g(j,i) = g(j,i)*AMS
          kk=kk+1
       End do
    End do
 
    return
! 
  END SUBROUTINE nma_qg2g
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! h : Hessian in Cartesian coordinate ( energy/length^2 )
! qh: Hessian in Normal coordinate ( energy/length^2(emu) )
!
  SUBROUTINE nma_h2qh(h,qh)

  USE nma_private

  Implicit None
!
    Integer :: i,ii,j,jj,k,kk
    Real(8)  :: AMS

    Real(8), intent(in):: h(Nat3,Nat3)
    Real(8), intent(out):: qh(Nfree,Nfree)
    Real(8), dimension(Nat3,Nfree) :: CL2
!
    if(.NOT.setup) Call nma_Error(1)
!
! initialize
!
    qh = 0.D+00
!
    kk=1
    Do i=1,Nat
       AMS = SQRT(Zmass(i))
       Do j=1,3
          Do k=1,Nfree
            CL2(kk,k) = CL(kk,k)/AMS
          End do
          kk=kk+1
       End do
    End do
!
    Do ii=1,Nfree
       Do jj=1,ii
          Do i=1,Nat3
             Do j=1,Nat3
                qh(jj,ii) = qh(jj,ii)+CL2(i,ii)*h(i,j)*CL2(j,jj)
  
             End do
          End do
          qh(ii,jj) = qh(jj,ii)
       End do
    End do
!
  return
!
!
  END SUBROUTINE nma_h2qh
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  q     : A (unit) vector of 3N-6 dimension in Normal Coordinate
!  rm    : Reduced Mass (emu)
!
!       rm = (|q|/|x|)^2
!           x = M^(-1/2)*CL^(-1)*q
!
  SUBROUTINE nma_getrm(q,rm)

  USE nma_private

  Implicit None
!
    Integer :: i,j,k,l
    Real(8)  :: qq,xx,tmp,AMS

    Real(8), dimension(Nfree), intent(in):: q
    Real(8), intent(out):: rm
 
    Real(8), dimension(Nat3):: x
!
    if(.NOT.setup) Call nma_Error(1)
!
    x=0.D+00
    qq=0.D+00
    qq=Dot_Product(q,q)
!
!   Check 
!
    tmp=ABS(qq-1.D+00)
    if(tmp>=1.D-08) Write(6,100)
!
    l=0
    Do i=1,Nat
       AMS = SQRT(Zmass(i))
       Do j=1,3
          l=l+1
          Do k=1,Nfree
             x(l)=x(l) + q(k)*CL(l,k)/AMS
          End do
       End do
    End do
!
    xx=0.D+00
    xx=Dot_Product(x,x)
!
    rm=qq/xx
!
    return
!
    100 Format(10x,'=== WARNING (GET_RED_MASS) == GIVEN Q IS NOT A UNIT VECTOR')
!
!
  END SUBROUTINE nma_getrm
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

  SUBROUTINE nma_Error(ierr)

    Integer :: ierr

    Write(6,*) '   >(ERROR)> ERROR IN NMA_MODULE.'

    Select case(ierr)
       case(1)
          Write(6,*) '   >(ERROR)> NMA_MODULE IS NOT SETUP.'
          Write(6,*) '   >(ERROR)> CONSTRUCT THE MODULE FIRST!'
    End Select
    Stop

  END SUBROUTINE nma_Error
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Subroutine nma_Setup()
!
!    Implicit None
!
!    Integer :: in,io,ierr,myrank
!    Integer :: Na,Nf,spr_GetNat,spr_GetNfree
!    Real(8) :: rNa,rNf
!    Real(8), allocatable :: x0(:),CL(:,:),Ms(:)
!
!#ifdef MPI
!    include 'mpif.h'
!#endif
!
!    Na=spr_GetNat()
!    Nf=spr_GetNfree()
!
!    rNa=dble(Na); rNf=dble(Nf)
!    Allocate(x0(Na*3),CL(Na*3,Nf),Ms(Na))
!
!    Call spr_Getio(in,io)
!    Call spr_Getxin(x0)
!    Call spr_GetMass(Ms)
!    Call spr_GetL(CL)
!    Call nma_Construct(Na,Nf,Ms,x0,CL)
!#ifdef MPI
!    Call MPI_COMM_RANK(MPI_COMM_WORLD,myrank,ierr)
!    if(myrank==0) Call nma_Print(io)
!#else
!    Call nma_Print(io)
!#endif
!
!    Deallocate(x0,CL,Ms)
!
!  End Subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
