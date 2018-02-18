!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/07/19
!   Copyright 2013
!   Code description by K.Yagi and H.Otaki
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Module Ocvscf_coeff_mod

   Integer :: Nf,Nf2,Nff1,Nff2,Nff3,Nff4
   Real(8), allocatable :: di(:),dii(:),diii(:),diiii(:), &
                    dij(:),diij(:),dijj(:),diijj(:),diiij(:),dijjj(:), &
                    dijk(:),diijk(:),dijjk(:),dijkk(:), &
                    dijkl(:)
   Real(8), allocatable :: cs(:),cst(:,:),cstu(:,:,:),cstuv(:,:,:,:)
   Real(8), allocatable :: Umat(:,:)
   Character(80) :: title
   Integer :: icff

   Contains 

   Subroutine cubic(vv,i,j,k)

   Implicit None

   Integer :: i,j,k
   Real(8) :: vv

      cstu(i,j,k)=vv
      cstu(i,k,j)=vv
      cstu(j,i,k)=vv
      cstu(j,k,i)=vv
      cstu(k,i,j)=vv
      cstu(k,j,i)=vv

   End subroutine

   Subroutine quartic(vv,i,j,k,l)

   Implicit None

   Integer :: i,j,k,l
   Real(8) :: vv

      cstuv(i,j,k,l)=vv
      cstuv(i,j,l,k)=vv
      cstuv(i,k,j,l)=vv
      cstuv(i,k,l,j)=vv
      cstuv(i,l,j,k)=vv
      cstuv(i,l,k,j)=vv
      cstuv(j,i,k,l)=vv
      cstuv(j,i,l,k)=vv
      cstuv(j,k,i,l)=vv
      cstuv(j,k,l,i)=vv
      cstuv(j,l,i,k)=vv
      cstuv(j,l,k,i)=vv
      cstuv(k,i,j,l)=vv
      cstuv(k,i,l,j)=vv
      cstuv(k,j,i,l)=vv
      cstuv(k,j,l,i)=vv
      cstuv(k,l,i,j)=vv
      cstuv(k,l,j,i)=vv
      cstuv(l,i,j,k)=vv
      cstuv(l,i,k,j)=vv
      cstuv(l,j,i,k)=vv
      cstuv(l,j,k,i)=vv
      cstuv(l,k,i,j)=vv
      cstuv(l,k,j,i)=vv

   End subroutine
   
   Subroutine getVec2(i1,i2,vec2)

   Implicit None

      Integer :: i1,i2,i3,s1,s2,s3,nn
      Real(8) :: aa,vec2(Nff2)
      
      nn=1
      Do s1=1,Nff1
         Do s2=1,Nff1
            vec2(nn)=Umat(s1,i1)*Umat(s2,i2)
            nn=nn+1
         End do
      End do

   End subroutine

   Subroutine getVec3(i1,i2,i3,vec3)

   Implicit None

      Integer :: i1,i2,i3,s1,s2,s3,nn
      Real(8) :: aa,vec3(Nff3)
      
      vec3=0.D+00

      nn=1
      Do s1=1,Nff1
         Do s2=1,Nff1
            aa=Umat(s1,i1)*Umat(s2,i2)
            Call daxpy(Nff1,aa,Umat(:,i3),1,vec3(nn:),1)
            nn=nn+Nff1
         End do
      End do

   End subroutine

   Subroutine getVec3b(i1,vec2,vec3)

   Implicit None

      Integer :: i1,s1,nn
      Real(8) :: vec3(Nff3),vec2(Nff2)
      
      vec3=0.D+00

      nn=1
      Do s1=1,Nff1
         Call daxpy(Nff2,Umat(s1,i1),vec2,1,vec3(nn:),1)
         nn=nn+Nff2
      End do

   End subroutine

   Subroutine getVec4(i1,i2,i3,i4,vec4)

   Implicit None

      Integer :: i1,i2,i3,i4,s1,s2,s3,s4,nn
      Real(8) :: aa,bb,vec4(Nff4)
      
      vec4=0.D+00 

      nn=1
      Do s1=1,Nff1
         Do s2=1,Nff1
            bb=Umat(s1,i1)*Umat(s2,i2)
            Do s3=1,Nff1
               aa=bb*Umat(s3,i3)
               Call daxpy(Nff1,aa,Umat(:,i4),1,vec4(nn:),1)
               nn=nn+Nff1
            End do
         End do
      End do

   End subroutine

   Subroutine getVec4b(i1,i2,vec2,vec4)

   Implicit None

      Integer :: i1,i2,s1,s2,n1
      Real(8) :: aa,vec2(Nff2),vec4(Nff4)
      
      vec4=0.D+00

      n1=1
      Do s1=1,Nff1
         Do s2=1,Nff1
            aa=Umat(s2,i2)*Umat(s1,i1)
            Call daxpy(Nff2,aa,vec2,1,vec4(n1:),1)
            n1=n1+Nff2
         End do
      End do

   End subroutine

   Subroutine getVec4c(i1,vec3,vec4)

   Implicit None

      Integer :: i1,s1,n1
      Real(8) :: vec3(Nff3),vec4(Nff4)
      
      vec4=0.D+00

      n1=1
      Do s1=1,Nff1
         Call daxpy(Nff3,Umat(s1,i1),vec3,1,vec4(n1:),1)
         n1=n1+Nff3
      End do

   End subroutine

   End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!Program test
