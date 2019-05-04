
   implicit none
   integer :: i, j, nat
   real(8) :: ene, dipole(3)
   character(80) :: fname, line

   open(10,file='makeGrid.xyz',status='old')
   open(11,file='makeGrid.dat',status='unknown')
   do while(.true.)
     read(10,*,end=100) nat
     read(10,'(a)') fname
     do i = 1, nat
       read(10,*) 
     end do
 
     open(12,file='minfo.files/'//trim(fname)//'.minfo',status='old')
     do while(.true.)
       read(12,'(a)') line
       if(index(line,'Energy') > 0) then
         read(12,*) ene
       else if(index(line,'Dipole') >0) then
         read(12,*)
         read(12,*) dipole
         exit
       end if
     end do
     close(12)

     write(11,'(a,", ",f25.13,", ",2(e20.10,", "), e20.10)') trim(fname), ene, dipole
   end do
   100 continue
   close(10)
   close(10)

   end
