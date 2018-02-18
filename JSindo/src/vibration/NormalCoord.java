package vibration;

import solver.Diagonalizer;
import solver.Solver;
import sys.Constants;
import sys.Utilities;

/**
 * Calculates the normal coordinates.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class NormalCoord{

   /**
    * The number of atoms
    */
   protected int Nat;
   /**
    * Nat *3
    */
   protected int Nat3;
   /**
    * The number of rotational degrees of freedom
    */
   protected int Nrot;
   /**
    * The number of vibrational degrees of freedom
    */
   protected int Nfree;
   /**
    * The information of vibrational coordinates
    */
   protected CoordinateData coordData;
   /**
    * True if the molecule is linear
    */
   protected boolean linear = false;
   /**
    * True if print the result
    */
   protected boolean print = true;
   /**
    * True if trans and rot modes are needed
    */
   protected boolean calcTR = true;

   /**
    * Sets that the molecule is linear.
    * @param linear linear molecule if true
    */
   public void setLinear(boolean linear){
      this.linear = linear;
   }
   /**
    * Sets to print the frequency and vector
    * @param print print if true
    */
   public void setPrint(boolean print){
      this.print = print;
   }
   /**
    * Sets trans and rot modes
    * @param calcTR calculate trans and rot modes if true
    */
   public void setTransRot(boolean calcTR){
      this.calcTR = calcTR;
   }
   /**
    * Returns the coordinates
    * @return the coordinates
    */
   public CoordinateData getCoordinate(){
      return coordData;
   }
   /**
    * Sets the coordinates
    * @param coordData the coordinates
    */
   public void setCoordinate(CoordinateData coordData){
      this.coordData = coordData;
   }
   /**
    * Calculate the normal coordinates and the harmonic frequencies.
    * @param mass mass of the atoms [Nat] (emu)
    * @param x0 XYZ coordinates of the reference geometry [Nat][3] (bohr)
    * @param hessian Hessian matrix in packed form [Nat*3*(Nat*3+1)/2] (Hartree/bohr^2)
    * @return Normal coordinates
    */
   public CoordinateData calc(double[] mass, double[][] x0, double[] hessian){
      
      Nat = mass.length;
      Nat3 = Nat*3;
      
      if(coordData == null) coordData = new CoordinateData();
      coordData.Nat = Nat;
      coordData.setX0(x0);
      coordData.setMass(mass);

      double[] msqrt = coordData.getMsqrt();
      double[][] mwHess = new double[Nat3][Nat3];
      for(int i1=0; i1<Nat3; i1++){
         int m1 = i1/3;
         for(int i2=0; i2<=i1; i2++){
            int m2 = i2/3;
            mwHess[i1][i2] = hessian[i1*(i1+1)/2 + i2]/msqrt[m1]/msqrt[m2];
            mwHess[i2][i1] = mwHess[i1][i2];
         }
      }
      
      if(calcTR){
         this.calcTranslation(mass, mwHess);
         this.calcRotation(mwHess);
         if(coordData.getCR() != null) this.calcNormalModes(mwHess);         
      }else{
         this.calcNormalModes(mwHess);
      }
      
      if(print){
         System.out.println("--- Normal mode calculations ---");
         coordData.print();
         System.out.println("--- End of Normal mode calculations ---");
         System.out.println();
         
      }
      
      return coordData;
      
   }

   private void calcTranslation(double[] mass, double[][] mwHess){
   
      double totmsqrt = 0.0d;
      for(int i=0; i<Nat; i++){
         totmsqrt = totmsqrt + mass[i];
      }
      totmsqrt = Math.sqrt(totmsqrt);
      
      double[] msqrt = coordData.getMsqrt();
      double[][] cT = new double[3][Nat3];
      for(int i=0; i<3; i++){
         for(int j=0; j<Nat; j++){
            cT[i][3*j+i]=msqrt[j]/totmsqrt;
         }
      }
      coordData.setCT(cT);
      
      double[] omegaT = new double[3];
      for(int i=0; i<omegaT.length; i++){
         double[] vec = new double[Nat3];
         for(int j=0; j<Nat3; j++){
            vec[j]=0.0d;
            for(int k=0; k<Nat3; k++){
               vec[j] = vec[j] + mwHess[j][k]*cT[i][k];
            }
         }
         omegaT[i]=0.0d;
         for(int j=0; j<Nat3; j++){
            omegaT[i]=omegaT[i] + cT[i][j]*vec[j];
         }
         
         for(int j=0; j<Nat3; j++){
            for(int k=0; k<Nat3; k++){
               mwHess[j][k] = mwHess[j][k] - cT[i][j]*vec[k] - vec[j]*cT[i][k] + cT[i][j]*omegaT[i]*cT[i][k];
            }
         }
      }
      
      for(int i=0; i<omegaT.length; i++){
         if(omegaT[i]>0.0d){
            omegaT[i] = Math.sqrt(omegaT[i])*Constants.Hartree2wvn;
         }else{
            omegaT[i] = -Math.sqrt(-omegaT[i])*Constants.Hartree2wvn;
         }
         //System.out.printf(" %12.2f \n",omegaT[i]);
      }
      coordData.setOmegaT(omegaT);
      
   }
   private void calcRotation(double[][] mwHess){
   
      double[] msqrt = coordData.getMsqrt();
      double[][] x0 = coordData.getX0();
      
      double[][] cR = null;

      if(! linear){
         Nrot = 3;
         coordData.Nrot = Nrot;

         cR = new double[3][Nat3];
         for(int j=0; j<Nat; j++){
            cR[0][3*j]  = 0.0d;
            cR[0][3*j+1]=-x0[j][2]*msqrt[j];
            cR[0][3*j+2]= x0[j][1]*msqrt[j];
   
            cR[1][3*j]  = x0[j][2]*msqrt[j];
            cR[1][3*j+1]= 0.0d;
            cR[1][3*j+2]=-x0[j][0]*msqrt[j];
   
            cR[2][3*j]  =-x0[j][1]*msqrt[j];
            cR[2][3*j+1]= x0[j][0]*msqrt[j];
            cR[2][3*j+2]= 0.0d;
         }
         
         for(int i=0; i<3; i++){
            Utilities.normalize(cR[i]);            
         }
         double ovlp1 = Utilities.dotProduct(cR[0], cR[1]);
         for(int i=0; i<Nat3; i++){
            cR[1][i] = cR[1][i] - ovlp1*cR[0][i];
         }
         Utilities.normalize(cR[1]);
         
         ovlp1 = Utilities.dotProduct(cR[0], cR[2]);
         double ovlp2 = Utilities.dotProduct(cR[1], cR[2]);
         for(int i=0; i<Nat3; i++){
            cR[2][i] = cR[2][i] - ovlp1*cR[0][i] - ovlp2*cR[1][i];
         }
         Utilities.normalize(cR[2]);
         
      }else{
         Nrot = 2;

         // Linear molecule
         int axis=-1;
         for(int i=0; i<3; i++){
            double aa = 0.0d;
            for(int j=0; j<Nat; j++){
               aa = aa + Math.abs(x0[j][i]);               
            }
            if(aa>1.e-04){
               if(axis==-1){
                  axis=i;
               }else{
                  System.out.println("Error in NormalCoord.");
                  System.out.println("Linear molecule must lie on one of XYZ axiz.");
                  return;
               }
            }
         }
         // System.out.println(axis);

         cR = new double[2][Nat3];
         switch(axis){
         case 0:
            for(int j=0; j<Nat; j++){
               cR[0][3*j]  =-x0[j][1]*msqrt[j];
               cR[0][3*j+1]= x0[j][0]*msqrt[j];
               cR[0][3*j+2]= 0.0d;
   
               cR[1][3*j]  = x0[j][2]*msqrt[j];
               cR[1][3*j+1]= 0.0d;
               cR[1][3*j+2]=-x0[j][0]*msqrt[j];
            }
            break;
   
         case 1:
            for(int j=0; j<Nat; j++){
               cR[0][3*j]  =-x0[j][1]*msqrt[j];
               cR[0][3*j+1]= x0[j][0]*msqrt[j];
               cR[0][3*j+2]= 0.0d;
   
               cR[1][3*j]  = 0.0d;
               cR[1][3*j+1]=-x0[j][2]*msqrt[j];
               cR[1][3*j+2]= x0[j][1]*msqrt[j];
            }
            break;
   
         case 2:
            for(int j=0; j<Nat; j++){
               cR[0][3*j]  = x0[j][2]*msqrt[j];
               cR[0][3*j+1]= 0.0d;
               cR[0][3*j+2]=-x0[j][0]*msqrt[j];
   
               cR[1][3*j]  = 0.0d;
               cR[1][3*j+1]=-x0[j][2]*msqrt[j];
               cR[1][3*j+2]= x0[j][1]*msqrt[j];
            }
            break;
         }
         Utilities.normalize(cR[0]);
         double aa = Utilities.dotProduct(cR[0], cR[1]);
         for(int i=0; i<cR[0].length; i++){
            cR[1][i] = cR[1][i] - aa*cR[0][i];
         }
         Utilities.normalize(cR[1]);
         
      }
      coordData.Nrot = Nrot;
      coordData.setCR(cR);
      

      double[] omegaR = new double[Nrot];
      for(int i=0; i<Nrot; i++){
         double[] vec = new double[Nat3];
         for(int j=0; j<Nat3; j++){
            vec[j]=0.0d;
            for(int k=0; k<Nat3; k++){
               vec[j] = vec[j] + mwHess[j][k]*cR[i][k];
            }
         }
         omegaR[i]=0.0d;
         for(int j=0; j<Nat3; j++){
            omegaR[i]=omegaR[i] + cR[i][j]*vec[j];
         }
         
         for(int j=0; j<Nat3; j++){
            for(int k=0; k<Nat3; k++){
               mwHess[j][k] = mwHess[j][k] - cR[i][j]*vec[k] - vec[j]*cR[i][k] + cR[i][j]*omegaR[i]*cR[i][k];
            }
         }
   
         if(omegaR[i]>0.0d){
            omegaR[i] = Math.sqrt(omegaR[i])*Constants.Hartree2wvn;
         }else{
            omegaR[i] = -Math.sqrt(-omegaR[i])*Constants.Hartree2wvn;
         }
         // System.out.printf(" %12.2f \n",omegaR[i]);
      }
      coordData.setOmegaR(omegaR);
      
   }
   private void calcNormalModes(double[][] mwHess){
      
      Diagonalizer solver = Solver.getDiagonalizer();
      solver.diag(mwHess);
   
      double[] dd = solver.getEigenValue();
      double[][] ll = solver.getTransposedVector();
      
      double tmp;
      double[] vtmp;
      for(int i=1; i<Nat3; i++){
         for(int j=0; j<i; j++){
            if(dd[i]<dd[j]){
               tmp = dd[i];
               dd[i] = dd[j];
               dd[j] = tmp;
               
               vtmp = ll[i];
               ll[i] = ll[j];
               ll[j] = vtmp;
            }
         }
      }
   

      double[] omegaV = null;
      double[][] cV   = null;

      if(calcTR){
         int Ntr = Nrot + 3;
         Nfree = Nat3 - Ntr;  
         omegaV = new double[Nfree];
         cV = new double[Nfree][];
         
         int img=0;
         while(dd[img]<0.0d){
            omegaV[img] = -Math.sqrt(-dd[img])*Constants.Hartree2wvn;
            if(omegaV[img]>-10.0d) break;
            // System.out.printf(" %4d %12.2f \n",img,omega[img]);
            cV[img] = ll[img];
            img++;
         }
         
         int j=img;
         for(int i=img+Ntr; i<Nat3; i++){
            omegaV[j] = Math.sqrt(dd[i])*Constants.Hartree2wvn;
            // System.out.printf(" %4d %12.2f \n",j,omega[j]);
            cV[j] = ll[i];
            j++;
         }
         

      }else{
         Nfree = Nat3;

         omegaV = new double[Nfree];
         cV = new double[Nfree][];
         
         int img=0;
         while(dd[img]<0.0d){
            omegaV[img] = -Math.sqrt(-dd[img])*Constants.Hartree2wvn;
            cV[img] = ll[img];
            img++;
         }
         
         for(int i=img; i<Nat3; i++){
            omegaV[i] = Math.sqrt(dd[i])*Constants.Hartree2wvn;
            cV[i] = ll[i];
         }
      }
      
      coordData.Nfree = Nfree;      
      coordData.setOmegaV(omegaV);
      coordData.setCV(cV);
      
   }

}
