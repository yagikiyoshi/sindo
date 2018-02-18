package vibration;

import sys.Constants;

/**
 * Calculates the local coordinates.
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
//public class LocalCoord extends NormalCoord {
public class LocalCoord {

   private boolean Boys       = true;
   private boolean PipekMezey = false;
   private double Ethresh     = 100.0d;
   private double Zthresh     = 0.0001d;
   private int maxIter        = 30;
   
   private CoordinateData coordData;
   private int Nat;
   private int Nat3;
   private int Nfree;
   private int Nrot;
   
   private boolean print = false;

   /**
    * Sets the energy window (default = 100 cm-1)
    * @param Ethresh the energy window (cm-1)
    */
   public void setEthresh(double Ethresh){
      this.Ethresh = Ethresh;
   }
   /**
    * Returns the energy window (cm-1)
    * @return the energy window
    */
   public double getEthresh(){
      return Ethresh;
   }
   /**
    * Convergence threshold of zeta maximization cycle (default = 0.0001)
    * @param Zthresh the threshold value 
    */
   public void setZthresh(double Zthresh){
      this.Zthresh = Zthresh;
   }
   /**
    * Returns the convergence threshold
    * @return the threshold
    */
   public double getZthresh(){
      return Zthresh;
   }
   /**
    * Sets the localiza to Boys localization
    */
   public void setBoys(){
      this.Boys = true;
      this.PipekMezey = false;
   }
   /**
    * Sets the localiza to Pipek &amp; Mezey
    */
   public void setPipekMezey(){
      this.Boys = false;
      this.PipekMezey = true;
   }
   /**
    * Returns the method of localization
    * @return Boys / Pipek &amp; Mezey
    */
   public String getMethod(){
      String method = null;
      if(Boys){
         method = "Boys";
      }else{
         method = "Pipek&Mezey";
      }
      return method;
   }
   /**
    * Sets the maximum iteration (default = 20)
    * @param maxIter the maximum iteration
    */
   public void setMaxIteration(int maxIter){
      this.maxIter = maxIter;
   }
   /**
    * Returns the maximum iteration
    * @return the maximum iteration
    */
   public int getMaxIteration(){
      return maxIter;
   }
   /**
    * Sets to print the result or not (default = false)
    * @param print print the result if true.
    */
   public void setPrint(boolean print){
      this.print = print;
   }
   /**
    * Calculates the local coordinates using the input CoordinateData as 
    * initial coordinates.
    * @param coordData Vibrational coordinates
    */
   public void calc(CoordinateData coordData){
      this.coordData = coordData;
      Nat = coordData.Nat;
      Nat3 = Nat*3;
      Nfree = coordData.Nfree;
      Nrot = coordData.Nrot;
      this.calc();
   }
   /**
    * Calculates the local coordinates
    */
   private void calc(){
      
      System.out.println("--- Local mode calculations ---");
      System.out.println();
      System.out.printf("Energy window (cm-1) = %12.3f",Ethresh);
      System.out.println();
      System.out.print("Localiza = ");
      if(Boys){
         System.out.println("Boys");
      }else{
         System.out.println("Pipek&Mezey");
      }
      System.out.println();
      
      double[][] x0 = coordData.getX0();
      double[] msqrt = coordData.getMsqrt();
      double[] omegaV = coordData.getOmegaV();
      double[][] cV = coordData.getCV();
      
      double[][] Umat = new double[Nfree][Nfree];
      for(int i=0; i<Nfree; i++){
         Umat[i][i] = 1.0d;
      }
      
      double[] hmat = new double[Nfree];
      {int i=0;
      while(omegaV[i]<0){
         hmat[i] = omegaV[i]/Constants.Hartree2wvn;
         hmat[i] = -hmat[i]*hmat[i];
         i++;
      }
      while(i<Nfree){
         hmat[i] = omegaV[i]/Constants.Hartree2wvn;
         hmat[i] = hmat[i]*hmat[i];
         i++;
      }}

      Localiza lza=null;
      if(this.PipekMezey){
         lza = new LocalizaPM();
      }else if(this.Boys){
         lza = new LocalizaBoys();
      }
      lza.setup(coordData);
      
      int[] modes = new int[Nfree];
      for(int i=0; i<modes.length; i++){
         modes[i] = i;
      }
      
      double zeta = lza.getZeta(modes);
      System.out.printf("Initial zeta = %12.4f \n\n",zeta);
      
      System.out.println("Iter            zeta        dz(ave)        dz(max)");
      double dzmax, dzave;
      int count;
      boolean conv = false;
      for(int n=0; n<maxIter; n++){
         System.out.printf("%4d ",n);
         dzmax=0.0d;
         dzave=0.0d;
         count=0;
         // i>j
         for(int mi=1; mi<modes.length; mi++){
            int i = modes[mi];
            for(int mj=0; mj<mi; mj++){
               int j = modes[mj];
               if((omegaV[i]-omegaV[j])>Ethresh) continue;
               //if((omegaV[i]-omegaV[j])/omegaV[j]>0.1) continue;
               
               double Aij = lza.getK(i,j,i,j) - 0.25d*(lza.getK(i,i,i,i) + lza.getK(j,j,j,j) - 2.0d*lza.getK(i,i,j,j));
               double Bij = lza.getK(i,i,i,j) - lza.getK(j,j,i,j);
               double rr = Math.sqrt(Aij*Aij + Bij*Bij);
               
               double dzeta = Aij + rr;
               dzave = dzave + dzeta;
               count++;
               if(dzeta > Zthresh*0.01d){
                  if(dzmax < dzeta){
                     dzmax = dzeta;
                  }
                  double thet = Math.acos(-Aij/rr);
                  if(Bij<0.0d){
                     thet = -thet;
                  }
                  thet = thet*0.25d;
                  zeta = zeta + dzeta;
                  this.update(i,j,thet,cV,omegaV,hmat,Umat);
                  lza.update(i,j);
                  //System.out.printf("%4d %4d   %12.6f  %12.4f \n",i,j,thet,zeta);
                  
               }
            }
         }
         dzave = dzave / count;
         System.out.printf("   %12.4f   %12.4f   %12.4f \n", zeta,dzave,dzmax);
         if(dzmax < Zthresh){
            conv = true;
            break;
         }
      }
      System.out.println();
      
      if(! conv){
         System.out.println("The localization step is unconverged!");
         System.out.println();
         return;
      }
      zeta = lza.getZeta(modes);
      System.out.printf("Final zeta = %12.4f \n",zeta);
      
      this.calcPi(x0,msqrt,cV);
      if(print) coordData.print();
      System.out.println("--- End of Local mode calculations ---");
      System.out.println();


   }

   // i>j
   private void update(int i, int j, double thet, double[][] cV, double[] omegaV, double[] hmat, double[][] Umat){
      double costh = Math.cos(thet);
      double sinth = Math.sin(thet);
      
      double[] li = new double[Nat3];
      double[] lj = new double[Nat3];
      
      for(int s=0; s<Nat3; s++){
         li[s] = cV[i][s]*costh + cV[j][s]*sinth;
         lj[s] =-cV[i][s]*sinth + cV[j][s]*costh;
      }
   
      double[] ui = new double[Nfree];
      double[] uj = new double[Nfree];
      for(int k=0; k<Nfree; k++){
         ui[k] = Umat[i][k]*costh + Umat[j][k]*sinth;
         uj[k] =-Umat[i][k]*sinth + Umat[j][k]*costh;
      }

      double omgi = 0.0d;
      double omgj = 0.0d;
      for(int k=0; k<Nfree; k++){
         omgi = omgi + ui[k]*ui[k]*hmat[k];
         omgj = omgj + uj[k]*uj[k]*hmat[k];
      }
      if(omgi>0.0d){
         omgi = Math.sqrt(omgi)*Constants.Hartree2wvn;         
      }else{
         omgi = -Math.sqrt(-omgi)*Constants.Hartree2wvn;
      }
      if(omgj>0.0d){
         omgj = Math.sqrt(omgj)*Constants.Hartree2wvn;         
      }else{
         omgj = -Math.sqrt(-omgj)*Constants.Hartree2wvn;
      }

      if(omgi > omgj){
         cV[i] = li;
         cV[j] = lj;
         Umat[i] = ui;
         Umat[j] = uj;
         omegaV[i] = omgi;
         omegaV[j] = omgj;
         
      }else{
         cV[i] = lj;
         cV[j] = li;
         Umat[i] = uj;
         Umat[j] = ui;
         omegaV[i] = omgj;
         omegaV[j] = omgi;
         
      }

   }
 
   private void calcPi(double[][] x0, double[] msqrt, double[][] cV){
      
      double[][] Pix = new double[Nfree][3];

      double[][] wia = new double[Nfree][Nat];
      for(int i=0; i<Nfree; i++){
         for(int a=0; a<Nat; a++){
            wia[i][a]=0;
            for(int x=0; x<3; x++){
               wia[i][a] = wia[i][a] + cV[i][a*3+x]*cV[i][a*3+x];
            }
         }
      }
      
      /*
      System.out.println("Pi(1)");
      for(int i=0; i<Nfree; i++){
         for(int x=0; x<3; x++){
            Pix[i][x]=0.0d;
            for(int a=0; a<Nat; a++){
               Pix[i][x] = Pix[i][x] + wia[i][a]*x0[a*3+x];
            }
         }
         System.out.printf("%12.4f %12.4f %12.4f \n",Pix[i][0],Pix[i][1],Pix[i][2]);
      }
      System.out.println();
      */

      double[] z0 = new double[Nat3];
      int ij=0;
      for(int i=0; i<Nat; i++){
         for(int j=0; j<3; j++){
            z0[ij] = x0[i][j]*msqrt[i]*Math.sqrt(Constants.Emu2Amu);
            ij++;
         }
      }

      System.out.println("Pi(2)");
      for(int i=0; i<Nfree; i++){
         for(int x=0; x<3; x++){
            Pix[i][x]=0.0d;
            for(int a=0; a<Nat; a++){
               Pix[i][x] = Pix[i][x] + wia[i][a]*z0[a*3+x];
            }
         }
         System.out.printf("%5d  %12.4f %12.4f %12.4f \n",(i+1),Pix[i][0],Pix[i][1],Pix[i][2]);
      }
      System.out.println();

   }

}
