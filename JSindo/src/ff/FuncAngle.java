package ff;

/**
 * Harmonic energy function for the angle
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class FuncAngle {
   
   private String[] atom;
   private double Ktheta;
   private double Theta0;
   
   /**
    * Returns the atom types
    * @return atom types
    */
   public String[] getAtomType() {
      return atom;
   }

   /**
    * Sets the atom types 
    * @param atom String [3] atom types 
    */
   public void setAtomType(String[] atom) {
      this.atom = atom;
   }
   
   /**
    * Returns the force constant
    * @return Ktheta in Hartree/degree^2
    */
   public double getKtheta() {
      return Ktheta;
   }
   
   /**
    * Sets the force constant
    * @param ktheta the force constant in Hartree/degree^2
    */
   public void setKtheta(double ktheta) {
      Ktheta = ktheta;
   }
   
   /**
    * Returns the equilibrium angle
    * @return the equilibrium angle in degree
    */
   public double getTheta0() {
      return Theta0;
   }
   
   /**
    * Sets the equilibrium angle
    * @param theta0 the equilibrium angle in degree
    */
   public void setTheta0(double theta0) {
      Theta0 = theta0;
   }
   
   /**
    * Returns the energy in Hartree
    * @param theta the current angle in degree
    * @return the energy in Hartree
    */
   public double getEnergy(double theta){
      return Ktheta*(theta - Theta0)*(theta - Theta0);
   }
   
}
