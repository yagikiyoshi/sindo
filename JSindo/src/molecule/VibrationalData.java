package molecule;

import sys.Utilities;

/**
 * (Package private) Stores the data of the vibrational calculation. The instance is generated 
 * by Molecule class. Nfree/Nrot are zero, and other variables are null if the data is not set. 
 * The setter and getter in this class is a shallow copy.  
 * This class includes the following data: <br>
 * <ol>
 * <li> Number of vibrational and rotational degrees of freedom </li>
 * <li> Translation/Rotation/Vibrational displacement vectors </li>
 * <li> Harmonic frequencies (cm-1) </li>
 * </ol>
 * 
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 * @see Molecule
 */
public class VibrationalData implements Cloneable {
   
   /**
    * The number of vibrational degrees of freedom
    */
   public int Nfree = 0;
   /**
    * The number of rotational degrees of freedom
    */
   public int Nrot = 0;
   private double[][] CT = null;
   private double[][] CR = null;
   private double[][] CV = null;
   private double[] omegaV  = null;
   private double[] omegaR  = null; 
   private double[] omegaT  = null;
   private String coordType = null;
   private int[]  atomIndex = null;
   /**
    * Constructor is package private.
    */
   VibrationalData(){
      
   }
   /**
    * Returns the frequency of translation, which should be zero for an isolated molecule (cm-1).
    * @return the frequency [3]
    */
   public double[] getOmegaT() {
      return omegaT;
   }

   /**
    * Returns the frequency of rotation, which should be close to zero for an isolated molecule (cm-1). 
    * @return the frequency [Nrot]
    */
   public double[] getOmegaR() {
      return omegaR;
   }
   /**
    * Returns harmonic frequencies (cm-1)
    * @return Harmonic frequencies [Nfree]
    */
   public double[] getOmegaV() {
      return omegaV;
   }
   /**
    * Returns translational vectors
    * @return CT[3][Nat*3]
    */
   public double[][] getTransVector() {
      return CT;
   }
   /**
    * Returns rotational vectors
    * @return CR[Nrot][Nat*3]
    */
   public double[][] getRotVector() {
      return CR;
   }
   /**
    * Returns vibrational vectors
    * @return CV[Nfree][Nat*3]
    */
   public double[][] getVibVector() {
      return CV;
   }
   /**
    * Returns the type of vibrational coordinates
    * @return description
    */
   public String getCoordType(){
      return coordType;
   }
   /**
    * Returns the index of atoms for this domain
    * @return atomIndex[Nat] or null
    */
   public int[] getAtomIndex(){
      return atomIndex;
   }
   /**
    * Sets the frequency of translation, which should be zero for an isolated molecule (cm-1).
    * @param omegaT the frequency [3]
    */
   void setOmegaT(double[] omegaT) {
      this.omegaT = omegaT;
   }

   /** 
    * Sets the frequency of rotation, which should be close to zero for an isolated molecule (cm-1).
    * @param omegaR the frequency [Nrot]
    */
   void setOmegaR(double[] omegaR) {
      this.omegaR = omegaR;
      this.Nrot = omegaR.length;
   }
   /**
    * Sets harmonic frequencies (cm-1)
    * @param omega Harmonic frequencies [Nfree]
    */
   void setOmegaV(double[] omega) {
      this.omegaV = omega;
      this.Nfree = omega.length;
   }
   /**
    * Sets translational vectors
    * @param cT Translational vector [3][Nat*3]
    */
   void setTransVector(double[][] cT) {
      this.CT = cT;
   }
   /**
    * Sets rotational vectors
    * @param cR Rotational vector [Nrot][Nat*3]
    */
   void setRotVector(double[][] cR) {
      this.CR = cR;
      this.Nrot = cR.length;
   }
   /**
    * Sets vibrational vectors
    * @param cV Vibrational vector [Nfree][Nat*3]
    */
   void setVibVector(double[][] cV) {
      this.CV = cV;
      this.Nfree = cV.length;
   }
   /**
    * Sets the type of coordinates
    * @param coordType the type of coordinates
    */
   void setCoordType(String coordType){
      this.coordType = coordType;
   }
   /**
    * Sets the index of atoms
    * @param atomIndex index of atoms
    */
   void setAtomIndex(int[] atomIndex){
      this.atomIndex = atomIndex;
   }

   public VibrationalData clone(){
      VibrationalData vdata = null;
      try{
         vdata = (VibrationalData)super.clone();
      }catch(CloneNotSupportedException e){
         e.printStackTrace();
         return null;
      }
      if(CT != null){
         vdata.setTransVector(Utilities.deepCopy(CT));
      }
      if(CR != null){
         vdata.setRotVector(Utilities.deepCopy(CR));
      }
      if(CV != null){
         vdata.setVibVector(Utilities.deepCopy(CV));
      }
      
      if(omegaT != null){
         vdata.setOmegaV(Utilities.deepCopy(omegaT));
      }
      if(omegaR != null){
         vdata.setOmegaV(Utilities.deepCopy(omegaR));
      }
      if(omegaV != null){
         vdata.setOmegaV(Utilities.deepCopy(omegaV));
      }
            
      if(atomIndex != null){
         vdata.setAtomIndex(Utilities.deepCopy(atomIndex));
      }
      
      return vdata;
      
   }
}
