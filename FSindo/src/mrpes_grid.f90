!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2009/02/24
!   Copyright 2009 
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Module grid_mod

   USE mrpes_mod

   Integer :: L1,L2,L3
   Integer, allocatable :: nn1(:),nn2(:,:),nn3(:,:)
   Integer, allocatable :: xg_idx1(:),xg_idx2(:),xg_idx3(:), &
                           vg_idx1(:),vg_idx2(:),vg_idx3(:)
   Real(8), allocatable :: xg1(:),xg2(:),xg3(:), &
                           vg1(:),vg2(:),vg3(:), &
                           vp1(:),vp2(:),vp3(:)
   Integer, parameter :: tlen=80
   Character(len=tlen), allocatable :: t1(:),t2(:),t3(:)

End module

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_construct()

USE Constants_mod
USE grid_mod

Implicit None

   Integer :: ierr 
   Integer :: i,j,k,ij,ijk,ll,nx,ny,nz,nv,mi,mj,mk,ifl
   Real(8) :: V
   Character :: fp*80
   Real(8) :: const

      fp=PotDir(:Len_PotDir)
      const = SQRT(elmass)/B2A

      L1=nMR_type(type_grd,1)
      if(L1>0) then

         Call Mem_alloc(-1,ierr,'I',L1)
         Call Mem_alloc(-1,ierr,'I',(L1+1)*2)
         Call Mem_alloc(-1,ierr,'C',tlen*L1)
         Allocate(nn1(L1),xg_idx1(L1+1),vg_idx1(L1+1),t1(L1))
  
         xg_idx1(1)=0
         vg_idx1(1)=0
         Do ll=1,L1
            mi=type_mode1(type_mode_idx1(type_grd)+ll)
            Call get_fname1(mi,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') t1(ll)
            Read(ifl,*)
            Read(ifl,*) nn1(ll)
            Close(ifl)
            xg_idx1(ll+1)=xg_idx1(ll)+nn1(ll)
            vg_idx1(ll+1)=vg_idx1(ll)+nn1(ll)
         End do
  
         nx=xg_idx1(L1+1)
         Call Mem_alloc(-1,ierr,'D',nx*3)
         Allocate(xg1(nx),vg1(nx),vp1(nx))
         Do ll=1,L1
            mi=type_mode1(type_mode_idx1(type_grd)+ll)
            Call get_fname1(mi,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,*)
            Read(ifl,*)
            Read(ifl,*)
            Read(ifl,*)
            Do i=1,nn1(ll)
               Read(ifl,*) xg1(xg_idx1(ll)+i),vg1(xg_idx1(ll)+i)
            End do
            Close(ifl)
            if(.not. au) then
               Do i=1,nn1(ll)
                  xg1(xg_idx1(ll)+i) = xg1(xg_idx1(ll)+i)*const
               End do
            endif
            Call lag1_Const(nn1(ll),xg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                                    vg1(vg_idx1(ll)+1:vg_idx1(ll+1)), &
                                    vp1(vg_idx1(ll)+1:vg_idx1(ll+1)))
         End do

      endif

      !dbg Do ll=1,L1
      !dbg    mi=type_mode1(type_mode_idx1(type_grd)+ll)
      !dbg    write(6,'(i3)') mi
      !dbg    Do i=1,nn1(ll)
      !dbg       write(6,'(2f12.6)') xg1(xg_idx1(ll)+i),vg1(xg_idx1(ll)+i)
      !dbg    End do
      !dbg End do

      if(MR==1) goto 1000

      L2=nMR_type(type_grd,2)
      if(L2>0) then

         Call Mem_alloc(-1,ierr,'I',L2*2)
         Call Mem_alloc(-1,ierr,'I',(L2+1)*2)
         Call Mem_alloc(-1,ierr,'C',tlen*L2)
         Allocate(nn2(2,L2),xg_idx2(L2+1),vg_idx2(L2+1),t2(L2))

         xg_idx2(1)=0
         vg_idx2(1)=0
         Do ll=1,L2
            mi=type_mode2(1,type_mode_idx2(type_grd)+ll)
            mj=type_mode2(2,type_mode_idx2(type_grd)+ll)
            Call get_fname2(mi,mj,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') t2(ll)
            Read(ifl,*)
            Read(ifl,*) nn2(:,ll)
            Close(ifl)
            xg_idx2(ll+1)=xg_idx2(ll)+nn2(1,ll)+nn2(2,ll)
            vg_idx2(ll+1)=vg_idx2(ll)+nn2(1,ll)*nn2(2,ll)
         End do

         nx=xg_idx2(L2+1)
         nv=vg_idx2(L2+1)
         Call Mem_alloc(-1,ierr,'D',nx+nv*2)
         Allocate(xg2(nx),vg2(nv),vp2(nv))
         Do ll=1,L2
            mi=type_mode2(1,type_mode_idx2(type_grd)+ll)
            mj=type_mode2(2,type_mode_idx2(type_grd)+ll)
            Call get_fname2(mi,mj,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,*)
            Read(ifl,*)
            Read(ifl,*)
            Read(ifl,*)

            nx=xg_idx2(ll)
            ny=nx+nn2(1,ll)
            ij=1
            Do i=1,nn2(2,ll)
            Do j=1,nn2(1,ll)
               Read(ifl,*) xg2(nx+j),xg2(ny+i),vg2(vg_idx2(ll)+ij)
               ij=ij+1
            End do
            End do

            Close(ifl)

            if(.not. au) then
               Do i=1,nn2(2,ll)
                  xg2(ny+i)=xg2(ny+i)*const
               End do
               Do j=1,nn2(1,ll)
                  xg2(nx+j)=xg2(nx+j)*const
               End do
            endif

            Call lag2_Const(nn2(1,ll),nn2(2,ll), &
                            xg2(nx+1:nx+nn2(1,ll)), &
                            xg2(ny+1:ny+nn2(2,ll)), &
                            vg2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                            vp2(vg_idx2(ll)+1:vg_idx2(ll+1)))
         End do

      endif

      !dbg Do ll=1,L2
      !dbg    mi=type_mode2(1,type_mode_idx2(type_grd)+ll)
      !dbg    mj=type_mode2(2,type_mode_idx2(type_grd)+ll)
      !dbg    write(6,'(4i3)') mi,mj,nn2(:,ll)
      !dbg
      !dbg    nx=xg_idx2(ll)
      !dbg    ny=nx+nn2(1,ll)
      !dbg    nv=vg_idx2(ll)
      !dbg
      !dbg    ij=1
      !dbg    Do i=1,nn2(2,ll)
      !dbg    Do j=1,nn2(1,ll)
      !dbg       write(6,'(2f12.6,f15.9)') xg2(nx+j),xg2(ny+i),vg2(nv+ij)
      !dbg       ij=ij+1
      !dbg    End do
      !dbg    End do
      !dbg End do

      if(MR==2) goto 1000

      L3=nMR_type(type_grd,3)
      if(L3>0) then

         Call Mem_alloc(-1,ierr,'I',L3*3)
         Call Mem_alloc(-1,ierr,'I',(L3+1)*2)
         Call Mem_alloc(-1,ierr,'C',tlen*L3)
         Allocate(nn3(3,L3),xg_idx3(L3+1),vg_idx3(L3+1),t3(L3))

         xg_idx3(1)=0
         vg_idx3(1)=0
         Do ll=1,L3
            mi=type_mode3(1,type_mode_idx3(type_grd)+ll)
            mj=type_mode3(2,type_mode_idx3(type_grd)+ll)
            mk=type_mode3(3,type_mode_idx3(type_grd)+ll)
            Call get_fname3(mi,mj,mk,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,'(a)') t3(ll)
            Read(ifl,*)
            Read(ifl,*) nn3(:,ll)
            Close(ifl)
            xg_idx3(ll+1)=xg_idx3(ll)+nn3(1,ll)+nn3(2,ll)+nn3(3,ll)
            vg_idx3(ll+1)=vg_idx3(ll)+nn3(1,ll)*nn3(2,ll)*nn3(3,ll)
         End do

         nx=xg_idx3(L3+1)
         nv=vg_idx3(L3+1)
         Call Mem_alloc(-1,ierr,'D',nx+nv*2)
         Allocate(xg3(nx),vg3(nv),vp3(nv))
         Do ll=1,L3
            mi=type_mode3(1,type_mode_idx3(type_grd)+ll)
            mj=type_mode3(2,type_mode_idx3(type_grd)+ll)
            mk=type_mode3(3,type_mode_idx3(type_grd)+ll)
            Call get_fname3(mi,mj,mk,fp(Len_PotDir+1:))
            Call file_indicator(12,ifl)
            Open(unit=ifl,file=fp,status='OLD')
            Read(ifl,*)
            Read(ifl,*)
            Read(ifl,*)
            Read(ifl,*)

            nx=xg_idx3(ll)
            ny=nx+nn3(1,ll)
            nz=ny+nn3(2,ll)
            ijk=1
            Do i=1,nn3(3,ll)
            Do j=1,nn3(2,ll)
            Do k=1,nn3(1,ll)
               Read(ifl,*) xg3(nx+k),xg3(ny+j),xg3(nz+i),vg3(vg_idx3(ll)+ijk)
               ijk=ijk+1
            End do
            End do
            End do

            Close(ifl)

            if(.not. au) then 
               Do i=1,nn3(3,ll)
                  xg3(nz+i)=xg3(nz+i)*const
               End do
               Do j=1,nn3(2,ll)
                  xg3(ny+j)=xg3(ny+j)*const
               End do
               Do k=1,nn3(1,ll)
                  xg3(nx+k)=xg3(nx+k)*const
               End do
            endif

            Call lag3_Const(nn3(1,ll),nn3(2,ll),nn3(3,ll), &
                            xg3(nx+1:nx+nn3(1,ll)), &
                            xg3(ny+1:ny+nn3(2,ll)), &
                            xg3(nz+1:nz+nn3(3,ll)), &
                            vg3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                            vp3(vg_idx3(ll)+1:vg_idx3(ll+1)))

         End do

      endif

      !dbg Do ll=1,L3
      !dbg    mi=type_mode3(1,type_mode_idx3(type_grd)+ll)
      !dbg    mj=type_mode3(2,type_mode_idx3(type_grd)+ll)
      !dbg    mk=type_mode3(3,type_mode_idx3(type_grd)+ll)
      !dbg    write(6,'(3i3,2x,3i3)') mi,mj,mk,nn3(:,ll)
      !dbg  
      !dbg    nx=xg_idx3(ll)
      !dbg    ny=nx+nn3(1,ll)
      !dbg   nz=ny+nn3(2,ll)
      !dbg   nv=vg_idx3(ll)
      !dbg 
      !dbg   ijk=1
      !dbg   Do i=1,nn3(3,ll)
      !dbg   Do j=1,nn3(2,ll)
      !dbg   Do k=1,nn3(1,ll)
      !dbg      write(6,'(3f12.6,f15.9)') xg3(nx+k),xg3(ny+j),xg3(nz+i),vg3(nv+ijk)
      !dbg      ijk=ijk+1
      !dbg   End do
      !dbg   End do
      !dbg   End do
      !dbg End do
      
      if(MR==3) goto 1000
      
 1000 Continue

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_destruct()

USE grid_mod

Implicit None

      if(L1>0) then
         Call Mem_dealloc('I',size(nn1))
         Call Mem_dealloc('I',size(xg_idx1))
         Call Mem_dealloc('I',size(vg_idx1))
         Call Mem_dealloc('D',size(xg1))
         Call Mem_dealloc('D',size(vg1))
         Call Mem_dealloc('D',size(vp1))
         Call Mem_dealloc('C',size(t1)*tlen)
         Deallocate(nn1,xg_idx1,vg_idx1,xg1,vg1,vp1,t1)
      endif
      if(L2>0) then
         Call Mem_dealloc('I',size(nn2))
         Call Mem_dealloc('I',size(xg_idx2))
         Call Mem_dealloc('I',size(vg_idx2))
         Call Mem_dealloc('D',size(xg2))
         Call Mem_dealloc('D',size(vg2))
         Call Mem_dealloc('D',size(vp2))
         Call Mem_dealloc('C',size(t2)*tlen)
         Deallocate(nn2,xg_idx2,vg_idx2,xg2,vg2,vp2,t2)
      endif
      if(L3>0) then
         Call Mem_dealloc('I',size(nn3))
         Call Mem_dealloc('I',size(xg_idx3))
         Call Mem_dealloc('I',size(vg_idx3))
         Call Mem_dealloc('D',size(xg3))
         Call Mem_dealloc('D',size(vg3))
         Call Mem_dealloc('D',size(vp3))
         Call Mem_dealloc('C',size(t3)*tlen)
         Deallocate(nn3,xg_idx3,vg_idx3,xg3,vg3,vp3,t3)
      endif

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getV1(ll,qq,V,mi)

USE grid_mod

Implicit None

   Integer :: ll,mi
   Real(8) :: qq(Nfree),V

      mi=type_mode1(type_mode_idx1(type_grd)+ll)
      Call lag1_getV(nn1(ll), &
                     xg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     vg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     vp1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     qq(mi),V)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getVG1(ll,qq,V,G,mi)

USE grid_mod

Implicit None

   Integer :: ll,mi
   Real(8) :: qq(Nfree),V,G

      mi=type_mode1(type_mode_idx1(type_grd)+ll)
      Call lag1_getVG(nn1(ll), &
                     xg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     vg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     vp1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     qq(mi),V,G)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getVGH1(ll,qq,V,G,H,mi)

USE grid_mod

Implicit None

   Integer :: ll,mi
   Real(8) :: qq(Nfree),V,G,H

      mi=type_mode1(type_mode_idx1(type_grd)+ll)
      Call lag1_getVGH(nn1(ll), &
                     xg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     vg1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     vp1(xg_idx1(ll)+1:xg_idx1(ll+1)), &
                     qq(mi),V,G,H)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getV2(ll,qq,V,mm)

USE grid_mod

Implicit None

   Integer :: ll,mm(2)
   Real(8) :: qq(Nfree),V

   Integer :: mi,mj,nx,ny

      mi=type_mode2(1,type_mode_idx2(type_grd)+ll)
      mj=type_mode2(2,type_mode_idx2(type_grd)+ll)
      mm(1)=mj
      mm(2)=mi
      nx=xg_idx2(ll)
      ny=nx+nn2(1,ll)

      Call lag2_getV(nn2(1,ll), nn2(2,ll), &
                     xg2(nx+1:nx+nn2(1,ll)), &
                     xg2(ny+1:ny+nn2(2,ll)), &
                     vg2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                     vp2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                     qq(mj),qq(mi),V)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getVG2(ll,qq,V,G,mm)

USE grid_mod

Implicit None

   Integer :: ll,mm(2)
   Real(8) :: qq(Nfree),V,G(2)

   Integer :: mi,mj,nx,ny

      mi=type_mode2(1,type_mode_idx2(type_grd)+ll)
      mj=type_mode2(2,type_mode_idx2(type_grd)+ll)
      mm(1)=mj
      mm(2)=mi
      nx=xg_idx2(ll)
      ny=nx+nn2(1,ll)

      Call lag2_getVG(nn2(1,ll), nn2(2,ll), &
                     xg2(nx+1:nx+nn2(1,ll)), &
                     xg2(ny+1:ny+nn2(2,ll)), &
                     vg2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                     vp2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                     qq(mj),qq(mi),V,G)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getVGH2(ll,qq,V,G,H,mm)

USE grid_mod

Implicit None

   Integer :: ll,mm(2)
   Real(8) :: qq(Nfree),V,G(2),H(2,2)

   Integer :: mi,mj,nx,ny

      mi=type_mode2(1,type_mode_idx2(type_grd)+ll)
      mj=type_mode2(2,type_mode_idx2(type_grd)+ll)
      mm(1)=mj
      mm(2)=mi
      nx=xg_idx2(ll)
      ny=nx+nn2(1,ll)

      Call lag2_getVGH(nn2(1,ll), nn2(2,ll), &
                     xg2(nx+1:nx+nn2(1,ll)), &
                     xg2(ny+1:ny+nn2(2,ll)), &
                     vg2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                     vp2(vg_idx2(ll)+1:vg_idx2(ll+1)), &
                     qq(mj),qq(mi),V,G,H)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getV3(ll,qq,V,mm)

USE grid_mod

Implicit None

   Integer :: ll,mm(3)
   Real(8) :: qq(Nfree),V

   Integer :: mi,mj,mk,nx,ny,nz

      mi=type_mode3(1,type_mode_idx3(type_grd)+ll)
      mj=type_mode3(2,type_mode_idx3(type_grd)+ll)
      mk=type_mode3(3,type_mode_idx3(type_grd)+ll)
      mm(1)=mk
      mm(2)=mj
      mm(3)=mi
      nx=xg_idx3(ll)
      ny=nx+nn3(1,ll)
      nz=ny+nn3(2,ll)

      Call lag3_getV(nn3(1,ll), nn3(2,ll),nn3(3,ll), &
                     xg3(nx+1:nx+nn3(1,ll)), &
                     xg3(ny+1:ny+nn3(2,ll)), &
                     xg3(nz+1:nz+nn3(3,ll)), &
                     vg3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                     vp3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                     qq(mk),qq(mj),qq(mi),V)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getVG3(ll,qq,V,G,mm)

USE grid_mod

Implicit None

   Integer :: ll,mm(3)
   Real(8) :: qq(Nfree),V,G(3)

   Integer :: mi,mj,mk,nx,ny,nz

      mi=type_mode3(1,type_mode_idx3(type_grd)+ll)
      mj=type_mode3(2,type_mode_idx3(type_grd)+ll)
      mk=type_mode3(3,type_mode_idx3(type_grd)+ll)
      mm(1)=mk
      mm(2)=mj
      mm(3)=mi
      nx=xg_idx3(ll)
      ny=nx+nn3(1,ll)
      nz=ny+nn3(2,ll)

      Call lag3_getVG(nn3(1,ll), nn3(2,ll),nn3(3,ll), &
                     xg3(nx+1:nx+nn3(1,ll)), &
                     xg3(ny+1:ny+nn3(2,ll)), &
                     xg3(nz+1:nz+nn3(3,ll)), &
                     vg3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                     vp3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                     qq(mk),qq(mj),qq(mi),V,G)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getVGH3(ll,qq,V,G,H,mm)

USE grid_mod

Implicit None

   Integer :: ll,mm(3)
   Real(8) :: qq(Nfree),V,G(3),H(3,3)

   Integer :: mi,mj,mk,nx,ny,nz

      mi=type_mode3(1,type_mode_idx3(type_grd)+ll)
      mj=type_mode3(2,type_mode_idx3(type_grd)+ll)
      mk=type_mode3(3,type_mode_idx3(type_grd)+ll)
      mm(1)=mk
      mm(2)=mj
      mm(3)=mi
      nx=xg_idx3(ll)
      ny=nx+nn3(1,ll)
      nz=ny+nn3(2,ll)

      Call lag3_getVGH(nn3(1,ll), nn3(2,ll),nn3(3,ll), &
                     xg3(nx+1:nx+nn3(1,ll)), &
                     xg3(ny+1:ny+nn3(2,ll)), &
                     xg3(nz+1:nz+nn3(3,ll)), &
                     vg3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                     vp3(vg_idx3(ll)+1:vg_idx3(ll+1)), &
                     qq(mk),qq(mj),qq(mi),V,G,H)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getnG1(ll,nG)

USE grid_mod

Implicit None

   Integer :: ll,nG

      nG=nn1(ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!      m1=type_mode2(2,type_mode_idx2(type_grd)+ll)
!      m2=type_mode2(1,type_mode_idx2(type_grd)+ll)

Subroutine grid_getnG2(ll,nG1,nG2)

USE grid_mod

Implicit None

   Integer :: ll,nG1,nG2

      nG2=nn2(1,ll)
      nG1=nn2(2,ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!      m1=type_mode3(3,type_mode_idx3(type_grd)+ll)
!      m2=type_mode3(2,type_mode_idx3(type_grd)+ll)
!      m3=type_mode3(1,type_mode_idx3(type_grd)+ll)

Subroutine grid_getnG3(ll,nG1,nG2,nG3)

USE grid_mod

Implicit None

   Integer :: ll,nG1,nG2,nG3

      nG3=nn3(1,ll)
      nG2=nn3(2,ll)
      nG1=nn3(3,ll)

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

Subroutine grid_getGridpoints1(ll,qq1)

USE grid_mod

Implicit None

   Integer :: ll,i
   Real(8) :: qq1(*)

      Do i=1,nn1(ll)
         qq1(i)=xg1(xg_idx1(ll)+i)
      End do

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!      m1=type_mode2(2,type_mode_idx2(type_grd)+ll)
!      m2=type_mode2(1,type_mode_idx2(type_grd)+ll)

Subroutine grid_getGridpoints2(ll,qq1,qq2)

USE grid_mod

Implicit None

   Integer :: ll,i,ny
   Real(8) :: qq1(*),qq2(*)

      Do i=1,nn2(1,ll)
         qq2(i)=xg2(xg_idx2(ll)+i)
      End do
      ny=xg_idx2(ll)+nn2(1,ll)
      Do i=1,nn2(2,ll)
         qq1(i)=xg2(ny+i)
      End do

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!      m1=type_mode3(3,type_mode_idx3(type_grd)+ll)
!      m2=type_mode3(2,type_mode_idx3(type_grd)+ll)
!      m3=type_mode3(1,type_mode_idx3(type_grd)+ll)

Subroutine grid_getGridpoints3(ll,qq1,qq2,qq3)

USE grid_mod

Implicit None

   Integer :: ll,i,ny,nz
   Real(8) :: qq1(*),qq2(*),qq3(*)

      Do i=1,nn3(1,ll)
         qq3(i)=xg3(xg_idx3(ll)+i)
      End do
      ny=xg_idx3(ll)+nn3(1,ll)
      Do i=1,nn3(2,ll)
         qq2(i)=xg3(ny+i)
      End do
      nz=ny+nn3(2,ll)
      Do i=1,nn3(3,ll)
         qq1(i)=xg3(nz+i)
      End do

End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
