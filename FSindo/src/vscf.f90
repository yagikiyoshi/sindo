!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/07/31
!   Copyright 2014 
!   Code description by K.Yagi and H.Otaki
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
 
   Module Vscf_mod

   USE Vib_mod, only : Nfree, nCHO, maxCHO

   ! -- (STATE parameters) ------------------------------------------------- 
   !     Nst       :: Index of current vibrational state
   !     lb(Nfree) :: Label of the current vibrational state

   Integer :: Nst
   Integer, dimension(:), allocatable:: lb

   ! -- (VSCF parameters) -------------------------------------------------- 
   !     restart      :: Restart option
   !     Maxitr       :: Max cycle of VSCF iteration
   !     Ethresh      :: Threshold energy for convergence criteria / in cm-1
   !     Etot(Nstate) :: Total energy

   Logical :: state_specific
   Logical :: restart
   Integer :: Maxitr
   Real(8) :: Ethresh
   Real(8), allocatable :: Etot(:)

   ! -- (VSCF one-mode variables) ------------------------------------------
   !     ptTmat(Nfree) :: Pointer to Tmat
   !     Tmat          :: Kinetic energy matrix
   !     ptVmf(maxCHO,Nfree) :: Pointer to the Vmf
   !     Vmf                 :: Mean-Field potential

   Integer, dimension(:), allocatable :: ptTmat
   Real(8), dimension(:), allocatable :: Tmat
   Integer, dimension(:,:), allocatable :: ptVmf0
   Real(8), dimension(:), allocatable   :: Vmf0

   ! -- Silent mode
   Logical :: silent=.false.

   ! -- (oc-VSCF parameters) -------------------------------------------------- 
   !     icff :: (default=0)
   Integer :: icff

   Logical :: vscf_debug = .false.
   Integer :: debugFile 

   End module 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_run()

      Call Vscf_construct()
      Call Vscf_main()
      Call Vscf_destruct()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_construct()

   USE Constants_mod
   USE Vscf_mod

   Implicit None

   Logical :: lvci,Vib_getlvci
   Logical :: lvpt,Vib_getlvpt
   Logical :: lvqdpt,Vib_getlvqdpt
   Logical :: locvscf,Vib_getlocvscf
   Integer :: i,i1,i2,j,k,kk,l

   Integer :: nn,Target_getNstate

   Real(8) :: msz

   Logical :: ex

   Namelist /vscf/state_specific,restart,Maxitr,Ethresh

      ! >> Read input 

      ! --- default ---
      ! - The number of state(s) to be calculated and quantum numbers of 
      ! - each mode.  Default is only the ground state. 
      state_specific=.false.
      restart=.false.
      Maxitr=10
      Ethresh=1.0D-03

      Rewind(inp)
      Read(inp,vscf,end=10)
   10 Continue

      ! --+----2----+----3----+----4----+----5----+----6----+----7----+----80
      ! >> Output options

      lvci = Vib_getlvci()
      lvpt = Vib_getlvpt()
      lvqdpt = Vib_getlvqdpt()
      locvscf = Vib_getlocvscf()
      if(lvpt .or. lvci .or. lvqdpt .or. locvscf) then
         if(Ethresh > 1.D-05) Ethresh=1.D-05
      endif

      if(.not. silent) then
         write(iout,100)
         write(iout,110)
         write(iout,120) state_specific,restart,Maxitr,Ethresh
      endif
  100 Format(/,'(  ENTER VSCF MODULE  )',/)
  110 Format(2x,'---  VIBRATIONAL SELF-CONSISTENT FIELD CALCULATIONS   ---',/)
  120 Format(3x,'>> SCF OPTIONS',/, &
      7x,'STATE SPECIFIC :',l9,/, &
      7x,'       RESTART :',l9,/, &
      7x,' MAX ITERATION :',i9,/, &
      7x,'  CONV. THRESH :',e9.2,/)

      ! --+----2----+----3----+----4----+----5----+----6----+----7----+----80
      ! >> Data files

      if(.not. silent) write(iout,130)
  130 Format(3x,'>> FILES',/, &
             7x,'WFN READ/WRITE FILE : vscf_xxx.wfn',/)

      ! --+----2----+----3----+----4----+----5----+----6----+----7----+----80
      ! >> Memory allocation

      Call Mem_alloc(-1,i,'I',Nfree)
      Allocate(ptTmat(Nfree))
      ptTmat(1)=0
      Do i=2,Nfree
         ptTmat(i)=ptTmat(i-1) + nCHO(i-1)*nCHO(i-1)
      End do
      j=ptTmat(Nfree) + nCHO(Nfree)*nCHO(Nfree)
      Call Mem_alloc(-1,i,'D',j)
      Allocate(Tmat(j))

!      Do i=1,Nfree
!         Call KE_genTmat(i,nCHO(i),Tmat(ptTmat(i)+1:ptTmat(i)+nCHO(i)*nCHO(i)))
!      End do

      Call Mem_alloc(-1,i,'I',maxCHO*Nfree)
      Allocate(ptVmf0(maxCHO,Nfree))

      i1=0
      Do i=1,Nfree
         Do j=1,nCHO(i)
            Call Modal_isGrid(i,j,ex)
            if(.not. ex) then
               ptVmf0(j,i)=-1

            else
               ptVmf0(j,i)=i1
               i1=i1+j

            endif
         End do
      End do
      !write(6,'(3i4)') i1

      Call Mem_alloc(-1,i,'D',i1)
      allocate(Vmf0(i1))

      nn = Target_getNstate()
      Call Mem_alloc(1,i,'D',nn+1)
      Allocate(Etot(0:nn))

!     debug_option
      if(vscf_debug) then
        debugFile=200
        call file_indicator(debugFile,debugFile)
        Open(debugFile,file='vscf_debug',status='unknown')
      endif

   End subroutine 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Vscf_destruct()

   USE Constants_mod
   USE Vscf_mod

   Implicit None

      Call Mem_dealloc('I',size(ptTmat))
      Call Mem_dealloc('D',size(Tmat))
      Call Mem_dealloc('I',size(ptVmf0))
      Call Mem_dealloc('D',size(Vmf0))
      Deallocate(ptTmat,Tmat,ptVmf0,Vmf0)

      Call Mem_dealloc('D',size(Etot))
      Deallocate(Etot)

      if(.not. silent) Write(iout,100)
  100 Format(/,'(  FINALIZE VSCF MODULE  )',/)

