!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2012/11/06
!   Copyright 2012
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
MODULE qff_mod

  USE mrpes_mod
!
!  Private variables for QFF
!
!    E0                : Energy
!    EC(Nfree)         : Geometry
!    Gi,Hii,Tiii,Uiiii    : 1MR
!    Hij,Tiij,Uiijj,Uiiij : 2MR
!    Tijk,Uiijk           : 3MR
!    Uijkl                : 4MR
!
   Real(8):: E0
   Real(8), dimension(:), Allocatable:: EC
   Real(8), dimension(:), Allocatable:: Gi,Hii,Tiii,Uiiii
   Real(8), dimension(:), Allocatable:: Hij,Tiij,Uiijj,Uiiij
   Real(8), dimension(:), Allocatable:: Tijk,Uiijk
   Real(8), dimension(:), Allocatable:: Uijkl
!
!    tl1,tl3,tl3,tl4  : Title of 1MR, 2MR, 3MR, and 4MR QFF data
!
   Character :: tl1*80,tl2*80,tl3*80,tl4*80
!
!
END MODULE 
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_Construct()
!
  USE qff_mod

  Implicit None
!
    Integer :: mm,ier
!
!
!=======================(  Set up reference points  ) =======================
!
! Set up Matrix
!

      E0=0.D+00

      Call Mem_alloc(-1,ier,'D',Nfree)
      Allocate (EC(Nfree))
      EC=0.D+00

      ! -- 1MR --
      mm=Nfree
      Call Mem_alloc(-1,ier,'D',mm*4)
      Allocate (Gi(mm),Hii(mm),Tiii(mm),Uiiii(mm))
      Gi=0.D+00
      Hii=0.D+00
      Tiii=0.D+00
      Uiiii=0.D+00

      if(MR == 1) goto 10

      ! -- 2MR --
      mm=Nfree*(Nfree-1)/2
      Call Mem_alloc(-1,ier,'D',mm*2)
      Allocate (Hij(mm),Uiijj(mm))
      Hij=0.D+00
      Uiijj=0.D+00

      mm=Nfree*(Nfree-1)
      Call Mem_alloc(-1,ier,'D',mm*2)
      Allocate (Tiij(mm),Uiiij(mm))
      Tiij=0.D+00
      Uiiij=0.D+00

      if(MR == 2) goto 10

      ! -- 3MR --
      mm=Nfree*(Nfree-1)*(Nfree-2)/6
      Call Mem_alloc(-1,ier,'D',mm)
      Allocate (Tijk(mm))
      Tijk=0.D+00

      mm=mm*3
      Call Mem_alloc(-1,ier,'D',mm)
      Allocate (Uiijk(mm))
      Uiijk=0.D+00

      if(MR == 3) goto 10

      ! -- 4MR --
      mm=Nfree*(Nfree-1)*(Nfree-2)*(Nfree-3)/24
      Call Mem_alloc(-1,ier,'D',mm)
      Allocate (Uijkl(mm))
      Uijkl=0.D+00

   10 Continue

!
! Read data
!
      if(len_trim(mopfile) == 0) then
         Call qff_readhs
      else
         Call qff_readMop
      endif
