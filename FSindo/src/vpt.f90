!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/03/15
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Vpt_mod

   USE Vib_mod, only : Nfree, nCHO, maxCHO

   ! Target states
   Integer :: Nstate,tcup
   Integer, allocatable :: tmm(:),tvv(:)

   ! Q-space selection parameters
   Integer :: maxSum,maxEx,nCUP
   Integer, allocatable :: maxExc(:)

   ! Threshold energy to avoid divergence / hartree
   Real(8) :: thresh_ene

   ! Runtime variables
   Integer :: current_state
   Integer :: nCnf1,nCnf2,nCnf3,nCnf4

   ! Dump the information of the wfn for prpt calc.
   Logical :: dump

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_run()

   USE Constants_mod
   USE Vpt_mod

   Implicit None

   Integer :: ierr
   Integer :: Vib_getMRforPES
   Integer :: Target_getNstate,Target_getMaxCup,maxCup
   Logical :: lvscf, Vib_getlvscf
   Character(12) :: vscfFile

   Integer :: i,j,k,m
   Integer :: nst,ndel,nstin
   Logical, allocatable :: ss(:)

   Namelist /vpt/maxSum,maxEx,nCUP,thresh_ene,dump

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

      ! Q-space selection
      maxSum=-1
      maxEx=-1
      nCUP=Vib_getMRforPES()

      ! Threshold energy
      thresh_ene=1.D-04

      ! Dump vmp.wfn
      dump=.true.

      Rewind(inp)
      Read(inp,vpt,end=10)
   10 Continue

      write(iout,100)
  100 Format(/,'(  ENTER VPT MODULE  )',//, &
             3x,'>> VPT OPTIONS',/)

      ! ------------------------------------------------------------

      if(lvscf) then
         write(iout,110) vscfFile
      else
         write(iout,112)
      endif
  110 Format(7x,'o VPT WITH ZERO-POINT VSCF REFERENCE (VMP)',/, &
             9x,'READ VSCF WFN  : ',a,/)
  112 Format(7x,'o VPT WITH HO REFERENCE',/)

      write(iout,120) 
      write(iout,122) nCUP
      if(maxSum>0) then
         write(iout,124) maxSum 
      elseif(maxEx>0) then
         Call Mem_alloc(-1,i,'I',Nfree)
         Allocate(maxExc(Nfree))
         Do i=1,Nfree
            if(nCHO(i)-1>maxEx) then
               maxExc(i)=maxEx
            else
               maxExc(i)=nCHO(i)-1
            endif
         End do
         write(iout,126) maxEx

      else
         write(iout,'(''  ERROR: EITHER maxSum OR maxEx NEEDS TO BE SPECIFIED.'')')
         write(iout,'(''  ERROR: TERMINATED WHILE SETTING UP VPT MODULE'')')
         Stop

      endif
  120 Format(7x,'o VPT LEVEL:')
  122 Format(9x,'NCUP   = ',i4)
  124 Format(9x,'MAXSUM = ',i4,/)
  126 Format(9x,'MAXEX  = ',i4,/)

      write(iout,150) thresh_ene
  150 Format(7x,'o THRESH_ENE :',e12.3,/)

      if(dump) write(iout,160) 
  160 Format(7x,'o DUMP VPT WFN : [ vmp-w.wfn ]'/)

      ! ------------------------------------------------------------

      Call Vpt_main()

      ! ------------------------------------------------------------

      if(maxEx>0) then
         Call Mem_dealloc('I',size(maxExc))
         Deallocate(maxExc)
      endif

      Call Hmat_destruct()

      write(iout,200)
  200 Format(/,'(  FINALIZE VPT MODULE  )',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_main()

   USE Constants_mod
   USE Vpt_mod

   Implicit None

   Integer :: i,j
   Integer :: lbl(Nfree)
   Real(8) :: Ezp_vpt2,Ezp_vpt1,Ene_vpt2,Ene_vpt1,E0,E1,E2
   Integer :: wwf

      if(dump) then
         Call file_indicator(30,wwf)
         Open(wwf,file='vmp-w.wfn',status='unknown',form='FORMATTED')
         write(wwf,'(''VMP WAVEFUNCTION'')')
         write(wwf,'(''THE NUMBER OF TARGET STATES'')')
         write(wwf,'(i5)') Nstate
         write(wwf,'(''THE TARGET STATES'')')
         Do i=1,Nstate
            Call Target_getConf(i,tcup,tmm,tvv)
            write(wwf,'(40i4)') tcup,tmm(1:tcup),tvv(1:tcup)
         End do
         write(wwf,'(''OPTIONS: maxSum, maxEx, nCUP'')')
         write(wwf,'(3i5)') maxSum, maxEx, nCUP
      endif

      write(iout,100)
  100 Format(3x,'>> ENTER VPT MAIN ROUTINE',//, &
             6x,'-----[  LOOP OVER TARGET STATES  ]-----',/)

      ! Ground state
      current_state=0
      write(iout,110) 

      lbl=0
      Call Vpt_getE0(lbl,E0)
      Call Hmat_getHmat(0,0,lbl,lbl,E1)
      Call Vpt_getE2(lbl,E0,E2)

      E1=E1-E0
      Ezp_vpt1 = E0+E1
      Ezp_vpt2 = Ezp_vpt1+E2
      if(dump) then
         Write(wwf,'(''STATE='',i6)') 0
         Write(wwf,'(40i4)') 0
         Write(wwf,'(''TOTAL ENERGY (E0,E1,E2)'')') 
         Write(wwf,'(3e17.8)') E0,Ezp_vpt1,Ezp_vpt2
      endif

      E0=E0*H2wvn
      E1=E1*H2wvn
      E2=E2*H2wvn
      Ezp_vpt1=Ezp_vpt1*H2wvn
      Ezp_vpt2=Ezp_vpt2*H2wvn
      write(iout,120) E0,E1,E2,Ezp_vpt1,Ezp_vpt2
      Call timer(1,Iout)

  110 Format(7x,'o STATE 000',':   ZERO-POINT STATE',/)
  120 Format(10x,'   E(0th)    =',f15.5,/, &
             10x,'   E(1st)    =',f15.5,/, &
             10x,'   E(2nd)    =',f15.5,//, &
             10x,'   E(VMP1)   =',f15.5,/, &
             10x,'   E(VMP2)   =',f15.5,/)

      ! Excited states
      Do i=1,Nstate
         current_state=i
         Call Target_getConf(i,tcup,tmm,tvv)
         write(iout,130) i,(tmm(j),tvv(j),j=1,tcup)
         write(iout,*)

         Call Target_getLabel(i,Nfree,lbl)
         Call Vpt_getE0(lbl,E0)
         Call Hmat_getHmat(0,0,lbl,lbl,E1)
         Call Vpt_getE2(lbl,E0,E2)

         E1=E1-E0
         Ene_vpt1 = E0+E1
         Ene_vpt2 = Ene_vpt1+E2
         if(dump) then
            write(wwf,'(''STATE='',i6)') i
            write(wwf,'(40i4)') tcup,tmm(1:tcup),tvv(1:tcup)
            write(wwf,'(''TOTAL ENERGY (E0,E1,E2)'')') 
            write(wwf,'(3e17.8)') E0,Ene_vpt1,Ene_vpt2
         endif

         E0=E0*H2wvn
         E1=E1*H2wvn
         E2=E2*H2wvn
         Ene_vpt1=Ene_vpt1*H2wvn
         Ene_vpt2=Ene_vpt2*H2wvn
         write(iout,140) E0,E1,E2,Ene_vpt1,Ene_vpt2,Ene_vpt1-Ezp_vpt1,Ene_vpt2-Ezp_vpt2

         Call timer(1,Iout)

      End do

  130 Format(7x,'o STATE ',i3.3,': ',10(i3,'_',i1,2x))
  140 Format(10x,'   E(0th)     =',f15.5,/, &
             10x,'   E(1st)     =',f15.5,/, &
             10x,'   E(2nd)     =',f15.5,//, &
             10x,'   E(VMP1)    =',f15.5,/, &
             10x,'   E(VMP2)    =',f15.5,//, &
             10x,'   E(VMP1)-E0 =',f15.5,/, &
             10x,'   E(VMP2)-E0 =',f15.5,/)

      if(dump) close(wwf)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_getE0(lbl,E0)

   USE Vpt_mod

   Implicit None

   Integer :: lbl(Nfree),m
   Real(8) :: E0,modal_ene

      E0=0.D+00
      Do m=1,Nfree
         if(nCHO(m)==0) cycle
         Call Modal_getEne_i(m,lbl(m),modal_ene)
         E0=E0 + modal_ene
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_getE2(lbl,E0i,E2)

   USE Constants_mod
   USE Vpt_mod

   Implicit None

   Integer :: lbl(Nfree)
   Real(8) :: E0i,E2

      if(maxSum > 0) then
         Call Vpt_getE2_maxSum(lbl,E0i,E2)
      elseif(maxEx > 0) then
         Call Vpt_getE2_maxEx(lbl,E0i,E2)
      endif

      write(iout,100)
      write(iout,101) nCnf1
      if(nCUP>1) write(iout,102) nCnf2
      if(nCUP>2) write(iout,103) nCnf3
      if(nCUP>3) write(iout,104) nCnf4
      write(iout,105) nCnf1+nCnf2+nCnf3+nCnf4

  100 Format(14x,'--- Q-SPACE COMPONENTS--- ')
  101 Format(18x,'1-MODE : ',i8)
  102 Format(18x,'2-MODE : ',i8)
  103 Format(18x,'3-MODE : ',i8)
  104 Format(18x,'4-MODE : ',i8)
  105 Format(18x,' TOTAL : ',i8,/, &
             14x,'------------------------- ',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_getE2_maxSum(lbli,E0i,E2)

   USE Vpt_mod
   USE PES_mod

   Implicit None

   Integer :: lbli(Nfree),lblj(Nfree)
   Real(8) :: E0i,E0j,E2,dE,Hij

   Integer :: n,m1,m2,m3,m4,q1,mm(4)
   Integer :: vv,v1,v2,v3,v4
   Integer :: q1a,q1b

      E2=0.D+00
      nCnf1=0
      nCnf2=0
      nCnf3=0
      nCnf4=0

      lblj=lbli

      ! 1-mode exc.
      if(current_state/=0) then
         Do m1=1,Nfree
            q1a=lbli(m1)-maxSum
            if(q1a < 0) q1a=0
            q1b=lbli(m1)+maxSum
            if(q1b >= nCHO(m1)) q1b=nCHO(m1)-1

            Do q1=q1a,q1b

               if(q1==lbli(m1)) cycle
               nCnf1=nCnf1+1

               lblj(m1)=q1
               Call Vpt_getE0(lblj,E0j)
               dE=E0i-E0j
               if(abs(dE)>thresh_ene) then
                  Call Hmat_getHmat(1,m1,lbli,lblj,Hij)
                  E2=E2 + Hij*Hij/dE
               endif

            End do
            lblj(m1)=lbli(m1)

         End do
      endif
      if(nCUP==1 .or. maxSum==1) return

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
      if(nCUP==2 .or. maxSum==2) return

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
      if(nCUP==3 .or. maxSum==3) return

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
      if(nCUP==4 .or. maxSum==4) return

      Contains

      Subroutine two_modeExc

      Implicit None

         Do vv=2,maxSum
         Do v1=1,vv-1
            v2=vv-v1

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2)) Call add2

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0) Call add2

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2)) Call add2

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0) Call add2

         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)

      End subroutine

      Subroutine add2

      Implicit None

         nCnf2=nCnf2+1
         Call Vpt_getE0(lblj,E0j)
         dE=E0i-E0j
         if(abs(dE)>thresh_ene) then
            mm(1)=m2
            mm(2)=m1
            Call Hmat_getHmat(2,mm(1:2),lbli,lblj,Hij)
            E2=E2 + Hij*Hij/dE
         endif

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
                                   .and. lblj(m3) < nCHO(m3)) Call add3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) &
                             .and. lblj(m3) < nCHO(m3)) Call add3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 &
                                   .and. lblj(m3) < nCHO(m3)) Call add3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) &
                                   .and. lblj(m3) >= 0) Call add3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 &
                             .and. lblj(m3) < nCHO(m3)) Call add3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) &
                             .and. lblj(m3) >= 0) Call add3

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 &
                                   .and. lblj(m3) >= 0) Call add3

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 &
                             .and. lblj(m3) >= 0) Call add3

         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)

      End subroutine

      Subroutine add3

      Implicit None

         nCnf3=nCnf3+1
         Call Vpt_getE0(lblj,E0j)
         dE=E0i-E0j
         if(abs(dE)>thresh_ene) then
            mm(1)=m3
            mm(2)=m2
            mm(3)=m1
            Call Hmat_getHmat(3,mm(1:3),lbli,lblj,Hij)
            E2=E2 + Hij*Hij/dE
         endif

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
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)+v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) < nCHO(m4)) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)+v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) < nCHO(m3) .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)+v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) < nCHO(m2) .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)+v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) < nCHO(m1) .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call add4

            lblj(m1)=lbli(m1)-v1
            lblj(m2)=lbli(m2)-v2
            lblj(m3)=lbli(m3)-v3
            lblj(m4)=lbli(m4)-v4
            if(lblj(m1) >= 0 .and. lblj(m2) >= 0 .and. &
               lblj(m3) >= 0 .and. lblj(m4) >= 0) Call add4

         End do
         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)
         lblj(m4)=lbli(m4)

      End subroutine

      Subroutine add4

      Implicit None

         nCnf4=nCnf4+1
         Call Vpt_getE0(lblj,E0j)
         dE=E0i-E0j
         if(abs(dE)>thresh_ene) then
            mm(1)=m4
            mm(2)=m3
            mm(3)=m2
            mm(4)=m1
            Call Hmat_getHmat(4,mm(1:4),lbli,lblj,Hij)
            E2=E2 + Hij*Hij/dE
         endif

      End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_getE2_maxEx(lbli,E0i,E2)

   USE Vpt_mod
   USE PES_mod

   Implicit None

   Integer :: lbli(Nfree),lblj(Nfree)
   Real(8) :: E0i,E0j,E2,dE,Hij

   Integer :: n,m1,m2,m3,m4,q1,q2,q3,q4,mm(4)

      E2=0.D+00
      nCnf1=0
      nCnf2=0
      nCnf3=0
      nCnf4=0

      lblj=lbli

      ! 1-mode exc.
      if(current_state/=0) then
         Do m1=1,Nfree
         Do q1=0,maxExc(m1)
            if(q1==lbli(m1)) cycle
            nCnf1=nCnf1+1

            lblj(m1)=q1
            Call Vpt_getE0(lblj,E0j)
            dE=E0i-E0j
            if(abs(dE)>thresh_ene) then
               Call Hmat_getHmat(1,m1,lbli,lblj,Hij)
               E2=E2 + Hij*Hij/dE
            endif

            lblj(m1)=lbli(m1)

         End do
         End do
      endif
      if(nCUP==1) return

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

      if(nCUP==2) return

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
      if(nCUP==3) return

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
      if(nCUP==4) return

      Contains 

      Subroutine two_modeExc

      Implicit None

         Do q1=0,maxExc(m1)
            if(q1==lbli(m1)) cycle
         Do q2=0,maxExc(m2)
            if(q2==lbli(m2)) cycle
            nCnf2=nCnf2+1

            lblj(m1)=q1
            lblj(m2)=q2
            Call Vpt_getE0(lblj,E0j)
            dE=E0i-E0j
            if(abs(dE)>thresh_ene) then
               mm(1)=m2
               mm(2)=m1
               Call Hmat_getHmat(2,mm(1:2),lbli,lblj,Hij)
               E2=E2 + Hij*Hij/dE
            endif

         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)

      End subroutine

      Subroutine three_modeExc

      Implicit None

         Do q1=0,maxExc(m1)
            if(q1==lbli(m1)) cycle
         Do q2=0,maxExc(m2)
            if(q2==lbli(m2)) cycle
         Do q3=0,maxExc(m3)
            if(q3==lbli(m3)) cycle
            nCnf3=nCnf3+1

            lblj(m1)=q1
            lblj(m2)=q2
            lblj(m3)=q3
            Call Vpt_getE0(lblj,E0j)
            dE=E0i-E0j
            if(abs(dE)>thresh_ene) then
               mm(1)=m3
               mm(2)=m2
               mm(3)=m1
               Call Hmat_getHmat(3,mm(1:3),lbli,lblj,Hij)
               E2=E2 + Hij*Hij/dE
            endif

         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)

      End subroutine

      Subroutine four_modeExc

      Implicit None

         Do q1=0,maxExc(m1)
            if(q1==lbli(m1)) cycle
         Do q2=0,maxExc(m2)
            if(q2==lbli(m2)) cycle
         Do q3=0,maxExc(m3)
            if(q3==lbli(m3)) cycle
         Do q4=0,maxExc(m4)
            if(q4==lbli(m4)) cycle
            nCnf4=nCnf4+1

            lblj(m1)=q1
            lblj(m2)=q2
            lblj(m3)=q3
            lblj(m4)=q4
            Call Vpt_getE0(lblj,E0j)
            dE=E0i-E0j
            if(abs(dE)>thresh_ene) then
               mm(1)=m4
               mm(2)=m3
               mm(3)=m2
               mm(4)=m1
               Call Hmat_getHmat(4,mm(1:4),lbli,lblj,Hij)
               E2=E2 + Hij*Hij/dE
            endif

         End do
         End do
         End do
         End do
         lblj(m1)=lbli(m1)
         lblj(m2)=lbli(m2)
         lblj(m3)=lbli(m3)
         lblj(m4)=lbli(m4)

      End subroutine

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_setnCUP(nCUPIn)

   USE Vpt_mod

   Integer :: nCUPIn

      nCUP = nCUPIn

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_setMaxSum(maxSumIn)

   USE Vpt_mod

   Integer :: maxSumIn

      maxSum = maxSumIn

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Vpt_setCurrentState(current_stateIn)

   USE Vpt_mod

   Integer :: current_stateIn

      current_state = current_stateIn

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