!     debug_option
      if(vscf_debug) Close(debugFile)

   End subroutine 

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_main()

   USE Constants_mod
   USE Vscf_mod

   Implicit None

   Integer :: i,j,jj,k,kk,l,ll,n,nn
   Integer :: ierr

   Integer :: Target_getNstate
   Integer :: Nstate
   Integer :: maxCup,cup,Target_getMaxCup
   Integer, allocatable :: mm(:),vv(:)

   Integer :: itr,nGj,lbj
   Real(8) :: Enew,Eold,delE,E0,vscf_totE,ovlp,tmp
   Real(8), dimension(:), allocatable :: Ene,Cw
   Real(8), dimension(:,:), allocatable :: C,C1,Hmat,Vmat
   Logical :: cnv

   Integer :: swt
   Character(12) :: vscfFile

      swt = 0

      Do i=1,Nfree
         if(nCHO(i)==0) cycle
         Call KE_genTmat(i,nCHO(i),Tmat(ptTmat(i)+1:ptTmat(i)+nCHO(i)*nCHO(i)))
      End do

      nn = Target_getNstate()
      if(state_specific) then 
         Nstate = nn
      else
         Nstate = 0
      endif
      Call Mem_alloc(-1,i,'I',Nfree)
      allocate(lb(Nfree))

      Do i=0,Nstate

         if(i /=0 ) then
            Call Target_getLabel(i,Nfree,lb)
         else
            lb=0
         endif

         Nst=i
         if(.not. silent) then
            if(i==0) then
               write(iout,110)
               if(vscf_debug) write(debugFile,110)
            else
               maxCup = Target_getMaxCup() 
               Allocate(mm(maxCup),vv(maxCup))
               Call Target_getConf(i,cup,mm,vv)
               write(iout,111) i,(mm(j),vv(j),j=1,cup)
               write(iout,*)
               if(vscf_debug) then 
                  write(debugFile,111) i,(mm(j),vv(j),j=1,cup)
                  write(debugFile,*)
               endif
               Deallocate(mm,vv)
            endif
         endif
     110 Format(3x,'>> STATE 000',':   ZERO-POINT STATE',/)
     111 Format(3x,'>> STATE ',i3.3,': ',10(i3,'_',i1,2x))

         if(restart) then
            Call Vscf_getFilename(Nst,vscfFile)
            Call Modal_readVSCF(j,vscfFile)
            if(.not. silent) then
               if(j==0) then
                  write(iout,113)
               else
                  if(i==1 .or. .not. cnv) then 
                    write(iout,114)
                  else
                    write(iout,115)
                  endif
               endif
            endif
               
         else
            if(.not. silent) then
               if(i==0 .or. .not. cnv) then 
                 write(iout,114)
               else
                 write(iout,115)
               endif
            endif

         endif
     113 Format(7x,'o RESTORE WFN FROM A PUNCH FILE',/)
     114 Format(7x,'o INITIAL GUESS FROM (CONTRACTED) HARMONIC OSCILLATOR ',/)
     115 Format(7x,'o USING THE PREVIOUS VSCF STATE AS INITAL WFN',/)

         ! Start of VSCF LOOP
         cnv=.False.
         Eold=0.D+00
         itr=0
         if(.not. silent) Write(iout,120)
     120 Format(3x,'-- (ITERATION) ',8('-'),' (EOLD) ',8('-'), &
                   ' (ENEW) ',5('-'),' (DELTA E) --')

         ! Initial energy
         Etot(i)=vscf_totE()
         Eold=Etot(i)*H2wvn

         Do while(.not. cnv) 

            itr=itr+1
            if(itr >= Maxitr+1) then 
               write(iout,1000) 
               1000 Format(/,7x,' o MAX NUMBER OF ITERATION REACHED ...', &
                                ' EXIT THE LOOP. ',/, &
                             7x,' o CURRENT VSCF STATE IS UNCONVERGED.')
               exit
            endif
            if(itr > 1 .and. abs(delE)/Eold > 0.4D+00) then
               write(iout,1001)
               1001 Format(/,7x,' o VSCF SEEMS UNCONVERGING ...', &
                                ' EXIT THE LOOP. ',/, &
                             7x,' o CURRENT VSCF STATE IS UNCONVERGED.')
               exit
            endif

            if(vscf_debug) write(debugFile,'(3x,''ITERATION='',i4)') itr

            Do j=1,Nfree

               if(vscf_debug) write(debugFile,'(''MODE='',i4)') j

               if(nCHO(j)==0) cycle

               nGj=nCHO(j)
               !dbg write(io,'(7x,''MODE='',i4,'', GRID='',i4)') j,nGj

               Call Mem_alloc(1,ierr,'D',nGj*nGj*3+nGj)
               if(ierr<0) return 
               Allocate(C(nGj,nGj),Hmat(nGj,nGj),Vmat(nGj,nGj),Ene(nGj))

               Ene=0.D+00
               C=0.D+00
               Hmat=0.D+00
               Vmat=0.D+00

               Call Vscf_getKinmat(j,Hmat)
               !dbg write(6,*) 'kinmat'
               !dbg write(6,'(5e15.6)') Hmat
               Call Vscf_getVmat(j,Vmat)
               !dbg write(6,*) 'vmat'
               !dbg write(6,'(5e15.6)') Vmat
               !dbg write(6,*)
               Hmat=Hmat+Vmat
               Call diag2(nGj,nGj,Hmat,C,Ene)
               if(vscf_debug) then
                  write(debugFile,'(''MODAL ENE'')') 
                  write(debugFile,'(11e12.4)') Ene
                  write(debugFile,'(''MODAL COEFF'')') 
                  Do k=1,nGj
                     write(debugFile,'(11f10.4)') C(:,k)
                  End do

                  lbj=lb(j)+1

                  Allocate(C1(nGj,nGj))
                  Call Modal_getCwfn(j,C1)
                  write(debugFile,'(''OLD MODAL COEFF'')') 
                  write(debugFile,'(11f10.4)') C1(:,lbj)

                  kk=1
                  ovlp=0.D+00
                  Do k=1,nGj
                     tmp=0.D+00
                     Do l=1,nGj
                        tmp=tmp + C1(l,lbj)*C(l,k)
                     End do
                     write(debugFile,'(i4,f10.4)') k,tmp
                     if(abs(tmp) > ovlp) then
                        ovlp=abs(tmp)
                        kk=k
                     endif
                  End do

                  Deallocate(C1)

                  if(kk /= lbj) then
                     Allocate(Cw(nGj))
                     Cw=C(:,lbj)
                     C(:,lbj)=C(:,kk)
                     C(:,kk)=Cw
                     Deallocate(Cw)

                     write(debugFile,'(''MODIFIED MODAL COEFF'')') 
                     Do k=1,nGj
                        write(debugFile,'(11f10.4)') C(:,k)
                     End do
                  endif

               endif

               if(swt/=1) then
                  Call Modal_setCwfn(j,C)
               else
                  Allocate(C1(nGj,nGj))
                  Call Modal_getCwfn(j,C1)
                  Do k=1,nGj
                     if(C(1,k)*C1(1,k)>0.D+00) then
                       !tmp=0.D+00
                       Do l=1,nGj
                          C(l,k)=(C(l,k)+C1(l,k))/2.0D+00
                          !tmp=tmp+C(l,k)*C(l,k)
                       End do
                       !C(:,k)=C(:,k)/sqrt(tmp)
                     else
                       !tmp=0.D+00
                       Do l=1,nGj
                          C(l,k)=(C(l,k)-C1(l,k))/2.0D+00
                          !tmp=tmp+C(l,k)*C(l,k)
                       End do
                       !C(:,k)=C(:,k)/sqrt(tmp)
                     endif
                  End do
                  Call Modal_setCwfn(j,C)
                  Deallocate(C1)
               endif
               Call Modal_setEne(j,Ene)

               !dbg write(iout,'(9x,i4,f12.2)') 0,Ene(1)*H2wvn
               !dbg write(iout,'(9x,i4,f12.2)') (k-1,(Ene(k)-Ene(1))*H2wvn,k=2,11)

               Call Mem_dealloc('D',size(C))
               Call Mem_dealloc('D',size(Hmat))
               Call Mem_dealloc('D',size(Vmat))
               Call Mem_dealloc('D',size(Ene))
               Deallocate(C,Hmat,Vmat,Ene)

            End do

            Call Modal_update(-1)
            Etot(i)=vscf_totE()
            Enew=Etot(i)*H2wvn
            delE=Enew-Eold
            if(.not. silent) write(iout,130) itr,Eold,Enew,delE
        130 Format(13x,i4,2f16.2,d16.3)

            if(delE>0.D+00 .and. itr/=0) then
               swt=1
            else
               swt=0
            endif

            if(abs(delE)>Ethresh) then
               Eold=Enew
            else
               cnv=.true.
               exit
            endif

         End do

         if(cnv) then
            ! VSCF is converged !
        
            if(i==0) E0=Enew
            if(.not. silent) then
               if(i/=0) then 
                  write(iout,140) Enew,Enew-E0
               else
                  write(iout,145) Enew
               endif

               write(iout,150)
               Call Mem_alloc(-1,ierr,'D',maxCHO*Nfree)
               Allocate(C(maxCHO,Nfree))
               Call Vscf_getConf(maxCHO,Nfree,C)

               jj=(Nfree-mod(Nfree,6))/6
               Do j=1,jj
                  write(iout,155) (k,k=(j-1)*6+1,j*6)
                  Do k=1,maxCHO
                     write(iout,156) k-1,(C(k,l),l=(j-1)*6+1,j*6)
                  End do
               End do

               if(mod(Nfree,6)/=0) then
                  write(iout,155) (k,k=jj*6+1,Nfree)
                  Do k=1,maxCHO
                     write(iout,156) k-1,(C(k,l),l=jj*6+1,Nfree)
                  End do
               endif

               write(iout,*)
               Call Mem_dealloc('D',size(C))
               Deallocate(C)
               Call Vscf_ave()

            endif

            if(.not. state_specific .and. nn > 0) Call Vscf_virtual(nn)
            Call Vscf_Dump()

         else
            ! VSCF is UNconverged !
            if(i/=0) then
               write(iout,140) Enew,Enew-E0
            else
               write(iout,145) Enew
            endif
            Etot(i)=-1.0D+00
            Call Modal_init()
            Call Modal_update(-1)

            if(vscf_debug) then
               Call flush(debugFile)
               close(debugFile)
               stop
            endif
         endif

      End do

      Call Mem_dealloc('I',size(lb))
      Deallocate(lb)
