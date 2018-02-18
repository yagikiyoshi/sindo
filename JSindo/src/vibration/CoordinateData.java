package vibration;

import sys.Utilities;

/**
 * Data of vibrational coordinates.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class CoordinateData implements Cloneable{

   /**
    * Number of atoms
    */
   public int Nat;
   /**
    * Number of vibrational degree of freedom
    */
   public int Nfree;
   /**
    * Number of rotational degree of freedom
    */
   public int Nrot;
   /**
    * SQRT(mass) of each atoms [Nat] (emu)^1/2
    */
   private double[] msqrt;
   /**
    * Cartesian coordinates [Nat][3]
    */
   private double[][] x0;
   /**
    * Translational vector [3][Nat3]
    */
   private double[][] CT;
   /**
    * Rotational vector [Nrot][Nat3]
    */
   private double[][] CR;
   /**
    * Vibrational vector [Nfree][Nat3]
    */
   private double[][] CV;
   /**
    * Translational frequency [3]
    */
   private double[] omegaT;
   /**
    * Rotational frequency [Nrot]
    */
   private double[] omegaR;
   /**
    * Vibrational frequency [Nfree]
    */
   private double[] omegaV;
   /**
    * This is a package private class
    */
   CoordinateData(){
      
   }
   /**
    * Returns the translational vectors  [3][Nat3]
    * @return Translational vectors
    */
   public double[][] getCT() {
      return CT;
   }
   /**
    * Returns the rotational vectors  [2 or 3][Nat3]
    * @return Rotational vectors
    */
   public double[][] getCR() {
      return CR;
   }
   /**
    * Returns the vibrational vectors [Nfree][Nat3]
    * @return Vibrational vectors
    */
   public double[][] getCV() {
      return CV;
   }
   /**
    * Returns the translational frequencies [3]  (cm-1)
    * @return omega_t
    */
   public double[] getOmegaT() {
      return omegaT;
   }
   /**
    * Returns the rotational frequencies [Nrot]  (cm-1)
    * @return omega_r
    */
   public double[] getOmegaR() {
      return omegaR;
   }
   /**
    * Returns the vibrational frequencies [Nfree] (cm-1)
    * @return omega_v
    */
   public double[] getOmegaV() {
      return omegaV;
   }
   /**
    * Returns the square root of mass [Nat] (emu)^1/2
    * @return SQRT(mass)
    */
   public double[] getMsqrt() {
      return msqrt;
   }
   /**
    * Returns the reference (equilibrium) geometry [Nat][3] (bohr) 
    * @return geometry
    */
   public double[][] getX0() {
      return x0;
   }
   /**
    * Sets the translational vectors [3][Nat3] 
    * @param cT translational vectors
    */
   void setCT(double[][] cT) {
      CT = cT;
   }
   /**
    * Sets the rotational vectors [Nrot][Nat3]
    * @param cR rotational vectors
    */
   void setCR(double[][] cR) {
      CR = cR;
   }
   /**
    * Sets the vibrational vectors [Nfree][Nat3]
    * @param cV vibrational vectors
    */
   void setCV(double[][] cV) {
      CV = cV;
   }
   /**
    * Sets the translational frequency [3] (cm-1)
    * @param omegaT translational frequency
    */
   void setOmegaT(double[] omegaT) {
      this.omegaT = omegaT;
   }
   /**
    * Sets the rotational frequency [Nrot] (cm-1)
    * @param omegaR rotational frequency
    */
   void setOmegaR(double[] omegaR) {
      this.omegaR = omegaR;
   }
   /**
    * Sets the vibrational frequency [Nfree] (cm-1)
    * @param omegaV vibrational frequency
    */
   void setOmegaV(double[] omegaV) {
      this.omegaV = omegaV;
   }
   /**
    * Sets the square root of the mass of each atom [Nat] (emu)^1/2
    * @param msqrt SQRT(mass)
    */
   void setMsqrt(double[] msqrt){
      this.msqrt = msqrt;
   }
   /**
    * Sets the mass of each atom [Nat] (emu)
    * @param mass mass
    */
   void setMass(double[] mass) {
      msqrt = new double[mass.length];
      for(int i=0; i<mass.length; i++){
         msqrt[i] = Math.sqrt(mass[i]);
      }
   }
   /**
    * Sets the reference (equilibrium) geometry [Nat][3] (bohr)
    * @param x0 geometry
    */
   void setX0(double[][] x0) {
      this.x0 = x0;
   }
   /**
    * Returns a copy of this object
    * @return CoordinateData
    */
   public CoordinateData clone(){
      CoordinateData coordinate = null;
      try{
         coordinate = (CoordinateData)super.clone();         
      }catch(CloneNotSupportedException e){
         // Do nothing
      }
      if(msqrt != null){
         double[] newMqsrt = Utilities.deepCopy(msqrt);
         coordinate.setMsqrt(newMqsrt);
      }
      if(x0 != null){
         double[][] newx0 = Utilities.deepCopy(x0);
         coordinate.setX0(newx0);
      }
      if(CT != null){
         double[][] newCT = Utilities.deepCopy(CT);
         coordinate.setCT(newCT);
      }
      if(CR != null){
         double[][] newCR = Utilities.deepCopy(CR);
         coordinate.setCR(newCR);
      }
      if(CV != null){
         double[][] newCV = Utilities.deepCopy(CV);
         coordinate.setCV(newCV);
      }
      if(omegaT != null){
         double[] newomegaT = Utilities.deepCopy(omegaT);
         coordinate.setOmegaT(newomegaT);
      }
      if(omegaR != null){
         double[] newomegaR = Utilities.deepCopy(omegaR);
         coordinate.setOmegaR(newomegaR);
      }
      if(omegaV != null){
         double[] newomegaV = Utilities.deepCopy(omegaV);
         coordinate.setOmegaV(newomegaV);
      }
      return coordinate;
   }
   /**
    * Print the vibrational coordinates and associated frequencies.
    */
   public void print(){
      System.out.println("x0:");
      for(int i=0; i<Nat; i++){
         for(int j=0; j<3; j++){
            System.out.printf(" %8.4f",x0[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      
      System.out.println("Translational Vectors:");
      for(int i=0; i<3; i++){
         System.out.printf(" %8.2f cm-1 : ",omegaT[i]);
         for(int j=0; j<Nat*3-1; j++){
            System.out.printf(" %8.4f",CT[i][j]);
            if(j%9 == 8) {
               System.out.println();
               System.out.print("                 ");
            }
         }
         System.out.printf(" %8.4f",CT[i][Nat*3-1]);
         System.out.println();         
      }
      System.out.println();
      
      System.out.println("Rotational Vectors:");
      for(int i=0; i<CR.length; i++){
         System.out.printf(" %8.2f cm-1 : ",omegaR[i]);
         for(int j=0; j<Nat*3-1; j++){
            System.out.printf(" %8.4f",CR[i][j]);
            if(j%9 == 8) {
               System.out.println();
               System.out.print("                 ");
            }
         }
         System.out.printf(" %8.4f",CR[i][Nat*3-1]);
         System.out.println();
      }
      System.out.println();

      System.out.println("Vibrational Vectors:");
      for(int i=0; i<CV.length; i++){
         System.out.printf(" %8.2f cm-1 : ",omegaV[i]);
         for(int j=0; j<Nat*3-1; j++){
            System.out.printf(" %8.4f",CV[i][j]);
            if(j%9 == 8) {
               System.out.println();
               System.out.print("                 ");
            }
         }
         System.out.printf(" %8.4f",CV[i][Nat*3-1]);
         System.out.println();
      }
      System.out.println();
      
   }

}
