
   Implicit None

   Integer :: c1,c2,c3
   Integer, allocatable :: m1(:),v1(:),m2(:),v2(:),m3(:),v3(:)

      c1=4
      c2=2
      c3=c1+c2
      Allocate(m1(c1),v1(c1),m2(c2),v2(c2),m3(c3),v3(c3))

      m1(1)=1; m1(2)=2; m1(3)=5; m1(4)=7
      v1(1)=1; v1(2)=2; v1(3)=3; v1(4)=4
      m2(1)=2; m2(2)=3
      v2(1)=5; v2(2)=6

      Call mvMinus(c1,m1,v1,c2,m2,v2,c3,m3,v3)

      write(6,'(''m1='',10i3)') m1
      write(6,'(''v1='',10i3)') v1
      write(6,*)
      write(6,'(''m2='',10i3)') m2
      write(6,'(''v2='',10i3)') v2
      write(6,*)
      write(6,'(''m3='',10i3)') m3(1:c3)
      write(6,'(''v3='',10i3)') v3(1:c3)

      Deallocate(m1,v1,m2,v2,m3,v3)

   End
