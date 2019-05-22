!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Version

   USE Constants_mod

   Integer :: IsHost,Hostnm
   Character(len=24) Date
   Character(len=32) HostName

      Write(Iout,*) '-------------------------------------------------------'
      Write(Iout,*) '                                                     '
      Write(Iout,*) '   ***   WELCOME TO SINDO PROGRAM                    '
      Write(Iout,*) '    ***                (  VERSION 4.0 BETA  )        '
      Write(Iout,*) '     ***                                             '
      Write(Iout,*) '      ***       COPYRIGHT 2019:                      '
      Write(Iout,*) '       ***         KIYOSHI YAGI  KIYOSHI.YAGI@RIKEN.JP'
      Write(Iout,*) '                                                     '
      Write(Iout,*) '-------------------------------------------------------'

      Ishost=Hostnm(HostName)
      Call Fdate(Date)

      Write(Iout,100) Date
      Write(Iout,200) HostName
      Call timer(0,Iout)

      return

  100 Format(/,5x,'JOB STARTED AT: ',a24)
  200 Format(5x,'RUNNING ON ',a32,//)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
   Subroutine diag(n, m, H, C, E)

   Implicit None

   Integer :: n,m,m1,io
   Real(8), dimension(n*(n+1)/2) :: H
   Real(8), dimension(n,m) :: C
   Real(8), dimension(n) :: E

   Integer :: i,j,k

   Character :: jobz,range,uplo
   Real(8)   :: vl,vu,abstol
   Integer   :: il,iu,ldz,info
   Integer, dimension(:), allocatable :: ifail,iwork
   Real(8), dimension(:), allocatable :: work


      !write(6,*) n,m
      !Do i=1,n
      !   write(6,'(11f12.6)') (H(j),j=i*(i-1)/2+1,i*(i+1)/2)
      !End do

      Call Mem_alloc(-1,i,'D',10*n)
      Call Mem_alloc(-1,i,'I',n+10*n)
      Allocate(work(10*n),ifail(n),iwork(10*n))

      jobz='V'
      uplo='U' 
      vl=0.D+00
      vu=0.D+00
      il=0
      iu=0
      if(n==m) then
         range='A'
      else
         range='I'; il=1; iu=m
      endif

      abstol=0.D+00
      ldz=n

      m1=0
      ifail=0; info=0
      Call dspevx(jobz,range,uplo,n,H,vl,vu,il,iu,abstol,m1,E, &
                  C,ldz,work,iwork,ifail,info)

      !write(6,*) info,ifail
      Call Mem_dealloc('D',size(work))
      Call Mem_dealloc('I',size(ifail))
      Call Mem_dealloc('I',size(iwork))
      Deallocate(work,ifail,iwork)

      !write(6,'(11f12.6)') E

      if(info==0) return

      !Call spr_Getio(in,io)
      io=6
      if(info<0) then 
         write(io,'(''ERROR IN '',i3,''TH PARAMETER'')') info
      else
         write(io,'(3x,i3,''EIGENVECTORS FAILED TO CONVERGE'')') info
      endif

   End Subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
      Subroutine diag2(n, m, H, C, E)

      Implicit None

         Integer :: n,m
         Real(8), dimension(n,n) :: H
         Real(8), dimension(n,m) :: C
         Real(8), dimension(n) :: E

         Integer :: i,j,k,spr_memalloc
         Real(8), dimension(n*(n+1)/2) :: H0

            H0=0.D+00; k=1
            Do i=1,n
               Do j=1,i
                  H0(k)=H(j,i)
                  k=k+1
               End do
            End do

            !write(6,'(11f12.6)') H
            !write(6,*)
            !Do i=1,n
            !   write(6,'(11f12.6)') (H0(j),j=i*(i-1)/2+1,i*(i+1)/2)
            !End do
            Call diag(n,m,H0,C,E)

            return

      End Subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!
!     Huckeler ver. 0.2
      Subroutine Huckeler(ncol,mcol,a,d,v)
      implicit double precision(a-h,o-z)
!      parameter (mcol=3)
      dimension a(mcol,mcol),d(mcol),v(mcol,mcol), &
                tv(mcol)
!     Initialization
      nrot=0
      do i=1,ncol
         d(i)=0.
         do j=1,ncol
            v(i,j)=0.
         enddo
      enddo
!     Calling jacobi
      call jacobi(a,ncol,mcol,d,v,nrot)
!     Printing out results
      do i=1,ncol
         if (v(1,i).lt.0.) then
            do j=1,ncol
               v(j,i)=-v(j,i)
            enddo
         endif
      enddo
      do k=1,ncol-1
         do i=k+1,ncol
            if (d(i).gt.d(k)) then
               td=d(k)
               d(k)=d(i)
               d(i)=td
               do j=1,ncol
                  tv(j)=v(j,k)
                  v(j,k)=v(j,i)
                  v(j,i)=tv(j)
               enddo
            endif
         enddo
      enddo
!      do i=1,ncol
!         write(6,99) d(i)
!         write(6,98) (v(j,i),j=1,ncol)
!         write(6,*)
!      enddo
!99   format(f8.4)
!98   format(10f8.4)
      end

      SUBROUTINE jacobi(adum,n,np,d,v,nrot)
      implicit double precision(a-h,o-z)
      PARAMETER (NMAX=500)
      dimension a(np,np),adum(np,np),d(np),v(np,np)
      dimension b(NMAX),z(NMAX)
!
!      do i=1,n
!         do j=1,n
!            write(6,*) 'a(',i,',',j,')=',a(i,j)
!         enddo
!      enddo
!
      do j=1,n
         do i=1,n
            a(i,j)=adum(i,j)
         end do
      end do
!
      do ip=1,n
         do iq=1,n
            v(ip,iq)=0.0d0
         end do
         v(ip,ip)=1.0d0
      end do
      do ip=1,n
         b(ip)=a(ip,ip)
         d(ip)=b(ip)
         z(ip)=0.0d0
      end do
      nrot=0
      do i=1,50
         sm=0.0d0
         do ip=1,n-1
            do iq=ip+1,n
               sm=sm+abs(a(ip,iq))
            end do
         end do
        if (sm.eq.0.0d0) return
        if (i.lt.4) then
           tresh=0.2d0*sm/n**2
        else
           tresh=0.0d0
        end if
        do ip=1,n-1
           do iq=ip+1,n
              g=100.0d0*abs(a(ip,iq))
              if ((i.gt.4).and.(abs(d(ip))+ &
                 g.eq.abs(d(ip))).and.(abs(d(iq))+g.eq.abs(d(iq)))) then
                 a(ip,iq)=0.0d0
              else if (abs(a(ip,iq)).gt.tresh) then
                 h=d(iq)-d(ip)
                 if (abs(h)+g.eq.abs(h)) then
                    t=a(ip,iq)/h
                 else
                    theta=0.5d0*h/a(ip,iq)
                    t=1./(abs(theta)+sqrt(1.+theta**2))
                    if (theta.lt.0.) t=-t
                 end if
                 c=1.0d0/sqrt(1+t**2)
                 s=t*c
                 tau=s/(1.0d0+c)
                 h=t*a(ip,iq)
                 z(ip)=z(ip)-h
                 z(iq)=z(iq)+h
                 d(ip)=d(ip)-h
                 d(iq)=d(iq)+h
                 a(ip,iq)=0.0d0
                 do j=1,ip-1
                    g=a(j,ip)
                    h=a(j,iq)
                    a(j,ip)=g-s*(h+g*tau)
                    a(j,iq)=h+s*(g-h*tau)
                 end do
                 do j=ip+1,iq-1
                    g=a(ip,j)
                    h=a(j,iq)
                    a(ip,j)=g-s*(h+g*tau)
                    a(j,iq)=h+s*(g-h*tau)
                 end do
                 do j=iq+1,n
                    g=a(ip,j)
                    h=a(iq,j)
                    a(ip,j)=g-s*(h+g*tau)
                    a(iq,j)=h+s*(g-h*tau)
                 end do
                 do j=1,n
                    g=v(j,ip)
                    h=v(j,iq)
                    v(j,ip)=g-s*(h+g*tau)
                    v(j,iq)=h+s*(g-h*tau)
                 end do
                 nrot=nrot+1
              end if
           end do
        end do
        do ip=1,n
           b(ip)=b(ip)+z(ip)
           d(ip)=b(ip)
           z(ip)=0.0d0
        end do
      end do
      Stop 'too many iterations in jacobi'
      return
      END

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine file_indicator(in,io)

      Implicit None

         Integer :: in,io
         Logical :: op

         io=in
         Do while(.true.)
            Inquire(io,opened=op)
            if(op) then 
              io=io+1
            else
              exit
            endif
         End do

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine sort(N,L,C)

      Implicit None

         Integer :: N,L(N)
         Real(8) :: C(N)

         Integer :: i,j(1),k,itmp
         Real(8) :: tmp

            Do i=1,N
               L(i)=i
            End do

            !dbg write(6,*)
            !dbg write(6,'(i3,f9.4)') (i,C(i),i=1,N)
            Do i=1,N
               j=MaxLoc(C(i:N))
               k=j(1)+i-1

               tmp=C(i)
               C(i)=C(k)
               C(k)=tmp

               itmp=L(i)
               L(i)=L(k)
               L(k)=itmp
            End do
            !dbg write(6,*)
            !dbg write(6,'(i3,f9.4)') (L(i),C(i),i=1,N)

      End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!

      Subroutine vec_product(a,b,c)

      Implicit None

         Real(8) :: a(3),b(3),c(3)

         c=0.D+00
         c(1)=a(2)*b(3)-a(3)*b(2)
         c(2)=a(3)*b(1)-a(1)*b(3)
         c(3)=a(1)*b(2)-a(2)*b(1)

      End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
  SUBROUTINE timer(ii,Iout)
!
  Implicit None

  Integer :: ii,Iout
  Integer :: day,hour,min
  Real :: delta,Elasped,etime,tarry(2),tarry_save(2)

  Save tarry_save
 
     if(ii==0) then
       Elasped=etime(tarry)
       tarry_save=tarry

     elseif(ii==1) then

       Elasped=etime(tarry)
       Write(Iout,1000) tarry(1)-tarry_save(1),tarry(2)-tarry_save(2)
       tarry_save=tarry

     elseif(ii==2) then

       Elasped=etime(tarry)
       Write(Iout,1001)

       day=tarry(1)/86400
       tarry(1)=tarry(1)-real(day*86400)
       hour=tarry(1)/3600
       tarry(1)=tarry(1)-real(hour*3600)
       min=tarry(1)/60
       tarry(1)=tarry(1)-real(min*60)
       Write(Iout,1002) day,hour,min,tarry(1)

       day=tarry(2)/86400
       tarry(2)=tarry(2)-real(day*86400)
       hour=tarry(2)/3600
       tarry(2)=tarry(2)-real(hour*3600)
       min=tarry(2)/60
       tarry(2)=tarry(2)-real(min*60)
       Write(Iout,1003) day,hour,min,tarry(2)

     End if
!
   1000 Format(12x,'(CLOCK) ---->  LAST STEP: USER ',F8.2, &
                     ', SYSTEM ',F8.2,' SECS   <----',/)
   1001 Format(3x,'(CLOCK) ---->  TOTAL CPU-TIME  <----')
   1002 Format(12x,'>     USER : ',i3,' DAYS ',i3, &
                  ' HOURS ',i3,' MINS ',f5.1,' SECS ')
   1003 Format(12x,'>    SYSTEM: ',i3,' DAYS ',i3, &
                  ' HOURS ',i3,' MINS ',f5.1,' SECS ',/)
!
  END SUBROUTINE
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Function mvCUP(c1,m1,v1,c2,m2,v2)

   Implicit None

   Integer :: c1,m1(c1),v1(c1)
   Integer :: c2,m2(c2),v2(c2)
   Integer :: mvCUP

   Integer :: i1,i2

      mvCUP=c1+c2
      Do i1=1,c1
         Do i2=1,c2
            if(m1(i1)==m2(i2)) then
               mvCUP=mvCUP-1
               if(v1(i1)==v2(i2)) mvCUP=mvCUP-1
            endif
         End do
      End do

   End Function

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!  m1(1)<m1(2)< ..
!  m2(1)<m2(2)< ..

   Subroutine mvPlus(c1,m1,v1,c2,m2,v2,c3,m3,v3)

   Implicit None

   Integer :: c1,m1(c1),v1(c1)
   Integer :: c2,m2(c2),v2(c2)
   Integer :: c3,m3(*),v3(*)

   Integer :: i1,i2,i3,j
   Logical :: l1,l2

      if(c1==0) then
         c3=c2
         m3(1:c2)=m2
         v3(1:c2)=v2
         return
      elseif(c2==0) then
         c3=c1
         m3(1:c1)=m1
         v3(1:c1)=v1
         return
      endif

      l1=.true. ; i1=1
      l2=.true. ; i2=1
      i3=0
      Do while(l1 .and. l2)
         if(m1(i1)<m2(i2)) then
            i3=i3+1
            m3(i3)=m1(i1)
            v3(i3)=v1(i1)
            i1=i1+1
         elseif(m2(i2)<m1(i1)) then
            i3=i3+1
            m3(i3)=m2(i2)
            v3(i3)=v2(i2)
            i2=i2+1
         else
            j=v1(i1)+v2(i2)
            if(j/=0) then
               i3=i3+1
               m3(i3)=m1(i1)
               v3(i3)=j
            endif
            i1=i1+1
            i2=i2+1
         endif
         if(i1>c1) l1=.false.
         if(i2>c2) l2=.false.
      End do

      if(l1) then
         Do while(i1<=c1)
            i3=i3+1
            m3(i3)=m1(i1)
            v3(i3)=v1(i1)
            i1=i1+1
         End do
      endif

      if(l2) then
         Do while(i2<=c2)
            i3=i3+1
            m3(i3)=m2(i2)
            v3(i3)=v2(i2)
            i2=i2+1
         End do
      endif

      c3=i3

   End subroutine

!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  m1(1)<m1(2)< ..
!  m2(1)<m2(2)< ..
!
   Subroutine mvMinus(c1,m1,v1,c2,m2,v2,c3,m3,v3)

   Implicit None

   Integer :: c1,m1(c1),v1(c1)
   Integer :: c2,m2(c2),v2(c2)
   Integer :: c3,m3(*),v3(*)

   Integer :: i1,i2,i3,j
   Logical :: l1,l2

      if(c1==0) then
         c3=c2
         m3(1:c2)=m2
         v3(1:c2)=v2
         return
      elseif(c2==0) then
         c3=c1
         m3(1:c1)=m1
         v3(1:c1)=v1
         return
      endif

      l1=.true. ; i1=1
      l2=.true. ; i2=1
      i3=0
      Do while(l1 .and. l2)
         if(m1(i1)<m2(i2)) then
            i3=i3+1
            m3(i3)=m1(i1)
            v3(i3)=v1(i1)
            i1=i1+1
         elseif(m2(i2)<m1(i1)) then
            i3=i3+1
            m3(i3)=m2(i2)
            v3(i3)=-v2(i2)
            i2=i2+1
         else
            j=v1(i1)-v2(i2)
            if(j/=0) then
               i3=i3+1
               m3(i3)=m1(i1)
               v3(i3)=j
            endif
            i1=i1+1
            i2=i2+1
         endif
         if(i1>c1) l1=.false.
         if(i2>c2) l2=.false.
      End do

      if(l1) then
         Do while(i1<=c1)
            i3=i3+1
            m3(i3)=m1(i1)
            v3(i3)=v1(i1)
            i1=i1+1
         End do
      elseif(l2) then
         Do while(i2<=c2)
            i3=i3+1
            m3(i3)=m2(i2)
            v3(i3)=-v2(i2)
            i2=i2+1
         End do
      endif

      c3=i3

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  m1(1)<m1(2)< ..
!  m2(1)<m2(2)< ..
!
   Subroutine mvMinus2(c1,m1,v1,c2,m2,v2,c3,m3,v3,vsum)

   Implicit None

   Integer :: c1,m1(c1),v1(c1)
   Integer :: c2,m2(c2),v2(c2)
   Integer :: c3,vsum,m3(*),v3(*)

   Integer :: i1,i2,i3,i,j
   Logical :: l1,l2

      vsum=0
      if(c1==0) then
         c3=c2
         m3(1:c2)=m2
         v3(1:c2)=v2
         Do i=1,c2
            vsum=vsum+v2(i)
         End do
         return
      elseif(c2==0) then
         c3=c1
         m3(1:c1)=m1
         v3(1:c1)=v1
         Do i=1,c1
            vsum=vsum+v1(i)
         End do
         return
      endif

      l1=.true. ; i1=1
      l2=.true. ; i2=1
      i3=0
      Do while(l1 .and. l2)
         if(m1(i1)<m2(i2)) then
            i3=i3+1
            m3(i3)=m1(i1)
            v3(i3)=v1(i1)
            vsum=vsum+v1(i1)
            i1=i1+1
         elseif(m2(i2)<m1(i1)) then
            i3=i3+1
            m3(i3)=m2(i2)
            v3(i3)=-v2(i2)
            vsum=vsum+v2(i2)
            i2=i2+1
         else
            j=v1(i1)-v2(i2)
            if(j/=0) then
               i3=i3+1
               m3(i3)=m1(i1)
               v3(i3)=j
               vsum=vsum+abs(j)
            endif
            i1=i1+1
            i2=i2+1
         endif
         if(i1>c1) l1=.false.
         if(i2>c2) l2=.false.
      End do

      if(l1) then
         Do while(i1<=c1)
            i3=i3+1
            m3(i3)=m1(i1)
            v3(i3)=v1(i1)
            vsum=vsum+v1(i1)
            i1=i1+1
         End do
      elseif(l2) then
         Do while(i2<=c2)
            i3=i3+1
            m3(i3)=m2(i2)
            v3(i3)=-v2(i2)
            vsum=vsum+v2(i2)
            i2=i2+1
         End do
      endif

      c3=i3

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!
!  m1(1)<m1(2)< ..
!  m2(1)<m2(2)< ..
!
   Subroutine mvMinus3(c1,m1,v1,c2,m2,v2,c3,vsum)

   Implicit None

   Integer :: c1,m1(c1),v1(c1)
   Integer :: c2,m2(c2),v2(c2)
   Integer :: c3,vsum

   Integer :: i1,i2,i3,i,j
   Logical :: l1,l2

      vsum=0
      if(c1==0) then
         c3=c2
         Do i=1,c2
            vsum=vsum+v2(i)
         End do
         return
      elseif(c2==0) then
         c3=c1
         Do i=1,c1
            vsum=vsum+v1(i)
         End do
         return
      endif

      l1=.true. ; i1=1
      l2=.true. ; i2=1
      i3=0
      Do while(l1 .and. l2)
         if(m1(i1)<m2(i2)) then
            i3=i3+1
            vsum=vsum+v1(i1)
            i1=i1+1
         elseif(m2(i2)<m1(i1)) then
            i3=i3+1
            vsum=vsum+v2(i2)
            i2=i2+1
         else
            j=abs(v1(i1)-v2(i2))
            if(j/=0) then
               i3=i3+1
               vsum=vsum+j
            endif
            i1=i1+1
            i2=i2+1
         endif
         if(i1>c1) l1=.false.
         if(i2>c2) l2=.false.
      End do

      if(l1) then
         Do while(i1<=c1)
            i3=i3+1
            vsum=vsum+v1(i1)
            i1=i1+1
         End do
      elseif(l2) then
         Do while(i2<=c2)
            i3=i3+1
            vsum=vsum+v2(i2)
            i2=i2+1
         End do
      endif

      c3=i3

   End subroutine
!
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
