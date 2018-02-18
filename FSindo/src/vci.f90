!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/30
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Vci_mod

   USE Vib_mod, only : Nfree, Mfree, nCHO, maxCHO

   ! -- (INPUT parameters) -------------------------------------------------
   !     dump     :: if true, dump VCI wfn
   !     dumpHmat :: if true, dump VCI matrix
   !     noDiag   :: if true, exit after generating Hmat without diagonalization
   !     geoAv    :: if true, calculate the vibrationally averaged geometry

   Logical :: dump, dumpHmat
   Logical :: noDiag,geomAv

   ! -- (CI basis parameters) ----------------------------------------------
   !    Nstate              :: The number of CI states to solve
   !     nCI                 :: CI dimension
   !     CI space selection
   !      o maxExc(Nfree)    :: Max quantum number of excitation
   !      o maxSum           :: Max sum of quantum number to excite
   !      o nCUP             :: Max number of mode to excite
   !     CI basis
   !      o lcup(nCI)        :: The number of modes that are excited
   !      o mm(nCUP,nCI)     :: The modes that are excited
   !      o vv(nCUP,nCI)     :: The quantum number of each mode

   Integer :: Nstate, maxSum, nCUP, nCI
   Integer, dimension(:),   allocatable:: maxExc
   Integer, dimension(:),   allocatable:: lcup
   Integer, dimension(:,:), allocatable:: mm,vv

   ! -- (VCI wfn) ----------------------------------------------------------
   !     CIwfn(nCI*Nstate)    :: CI coefficient matrix
   !     CIene(nCI)           :: CI energies

   Real(8), dimension(:), allocatable :: CIwfn, CIene

   ! -- (Print option) -----------------------------------------------------
   !     printWeight :: Print if the weight is larger than this threshold

   Real(8) :: printWeight

   Contains

   Subroutine setCIbasis()

   USE Constants_mod

   Implicit None

   Logical :: lvscf, Vib_getlvscf
   Integer :: i,j,k,jtmp,ktmp,jold,kold,ist,ierr
   Real(8) :: tmp,Ei,Ej
   Real(8) :: omegaf(Nfree),DelE(Nfree)
   Real(8) :: E1wfn(maxCHO,Nfree)

   Integer :: lb(Nfree),im(Nfree),nP(nCUP)

   Integer, dimension(:), allocatable :: lsum
   Real(8), dimension(:), allocatable :: Ene

      E1wfn=0.D+00

      lvscf=Vib_getlvscf()
      if(lvscf) then

        ! VCI/VSCF

        Do i=1,Nfree
           Call Modal_getEne(i,E1wfn(:,i))
        End do
        E1wfn=E1wfn*H2wvn

        Do i=2,Nfree
           tmp=E1wfn(1,i)
           Do j=1,i-1
              if(abs(tmp-E1wfn(1,j))<1.D-02) then
                 Do k=1,nCHO(j)
                    E1wfn(k,j)=E1wfn(k,j)+1.D+00*dble(k)
                 End do
              endif
           End do
        End do
        !dbg  Do i=1,Nfree
        !dbg     write(6,*)
        !dbg     write(6,'(2i4)') i,nCHO(i)
        !dbg     write(6,'(f12.3)') (E1wfn(j,i),j=1,maxCHO)
        !dbg End do

      else

        ! VCI/HO

        Call Vib_getFreq(omegaf)
        Do i=2,Nfree
           tmp=omegaf(i)
           Do j=1,i-1
              if(abs(tmp-omegaf(j))<1.D-02) then
                 omegaf(j)=omegaf(j)+1.D+00
              endif
           End do
        End do

        Do i=1,Nfree
           tmp=omegaf(i)
           Do j=1,nCHO(i)
              E1wfn(j,i)=(dble(j)-0.5D+00)*tmp
           End do
           Do j=nCHO(i)+1,maxCHO
              E1wfn(j,i)=0.D+00
           End do
        End do

      endif
      !dbg  Do i=1,Nfree
      !dbg  Do j=1,nCHO(i)
      !dbg     write(6,'(2i4,f10.2)') i,j-1,E1wfn(j,i)-E1wfn(1,i)
      !dbg  End do
      !dbg  End do

      Call Mem_alloc(-1,ierr,'D',nCI)
      Call Mem_alloc(-1,ierr,'I',nCI*2)
      Allocate(Ene(nCI),lsum(nCI))

      mm(:,1)=0
      vv(:,1)=0
      lsum(1)=0
      lcup(1)=0
      Ene(1)=0.D+00
      Do i=1,Nfree
         Ene(1)=Ene(1)+E1wfn(1,i)
      End do
      nP=0

      jold=-1;kold=-1
      ist=2
      Do while(ist<=nCI) 
         Ei=1.D+15
         Do j=1,ist-1
            if(lsum(j)>=maxSum) cycle
            Call getLabel(j,lb)
            im=lb+1

            if(lcup(j) < nCUP) then
               Do k=1,Nfree
                  if(im(k)<maxExc(k)) then
                     DelE(k)=E1wfn(im(k)+1,k)-E1wfn(im(k),k)
                  else
                     DelE(k)=0.D+00
                  endif
               End do
            else
               Do k=1,Nfree
                  if(im(k)<maxExc(k) .and. lb(k)/=0) then
                     DelE(k)=E1wfn(im(k)+1,k)-E1wfn(im(k),k)
                  else
                     DelE(k)=0.D+00
                  endif
               End do
            endif

            tmp=Ene(j)+MaxVal(DelE)
            if(tmp<Ene(ist-1)) cycle

            Do k=1,Nfree
               Ej=Ene(j)+DelE(k)
               if(Ej<Ei.and.(Ej-Ene(ist-1))>1.D-04) then 
                  Ei=Ej
                  jtmp=j
                  ktmp=k
               endif
            End do
         End do
         Call getLabel(jtmp,lb)
         im=lb+1
         lb(ktmp)=lb(ktmp)+1
         
         if(jtmp==jold .and. ktmp==kold) then
             nCI=ist-1
             exit
         endif
         jold=jtmp
         kold=ktmp

         lsum(ist)=0
         lcup(ist)=0
         Do j=1,Nfree
            if(lb(j)/=0) then
               lsum(ist)=lsum(ist)+lb(j)
               lcup(ist)=lcup(ist)+1
               mm(lcup(ist),ist)=j
               vv(lcup(ist),ist)=lb(j)
            endif
         End do
         Ene(ist)=Ene(jtmp)+E1wfn(im(ktmp)+1,ktmp)-E1wfn(im(ktmp),ktmp)
         !dbg write(6,'(3i5)') im
         !dbg write(6,'(2i5,''|'',3i5,f12.4)') jtmp,ktmp,lb(:,i),Ene(i)

         nP(lcup(ist))=nP(lcup(ist))+1
         ist=ist+1
      End do

      !dbg write(6,*) nCI
      !dbg Do i=1,nCI
      !dbg    Call getLabel(i,lb)
      !dbg    write(6,'(f12.1,7i3)') Ene(i),lb
      !dbg End do
      !dbg write(6,*) nP

      Call Mem_dealloc('D',size(Ene))
      Deallocate(Ene)
      Call Mem_dealloc('I',size(lsum))
      Deallocate(lsum)

   End subroutine

   Subroutine getLabel(nn,label)

   Integer :: nn,label(Nfree)

      label=0
      Do i=1,lcup(nn)
         label(mm(i,nn))=vv(i,nn)
      End do

   End subroutine

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vci_run()

   USE Constants_mod
   USE Vci_mod

   Implicit None

   Integer :: ierr
   Integer :: i,j,k,l
   Integer :: maxEx(MaxNfree),maxExAll
   Logical :: lvscf, Vib_getlvscf
   Logical :: readCIbasis
   Character(12) :: vscfFile

   Namelist /vci/Nstate,nCI,maxEx,maxExAll,maxSum,nCUP,geomAv,dump,printWeight, &
                 readCIbasis,dumpHmat,noDiag

      ! ------------------------------------------------------------
      ! Read VSCF modal and setup Hmat
      lvscf = Vib_getlvscf()
      if(lvscf) then
         Call Vscf_getFilename(0,vscfFile)
         Call Modal_readVSCF(ierr,vscfFile)
      endif
      Call Hmat_construct()

      ! ------------------------------------------------------------
      ! >> Read input 

      ! --- default ---
      dump=.true.
      dumpHmat=.false.
      geomAv=.false.
      noDiag=.false.

      ! - VCI space selection 
      Nstate=-1
      nCI=0
      maxEx=-1
      maxExAll=-1
      maxSum=-1
      nCUP=4
      readCIbasis=.false.

      ! - Print threshold
      printWeight=1.0D-03

      Rewind(inp)
      Read(inp,vci,end=10)
   10 Continue

      write(iout,100)
  100 Format(/,'(  ENTER VCI MODULE  )',//, &
             3x,'>> VCI OPTIONS',/)

      ! ------------------------------------------------------------

      write(iout,120)

      if(.not. readCIbasis) then
         if(nCUP < 1 .or. nCUP > Nfree) nCUP=Nfree
         write(iout,121) nCUP

         if(maxSum>0) then 
            write(iout,122) maxSum
         else
            write(iout,123) 
         endif

         Call Mem_alloc(-1,ierr,'I',Nfree)
         Allocate(maxExc(Nfree))
         Do i=1,Nfree
            if(maxEx(i)/=-1) then 
               maxExc(i)=maxEx(i)+1
            elseif(maxExAll/=-1) then
               maxExc(i)=maxExAll+1
            else
               maxExc(i)=nCHO(i)
            endif
            if(maxExc(i)>nCHO(i)) maxExc(i)=nCHO(i)
            if(maxSum>0 .and. maxExc(i)>maxSum+1) maxExc(i)=maxSum+1
         End do
         if(Nfree<7) then 
            write(iout,124) (maxExc(i)-1,i=1,Nfree)
         else
            write(iout,124) (maxExc(i)-1,i=1,6)
            write(iout,125) (maxExc(i)-1,i=7,Nfree)
         endif
         write(iout,*)

         if(maxSum>0) then
            k=maxVal(maxExc)-1
            j=1; l=1
            Do i=1,nCUP
               !l=l*(Nfree+1-i)/i*(maxSum+1-i)/i
               l=l*(Mfree+1-i)/i*(maxSum+1-i)/i
               j=j+l
            End do

         else
            maxSum=0
            Do i=1,Nfree
               if(maxExc(i)>0) maxSum=maxSum + maxExc(i)-1
            End do
            !maxSum=maxSum-Nfree

            k=maxVal(maxExc)-1
            j=1; l=1
            Do i=1,nCUP
               !l=l*(Nfree+1-i)/i*k
               l=l*(Mfree+1-i)/i*k
               j=j+l
            End do
         endif
         if(nCI>j .or. nCI==0) nCI=j

         Call Mem_alloc(-1,ierr,'I',nCI)
         Allocate(lcup(nCI))

         Call Mem_alloc(-1,ierr,'I',nCUP*nCI*2)
         Allocate(mm(nCUP,nCI),vv(nCUP,nCI))

         Call setCIbasis()
         !dbg write(6,*) 'nCI=',nci
         !dbg write(6,*) 'end of setCIbasis'

         Call Mem_dealloc('I',Nfree)
         Deallocate(maxExc)

         if(Nstate>nCI) Nstate=nCI
         Call Mem_alloc(-1,ierr,'D',nCI*(Nstate+1))
         Allocate(CIwfn(nCI*Nstate),CIene(nCI))

      else
         write(iout,126)
         Call Vci_read()

      endif

  120 Format(7x,'o VCI SPACE SELECTION')
  121 Format(9x,'- MAX NUM. OF MODES TO EXCITE :    ',i7)
  122 Format(9x,'- MAX SUM OF QUANTUM NUM.     :    ',i7)
  123 Format(9x,'- MAX SUM OF QUANTUM NUM.     :  UNLIMITED')
  124 Format(9x,'- MAX EXCITATION OF EACH MODE :  ',6i3)
  125 Format(9x,'                                 ',6i3)
  126 Format(9x,'- READ FROM FILE : [ vci-w.wfn ]')

      ! ------------------------------------------------------------

      write(iout,130) nCI,Nstate
  130 Format(7x,'o VCI DIMENSION  : ',i10,/, &
             7x,'o NUM_OF_STATES  : ',i10,/)

      ! ------------------------------------------------------------

      if(lvscf) then
         write(iout,140) trim(vscfFile)
      else
         write(iout,142)
      endif
  140 Format(3x,'>> VCI WITH ZERO-POINT VSCF REFERENCE',//, &
             7x,'o READ VSCF WFN   : [ ',a,' ]',/)
  142 Format(3x,'>> VCI WITH HO REFERENCE',/)

      ! ------------------------------------------------------------

      if(dump) write(iout,150)
      if(dumpHmat) write(iout,151)
  150 Format(7x,'o DUMP VCI WFN    : [ vci-w.wfn ]',/)
  151 Format(7x,'o DUMP VCI MATRIX : [ vci-w.mat ]',/)

      ! ------------------------------------------------------------

      write(iout,160) geomAv
  160 Format(7x,'o VIB. AV. GEOM. : ',L1,/)

      ! ------------------------------------------------------------

      write(iout,170)
  170 Format(3x,'>> VCI MAIN',/)
      Call Vci_main()
      Call Vci_print()
      if(dump) Call Vci_dump()

      Call Mem_dealloc('D',size(CIwfn)+size(CIene))
      Deallocate(CIwfn,CIene)

      Call Mem_dealloc('I',size(lcup))
      Deallocate(lcup)

      Call Mem_dealloc('I',size(mm)+size(vv))
      Deallocate(mm,vv)

      Call Hmat_destruct()

      Write(iout,180)
  180 Format(/,'(  FINALIZE VCI MODULE  )',/)


   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Vci_main()

   USE Constants_mod
   USE Vci_mod

   Implicit None

   Integer :: MR,Vib_getMRforPES
   Logical :: lvscf,Vib_getlvscf
   Real(8), allocatable :: Hmat(:)

   Integer :: i,j,k,l
   Integer :: cp,zp(Nfree),lbi(Nfree),lbj(Nfree),mij(nCUP*2),vij(nCUP*2)

   Integer :: ifl

      Call Mem_alloc(-1,i,'D',nCI*(nCI+1)/2)
      Allocate(Hmat(nCI*(nCI+1)/2))

      MR=Vib_getMRforPES()
      lvscf=Vib_getlvscf()

      ! Generate Hamiltonian matrix
      write(iout,100)

      k=1
      zp=0
      Do i=1,nCI
         cp=lcup(i)
         Call getLabel(i,lbi)

         if(cp>MR) then
            Hmat(k)=0.D+00
         elseif(cp/=1) then
            Call Hmat_getHmat(cp,mm(1:cp,i),zp,lbi,Hmat(k))
         else
            if(.not. lvscf) then 
               Call Hmat_getHmat(cp,mm(1:cp,i),zp,lbi,Hmat(k))
            else
               Hmat(k)=0.D+00
            endif
         endif
         k=k+1

         Do j=2,i
            Call mvMinus(lcup(i),mm(1:lcup(i),i),vv(1:lcup(i),i), &
                         lcup(j),mm(1:lcup(j),j),vv(1:lcup(j),j), &
                         cp,mij,vij)
            Call getLabel(j,lbj)

            if(cp>MR) then
               Hmat(k)=0.D+00
            else
               Call Hmat_getHmat(cp,mij(1:cp),lbi,lbj,Hmat(k))
            endif
            k=k+1
         End do

      End do
      write(iout,200)
      Call timer(1,Iout)

      if(dumpHmat) then
         Call file_indicator(30,ifl)
         Open(ifl,file='vci-w.mat',status='unknown',form='FORMATTED')
         write(ifl,'(i10)') size(Hmat)
         write(ifl,'(5e17.8)') Hmat
         Close(ifl)
      endif

      if(.not. noDiag) then
         write(iout,110)
         Call diag(nCI,Nstate,Hmat,CIwfn,CIene)
         write(iout,200)
         Call timer(1,iout)
      endif

      Call Mem_dealloc('D',size(Hmat))
      Deallocate(Hmat)

  100 Format(6x,'o FORMING HAMILTONIAN MATRIX',$)
  110 Format(6x,'o DIAGONALIZING HAMILTONIAN MATRIX',$)
  200 Format(3x,'  ... DONE',/)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Vci_print()

   USE Constants_mod
   USE Vci_mod

   Implicit None

   Integer :: i,j,k,l
   Integer :: io,nex

   Real(8) :: E0,E
   Integer, dimension(:), allocatable :: Lbl,Label,iex
   Real(8), dimension(:), allocatable :: Ci,Wi

      Call Mem_alloc(-1,i,'D',nCI*2)
      Call Mem_alloc(-1,i,'I',nCI+Nfree)
      Allocate(Ci(nCI),Wi(nCI),Lbl(nCI),Label(Nfree),iex(Nfree))

      ! Print Ground state
      Ci=CIwfn(1:nCI)
      Do i=1,nCI
         Wi(i)=Ci(i)*Ci(i)
      End do
      Call sort(nCI,Lbl,Wi)

      E0=CIene(1)*H2wvn

      nex=lcup(Lbl(1))
      if(nex==0) then
         write(iout,200) 0
      else
         write(iout,201) 0,(mm(j,Lbl(1)),vv(j,Lbl(1)),j=1,nex)
      endif
      write(iout,110) E0
      write(iout,120)
      i=1
      Do while(Wi(i) > printWeight) 
         nex=lcup(Lbl(i))
         if(nex/=0) then 
            write(iout,210) Ci(Lbl(i)),Wi(i),(mm(j,Lbl(i)),vv(j,Lbl(i)),j=1,nex)
         else
            write(iout,211) Ci(Lbl(i)),Wi(i)
         endif
         i=i+1
      End do

      if(geomAv) Call Vci_ave(1)

      ! Print Excited state
      Do i=2,Nstate
         Ci=CIwfn(nCI*(i-1)+1:nCI*i)
         Do j=1,nCI
            Wi(j)=Ci(j)*Ci(j)
         End do
         Call sort(nCI,Lbl,Wi)

         E=CIene(i)*H2wvn
         nex=lcup(Lbl(1))
         write(iout,201) i-1,(mm(j,Lbl(1)),vv(j,Lbl(1)),j=1,nex)
         write(iout,115) E,E-E0
         write(iout,120)
         j=1
         Do while(Wi(j) > printWeight) 
            nex=lcup(Lbl(j))
            if(nex/=0) then 
               write(iout,210) Ci(Lbl(j)),Wi(j),(mm(k,Lbl(j)),vv(k,Lbl(j)),k=1,nex)
            else
               write(iout,211) Ci(Lbl(j)),Wi(j)
            endif
            j=j+1
         End do

         if(geomAv) Call Vci_ave(i)

      End do

      Call Mem_dealloc('D',size(Ci)+size(Wi))
      Call Mem_dealloc('I',size(Lbl)+size(Label))
      Deallocate(Ci,Wi,Lbl,Label)

  110 Format(/,10x,'   E(VCI)   =',f15.5)
  115 Format(/,10x,'   E(VCI)   =',f15.5,/ &
             10x,'   E(VCI)-E0=',f15.5)
  120 Format(/,10x,'   COEFF.  WEIGHT      CONFIG.') 
  200 Format(/,9x,'> STATE ',i5.5,': ZERO-POINT STATE')
  201 Format(/,9x,'> STATE ',i5.5,': ',10(i3,'_',i1,2x))
  210 Format(13x,f6.3,2x,f6.3,6x,10(i3,'_',i1,2x))
  211 Format(13x,f6.3,2x,f6.3,6x,'  0_0')

  End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vci_dump()

   USE Vci_mod

   Implicit None

   Integer :: wwf,i

      ! ------------------------------------------------------------
      ! Dump VCI wavefunction
      Call file_indicator(30,wwf)
      Open(wwf,file='vci-w.wfn',status='unknown',form='FORMATTED')

      Write(wwf,'(''VCI WAVEFUNCTION'')')
      Write(wwf,'(''THE NUMBER OF STATES AND CONFIGURATION FUNCTIONS'')')
      Write(wwf,'(2i8)') Nstate,nCI

      Write(wwf,'(''MAX NUMBER OF MODES TO EXCITE'')')
      Write(wwf,'(i8)') nCUP

      Write(wwf,'(''THE CONFIGURATION FUNCTIONS'')')
      Do i=1,nCI
         Write(wwf,'(40i4)') lcup(i),mm(1:lcup(i),i),vv(1:lcup(i),i)
      End do

      if(.not. noDiag) then
         Write(wwf,'(''THE VCI WAVEFUNCTIONS'')')
         Do i=1,Nstate
            Write(wwf,'(''STATE='',i6)') i
            Write(wwf,'(''ENERGY'')') 
            Write(wwf,'(e17.8)') CIene(i)
            Write(wwf,'(''CI COEFF.'')') 
            Write(wwf,'(5e17.8)') CIwfn(nCI*(i-1)+1:nCI*i)
         End do
      endif

      Close(wwf)


   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vci_read()

   USE Vci_mod

   Implicit None

   Integer :: wwf,i,ierr,num(100)
   Character :: line*80

      ! ------------------------------------------------------------
      ! Read VCI wavefunction
      Call file_indicator(30,wwf)
      Open(wwf,file='vci-w.wfn',status='unknown',form='FORMATTED')

      Read(wwf,*)
      Read(wwf,*)
      Read(wwf,*) Nstate,nCI
      Read(wwf,*)
      Read(wwf,*) nCUP

      Call Mem_alloc(-1,ierr,'I',nCI)
      Allocate(lcup(nCI))

      Call Mem_alloc(-1,ierr,'I',nCUP*nCI*2)
      Allocate(mm(nCUP,nCI),vv(nCUP,nCI))

      Call Mem_alloc(-1,ierr,'D',nCI*(Nstate+1))
      Allocate(CIwfn(nCI*Nstate),CIene(nCI))

      num=0
      Read(wwf,*)
      Do i=1,nCI
         Read(wwf,*) lcup(i),mm(1:lcup(i),i),vv(1:lcup(i),i)
      End do
      !Do i=1,nCI
      !   Write(6,'(40i4)') lcup(i),mm(1:lcup(i),i),vv(1:lcup(i),i)
      !End do

      Read(wwf,*,end=100)
      Do i=1,Nstate
         Read(wwf,*)
         Read(wwf,*)
         Read(wwf,*) CIene(i)
         Read(wwf,*)
         Read(wwf,*) CIwfn(nCI*(i-1)+1:nCI*i)
      End do
      !Do i=1,Nstate
      !   Write(6,'(e17.8)') CIene(i)
      !   Write(6,'(5e17.8)') CIwfn(nCI*(i-1)+1:nCI*i)
      !   Write(6,*)
      !End do

  100 Continue

      Close(wwf)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vci_destruct

   USE Vci_mod

      Call Mem_dealloc('D',size(CIwfn)+size(CIene))
      Deallocate(CIwfn,CIene)

      Call Mem_dealloc('I',size(lcup))
      Deallocate(lcup)

      Call Mem_dealloc('I',size(mm)+size(vv))
      Deallocate(mm,vv)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  ist :: an index for the current state