!
!===============================================================================
!
      !quiet Write(Iout,300)
      !quiet Call spr_meminfo
      !quiet Call timer(1)
      return

  100 Format(//,'(  ENTER QFF MODULE  )',//, &
             3x,'o NUMBER OF MODES = ',i8)
  300 Format(/,'(  SETUP OF QFF MODULE COMPLETED  )',//)
!
  End subroutine  qff_Construct
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_Destruct
!
  USE qff_mod
!
  Implicit None
!
      Call Mem_dealloc('D',size(EC))
      Deallocate (EC)
      Call Mem_dealloc('D',size(Gi))
      Call Mem_dealloc('D',size(Hii))
      Call Mem_dealloc('D',size(Tiii))
      Call Mem_dealloc('D',size(Uiiii))
      Deallocate (Gi,Hii,Tiii,Uiiii)

      if(MR ==1) goto 10

      Call Mem_dealloc('D',size(Hij))
      Call Mem_dealloc('D',size(Tiij))
      Call Mem_dealloc('D',size(Uiijj))
      Call Mem_dealloc('D',size(Uiiij))
      Deallocate (Hij,Uiijj,Tiij,Uiiij)

      if(MR ==2) goto 10

      Call Mem_dealloc('D',size(Tijk))
      Call Mem_dealloc('D',size(Uiijk))
      Deallocate (Tijk,Uiijk)

      if(MR ==3) goto 10

      Call Mem_dealloc('D',size(Uijkl))
      Deallocate (Uijkl)

   10 Continue

      return
!
!
  100 Format(/,'(  EXIT QFF MODULE... EXECUTED NORMALLY  ) ',/)
!
  End subroutine qff_Destruct
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_readhs
!
  USE Constants_mod
  USE qff_mod
!
  Implicit None
!
!--------------------------------------------------------------------------------
!
    Integer :: i,j,k,l,m,n,n1,n2,n3
    Real(8) :: xx
    Character :: ch*80
    Real(8) :: toau,toau2,toau3,toau4
!
!--------------------------------------------------------------------------------
!
      Open(unit=7,file=PotDir(:Len_PotDir)//'001.hs',status='OLD')

      ! Angs(amu)1/2 -> Bohr(emu)1/2
      toau = SQRT(elmass)/B2A
      toau2 = toau*toau
      toau3 = toau2*toau
      toau4 = toau3*toau
!
!--------------------------------------------------------------------------------
!
!   >> >> Potential energy and Geometry << <<
!
      Read(7,*)
      Read(7,*) E0
      Read(7,*)

      j=mod(Nfree,3)
      if(j==0) then
         n=Nfree/3
         Do i=1,n
            Read(7,*) (EC(3*(i-1)+j),j=1,3)
         End do
      else
         n=(Nfree-j)/3
         Do i=1,n
            Read(7,*) (EC(3*(i-1)+j),j=1,3)
         End do
         Read(7,*) (EC(j),j=3*n+1,Nfree)
      endif

!
!   >> >> 1 MR << <<
!
      Read(7,'(5x,a)') tl1

      Read(7,*)
      Do i=1,Nfree
         Read(7,*) n,xx
         Gi(i)=xx/toau
      End do
      Read(7,*)
      Do i=1,Nfree
         Read(7,*) n,xx
         Hii(i)=xx/toau2
      End do
      Hii=Hii*0.5D+00

      Read(7,*)
      Do i=1,Nfree
         Read(7,*) n,xx
         Tiii(i)=xx/toau3
      End do
      Tiii=Tiii/6.D+00

      Read(7,*)
      Do i=1,Nfree
         Read(7,*) n,xx
         Uiiii(i)=xx/toau4
      End do
      Uiiii=Uiiii/24.D+00

      if(MR == 1) goto 100

!
!   >> >> 2 MR << <<
!
      Read(7,'(5x,a)') tl2

      Read(7,*)
      k=1
      Do i=2,Nfree
      Do j=1,i-1
         Read(7,*) n1,n2,xx
         Hij(k)=xx/toau2
         k=k+1
      End do
      End do

      Read(7,*)
      k=1
      Do i=2,Nfree
      Do j=1,i-1
         Read(7,*) n1,n2,xx
         Uiijj(k)=xx/toau4
         k=k+1
      End do
      End do
      Uiijj=Uiijj*0.25D+00

      Read(7,*)
      k=1
      Do i=2,Nfree
      Do j=1,i-1
         Read(7,*) n1,n2,xx
         Tiij(k)=xx/toau3
         Read(7,*) n1,n2,xx
         Tiij(k+1)=xx/toau3
         k=k+2
      End do
      End do
      Tiij=Tiij*0.5D+00

      Read(7,*)
      k=1
      Do i=2,Nfree
      Do j=1,i-1
         Read(7,*) n1,n2,xx
         Uiiij(k)=xx/toau4
         Read(7,*) n1,n2,xx
         Uiiij(k+1)=xx/toau4
         k=k+2
      End do
      End do
      Uiiij=Uiiij/6.D+00

      if(MR == 2) goto 100

!
!   >> >> 3 MR << <<
!
      Read(7,'(5x,a)') tl3

      Read(7,*)
      l=1
      Do i=3,Nfree
      Do j=2,i-1
      Do k=1,j-1
         Read(7,*) n1,n2,n3,xx
         Tijk(l)=xx/toau3
         l=l+1
      End do
      End do
      End do

      Read(7,*)
      l=1
      Do i=3,Nfree
      Do j=2,i-1
      Do k=1,j-1
         Read(7,*) n1,n2,n3,xx
         Uiijk(l)=xx/toau4
         Read(7,*) n1,n2,n3,xx
         Uiijk(l+1)=xx/toau4
         Read(7,*) n1,n2,n3,xx
         Uiijk(l+2)=xx/toau4
         l=l+3
      End do
      End do
      End do
      Uiijk=Uiijk*0.5D+00

      if(MR == 3) goto 100

!
!   >> >> 4 MR << <<
!

  100 Continue
      Close(7)
      return

!--------------------------------------------------------------------------------
!
  End subroutine qff_Readhs
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Subroutine qff_readMop()

  USE qff_mod

  Implicit None

    Integer :: i,j,k,l,m,k2,l2

    Real(8) :: sqomg(Nfree),H2wvn,aa
    Real(8) :: si,sj,sk,sl,sii,sjj,skk
    Integer :: mm(4),no,mtmp, mi,mj,mk,ml
    Character :: line*80

      i=Len_trim(mopfile)
      Open(unit=10,file=PotDir(:Len_PotDir)//mopfile(:i),status='OLD')
      read(10,*)
      Do i=1,Nfree
         read(10,*) sqomg(i)
         !aa=sqomg(i)*H2wvn()
         !write(6,'(f12.4)') aa
         sqomg(i)=sqrt(sqomg(i))
      End do

      read(10,'(a)') line
      tl1=line(18:)
      tl2=tl1
      tl3=tl1
      tl4=tl1

      Do while(.true.)
         read(10,'(a)',end=100) line

         mm=-1
         read(line,*) aa,mm(1)
         no=1
         read(line,*,end=10) aa,mm(1:2)
         no=2
         read(line,*,end=10) aa,mm(1:3)
         no=3
         read(line,*,end=10) aa,mm(1:4)
         no=4

      10 Continue

         Do i=1,no-1
            Do j=i+1,no
               if(mm(i)<mm(j)) then
                  mtmp=mm(i)
                  mm(i)=mm(j)
                  mm(j)=mtmp
               endif
            End do
         End do

         ! write(6,'(5i4,12x,f12.6)') no,mm,aa
         mi=mm(1)
         mj=mm(2)
         mk=mm(3)
         ml=mm(4)

         Select case(no)
         case (1)
            Gi(mi)=aa*sqomg(mi)
            !write(6,'("Gi",i4,12x,e12.4)') mi,Gi(mi)

         case (2)
            aa=aa*sqomg(mj)*sqomg(mi)
            if(mi==mj) then
               Hii(mi)=aa
               !write(6,'("Hii",i4,12x,e12.4)') mi,Hii(mi)

            else
               if(MR>1) then 
                  k=(mi-1)*(mi-2)/2 + mj
                  Hij(k)=aa
               endif
            endif

         case (3)
            aa=aa*sqomg(mi)*sqomg(mj)*sqomg(mk)
            if(mi==mj .and. mi==mk) then
               ! Tiii
               Tiii(mi)=aa
               !write(6,'("Tiii",i4,12x,e12.4)') mi,Tiii(mi)

            else if(mi==mj) then
               if(MR>1) then 
                  ! Tiij
                  k=((mi-1)*(mi-2)/2 + mk)*2-1
                  Tiij(k)=aa
               endif
            else if(mj==mk) then
               if(MR>1) then 
                  ! Tijj
                  k=((mi-1)*(mi-2)/2 + mk)*2
                  Tiij(k)=aa
               endif
            else
               if(MR>2) then 
                  ! Tijk
                  k=(mi-1)*(mi-2)*(mi-3)/6 + (mj-1)*(mj-2)/2 + mk
                  Tijk(k)=aa
               endif
            endif

         case (4)
            aa=aa*sqomg(mi)*sqomg(mj)*sqomg(mk)*sqomg(ml)
            if(mi==mj .and. mi==mk .and. mi==ml) then
               ! Uiiii
               Uiiii(mi)=aa
               !write(6,'("Uiiii",i4,12x,e12.4)') mi,Uiiii(mi)
            else if(mi==mj .and. mi==mk) then
               if(MR>1) then 
                  ! Uiiij
                  k=((mi-1)*(mi-2)/2 + ml)*2-1
                  Uiiij(k)=aa
               endif
            else if(mj==mk .and. mj==ml) then
               if(MR>1) then 
                  ! Uijjj
                  k=((mi-1)*(mi-2)/2 + ml)*2
                  Uiiij(k)=aa
               endif
            else if(mi==mj .and. mk==ml) then
               if(MR>1) then 
                  ! Uiijj
                  k=(mi-1)*(mi-2)/2 + ml
                  Uiijj(k)=aa
               endif
            else if(mi==mj) then
               if(MR>2) then 
                  ! Uiijk
                  k=((mi-1)*(mi-2)*(mi-3)/6 + (mk-1)*(mk-2)/2 + ml)*3-2
                  Uiijk(k)=aa
               endif
            else if(mj==mk) then
               if(MR>2) then 
                  ! Uijjk
                  k=((mi-1)*(mi-2)*(mi-3)/6 + (mj-1)*(mj-2)/2 + ml)*3-1
                  Uiijk(k)=aa
               endif
            else if(mk==ml) then
               if(MR>2) then 
                  ! Uijkk
                  k=((mi-1)*(mi-2)*(mi-3)/6 + (mj-1)*(mj-2)/2 + ml)*3
                  Uiijk(k)=aa
               endif
            else
               if(MR>3) then 
                  k=(mi-1)*(mi-2)*(mi-3)*(mi-4)/24 + (mj-1)*(mj-2)*(mj-3)/6 + (mk-1)*(mk-2)/2 + ml
                  Uijkl(k)=aa
               endif
            endif

         End select

      End do
  100 Continue

      close(10)

  End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_PES(QQ,V)
!
  USE qff_mod
!
  Implicit None
!
!--------------------------------------------------------------------------------
!
! Geometry in normal coordinate (Bohr(emu)^1/2)
!
    Real(8), dimension(Nfree), intent(in):: QQ
    Real(8), intent(out):: V

!--------------------------------------------------------------------------------
!
!
    Integer :: i,j,k,l,n,a1,a2

    Real(8) :: Di,Dj,Dk,Dl,G1,H1,T1,U1,H2,T2,U21,U22,T3,U3,U4
!
!--------------------------------------------------------------------------------
!
!   >>  initialize  <<
!
!
        G1=0.D+00; H1=0.D+00; T1=0.D+00; U1=0.D+00
        H2=0.D+00; T2=0.D+00; U21=0.D+00; U22=0.D+00
        T3=0.D+00; U3=0.D+00
        U4=0.D+00

        Do i=1,Nfree
           Di=QQ(i) - EC(i)

           G1 = G1 + Di * Gi(i)
           H1 = H1 + Di*Di * Hii(i)
           T1 = T1 + Di*Di*Di * Tiii(i)
           U1 = U1 + Di*Di*Di*Di * Uiiii(i)

        End do
        V = E0 + G1 + H1 + T1 + U1 
        if(MR==1) return

        a1=1
        a2=1
        Do i=2,Nfree
           Di=QQ(i) - EC(i)
           Do j=1,i-1
              Dj = QQ(j) - EC(j)

              H2 = H2 + Di*Dj * Hij(a1)
              U21= U21+ Di*Di*Dj*Dj * Uiijj(a1)
              a1=a1+1
              T2 = T2 + Di*Di*Dj * Tiij(a2)
              T2 = T2 + Dj*Dj*Di * Tiij(a2+1)
              U22= U22+ Di*Di*Di*Dj * Uiiij(a2)
              U22= U22+ Dj*Dj*Dj*Di * Uiiij(a2+1)
              a2=a2+2

           End do
        End do
        V = V + H2 + T2 + U21+ U22 
        if(MR==2) return

        a1=1
        a2=1
        Do i=3,Nfree
           Di=QQ(i) - EC(i)
           Do j=2,i-1
              Dj=QQ(j) - EC(j)
              Do k=1,j-1
                 Dk=QQ(k) - EC(k)

                 T3 = T3 + Di*Dj*Dk * Tijk(a1)
                 a1=a1+1
                 U3 = U3 + Di*Di*Dj*Dk * Uiijk(a2)
                 U3 = U3 + Dj*Dj*Dk*Di * Uiijk(a2+1)
                 U3 = U3 + Dk*Dk*Di*Dj * Uiijk(a2+2)
                 a2=a2+3

              End do
           End do
        End do
        V = V + T3 + U3
        if(MR==3) return

        a1=1
        Do i=4,Nfree
           Di=QQ(i) - EC(i)
           Do j=3,i-1
              Dj=QQ(j) - EC(j)
              Do k=2,j-1
                 Dk=QQ(k) - EC(k)
                 Do l=1,k-1
                    Dl=QQ(l) - EC(l)

                    U4 = U4 + Di*Dj*Dk*Dl * Uijkl(a1)
                    a1=a1+1

                 End do
              End do
           End do
        End do
        V = V + U4

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_PES
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! qq / Bohr(emu)^1/2
!  V / Hartree

  Subroutine qff_getV1(ll,qq,V,Ni)

  USE qff_mod

  Implicit None

     Integer :: ll
     Real(8) :: qq(Nfree),V

     Integer :: i,Ni
     Real(8) :: Qi,G1,H1,T1,U1

!--------------------------------------------------------------------------------
!

        Ni=type_mode1(type_mode_idx1(type_qff)+ll)
        Qi=qq(Ni)
        
        i=Ni
        G1 = Qi * Gi(i)
        H1 = Qi*Qi * Hii(i)
        T1 = Qi*Qi*Qi * Tiii(i)
        U1 = Qi*Qi*Qi*Qi * Uiiii(i)

        V = E0 + G1 + H1 + T1 + U1

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getV1
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! qq / Bohr(emu)^1/2
!  V / Hartree

  Subroutine qff_getVG1(ll,qq,V,G,Ni)

  USE qff_mod

  Implicit None

     Integer :: ll,Ni
     Real(8) :: qq(Nfree),V,G

     Integer :: i
     Real(8) :: Qi,G1,H1,T1,U1

!--------------------------------------------------------------------------------
!

        Call qff_getV1(ll,qq,V,Ni)

        Qi=qq(Ni)

        i=Ni
        G1 = Gi(i)
        H1 = 2.D+00 * Qi * Hii(i)
        T1 = 3.D+00 * Qi*Qi * Tiii(i)
        U1 = 4.D+00 * Qi*Qi*Qi * Uiiii(i)

        G = G1 + H1 + T1 + U1

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVG1
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
! qq / Bohr(emu)^1/2
!  V / Hartree

  Subroutine qff_getVGH1(ll,qq,V,G,H,Ni)

  USE qff_mod

  Implicit None

     Integer :: ll,Ni
     Real(8) :: qq(Nfree),V,G,H

     Integer :: i
     Real(8) :: Qi,H1,T1,U1

!--------------------------------------------------------------------------------
!

        Call qff_getVG1(ll,qq,V,G,Ni)

        Qi=qq(Ni)
        i=Ni
        H1 =  2.D+00 * Hii(i)
        T1 =  6.D+00 * Qi * Tiii(i)
        U1 = 12.D+00 * Qi*Qi * Uiiii(i)

        H = H1 + T1 + U1

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVGH1
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getV2(ll,qq,V,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(2)
     Real(8) :: qq(Nfree),V

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj
     Real(8) :: Qi,Qj,H2,T2,U21,U22
!
!--------------------------------------------------------------------------------
!

    ! Index of normal modes (Ni>Nj)
        Ni=type_mode2(1,type_mode_idx2(type_qff)+ll)
        Nj=type_mode2(2,type_mode_idx2(type_qff)+ll)
        nn(1)=Ni
        nn(2)=Nj
        Qi=qq(Ni)
        Qj=qq(Nj)

        a1=(Ni-2)*(Ni-1)/2 + Nj
        a2=a1*2 -1

        H2 = Qi*Qj * Hij(a1)
        U21= Qi*Qi*Qj*Qj * Uiijj(a1)
        T2 = Qi*Qi*Qj * Tiij(a2)
        T2 = T2 + Qj*Qj*Qi * Tiij(a2+1)
        U22= Qi*Qi*Qi*Qj * Uiiij(a2)
        U22= U22 + Qj*Qj*Qj*Qi * Uiiij(a2+1)
   
        V = H2 + T2 + U21 + U22

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getV2
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getVG2(ll,qq,V,G,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(2)
     Real(8) :: qq(Nfree),V,G(2)

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj
     Real(8) :: Qi,Qj,H2,T2,U21,U22
!
!--------------------------------------------------------------------------------
!

        Call qff_getV2(ll,qq,V,nn)
        Ni=nn(1)
        Nj=nn(2)
        Qi=qq(Ni)
        Qj=qq(Nj)

        a1=(Ni-2)*(Ni-1)/2 + Nj
        a2=a1*2 -1

      ! G(Ni)
        H2 = Qj * Hij(a1)
        U21= 2.D+00 * Qi*Qj*Qj * Uiijj(a1)
        T2 = 2.D+00 * Qi*Qj * Tiij(a2)
        T2 = T2 + Qj*Qj * Tiij(a2+1)
        U22= 3.D+00 * Qi*Qi*Qj * Uiiij(a2)
        U22= U22 + Qj*Qj*Qj * Uiiij(a2+1)
        G(1) = H2 + T2 + U21 + U22

      ! G(Nj)
        H2 = Qi * Hij(a1)
        U21= 2.D+00 * Qi*Qi*Qj * Uiijj(a1)
        T2 = Qi*Qi * Tiij(a2)
        T2 = T2 + 2.D+00 * Qj*Qi * Tiij(a2+1)
        U22= Qi*Qi*Qi * Uiiij(a2)
        U22= U22 + 3.D+00 * Qj*Qj*Qi * Uiiij(a2+1)
        G(2) = H2 + T2 + U21 + U22
!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVG2
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getVGH2(ll,qq,V,G,H,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(2)
     Real(8) :: qq(Nfree),V,G(2),H(2,2)

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj
     Real(8) :: Qi,Qj,H2,T2,U21,U22
!
!--------------------------------------------------------------------------------
!

        Call qff_getVG2(ll,qq,V,G,nn)
        Ni=nn(1)
        Nj=nn(2)
        Qi=qq(Ni)
        Qj=qq(Nj)

        a1=(Ni-2)*(Ni-1)/2 + Nj
        a2=a1*2 -1

      ! H(Ni,Ni)
        U21= 2.D+00 * Qj*Qj * Uiijj(a1)
        T2 = 2.D+00 * Qj * Tiij(a2)
        U22= 6.D+00 * Qi*Qj * Uiiij(a2)
        H(1,1) = T2 + U21 + U22

      ! H(Ni,Nj)
        H2 = Hij(a1)
        U21= 4.D+00 * Qi*Qj * Uiijj(a1)
        T2 = 2.D+00 * Qi * Tiij(a2)
        T2 = T2 + 2.D+00 * Qj * Tiij(a2+1)
        U22= 3.D+00 * Qi*Qi * Uiiij(a2)
        U22= U22 + 3.D+00 * Qj*Qj * Uiiij(a2+1)
        H(1,2) = H2 + T2 + U21 + U22
        H(2,1) = H(1,2)

      ! H(Nj,Nj)
        U21= 2.D+00 * Qi*Qi * Uiijj(a1)
        T2 = 2.D+00 * Qi * Tiij(a2+1)
        U22= 6.D+00 * Qj*Qi * Uiiij(a2+1)
        H(2,2) = T2 + U21 + U22
!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVGH2
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getV3(ll,qq,V,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(3)
     Real(8) :: qq(Nfree),V

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj,Nk
     Real(8) :: Qi,Qj,QK,T3,U3,U4

!--------------------------------------------------------------------------------

    ! Index of normal modes (Ni>Nj>Nk)
        Ni=type_mode3(1,type_mode_idx3(type_qff)+ll)
        Nj=type_mode3(2,type_mode_idx3(type_qff)+ll)
        Nk=type_mode3(3,type_mode_idx3(type_qff)+ll)
        nn(1)=Ni
        nn(2)=Nj
        nn(3)=Nk
        Qi=qq(Ni)
        Qj=qq(Nj)
        Qk=qq(Nk)

        a1=(Ni-3)*(Ni-2)*(Ni-1)/6 + (Nj-2)*(Nj-1)/2 + Nk
        a2=(Ni-3)*(Ni-2)*(Ni-1)/2 + (Nj-2)*(Nj-1)/2*3 + Nk*3 - 2

        T3 = T3 + Qi*Qj*Qk * Tijk(a1)
        U3 = U3 + Qi*Qi*Qj*Qk * Uiijk(a2)
        U3 = U3 + Qj*Qj*Qk*Qi * Uiijk(a2+1)
        U3 = U3 + Qk*Qk*Qi*Qj * Uiijk(a2+2)
   
        V =T3 + U3

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getV3
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getVG3(ll,qq,V,G,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(3)
     Real(8) :: qq(Nfree),V,G(3)

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj,Nk
     Real(8) :: Qi,Qj,QK,T3,U3,U4

!--------------------------------------------------------------------------------

        Call qff_getV3(ll,qq,V,nn)
        Ni=nn(1)
        Nj=nn(2)
        Nk=nn(3)
        Qi=qq(Ni)
        Qj=qq(Nj)
        Qk=qq(Nk)

        a1=(Ni-3)*(Ni-2)*(Ni-1)/6 + (Nj-2)*(Nj-1)/2 + Nk
        a2=(Ni-3)*(Ni-2)*(Ni-1)/2 + (Nj-2)*(Nj-1)/2*3 + Nk*3 - 2

      ! G(Ni)
        T3 = Qj*Qk * Tijk(a1)
        U3 = 2.D+00 * Qi*Qj*Qk * Uiijk(a2)
        U3 = U3 + Qj*Qj*Qk * Uiijk(a2+1)
        U3 = U3 + Qk*Qk*Qj * Uiijk(a2+2)
        G(1) =T3 + U3

      ! G(Nj)
        T3 = Qi*Qk * Tijk(a1)
        U3 = Qi*Qi*Qk * Uiijk(a2)
        U3 = U3 + 2.D+00 * Qj*Qk*Qi * Uiijk(a2+1)
        U3 = U3 + Qk*Qk*Qi * Uiijk(a2+2)
        G(2) =T3 + U3

      ! G(Nk)
        T3 = Qi*Qj * Tijk(a1)
        U3 = Qi*Qi*Qj * Uiijk(a2)
        U3 = U3 + Qj*Qj*Qi * Uiijk(a2+1)
        U3 = U3 + 2.D+00 * Qk*Qi*Qj * Uiijk(a2+2)
        G(3) =T3 + U3
!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVG3
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getVGH3(ll,qq,V,G,H,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(3)
     Real(8) :: qq(Nfree),V,G(3),H(3,3)

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj,Nk
     Real(8) :: Qi,Qj,QK,T3,U3,U4

!--------------------------------------------------------------------------------

        Call qff_getVG3(ll,qq,V,G,nn)
        Ni=nn(1)
        Nj=nn(2)
        Nk=nn(3)
        Qi=qq(Ni)
        Qj=qq(Nj)
        Qk=qq(Nk)

        a1=(Ni-3)*(Ni-2)*(Ni-1)/6 + (Nj-2)*(Nj-1)/2 + Nk
        a2=(Ni-3)*(Ni-2)*(Ni-1)/2 + (Nj-2)*(Nj-1)/2*3 + Nk*3 - 2

      ! H(Ni,Ni)
        H(1,1) = 2.D+00 * Qj*Qk * Uiijk(a2)

      ! H(Ni,Nj)
        T3 = Qk * Tijk(a1)
        U3 = 2.D+00 * Qi*Qk * Uiijk(a2)
        U3 = U3 + 2.D+00 * Qj*Qk * Uiijk(a2+1)
        U3 = U3 + Qk*Qk * Uiijk(a2+2)
        H(1,2) = T3 + U3
        H(2,1) = H(1,2)

      ! H(Ni,Nk)
        T3 = Qj * Tijk(a1)
        U3 = 2.D+00 * Qi*Qj * Uiijk(a2)
        U3 = U3 + Qj*Qj * Uiijk(a2+1)
        U3 = U3 + 2.D+00 * Qk*Qj * Uiijk(a2+2)
        H(1,3) = T3 + U3
        H(3,1) = H(1,3)

      ! H(Nj,Nj)
        H(2,2) = 2.D+00 * Qk*Qi * Uiijk(a2+1)

      ! H(Nj,Nk)
        T3 = Qi * Tijk(a1)
        U3 = Qi*Qi * Uiijk(a2)
        U3 = U3 + 2.D+00 * Qj*Qi * Uiijk(a2+1)
        U3 = U3 + 2.D+00 * Qk*Qi * Uiijk(a2+2)
        H(2,3) = T3 + U3
        H(3,2) = H(2,3)

      ! H(Nk,Nk)
        H(3,3) = 2.D+00 * Qi*Qj * Uiijk(a2+2)

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVGH3
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getV4(ll,qq,V,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(4)
     Real(8) :: qq(Nfree),V

     Integer :: i,j,k,l,n,a1,a2,Ni,Nj,Nk,Nl
     Real(8) :: Qi,Qj,Qk,Ql,U4

!--------------------------------------------------------------------------------

    ! Index of normal modes (Ni>Nj>Nk>Nl)
        Ni=type_mode4(1,type_mode_idx4(type_qff)+ll)
        Nj=type_mode4(2,type_mode_idx4(type_qff)+ll)
        Nk=type_mode4(3,type_mode_idx4(type_qff)+ll)
        Nl=type_mode4(4,type_mode_idx4(type_qff)+ll)
        nn(1)=Ni
        nn(2)=Nj
        nn(3)=Nk
        nn(4)=Nl
        Qi=qq(Ni)
        Qj=qq(Nj)
        Qk=qq(Nk)
        Ql=qq(Nl)

        a1=(Ni-4)*(Ni-3)*(Ni-2)*(Ni-1)/24 + (Nj-3)*(Nj-2)*(Nj-1)/6  &
          +(Nk-2)*(Nk-1)/2 + Nl
   
        V = Qi*Qj*Qk*Ql * Uijkl(a1)

!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getV4
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getVG4(ll,qq,V,G,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(4)
     Real(8) :: qq(Nfree),V,G(4)

     Integer :: i,j,k,l,n,a1,Ni,Nj,Nk,Nl
     Real(8) :: Qi,Qj,Qk,Ql

!--------------------------------------------------------------------------------

        Call qff_getV4(ll,qq,V,nn)
        Ni=nn(1)
        Nj=nn(2)
        Nk=nn(3)
        Nl=nn(4)
        Qi=qq(Ni)
        Qj=qq(Nj)
        Qk=qq(Nk)
        Ql=qq(Nl)

        a1=(Ni-4)*(Ni-3)*(Ni-2)*(Ni-1)/24 + (Nj-3)*(Nj-2)*(Nj-1)/6 &
         + (Nk-2)*(Nk-1)/2 + Nl

      ! G(Ni)
        G(1) = Qj*Qk*Ql * Uijkl(a1)
      ! G(Nj)
        G(2) = Qi*Qk*Ql * Uijkl(a1)
      ! G(Nk)
        G(3) = Qi*Qj*Ql * Uijkl(a1)
      ! G(Nl)
        G(4) = Qi*Qj*Qk * Uijkl(a1)
!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVG4
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
  Subroutine qff_getVGH4(ll,qq,V,G,H,nn)
!
  USE qff_mod
!
  Implicit None

     Integer :: ll,nn(4)
     Real(8) :: qq(Nfree),V,G(4),H(4,4)

     Integer :: i,j,k,l,n,a1,Ni,Nj,Nk,Nl
     Real(8) :: Qi,Qj,Qk,Ql

!--------------------------------------------------------------------------------

        Call qff_getVG4(ll,qq,V,G,nn)
        Ni=nn(1)
        Nj=nn(2)
        Nk=nn(3)
        Nl=nn(4)
        Qi=qq(Ni)
        Qj=qq(Nj)
        Qk=qq(Nk)
        Ql=qq(Nl)

        a1=(Ni-4)*(Ni-3)*(Ni-2)*(Ni-1)/24 + (Nj-3)*(Nj-2)*(Nj-1)/6 &
         + (Nk-2)*(Nk-1)/2 + Nl

      ! H(Ni,Ni)
        Do i=1,4
           H(i,i) = 0.0D+00
        End do

      ! H(Ni,Nj)
        H(1,2) = Qk*Ql* Uijkl(a1)
        H(2,1) = H(1,2)

      ! H(Ni,Nk)
        H(1,3) = Qj*Ql* Uijkl(a1)
        H(3,1) = H(1,3)

      ! H(Ni,Nl)
        H(1,4) = Qj*Qk* Uijkl(a1)
        H(4,1) = H(1,4)

      ! H(Nj,Nk)
        H(2,3) = Qi*Ql* Uijkl(a1)
        H(3,2) = H(2,3)

      ! H(Nj,Nl)
        H(2,4) = Qi*Qk* Uijkl(a1)
        H(4,2) = H(2,4)

      ! H(Nk,Nl)
        H(3,4) = Qi*Qj* Uijkl(a1)
        H(4,3) = H(3,4)
!
!--------------------------------------------------------------------------------
!
     return
!
!
  End subroutine qff_getVGH4
!
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetGi(i)
!
  USE qff_mod

  Implicit None

    Integer :: i

    Real(8) :: qff_GetGi

       qff_GetGi=Gi(i)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetHii(i)
!
  USE qff_mod

  Implicit None

    Integer :: i

    Real(8) :: qff_GetHii

       qff_GetHii=Hii(i)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetTiii(i)
!
  USE qff_mod

  Implicit None

    Integer :: i

    Real(8) :: qff_GetTiii

       qff_GetTiii=Tiii(i)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetUiiii(i)
!
  USE qff_mod

  Implicit None

    Integer :: i

    Real(8) :: qff_GetUiiii

       qff_GetUiiii=Uiiii(i)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetHij(k)
!
  USE qff_mod

  Implicit None

    ! k = (i-1)*(i-2)/2 + j  (i>j)
    Integer :: k

    Real(8) :: qff_GetHij

       qff_GetHij=Hij(k)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetUiijj(k)
!
  USE qff_mod

  Implicit None

    ! k = (i-1)*(i-2)/2 + j  (i>j)
    Integer :: k

    Real(8) :: qff_GetUiijj

       qff_GetUiijj=Uiijj(k)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetTiij(k,iopt)
!
  USE qff_mod

  Implicit None

    ! k = (i-1)*(i-2)/2 + j  (i>j)
    Integer :: k
    ! iopt =0 : tiij, =1: tjji
    Integer :: iopt

    Real(8) :: qff_GetTiij

       qff_GetTiij=Tiij(2*k-1+iopt)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetUiiij(k,iopt)
!
  USE qff_mod

  Implicit None

    ! k = (i-1)*(i-2)/2 + j  (i>j)
    Integer :: k
    ! iopt =0 : uiiij, =1: ujjji
    Integer :: iopt

    Real(8) :: qff_GetUiiij

       qff_GetUiiij=Uiiij(2*k-1+iopt)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetTijk(l)
!
  USE qff_mod

  Implicit None

    ! l = (i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k  (i>j>k)
    Integer :: l

    Real(8) :: qff_GetTijk

       qff_GetTijk=Tijk(l)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetUiijk(l,iopt)
!
  USE qff_mod

  Implicit None

    ! l = (i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k  (i>j>k)
    Integer :: l
    ! iopt =0 : uiijk, =1: uijjk, =2: uijkk
    Integer :: iopt

    Real(8) :: qff_GetUiijk

       qff_GetUiijk=Uiijk(3*l-2+iopt)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Function qff_GetUijkl(l)
!
  USE qff_mod

  Implicit None

    ! l = (i-1)*(i-2)*(i-3)*(i-4)/24 + (j-1)*(j-2)*(j-3)/6 + (k-1)*(k-2)/2
    !     + l  (i>j>k>l)
    Integer :: l

    Real(8) :: qff_GetUijkl

       qff_GetUijkl=Uijkl(l)

  End function
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  Subroutine qff_getTitle(ch1,ch2,ch3,ch4)
!
  USE qff_mod

  Implicit None

    Character :: ch1*(*),ch2*(*),ch3*(*),ch4*(*)

       ch1=tl1; ch2=tl2; ch3=tl3; ch4=tl4

  End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
