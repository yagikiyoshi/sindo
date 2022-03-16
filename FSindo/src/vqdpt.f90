!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/30
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Vqdpt_mod

   USE Vib_mod, only : Nfree, nCHO, maxCHO

   ! Target states
   Integer :: Nstate,tcup
   Integer, allocatable :: tmm(:),tvv(:)

   ! P-space selection parameters
   Integer :: nGen
   Real(8) :: thresh_p0,thresh_p1,thresh_p2,thresh_p3

   ! P/Q interaction
   Integer :: pqSum

   ! P space design
   !   =0 : combine only when the target states have an overlap
   !   =1 : combine when the p-space configurations have an overlap
   Integer :: pset

   ! Print option
   Real(8) :: printWeight

   ! Group information
   !   o ngrp          : The number of group
   !   o ntstgrp(ngrp) : The number of target state for each group
   !   o tstID(max(ntstgrp),ngrp) : The ID of the target state for each group
   Integer :: ngrp
   Integer, allocatable :: ntstgrp(:),tstID(:,:)

   ! P-space configurations
   !   o npCnf(ngrp)   : The number of P-space configurations for each group
   !   o maxpCUP(ngrp) : Max coupling of P-space configurations for each group
   Integer, allocatable :: npCnf(:),maxpCUP(:)
   Integer, allocatable :: pCnf_cup(:),pCnf_mm(:,:),pCnf_vv(:,:)

   ! Zero-th order energy of the P-space configurations
   Real(8), allocatable :: Ep0(:)

   ! Q-space selection parameters
   Integer :: maxSum,nCUP

   ! Q-space configuration
   Integer :: nqCnf,maxqCup,maxqCnf
   Integer, allocatable :: qCnf_cup(:),qCnf_mm(:,:),qCnf_vv(:,:)

   ! Work directory and file indicator
   Integer :: len_fw,ifw
   Character :: fw*80

   ! Threshold energy to avoid divergence / hartree
   Real(8) :: thresh_ene

   ! Runtime variables
   Integer :: current_state

   ! Dump the information of the wfn for prpt calc.
   Logical :: dump

   ! Selection of the algorithm
   Integer :: vqdpt2_loop

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vqdpt_run()

   USE Constants_mod
   USE Vqdpt_mod

   Implicit None

   Integer :: ierr
   Integer :: Vib_getMRforPES
   Integer :: Target_getNstate,Target_getMaxCup,maxCup
   Logical :: lvscf, Vib_getlvscf
   Character(12) :: vscfFile

   Integer :: i,j,k,m
   Integer :: nst,ndel,nstin
   Logical, allocatable :: ss(:)

   Namelist /vqdpt/nGen,thresh_p0,thresh_p1,thresh_p2,thresh_p3,pset,&
                   maxSum,nCUP,pqSum,thresh_ene, &
                   printWeight,dump,vqdpt2_loop

      ! ------------------------------------------------------------
      ! Read VSCF modal and setup Hmat
      lvscf = Vib_getlvscf()
      if(lvscf) then
         Call Vscf_getFilename(0,vscfFile)
         Call Modal_readVSCF(ierr,vscfFile)
      endif
      Call Hmat_construct()

      ! ------------------------------------------------------------
      ! Setup Nstate, and tmm and tvv
      Nstate=Target_getNstate()
      maxCup=Target_getMaxCup()
      Allocate(tmm(maxCup),tvv(maxCup))

      ! ------------------------------------------------------------
      ! >> Read input 

      ! --- default ---

      ! P-space selection
      nGen=3
      thresh_p0=500.0
      thresh_p1=0.1D+00
      thresh_p2=0.05D+00
      thresh_p3=0.9D+00
      pset=0

      ! Q-space selection
      maxSum=-1
      nCUP=Vib_getMRforPES()

      ! P/Q interaction
      pqSum=1

      ! Threshold energy
      thresh_ene=1.D-04

      ! Dump vqdpt.wfn
      dump=.true.

      printWeight=1.D-03

      ! Algorithm of vqdpt2
      vqdpt2_loop=0

      Rewind(inp)
      Read(inp,vqdpt,end=10)
   10 Continue

      write(iout,100)
  100 Format(/,'(  ENTER VQDPT MODULE  )',//, &
             3x,'>> VQDPT OPTIONS',/)

      ! ------------------------------------------------------------

      if(lvscf) then
         write(iout,110) vscfFile
      else
         write(iout,112)
      endif
  110 Format(7x,'o VQDPT WITH ZERO-POINT VSCF REFERENCE',/, &
             9x,'READ VSCF WFN  : ',a,/)
  112 Format(7x,'o VQDPT WITH HO REFERENCE',/)

      if(maxSum < 0) then
         write(iout,'(''  ERROR: maxSum NEEDS TO BE SPECIFIED FOR VQDPT.'')')
         write(iout,'(''  ERROR: TERMINATED WHILE SETTING UP VPT MODULE'')')
         Stop
      endif
             
      write(iout,120) nGen,thresh_p0,thresh_p1,thresh_p2,thresh_p3,pset
  120 Format(7x,'o P-SPACE CONSTRUCTION',/, &
             9x,'NGEN = ',i4,/, &
             9x,'THRESH_P0  = ',e8.2,/, &
             9x,'THRESH_P1  = ',e8.2,/, &
             9x,'THRESH_P2  = ',e8.2,/, &
             9x,'THRESH_P3  = ',e8.2,/, &
             9x,'P SET      = ',i8,/ )
      ! cm-1 -> Hartree
      thresh_p0=thresh_p0/H2wvn

      write(iout,130) nCUP,maxSum
  130 Format(7x,'o Q-SPACE CONSTRUCTION',/, &
             9x,'NCUP   = ',i4,/, &
             9x,'MAXSUM = ',i4,/)
      Call Vpt_setnCUP(nCUP)
      Call Vpt_setMaxSum(maxSum)

      write(iout,140) pqSum
  140 Format(7x,'o P/Q INTERACTION =',i4,/)

      write(iout,150) thresh_ene
  150 Format(7x,'o THRESH_ENE =',e12.3,/)

      if(dump) write(iout,160) 
  160 Format(7x,'o DUMP VQDPT WFN : [ vqdpt-w.wfn ]'/)

      ! ------------------------------------------------------------

      Call Vqdpt_main()

      ! ------------------------------------------------------------

      Call Hmat_destruct()

      write(iout,200)
  200 Format(/,'(  FINALIZE VQDPT MODULE  )',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vqdpt_main()

   USE Constants_mod
   USE Vqdpt_mod

   Implicit None

   Integer :: i,j,jj,k,kk
   Integer :: lbl(Nfree),mode(1)
   Real(8) :: Ezp_vpt2,Ezp_vpt1,Ene_vpt2,Ene_vpt1,E0,E1,E2
   Real(8), allocatable :: Hpp(:),Cpp(:,:),Ep(:)

   Character :: num*4,fname*120
   Integer :: cup,mvCUP
   Integer, allocatable :: target_idx(:)
   Integer :: wwf

      mode(1)=0
      Call GetEnv('SINDOWORK',fw)
      len_fw=Len_Trim(fw)
      if(len_fw /=0) then
         fw(len_fw+1:)='/'
         len_fw=len_fw+1
      else
         fw='./'
         len_fw=2
      endif
      Call file_indicator(10,ifw)

      write(iout,100)
  100 Format(3x,'>> ENTER VQDPT MAIN ROUTINE',/)

      Call Vqdpt_setpCnf

      write(iout,110) ngrp
  110 Format(7x,'o FOUND ',i4,' GROUPS FROM THE TARGET STATES',/)

      Call timer(1,iout)

      write(iout,120)
  120 Format(3x,' [ LOOP OVER THE GROUPS ]',/)

      if(dump) then
         Call file_indicator(30,wwf)
         Open(wwf,file='vqdpt-w.wfn',status='unknown',form='FORMATTED')
         write(wwf,'(''VQDPT WAVEFUNCTION'')')
         write(wwf,'(''THE NUMBER OF TARGET STATES'')')
         write(wwf,'(i5)') Nstate
         write(wwf,'(''THE TARGET STATES'')')
         Do i=1,Nstate
            Call Target_getConf(i,tcup,tmm,tvv)
            write(wwf,'(40i4)') tcup,tmm(1:tcup),tvv(1:tcup)
         End do
         write(wwf,'(''OPTIONS: maxSum, nCUP, pqSum, pSet'')')
         write(wwf,'(4i5)') maxSum, nCUP, pqSum, pset
         write(wwf,'(''P-SPACE CONSTRUCTION: nGen, P0, P1, P2, P3'')')
         write(wwf,'(i5,4e10.2)') nGen,thresh_p0*H2wvn,thresh_p1,thresh_p2,thresh_p3
         write(wwf,'(''THE NUMBER OF GROUPS'')')
         write(wwf,'(i5)') ngrp
         write(wwf,'(''THE NUMBER OF P-SPACE CONFIGURATIONS'')')
         write(wwf,'(5i5)') npCnf
      endif

      ! Ground state
      current_state=0
      Call Vpt_setCurrentState(current_state)
      write(iout,130) 

      lbl=0
      Call Vpt_getE0(lbl,E0)
      Call Hmat_getHmat(0,mode,lbl,lbl,E1)
      Call Vpt_getE2(lbl,E0,E2)

      E0=E0*H2wvn
      E1=E1*H2wvn - E0
      Ezp_vpt1 = E0+E1

      E2=E2*H2wvn
      Ezp_vpt2 = Ezp_vpt1+E2
      write(iout,135) E0,E1,E2,Ezp_vpt1,Ezp_vpt2
      Call timer(1,Iout)

  130 Format(7x,'o STATE 000',':   ZERO-POINT STATE',/)
  135 Format(10x,'   E(0th)    =',f15.5,/, &
             10x,'   E(1st)    =',f15.5,/, &
             10x,'   E(2nd)    =',f15.5,//, &
             10x,'   E(VMP1)   =',f15.5,/, &
             10x,'   E(VMP2)   =',f15.5,/)

      if(dump) then
         Write(wwf,'(''ZERO-POINT ENERGY'')') 
         Write(wwf,'(e25.15)') Ezp_vpt2
      endif

      ! Excited states
      Do i=1,ngrp
         current_state=i
         Call Vpt_setCurrentState(current_state)

         if(ntstgrp(i)==1) then
            jj=tstID(1,i)
            Call Target_getConf(jj,tcup,tmm,tvv)
            write(iout,140) i,(tmm(k),tvv(k),k=1,tcup)
         else
            write(iout,140) i
         endif
         write(iout,*)
     140 Format(7x,'o GROUP ',i3.3,': ',10(i3,'_',i1,2x))

         if(dump) Write(wwf,'(''GROUP='',i6)') i

         if(npCnf(i)==1) then
            ! VMP2
            jj=tstID(1,i)
            Call Target_getLabel(jj,Nfree,lbl)
            Call Vpt_getE0(lbl,E0)
            Call Hmat_getHmat(0,mode,lbl,lbl,E1)
            Call Vpt_getE2(lbl,E0,E2)

            E0=E0*H2wvn
            E1=E1*H2wvn - E0
            Ene_vpt1 = E0+E1

            E2=E2*H2wvn
            Ene_vpt2 = Ene_vpt1+E2
            write(iout,145) E0,E1,E2,Ene_vpt1,Ene_vpt2,Ene_vpt1-Ezp_vpt1,Ene_vpt2-Ezp_vpt2

            Call timer(1,Iout)
        145 Format(10x,'   E(0th)     =',f15.5,/, &
                   10x,'   E(1st)     =',f15.5,/, &
                   10x,'   E(2nd)     =',f15.5,//, &
                   10x,'   E(VMP1)    =',f15.5,/, &
                   10x,'   E(VMP2)    =',f15.5,//, &
                   10x,'   E(VMP1)-E0 =',f15.5,/, &
                   10x,'   E(VMP2)-E0 =',f15.5,/)

            if(dump) then
               write(wwf,'(''THE P-SPACE CONFIGURATIONS'')')
               Call Target_getConf(jj,tcup,tmm,tvv)
               write(wwf,'(40i4)') tcup,tmm(1:tcup),tvv(1:tcup)
               write(wwf,'(''ENERGY'')') 
               write(wwf,'(e25.15)') Ene_vpt2
            endif

         else
            ! VQDPT2
            jj=npCnf(i)
            Allocate(pCnf_cup(jj),pCnf_mm(maxpCUP(i),jj),pCnf_vv(maxpCUP(i),jj))

            write(num,'(i4.4)') i
            fname=fw(:len_fw)//'SINDO-pCnf'//num//'.dat'
            Open(ifw,file=fname,status='OLD')
            Do j=1,npCnf(i)
               read(ifw,*) pCnf_cup(j)
               read(ifw,*) pCnf_mm(1:pCnf_cup(j),j),pCnf_vv(1:pCnf_cup(j),j)
            End do
            Close(ifw,status='DELETE')

            Allocate(target_idx(ntstgrp(i)))
            Do k=1,ntstgrp(i)
               jj=tstID(k,i)
               Call Target_getConf(jj,tcup,tmm,tvv)
               Do j=1,npCnf(i)
                  cup=mvCUP(pCnf_cup(j),pCnf_mm(:,j),pCnf_vv(:,j), &
                            tcup,tmm,tvv)
                  if(cup==0) then
                     target_idx(k)=j
                     exit
                  endif
               End do
            End do
            Do j=1,ntstgrp(i)
               Do k=j+1,ntstgrp(i)
                  if(target_idx(k) < target_idx(j)) then
                     jj=target_idx(k)
                     target_idx(k)=target_idx(j)
                     target_idx(j)=jj
                  endif
               End do
            End do
            !dbg write(6,'(i4)') target_idx

            write(iout,150)
            jj=1
            Do j=1,npCnf(i)
               if(target_idx(jj)/=j) then
                  write(iout,151) j,(pCnf_mm(k,j),pCnf_vv(k,j),k=1,pCnf_cup(j))
               else
                  write(iout,152) j,(pCnf_mm(k,j),pCnf_vv(k,j),k=1,pCnf_cup(j))
                  jj=jj+1
                  if(jj>ntstgrp(i)) then 
                     jj=j+1
                     exit
                  endif
               endif
            End do
            Do j=jj,npCnf(i)
               write(iout,151) j,(pCnf_mm(k,j),pCnf_vv(k,j),k=1,pCnf_cup(j))
            End do
            write(iout,153)

            jj=npCnf(i)
            Call Mem_alloc(-1,k,'D',jj*(jj+1)/2 + jj*jj + jj)
            Allocate(Hpp(jj*(jj+1)/2),Cpp(jj,jj),Ep(jj))

            Call Vqdpt_getE1(jj,Hpp)
            Call Vqdpt_getE2(jj,Hpp)
            Call diag(jj,jj,Hpp,Cpp,Ep)
            Ep=Ep*H2wvn

            Call Vqdpt_print(jj,ntstgrp(i),target_idx,Ezp_vpt2,Cpp,Ep)
            Call timer(1,Iout)

            if(dump) then
               Write(wwf,'(''THE NUMBER OF CONFIGURATION FUNCTIONS'')')
               Write(wwf,'(i4)') jj
               Write(wwf,'(''THE NUMBER OF TARGET STATES AND THEIR INDEX'')')
               Write(wwf,'(i7)') ntstgrp(i)
               Write(wwf,'(40i7)') target_idx(1:ntstgrp(i))
               Write(wwf,'(''THE P-SPACE CONFIGURATIONS'')')
               Do j=1,jj
                  Write(wwf,'(40i4)') &
                     pCnf_cup(j),pCnf_mm(1:pCnf_cup(j),j),pCnf_vv(1:pCnf_cup(j),j)
               End do
 
               Do j=1,jj
                  Write(wwf,'(''STATE='',i6)') j
                  Write(wwf,'(''ENERGY'')') 
                  Write(wwf,'(e25.15)') Ep(j)
                  Write(wwf,'(''CI COEFF.'')') 
                  Write(wwf,'(5e17.8)') Cpp(:,j)
               End do
            endif

            Call Mem_dealloc('D',size(Hpp)+size(Cpp)+size(Ep))
            Deallocate(Hpp,Cpp,Ep)

            Deallocate(pCnf_cup,pCnf_mm,pCnf_vv)
            Deallocate(target_idx)

         endif

      End do
  150 Format(10x,'--- P-SPACE COMPONENTS -----------------------------------------')
  151 Format(12x,i4,') ',10(i3,'_',i1,2x))
  152 Format(10x,'* ',i4,') ',10(i3,'_',i1,2x))
  153 Format(10x,'----------------------------------------------------------------',/, &
             10x,'                                           * IS THE TARGET STATE',/)

      if(dump) Close(wwf)

      if(allocated(ntstgrp)) then
         Call Mem_dealloc('I',size(ntstgrp))
         Deallocate(ntstgrp)
      endif
      if(allocated(tstID)) then
         Call Mem_dealloc('I',size(tstID))
         Deallocate(tstID)
      endif
      if(allocated(npCnf)) then
         Call Mem_dealloc('I',size(npCnf))
         Deallocate(npCnf)
      endif
      if(allocated(maxpCUP)) then
         Call Mem_dealloc('I',size(maxpCUP))
         Deallocate(maxpCUP)
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_print(nCI,nst,idx,E0,Cpp,CIene)

   USE Constants_mod
   USE Vqdpt_mod

   Implicit None

   Integer :: nCI,nst,idx(nst)
   Real(8) :: E0,Cpp(nCI,nCI),CIene(nCI)

   Integer :: i,j,k,nex
   Real(8) :: E
   Integer, allocatable :: lbl(:)
   Real(8), allocatable :: Wi(:)

      Allocate(lbl(nCI),Wi(nCI))

      ! Print CI state
      Do i=1,nCI
         Wi=Cpp(:,i)
         Do j=1,nCI
            Wi(j)=Wi(j)*Wi(j)
         End do

         k=-1
         Do j=1,nst
            if(Wi(idx(j))>printWeight) then
               k=0
            endif
         End do
         if(k==-1) cycle

         Call sort(nCI,Lbl,Wi)

         E=CIene(i)
         nex=pCnf_cup(Lbl(1))
         write(iout,100) i,(pCnf_mm(j,Lbl(1)),pCnf_vv(j,Lbl(1)),j=1,nex)
         write(iout,*)
         write(iout,110) E,E-E0
         write(iout,120)

         Do j=1,nCI
            if(Wi(j) < printWeight) exit
            nex=pCnf_cup(Lbl(j))
            if(nex/=0) then 
               write(iout,130) Cpp(Lbl(j),i),Wi(j),&
                  (pCnf_mm(k,Lbl(j)),pCnf_vv(k,Lbl(j)),k=1,nex)
            else
               write(iout,131) Cpp(Lbl(j),i),Wi(j)
            endif

         End do
         write(iout,*)

      End do
      write(iout,*)

      Deallocate(lbl,Wi)

  100 Format(9x,'> STATE ',i3.3,': ',10(i3,'_',i1,2x))
  110 Format(10x,'   E(VQDPT2)   =',f15.5,/ &
             10x,'   E(VQDPT2)-E0=',f15.5,/)
  120 Format(10x,'   COEFF.  WEIGHT      CONFIG.',/) 
  130 Format(13x,f6.3,2x,f6.3,6x,10(i3,'_',i1,2x))
  131 Format(13x,f6.3,2x,f6.3,6x,'  0_0')

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vqdpt_setpCnf()

   USE Constants_mod
   USE Vqdpt_mod

   Implicit None

   Integer :: maxCup,Target_getMaxCup
   Integer :: npCnfst(Nstate),maxpCupst(Nstate),grpID(Nstate)
   Logical :: ovlp(Nstate,Nstate)

   Character :: num*4,fname*120,fname2*120

   Integer, allocatable :: pcup(:),pmm(:,:),pvv(:,:)

   Integer :: nCnf,jsave,k0,k1,cup,mvCUP
   Logical :: ex

   Logical, allocatable :: lprune(:)

   Integer :: i,j,k,l,m
   Integer :: ii

      maxCup=nGen*(Target_getMaxCup()+nCUP)
      Call Mem_alloc(-1,i,'I',maxpCnf)
      Call Mem_alloc(-1,i,'I',maxCup*maxpCnf*2)
      Allocate(pCnf_cup(maxpCnf))
      Allocate(pCnf_mm(maxCup,maxpCnf))
      Allocate(pCnf_vv(maxCup,maxpCnf))

      Call Mem_alloc(-1,i,'I',maxpCnf)
      Call Mem_alloc(-1,i,'I',maxCup*maxpCnf*2)
      Allocate(pcup(maxpCnf))
      Allocate(pmm(maxCup,maxpCnf))
      Allocate(pvv(maxCup,maxpCnf))

      ovlp=.false.
      Do i=1,Nstate
         !dbg write(iout,'(''state='',i4)') i

         Call Target_getConf(i,tcup,tmm,tvv)
         pCnf_cup(1)=tcup
         pCnf_mm(1:pCnf_cup(1),1)=tmm(1:tcup)
         pCnf_vv(1:pCnf_cup(1),1)=tvv(1:tcup)

         npCnfst(i)=1
         maxpCupst(i)=tcup
         k0=1; k1=1

         Do j=1,nGen

            !dbg write(iout,'(''nGen='',i4,'' k0='',i4,'' k1=''i4)') j,k0,k1
            jsave=npCnfst(i)

            Do k=k0,k1
               l=maxpCnf
               Call Vqdpt_getpCnf(pCnf_cup(k),pCnf_mm(:,k),pCnf_vv(:,k), &
                                  maxCup,l,nCnf,pcup,pmm,pvv)
               if(nCnf==1) cycle
               !dbg Do l=1,nCnf
               !dbg    write(iout,'(''mm='',10i4)') pmm(1:pcup(l),l)
               !dbg    write(iout,'(''vv='',10i4)') pvv(1:pcup(l),l)
               !dbg End do
               !dbg write(iout,*)

               Allocate(lprune(nCnf))
               lprune=.true.

               ! Should we do vciprune for j=1?
               if(j /= 1) Call Vqdpt_vciprune(maxCup,nCnf,pcup,pmm,pvv,lprune)
               !Call Vqdpt_vciprune(maxCup,nCnf,pcup,pmm,pvv,lprune)

               Do l=2,nCnf
                  if(.not. lprune(l)) cycle

                  ! Check if the new conf. already exists
                  ex=.false.
                  Do m=1,npCnfst(i)
                     if(pCnf_cup(m)==pcup(l)) then
                        cup= mvCUP(pCnf_cup(m), &
                                   pCnf_mm(1:pCnf_cup(m),m), &
                                   pCnf_vv(1:pCnf_cup(m),m), &
                                   pcup(l), &
                                   pmm(1:pcup(l),l), &
                                   pvv(1:pcup(l),l))
                        if(cup==0) then
                           ex=.true.
                           exit
                        endif
                     endif
                  End do
                  if(ex) cycle

                  ! Now add the configuration
                  npCnfst(i)=npCnfst(i)+1
                  pCnf_cup(npCnfst(i))=pcup(l)
                  pCnf_mm(1:pcup(l),npCnfst(i))=pmm(1:pcup(l),l)
                  pCnf_vv(1:pcup(l),npCnfst(i))=pvv(1:pcup(l),l)
                  if(maxpCupst(i)<pcup(l)) maxpCupst(i)=pcup(l)
               End do

               !dbg write(iout,'(3i4,/)') nCnf-1,jsave,npCnfst(i)

               Deallocate(lprune)

            End do

            if(npCnfst(i) /= jsave) then
               k0=jsave+1
               k1=npCnfst(i)
            else
               exit
            endif

         End do

         if(npCnfst(i)>1) then 
            write(num,'(i4.4)') i
            fname=fw(:len_fw)//'SINDO-target'//num//'.dat'
            Open(ifw,file=fname,status='REPLACE')
            Do j=1,npCnfst(i)
               write(ifw,100) pCnf_cup(j)
               write(ifw,100) pCnf_mm(1:pCnf_cup(j),j),pCnf_vv(1:pCnf_cup(j),j)
            End do
            Close(ifw)
        100 Format(100i4)

         endif

         if(pset == 0) then
            !Do j=1,i-1
            Do j=1,Nstate
               Call Target_getConf(j,tcup,tmm,tvv)
               Do l=1,npCnfst(i)
                  cup=mvCUP(tcup,tmm,tvv,pCnf_cup(l),pCnf_mm(:,l),pCnf_vv(:,l))
                  if(cup==0) then
                     ovlp(j,i)=.true.
                     ovlp(i,j)=.true.
                     exit
                  endif
               End do
            End do

         else
            !Do j=1,i-1
            Do j=1,Nstate
               if(npCnfst(j)>1) then
                  write(num,'(i4.4)') j
                  fname=fw(:len_fw)//'SINDO-target'//num//'.dat'
                  Open(ifw,file=fname,status='OLD')
                  Do k=1,npCnfst(j)
                     read(ifw,100) pcup(1)
                     read(ifw,100) pmm(1:pcup(1),1),pvv(1:pcup(1),1)
                     Do l=1,npCnfst(i)
                        cup=mvCUP(pcup(1),pmm(:,1),pvv(:,1), &
                                  pCnf_cup(l),pCnf_mm(:,l),pCnf_vv(:,l))
                        if(cup==0) then
                           ovlp(j,i)=.true.
                           ovlp(i,j)=.true.
                           exit
                        endif
      
                     End do
                     if(ovlp(j,i)) exit
      
                  End do
                  close(ifw)
  
               else
                  Call Target_getConf(j,tcup,tmm,tvv)
                  Do l=1,npCnfst(i)
                     cup=mvCUP(tcup,tmm,tvv,pCnf_cup(l),pCnf_mm(:,l),pCnf_vv(:,l))
                     if(cup==0) then
                        ovlp(j,i)=.true.
                        ovlp(i,j)=.true.
                        exit
                     endif
                  End do
  
               endif
  
            End do

         endif

      End do
      !dbg write(6,'(5i4)') npCnfst
      !dbg write(6,'(5i4)') maxpCupst
      !dbg write(6,*)
      !dbg Do i=1,Nstate
      !dbg    write(6,'(i3)') i
      !dbg    write(6,'(20l3)') ovlp(:,i)
      !dbg End do

      ngrp=0
      grpID=-1
      Do i=1,Nstate
         if(grpID(i) > 0) cycle
         ngrp=ngrp+1
         grpID(i)=0

         Do while(.true.)

            ii=-1
            Do j=i,Nstate
               if(grpID(j) == 0) then
                  grpID(j)=ngrp
                  ii=j
                  exit
               endif
            End do
            if(ii < 0) exit
  
            Do j=i+1,Nstate
               if(ovlp(j,ii) .and. grpID(j) < 0) then
                  grpID(j)=0
               endif
            End do

         End do

      End do
      !dbg write(6,'(i4)') ngrp
      !dbg write(6,'(5i4)') grpID

      Call Mem_alloc(-1,i,'I',ngrp)
      Allocate(ntstgrp(ngrp))
      ntstgrp=0
      Do i=1,Nstate
         ntstgrp(grpID(i))=ntstgrp(grpID(i))+1
      End do
      i=maxval(ntstgrp)
      !dbg write(6,'(5i4)') ntstgrp

      Call Mem_alloc(-1,j,'I',i*ngrp)
      Allocate(tstID(i,ngrp))
      tstID=-1
      Do i=1,Nstate
         j=1
         Do while(tstID(j,grpID(i))>0)
            j=j+1
         End do
         tstID(j,grpID(i))=i
      End do
      !dbg Do j=1,ngrp
      !dbg    write(6,'(i3,'':'',10i3)') j,tstID(1:ntstgrp(j),j)
      !dbg End do
      !dbg write(6,*)

      Call Mem_alloc(-1,i,'I',ngrp*2)
      Allocate(npCnf(ngrp),maxpCUP(ngrp))
      Do i=1,ngrp
         if(ntstgrp(i)==1) then
            if(npCnfst(tstID(1,i))>1) then
               write(num,'(i4.4)') tstID(1,i)
               fname=fw(:len_fw)//'SINDO-target'//num//'.dat'
               write(num,'(i4.4)') i
               fname2=fw(:len_fw)//'SINDO-pCnf'//num//'.dat'
               Call System('mv '//trim(fname)//' '//trim(fname2))
            endif
            npCnf(i)=npCnfst(tstID(1,i))
            maxpCUP(i)=maxpCupst(tstID(1,i))

         else
            npCnf(i)=0
            Do j=1,ntstgrp(i)
               write(num,'(i4.4)') tstID(j,i)
               fname=fw(:len_fw)//'SINDO-target'//num//'.dat'
               Open(ifw,file=fname,status='OLD')

               Do k=1,npCnfst(tstID(j,i))
                  read(ifw,100) pcup(1)
                  read(ifw,100) pmm(1:pcup(1),1),pvv(1:pcup(1),1)

                  k0=0
                  Do l=1,npCnf(i)
                     cup=mvCUP(pcup(1),pmm,pvv,pCnf_cup(l),pCnf_mm(:,l),pCnf_vv(:,l))
                     if(cup==0) then
                        k0=-1
                        exit
                     endif
                  End do
                  if(k0==0) then
                     npCnf(i)=npCnf(i)+1
                     pCnf_cup(npCnf(i))=pcup(1)
                     pCnf_mm(1:pcup(1),npCnf(i))=pmm(1:pcup(1),1)
                     pCnf_vv(1:pcup(1),npCnf(i))=pvv(1:pcup(1),1)
                  endif

               End do
               Close(ifw,status='DELETE')

            End do

            write(num,'(i4.4)') i
            fname2=fw(:len_fw)//'SINDO-pCnf'//num//'.dat'
            Open(ifw,file=fname2,status='REPLACE')

            maxpCUP(i)=0
            Do j=1,npCnf(i)
               write(ifw,100) pCnf_cup(j)
               write(ifw,100) pCnf_mm(1:pCnf_cup(j),j),pCnf_vv(1:pCnf_cup(j),j)
               if(maxpCUP(i) < pCnf_cup(j)) maxpCUP(i)=pCnf_cup(j)
            End do

            Close(ifw)

            !dbg write(6,'(''GROUP='',i4)') i
            !dbg Do j=1,npCnf(i)
            !dbg    k0=0
            !dbg    Do k=1,Nstate
            !dbg       Call Target_getConf(k,tcup,tmm,tvv)
            !dbg       cup=mvCUP(tcup,tmm,tvv,pCnf_cup(j),pCnf_mm(:,j),pCnf_vv(:,j))
            !dbg       if(cup==0) then
            !dbg         k0=-1
            !dbg         exit
            !dbg       endif
            !dbg    End do

            !dbg    if(k0==0) then
            !dbg       write(6,'(8x,i4,4x,10(i3,''_'',i1,2x))') & 
            !dbg                 j,(pCnf_mm(k,j),pCnf_vv(k,j),k=1,pCnf_cup(j))
            !dbg    else
            !dbg       write(6,'(2x,''* '',4x,i4,4x,10(i3,''_'',i1,2x))') & 
            !dbg                 j,(pCnf_mm(k,j),pCnf_vv(k,j),k=1,pCnf_cup(j))
            !dbg    endif
            !dbg End do

         endif
      End do
      !dbg write(6,'(5i4)') npCnf
      !dbg write(6,'(5i4)') maxpCUP

      Call Mem_dealloc('I',maxpCnf)
      Call Mem_dealloc('I',maxCup*maxpCnf*2)
      Deallocate(pcup,pmm,pvv)
      Call Mem_dealloc('I',maxpCnf)
      Call Mem_dealloc('I',maxCup*maxpCnf*2)
      Deallocate(pCnf_cup,pCnf_mm,pCnf_vv)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vqdpt_getpCnf(icup,imm,ivv,maxCup,maxpCnf,nCnf,pcup,pmm,pvv)

   USE Constants_mod, only : iout
   USE Vqdpt_mod
   USE PES_mod

   Implicit None

   Integer :: icup,imm(icup),ivv(icup)
   Integer :: maxCup,maxpCnf,nCnf
   Integer :: pcup(maxpCnf),pmm(maxCup,maxpCnf),pvv(maxCup,maxpCnf)

   Integer :: m1,m2,m3,m4
   Integer :: vv,v1,v2,v3,v4
   Integer :: lbli(Nfree),lblj(Nfree)
   Real(8) :: modal_ene(0:maxCHO-1,4)

   Integer :: i,n,mm(4)

      lbli=0
      Do i=1,icup
         lbli(imm(i))=ivv(i)
      End do
      !dbg write(6,'(6i3)') lbli

      lblj=lbli

      nCnf=1
      pcup(1)=icup
      pmm(1:icup,1)=imm
      pvv(1:icup,1)=ivv

      ! 2-mode exc.
      Do n=1,nQ2
         m1=mQ2(1,n)
         m2=mQ2(2,n)
         Call two_modeExc
      End do
      Do n=1,nS2
         m1=mS2(1,n)
         m2=mS2(2,n)
         Call two_modeExc
      End do
      if(nCUP==2 .or. maxSum==2) goto 100

      ! 3-mode exc.
      Do n=1,nQ3
         m1=mQ3(1,n)
         m2=mQ3(2,n)
         m3=mQ3(3,n)
         Call three_modeExc
      End do
      Do n=1,nS3
         m1=mS3(1,n)
         m2=mS3(2,n)
         m3=mS3(3,n)
         Call three_modeExc
      End do
      if(nCUP==3 .or. maxSum==3) goto 100

      ! 4-mode exc.
      Do n=1,nQ4
         m1=mQ4(1,n)
         m2=mQ4(2,n)
         m3=mQ4(3,n)
         m4=mQ4(4,n)
         Call four_modeExc
      End do
      Do n=1,nS4
         m1=mS4(1,n)
         m2=mS4(2,n)
         m3=mS4(3,n)
         m4=mS4(4,n)
         Call four_modeExc
      End do
      if(nCUP==4 .or. maxSum==4) goto 100

  100 Continue

      Contains

      Subroutine two_modeExc

      Implicit None

         if(lbli(m1)==0 .and. lbli(m2)==0) return

         Call Modal_getEne(m1,modal_ene(0:nCHO(m1)-1,1))
         Call Modal_getEne(m2,modal_ene(0:nCHO(m2)-1,2))

         Do vv=2,maxSum
         Do v1=1,vv-1
            v2=vv-v1

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0) Call search2

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2)) Call search2

         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)

      End subroutine

      Subroutine search2

      Implicit None

      Real(8) :: dE,Hij

         dE = modal_ene(lbli(m1),1) - modal_ene(lblj(m1),1) &
            + modal_ene(lbli(m2),2) - modal_ene(lblj(m2),2)

         if(abs(dE) > thresh_p0) return

         mm(1)=m2
         mm(2)=m1
         Call Hmat_getHmat(2,mm,lbli,lblj,Hij)
         Hij=abs(Hij/de)

         if(Hij < thresh_p1) return

         Call addCnf(2)

         !dbg write(6,'(2i4)') m1,m2
         !dbg write(6,'(2i4,''  |'',2i4)') &
         !dbg   lbli(m1),lbli(m2),lblj(m1),lblj(m2)
         !dbg write(6,'(f12.4)') dE*H2wvn
         !dbg write(6,'(f12.4)') Hij
         !dbg write(6,*)

      End subroutine

      Subroutine three_modeExc

      Implicit None

         if(lbli(m1)==0 .and. lbli(m2)==0 .and. lbli(m3)==0) return

         Call Modal_getEne(m1,modal_ene(0:nCHO(m1)-1,1))
         Call Modal_getEne(m2,modal_ene(0:nCHO(m2)-1,2))
         Call Modal_getEne(m3,modal_ene(0:nCHO(m3)-1,3))

         Do vv=3,maxSum
         Do v1=1,vv-1
         Do v2=1,vv-v1-1
            v3=vv-v1-v2

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) &
                             .and. lblj(m3) < nCHO(m3)) Call search3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 &
                                   .and. lblj(m3) < nCHO(m3)) Call search3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) &
                                   .and. lblj(m3) >= 0) Call search3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 &
                             .and. lblj(m3) < nCHO(m3)) Call search3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) &
                             .and. lblj(m3) >= 0) Call search3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 &
                                   .and. lblj(m3) >= 0) Call search3

         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)

      End subroutine

      Subroutine search3

      Implicit None

      Real(8) :: dE,Hij

         dE = modal_ene(lbli(m1),1) - modal_ene(lblj(m1),1) &
            + modal_ene(lbli(m2),2) - modal_ene(lblj(m2),2) &
            + modal_ene(lbli(m3),3) - modal_ene(lblj(m3),3)

         if(abs(dE) > thresh_p0) return

         mm(1)=m3
         mm(2)=m2
         mm(3)=m1
         Call Hmat_getHmat(3,mm(1:3),lbli,lblj,Hij)
         Hij=abs(Hij/de)

         if(Hij < thresh_p1) return

         Call addCnf(3)

         !dbg write(6,'(3i4)') m1,m2,m3
         !dbg write(6,'(3i4,''  |'',3i4)') &
         !dbg   lbli(m1),lbli(m2),lbli(m3),lblj(m1),lblj(m2),lblj(m3)
         !dbg write(6,'(f12.4)') dE*H2wvn
         !dbg write(6,'(f12.4)') Hij
         !dbg write(6,*)

      End subroutine

      Subroutine four_modeExc

      Implicit None

         if(lbli(m1)==0 .and. lbli(m2)==0 .and. &
            lbli(m3)==0 .and. lbli(m4)==0) return

         Call Modal_getEne(m1,modal_ene(0:nCHO(m1)-1,1))
         Call Modal_getEne(m2,modal_ene(0:nCHO(m2)-1,2))
         Call Modal_getEne(m3,modal_ene(0:nCHO(m3)-1,3))
         Call Modal_getEne(m4,modal_ene(0:nCHO(m4)-1,4))

         Do vv=4,maxSum
         Do v1=1,vv-1
         Do v2=1,vv-v1-1
         Do v3=1,vv-v1-v2-1
            v4=vv-v1-v2-v3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call search4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call search4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call search4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call search4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call search4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call search4

         End do
         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)
         lblj(m4)=lbli(m4)

      End subroutine

      Subroutine search4

      Implicit None

      Real(8) :: dE,Hij

         dE = modal_ene(lbli(m1),1) - modal_ene(lblj(m1),1) &
            + modal_ene(lbli(m2),2) - modal_ene(lblj(m2),2) &
            + modal_ene(lbli(m3),3) - modal_ene(lblj(m3),3) &
            + modal_ene(lbli(m4),4) - modal_ene(lblj(m4),4)
