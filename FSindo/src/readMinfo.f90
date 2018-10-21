!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+----10
!   Last modified  2017/11/19
!   Code description by K.Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+----10
Subroutine ReadMinfo(minfoFile,Nat,Nfree,x,mass,omega,L)

   USE Constants_mod

   Implicit None

   Character*80 :: minfoFile
   Integer :: Nat,Nfree,Nat3
   Real(8) :: x(*),mass(*),omega(*),L(*)

   Integer :: i,j,k,mat,mat3,ndomain,nd,nf
   Integer :: version
   Integer, allocatable :: ai(:)
   Real(8), allocatable :: L0(:)
   Character*120 :: line
   Character*4   :: aname

      Open(10,file=minfoFile,status='old')

      Do while(.true.)
         Read(10,'(a)') line
         !write(6,*) line
         if(Index(line,'version 1')>0) then
            version=1
            exit
         else if(Index(line,'version 2')>0) then
            version=2
            exit
         endif
      End do

      Do while(.true.)
         Read(10,'(a)') line
         if(Index(line,'Atomic')>0) exit
      End do
      Read(10,*) Nat
      Nat3=Nat*3
      Do i=1,Nat
         Read(10,'(a)') line
         Read(line,*) aname,j,mass(i),x(3*i-2),x(3*i-1),x(3*i)
      End do
      if(version == 2) then
         mass(1:Nat)=mass(1:Nat)*elmass
      endif

      !write(6,*) Nat
      !write(6,'(3f12.4)') mass(1:Nat)
      !write(6,'(3f12.6)') x(1:Nat*3)

      Do while(.true.)
         Read(10,'(a)') line
         if(index(line,'Vibrational Data')>0) then
            Read(10,'(a)') line
            if(index(line,'Domain')>0) then
               Read(10,*) ndomain
            else
               ndomain=0
            endif
            exit
         endif
      End do

      if(ndomain == 0) then
         Do while(.true.)
            Read(10,'(a)') line
            if(Index(line,'Vibrational Frequency')>0) exit
         End do
         Read(10,*) Nfree
         Read(10,*) omega(1:Nfree)
         Read(10,*)

         k=1
         Do i=1,Nfree
            Read(10,*)
            Read(10,*)
            Read(10,*) L(k:Nat3*i)
            k=Nat3*i+1
         End do

      else
         Nfree=0
         k=0
         Do nd=1,ndomain
            Read(10,*)

            Read(10,*)
            Read(10,*) mat
            allocate(ai(mat))
            Read(10,*) ai

            Read(10,*)

            Read(10,*)
            Read(10,*) nf
            Read(10,*) omega(Nfree+1:Nfree+nf)
            Nfree=Nfree + nf

            Read(10,*)

            mat3=mat*3
            allocate(L0(mat3))
            Do i=1,nf
               Read(10,*)
               Read(10,*)
               Read(10,*) L0

               Do j=1,mat
                  L(k+(ai(j)-1)*3+1:k+ai(j)*3) = L0((j-1)*3+1:j*3)
               End do
               k=k+Nat3
            End do

            deallocate(ai,L0)
            
         End do

      endif

      !write(6,*) Nfree
      !write(6,'(3f12.4)') omega(1:Nfree)
      !Do i=1,Nfree
      !   write(6,'(3f12.4)') L((i-1)*Nat3+1:i*Nat3)
      !   write(6,*)
      !End do
      !stop

      Close(10)

End subroutine
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9----+----10
!
