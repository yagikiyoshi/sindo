!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/07/19
!   Copyright 2013
!   Code description by K.Yagi and H.Otaki
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Module OptCoord_mod

   Integer :: maxIter
   Real(8) :: gthresh,ethresh,eta12thresh
   Integer :: Nfree,icff,iscreen

   Real(8), allocatable :: U1(:,:)
   Logical, allocatable :: activeModes(:)
   
   End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine OptCoord_Init(Nf,mxi,eth,gth,icffin,iscr,eta12th)

   USE OptCoord_mod

   Implicit None

   Integer :: Nf,mxi,i,icffin,iscr
   Real(8) :: eth,gth,eta12th

      Nfree=Nf
      maxIter=mxi
      ethresh=eth
      gthresh=gth
      icff=icffin
      iscreen=iscr
      eta12thresh=eta12th

      Allocate(U1(Nfree,Nfree))
      U1=0.0D+00
      Do i=1,Nfree
         U1(i,i)=1.D+00
      End do

      Allocate(activeModes(Nfree))
      Do i=1,Nfree
         activeModes(i)=.true.
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine OptCoord_Finalz()

   USE OptCoord_mod

   Implicit None

      Deallocate(U1,activeModes)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!   Subroutine OptCoord_getEnergy(ene)
!
!   USE OptCoord_mod
!   USE modVSCF!old
!
!   Implicit None
!
!   Integer :: state(Nfree)
!   Real(8) :: ene
!
!      Call Ocvscf_coeff_writeMaVi()
!      state=0
!      Call subVSCF(state,ene)
!
!   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine OptCoord_getEnergy(ene)

   USE OptCoord_mod
   USE Constants_mod

   Implicit None

   Real(8) :: ene, Vscf_getEvscf

   Real(8) :: omega(Nfree)
   Integer :: MR,Vib_getMRforPES
   Integer :: i,nCHO(Nfree)

      Call Ocvscf_coeff_getOmega(omega)
      omega=omega*H2wvn
      Call Vib_setFreq(omega)

      MR=Vib_getMRforPES()
      if(MR==4.and.icff==1) then
         MR=3
      end if

      Call PES_construct_qff(Nfree,MR)
      Call Ocvscf_coeff_writeSindo()

      Call Vib_getnCHO(nCHO)

      Call Modal_construct(Nfree,nCHO)
      Call Modal_open()

      Do i=1,Nfree
         Call Modal_add(i,nCHO(i))
      End do

      Call Modal_close()

      Call Vscf_construct()
      Call Vscf_main()
      ene = Vscf_getEvscf(0)
      Call Vscf_destruct()

      Call PES_destruct()
      Call PES_qff_destruct()
      Call Modal_destruct()

   End subroutine

