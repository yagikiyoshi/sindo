package vibration;

import sys.Utilities;
import sys.Constants;

/**
 * Calculates the properties of vibrational coordinates.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */

public class CoordProperty {


   private CoordinateData coordinate;
   private double[] rdmass;
   private double[][] cOfvib;
   private double[] dr;

   /**
    * Constructor
    */
   public CoordProperty(){
   }
   /**
    * Construct the class with the specified coordinate
    * @param coordinate the vibrational coordinate
    */
   public CoordProperty(CoordinateData coordinate){
      this.appendCoordinate(coordinate);
   }
   /**
    * Append the vibrational coordinate
    * @param coordinate the vibrational coordinate
    */
   public void appendCoordinate(CoordinateData coordinate){
      this.coordinate = coordinate;
      rdmass = null;
      cOfvib = null;
      dr = null;
   }
   /**
    * Calculate the property of given coordinate, i.e., reduced mass, center of vibration, and the variant.
    */
   private void calcProperties(){
      
      if(coordinate == null){
         System.out.println("Error while calculating the properties of vibrational coordinates.");
         System.out.println("CoordiateData is empty!");
         return;
      }
      int Nat = coordinate.Nat;
      int Nfree = coordinate.Nfree;
      double[][] CL = coordinate.getCV();
      double[][] xyz = coordinate.getX0();
      double[] mass = Utilities.deepCopy(coordinate.getMsqrt());
      for(int i=0; i<mass.length; i++){
         mass[i] = mass[i]*mass[i];
      }
            
      rdmass = new double[Nfree];
      cOfvib = new double[Nfree][3];
      dr = new double[Nfree];
      
      for(int i=0; i<Nfree; i++){
         double[] pn = new double[Nat];
         
         double aa=0.0d;
         for(int n=0; n<Nat; n++){
            for(int j=0; j<3; j++){
               pn[n] = pn[n] + CL[i][n*3+j]*CL[i][n*3+j];
            }
            aa=aa + pn[n]/mass[n];
         }
         rdmass[i] = 1.0/aa;
         
         for(int n=0; n<Nat; n++){
            pn[n] = pn[n]/mass[n]*rdmass[i];
         }
         
         for(int n=0; n<Nat; n++){
            for(int j=0; j<3; j++){
               cOfvib[i][j] = cOfvib[i][j] + xyz[n][j]*pn[n];
            }
         }

         aa = 0.0;
         for(int n=0; n<Nat; n++){
            double rr = 0.0;
            for(int j=0; j<3; j++){
               rr = rr + (xyz[n][j]-cOfvib[i][j])*(xyz[n][j]-cOfvib[i][j]);
            }
            aa = aa + rr*pn[n];
         }
         dr[i] = Math.sqrt(aa);
         
      }
   }
   
