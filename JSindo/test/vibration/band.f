
      Implicit None

      Integer, parameter :: nst0=150
      Real(8), parameter :: cutoff=1.0D-02, Gamma=10.0D+00

      Integer :: i,j,k,nst,num
      Real(8) :: Ene(nst0),tty(nst0),ww,Intty,Lorentzian

         Open(10,file='band.info',status='unknown')
         Read(10,*) nst

         num=1
         Do i=1,nst
            Read(10,*) Ene(num),tty(num)
            if(tty(num) .gt. cutoff) num=num+1
         End do
         num=num-1

         Close(10)

         !chk Write(6,'(i4)') num
         !chk Write(6,'(2f12.4)') (Ene(i),tty(i),i=1,num)

         ww=1600D+00
         Do while(ww .lt. 1900.D+00)
            Intty=0.D+00
            Do i=1,num
               if(abs(Ene(i)-ww) .gt. 1.D+03) cycle
               Intty=Intty + Lorentzian(ww,Ene(i),tty(i),Gamma)
            End do
            !Intty=Intty*100  ! km cm -> cm^2
            Write(6,'(2f12.4)') ww,Intty
            ww=ww+1.D+00
         End do

      End

      Function Lorentzian(ww,w0,A,G)

      Implicit None

         Real(8), parameter :: PI=3.14159265358979312D+00
         Real(8) :: Lorentzian,ww,w0,A,G,G2

         G2 = G*G
         !Lorentzian = A*G2 / ((ww-w0)*(ww-w0) + G2)
         Lorentzian = 2.0D+00 * A*G / (4.D+00*(ww-w0)*(ww-w0) + G2) / PI

      End Function

