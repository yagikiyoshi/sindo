package ff;

/**
 * Energy function for the dihedral angle
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class FuncDihedChild {

   private double Kchi;
   private double delta;
   private double nn;
      
   /**
    * Returns the force constant
    * @return Kchi in Hartree
    */
   public double getKchi() {
      return Kchi;
   }
   
   /**
    * Sets the force constant
    * @param kchi the force constant in Hartree
    */
   public void setKchi(double kchi) {
      this.Kchi = kchi;
   }
   
   /**
    * Returns the phase shift
    * @return the phase shift in degrees
    */
   public double getDelta() {
      return delta;
   }
   
   /**
    * Sets the phase shift
    * @param delta the phase shift in degrees
    */
   public void setDelta(double delta) {
      this.delta = delta;
   }
   
   /**
    * Returns the multiplicity
    * @return the multiplicity
    */
   public int getMultiplicity(){
      return (int)nn;
   }
   
   /**
    * Sets the multiplicity
    * @param nn the multiplicity
    */
   public void setMultiplicity(int nn){
      this.nn = (double)nn;
   }
   
   /**
    * Returns the energy in Hartree
    * @param chi the current dihedral angle in degree
    * @return the energy in Hartree
    */
   public double getEnergy(double chi){
      double aa = (nn*chi - delta)/180.0*Math.PI;
      return Kchi*(1.0 + Math.cos(aa));
   }
   
}
