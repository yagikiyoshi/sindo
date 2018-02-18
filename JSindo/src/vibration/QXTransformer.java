package vibration;

/**
 * Provides transformation between Cartesian and vibrational coordinates.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class QXTransformer {
   
   private int Nat, Nat3, Nfree;
   private double[][] x0;
   private double[] msqrt;
   private double[][] CL;
   private double[][] CL2;

   /**
    * Transform the geometry in q to x: xx = x0 + qq*CL
    * @param qq Vibrational coordinates [Nfree] (bohr(emu)1/2)
    * @return Cartesian coordinates [Nat][3]  (bohr)
    */
   public double[][] q2x(double[] qq){
      double[][] xx = new double[Nat][3];
      for(int i=0; i<Nfree; i++){
         int jk=0;
         for(int j=0; j<Nat; j++){
            for(int k=0; k<3; k++){
               xx[j][k] = xx[j][k] + CL[i][jk]*qq[i];
               jk++;
            }
         }
      }
      for(int i=0; i<Nat; i++){
         for(int j=0; j<3; j++){
            xx[i][j] = x0[i][j] + xx[i][j]/msqrt[i];            
         }
      }
      return xx;
   }
   /**
    * Returns the displacement in Cartesian: dx = q*CL[i]
    * @param qi Mode to be displaced
    * @param q  Vibrational coordinate of the qi-th mode (bohr(emu)1/2)
    * @return Displacement [Nat][3] (bohr). This is a newly generated object.
    */
   public double[][] dq2dx(int qi, double q){
      double[][] xx = new double[Nat][3];
      int jk=0;
      for(int i=0; i<Nat; i++){
         for(int j=0; j<3; j++){
            xx[i][j] = CL[qi][jk]*q/msqrt[i];
            jk++;
         }
      }
      return xx;
   }
   /**
    * Converts the gradient in x to q. 
    * @param gxyz Gradient in Cartesian (Hartree/bohr). 
    * @return Gradient in vibrational coordinates [Nat*3] (Hartree/bohr(emu)^1/2) 
    */
   public double[] gx2gq(double[] gxyz){
      double[] gxyz2 = new double[Nat3]; 
      for(int i=0; i<Nat3 ; i++){
         gxyz2[i] = gxyz[i]/msqrt[i/3];
      }
      double[] gq = new double[Nfree];
      for(int i=0; i<Nfree; i++){
         gq[i] = 0.0d;
         for(int j=0; j<Nat3; j++){
            gq[i] = gq[i] + CL[i][j]*gxyz2[j];
         }
      }
      return gq;
   }
   /**
    * Converts the Hessian in Cartesian to Vibrational coordinates 
    * @param hxyz Hessian in Cartesian in a packed form [Nat3][Nat3] (Hartree/bohr^2). 
    * @return Hessian in vibrational coordinates [Nfree][Nfree] (Hartree/bohr^2 (emu)
    */
   public double[][] hx2hq(double[] hxyz){
      double[][] hxyz2 = new double[Nat3][Nat3];
      int k=0;
      for(int i=0; i<Nat*3; i++){
         for(int j=0; j<=i; j++){
            hxyz2[i][j] = hxyz[k];
            hxyz2[j][i] = hxyz2[i][j];
            k++;
         }
      }
      return hx2hq(hxyz2);
   }
   /**
    * Converts the Hessian in x to q. 
    * @param hxyz Hessian in Cartesian [Nat3][Nat3] (Hartree/bohr^2). 
    * @return Hessian in vibrational coordinates [Nfree][Nfree] (Hartree/bohr^2 (emu)
    */
   public double[][] hx2hq(double[][] hxyz){
      double[][] hxyz2 = new double[Nat3][Nat3];
      for(int i=0; i<Nat3; i++){
         for(int j=0; j<=i; j++){
            hxyz2[i][j] = hxyz[i][j]/msqrt[i/3]/msqrt[j/3];
            hxyz2[j][i] = hxyz2[i][j];
         }
      }
      double[][] aa = new double[Nat3][Nfree];
      for(int i=0; i<Nat3; i++){
         for(int j=0; j<Nfree; j++){
            aa[i][j] = 0.0d;
            for(int k=0; k<Nat3; k++){
               aa[i][j] = aa[i][j] + hxyz2[i][k]*CL2[k][j];
            }
         }         
      }
      double[][] hq = new double[Nfree][Nfree];
      for(int i=0; i<Nfree; i++){
         for(int j=0; j<Nfree; j++){
            hq[i][j] = 0.0d;
            for(int k=0; k<Nat3; k++){
               hq[i][j] = hq[i][j] + CL[i][k]*aa[k][j];
            }
         }         
      }
      return hq;
   }

   /**
    * Sets the transformation matrix, etc. needed for transformation
    * @param coordData The data of coordinate
    */
   public void appendCoordinate(CoordinateData coordData){
      this.x0 = coordData.getX0();
      this.msqrt = coordData.getMsqrt();
      this.CL = coordData.getCV();
      
      Nat = coordData.Nat;
      Nat3 = Nat*3;
      Nfree = coordData.Nfree;

      CL2 = new double[Nat3][Nfree];
      for(int i=0; i<Nat3; i++){
         for(int j=0; j<Nfree; j++){
            CL2[i][j] = CL[j][i];
         }
      }
   }

}
