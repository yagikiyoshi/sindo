package vibration;

import sys.Constants;

/**
 * Transforms the vibrational coordinates.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class TransformCoord {
   
   private CoordinateData coordData;
   private double[][] U1;
   private double[] Hessian;
   
   public TransformCoord(CoordinateData coordData){
      this.coordData = coordData;
   }
   
   public void setTransformationMatrix(double[][] U1){
      this.U1 = U1;
   }
   
   /**
    * Sets the Hessian matrix 
    * @param hessian 1D array of the lower half of Hessian matrix
    */
   public void setHessian(double[] hessian){
      this.Hessian = hessian;
   }
   
   public CoordinateData transform(){
      int Nat3 = coordData.Nat*3;
      int Nfree = coordData.Nfree;
      
      double[][] cv = coordData.getCV();
      double[][] cv_new = new double[Nfree][Nat3];
      
      for(int i=0; i<Nfree; i++){
         for(int s=0; s<Nfree; s++){
            for(int x=0; x<Nat3; x++){
               cv_new[i][x] = cv_new[i][x] + cv[s][x]*U1[s][i];
            }
         }
      }
      
      double[] msqrt = coordData.getMsqrt();
      double[][] mwHess = new double[Nat3][Nat3];
      for(int i1=0; i1<Nat3; i1++){
         int m1 = i1/3;
         for(int i2=0; i2<=i1; i2++){
            int m2 = i2/3;
            mwHess[i1][i2] = Hessian[i1*(i1+1)/2 + i2]/msqrt[m1]/msqrt[m2];
            mwHess[i2][i1] = mwHess[i1][i2];
         }
      }

      double[] omega_new = new double[Nfree];
      for(int i=0; i<Nfree; i++){
         for(int x1=0; x1<Nat3; x1++){
            for(int x2=0; x2<Nat3; x2++){
               omega_new[i] = omega_new[i] + cv_new[i][x1]*mwHess[x1][x2]*cv_new[i][x2];
            }
         }
         omega_new[i] = Math.sqrt(omega_new[i])*Constants.Hartree2wvn;         
      }

      coordData.setCV(cv_new);
      coordData.setOmegaV(omega_new);

      return coordData;
   }

}