!
   Subroutine Vci_ave(ist)
  
   USE Constants_mod
   USE Vci_mod
  
   Implicit None
  
   Integer :: Nat,ie
   Integer :: i,j,k,l,m,n,i1,i2,n1,n2
  
   Integer :: ist,jst
   Integer :: Label1(Nfree),Label2(Nfree)
   Real(8) :: wgt,tmp,D1(maxCHO)
   Real(8) :: QQ(Nfree),quad(maxCHO,Nfree)
   Real(8), allocatable :: x0(:,:)
   Real(8), parameter :: thresh=1.D-06
   Logical :: isNMA,Mol_isNMA
  
      Do i=1,Nfree
         Call Modal_getQ(i,nCHO(i),quad(:,i))
      End do
  
      jst=(ist-1)*nCI
  
      QQ=0.D+00
      Do i=1,nCI
         wgt=CIwfn(i+jst)*CIwfn(i+jst)
         if(abs(wgt)<thresh) cycle
         Call getLabel(i,Label1)
         Do j=1,Nfree
            n=Label1(j)
            Call Modal_getXwfn(j,nCHO(j),n,n,D1)
            tmp=0.D+00
            Do k=1,nCHO(j)
               !QQ(j)=QQ(j) + wgt*quad(k,j)*D1(k)
               tmp=tmp + quad(k,j)*D1(k)
            End do
            QQ(j)=QQ(j)+tmp*wgt
         End do
      End do
  
      Do i1=1,nCI
         Call getLabel(i1,Label1)
      Do i2=1,i1-1
         Call getLabel(i2,Label2)
         wgt=2.D+00*CIwfn(i1+jst)*CIwfn(i2+jst)
         if(abs(wgt)<thresh) cycle
         Do j=1,Nfree
            Do k=1,j-1
               if(Label1(k)/=Label2(k)) goto 1000
            End do
            Do k=j+1,Nfree
               if(Label1(k)/=Label2(k)) goto 1000
            End do
  
            n1=Label1(j)
            n2=Label2(j)
            Call Modal_getXwfn(j,nCHO(j),n1,n2,D1)
            tmp=0.D+00
            Do k=1,nCHO(j)
               !QQ(j)=QQ(j) + wgt*quad(k,j)*D1(k)
               tmp=tmp + quad(k,j)*D1(k)
            End do
            QQ(j)=QQ(j)+tmp*wgt
  
            1000 Continue
         End do
      End do
      End do
  
      Write(Iout,100) 
      Write(Iout,200) QQ
  
      isNMA=Mol_isNMA()
      if(isNMA) then
         Call Mol_getNat(Nat)
         Call Mem_alloc(-1,ie,'D',3*Nat)
         Allocate(x0(3,Nat))
  
         Call nma_q2x(x0,QQ)
  
         Write(Iout,110) 
         Write(Iout,200) x0
  
         Call Mem_dealloc('D',size(x0))
         Deallocate(x0)
  
      endif
  
   100 Format(/,10x,'o VIBRATIONALLY AVERAGED STRUCTURE',//,10x,'  - Q0 -')
   110 Format(/,10x,'  - X0 -')
   200 Format(12x,3f12.6)
  
   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Vci_getNstate()

   USE Vci_mod

   Implicit None

   Integer :: Vci_getNstate

      Vci_getNstate = Nstate

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
