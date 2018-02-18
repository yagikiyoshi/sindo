package ff;

/**
 * Harmonic energy function for the angle
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class FuncUB {
   
   private String[] atom;
   private double Kub;
   private double S0;
   
   /**
    * Returns the atom types
    * @return atom types
    */
   public String[] getAtomType() {
      return atom;
   }

   /**
    * Sets the atom types 
    * @param atom String [2] atom types 
    */
   public void setAtomType(String[] atom) {
      this.atom = atom;
   }
   
   /**
    * Returns the force constant
    * @return Kub in Hartree/bohr^2
    */
   public double getKub() {
      return Kub;
   }
   
   /**
    * Sets the force constant
    * @param kub the force constant in Hartree/bohr^2
    */
   public void setKub(double kub) {
      Kub = kub;
   }
   
   /**
    * Returns the equilibrium angle
    * @return the equilibrium angle in bohr
    */
   public double S0() {
      return S0;
   }
   
   /**
    * Sets the equilibrium angle
    * @param s0 the equilibrium angle in bohr
    */
   public void setS0(double s0) {
      S0 = s0;
   }
   
   /**
    * Returns the energy in Hartree
    * @param ss the current anglar bond in bohr
    * @return the energy in Hartree
    */
   public double getEnergy(double ss){
      return Kub*(ss - S0)*(ss - S0);
   }
   
}