   /**
    * Returns a reduced mass associated with the vibrational coordinates. The reduced mass is defined as, <br>
    * 1/mu_i = Sum_{a=1}^N 1/ma Sum_{xx=x,y,z} L_{axx,i}^2 
    * @return The reduced mass for all modes in emu.
    */
   public double[] getReducedMass(){
      if(rdmass == null) this.calcProperties();
      return rdmass;
   }
   /**
    * Returns a center of vibrational mode defined as, <br>
    * r_i = Sum_{a=1}^{N} p_a^{(i)} r_a, <br>
    * p_a^{(i)} = mu_i/ma Sum_{xx=x,y,z} L_{axx,i}^2
    * @return The center of vibration for all modes in bohr
    */
   public double[][] getCenterOfVibration(){
      if(cOfvib == null) this.calcProperties();
      return cOfvib;
   }
   /**
    * Returns a variant of vibrational modes defined as, <br>
    * dr = sqrt(Sum_{a=1}^N p_a^(i) |r_a - r_i|^2), <br>
    * p_a^{(i)} = mu_i/ma Sum_{xx=x,y,z} L_{axx,i}^2
    * @return The variant of vibration for all modes in bohr
    */
   public double[] getVariantOfVibration(){
      if(dr == null) this.calcProperties();
      return dr;
   }
   /**
    * Returns the infrared intensity
    * @param dipoleDerv double[Nat3][xyz] dipole derivatives in mass-weight Cartesian (atomic unit)
    * @return IR intensity (km/mol)
    */
   public double[] getInfrared(double[][] dipoleDerv){
      
      int Nat3  = coordinate.Nat*3;
      int Nfree = coordinate.Nfree;
      double[] infrared = new double[Nfree];
      
      double[][] cv = coordinate.getCV();
      for(int i=0; i<Nfree; i++){
         double[] myu= new double[3];
         for(int j=0; j<Nat3; j++){
            for(int xx=0; xx<3; xx++){
               myu[xx] = myu[xx] + cv[i][j]*dipoleDerv[j][xx];
            }
         }
         
         // intensity in atomic unit (bohr)
         infrared[i] = (myu[0]*myu[0]+myu[1]*myu[1]+myu[2]*myu[2])*Math.PI/3.0/Constants.vlight_in_au/Constants.vlight_in_au;
         // intensity in km/mol
         infrared[i] = infrared[i]*Constants.Bohr*Constants.Avogadro/1000.0;
      }

      return infrared;
   }
   
   /**
    * Returns the raman activity
    * @param polarizabilityDerv double[Nat3][6] polarizability tensor derivatives in mass-weight
    * Cartesian coordinates (atomic unit)
    * @return Raman activity (Angs^4/amu)
    */
   public double[] getRaman(double[][] polarizabilityDerv){
      int Nat3  = coordinate.Nat*3;
      int Nfree = coordinate.Nfree;
      double[] raman = new double[Nfree];

      double[][] cv = coordinate.getCV();
      for(int i=0; i<Nfree; i++){
         double[] alpha = new double[6];
         for(int j=0; j<Nat3; j++){
            for(int xy=0; xy<6; xy++){
               alpha[xy] = alpha[xy] + cv[i][j]*polarizabilityDerv[j][xy];
            }
         }
         
         //   0  1  2  3  4  5
         // [xx,xy,yy,xz,yz,zz]
         double aa = (alpha[0]+alpha[2]+alpha[5])/3.0;
         aa = aa*aa;
         double bb = 0.5*((alpha[0]-alpha[2])*(alpha[0]-alpha[2]) 
                        + (alpha[0]-alpha[5])*(alpha[0]-alpha[5])
                        + (alpha[2]-alpha[5])*(alpha[2]-alpha[5])
                        + 6.0*(alpha[1]*alpha[1] + alpha[3]*alpha[3] + alpha[4]*alpha[4]));

         // raman activity in atomic unit (bohr^4/emu)
         raman[i] = 45.0*aa + 7.0*bb;
         // raman activity in Angs^4/amu
         raman[i] = raman[i]*Math.pow(Constants.Bohr2Angs,4.0)/Constants.Emu2Amu;
         
      }
      
      return raman;

   }
   
   /**
    * Returns Pij = |r[i]-r[j]|, where r[i] is the center of the i-th coordinate.
    * @param i the i-th coordinate
    * @param j the j-th coordinate
    * @return Pij
    */
   public double getPij(int i, int j){
//      double pij=(dr[i]+dr[j])/Utilities.getNorm(cOfvib[i], cOfvib[j]);
      double pij=Utilities.getNorm(cOfvib[i], cOfvib[j]);
      return pij;
   }
   public double getPijk(int i, int j, int k){
      double[] aa = new double[3];
      for(int n=0; n<3; n++){
         aa[n] = (cOfvib[i][n] + cOfvib[j][n] + cOfvib[k][n])/3.0d;
      }
      double pijk = Utilities.getNorm(aa, cOfvib[i]) + 
            Utilities.getNorm(aa, cOfvib[j]) + 
            Utilities.getNorm(aa, cOfvib[k]);
      return pijk;
   }
}
