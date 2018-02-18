package vibration;

import sys.Constants;

/**
 * (Package private) Driver class for Boys localization
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class LocalizaBoys extends Localiza {

   private double[][] Mklx;
   /**
    * The mass weighted Cartesian coordinates
    */
   private double[] z0;
   
   /**
    * This class is package private.
    */
   LocalizaBoys(){
      
   }
   
   public void setup(CoordinateData coordData){
      super.setup(coordData);
      
      // Units in bohr(amu)1/2
      double[][] x0 = coordData.getX0();
      double[] msqrt = coordData.getMsqrt();
      z0 = new double[Nat3];
      int ij=0;
      for(int i=0; i<Nat; i++){
         for(int j=0; j<3; j++){
            z0[ij] = x0[i][j]*msqrt[i]*Math.sqrt(Constants.Emu2Amu);
            ij++;
         }
      }
      
      int kl=0;
      Mklx = new double[Nfree*(Nfree+1)/2][3];
      for(int k=0; k<Nfree; k++){
         for(int l=0; l<=k; l++){
            for(int x=0; x<3; x++){
               Mklx[kl][x] = 0.0d;
            }
            for(int a=0; a<Nat; a++){
               double ckl=0.0d;
               for(int x=0; x<3; x++){
                  ckl = ckl + CL[k][a*3+x]*CL[l][a*3+x];
               }
               for(int x=0; x<3; x++){
                  Mklx[kl][x] = Mklx[kl][x] + ckl*z0[a*3+x];
               }
               
            }
            /*
            if(k==l) {
               for(int x=0; x<3; x++){
                  System.out.printf("%12.4f ",Mklx[kl][x]);                  
               }
               System.out.println();
            }
            */
            kl++;
         }
      }

   }
   @Override
   public double getK(int i, int j, int k, int l) {
      double KK=0.0d;
      int ij = i*(i+1)/2+j;
      int kl = k*(k+1)/2+l;
      for(int x=0; x<3; x++){
         KK = KK + Mklx[ij][x]*Mklx[kl][x];
      }
      return KK;
   }

   @Override
   public double getZeta(int[] modes) {
      double zeta=0.0d;
      int ii;
      for(int i=0; i<modes.length; i++){
         ii=modes[i]*(modes[i]+1)/2 +modes[i];
         for(int x=0; x<3; x++){
            zeta = zeta + Mklx[ii][x]*Mklx[ii][x];
         }
      }
      return zeta;
   }

   @Override
   public void update(int i, int j) {
      for(int k=0; k<Nfree; k++){
         int ik = 0;
         if(i>k){
            ik = i*(i+1)/2 + k;            
         }else{
            ik = k*(k+1)/2 + i;            
         }
         int jk = 0;
         if(j>k){
            jk = j*(j+1)/2 + k;
         }else{
            jk = k*(k+1)/2 + j;
         }
         for(int x=0; x<3; x++){
            Mklx[ik][x] = 0.0d;
            Mklx[jk][x] = 0.0d;
         }

         for(int a=0; a<Nat; a++){
            double cik=0.0d;
            double cjk=0.0d;
            for(int x=0; x<3; x++){
               cik = cik + CL[i][a*3+x]*CL[k][a*3+x];
               cjk = cjk + CL[j][a*3+x]*CL[k][a*3+x];
            }
            for(int x=0; x<3; x++){
               Mklx[ik][x] = Mklx[ik][x] + cik*z0[a*3+x];
               Mklx[jk][x] = Mklx[jk][x] + cjk*z0[a*3+x];
            }
         }
      }
   }

}
