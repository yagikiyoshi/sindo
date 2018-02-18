!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2014/01/30
!   Copyright 2014
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module Target_mod

   Integer :: Nstate,maxCup
   Integer, allocatable :: target_cup(:),target_mm(:,:),target_vv(:,:)

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

   Subroutine Target_construct(Nfree,activemodes)

   USE Target_mod
   USE Constants_mod

   Implicit None

   Integer :: target_state(MaxNfree,MaxState)
   Logical :: fund

   Logical, allocatable :: ss(:)
   Integer :: ndel,cup,nstin,nst
   Integer :: Nfree,Mfree
   Logical :: activemodes(Nfree)

   Integer :: i,j,k,m

   Namelist /states/Nstate,target_state,fund

      Mfree=0
      Do m=1,Nfree
         if(activemodes(m)) Mfree=Mfree+1
      End do

      write(iout,100)
  100 Format(/,'(  SETUP TARGET MODULE  )',/)

      ! ------------------------------------------------------------
      ! >> Read input 

      ! --- default ---

      Nstate=0
      target_state=0
      fund=.false.

      Rewind(inp)
      Read(inp,states,end=10)
   10 Continue

      if(Nstate>MaxState) then
         write(iout,'(''  ERROR: MAXIMUM NUMBER OF STATE IS LIMITIED TO '',i4)') &
                    MaxState
         write(iout,'(''  ERROR: TERMINATED WHILE SETTING UP TARGET MODULE'')')
         Stop
      endif

      nstin=Nstate

      ! Check target_state
      Allocate(ss(nstin))
      ss=.true.
      ndel=0

      ! Check if target_state has zero-point
      Do i=1,nstin
         k=0
         Do m=1,Nfree
            if(target_state(m,i)/=0) then
               k=1
               exit
            endif
         End do
         if(k==0) then
            write(iout,'(9x,''WARNING: ZERO-POINT IS FOUND FOR TARGET_STATE'')')
            write(iout,'(9x,''WARNING: STATE ='',i4,'' IS REMOVED FROM THE TARGET'',/)') i
            ss(i)=.false.
            ndel=ndel+1
         endif
      End do

      ! Check if target_state has a duplicate entry
      Do i=1,nstin
         if(.not. ss(i)) cycle
         Do j=1,i-1
            k=0
            Do m=1,Nfree
               if(target_state(m,i)/=target_state(m,j)) then
                  k=1
                  exit
               endif
            End do
            if(k==0) then
               write(iout,'(9x,''WARNING: DUPLICATE INPUT IS FOUND FOR TARGET_STATE'')')
               write(iout,'(9x,''WARNING: STATE ='',i4,'' IS REMOVED FROM THE TARGET'',/)') j
               ss(j)=.false.
               ndel=ndel+1
            endif
         End do
      End do

      ! Check if target_state has a fundamental
      if(fund) then 
         Do i=1,nstin
            if(.not. ss(i)) cycle
            j=0
            Do m=1,Nfree
               if(target_state(m,i)/=0) then 
                  j=j+1
                  k=target_state(m,i)
               endif
            End do
            if(j==1 .and. k==1) then
               write(iout,'(9x,''WARNING: FUNDAMENTAL IS FOUND FOR TARGET_STATE'')')
               write(iout,'(9x,''WARNING: STATE ='',i4,'' IS REMOVED FROM THE TARGET'',/)') i
               ss(i)=.false.
               ndel=ndel+1
            endif
         End do
      endif
      !dbg write(6,'(5l4)') ss

      ! Set maxCup
      maxCup=0
      if(fund) maxCup=1
      Do i=1,nstin
         if(.not. ss(i)) cycle

         cup=0
         Do m=1,Nfree
            if(target_state(m,i)/=0) cup=cup+1
         End do
         if(cup > maxCup) maxCup=cup

      End do

      ! Set Nstate
      if(fund) then 
         !Nstate=nstin+Nfree-ndel
         Nstate=nstin+Mfree-ndel
      else
         Nstate=nstin-ndel
      endif

      Call Mem_alloc(-1,i,'I',Nstate+Nstate*maxCup*2)
      Allocate(target_cup(Nstate), &
               target_mm(maxCup,Nstate), &
               target_vv(maxCup,Nstate))

      nst=0
      if(fund) then
         !target_cup(1:Nfree)=1
         !Do m=1,Nfree
         !   target_mm(1,m)=m
         !   target_vv(1,m)=1
         !End do
         !nst=Nfree
         target_cup(1:Mfree)=1
         Do m=1,Nfree
            if(activemodes(m)) then
               nst=nst+1
               target_mm(1,nst)=m
               target_vv(1,nst)=1
            endif
         End do
      endif

      Do i=1,nstin
         if(.not. ss(i)) cycle
         nst=nst+1

         target_cup(nst)=0
         Do m=1,Nfree
            if(target_state(m,i)/=0) then
               target_cup(nst)=target_cup(nst)+1
               target_mm(target_cup(nst),nst)=m
               target_vv(target_cup(nst),nst)=target_state(m,i)
            endif

         End do

      End do
      Deallocate(ss)

      write(iout,110) 
  110 Format(3x,'>> TARGET STATES',/, &
             7x,'000   : ZERO-POINT STATE')

      nst=0
      if(fund) then
         !write(iout,120) Nfree 
         !nst=Nfree
         write(iout,120) Mfree 
         nst=Mfree
      endif
  120 Format(5x,'001-',i3.3,' : FUNDAMENTALS')

      Do i=nst+1,Nstate
         write(iout,130) i,(target_mm(j,i),target_vv(j,i),j=1,target_cup(i))
      End do
      write(iout,*)
  130 Format(7x,i3.3,'   :',10(i3,'_',i1,2x))

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Target_read(Filename)

   USE Target_mod
   USE Constants_mod

   Implicit None

   Character(30) :: Filename
   Character(120) :: line
   Integer :: ifl

   Integer :: i,cup

      Call file_indicator(30,ifl)
      Open(ifl,file=Filename,status='old')

      Do while (index(line,'THE NUMBER OF TARGET STATES') == 0)
         Read(ifl,'(a)',end=10) line
      End do

      Read(ifl,*) Nstate
      Read(ifl,*) 

      ! Set maxCup and allocate mm/vv
      maxCup=0
      Do i=1,Nstate
         Read(ifl,*) cup
         if(cup > maxCup) maxCup=cup
      End do
      Call Mem_alloc(-1,i,'I',Nstate+Nstate*maxCup*2)
      Allocate(target_cup(Nstate), &
               target_mm(maxCup,Nstate), &
               target_vv(maxCup,Nstate))

      ! Backspace and read cup/mm/vv
      Do i=1,Nstate
         backspace(ifl)
      End do
      Do i=1,Nstate
         Read(ifl,*) target_cup(i), &
                     target_mm(1:target_cup(i),i), &
                     target_vv(1:target_cup(i),i)
      End do

      Close(ifl)
      return

   10 Continue
      write(iout,'(''ERROR: NO TARGET STATES IS FOUND IN '',a)') Filename
      Stop

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Target_destruct()

   USE Target_mod
   USE Constants_mod

   Implicit None

      Call Mem_dealloc('I',size(target_cup))
      Call Mem_dealloc('I',size(target_mm))
      Call Mem_dealloc('I',size(target_vv))
      Deallocate(target_cup,target_mm,target_vv)

      write(iout,100)
  100 Format(/,'(  FINALIZE TARGET MODULE  )',/)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Target_getLabel(nst,Nfree,label)

   USE Target_mod

   Implicit None

   Integer :: nst,Nfree,label(Nfree)

   Integer :: i

      label(1:Nfree)=0
      Do i=1,target_cup(nst)
         label(target_mm(i,nst)) = target_vv(i,nst)
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Target_getConf(nst,cup,mm,vv)

   USE Target_mod

   Implicit None

   Integer :: nst,cup,mm(*),vv(*)

   Integer :: i

      cup = target_cup(nst)
      Do i=1,target_cup(nst)
         mm(i)=target_mm(i,nst)
         vv(i)=target_vv(i,nst)
      End do

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Target_getNstate()

   USE Target_mod

   Implicit None

   Integer :: Target_getNstate

      Target_getNstate = Nstate

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Target_setNstate(NstateIn)

   USE Target_mod

   Implicit None

   Integer :: NstateIn

      Nstate = NstateIn

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function Target_getMaxCup()

   USE Target_mod

   Implicit None

   Integer :: Target_getMaxCup

      Target_getMaxCup = maxCup

   End function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