!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Coordinate optimization main routine
!
   Subroutine OptCoord_main(ier,pp)

   USE OptCoord_mod
   USE Constants_mod

   Implicit None

   Integer :: ier

   Integer :: i,j,iter,idx(2),istat,n
   Logical :: conv,screen
   Integer :: pp,MM
   Real(8) :: T0,dt,tt,ff,currentEne,delE
   Real(8) :: dd,ene,e1,e2,grad,hess,up(Nfree,Nfree)
   Real(8), allocatable :: fn(:),omega(:)
   Real(8), parameter :: alpha=2.0D+01
   Real(8) :: PI
   Real(8) :: eta12

      write(Iout,'(3x,''> START OF OPTIMIZA'',/)')

      ! Parameters for Fourier fit
      MM=2*pp+1
      Allocate(fn(-pp:pp))

      PI=Acos(-1.0D+00)
      T0=PI/2.D+00
      dt=T0/MM

      Call fse_init(pp,T0)

      ! Step size of numerical differenciation
      dd=1.0D+00/180.D+00*PI

      ! Initialize transformation matrix
      U1=0.D+00
      Do i=1,Nfree
         U1(i,i)=1.D+00
      End do

      ! Get the initial energy
      Call OptCoord_getEnergy(currentEne)

      Allocate(omega(Nfree))

      conv=.false.
      if (iscreen>0) then
         screen=.true.
      else
         screen=.false.
      end if

      Do iter=1,maxIter
         write(Iout,'(3x,''o ITERATION: '',i4)') iter
         write(Iout,'(6x,''PAIR     NEWANGLE    E0                  ENEW                DELTA-E             GRADIENT'')')

         Call Ocvscf_coeff_getOmega(omega)
         delE=0.D+00

         Do i=1,Nfree
            if(.not. activeModes(i)) cycle
         Do j=1,i-1
            if(.not. activeModes(j)) cycle
            idx(1)=j
            idx(2)=i

            if(screen) then
               Call Ocvscf_coeff_eta12(i,j,eta12)
               if( eta12*H2wvn > eta12thresh ) cycle
            endif

            ! Wide search - Fourier Fit -
            istat=0
            Do n=-pp,pp
               !Call getUp(Nfree,idx,dt*dble(n),up)
               !Call Ocvscf_coeff_transC2D(up)
               Call Ocvscf_coeff_rotate(idx,dt*dble(n))
               Call OptCoord_getEnergy(fn(n))
               !write(6,'(i4,f12.4)') n,fn(n)*H2wvn
               if(fn(n)<0.D+00) then
                  istat=-1
                  exit
               endif
            End do
            if(istat/=0) then
               write(Iout,*) 'VSCF UNCONVERGED.. SKIP THE FOURIER FIT.'
               tt=0.D+00
               !cycle
            !endif
            else
               Call fse_setFunc(fn)
               Call fse_getMinVal(gthresh,tt,ff)

               if(tt>T0/2.D+00) tt=tt-T0
               if(tt<-T0/2.D+00) tt=tt+T0
            endif

  
            ! Local search - Newton minimization -
            istat=0
            Do while(.true.)
               Call Ocvscf_coeff_rotate(idx,tt)
               call OptCoord_getEnergy(ene)
               if(ene<0.D+00) then
                  istat=-1
                  exit
               endif
  
               Call Ocvscf_coeff_rotate(idx,tt+dd)
               call OptCoord_getEnergy(e1)
               if(e1<0.D+00) then
                  istat=-1
                  exit
               endif
      
               Call Ocvscf_coeff_rotate(idx,tt-dd)
               Call OptCoord_getEnergy(e2)
               if(e2<0.D+00) then
                  istat=-1
                  exit
               endif
      
               grad=(e1-e2)/dd*0.5D+00
               if(abs(grad)<gthresh) exit
  
               hess=(e1+e2-2.0D+00*ene)/dd/dd
               if(hess>0.D+00) then
                  tt = tt-grad/hess
               else
                  write(Iout,*) 'HESS IS NEGATIVE'
                  !tt = tt-grad*alpha
                  !write(6,'(e15.6,f12.4)') grad,tt
                  !call flush(6)
                  istat=-1
                  exit
               endif
      
               !write(6,*) 'debug-1'
      
            End do
            if(istat/=0) then
               write(Iout,*) 'VSCF UNCONVERGED.. SKIP THIS MODE PAIR.'
               cycle
            endif
  
            Call Ocvscf_coeff_rotate(idx,tt)
            Call Ocvscf_coeff_copyD2C()
            Call calcAUp(Nfree,idx,tt,U1)
  
            write(6,'(3x,2i4,f12.4,2f20.10,2e20.10)') idx,tt/PI*180.0D+00, &
                  currentEne*H2wvn,ene*H2wvn,(ene-currentEne)*H2wvn,grad
            Call flush(6)

            delE=delE + abs(ene-currentEne)*H2wvn
            currentEne=ene
  
         End do
         End do

         write(Iout,*) 
         write(Iout,'(10x,''DELTA E = '',f20.10,/)') delE

         if(delE<ethresh) then
            conv=.true.
            exit
         endif
         if(iscreen==2 .and. delE<1.0D+00) then
            screen=.false.
         endif

      End do
      !Call Ocvscf_coeff_getOmega(omega)
      !write(Iout,'(f15.6)') omega*H2wvn
      Deallocate(omega)


      if(conv) then
         write(Iout,'(3x,''> OPTIMIZATION CONVERGED!'')')
      else
         write(Iout,'(3x,''> OPTIMIZATION UN-CONVERGED!'')')
         ier=-1
      endif

      write(Iout,'(3x,''> END OF OPTIMIZA'',//)')

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine OptCoord_printU1(order,fname)

   USE OptCoord_mod

   Implicit None

   Integer :: order,i,j
   Real(8) :: U2(Nfree*Nfree)
   Real(8) :: scfthresh,Vscf_getEthresh
   Integer :: scfmaxiter,Vscf_getMaxIteration
   Character(80) :: fname

      U2=Reshape(U1,(/Nfree*Nfree/))
      scfthresh = Vscf_getEthresh()
      scfmaxiter = Vscf_getMaxIteration()

      Open(10,file=fname,status='unknown')
      if(iscreen == 0) then
         write(10,'('' VSCFeth='',e8.2,'',VSCFmaxIter='',i4,'',Eth='',e8.2, &
                    '',Gth='',e8.2,'',Fse='',i4)') &
                    scfthresh,scfmaxiter,ethresh,gthresh,order
      else
         write(10,'('' VSCFeth='',e8.2,'',VSCFmaxIter='',i4,'',Eth='',e8.2, &
                    '',Gth='',e8.2,'',Fse='',i4,'',eta12th='',f6.2)') &
                    scfthresh,scfmaxiter,ethresh,gthresh,order,eta12thresh
      endif
      write(10,*) 'Transformation matrix'
      write(10,'(i6)') Nfree*Nfree
      Do i=1,Nfree*Nfree-1
         if(mod(i,5)/=0) then
            write(10,'(e20.12,'','',$)') U2(i)
         else
            write(10,'(e20.12)') U2(i)
         endif
      End do
      write(10,'(e20.12)') U2(Nfree*Nfree)
      Close(10)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine OptCoord_profile()

   USE OptCoord_mod

   Implicit None

   Integer :: i,j,n,state(Nfree),idx(2)
   Integer, parameter :: nn=13
   Real(8) :: e0(nn),ang,dap,ap,up(Nfree,Nfree)
   Real(8), parameter :: H2wvn=2.194746E+05
   Real(8) :: PI

      PI=Acos(-1.0D+00)
      ang=45.0D+00
      dap=ang*2.D+00/(nn-1)
      write(6,'(8x,14f12.4)') (-ang+(n-1)*dap,n=1,nn)
      !write(6,'(8x,14f12.4)') ((i-1)*dap,i=1,nn+1)

      ang=ang/180.D+00*PI
      dap=dap/180.D+00*PI
      state=0

      Call Ocvscf_coeff_copyD2C()

      Do i=1,Nfree
      Do j=1,i-1
         idx(1)=j
         idx(2)=i
         write(6,'(2i4,$)') idx

         Do n=1,nn
            ap=-ang+(n-1)*dap
            !Call getUp(Nfree,idx,ap,up)
            !Call Ocvscf_coeff_transC2D(up)
            Call Ocvscf_coeff_rotate(idx,ap)
            Call OptCoord_getEnergy(e0(n))
         End do
         e0=e0*H2wvn
         write(6,'(14f12.4)') e0
         Call Ocvscf_coeff_copyC2D()

      End do
      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine OptCoord_setInactiveMode(mode)

   USE OptCoord_mod

   Implicit None

   Integer :: mode

      activeModes(mode)=.false.

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    pp(1)<pp(2)
!
   Subroutine getUp(Nfree,pp,ap,AA)

   Implicit None

   Integer :: Nfree,pp(2),i
   Real(8) :: AA(Nfree,Nfree),ap

      AA=0.D+00
      Do i=1,Nfree
         AA(i,i)=1.D+00
      End do
      AA(pp(1),pp(1)) = cos(ap)
      AA(pp(2),pp(2)) = cos(ap)
      AA(pp(2),pp(1)) = sin(ap)
      AA(pp(1),pp(2)) =-sin(ap)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!    pp(1)<pp(2)
!
   Subroutine calcAUp(Nfree,pp,ap,AA)

   Implicit None

   Integer :: Nfree,pp(2),i,j,n
   Real(8) :: AA(Nfree,Nfree),ap,bb(Nfree,2)

      Do n=1,Nfree
         bb(n,1)= AA(n,pp(1))*cos(ap) + AA(n,pp(2))*sin(ap)
         bb(n,2)=-AA(n,pp(1))*sin(ap) + AA(n,pp(2))*cos(ap)
      End do

      AA(:,pp(1)) = bb(:,1)
      AA(:,pp(2)) = bb(:,2)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
