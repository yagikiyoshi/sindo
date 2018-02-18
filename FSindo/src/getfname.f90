!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine get_fnameExt0(fname,ext)

      Implicit None

         Integer :: i,imod
         Character(*) :: fname,ext

            fname='eq'//ext

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine get_oldfnameExt0(fname,ext)

      Implicit None

         Integer :: i,imod
         Character(*) :: fname,ext

            fname='q0'//ext

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine get_fname1(imod,fname)

      Implicit None

         Integer :: i,imod
         Character(*) :: fname
         Character*4 :: ext

            ext='.pot'
            Call get_fnameExt1(imod,fname,ext)

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine get_fnameExt1(imod,fname,ext)

      Implicit None

         Integer :: i,imod
         Character(*) :: fname,ext

            !if(imod<10) then
            !   write(fname,'(''q'',i1)') imod
            !elseif(imod<100) then
            !   write(fname,'(''q'',i2)') imod
            !else
            !   write(fname,'(''q'',i3)') imod
            !endif
            i=0
            Call get_header(imod,i,fname(i+1:))
            fname(i+1:)=ext

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      ! imod>jmod
      Subroutine get_fname2(imod,jmod,fname)

      Implicit None

         Integer :: imod,jmod,i,j
         Character(*) :: fname
         Character*4 :: ext

            ext='.pot'
            Call get_fnameExt2(imod,jmod,fname,ext)

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      ! imod>jmod
      Subroutine get_fnameExt2(imod,jmod,fname,ext)

      Implicit None

         Integer :: imod,jmod,i,j
         Character(*) :: fname,ext

            i=0
            Call get_header(imod,i,fname(i+1:))
            Call get_header(jmod,i,fname(i+1:))
            !fname(i+1:)='.pot'
            fname(i+1:)=ext

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      ! i>j>k
      Subroutine get_fname3(imod,jmod,kmod,fname)

      Implicit None

         Integer :: i,j,k,imod,jmod,kmod
         Character(*) :: fname
         Character*4 :: ext

            ext='.pot'
            Call get_fnameExt3(imod,jmod,kmod,fname,ext)

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      ! i>j>k
      Subroutine get_fnameExt3(imod,jmod,kmod,fname,ext)

      Implicit None

         Integer :: i,j,k,imod,jmod,kmod
         Character(*) :: fname,ext

         i=0
         Call get_header(imod,i,fname(i+1:))
         Call get_header(jmod,i,fname(i+1:))
         Call get_header(kmod,i,fname(i+1:))
         !fname(i+1:)='.pot'
         fname(i+1:)=ext

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      ! i>j>k>l
      Subroutine get_fname4(imod,jmod,kmod,lmod,fname)

      Implicit None

         Integer :: i,j,k,imod,jmod,kmod,lmod
         Character(*) :: fname
         Character*4 :: ext

            ext='.pot'
            Call get_fnameExt4(imod,jmod,kmod,lmod,fname,ext)

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      ! i>j>k>l
      Subroutine get_fnameExt4(imod,jmod,kmod,lmod,fname,ext)

      Implicit None

         Integer :: i,j,k,imod,jmod,kmod,lmod
         Character(*) :: fname,ext

         i=0
         Call get_header(imod,i,fname(i+1:))
         Call get_header(jmod,i,fname(i+1:))
         Call get_header(kmod,i,fname(i+1:))
         Call get_header(lmod,i,fname(i+1:))
         fname(i+1:)=ext

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80

      Subroutine get_header(imod,ipt,fp)

      Implicit None

         Integer :: imod,ipt
         Character(*) :: fp
         Character :: cm1*1,cm2*2,cm3*3

         if(imod<10) then
            write(cm1,'(i1)') imod
            fp='q'//cm1
            ipt=ipt+2
         elseif(imod<100) then
            write(cm2,'(i2)') imod
            fp='q'//cm2
            ipt=ipt+3
         else
            write(cm3,'(i3)') imod
            fp='q'//cm3
            ipt=ipt+4
         endif

      End Subroutine

!---+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----80
