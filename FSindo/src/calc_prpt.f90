!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
!   Last modified  2013/05/01
!   Copyright 2013
!   Kiyoshi Yagi
!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine calc_prpt()

   USE Constants_mod
   USE Prpt_mod

   Implicit None

   Integer :: i
   Logical :: lprpt,Vib_getlprpt
   Character(10) :: dipole

      lprpt = Vib_getlprpt()
      if(.not. lprpt) return

      Call Prpt_construct()

      if(Nprpt > 0) then
         write(iout,100)
     100 Format(3x,'>> CALC. PROPERTY MATRIX',/)

         Do i=1,Nprpt
  
            write(iout,110) i
            write(iout,111) trim(extn(i))
            if(matrix(i) ==0) then
               write(iout,112) 
            else
               write(iout,113) 
            endif
        110 Format(6x,'PROP ',i2,': ')
        111 Format(9x,'o EXTN  = ',a10)
        112 Format(9x,'o CALC THE AVERAGE',/)
        113 Format(9x,'o CALC ALL MATRIX ELEMENTS',/)
  
            Call Prpt_Modal_construct(Nfree,MR,extn(i))
  
            if(vscfprpt) then 
               Call Vscfprpt_calcPrptMatrix(i)
               Call Mem_printInfo
               Call timer(1,Iout)
            endif
  
            if(vciprpt) then
               Call Vciprpt_calcPrptMatrix(i)
               Call Mem_printInfo
               Call timer(1,Iout)
            endif
  
            if(vptprpt) then
               !Call Vptprpt_calcPrptMatrix(i)
               Call Mem_printInfo
               Call timer(1,Iout)
            endif
  
            if(vqdptprpt) then
               Call Vqdptprpt_calcPrptMatrix(i)
               Call Mem_printInfo
               Call timer(1,Iout)
            endif

            Call Prpt_Modal_destruct()
  
         End do

      endif

      if(infrared) then
         Call IR_construct()

         dipole='.dipole'
         Call Prpt_Modal_construct(Nfree,MR,dipole)

         if(vscfprpt) then 
            Call Vscfprpt_infrared()
            Call Mem_printInfo
            Call timer(1,Iout)
         endif

         if(vciprpt) then 
            Call Vciprpt_infrared()
            Call Mem_printInfo
            Call timer(1,Iout)
         endif

         if(vptprpt) then
            Call Vptprpt_infrared()
            Call Mem_printInfo
            Call timer(1,Iout)
         endif

         if(vqdptprpt) then
            Call Vqdptprpt_infrared()
            Call Mem_printInfo
            Call timer(1,Iout)
         endif

         Call Prpt_Modal_destruct()

      endif

      Call Prpt_destruct()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Prpt_Modal_construct(Nfree,MR,extn)

   USE Constants_mod

   Implicit None

   Integer :: Nfree,MR
   Character(10) :: extn
   Character(12) :: vscfFile

   Integer :: ierr,nCHO(Nfree)

      Call Vib_getnCHO(nCHO)
      Call Grid_surface_construct(Nfree,MR,extn,nCHO)
      Call setupModal_for_PRPT(Nfree)
      Call Grid_surface_setValue()

      ! We assume the VSCF modal for the ground state is used for calculation of
      ! the properties. 
      Call Vscf_getFilename(0,vscfFile)
      Call Modal_readVSCF(ierr,vscfFile)
      if(ierr < 0) then
         write(iout,'(''ERROR: FATAL ERROR WHILE READING THE MODAL COEFFICIENTS'')')
         write(iout,'(''ERROR: FILE ENDED ['',a,'']'')') vscfFile
         write(iout,*)
         Stop
      endif

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

!  Setup the modals

   Subroutine setupModal_for_PRPT(Nfree)

   USE grid_surface_mod

   Implicit None

   Integer :: Nfree

   Integer :: nCHO(Nfree)
   Integer :: i,n, mi,mj,mk,ml, nGi,nGj,nGk,nGl

      Call Vib_getnCHO(nCHO)

      Call Modal_construct(Nfree,nCHO)
      Call Modal_open()

      Do i=1,Nfree
         Call Modal_add(i,nCHO(i))
      End do

      Do n=1,nS1
         mi=mS1(1,n)
         nGi=nG1(1,n)
         Call Modal_add(mi,nGi)
      End do

      Do n=1,nS2
         mi=mS2(1,n)
         mj=mS2(2,n)
         nGi=nG2(1,n)
         nGj=nG2(2,n)
         Call Modal_add(mi,nGi)
         Call Modal_add(mj,nGj)
      End do

      Do n=1,nS3
         mi=mS3(1,n)
         mj=mS3(2,n)
         mk=mS3(3,n)
         nGi=nG3(1,n)
         nGj=nG3(2,n)
         nGk=nG3(3,n)
         Call Modal_add(mi,nGi)
         Call Modal_add(mj,nGj)
         Call Modal_add(mk,nGk)
      End do

      Do n=1,nS4
         mi=mS4(1,n)
         mj=mS4(2,n)
         mk=mS4(3,n)
         ml=mS4(4,n)
         nGi=nG4(1,n)
         nGj=nG4(2,n)
         nGk=nG4(3,n)
         nGl=nG4(4,n)
         Call Modal_add(mi,nGi)
         Call Modal_add(mj,nGj)
         Call Modal_add(mk,nGk)
         Call Modal_add(ml,nGl)
      End do

      Call Modal_close()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

   Subroutine Prpt_Modal_destruct

   Implicit None

      Call Grid_surface_destruct()
      Call Modal_destruct()

   End subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