!      Call Mem_dealloc('D',size(Etot))
!      Deallocate(Etot)

      if(vscf_debug) stop

  140 Format(3x,65('-'),/,7x,'E(VSCF)   =',f18.8,/,7x,'E(VSCF)-E0=',f18.8,/)
  145 Format(3x,65('-'),/,7x,'E(VSCF)=',f18.8,/)
  150 Format(7x,'o COEFFICIENTS',/)
  155 Format(9x,'-MODES-',1x,6i10)
  156 Format(10x,i5,2x,6f10.5)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_virtual(Nstate)

   USE Vscf_mod
   USE Constants_mod

   Implicit None

   Integer :: Nstate
   Integer :: n,lbl(Nfree)

   Integer :: i,cup,maxCup,Target_getMaxCup
   Integer, allocatable :: mm(:),vv(:)
   Real(8) :: E0,Ene


      Call Hmat_construct()
      Do n=1,Nstate
         Call Target_getLabel(n,Nfree,lbl)
         Call Hmat_getHmat(0,0,lbl,lbl,Etot(n))
      End do
      Call Hmat_destruct()

      if(.not. silent) then
         write(iout,100)
     100 Format(3x,'>> VIRTUAL VSCF ENERGIES',//, &
                6x,'o VSCF STATES',15x,'TOTAL ENERGY',9x,'E-E0')

         maxCup = Target_getMaxCup() 
         Allocate(mm(maxCup),vv(maxCup))

         E0=Etot(0)*H2wvn
         Do n=1,Nstate
            Ene=Etot(n)*H2wvn
            Call Target_getConf(n,cup,mm,vv)
            write(iout,110) 
            Do i=1,cup
               write(iout,111) mm(i),vv(i)
            End do
            Do i=cup+1,4
               write(iout,112)
            End do
            write(iout,120) Ene,Ene-E0
         End do
     110 Format(5x,' ',$)
     111 Format(i3,'_',i1,$)
     112 Format('     ',$)
     120 Format(2(3x,f18.8))

         Deallocate(mm,vv)

     endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_getConf(maxn,Nf,C)

   USE Vscf_mod

   Implicit None

   Integer :: maxn,Nf
   Real(8) :: C(maxn,Nf)

   Integer :: i,j,n
   Real(8), dimension(:,:), allocatable :: Cwfn

      Do i=1,Nfree
         if(nCHO(i)==0) then
            C(:,i)=0.D+00
            cycle
         endif
         n=nCHO(i)
         Allocate(Cwfn(n,n))
         Call Modal_getCwfn(i,Cwfn)
         Do j=1,n
            C(j,i)=Cwfn(j,lb(i)+1)
         End do
         Do j=n+1,maxn
            C(j,i)=0.D+00
         End do
         Deallocate(Cwfn)
      End do

   End subroutine
 
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_getKinmat(mode,Tm)

   USE Vscf_mod

   Implicit None

      Integer :: mode
      Real(8) :: Tm(nCHO(mode)*nCHO(mode))

      Tm=Tmat(ptTmat(mode)+1:ptTmat(mode)+nCHO(mode)*nCHO(mode))

   End subroutine
 
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_getDwfn(mode,nGrid,Dwfn)

   USE Vscf_mod

   Implicit None

   Integer :: mode,nGrid
   Real(8) :: Dwfn(nGrid)

      Call Modal_getXwfn(mode,nGrid,lb(mode),lb(mode),Dwfn)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Vscf_Dump()

   USE Vscf_mod

   Implicit None

   Integer :: i,wwf, nGi
   Real(8), allocatable :: Cwfn(:,:),Ene(:)
   Character(12) :: fname

   Integer :: n,nn,Target_getNstate
   Integer :: maxCup,cup,Target_getMaxCup
   Integer, allocatable :: mm(:),vv(:)

      Call Vscf_getFilename(Nst,fname)
      Call file_indicator(30,wwf)

      Open(wwf,file=trim(fname),status='unknown',form='FORMATTED')
      Write(wwf,'(''VSCF WAVEFUNCTION FOR STATE:'',i5)') Nst

      Write(wwf,'(10i3)') lb

      Write(wwf,'(''VSCF ENERGY'')') 
      Write(wwf,'(e17.8)') Etot(Nst)
      Do i=1,Nfree
         nGi=nCHO(i)
         Write(wwf,'(''MODE='',i3)') i
         Write(wwf,'(i5)') nGi
         if(nCHO(i)==0) cycle
         Allocate(Cwfn(nGi,nGi),Ene(nGi))
         Call Modal_getCwfn(i,Cwfn)
         Call Modal_getEne(i,Ene)
         Write(wwf,'(5e17.8)') Ene
         Write(wwf,'(i5)') nGi*nGi
         Write(wwf,'(5e17.8)') Cwfn
         Deallocate(Cwfn,Ene)
      End do

      nn=Target_getNstate()
      if(.not. state_specific .and. nn > 0) then
         maxCup=Target_getMaxCup()
         Allocate(mm(maxCup),vv(maxCup))
         write(wwf,'(''THE NUMBER OF TARGET STATES'')')
         write(wwf,'(i5)') nn
         write(wwf,'(''THE TARGET STATES'')')
         Do i=1,nn
            Call Target_getConf(i,cup,mm,vv)
            write(wwf,'(40i4)') cup,mm(1:cup),vv(1:cup)
         End do
         write(wwf,'(''VIRTUAL VSCF ENERGIES'')')
         Write(wwf,'(i5)') nn
         write(wwf,'(5e17.8)') Etot(1:nn)
         Deallocate(mm,vv)

      endif

      Close(wwf)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vscf_getFilename(nstate,fname)

   Implicit None

   Integer :: nstate
   Character(12) :: fname
   Character :: num*3

      write(num,'(i3.3)') nstate
      fname(1:12)='vscf-'//num//'.wfn'

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Function Vscf_totE()

     USE Vscf_mod
     USE PES_mod
     USE PES_qff_mod
     USE PES_grid_mod

     Implicit None

     Real(8) :: Vscf_totE

     Integer :: i,j,k,l,ij,ijk,idx,jdx,int2
     Real(8) :: Ekin,Epot,omgh,tmp,tmp1,tmp2

     Integer :: n,nGi,nGj,nGk,nGl,mi,mj,mk,ml
     Real(8), allocatable :: qav(:,:),qi(:,:),qj(:,:),qk(:,:),ql(:,:)
     Real(8), parameter :: thresh=1.D-06

     Real(8), allocatable :: Cwfn(:,:),Cw(:),Tm(:,:)
     Real(8), allocatable :: Di(:),Dj(:),Dk(:)
     Real(8), allocatable :: Vi(:),Vj(:),Vk(:),Vl(:),Vij(:,:),Vijk(:,:,:)

     Ekin=0.D+00
     Do i=1,Nfree
        nGi=nCHO(i)
        if(nGi == 0) cycle
        Allocate(Cwfn(nGi,nGi),Cw(nGi),Tm(nGi,nGi))

        Call Modal_getCwfn(i,Cwfn)
        Cw=Cwfn(:,lb(i)+1)

        Call Vscf_getKinmat(i,Tm)

        Do j=1,nGi
           tmp=0.D+00
           Do k=1,nGi
              tmp=tmp + Tm(j,k)*Cw(k)
           End do
           Ekin=Ekin + Cw(j)*tmp
        End do

        Deallocate(Cwfn,Cw,Tm)
     End do

     Epot=0.D+00
     Call Vscf_clearVmf()

     ! Potential energy and mean-field potential
     ! QFF
     if(qff) then         
        if(icff==0) then ! QFF
           Call Mem_alloc(-1,i,'D',Nfree*4) 
           Allocate(qav(4,Nfree))
        else             ! CFF
           Call Mem_alloc(-1,i,'D',Nfree*3) 
           Allocate(qav(3,Nfree))
        end if

        Do mi=1,Nfree
           nGi=nCHO(mi)
           if(nGi > 0) then
              Allocate(Di(nGi),qi(nGi,4))
              Call Vscf_getDwfn(mi,nGi,Di)
              Call Modal_getQ4(mi,qi)
              ! <Qi>
              tmp=0.D+00
              Do i=1,nGi
                 tmp=tmp + Di(i)*qi(i,1)
              End do
              qav(1,mi)=tmp
              ! <Qi^2>
              tmp=0.D+00
              Do i=1,nGi
                 tmp=tmp + Di(i)*qi(i,2)
              End do
              qav(2,mi)=tmp
              ! <Qi^3>
              tmp=0.D+00
              Do i=1,nGi
                 tmp=tmp + Di(i)*qi(i,3)
              End do
              qav(3,mi)=tmp

              if(icff==0) then ! QFF
                 ! <Qi^4>
                 tmp=0.D+00
                 Do i=1,nGi
                    tmp=tmp + Di(i)*qi(i,4)
                 End do
                 qav(4,mi)=tmp
              end if

              Deallocate(Di,qi)
           else
              qav(:,mi)=0.D+00

           endif

        End do
        ! Do mi=1,Nfree
        !    write(6,'(3f12.2)') qav(:,mi)
        !    write(6,'(4f12.2)') qav(:,mi)
        ! End do
     endif

     if(icff==1) then ! CFF
        Do n=1,nQ1
           mi=mQ1(1,n)
           nGi=nCHO(mi)
           if(nGi == 0) cycle

           Allocate(Vi(nGi),qi(nGi,4))
           Call Modal_getQ4(mi,qi)

           Epot=Epot + qav(1,mi)*coeff1(1,n) &
                     + qav(2,mi)*coeff1(2,n) &
                     + qav(3,mi)*coeff1(3,n)
           Do i=1,nGi
              Vi(i)=   qi(i,1)*coeff1(1,n) &
                     + qi(i,2)*coeff1(2,n) &
                     + qi(i,3)*coeff1(3,n)
           End do

           Call Vscf_addVmf(mi,nGi,Vi)
           Deallocate(Vi,qi)

        End do

     else ! QFF
        Do n=1,nQ1
           mi=mQ1(1,n)
           nGi=nCHO(mi)
           if(nGi == 0) cycle

           Allocate(Vi(nGi),qi(nGi,4))
           Call Modal_getQ4(mi,qi)

           Epot=Epot + qav(1,mi)*coeff1(1,n) &
                     + qav(2,mi)*coeff1(2,n) &
                     + qav(3,mi)*coeff1(3,n) &
                     + qav(4,mi)*coeff1(4,n) 
           Do i=1,nGi
              Vi(i)=   qi(i,1)*coeff1(1,n) &
                     + qi(i,2)*coeff1(2,n) &
                     + qi(i,3)*coeff1(3,n) &
                     + qi(i,4)*coeff1(4,n) 
           End do

           Call Vscf_addVmf(mi,nGi,Vi)
           Deallocate(Vi,qi)

        End do
     end if

     Do n=1,nS1
        mi=mS1(1,n)
        nGi=nGrid1(1,n)
        Allocate(Vi(nGi),Di(nGi))

        Call PES_grid_getV1(n,Vi)
        Call Vscf_getDwfn(mi,nGi,Di)
        Call Vscf_addVmf(mi,nGi,Vi)
        tmp=0.D+00
        Do i=1,nGi
           tmp=tmp + Di(i)*Vi(i)
        End do
        Epot=Epot+tmp
        
        Deallocate(Vi,Di)

     End do

     if(MR==1) goto 999

     if(icff==1) then ! CFF
        Do n=1,nQ2
           mi=mQ2(1,n)
           mj=mQ2(2,n)
           nGi=nCHO(mi)
           nGj=nCHO(mj)
           if(nGi == 0 .or. nGj == 0) cycle

           Allocate(Vi(nGi),Vj(nGj))
           Allocate(qi(nGi,4),qj(nGj,4))
           Call Modal_getQ4(mi,qi)
           Call Modal_getQ4(mj,qj)

           Epot=Epot + qav(1,mi)*qav(1,mj)*coeff2(1,n) &
                     + qav(2,mi)*qav(1,mj)*coeff2(2,n) &
                     + qav(1,mi)*qav(2,mj)*coeff2(3,n) 

           Do i=1,nGi
              Vi(i)= + qi(i,1)*qav(1,mj)*coeff2(1,n) &
                     + qi(i,2)*qav(1,mj)*coeff2(2,n) &
                     + qi(i,1)*qav(2,mj)*coeff2(3,n) 
           End do

           Do j=1,nGj
              Vj(j)= + qav(1,mi)*qj(j,1)*coeff2(1,n) &
                     + qav(2,mi)*qj(j,1)*coeff2(2,n) &
                     + qav(1,mi)*qj(j,2)*coeff2(3,n) 
           End do
           Call Vscf_addVmf(mi,nGi,Vi)
           Call Vscf_addVmf(mj,nGj,Vj)
           Deallocate(Vi,Vj,qi,qj)

        End do

     else ! QFF
        Do n=1,nQ2
           mi=mQ2(1,n)
           mj=mQ2(2,n)
           nGi=nCHO(mi)
           nGj=nCHO(mj)
           if(nGi == 0 .or. nGj == 0) cycle

           Allocate(Vi(nGi),Vj(nGj))
           Allocate(qi(nGi,4),qj(nGj,4))
           Call Modal_getQ4(mi,qi)
           Call Modal_getQ4(mj,qj)

           Epot=Epot + qav(1,mi)*qav(1,mj)*coeff2(1,n) &
                     + qav(2,mi)*qav(1,mj)*coeff2(2,n) &
                     + qav(1,mi)*qav(2,mj)*coeff2(3,n) &
                     + qav(2,mi)*qav(2,mj)*coeff2(4,n) &
                     + qav(3,mi)*qav(1,mj)*coeff2(5,n) &
                     + qav(1,mi)*qav(3,mj)*coeff2(6,n) 

           Do i=1,nGi
              Vi(i)= + qi(i,1)*qav(1,mj)*coeff2(1,n) &
                     + qi(i,2)*qav(1,mj)*coeff2(2,n) &
                     + qi(i,1)*qav(2,mj)*coeff2(3,n) &
                     + qi(i,2)*qav(2,mj)*coeff2(4,n) &
                     + qi(i,3)*qav(1,mj)*coeff2(5,n) &
                     + qi(i,1)*qav(3,mj)*coeff2(6,n) 
           End do

           Do j=1,nGj
              Vj(j)= + qav(1,mi)*qj(j,1)*coeff2(1,n) &
                     + qav(2,mi)*qj(j,1)*coeff2(2,n) &
                     + qav(1,mi)*qj(j,2)*coeff2(3,n) &
                     + qav(2,mi)*qj(j,2)*coeff2(4,n) &
                     + qav(3,mi)*qj(j,1)*coeff2(5,n) &
                     + qav(1,mi)*qj(j,3)*coeff2(6,n) 
           End do
           Call Vscf_addVmf(mi,nGi,Vi)
           Call Vscf_addVmf(mj,nGj,Vj)
           Deallocate(Vi,Vj,qi,qj)

        End do
     end if

     Do n=1,nS2
        mi=mS2(1,n)
        mj=mS2(2,n)
        nGi=nGrid2(1,n)
        nGj=nGrid2(2,n)
        Allocate(Vij(nGj,nGi),Vi(nGi),Vj(nGj),Di(nGi),Dj(nGj))
        Call PES_grid_getV2(n,Vij)
        Call Vscf_getDwfn(mi,nGi,Di)
        Call Vscf_getDwfn(mj,nGj,Dj)
        
        tmp=0.D+00
        Do i=1,nGi
           Vi(i)=0.D+00
           Do j=1,nGj
              Vi(i)=Vi(i) + Dj(j)*Vij(j,i)
           End do
           tmp=tmp + Di(i)*Vi(i)
        End do
        Epot=Epot+tmp
        
        Do j=1,nGj
           Vj(j)=0.D+00
           Do i=1,nGi
              Vj(j)=Vj(j) + Di(i)*Vij(j,i)
           End do
        End do
        
        Call Vscf_addVmf(mi,nGi,Vi)
        Call Vscf_addVmf(mj,nGj,Vj)
        Deallocate(Vij,Vi,Vj,Di,Dj)
        
     End do

     if(MR==2) goto 999

     if(icff==1) then ! CFF
        Do n=1,nQ3
           mi=mQ3(1,n)
           mj=mQ3(2,n)
           mk=mQ3(3,n)
           nGi=nCHO(mi)
           nGj=nCHO(mj)
           nGk=nCHO(mk)
           if(nGi == 0 .or. nGj == 0 .or. nGk == 0) cycle

           Allocate(Vi(nGi),Vj(nGj),Vk(nGk))
           Allocate(qi(nGi,4),qj(nGj,4),qk(nGk,4))
           Call Modal_getQ4(mi,qi)
           Call Modal_getQ4(mj,qj)
           Call Modal_getQ4(mk,qk)
           
           Epot=Epot + qav(1,mi)*qav(1,mj)*qav(1,mk)*coeff3(1,n) 

           Do i=1,nGi
              Vi(i) =  qi(i,1)*qav(1,mj)*qav(1,mk)*coeff3(1,n) 
           End do

           Do j=1,nGj
              Vj(j)=   qav(1,mi)*qj(j,1)*qav(1,mk)*coeff3(1,n) 
           End do

           Do k=1,nGk
              Vk(k)=   qav(1,mi)*qav(1,mj)*qk(k,1)*coeff3(1,n) 
           End do

           Call Vscf_addVmf(mi,nGi,Vi)
           Call Vscf_addVmf(mj,nGj,Vj)
           Call Vscf_addVmf(mk,nGk,Vk)
           Deallocate(Vi,Vj,Vk,qi,qj,qk)

        End do

     else ! QFF
        Do n=1,nQ3
           mi=mQ3(1,n)
           mj=mQ3(2,n)
           mk=mQ3(3,n)
           nGi=nCHO(mi)
           nGj=nCHO(mj)
           nGk=nCHO(mk)
           if(nGi == 0 .or. nGj == 0 .or. nGk == 0) cycle

           Allocate(Vi(nGi),Vj(nGj),Vk(nGk))
           Allocate(qi(nGi,4),qj(nGj,4),qk(nGk,4))
           Call Modal_getQ4(mi,qi)
           Call Modal_getQ4(mj,qj)
           Call Modal_getQ4(mk,qk)
           
           Epot=Epot + qav(1,mi)*qav(1,mj)*qav(1,mk)*coeff3(1,n) &
                     + qav(2,mi)*qav(1,mj)*qav(1,mk)*coeff3(2,n) &
                     + qav(1,mi)*qav(2,mj)*qav(1,mk)*coeff3(3,n) &
                     + qav(1,mi)*qav(1,mj)*qav(2,mk)*coeff3(4,n) 

           Do i=1,nGi
              Vi(i) =  qi(i,1)*qav(1,mj)*qav(1,mk)*coeff3(1,n) &
                     + qi(i,2)*qav(1,mj)*qav(1,mk)*coeff3(2,n) &
                     + qi(i,1)*qav(2,mj)*qav(1,mk)*coeff3(3,n) &
                     + qi(i,1)*qav(1,mj)*qav(2,mk)*coeff3(4,n) 
           End do

           Do j=1,nGj
              Vj(j)=   qav(1,mi)*qj(j,1)*qav(1,mk)*coeff3(1,n) &
                     + qav(2,mi)*qj(j,1)*qav(1,mk)*coeff3(2,n) &
                     + qav(1,mi)*qj(j,2)*qav(1,mk)*coeff3(3,n) &
                     + qav(1,mi)*qj(j,1)*qav(2,mk)*coeff3(4,n) 
           End do

           Do k=1,nGk
              Vk(k)=   qav(1,mi)*qav(1,mj)*qk(k,1)*coeff3(1,n) &
                     + qav(2,mi)*qav(1,mj)*qk(k,1)*coeff3(2,n) &
                     + qav(1,mi)*qav(2,mj)*qk(k,1)*coeff3(3,n) &
                     + qav(1,mi)*qav(1,mj)*qk(k,2)*coeff3(4,n) 
           End do

           Call Vscf_addVmf(mi,nGi,Vi)
           Call Vscf_addVmf(mj,nGj,Vj)
           Call Vscf_addVmf(mk,nGk,Vk)
           Deallocate(Vi,Vj,Vk,qi,qj,qk)

        End do
     end if

     Do n=1,nS3
        mi=mS3(1,n)
        mj=mS3(2,n)
        mk=mS3(3,n)
        nGi=nGrid3(1,n)
        nGj=nGrid3(2,n)
        nGk=nGrid3(3,n)
        Allocate(Vijk(nGk,nGj,nGi),Vi(nGi),Vj(nGj),Vk(nGk))
        Allocate(Vij(nGj,nGi))
        Allocate(Di(nGi),Dj(nGj),Dk(nGk))
        Call PES_grid_getV3(n,Vijk)
        Call Vscf_getDwfn(mi,nGi,Di)
        Call Vscf_getDwfn(mj,nGj,Dj)
        Call Vscf_getDwfn(mk,nGk,Dk)
        
        Do i=1,nGi
        Do j=1,nGj
           Vij(j,i)=0.D+00
           Do k=1,nGk
              Vij(j,i)=Vij(j,i) + Dk(k)*Vijk(k,j,i)
           End do
        End do
        End do
        
        Do i=1,nGi
           Vi(i)=0.D+00
           Do j=1,nGj
              Vi(i)=Vi(i) + Dj(j)*Vij(j,i)
           End do
        End do
        
        Do j=1,nGj
           Vj(j)=0.D+00
           Do i=1,nGi
              Vj(j)=Vj(j) + Di(i)*Vij(j,i)
           End do
        End do

        Do k=1,nGk
           Vk(k)=0.D+00
           Do i=1,nGi
              tmp=0.D+00
              Do j=1,nGj
                 tmp=tmp + Dj(j)*Vijk(k,j,i)
              End do
              Vk(k)=Vk(k) + tmp*Di(i)
           End do
        End do
        
        tmp=0.D+00
        Do i=1,nGi
           tmp=tmp + Di(i)*Vi(i)
        End do
        Epot=Epot + tmp
        
        Call Vscf_addVmf(mi,nGi,Vi)
        Call Vscf_addVmf(mj,nGj,Vj)
        Call Vscf_addVmf(mk,nGk,Vk)
        Deallocate(Vijk,Vij,Vi,Vj,Vk,Di,Dj,Dk)
        
     End do

     if(MR==3) goto 999

     Do n=1,nQ4
        mi=mQ4(1,n)
        mj=mQ4(2,n)
        mk=mQ4(3,n)
        ml=mQ4(4,n)
        nGi=nCHO(mi)
        nGj=nCHO(mj)
        nGk=nCHO(mk)
        nGl=nCHO(ml)
        if(nGi == 0 .or. nGj == 0 .or. nGk == 0 .or. nGl == 0) cycle

        Allocate(Vi(nGi),Vj(nGj),Vk(nGk),Vl(nGl))
        Allocate(qi(nGi,4),qj(nGj,4),qk(nGk,4),ql(nGk,4))
        Call Modal_getQ4(mi,qi)
        Call Modal_getQ4(mj,qj)
        Call Modal_getQ4(mk,qk)
        Call Modal_getQ4(ml,ql)
        
        Epot=Epot + qav(1,mi)*qav(1,mj)*qav(1,mk)*qav(1,ml)*coeff4(1,n) 
        
        Do i=1,nGi
           Vi(i) =  qi(i,1)*qav(1,mj)*qav(1,mk)*qav(1,ml)*coeff4(1,n) 
        End do
        
        Do j=1,nGj
           Vj(j)=   qav(1,mi)*qj(j,1)*qav(1,mk)*qav(1,ml)*coeff4(1,n) 
        End do
        
        Do k=1,nGk
           Vk(k)=   qav(1,mi)*qav(1,mj)*qk(k,1)*qav(1,ml)*coeff4(1,n) 
        End do
        
        Do l=1,nGl
           Vl(l)=   qav(1,mi)*qav(1,mj)*qav(1,mk)*ql(l,1)*coeff4(1,n) 
        End do

        Call Vscf_addVmf(mi,nGi,Vi)
        Call Vscf_addVmf(mj,nGj,Vj)
        Call Vscf_addVmf(mk,nGk,Vk)
        Call Vscf_addVmf(ml,nGl,Vl)
        Deallocate(Vi,Vj,Vk,Vl,qi,qj,qk,ql)
        
     End do
     
 999 Continue

     !dbg write(6,'(2f20.10)') Ekin,Epot
     Vscf_totE=Ekin+Epot
     if(qff) then
        Call Mem_dealloc('D',size(qav))
        Deallocate(qav)
     endif

     return
     
   End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_setVmf(mode,nGrid,Vmf)

   USE Vscf_mod

   Implicit None

   Integer :: mode,nGrid,nGrid2,pt
   Real(8) :: Vmf(nGrid)

      pt=ptVmf0(nGrid,mode)
      Vmf0(pt+1:pt+nGrid)=Vmf

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_getVmf(mode,nGrid,Vmf)

   USE Vscf_mod

   Implicit None

   Integer :: mode,nGrid,nGrid2,pt
   Real(8) :: Vmf(nGrid)

      pt=ptVmf0(nGrid,mode)
      Vmf=Vmf0(pt+1:pt+nGrid)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_addVmf(mode,nGrid,Vmf)

   USE Vscf_mod

   Implicit None

   Integer :: mode,nGrid,nGrid2,pt
   Real(8) :: Vmf(nGrid)

      pt=ptVmf0(nGrid,mode)
      Vmf0(pt+1:pt+nGrid)=Vmf0(pt+1:pt+nGrid)+Vmf

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_clearVmf()

   USE Vscf_mod

   Implicit None

   Integer :: i,j,k,l, mm,nG
   Real(8), allocatable :: Vmf(:)

      Do i=1,Nfree
      Do j=1,nCHO(i)
         if(ptVmf0(j,i)<0) cycle
         mm=i
         nG=j

         Allocate(Vmf(nG))
         Vmf=0.D+00
         Call Vscf_setVmf(mm,nG,Vmf)
         Deallocate(Vmf)

      End do
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vscf_getVmat(mm,Vmat)

   USE Vscf_mod

   Implicit None

   Integer :: pt,mm,i,j,k,l,nG,nGi
   Real(8) :: Vmat(nCHO(mm),nCHO(mm))
   Real(8), allocatable :: Vmf(:),xdvr(:,:),Vms(:,:)
   Real(8), allocatable :: qm(:)

      Vmat=0.D+00
      nG=nCHO(mm)

      Allocate(Vmf(nG),xdvr(nG,nG))
      Call Vscf_getVmf(mm,nG,Vmf)
      Call Modal_getxdvr(mm,nG,xdvr)

      if(vscf_debug) then
         Allocate(qm(nG))
         Call Modal_getQ(mm,nG,qm)
         write(debugFile,'(11f12.4)') qm
         write(debugFile,'(11f12.6)') Vmf
         write(debugFile,*)
         Deallocate(qm)
         !Do j=1,nG
         !   write(debugFile,'(11f8.4)') xdvr(:,j)
         !End do
         !write(debugFile,*)
      endif

      Do j=1,nG
         Do k=1,j-1
            Vmat(j,k)=0.D+00
            Do l=1,nG
               Vmat(j,k)=Vmat(j,k) + xdvr(j,l)*Vmf(l)*xdvr(k,l)
            End do
            Vmat(k,j)=Vmat(j,k)
         End do

         Vmat(j,j)=0.D+00
         Do l=1,nG
            Vmat(j,j)=Vmat(j,j) + xdvr(j,l)*Vmf(l)*xdvr(j,l)
         End do
      End do
      Deallocate(Vmf,xdvr)

      Do i=1,nG-1
         if(ptVmf0(i,mm)<0) cycle

         nGi=i
         Allocate(Vmf(nGi),xdvr(nGi,nGi),Vms(nGi,nGi))
         Call Vscf_getVmf(mm,nGi,Vmf)
         Call Modal_getxdvr(mm,nGi,xdvr)

         Do j=1,nGi
            Do k=1,j-1
               Vms(j,k)=0.D+00
               Do l=1,nGi
                  Vms(j,k)=Vms(j,k) + xdvr(j,l)*Vmf(l)*xdvr(k,l)
               End do
               Vms(k,j)=Vms(j,k)
            End do

            Vms(j,j)=0.D+00
            Do l=1,nGi
               Vms(j,j)=Vms(j,j) + xdvr(j,l)*Vmf(l)*xdvr(j,l)
            End do
         End do

         Do j=1,nGi
            Do k=1,j-1
               Vmat(k,j)=Vmat(k,j)+Vms(k,j)
               Vmat(j,k)=Vmat(k,j)
            End do
            Vmat(j,j)=Vmat(j,j)+Vms(j,j)
         End do
         Deallocate(Vmf,xdvr,Vms)

      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vscf_ave

   USE Constants_mod
   USE Vscf_mod

   Implicit None

   Integer :: ie,Nat,spr_getNat
   Integer :: j,k,nGj
   Logical :: isNMA, Mol_isNMA

   Real(8), allocatable :: QQ(:),x0(:,:),Dwfn(:),qj(:)

      Call Mem_alloc(-1,ie,'D',Nfree)
      Allocate(QQ(Nfree))

      write(iout,100)
      QQ=0.D+00
      Do j=1,Nfree
         if(nCHO(j)==0) cycle
         nGj=nCHO(j)
         Allocate(Dwfn(nGj),qj(nGj))
         Call Modal_getQ(j,nGj,qj)
         Call Vscf_getDwfn(j,nGj,Dwfn)
         Do k=1,nGj
            QQ(j)=QQ(j)+Dwfn(k)*qj(k)
         End do
         Deallocate(Dwfn,qj)
         !Write(iout,'(i10)') nho(j)
         !Write(iout,'(11f10.3)') wfn(:,j) 
         !Write(iout,'(11f10.3)') quad(:,j)
      End do
      Write(iout,200) QQ
      Write(iout,*)

      isNMA=Mol_isNMA()
      if(isNMA) then
         Write(iout,110)
         Call Mol_getNat(Nat)
         Call Mem_alloc(1,ie,'D',Nat*3)
         Allocate(x0(3,Nat))
         Call nma_q2x(x0,QQ)
         Write(iout,200) x0
         Write(iout,*)

         Call Mem_dealloc('D',size(x0))
         Deallocate(x0)
      endif

      Call Mem_dealloc('D',size(QQ))
      Deallocate(QQ)

  100 Format(7x,'o VIBRATIONALLY AVERAGED STRUCTURE',//,7x,'  - Q0 -')
  110 Format(7x,'  - X0 -')
  200 Format(10x,3f12.6)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Set silent mode
!
   Subroutine Vscf_setSilent()

   USE Vscf_mod

   Implicit None

      silent = .true.

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Set silent mode
!
   Subroutine Vscf_unsetSilent()

   USE Vscf_mod

   Implicit None

      silent = .false.

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Return the VSCF energy in Hartree
!    -1.0 when the VSCF is unconverged
!
   Function Vscf_getEvscf(istate)

   USE Vscf_mod

   Implicit None

   Integer :: istate
   Real(8) :: Vscf_getEvscf

      Vscf_getEvscf = Etot(istate)

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Function Vscf_getEthresh()

   USE Vscf_mod

   Implicit None

   Real(8) :: Vscf_getEthresh

      Vscf_getEthresh = Ethresh

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Function Vscf_getMaxIteration()

   USE Vscf_mod

   Implicit None

   Integer :: Vscf_getMaxIteration

      Vscf_getMaxIteration = MaxItr 

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Vscf_setIcff(icff0)

   USE Vscf_mod

   Implicit None

   Integer :: icff0

      icff=icff0

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine Vscf_unsetIcff()

   USE Vscf_mod

   Implicit None

      icff=0

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