!
!   Character :: fname*80
!   Integer :: pp(2),i
!   Real(8) :: PI,ap,U1(9,9)
!
!      PI=Acos(-1.0D+00)
!      ap=24.0D+00/180.D+00*PI
!
!      U1=0.D+00
!      Do i=1,9
!         U1(i,i)=1.D+00
!      End do
!
!      Call Ocvscf_coeff_Init(9)
!
!      fname='prop_no_1.mop'
!      Call Ocvscf_coeff_readMop(fname)
!
!      Call Ocvscf_coeff_copyD2C()
!      !Call Ocvscf_coeff_copyC2D()
!
!      !pp=(/3,5/)
!      !Call Ocvscf_coeff_rotate(pp,ap)
!      !Call Ocvscf_coeff_copyD2C()
!      !pp=(/4,6/)
!      !Call Ocvscf_coeff_rotate(pp,ap)
!
!      pp=(/3,5/)
!      U1(pp(1),pp(1))=cos(ap)
!      U1(pp(2),pp(2))=cos(ap)
!      U1(pp(2),pp(1))=sin(ap)
!      U1(pp(1),pp(2))=-sin(ap)
!      pp=(/4,6/)
!      U1(pp(1),pp(1))=cos(ap)
!      U1(pp(2),pp(2))=cos(ap)
!      U1(pp(2),pp(1))=sin(ap)
!      U1(pp(1),pp(2))=-sin(ap)
!      Call Ocvscf_coeff_transC2D(U1)
!
!      Call Ocvscf_coeff_writeForce()
!      !fname='prop_no_1.mop_new'
!      !Call Ocvscf_coeff_writeMop(fname)
!      Call Ocvscf_coeff_Finalz()
!      
!End
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_Init(Nfree,icffin)

   USE Ocvscf_coeff_mod
   USE Constants_mod

   Implicit None

   Integer :: Nfree,icffin
   Integer :: Nf3,Nf4,i

      icff=icffin

      Nf=Nfree
      Nf2=Nfree*(Nfree-1)/2
      Nf3=Nfree*(Nfree-1)*(Nfree-2)/6
      Nf4=Nfree*(Nfree-1)*(Nfree-2)*(Nfree-3)/24

      Call Mem_alloc(-1,i,'D',Nf*3)
      Allocate(di(Nf))
      Allocate(dii(Nf))
      Allocate(diii(Nf))
         
      Call Mem_alloc(-1,i,'D',Nf2*3)
      Allocate(dij(Nf2))
      Allocate(diij(Nf2))
      Allocate(dijj(Nf2))
         
      Call Mem_alloc(-1,i,'D',Nf3)
      Allocate(dijk(Nf3))

      Call Mem_alloc(-1,i,'D',Nf + Nf*Nf + Nf*Nf*Nf)
      Allocate(cs(Nf),cst(Nf,Nf),cstu(Nf,Nf,Nf))

      if(icff==0) then ! QFF

         Call Mem_alloc(-1,i,'D',Nf)
         Allocate(diiii(Nf))
         
         Call Mem_alloc(-1,i,'D',Nf2*3)
         Allocate(diijj(Nf2))
         Allocate(diiij(Nf2))
         Allocate(dijjj(Nf2))
         
         Call Mem_alloc(-1,i,'D',Nf3*3)
         Allocate(diijk(Nf3))
         Allocate(dijjk(Nf3))
         Allocate(dijkk(Nf3))
         
         Call Mem_alloc(-1,i,'D',Nf4)
         Allocate(dijkl(Nf4))
            
         Call Mem_alloc(-1,i,'D',Nf*Nf*Nf*Nf)
         Allocate(cstuv(Nf,Nf,Nf,Nf))
      
      end if

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_Finalz()

   USE Ocvscf_coeff_mod

   Implicit None

      Call Mem_dealloc('D',size(di)) 
      Deallocate(di)
      Call Mem_dealloc('D',size(dii)) 
      Deallocate(dii)
      Call Mem_dealloc('D',size(diii)) 
      Deallocate(diii)
      
      Call Mem_dealloc('D',size(dij)) 
      Deallocate(dij)
      Call Mem_dealloc('D',size(diij)) 
      Deallocate(diij)
      Call Mem_dealloc('D',size(dijj)) 
      Deallocate(dijj)
      
      Call Mem_dealloc('D',size(dijk)) 
      Deallocate(dijk)
      
      Call Mem_dealloc('D',size(cs)) 
      Call Mem_dealloc('D',size(cst)) 
      Call Mem_dealloc('D',size(cstu)) 
      Deallocate(cs,cst,cstu)

      if(icff==0) then
         
         Call Mem_dealloc('D',size(diiii)) 
         Deallocate(diiii)
         
         Call Mem_dealloc('D',size(diijj)) 
         Deallocate(diijj)
         Call Mem_dealloc('D',size(diiij)) 
         Deallocate(diiij)
         Call Mem_dealloc('D',size(dijjj)) 
         Deallocate(dijjj)

         Call Mem_dealloc('D',size(diijk)) 
         Deallocate(diijk)
         Call Mem_dealloc('D',size(dijjk)) 
         Deallocate(dijjk)
         Call Mem_dealloc('D',size(dijkk)) 
         Deallocate(dijkk)

         Call Mem_dealloc('D',size(dijkl)) 
         Deallocate(dijkl)

         Call Mem_dealloc('D',size(cstuv)) 
         Deallocate(cstuv)

      end if

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_readMop(fname)

   USE Ocvscf_coeff_mod

   Implicit None

   Character :: fname*80,PotDir*80
   Integer :: i,j,k,l,m,Nfree

   Real(8) :: sqomg(Nf),H2wvn,aa
   Real(8) :: si,sj,sk,sl,sii,sjj,skk

      Nfree=Nf

      Call GetEnv('POTDIR',PotDir)
      i=Len_trim(PotDir)
      if(i /=0) then
         PotDir(i+1:)='/'
         i=i+1
      else
         PotDir='./'
         i=2
      endif

      Open(10,file=PotDir(:i)//fname,status='old')
      read(10,*)
      Do i=1,Nfree
         read(10,*) sqomg(i)
         !aa=sqomg(i)*H2wvn()
         !write(6,'(f12.4)') aa
         sqomg(i)=sqrt(sqomg(i))
      End do

      read(10,'(17x,a)') title

      if(icff==0) then

         Do i=1,Nfree
            si=sqomg(i)
            sii=si*si

            read(10,*) di(i)
            read(10,*) dii(i)
            read(10,*) diii(i)
            read(10,*) diiii(i)

            di(i)=di(i)*si
            dii(i)=dii(i)*sii
            diii(i)=diii(i)*sii*si
            diiii(i)=diiii(i)*sii*sii

         End do

         k=1
         Do i=1,Nfree
            si=sqomg(i)
            sii=si*si
         Do j=1,i-1
            sj=sqomg(j)
            sjj=sj*sj

            read(10,*) dij(k)
            read(10,*) diij(k)
            read(10,*) diiij(k)
            read(10,*) dijj(k)
            read(10,*) diijj(k)
            read(10,*) dijjj(k)

            dij(k)=dij(k)*si*sj
            diij(k)=diij(k)*sii*sj
            dijj(k)=dijj(k)*si*sjj
            diiij(k)=diiij(k)*sii*si*sj
            dijjj(k)=dijjj(k)*si*sjj*sj
            diijj(k)=diijj(k)*sii*sjj

            k=k+1

         End do
         End do

         l=1
         Do i=1,Nfree
            si=sqomg(i)
            sii=si*si
         Do j=1,i-1
            sj=sqomg(j)
            sjj=sj*sj
         Do k=1,j-1
            sk=sqomg(k)
            skk=sk*sk

            read(10,*) dijk(l)
            read(10,*) diijk(l)
            read(10,*) dijjk(l)
            read(10,*) dijkk(l)

            dijk(l)=dijk(l)*si*sj*sk
            diijk(l)=diijk(l)*sii*sj*sk
            dijjk(l)=dijjk(l)*si*sjj*sk
            dijkk(l)=dijkk(l)*si*sj*skk

            l=l+1

         End do
         End do
         End do

         dijkl=0.D+00
         m=1
         Do i=1,Nf
            si=sqomg(i)
         Do j=1,i-1
            sj=sqomg(j)
         Do k=1,j-1
            sk=sqomg(k)
         Do l=1,k-1
            sl=sqomg(l)

            read(10,*,end=10) dijkl(m)
            dijkl(m)=dijkl(m)*si*sj*sk*sl
            m=m+1

         End do
         End do
         End do
         End do

      else ! CFF

         Do i=1,Nfree
            si=sqomg(i)
            sii=si*si

            read(10,*) di(i)
            read(10,*) dii(i)
            read(10,*) diii(i)
            read(10,*)

            di(i)=di(i)*si
            dii(i)=dii(i)*sii
            diii(i)=diii(i)*sii*si

         End do

         k=1
         Do i=1,Nfree
            si=sqomg(i)
            sii=si*si
         Do j=1,i-1
            sj=sqomg(j)
            sjj=sj*sj
         
            read(10,*) dij(k)
            read(10,*) diij(k)
            read(10,*)
            read(10,*) dijj(k)
            read(10,*)
            read(10,*)

            dij(k)=dij(k)*si*sj
            diij(k)=diij(k)*sii*sj
            dijj(k)=dijj(k)*si*sjj

            k=k+1

         End do
         End do

         l=1
         Do i=1,Nfree
            si=sqomg(i)
            sii=si*si
         Do j=1,i-1
            sj=sqomg(j)
            sjj=sj*sj
         Do k=1,j-1
            sk=sqomg(k)
            skk=sk*sk

            read(10,*) dijk(l)
            read(10,*)
            read(10,*)
            read(10,*)

            dijk(l)=dijk(l)*si*sj*sk

            l=l+1

         End do
         End do
         End do

      end if

   10 Continue

      close(10)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  Used for debug only
!
   Subroutine Ocvscf_coeff_writeForce()

   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: i,j,k,l,nn

      if(icff==0) then

         Do i=1,Nf
            Write(6,'(i4,12x,e20.10)') i,di(i)
            Write(6,'(2i4,8x,e20.10)') i,i,dii(i)
            Write(6,'(3i4,4x,e20.10)') i,i,i,diii(i)
            Write(6,'(4i4,e20.10)') i,i,i,i,diiii(i)
         End do
         
         nn=1
         Do i=1,Nf
         Do j=1,i-1
            Write(6,'(2i4,8x,e20.10)') i,j,dij(nn)
            Write(6,'(3i4,4x,e20.10)') i,i,j,diij(nn)
            Write(6,'(3i4,4x,e20.10)') i,j,j,dijj(nn)
            Write(6,'(4i4,e20.10)') i,i,i,j,diiij(nn)
            Write(6,'(4i4,e20.10)') i,i,j,j,diijj(nn)
            Write(6,'(4i4,e20.10)') i,j,j,j,dijjj(nn)
            nn=nn+1
         End do
         End do
         
         nn=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
            Write(6,'(3i4,4x,e20.10)') i,j,k,dijk(nn)
            Write(6,'(4i4,e20.10)') i,i,j,k,diijk(nn)
            Write(6,'(4i4,e20.10)') i,j,j,k,dijjk(nn)
            Write(6,'(4i4,e20.10)') i,j,k,k,dijkk(nn)
            nn=nn+1
         End do
         End do
         End do

         nn=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
         Do l=1,k-1
            Write(6,'(4i4,e20.10)') i,j,k,l,dijkl(nn)
            nn=nn+1
         End do
         End do
         End do
         End do
      
      else ! CFF

         Do i=1,Nf
            Write(6,'(i4,12x,e20.10)') i,di(i)
            Write(6,'(2i4,8x,e20.10)') i,i,dii(i)
            Write(6,'(3i4,4x,e20.10)') i,i,i,diii(i)
         End do
         
         nn=1
         Do i=1,Nf
         Do j=1,i-1
            Write(6,'(2i4,8x,e20.10)') i,j,dij(nn)
            Write(6,'(3i4,4x,e20.10)') i,i,j,diij(nn)
            Write(6,'(3i4,4x,e20.10)') i,j,j,dijj(nn)
            nn=nn+1
         End do
         End do
         
         nn=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
            Write(6,'(3i4,4x,e20.10)') i,j,k,dijk(nn)
            nn=nn+1
         End do
         End do
         End do

      end if


   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_writeMop(fname)

   USE Ocvscf_coeff_mod
   USE Constants_mod

   Implicit None

   Character :: fname*80,PotDir*80
   Integer :: i,i2,j2,k2,l2,nn,modes(Nf)
   Real(8) :: sqomg2(Nf),si,sj,sk,sl,sii,sjj,skk

      Do i=1,Nf
         modes(i)=i
      End do

      Call GetEnv('POTDIR',PotDir)
      i=Len_trim(PotDir)
      if(i /=0) then
         PotDir(i+1:)='/'
         i=i+1
      else
         PotDir='./'
         i=2
      endif

      Open(10,file=PotDir(:i)//fname,status='unknown')

      if(Nf<10) then
         write(10,'(''SCALING FREQUENCIES N_FRQS='',i1)') Nf
      elseif(Nf<100) then
         write(10,'(''SCALING FREQUENCIES N_FRQS='',i2)') Nf
      else
         write(10,'(''SCALING FREQUENCIES N_FRQS='',i3)') Nf
      endif
      Do i=1,Nf
         sqomg2(i)=sqrt(abs(dii(modes(i))*2.0D+00))
         write(10,'(e28.22)') sqomg2(i)
         sqomg2(i)=sqrt(sqomg2(i))
      End do

      write(10,'(''DALTON_FOR_MIDAS '',a)') trim(title)

      if(icff==0) then ! QFF
         Do i2=1,Nf
            si=sqomg2(i2)
            sii=si*si
            Write(10,'(e28.22,5i5)') di(modes(i2))/si,i2
            Write(10,'(e28.22,5i5)') dii(modes(i2))/sii,i2,i2
            Write(10,'(e28.22,5i5)') diii(modes(i2))/sii/si,i2,i2,i2
            Write(10,'(e28.22,5i5)') diiii(modes(i2))/sii/sii,i2,i2,i2,i2
         End do

         nn=1
         Do i2=1,Nf
            si=sqomg2(i2)
            sii=si*si
         Do j2=1,i2-1
            sj=sqomg2(j2)
            sjj=sj*sj
            Write(10,'(e28.22,5i5)') dij(nn)/si/sj,j2,i2
            Write(10,'(e28.22,5i5)') diij(nn)/sii/sj,j2,i2,i2
            Write(10,'(e28.22,5i5)') diiij(nn)/sii/si/sj,j2,i2,i2,i2
            Write(10,'(e28.22,5i5)') dijj(nn)/si/sjj,j2,j2,i2
            Write(10,'(e28.22,5i5)') diijj(nn)/sii/sjj,j2,j2,i2,i2
            Write(10,'(e28.22,5i5)') dijjj(nn)/si/sjj/sj,j2,j2,j2,i2
            nn=nn+1
         End do
         End do

         nn=1
         Do i2=1,Nf
            si=sqomg2(i2)
            sii=si*si
         Do j2=1,i2-1
            sj=sqomg2(j2)
            sjj=sj*sj
         Do k2=1,j2-1
            sk=sqomg2(k2)
            skk=sk*sk
            Write(10,'(e28.22,5i5)') dijk(nn)/si/sj/sk,k2,j2,i2
            Write(10,'(e28.22,5i5)') diijk(nn)/sii/sj/sk,k2,j2,i2,i2
            Write(10,'(e28.22,5i5)') dijjk(nn)/si/sjj/sk,k2,j2,j2,i2
            Write(10,'(e28.22,5i5)') dijkk(nn)/si/sj/skk,k2,k2,j2,i2
            nn=nn+1
         End do
         End do
         End do

         nn=1
         Do i2=1,Nf
            si=sqomg2(i2)
         Do j2=1,i2-1
            sj=sqomg2(j2)
         Do k2=1,j2-1
            sk=sqomg2(k2)
         Do l2=1,k2-1
            sl=sqomg2(l2)
            Write(10,'(e28.22,5i5)') dijkl(nn)/si/sj/sk/sl,l2,k2,j2,i2
            nn=nn+1
         End do
         End do
         End do
         End do

      else ! CFF

         Do i2=1,Nf
            si=sqomg2(i2)
            sii=si*si
            Write(10,'(e28.22,5i5)') di(modes(i2))/si,i2
            Write(10,'(e28.22,5i5)') dii(modes(i2))/sii,i2,i2
            Write(10,'(e28.22,5i5)') diii(modes(i2))/sii/si,i2,i2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,i2,i2,i2,i2
         End do

         nn=1
         Do i2=1,Nf
            si=sqomg2(i2)
            sii=si*si
         Do j2=1,i2-1
            sj=sqomg2(j2)
            sjj=sj*sj
            Write(10,'(e28.22,5i5)') dij(nn)/si/sj,j2,i2
            Write(10,'(e28.22,5i5)') diij(nn)/sii/sj,j2,i2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,j2,i2,i2,i2
            Write(10,'(e28.22,5i5)') dijj(nn)/si/sjj,j2,j2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,j2,j2,i2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,j2,j2,j2,i2
            nn=nn+1
         End do
         End do

         nn=1
         Do i2=1,Nf
            si=sqomg2(i2)
            sii=si*si
         Do j2=1,i2-1
            sj=sqomg2(j2)
            sjj=sj*sj
         Do k2=1,j2-1
            sk=sqomg2(k2)
            skk=sk*sk
            Write(10,'(e28.22,5i5)') dijk(nn)/si/sj/sk,k2,j2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,k2,j2,i2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,k2,j2,j2,i2
            Write(10,'(e28.22,5i5)') 0.D0+00,k2,k2,j2,i2             
            nn=nn+1
         End do
         End do
         End do

      end if

      Close(10)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Ocvscf_coeff_writeSindo()

   USE PES_qff_mod
   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: ierr

   Integer :: i,j,k,l,m,n
   Integer :: dcoeff1,dcoeff2,dcoeff3

      if(icff==0) then ! QFF
         dcoeff1=4; dcoeff2=6; dcoeff3=4 
      else             ! CFF
         dcoeff1=3; dcoeff2=3; dcoeff3=1 
      end if

      Call Mem_alloc(-1,ierr,'D',nQ1*dcoeff1)
      Allocate(coeff1(dcoeff1,nQ1))
      coeff1=0.D+00
      Do i=1,nQ1
         coeff1(1,i)=di(i)
         coeff1(2,i)=dii(i)
         coeff1(3,i)=diii(i)
      End do

      if(nQ2/=0) then
         Call Mem_alloc(-1,ierr,'D',nQ2*dcoeff2)
         allocate(coeff2(dcoeff2,nQ2))
         coeff2=0.D+00
         Do n=1,nQ2
            i=mQ2(1,n)
            j=mQ2(2,n)
            k=(i-1)*(i-2)/2 + j
            coeff2(1,n)=dij(k)
            coeff2(2,n)=diij(k)
            coeff2(3,n)=dijj(k)
         End do
      endif
      
      if(nQ3/=0) then
         Call Mem_alloc(-1,ierr,'D',nQ3*dcoeff3)
         allocate(coeff3(dcoeff3,nQ3))
         coeff3=0.D+00
         Do n=1,nQ3
            i=mQ3(1,n)
            j=mQ3(2,n)
            k=mQ3(3,n)
            l=(i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k
            coeff3(1,n)=dijk(l)
         End do
      endif

      if(icff==0) then ! QFF
         Do i=1,nQ1
            coeff1(4,i)=diiii(i)
         End do

         if(nQ2/=0) then
            Do n=1,nQ2
               i=mQ2(1,n)
               j=mQ2(2,n)
               k=(i-1)*(i-2)/2 + j
               coeff2(4,n)=diijj(k)
               coeff2(5,n)=diiij(k)
               coeff2(6,n)=dijjj(k)
            End do
         endif

         if(nQ3/=0) then
            Do n=1,nQ3
               i=mQ3(1,n)
               j=mQ3(2,n)
               k=mQ3(3,n)
               l=(i-1)*(i-2)*(i-3)/6 + (j-1)*(j-2)/2 + k
               coeff3(2,n)=diijk(l)
               coeff3(3,n)=dijjk(l)
               coeff3(4,n)=dijkk(l)
            End do
         endif

         if(nQ4/=0) then
            Call Mem_alloc(-1,ierr,'D',nQ4)
            allocate(coeff4(1,nQ4))
            coeff4=0.D+00
            Do n=1,nQ4
               i=mQ4(1,n)
               j=mQ4(2,n)
               k=mQ4(3,n)
               l=mQ4(4,n)
               m=(i-1)*(i-2)*(i-3)*(i-4)/24 + (j-1)*(j-2)*(j-3)/6 &
                +(k-1)*(k-2)/2 + l
               coeff4(1,n)=dijkl(m)
            End do
         endif
      end if

 1000 Continue

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!   Subroutine Ocvscf_coeff_writeMaVi()
!
!   USE Ocvscf_coeff_mod
!   USE global, only : nMR,hrmfreq,freq,convert
!   USE modQFF, only : Gi,Hii,Ciii,Qiiii,Hij,Ciij,Qiijj,Qiiij,Cijk,Qiijk,Qijkl
!
!   Implicit None
!
!   Integer :: i,j,k,l,nn
!   Real(8) :: dd,ff(Nf)
!
!      Gi=0.D+00
!      Hii=0.D+00
!      Ciii=0.D+00
!      Qiiii=0.D+00
!      Do i=1,Nf
!         Gi(i)=di(i)
!         Hii(i)=dii(i)
!         Ciii(i)=diii(i)
!         Qiiii(i)=diiii(i)
!         ff(i)=sqrt(dii(i)*2.D+00)
!      End do
!
!      !write(6,'(f12.4)') ff*convert
!      !write(6,*)
!      !write(6,'(f12.4)') hrmfreq*convert
!      !write(6,*)
!      !write(6,'(f12.4)') freq*convert
!      hrmfreq=ff
!      freq=ff
!
!      !write(6,*) nMR
!      if(nMR==1) return 
!
!      Hij=0.D+00
!      Ciij=0.D+00
!      Qiijj=0.D+00
!      Qiiij=0.D+00
!      nn=1
!      Do i=2,Nf
!      Do j=1,i-1
!         Hij(i,j)=dij(nn)
!         Hij(j,i)=Hij(i,j)
!         Ciij(i,j)=diij(nn)
!         Ciij(j,i)=dijj(nn)
!         Qiijj(i,j)=diijj(nn)
!         Qiijj(j,i)=Qiijj(i,j)
!         Qiiij(i,j)=diiij(nn)
!         Qiiij(j,i)=dijjj(nn)
!         nn=nn+1
!      End do
!      End do
!
!      if(nMR==2) return 
!
!      Cijk=0.D+00
!      Qiijk=0.D+00
!      nn=1
!      Do i=3,Nf
!      Do j=2,i-1
!      Do k=1,j-1
!         Cijk(i,j,k)=dijk(nn)
!         Cijk(i,k,j)=dijk(nn)
!         Cijk(j,i,k)=dijk(nn)
!         Cijk(j,k,i)=dijk(nn)
!         Cijk(k,i,j)=dijk(nn)
!         Cijk(k,j,i)=dijk(nn)
!
!         Qiijk(i,j,k)=diijk(nn)
!         Qiijk(i,k,j)=diijk(nn)
!         Qiijk(j,i,k)=dijjk(nn)
!         Qiijk(j,k,i)=dijjk(nn)
!         Qiijk(k,i,j)=dijkk(nn)
!         Qiijk(k,j,i)=dijkk(nn)
!         nn=nn+1
!      End do
!      End do
!      End do
!
!      if(nMR==3) return 
!      
!      Qijkl=0.D+00
!      nn=1
!      Do i=4,Nf
!      Do j=3,i-1
!      Do k=2,j-1
!      Do l=1,k-1
!         Qijkl(i,j,k,l)=dijkl(nn)
!         Qijkl(i,j,l,k)=dijkl(nn)
!         Qijkl(i,k,j,l)=dijkl(nn)
!         Qijkl(i,k,l,j)=dijkl(nn)
!         Qijkl(i,l,j,k)=dijkl(nn)
!         Qijkl(i,l,k,j)=dijkl(nn)
!         Qijkl(j,i,k,l)=dijkl(nn)
!         Qijkl(j,i,l,k)=dijkl(nn)
!         Qijkl(j,k,i,l)=dijkl(nn)
!         Qijkl(j,k,l,i)=dijkl(nn)
!         Qijkl(j,l,i,k)=dijkl(nn)
!         Qijkl(j,l,k,i)=dijkl(nn)
!         Qijkl(k,i,j,l)=dijkl(nn)
!         Qijkl(k,i,l,j)=dijkl(nn)
!         Qijkl(k,j,i,l)=dijkl(nn)
!         Qijkl(k,j,l,i)=dijkl(nn)
!         Qijkl(k,l,i,j)=dijkl(nn)
!         Qijkl(k,l,j,i)=dijkl(nn)
!         Qijkl(l,i,j,k)=dijkl(nn)
!         Qijkl(l,i,k,j)=dijkl(nn)
!         Qijkl(l,j,i,k)=dijkl(nn)
!         Qijkl(l,j,k,i)=dijkl(nn)
!         Qijkl(l,k,i,j)=dijkl(nn)
!         Qijkl(l,k,j,i)=dijkl(nn)
!         nn=nn+1
!      End do
!      End do
!      End do
!      End do
!
!End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_copyD2C()

   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: i,j,k,l,nc

      Do i=1,Nf
         cs(i)=di(i)
         cst(i,i)=dii(i)*2.D+00
         cstu(i,i,i)=diii(i)*6.D+00
      End do

      nc=1
      Do i=1,Nf
      Do j=1,i-1
         cst(i,j)=dij(nc)
         cst(j,i)=dij(nc)
         Call cubic(diij(nc)*2.D+00,i,i,j)
         Call cubic(dijj(nc)*2.D+00,i,j,j)
         nc=nc+1

      End do
      End do

      nc=1
      Do i=1,Nf
      Do j=1,i-1
      Do k=1,j-1
         Call cubic(dijk(nc),i,j,k)
         nc=nc+1
            
      End do
      End do
      End do


      if(icff==0) then ! QFF

         Do i=1,Nf
            cstuv(i,i,i,i)=diiii(i)*24.D+00
         End do

         nc=1
         Do i=1,Nf
         Do j=1,i-1
            Call quartic(diijj(nc)*4.D+00,i,i,j,j)
            Call quartic(diiij(nc)*6.D+00,i,i,i,j)
            Call quartic(dijjj(nc)*6.D+00,i,j,j,j)
            nc=nc+1

         End do
         End do

         nc=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
            Call quartic(diijk(nc)*2.D+00,i,i,j,k)
            Call quartic(dijjk(nc)*2.D+00,i,j,j,k)
            Call quartic(dijkk(nc)*2.D+00,i,j,k,k)
            nc=nc+1
            
         End do
         End do
         End do

         nc=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
         Do l=1,k-1
            Call quartic(dijkl(nc),i,j,k,l)
            nc=nc+1

         End do
         End do
         End do
         End do

      end if

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_copyC2D()

   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: i,j,k,l,nc

      Do i=1,Nf
         di(i)=cs(i)
         dii(i)=cst(i,i)*0.5D+00
         diii(i)=cstu(i,i,i)/6.D+00
      End do
         
      nc=1
      Do i=1,Nf
      Do j=1,i-1
         dij(nc)=cst(i,j)
         diij(nc)=cstu(i,i,j)*0.5D+00
         dijj(nc)=cstu(i,j,j)*0.5D+00
         nc=nc+1

      End do
      End do

      nc=1
      Do i=1,Nf
      Do j=1,i-1
      Do k=1,j-1
         dijk(nc)=cstu(i,j,k)
         nc=nc+1
            
      End do
      End do
      End do


      if(icff==0) then ! QFF

         Do i=1,Nf
            diiii(i)=cstuv(i,i,i,i)/24.D+00
         End do
         
         nc=1
         Do i=1,Nf
         Do j=1,i-1
            diijj(nc)=cstuv(i,i,j,j)*0.25D+00
            diiij(nc)=cstuv(i,i,i,j)/6.D+00
            dijjj(nc)=cstuv(i,j,j,j)/6.D+00
            nc=nc+1

         End do
         End do

         nc=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
            diijk(nc)=cstuv(i,i,j,k)*0.5D+00
            dijjk(nc)=cstuv(i,j,j,k)*0.5D+00
            dijkk(nc)=cstuv(i,j,k,k)*0.5D+00
            nc=nc+1
            
         End do
         End do
         End do

         nc=1
         Do i=1,Nf
         Do j=1,i-1
         Do k=1,j-1
         Do l=1,k-1
            dijkl(nc)=cstuv(i,j,k,l)
            nc=nc+1
               
         End do
         End do
         End do
         End do
      
      end if

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!   pp : pair of modes (pp(1) < pp(2))
!   ap : rotation angle / rad
!
   Subroutine Ocvscf_coeff_rotate(pp,ap)

   USE Ocvscf_coeff_mod

   Implicit None

   Integer, parameter :: nD1=2,nD2=nD1*nD1,nD3=nD2*nD1,nD4=nD3*nD1
   Integer :: pp(nD1),qq(Nf-nD1)
   Real(8) :: ap

   Integer :: i1,i2,i3,i4,m2,m3,m4,mm2,n2,n3,n4,nn2,nn3,nnn2
   Real(8) :: vec2(nD2),vec3(nD3),vec4(nD4)
   Real(8), allocatable :: ccs(:),ccst(:,:),ccstu(:,:,:),ccstuv(:,:,:,:),ccs2(:)
   Real(8) :: ddot

      Nff1=2
      Nff2=nD2
      Nff3=nD3
      Nff4=nD4

      Allocate(Umat(nD1,nD1))
      Umat(1,1)=cos(ap)
      Umat(2,2)=Umat(1,1)
      Umat(2,1)=sin(ap)
      Umat(1,2)=-Umat(2,1)
      !write(6,'(2f8.2)') ap/PI*180.0D+00
      !write(6,'(2i6)') pp
      !write(6,'(2f12.6)') (Umat(i1,:),i1=1,2)

      Do i1=1,pp(1)-1                                                                     
         qq(i1)=i1                                                                        
      End do
      Do i1=pp(1)+1,pp(2)-1                                                               
         qq(i1-1)=i1                                                                      
      End do
      Do i1=pp(2)+1,Nf                                                                    
         qq(i1-2)=i1                                                                      
      End do
      !write(6,'(i3)') qq

      mm2=(pp(2)-1)*(pp(2)-2)/2 + pp(1)
      !write(6,'(i3)') mm2

      ! 1st order
      Allocate(ccs(nD1))
      Do i1=1,nD1
         ccs(i1)=cs(pp(i1))
      End do

      Do i1=1,nD1
         di(pp(i1))=ddot(nD1,Umat(:,i1),1,ccs,1)
      End do
      Deallocate(ccs)

      Do i1=1,Nf-nD1
         di(qq(i1))=cs(qq(i1))
      End do

      ! 2nd order
      Allocate(ccst(nD1,nD1))
      Do i1=1,nD1
      Do i2=1,nD1
         ccst(i2,i1)=cst(pp(i2),pp(i1))
      End do
      End do

      Do i1=1,nD1
         Call getVec2(i1,i1,vec2)
         dii(pp(i1))=ddot(nD2,vec2,1,ccst,1)*0.5D+00
      End do
      Call getVec2(1,2,vec2)
      dij(mm2)=ddot(nD2,vec2,1,ccst,1)

      Deallocate(ccst)

      Do i1=1,Nf-nD1
         Allocate(ccs(nD1))
         Do i2=1,nD1
            ccs(i2)=cst(pp(i2),qq(i1))
         End do
         Do i2=1,nD1
            if(qq(i1)>pp(i2)) then
               !OK write(6,*) 'passed2-1'
               m2=(qq(i1)-1)*(qq(i1)-2)/2 + pp(i2)
            else
               !OK write(6,*) 'passed2-2'
               m2=(pp(i2)-1)*(pp(i2)-2)/2 + qq(i1)
            endif

            dij(m2)=ddot(nD1,Umat(:,i2),1,ccs,1)
         End do
         Deallocate(ccs)

      End do

      Do i1=1,Nf-nD1
         dii(qq(i1))=cst(qq(i1),qq(i1))*0.5D+00
         n2=(qq(i1)-1)*(qq(i1)-2)/2
         Do i2=1,i1-1
            m2=n2 + qq(i2)
            dij(m2)=cst(qq(i2),qq(i1))
         End do
      End do

      ! 3rd order
      Allocate(ccstu(nD1,nD1,nD1))
      Do i1=1,nD1
      Do i2=1,nD1
      Do i3=1,nD1
         ccstu(i3,i2,i1)=cstu(pp(i3),pp(i2),pp(i1))
      End do
      End do
      End do

      Do i1=1,nD1
         Call getVec3(i1,i1,i1,vec3)
         diii(pp(i1))=ddot(nD3,vec3,1,ccstu,1)/6.D+00
      End do
      Call getVec2(1,2,vec2)
      Call getVec3b(2,vec2,vec3)
      diij(mm2)=ddot(nD3,vec3,1,ccstu,1)/2.D+00
      Call getVec3b(1,vec2,vec3)
      dijj(mm2)=ddot(nD3,vec3,1,ccstu,1)/2.D+00

      Deallocate(ccstu)

      Do i1=1,Nf-nD1
         Allocate(ccs(nD1))
         Do i2=1,nD1
            ccs(i2)=cstu(pp(i2),qq(i1),qq(i1))
         End do

         Allocate(ccst(nD1,nD1))
         Do i2=1,nD1
         Do i3=1,nD1
            ccst(i3,i2)=cstu(pp(i3),pp(i2),qq(i1))
         End do
         End do

         Do i2=1,nD1
            Call getVec2(i2,i2,vec2)
            if(qq(i1)>pp(i2)) then
               !OK write(6,*) 'passed3-1'
               m2=(qq(i1)-1)*(qq(i1)-2)/2 + pp(i2)
               diij(m2)=ddot(nD1,Umat(:,i2),1,ccs,1)/2.D+00
               dijj(m2)=ddot(nD2,vec2,1,ccst,1)/2.D+00
            else
               !OK write(6,*) 'passed3-2'
               m2=(pp(i2)-1)*(pp(i2)-2)/2 + qq(i1)
               diij(m2)=ddot(nD2,vec2,1,ccst,1)/2.D+00
               dijj(m2)=ddot(nD1,Umat(:,i2),1,ccs,1)/2.D+00
            endif

         End do

         Do i2=1,nD1
         Do i3=1,i2-1
            Call getVec2(i2,i3,vec2)
            if(qq(i1)>pp(i2)) then
               !OK write(6,*) 'passed3-3'
               m3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 + (pp(i2)-1)*(pp(i2)-2)/2 &
                 + pp(i3)
            else if(qq(i1)>pp(i3)) then
               !OK write(6,*) 'passed3-4'
               m3=(pp(i2)-1)*(pp(i2)-2)*(pp(i2)-3)/6 + (qq(i1)-1)*(qq(i1)-2)/2 &
                 + pp(i3)
            else
               !OK write(6,*) 'passed3-5'
               m3=(pp(i2)-1)*(pp(i2)-2)*(pp(i2)-3)/6 + (pp(i3)-1)*(pp(i3)-2)/2 &
                 + qq(i1)
            endif
            dijk(m3)=ddot(nD2,vec2,1,ccst,1)
         End do
         End do
         Deallocate(ccst)
         Deallocate(ccs)

      End do

      Do i1=1,Nf-nD1
      Do i2=1,i1-1
         Allocate(ccs(nD1))
         Do i3=1,nD1
            ccs(i3)=cstu(pp(i3),qq(i2),qq(i1))
         End do

         Do i3=1,nD1
            if(pp(i3)>qq(i1)) then
               !OK write(6,*) 'passed3-6'
               m3=(pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)/6 + (qq(i1)-1)*(qq(i1)-2)/2 &
                 + qq(i2)
            else if(pp(i3)>qq(i2)) then
               !OK write(6,*) 'passed3-7'
               m3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 + (pp(i3)-1)*(pp(i3)-2)/2 &
                 + qq(i2)
            else
               !OK write(6,*) 'passed3-8'
               m3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 + (qq(i2)-1)*(qq(i2)-2)/2 &
                 + pp(i3)
            endif
            dijk(m3)=ddot(nD1,Umat(:,i3),1,ccs,1)
         End do
         Deallocate(ccs)

      End do
      End do

      Do i1=1,Nf-nD1
         n2=(qq(i1)-1)*(qq(i1)-2)/2 
         n3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6
         diii(qq(i1))=cstu(qq(i1),qq(i1),qq(i1))/6.0D+00
         Do i2=1,i1-1
            m2=n2 + qq(i2)
            nn2=(qq(i2)-1)*(qq(i2)-2)/2
            diij(m2)=cstu(qq(i1),qq(i1),qq(i2))*0.5D+00
            dijj(m2)=cstu(qq(i1),qq(i2),qq(i2))*0.5D+00
            Do i3=1,i2-1
               m3=n3 + nn2 + qq(i3)
               dijk(m3)=cstu(qq(i3),qq(i2),qq(i1))
            End do
         End do
      End do

      ! 4th order
      if(icff==0) then
         Allocate(ccstuv(nD1,nD1,nD1,nD1))
         Do i1=1,nD1
         Do i2=1,nD1
         Do i3=1,nD1
         Do i4=1,nD1
            ccstuv(i4,i3,i2,i1)=cstuv(pp(i4),pp(i3),pp(i2),pp(i1))
         End do
         End do
         End do
         End do

         Do i1=1,nD1
            Call getVec4(i1,i1,i1,i1,vec4)
            diiii(pp(i1))=ddot(nD4,vec4,1,ccstuv,1)/24.D+00
         End do

         m2=(pp(i1)-1)*(pp(i1)-2)/2 + pp(i2)
         Call getVec2(1,2,vec2)
         Call getVec4b(1,1,vec2,vec4)
         dijjj(mm2)=ddot(nD4,vec4,1,ccstuv,1)/6.D+00
         Call getVec4b(1,2,vec2,vec4)
         diijj(mm2)=ddot(nD4,vec4,1,ccstuv,1)/4.D+00
         Call getVec4b(2,2,vec2,vec4)
         diiij(mm2)=ddot(nD4,vec4,1,ccstuv,1)/6.D+00
         Deallocate(ccstuv)

         Do i1=1,Nf-nD1
            Allocate(ccs(nD1))
            Do i2=1,nD1
               ccs(i2)=cstuv(pp(i2),qq(i1),qq(i1),qq(i1))
            End do

            Allocate(ccst(nD1,nD1))
            Do i2=1,nD1
            Do i3=1,nD1
               ccst(i3,i2)=cstuv(pp(i3),pp(i2),qq(i1),qq(i1))
            End do
            End do

            Allocate(ccstu(nD1,nD1,nD1))
            Do i2=1,nD1
            Do i3=1,nD1
            Do i4=1,nD1
               ccstu(i4,i3,i2)=cstuv(pp(i4),pp(i3),pp(i2),qq(i1))
            End do
            End do
            End do

            Do i2=1,nD1
               Call getVec2(i2,i2,vec2)
               Call getVec3b(i2,vec2,vec3)
               if(qq(i1)>pp(i2)) then
                  !OK write(6,*) 'passed4-1'
                  m2=(qq(i1)-1)*(qq(i1)-2)/2 + pp(i2)
                  diiij(m2)=ddot(nD1,Umat(:,i2),1,ccs,1)/6.D+00
                  dijjj(m2)=ddot(nD3,vec3,1,ccstu,1)/6.D+00
                  diijj(m2)=ddot(nD2,vec2,1,ccst,1)/4.D+00
               else
                  !OK write(6,*) 'passed4-2'
                  m2=(pp(i2)-1)*(pp(i2)-2)/2 + qq(i1)
                  diiij(m2)=ddot(nD3,vec3,1,ccstu,1)/6.D+00
                  dijjj(m2)=ddot(nD1,Umat(:,i2),1,ccs,1)/6.D+00
                  diijj(m2)=ddot(nD2,vec2,1,ccst,1)/4.D+00
               endif
            End do

            Do i2=1,nD1
            Do i3=1,i2-1
               Call getVec2(i2,i3,vec2)
               if(qq(i1)>pp(i2)) then
                  !OK write(6,*) 'passed4-3'
                  m3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 + (pp(i2)-1)*(pp(i2)-2)/2 &
                    + pp(i3)
                  diijk(m3)=ddot(nD2,vec2,1,ccst,1)/2.D+00
                  Call getVec3b(i2,vec2,vec3)
                  dijjk(m3)=ddot(nD3,vec3,1,ccstu,1)/2.D+00
                  Call getVec3b(i3,vec2,vec3)
                  dijkk(m3)=ddot(nD3,vec3,1,ccstu,1)/2.D+00

               elseif(qq(i1)>pp(i3)) then
                  !OK write(6,*) 'passed4-4'
                  m3=(pp(i2)-1)*(pp(i2)-2)*(pp(i2)-3)/6 + (qq(i1)-1)*(qq(i1)-2)/2 &
                    + pp(i3)
                  Call getVec3b(i2,vec2,vec3)
                  diijk(m3)=ddot(nD3,vec3,1,ccstu,1)/2.D+00
                  dijjk(m3)=ddot(nD2,vec2,1,ccst,1)/2.D+00
                  Call getVec3b(i3,vec2,vec3)
                  dijkk(m3)=ddot(nD3,vec3,1,ccstu,1)/2.D+00

               else
                  !OK write(6,*) 'passed4-5'
                  m3=(pp(i2)-1)*(pp(i2)-2)*(pp(i2)-3)/6 + (pp(i3)-1)*(pp(i3)-2)/2 &
                    + qq(i1)
                  Call getVec3b(i2,vec2,vec3)
                  diijk(m3)=ddot(nD3,vec3,1,ccstu,1)/2.D+00
                  Call getVec3b(i3,vec2,vec3)
                  dijjk(m3)=ddot(nD3,vec3,1,ccstu,1)/2.D+00
                  dijkk(m3)=ddot(nD2,vec2,1,ccst,1)/2.D+00

               endif
            End do
            End do

            Deallocate(ccs,ccst,ccstu)
         End do

         Do i1=1,Nf-nD1
         Do i2=1,i1-1
            Allocate(ccs(nD1),ccs2(nD1))
            Do i3=1,nD1
               ccs(i3)=cstuv(pp(i3),qq(i2),qq(i1),qq(i1))
            End do
            Do i3=1,nD1
               ccs2(i3)=cstuv(pp(i3),qq(i2),qq(i2),qq(i1))
            End do
            
            Allocate(ccst(nD1,nD1))
            Do i3=1,nD1
            Do i4=1,nD1
               ccst(i4,i3)=cstuv(pp(i4),pp(i3),qq(i2),qq(i1))
            End do
            End do

            Do i3=1,nD1
               Call getVec2(i3,i3,vec2)
               if(pp(i3)>qq(i1)) then
                  !OK write(6,*) 'passed4-10'
                  m3=(pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)/6 + (qq(i1)-1)*(qq(i1)-2)/2 &
                    + qq(i2)
                  diijk(m3)=ddot(nD2,vec2,1,ccst,1)/2.D+00
                  dijjk(m3)=ddot(nD1,Umat(:,i3),1,ccs,1)/2.D+00
                  dijkk(m3)=ddot(nD1,Umat(:,i3),1,ccs2,1)/2.D+00
               else if(pp(i3)>qq(i2)) then
                  !OK write(6,*) 'passed4-11'
                  m3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 + (pp(i3)-1)*(pp(i3)-2)/2 &
                    + qq(i2)
                  diijk(m3)=ddot(nD1,Umat(:,i3),1,ccs,1)/2.D+00
                  dijjk(m3)=ddot(nD2,vec2,1,ccst,1)/2.D+00
                  dijkk(m3)=ddot(nD1,Umat(:,i3),1,ccs2,1)/2.D+00
               else
                  !OK write(6,*) 'passed4-12'
                  m3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 + (qq(i2)-1)*(qq(i2)-2)/2 &
                    + pp(i3)
                  diijk(m3)=ddot(nD1,Umat(:,i3),1,ccs,1)/2.D+00
                  dijjk(m3)=ddot(nD1,Umat(:,i3),1,ccs2,1)/2.D+00
                  dijkk(m3)=ddot(nD2,vec2,1,ccst,1)/2.D+00
               endif
            End do

            Do i3=1,nD1
            Do i4=1,i3-1
               Call getVec2(i3,i4,vec2)
               if(qq(i2)>pp(i3)) then
                  !OK write(6,*) 'passed4-13'
                  m4= (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24 &
                    + (qq(i2)-1)*(qq(i2)-2)*(qq(i2)-3)/6 &
                    + (pp(i3)-1)*(pp(i3)-2)/2 &
                    + pp(i4)
               else if(qq(i1)>pp(i3) .and. pp(i3)>qq(i2) .and. qq(i2)>pp(i4)) then
                  !OK write(6,*) 'passed4-14'
                  m4= (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24 &
                    + (pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)/6 &
                    + (qq(i2)-1)*(qq(i2)-2)/2 &
                    + pp(i4)
               else if(qq(i1)>pp(i3) .and. pp(i4)>qq(i2)) then
                  !OK write(6,*) 'passed4-15'
                  m4= (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24 &
                    + (pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)/6 &
                    + (pp(i4)-1)*(pp(i4)-2)/2 &
                    + qq(i2)
               else if(pp(i3)>qq(i1) .and. qq(i2)>pp(i4)) then
                  !OK write(6,*) 'passed4-16'
                  m4= (pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)*(pp(i3)-4)/24 &
                    + (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 &
                    + (qq(i2)-1)*(qq(i2)-2)/2 &
                    + pp(i4)
               else if(pp(i3)>qq(i1) .and. pp(i4)>qq(i2) .and. qq(i1)>pp(i4)) then
                  !OK write(6,*) 'passed4-17'
                  m4= (pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)*(pp(i3)-4)/24 &
                    + (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 &
                    + (pp(i4)-1)*(pp(i4)-2)/2 &
                    + qq(i2)
               else if(pp(i4)>qq(i1)) then
                  !OK write(6,*) 'passed4-18'
                  m4= (pp(i3)-1)*(pp(i3)-2)*(pp(i3)-3)*(pp(i3)-4)/24 &
                    + (pp(i4)-1)*(pp(i4)-2)*(pp(i4)-3)/6 &
                    + (qq(i1)-1)*(qq(i1)-2)/2 &
                    + qq(i2)
               endif
               dijkl(m4)=ddot(nD2,vec2,1,ccst,1)

            End do
            End do

            Deallocate(ccs,ccs2,ccst)
         End do
         End do
 
         Do i1=1,Nf-nD1
         Do i2=1,i1-1
         Do i3=1,i2-1
            Allocate(ccs(nD1))
            Do i4=1,nD1
               ccs(i4)=cstuv(pp(i4),qq(i3),qq(i2),qq(i1))
            End do

            Do i4=1,nD1
               if(pp(i4)>qq(i1)) then
                  !OK write(6,*) 'passed4-19'
                  m4= (pp(i4)-1)*(pp(i4)-2)*(pp(i4)-3)*(pp(i4)-4)/24 &
                    + (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6 &
                    + (qq(i2)-1)*(qq(i2)-2)/2 &
                    + qq(i3)
               elseif(pp(i4)>qq(i2)) then
                  !OK write(6,*) 'passed4-20'
                  m4= (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24 &
                    + (pp(i4)-1)*(pp(i4)-2)*(pp(i4)-3)/6 &
                    + (qq(i2)-1)*(qq(i2)-2)/2 &
                    + qq(i3)
               elseif(pp(i4)>qq(i3)) then
                  !OK write(6,*) 'passed4-21'
                  m4= (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24 &
                    + (qq(i2)-1)*(qq(i2)-2)*(qq(i2)-3)/6 &
                    + (pp(i4)-1)*(pp(i4)-2)/2 &
                    + qq(i3)
               else
                  !OK write(6,*) 'passed4-22'
                  m4= (qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24 &
                    + (qq(i2)-1)*(qq(i2)-2)*(qq(i2)-3)/6 &
                    + (qq(i3)-1)*(qq(i3)-2)/2 &
                    + pp(i4)
               endif
               dijkl(m4)=ddot(nD1,Umat(:,i4),1,ccs,1)
            End do
            Deallocate(ccs)
         End do
         End do
         End do

         Do i1=1,Nf-nD1
            diiii(qq(i1))=cstuv(qq(i1),qq(i1),qq(i1),qq(i1))/24.D+00
            n2=(qq(i1)-1)*(qq(i1)-2)/2 
            n3=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)/6
            n4=(qq(i1)-1)*(qq(i1)-2)*(qq(i1)-3)*(qq(i1)-4)/24
            Do i2=1,i1-1
               m2=n2 + qq(i2)
               nn2=(qq(i2)-1)*(qq(i2)-2)/2
               nn3=(qq(i2)-1)*(qq(i2)-2)*(qq(i2)-3)/6
               diiij(m2)=cstuv(qq(i1),qq(i1),qq(i1),qq(i2))/6.D+00
               diijj(m2)=cstuv(qq(i1),qq(i1),qq(i2),qq(i2))/4.D+00
               dijjj(m2)=cstuv(qq(i1),qq(i2),qq(i2),qq(i2))/6.D+00
               Do i3=1,i2-1
                  m3=n3 + nn2 + qq(i3)
                  nnn2=(qq(i3)-1)*(qq(i3)-2)/2
                  diijk(m3)=cstuv(qq(i1),qq(i1),qq(i2),qq(i3))*0.5D+00
                  dijjk(m3)=cstuv(qq(i1),qq(i2),qq(i2),qq(i3))*0.5D+00
                  dijkk(m3)=cstuv(qq(i1),qq(i2),qq(i3),qq(i3))*0.5D+00
                  Do i4=1,i3-1
                     m4=n4 + nn3 + nnn2 + qq(i4)
                     dijkl(m4)=cstuv(qq(i4),qq(i3),qq(i2),qq(i1))
                  End do
               End do
            End do
         End do
      end if

      Deallocate(Umat)

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_transC2D(U1)

   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: i1,i2,i3,i4,m2,m3,m4
   Real(8) :: U1(Nf,Nf)
   Real(8), allocatable :: vec2(:),vec3(:),vec4(:)
   Real(8) :: ddot

      Nff1=Nf
      Nff2=Nf*Nf
      Nff3=Nff2*Nf
      Nff4=Nff3*Nf

      Allocate(Umat(Nf,Nf))
      Umat=U1

      ! Now transform

      Do i1=1,Nf
         di(i1)=ddot(Nf,Umat(:,i1),1,cs,1)
      End do

      Allocate(vec2(Nff2),vec3(Nff3),vec4(Nff4))
      m2=1
      Do i1=1,Nf

         Call getVec2(i1,i1,vec2)
         dii(i1)=ddot(Nff2,vec2,1,cst,1)*0.5D+00

         Do i2=1,i1-1
            Call getVec2(i1,i2,vec2)
            dij(m2)=ddot(Nff2,vec2,1,cst,1)

            m2=m2+1
         End do
      End do

      m2=1
      m3=1
      Do i1=1,Nf
         Call getVec3(i1,i1,i1,vec3)
         diii(i1)=ddot(Nff3,vec3,1,cstu,1)/6.D+00

         Do i2=1,i1-1
            Call getVec2(i1,i2,vec2)
            !Call getVec3(i1,i1,i2,vec3)
            Call getVec3b(i1,vec2,vec3)
            diij(m2)=ddot(Nff3,vec3,1,cstu,1)/2.D+00
            !Call getVec3(i1,i2,i2,vec3)
            Call getVec3b(i2,vec2,vec3)
            dijj(m2)=ddot(Nff3,vec3,1,cstu,1)/2.D+00
            m2=m2+1

            Do i3=1,i2-1
               !Call getVec3(i1,i2,i3,vec3)
               Call getVec3b(i3,vec2,vec3)
               dijk(m3)=ddot(Nff3,vec3,1,cstu,1)
               m3=m3+1

            End do
         End do
      End do

      if(icff==0) then
         m2=1
         m3=1
         m4=1
         Do i1=1,Nf
            Call getVec4(i1,i1,i1,i1,vec4)
            diiii(i1)=ddot(Nff4,vec4,1,cstuv,1)/24.D+00

            Do i2=1,i1-1
               Call getVec2(i1,i2,vec2)

               !Call getVec4(i1,i1,i1,i2,vec4)
               Call getVec4b(i1,i1,vec2,vec4)
               diiij(m2)=ddot(Nff4,vec4,1,cstuv,1)/6.D+00
               !Call getVec4(i1,i1,i2,i2,vec4)
               Call getVec4b(i1,i2,vec2,vec4)
               diijj(m2)=ddot(Nff4,vec4,1,cstuv,1)/4.D+00
               !Call getVec4(i1,i2,i2,i2,vec4)
               Call getVec4b(i2,i2,vec2,vec4)
               dijjj(m2)=ddot(Nff4,vec4,1,cstuv,1)/6.D+00
               m2=m2+1

               Do i3=1,i2-1
                  Call getVec3b(i3,vec2,vec3)

                  !Call getVec4(i1,i1,i2,i3,vec4)
                  Call getVec4c(i1,vec3,vec4)
                  diijk(m3)=ddot(Nff4,vec4,1,cstuv,1)/2.D+00
                  !Call getVec4(i1,i2,i2,i3,vec4)
                  Call getVec4c(i2,vec3,vec4)
                  dijjk(m3)=ddot(Nff4,vec4,1,cstuv,1)/2.D+00
                  !Call getVec4(i1,i2,i3,i3,vec4)
                  Call getVec4c(i3,vec3,vec4)
                  dijkk(m3)=ddot(Nff4,vec4,1,cstuv,1)/2.D+00
                  m3=m3+1

                  Do i4=1,i3-1
                     !Call getVec4(i1,i2,i3,i4,vec4)
                     Call getVec4c(i4,vec3,vec4)
                     dijkl(m4)=ddot(Nff4,vec4,1,cstuv,1)
                     m4=m4+1

                  End do
               End do
            End do
         End do
      end if

      Deallocate(Umat,vec2,vec3,vec4)

      return


   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
   Subroutine Ocvscf_coeff_getOmega(omega)

   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: i
   Real(8) :: omega(Nf)

      Do i=1,Nf
         omega(i)=sqrt(dii(i)*2.D+00)
      End do

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!  
   Subroutine Ocvscf_coeff_eta12(i,j,eta12out)

   USE Ocvscf_coeff_mod

   Implicit None

   Integer :: i,j,k
   Real(8) :: eta12out

   k = (i-1)*(i-2)/2 + j
   eta12out = sqrt( ((dii(i)-dii(j))**2 + dij(k)**2)/(dii(i)+dii(j)) )
   
   End Subroutine Ocvscf_coeff_eta12
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
