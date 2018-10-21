!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/07/19
!   Copyright 2013 
!   Code description by K.Yagi and H.Otaki
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Ocvscf_run(Nfree)

   USE Constants_mod

   Implicit None

   Integer :: Nfree
   Integer :: maxOptIter,Istat
   Real(8) :: ethresh,gthresh,eta12thresh
   Integer :: pfit,icff,iscreen
   Character(80):: minfofile,mopfile,u1file,fname

   Integer :: i,idx
   Real(8) :: ene,omega(Nfree)

   Integer :: nCHO(Nfree)

   Namelist /ocvscf/maxOptIter,ethresh,gthresh,pfit,mopfile,u1file,icff,iscreen,eta12thresh
  
      write(Iout,'(/,''(  SETUP OCVSCF MODULE  )'',/)') 

      maxOptIter=30
      ethresh=1.D-06
      gthresh=1.D-06
      pfit=2
      u1file='u1.dat'
      icff=0
      iscreen=1
      eta12thresh=500.D+00
      rewind(5)
      read(5,ocvscf,end=100)

  100 continue

      write(Iout,'(3x,''o MOPFILE = '',a)') trim(mopfile)
      write(Iout,'(3x,''o ICFF    = '',i10)') icff
      write(Iout,'(3x,''o MAXITER = '',i10)') maxOptIter
      write(Iout,'(3x,''o PFIT    = '',i10)') pfit
      write(Iout,'(3x,''o ETHRESH = '',e10.3)') ethresh
      write(Iout,'(3x,''o GTHRESH = '',e10.3)') gthresh
      write(Iout,'(3x,''o ISCREEN = '',i10)') iscreen
      write(Iout,'(3x,''o ETA12THRESH = '',f8.2,/)') eta12thresh

      Call Ocvscf_coeff_Init(Nfree,icff)
      Call Ocvscf_coeff_readMop(mopfile)
      Call Ocvscf_coeff_copyD2C() 
      
      Call Vscf_setIcff(icff)
      
      Call OptCoord_Init(Nfree,maxOptiter,ethresh,gthresh,icff,iscreen,eta12thresh)
      Call Vib_getnCHO(nCHO)
      Do i=1,Nfree
         if(nCHO(i) <= 0) Call OptCoord_setInactiveMode(i)
      End do

      write(Iout,'(3x,''> VSCF RUN WITH THE INITIAL COORDINATES... '',/)') 
      Call OptCoord_getEnergy(ene) 
      if(ene>0) then 
         write(Iout,'(3x,''> SAFELY RETURNED TO OCVSCF MODULE '',/)') 
      else
         write(Iout,'(''>> VSCF UNCONVERGED WITH THE INITIAL COORDINATES! '')') 
         write(Iout,'(''>> TERMINATED WITH ERROR. '',/)') 
         Stop
      endif
      Call Vscf_setSilent()

      Call OptCoord_main(Istat,pfit)
      !Call optCoord_profile()

      write(Iout,'(3x,''o TRANSFORMATION MATRIX WRITTEN TO : [ '',a,'' ]'')') &
            trim(u1file)
      Call OptCoord_printU1(pfit,u1file)

      fname=trim(mopfile)//'_ocvscf'
      write(Iout,'(3x,''o FORCE CONSTANTS WRITTEN TO       : [ '',a,'' ]'')') &
            trim(fname)
      Call Ocvscf_coeff_writeMop(fname)

      Call Mol_getMinfofile(minfofile)
      if(len_trim(minfoFile)>0) then
         idx=index(minfofile,'.minfo')
         write(Iout,'(3x,''o NEW COORDINATES WRITTEN TO       : [ '',a,'' ]'',/)') &
               minfofile(1:idx-1)//'_ocvscf.minfo'
         Call Ocvscf_writeMinfo(pfit, minfofile)
      endif

      Call Ocvscf_coeff_getOmega(omega)
      write(Iout,'(3x,''o HARMONIC FREQUENCIES OF THE NEW MODES'')') 
      write(Iout,'(3x,''      -MODE-    -FREQ(CM-1)-'')')
      Do i=1,Nfree
         write(Iout,'(10x,i4,2x,f18.7)') i,omega(i)*H2wvn
      End do
      write(Iout,*)

      Call OptCoord_Finalz()
      Call Ocvscf_coeff_Finalz()

      Write(Iout,'(''(  FINALIZE OCVSCF MODULE  )'',/)') 

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Ocvscf_writeMinfo(order, minfofile)

   USE OptCoord_mod
   USE Constants_mod

   Implicit None

   Integer :: order
   Character(80) :: minfofile

   Integer :: nat,nat3, i,j,k, idx, nsize, ni, iminfo, iminfo_new
   Real(8), allocatable :: mass(:), sqmass(:), dd(:), hess(:,:)
   Real(8), allocatable :: freq(:), freq_new(:)
   Real(8), allocatable :: cv(:,:), cv_new(:,:)
   Real(8) :: scfthresh,Vscf_getEthresh
   Integer :: scfmaxiter,Vscf_getMaxIteration
   Character(80) :: minfofile_new
   Character(130) :: line

      Call Mol_getNat(nat)
      nat3 = nat*3
      allocate(mass(nat), freq(nfree), cv(nat3,nfree), hess(nat3,nat3))
      Call Mol_getMass(mass)
      Call Mol_getFreq(freq)
      Call Mol_getL(cv)

      iminfo=10
      open(iminfo,file=trim(minfofile),status='old')
      do while(.true.)
         read(iminfo,'(a)') line
         if(index(line,'Hessian') > 0) exit
      end do

      read(iminfo,'(a)') line
      read(line,*) nsize
      allocate(dd(nsize))
      read(iminfo,*) dd

      k=1
      do i=1,nat3
      do j=1,i
         hess(i,j) = dd(k)
         hess(j,i) = hess(i,j)
         k = k + 1
      end do
      end do
      deallocate(dd)

      allocate(freq_new(nfree), cv_new(nat3,nfree))
      do i=1,nfree
         cv_new(:,i) = 0.0D+00
         do j=1,nfree
         do k=1,nat3
            cv_new(k,i) = cv_new(k,i) + U1(j,i)*cv(k,j)
         end do
         end do
      end do

      allocate(sqmass(nat3))
      k=1
      do i=1,nat
      do j=1,3
         sqmass(k) = sqrt(mass(i))
         k=k+1
      end do
      end do

      do i=1,nat3
         do j=1,nat3
            hess(j,i) = hess(j,i)/sqmass(j)/sqmass(i)
         end do
      end do
      deallocate(sqmass)

      do i=1,nfree
         freq_new(i)=0.0D+00
         do j=1,nat3
         do k=1,nat3
            freq_new(i) = freq_new(i) + cv_new(j,i)*hess(j,k)*cv_new(k,i)
         end do
         end do
         freq_new(i) = sqrt(freq_new(i))*H2wvn
      end do

    ! now create new minfo file
      rewind(iminfo)

      idx=index(minfofile,'.minfo')
      minfofile_new = minfofile(1:idx-1)//'_ocvscf.minfo'
      iminfo_new=11
      open(iminfo_new,file=trim(minfofile_new),status='unknown')

      do while(.true.)
         read(iminfo,'(a)') line
         write(iminfo_new,'(a)') trim(line)
         if(index(line,"Vibrational Data") > 0) exit
      end do

      read(iminfo,*)
      scfthresh  = Vscf_getEthresh()
      scfmaxiter = Vscf_getMaxIteration()
      if(iscreen == 0) then
         write(iminfo_new,'('' VSCFeth='',e8.2,'',VSCFmaxIter='',i4,'',Eth='',e8.2, &
                    '',Gth='',e8.2,'',Fse='',i4)') &
                    scfthresh,scfmaxiter,ethresh,gthresh,order
      else
         write(iminfo_new,'('' VSCFeth='',e8.2,'',VSCFmaxIter='',i4,'',Eth='',e8.2, &
                    '',Gth='',e8.2,'',Fse='',i4,'',eta12th='',f6.2)') &
                    scfthresh,scfmaxiter,ethresh,gthresh,order,eta12thresh
      endif

      do while(.true.)
         read(iminfo,'(a)') line
         write(iminfo_new,'(a)') trim(line)
         if(index(line,"Vibrational Frequency") > 0) exit
      end do

      write(iminfo_new,'(i0)') nfree
      do i = 1, nfree-1
         write(iminfo_new,'(es15.8,$)') freq_new(i)
         if(mod(i,5)/=0) then
           write(iminfo_new,'(", ",$)')
         else
           write(iminfo_new,*)
         end if
      end do
      write(iminfo_new,'(es15.8)') freq_new(nfree)

      write(iminfo_new,'("Vibrational vector")')
      do i = 1, nfree
         write(iminfo_new,'("Mode ",i3)') i
         write(iminfo_new,'(i0)') nat3
         do j = 1, nat3-1
            write(iminfo_new,'(es15.8,$)') cv_new(j,i)
            if(mod(j,5)/=0) then
              write(iminfo_new,'(", ",$)')
            else
              write(iminfo_new,*)
            end if
         end do
         write(iminfo_new,'(es15.8)') cv_new(nat3,i)
      end do

      close(iminfo)
      close(iminfo_new)
      deallocate(mass, freq, cv, hess)
      deallocate(freq_new, cv_new)

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

