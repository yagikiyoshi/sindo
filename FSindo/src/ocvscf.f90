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
         Call System('java OcVSCF '//minfofile(1:idx-1)//' '//trim(u1file))
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