!            + modal_ene(lbli(m3),4) - modal_ene(lblj(m3),4)

         if(abs(dE) > thresh_p0) return

         mm(1)=m4
         mm(2)=m3
         mm(3)=m2
         mm(4)=m1
         Call Hmat_getHmat(4,mm,lbli,lblj,Hij)
         Hij=abs(Hij/de)

         if(Hij < thresh_p1) return

         Call addCnf(4)

      End subroutine

      Subroutine addCnf(ii)

      Implicit None

      Integer :: ii,i
      Integer :: vv(ii),jcup,jmm(icup+ii),jvv(icup+ii)

         nCnf=nCnf+1
         if(nCnf > maxpCnf) then
            Write(iout,*) 'ERROR:'
            Write(iout,*) 'ERROR: EXCEEDED MAXIMUM NUMBER OF P-SPACE CONFIGURATION'
            Write(iout,*) 'ERROR: RESET maxpCnf'
            Write(iout,*) 'ERROR:'
            Write(iout,*)
            Stop
         endif

         !dbg write(iout,'(''  -  '',6i3)') lbli
         !dbg write(iout,'(''  im '',6i3)') imm(1:icup)
         !dbg write(iout,'(''  iv '',6i3)') ivv(1:icup)

         Do i=1,ii
            vv(i)=lblj(mm(i))-lbli(mm(i))
         End do
         !dbg write(iout,'(''  -  '',6i3)') lblj
         !dbg write(iout,'(''  mm '',6i3)') mm(1:ii)
         !dbg write(iout,'(''  vv '',6i3)') vv(1:ii)

         Call mvPlus(icup,imm,ivv,ii,mm,vv,jcup,jmm,jvv)

         !dbg write(iout,'(''  mm3'',6i3)') jmm(1:jcup)
         !dbg write(iout,'(''  vv3'',6i3)') jvv(1:jcup)

         pcup(nCnf)=jcup
         Do i=1,jcup
            pmm(i,nCnf)=jmm(i)
            pvv(i,nCnf)=jvv(i)
         End do

      End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vqdpt_vciprune(maxCup,nCnf,pcup,pmm,pvv,lprune)

   USE Constants_mod
   USE Vqdpt_mod

   Implicit None

   Integer :: maxCup,nCnf
   Integer :: pcup(maxpCnf),pmm(maxCup,nCnf),pvv(maxCup,nCnf)
   Logical :: lprune(nCnf)

   Integer :: i,j,k,ic
   Integer :: lbli(Nfree),lblj(Nfree)
   Integer :: cup,mm(maxCup),vv(maxCup)
   Real(8) :: Hmat(nCnf*(nCnf+1)/2),Ene(nCnf),Cw(nCnf,nCnf)

   Logical :: lcheck(nCnf)

      lbli=0
      lblj=0

      k=1
      Do i=1,nCnf
         Do ic=1,pcup(i) 
            lbli(pmm(ic,i))=pvv(ic,i)
         End do

         Do j=1,i-1
            Do ic=1,pcup(j) 
               lblj(pmm(ic,j))=pvv(ic,j)
            End do

            Call mvMinus(pcup(i),pmm(:,i),pvv(:,i), &
                         pcup(j),pmm(:,j),pvv(:,j), &
                         cup,mm,vv)
            Call Hmat_getHmat(cup,mm(1:cup),lbli,lblj,Hmat(k))
            k=k+1

            Do ic=1,pcup(j) 
               lblj(pmm(ic,j))=0
            End do
         End do

         Call Hmat_getHmat(0,mm(1:1),lbli,lbli,Hmat(k))
         k=k+1

         Do ic=1,pcup(i) 
            lbli(pmm(ic,i))=0
         End do

      End do

      Call diag(nCnf,nCnf,Hmat,Cw,Ene)

      Do i=1,nCnf
      Do j=1,nCnf
         Cw(j,i)=Cw(j,i)*Cw(j,i)
      End do
      End do
      !dbg write(iout,'(i4)') nCnf
      !dbg write(iout,'(f12.2)') Ene*H2wvn
      !dbg Do i=1,nCnf
      !dbg    write(iout,'(100f12.6)') Cw(i,:)
      !dbg End do

      lprune=.false.
      lprune(1)=.true.
      lcheck=.false.

  100 Continue
      Do i=1,nCnf
         if(lprune(i) .and. .not. lcheck(i)) then
            Do j=1,nCnf
               if(Cw(i,j)>thresh_p2) then
                  Do k=1,nCnf
                     if(.not. lprune(k) .and. Cw(k,j)>thresh_p2) lprune(k)=.true.
                  End do
               endif
            End do
            lcheck(i)=.true.
            goto 100
         endif
      End do
      !dbg write(iout,'(10l3)') lprune

      Do i=1,nCnf
         if(.not. lprune(i)) cycle
         Do j=1,nCnf
            if(Cw(i,j)>thresh_p3) then
               lprune(i)=.false.
            endif
         End do
      End do
      !dbg write(iout,'(10l3)') lprune

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_getE1(nn,Hmat)

   USE Vqdpt_mod

   Implicit None

   Integer :: nn
   Real(8) :: Hmat(nn*(nn+1)/2)

   Integer :: i,j,ij,k
   Integer :: lbli(Nfree),lblj(Nfree)
   Integer :: cp,mij(maxpCUP(current_state)*2),vij(maxpCUP(current_state)*2)

      lbli=0
      lblj=0

      ij=1
      Do i=1,nn
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=pCnf_vv(k,i)
         End do
         !dbg write(6,'(6i2)') lbli

         Do j=1,i-1
            Do k=1,pCnf_cup(j)
               lblj(pCnf_mm(k,j))=pCnf_vv(k,j)
            End do
            !dbg write(6,'(3x,6i2)') lblj
            Call mvMinus(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                         pCnf_cup(j),pCnf_mm(:,j),pCnf_vv(:,j), &
                         cp,mij,vij)
            Call Hmat_getHmat(cp,mij,lbli,lblj,Hmat(ij))

            ij=ij+1
            Do k=1,pCnf_cup(j)
               lblj(pCnf_mm(k,j))=0
            End do
         End do

         Call Hmat_getHmat(0,mij(1:1),lbli,lbli,Hmat(ij))

         ij=ij+1
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=0
         End do

      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_getE2(nn,Hmat)

   USE Constants_mod
   USE PES_mod
   USE Vqdpt_mod

   Implicit None

   Integer :: nn
   Real(8) :: Hmat(nn*(nn+1)/2)

   Integer :: fl,kl,l,ierr
   Integer :: i,k,lbli(Nfree)

      ! Calc. the zero-th order energy
      Call Mem_alloc(-1,i,'D',nn)
      Allocate(Ep0(nn))

      lbli=0
      Do i=1,nn
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=pCnf_vv(k,i)
         End do
         Call Vpt_getE0(lbli,Ep0(i))
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=0
         End do
      End do

      maxqCup=maxpCUP(current_state)+nCUP
      kl=1

      l=1
      fl=nQ1+nS1
      kl=(kl*(maxSum-l+1))/l
      maxqCnf=fl*kl*(2**l)
      if(nCUP==1) goto 10

      l=2
      fl=nQ2+nS2
      kl=(kl*(maxSum-l+1))/l
      maxqCnf=maxqCnf + fl*kl*(2**l)
      if(nCUP==2) goto 10

      l=3
      fl=nQ3+nS3
      kl=(kl*(maxSum-l+1))/l
      maxqCnf=maxqCnf + fl*kl*(2**l)
      if(nCUP==3) goto 10

      l=4
      fl=nQ4+nS4
      kl=(kl*(maxSum-l+1))/l
      maxqCnf=maxqCnf + fl*kl*(2**l)
   10 Continue
      !write(6,'(i8)') maxqCnf
      !write(6,'(i6)') maxqCup

      Call Mem_alloc(-1,ierr,'I',maxqCnf + maxqCup*maxqCnf*2)
      Allocate(qCnf_cup(maxqCnf),qCnf_mm(maxqCup,maxqCnf),qCnf_vv(maxqCup,maxqCnf))

      if(vqdpt2_loop == 0) then
         if(pqSum > 0) then
            Call vqdpt_getE2_p1(nn,Hmat)
         else
            Call vqdpt_getE2_m1(nn,Hmat)
         endif
         write(iout,100) nqCnf

      elseif(vqdpt2_loop == 1) then
         if(pqSum > 0) then
            Call vqdpt_getE2_p1_lp1(nn,Hmat)
         else
            Call vqdpt_setqCnf()
            write(iout,100) nqCnf
            Call timer(1,iout)
            Call vqdpt_getE2_m1_lp1(nn,Hmat)
         endif
      endif

  100 Format(9x,'o Q-SPACE COMPONENTS: ',i12,/)
      Call timer(1,iout)

      Call Mem_dealloc('D',size(qCnf_cup)+size(qCnf_mm)+size(qCnf_vv))
      Deallocate(qCnf_cup,qCnf_mm,qCnf_vv)

      Call Mem_dealloc('D',size(Ep0))
      Deallocate(Ep0)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Vqdpt_getqCnf(icup,imm,ivv,maxCup,maxCnf,nCnf,qcup,qmm,qvv)

   USE Constants_mod, only : iout
   USE Vqdpt_mod
   USE PES_mod

   Implicit None

   Integer :: icup,imm(icup),ivv(icup)
   Integer :: maxCup,maxCnf,nCnf
   Integer :: qcup(maxCnf),qmm(maxCup,maxCnf),qvv(maxCup,maxCnf)

   Integer :: m1,m2,m3,m4,mm(4)
   Integer :: vv,v1,v2,v3,v4,q1,q2,q3,q4,q1a,q1b
   Integer :: lbli(Nfree),lblj(Nfree)
   Integer :: i,n

      nCnf=0

      lbli=0
      Do i=1,icup
         lbli(imm(i))=ivv(i)
      End do
      !dbg write(6,'(6i1)') lbli

      lblj=lbli

      ! 1-mode exc.
      Do m1=1,Nfree
         q1a=lbli(m1)-maxSum
         if(q1a < 0) q1a=0
         q1b=lbli(m1)+maxSum
         if(q1b >= nCHO(m1)) q1b=nCHO(m1)-1

         mm(1)=m1
         Do q1=q1a,q1b
            if(q1==lbli(m1)) cycle

            lblj(m1)=q1
            Call addCnf(1)

         End do
         lblj(m1)=lbli(m1)

      End do
      if(nCUP==1 .or. maxSum==1) goto 100

      ! 2-mode exc.
      Do n=1,nQ2
         m1=mQ2(1,n)
         m2=mQ2(2,n)
         mm(1)=m2
         mm(2)=m1
         Call two_modeExc
      End do
      Do n=1,nS2
         m1=mS2(1,n)
         m2=mS2(2,n)
         mm(1)=m2
         mm(2)=m1
         Call two_modeExc
      End do
      if(nCUP==2 .or. maxSum==2) goto 100

      ! 3-mode exc.
      Do n=1,nQ3
         m1=mQ3(1,n)
         m2=mQ3(2,n)
         m3=mQ3(3,n)
         mm(1)=m3
         mm(2)=m2
         mm(3)=m1
         Call three_modeExc
      End do
      Do n=1,nS3
         m1=mS3(1,n)
         m2=mS3(2,n)
         m3=mS3(3,n)
         mm(1)=m3
         mm(2)=m2
         mm(3)=m1
         Call three_modeExc
      End do
      if(nCUP==3 .or. maxSum==3) goto 100

      ! 4-mode exc.
      Do n=1,nQ4
         m1=mQ4(1,n)
         m2=mQ4(2,n)
         m3=mQ4(3,n)
         m4=mQ4(4,n)
         mm(1)=m4
         mm(2)=m3
         mm(3)=m2
         mm(4)=m1
         Call four_modeExc
      End do
      Do n=1,nS4
         m1=mS4(1,n)
         m2=mS4(2,n)
         m3=mS4(3,n)
         m4=mS4(4,n)
         mm(1)=m4
         mm(2)=m3
         mm(3)=m2
         mm(4)=m1
         Call four_modeExc
      End do
      if(nCUP==4 .or. maxSum==4) goto 100

  100 Continue

      Contains

      Subroutine two_modeExc

      Implicit None

         Do vv=2,maxSum
         Do v1=1,vv-1
            v2=vv-v1

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2)) Call addCnf(2)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0) Call addCnf(2)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2)) Call addCnf(2)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0) Call addCnf(2)

         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)

      End subroutine

      Subroutine three_modeExc

      Implicit None

         Do vv=3,maxSum
         Do v1=1,vv-1
         Do v2=1,vv-v1-1
            v3=vv-v1-v2

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) &
                                   .and. lblj(m3) < nCHO(m3)) Call addCnf(3)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) &
                             .and. lblj(m3) < nCHO(m3)) Call addCnf(3)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 &
                                   .and. lblj(m3) < nCHO(m3)) Call addCnf(3)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) &
                                   .and. lblj(m3) >= 0) Call addCnf(3)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 &
                             .and. lblj(m3) < nCHO(m3)) Call addCnf(3)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) &
                             .and. lblj(m3) >= 0) Call addCnf(3)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 &
                                   .and. lblj(m3) >= 0) Call addCnf(3)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 &
                             .and. lblj(m3) >= 0) Call addCnf(3)

         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)

      End subroutine

      Subroutine four_modeExc

      Implicit None

         Do vv=4,maxSum
         Do v1=1,vv-1
         Do v2=1,vv-v1-1
         Do v3=1,vv-v1-v2-1
            v4=vv-v1-v2-v3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call addCnf(4)

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call addCnf(4)

         End do
         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)
         lblj(m4)=lbli(m4)

      End subroutine

      Subroutine addCnf(ii)

      Implicit None

      Integer :: ii,i
      Integer :: vv(ii),jcup,jmm(icup+ii),jvv(icup+ii)

         nCnf=nCnf+1
         if(nCnf > maxCnf) then
            Write(iout,*) 'ERROR:'
            Write(iout,*) 'ERROR: EXCEEDED MAXIMUM NUMBER OF Q-SPACE CONFIGURATION'
            Write(iout,*) 'ERROR:'
            Write(iout,*)
            Stop
         endif

         !dbg write(iout,'(''  -  '',6i3)') lbli
         !dbg write(iout,'(''  im '',6i3)') imm(1:icup)
         !dbg write(iout,'(''  iv '',6i3)') ivv(1:icup)

         Do i=1,ii
            vv(i)=lblj(mm(i))-lbli(mm(i))
         End do
         !dbg write(iout,'(''  -  '',6i3)') lblj
         !dbg write(iout,'(''  mm '',6i3)') mm(1:ii)
         !dbg write(iout,'(''  vv '',6i3)') vv(1:ii)

         Call mvPlus(icup,imm,ivv,ii,mm,vv,jcup,jmm,jvv)

         !dbg write(iout,'(''  mm3'',6i3)') jmm(1:jcup)
         !dbg write(iout,'(''  vv3'',6i3)') jvv(1:jcup)

         qcup(nCnf)=jcup
         Do i=1,jcup
            qmm(i,nCnf)=jmm(i)
            qvv(i,nCnf)=jvv(i)
         End do

      End subroutine

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_getE2_p1(nn,Hmat)

   USE Vqdpt_mod

   Implicit None

   Integer :: nn
   Real(8) :: Hmat(nn*(nn+1)/2)

   Integer :: p,q,i,j,ij,k,l,nCnf
   Integer :: cup,mvCUP,vsum &
              ,mm(maxqCup+maxpCUP(current_state)) &
              ,vv(maxqCup+maxpCUP(current_state))

   Logical :: add

   Integer :: lbli(Nfree),lblj(Nfree),lblq(Nfree)
   Real(8) :: Eq0,dEi,dEj,inv_thresh_ene
   Real(8), allocatable :: Hqp(:)
   Logical, allocatable :: isHqp(:)

      lbli=0
      lblj=0
      lblq=0

      inv_thresh_ene = 1.D+00/thresh_ene

      Call Mem_alloc(-1,i,'D',nn)
      Allocate(Hqp(nn))
      Call Mem_alloc(-1,i,'L',nn)
      Allocate(isHqp(nn))

      nqCnf=0
      Do p=1,nn

         Call Vqdpt_getqCnf(pCnf_cup(p),pCnf_mm(:,p),pCnf_vv(:,p), &
                            maxqCup,maxqCnf,nCnf,qCnf_cup,qCnf_mm,qCnf_vv)

         Do q=1,nCnf

            ! Check if the current q is already selected
            add=.true.

            Do i=1,p-1
               Call mvMinus3(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                             qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                             cup,vsum)
               if(cup <= nCUP .and. vsum <= maxSum) then
                  add = .false.
                  exit
               endif
            End do
            if(.not. add) cycle

            Do i=p+1,nn
               cup=mvCUP(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                         qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q))
               if(cup == 0) then
                  add = .false.
                  exit
               endif
            End do
            if(.not. add) cycle

            ! Calc Hpq for cuurent q
            nqCnf=nqCnf+1
            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=qCnf_vv(k,q)
            End do
            Call Vpt_getE0(lblq,Eq0)

            Do i=p,nn
               Call mvMinus2(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                             qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                             cup,mm,vv,vsum)
               if(cup <= nCUP .and. vsum <= maxSum) then
                  Do k=1,pCnf_cup(i)
                     lbli(pCnf_mm(k,i))=pCnf_vv(k,i)
                  End do
                  Call Hmat_getHmat(cup,mm,lbli,lblq,Hqp(i))
                  isHqp(i)=.true.
                  Do k=1,pCnf_cup(i)
                     lbli(pCnf_mm(k,i))=0
                  End do

               else
                  Hqp(i)=0.D+00
                  isHqp(i)=.false.

               endif
            End do

            ij=p*(p+1)/2
            Do i=p,nn
               if(.not. isHqp(i)) then
                  ij=ij+i
                  cycle
               endif
               dEi=1.D+00/(Ep0(i)-Eq0)
               if(abs(dEi) > inv_thresh_ene) then
                  ij=ij+i
                  cycle
               endif

               Do j=p,i-1
                  if(.not. isHqp(j)) then
                     ij=ij+1
                     cycle
                  endif
                  dEj=1.D+00/(Ep0(j)-Eq0)
                  if(abs(dEj) > inv_thresh_ene) then
                     ij=ij+1
                     cycle
                  endif
                  Hmat(ij)=Hmat(ij) + 0.5D+00*Hqp(i)*Hqp(j) &
                                    * (dEi + dEj)
                  ij=ij+1
               End do

               Hmat(ij)=Hmat(ij) + Hqp(i)*Hqp(i)*dEi
               ij=ij+p

            End do

            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=0
            End do
         End do

      End do

      Call Mem_dealloc('D',size(Hqp))
      Deallocate(Hqp)
      Call Mem_dealloc('L',size(isHqp))
      Deallocate(isHqp)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_getE2_m1(nn,Hmat)

   USE Vqdpt_mod

   Implicit None

   Integer :: nn
   Real(8) :: Hmat(nn*(nn+1)/2)

   Integer :: p,q,i,j,ij,k,l,nCnf
   Integer :: cup,mvCUP,vsum &
              ,mm(maxqCup+maxpCUP(current_state)) &
              ,vv(maxqCup+maxpCUP(current_state))

   Logical :: add

   Integer :: lbli(Nfree),lblj(Nfree),lblq(Nfree)
   Real(8) :: Eq0,dEi,dEj,inv_thresh_ene
   Real(8), allocatable :: Hqp(:)
   Logical, allocatable :: isHqp(:)

      lbli=0
      lblj=0
      lblq=0

      inv_thresh_ene = 1.D+00/thresh_ene

      Call Mem_alloc(-1,i,'D',nn)
      Allocate(Hqp(nn))
      Call Mem_alloc(-1,i,'L',nn)
      Allocate(isHqp(nn))

      nqCnf=0
      Do p=1,nn

         Call Vqdpt_getqCnf(pCnf_cup(p),pCnf_mm(:,p),pCnf_vv(:,p), &
                            maxqCup,maxqCnf,nCnf,qCnf_cup,qCnf_mm,qCnf_vv)

         Do q=1,nCnf

            ! Check if the current q is already selected
            add=.true.

            Do i=1,p-1
               Call mvMinus3(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                             qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                             cup,vsum)
               if(cup <= nCUP .and. vsum <= maxSum) then
                  add = .false.
                  exit
               endif
            End do
            if(.not. add) cycle

            Do i=p+1,nn
               cup=mvCUP(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                         qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q))
               if(cup == 0) then
                  add = .false.
                  exit
               endif
            End do
            if(.not. add) cycle

            ! Calc Hpq for cuurent q
            nqCnf=nqCnf+1
            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=qCnf_vv(k,q)
            End do
            Call Vpt_getE0(lblq,Eq0)

            Do i=1,nn
               Call mvMinus(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                            qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                            cup,mm,vv)
               if(cup <= nCUP) then
                  Do k=1,pCnf_cup(i)
                     lbli(pCnf_mm(k,i))=pCnf_vv(k,i)
                  End do
                  Call Hmat_getHmat(cup,mm,lbli,lblq,Hqp(i))
                  isHqp(i)=.true.
                  Do k=1,pCnf_cup(i)
                     lbli(pCnf_mm(k,i))=0
                  End do

               else
                  Hqp(i)=0.D+00
                  isHqp(i)=.false.

               endif
            End do

            ij=1
            Do i=1,nn
               if(.not. isHqp(i)) then
                  ij=ij+i
                  cycle
               endif
               dEi=1.D+00/(Ep0(i)-Eq0)
               if(abs(dEi) > inv_thresh_ene) then
                  ij=ij+i
                  cycle
               endif

               Do j=1,i-1
                  if(.not. isHqp(j)) then
                     ij=ij+1
                     cycle
                  endif
                  dEj=1.D+00/(Ep0(j)-Eq0)
                  if(abs(dEj) > inv_thresh_ene) then
                     ij=ij+1
                     cycle
                  endif
                  Hmat(ij)=Hmat(ij) + 0.5D+00*Hqp(i)*Hqp(j) &
                                    * (dEi + dEj)
                  ij=ij+1
               End do

               Hmat(ij)=Hmat(ij) + Hqp(i)*Hqp(i)*dEi
               ij=ij+1

            End do

            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=0
            End do
         End do

      End do

      Call Mem_dealloc('D',size(Hqp))
      Deallocate(Hqp)
      Call Mem_dealloc('L',size(isHqp))
      Deallocate(isHqp)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_getE2_p1_lp1(nn,Hmat)

   USE Vqdpt_mod

   Implicit None

   Integer :: nn
   Real(8) :: Hmat(nn*(nn+1)/2)

   Integer :: i,j,ij,k,q,npq
   Integer :: lbli(Nfree),lblj(Nfree),lblq(Nfree)
   Integer :: cp,mij(maxpCUP(current_state)+maxqCup), &
                 vij(maxpCUP(current_state)+maxqCup), &
              mvCUP,vsum

   Real(8) :: Hiq,Hjq,dEi,dEj,Eq0
   Logical, allocatable :: isP(:)

      lbli=0
      lblj=0
      lblq=0

      ij=1
      npq=0
      Do i=1,nn
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=pCnf_vv(k,i)
         End do
         Call Vqdpt_getqCnf(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                            maxqCup,maxqCnf,nqCnf,qCnf_cup,qCnf_mm,qCnf_vv)

         Allocate(isP(nqCnf))
         isP=.false.
         Do j=1,nn
            if(j==i) cycle
            Do q=1,nqCnf
               cp=mvCUP(pCnf_cup(j),pCnf_mm(:,j),pCnf_vv(:,j), &
                        qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q))
               if(cp==0) then
                  isP(q)=.true.
               endif
            End do
         End do

         Do j=1,i-1
            Do k=1,pCnf_cup(j)
               lblj(pCnf_mm(k,j))=pCnf_vv(k,j)
            End do

            Do q=1,nqCnf
               if(isP(q)) cycle

               Call mvMinus2(pCnf_cup(j),pCnf_mm(:,j),pCnf_vv(:,j), &
                             qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                             cp,mij,vij,vsum)
               if(cp <= nCUP .and. vsum <= maxSum) then
                  Do k=1,qCnf_cup(q)
                     lblq(qCnf_mm(k,q))=qCnf_vv(k,q)
                  End do

                  Call Vpt_getE0(lblq,Eq0)
                  dEj=Ep0(j)-Eq0
                  dEi=Ep0(i)-Eq0
                  if(abs(dEj) > thresh_ene .and. abs(dEi) > thresh_ene) then

                     Call Hmat_getHmat(cp,mij,lblj,lblq,Hjq)
                     Call mvMinus(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                                  qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                                  cp,mij,vij)
                     Call Hmat_getHmat(cp,mij,lbli,lblq,Hiq)
                     Hmat(ij)=Hmat(ij) + 0.5D+00*Hiq*Hjq*(1.D+00/dEi + 1.D+00/dEj)
                     npq=npq + 1

                  endif

                  Do k=1,qCnf_cup(q)
                     lblq(qCnf_mm(k,q))=0
                  End do
               endif

            End do

            ij=ij+1

            Do k=1,pCnf_cup(j)
               lblj(pCnf_mm(k,j))=0
            End do
         End do

         Do q=1,nqCnf
            if(isP(q)) cycle
            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=qCnf_vv(k,q)
            End do

            Call Vpt_getE0(lblq,Eq0)
            dEi=Ep0(i)-Eq0
            if(abs(dEi) > thresh_ene) then

               Call mvMinus(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                            qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                            cp,mij,vij)
               Call Hmat_getHmat(cp,mij,lbli,lblq,Hiq)
               Hmat(ij)=Hmat(ij) + Hiq*Hiq/dEi
               npq=npq + 1

            endif

            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=0
            End do
         End do
         ij=ij+1

         Deallocate(isP)
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=0
         End do
      End do

      !write(6,'(''npq='',i12)') npq

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vqdpt_setqCnf()

   USE Vqdpt_mod

   Implicit None

   Integer :: nCnf

   Character :: num*4,fname*120

   Integer :: cup,mvCUP,maxCup
   Integer, allocatable :: mm(:),vv(:)

   Integer :: i,j,k,l,ll
   Logical :: add

      write(num,'(i4.4)') current_state
      fname=fw(:len_fw)//'SINDO-qCnf'//num//'.dat'
      Open(ifw,file=fname,status='REPLACE')

      cup=maxqCup+maxpCUP(current_state)
      Call Mem_alloc(-1,i,'D',cup*2)
      Allocate(mm(cup),vv(cup))

      nqCnf=0
      maxCup=0
      Do i=1,npCnf(current_state)

         Call Vqdpt_getqCnf(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                            maxqCup,maxqCnf,nCnf,qCnf_cup,qCnf_mm,qCnf_vv)

         ! Check if the Q-space configurations are already selected
         Do j=1,nCnf
            add=.true.

            Do k=1,i-1
               Call mvMinus(pCnf_cup(k),pCnf_mm(:,k),pCnf_vv(:,k), &
                            qCnf_cup(j),qCnf_mm(:,j),qCnf_vv(:,j), &
                            cup,mm,vv)
               ll=0
               Do l=1,cup
                  ll=ll+abs(vv(l))
               End do
               if(cup <= nCUP .and. ll <= maxSum) then
                  add = .false.
                  exit
               endif
            End do
            if(.not. add) cycle

            Do k=i+1,npCnf(current_state)
               cup=mvCUP(pCnf_cup(k),pCnf_mm(:,k),pCnf_vv(:,k), &
                         qCnf_cup(j),qCnf_mm(:,j),qCnf_vv(:,j))
               if(cup == 0) then
                  add = .false.
                  exit
               endif
            End do
            if(.not. add) cycle

            ! Add Q-space
            nqCnf=nqCnf+1
            if(qCnf_cup(j) > maxCup) maxCup = qCnf_cup(j)
            write(ifw,100) qCnf_cup(j)
            write(ifw,100) qCnf_mm(1:qCnf_cup(j),j),qCnf_vv(1:qCnf_cup(j),j)
        100 Format(100i4)

         End do

      End do
      Close(ifw)

      Call Mem_dealloc('D',size(mm)+size(vv))
      Deallocate(mm,vv)

      Call Mem_dealloc('D',size(qCnf_cup)+size(qCnf_mm)+size(qCnf_vv))
      Deallocate(qCnf_cup,qCnf_mm,qCnf_vv)

      maxqCup=maxCup
      Call Mem_alloc(-1,i,'D',nqCnf + maxqCup*nqCnf*2)
      Allocate(qCnf_cup(nqCnf),qCnf_mm(maxqCup,nqCnf),qCnf_vv(maxqCup,nqCnf))

      ! Read the Q-space configuration
      Open(ifw,file=fname,status='OLD')
      Do i=1,nqCnf
         Read(ifw,*) qCnf_cup(i)
         Read(ifw,*) qCnf_mm(1:qCnf_cup(i),i),qCnf_vv(1:qCnf_cup(i),i)
      End do
      !dbg write(iout,'(i6)') nqCnf

      Close(ifw,status='DELETE')

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Vqdpt_getE2_m1_lp1(nn,Hmat)

   USE Vqdpt_mod

   Implicit None

   Integer :: nn
   Real(8) :: Hmat(nn*(nn+1)/2)

   Integer :: i,j,ij,k,q,nCnf,npq
   Integer :: lbli(Nfree),lblj(Nfree),lblq(Nfree)
   Integer :: cpi,miq(maxpCUP(current_state)+maxqCup), &
                  viq(maxpCUP(current_state)+maxqCup), &
              cpj,mjq(maxpCUP(current_state)+maxqCup), &
                  vjq(maxpCUP(current_state)+maxqCup), &
              mvCUP,vsum

   Real(8) :: Hiq,Hjq,dEi,dEj,Eq0

      lbli=0
      lblj=0
      lblq=0

      ij=1
      npq=0
      Do i=1,nn
         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=pCnf_vv(k,i)
         End do

         Do j=1,i-1
            Do k=1,pCnf_cup(j)
               lblj(pCnf_mm(k,j))=pCnf_vv(k,j)
            End do

            Do q=1,nqCnf

               Call mvMinus(pCnf_cup(j),pCnf_mm(:,j),pCnf_vv(:,j), &
                            qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                            cpj,mjq,vjq)
               Call mvMinus(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                            qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                            cpi,miq,viq)
               if(cpi <= nCUP .and. cpj <= nCUP) then
                  Do k=1,qCnf_cup(q)
                     lblq(qCnf_mm(k,q))=qCnf_vv(k,q)
                  End do

                  Call Vpt_getE0(lblq,Eq0)
                  dEj=Ep0(j)-Eq0
                  dEi=Ep0(i)-Eq0
                  if(abs(dEj) > thresh_ene .and. abs(dEi) > thresh_ene) then

                     Call Hmat_getHmat(cpi,miq,lbli,lblq,Hiq)
                     Call Hmat_getHmat(cpj,mjq,lblj,lblq,Hjq)
                     Hmat(ij)=Hmat(ij) + 0.5D+00*Hiq*Hjq*(1.D+00/dEi + 1.D+00/dEj)
                     npq=npq + 1

                  endif

                  Do k=1,qCnf_cup(q)
                     lblq(qCnf_mm(k,q))=0
                  End do
               endif

            End do

            ij=ij+1

            Do k=1,pCnf_cup(j)
               lblj(pCnf_mm(k,j))=0
            End do
         End do

         Do q=1,nqCnf
            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=qCnf_vv(k,q)
            End do

            Call Vpt_getE0(lblq,Eq0)
            dEi=Ep0(i)-Eq0
            if(abs(dEi) > thresh_ene) then

               Call mvMinus(pCnf_cup(i),pCnf_mm(:,i),pCnf_vv(:,i), &
                            qCnf_cup(q),qCnf_mm(:,q),qCnf_vv(:,q), &
                            cpi,miq,viq)
               Call Hmat_getHmat(cpi,miq,lbli,lblq,Hiq)
               Hmat(ij)=Hmat(ij) + Hiq*Hiq/dEi
               npq=npq + 1

            endif

            Do k=1,qCnf_cup(q)
               lblq(qCnf_mm(k,q))=0
            End do
         End do
         ij=ij+1

         Do k=1,pCnf_cup(i)
            lbli(pCnf_mm(k,i))=0
         End do
      End do

      write(6,'(''npq='',i12)') npq

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
