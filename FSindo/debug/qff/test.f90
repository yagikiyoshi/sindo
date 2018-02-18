!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
 Program test

    Implicit None

    Integer :: Nf,spr_GetNfree

    Integer :: i,j,k,l,nn,n1
    Integer :: mrpes_getnMRtype,mm(4)
    Real(8) :: aa,mc_getInt2,mc_getInt3,mc_getInt4
    Real(8) :: Vpot,Vp,Vm,V1,V2,V3,V4,gi,hi,dq,H2wvn
    Real(8), allocatable :: qq(:),grad(:),hess(:,:)

       i=0
       Call spr_Const(i)
       Nf=spr_GetNfree()

       Call mrpes_Const(4)
       Call mrpes_printSettings()
       Do i=1,Nf
       Do j=1,i-1
          aa = mc_getInt2(i,j)
          if(aa > 1.0e+01) write(6,'(2i4,f12.6)') i,j,aa
       End do
       End do
       Do i=1,Nf
       Do j=1,i-1
       Do k=1,j-1
          aa = mc_getInt3(i,j,k)
          if(aa > 1.0e+01) write(6,'(3i4,f12.6)') i,j,k,aa
       End do
       End do
       End do
       Do i=1,Nf
       Do j=1,i-1
       Do k=1,j-1
       Do l=1,k-1
          aa = mc_getInt4(i,j,k,l)
          if(aa > 1.0e+01) write(6,'(4i4,f12.6)') i,j,k,l,aa
       End do
       End do
       End do
       End do

       nn = mrpes_getnMRtype(1,4)
       Do n1=1,nn
          Call mrpes_getMode4(1,n1,mm)
          write(6,'(i3,3x,4i3)') n1,mm
       End do

       Do i=4,Nf
       Do j=3,i-1
       Do k=2,j-1
       Do l=1,k-1
          Call mrpes_getidx4(i,j,k,l,n1,nn)
          if(n1 /= 0) write(6,'(4i3,2i4)') i,j,k,l,n1,nn
       End do
       End do
       End do
       End do

       write(6,*)
       Allocate(qq(Nf),grad(Nf),hess(Nf,Nf))

       qq=0.D+00
       qq(2)=40.D+00
       qq(3)=35.D+00
       qq(6)=15.D+00
       qq(9)=22.D+00
       qq(10)=10.D+00
       qq(11)=22.D+00
       qq(12)=20.D+00

       Call mrpes_getVGH(qq,Vpot,grad,hess)
       !Call mrpes_getVG(qq,Vpot,grad)
       write(6,'(f12.6)') Vpot
       Call qff_PES(qq,Vpot)
       write(6,'(f12.6)') Vpot

       dq=0.001D+00
       write(6,*) 'gi'
       Do i=1,Nf
          qq(i)=qq(i)+dq
          Call mrpes_getV(qq,Vp)
          qq(i)=qq(i)-dq*2.D+00
          Call mrpes_getV(qq,Vm)

          gi=(Vp-Vm)/dq*0.5D+00
          write(6,'(2e12.4,e15.6)') gi,grad(i),gi-grad(i)

          qq(i)=qq(i)+dq
       End do

       write(6,*) 'hij'
       Do i=1,Nf
          qq(i)=qq(i)+dq
          Call mrpes_getV(qq,Vp)
          qq(i)=qq(i)-dq*2.D+00
          Call mrpes_getV(qq,Vm)

          hi=(Vp+Vm-2.D+00*Vpot)/dq/dq
          write(6,'(2e12.4,e15.6)') hi,hess(i,i),hi-hess(i,i)

          qq(i)=qq(i)+dq
       End do
       write(6,*)

       write(6,*) 'hij'
       Do i=1,Nf
       Do j=1,i-1
          qq(i)=qq(i)+dq
          qq(j)=qq(j)+dq
          Call mrpes_getV(qq,V1)
          qq(j)=qq(j)-dq*2.D+00
          Call mrpes_getV(qq,V2)
          qq(i)=qq(i)-dq*2.D+00
          qq(j)=qq(j)+dq*2.D+00
          Call mrpes_getV(qq,V3)
          qq(j)=qq(j)-dq*2.D+00
          Call mrpes_getV(qq,V4)

          hi=(V1-V2-V3+V4)/dq/dq*0.25D+00
          write(6,'(2e12.4,e15.6)') hi,hess(j,i),hi-hess(j,i)

          qq(i)=qq(i)+dq
          qq(j)=qq(j)+dq
       End do
       End do
       write(6,*)

       Deallocate(qq,grad,hess)

       Call mrpes_Dest()
       Call spr_Dest()

 End
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

