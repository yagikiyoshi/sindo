package ff;

/**
 * Harmonic energy function for the bond
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class FuncBond {

   private String[] atom;
   private double Kb;
   private double b0;
   
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
    * @return Kb in Hartree/bohr^2
    */
   public double getKb() {
      return Kb;
   }
   
   /**
    * Sets the force constant
    * @param kb the force constant in Hartree/bohr^2
    */
   public void setKb(double kb) {
      this.Kb = kb;
   }
   
   /**
    * Returns the equilibrium distance
    * @return the equilibrium distance in bohr
    */
   public double getB0() {
      return b0;
   }
   
   /**
    * Sets the equilibrium distance
    * @param b0 the equilibrium distance in bohr
    */
   public void setB0(double b0) {
      this.b0 = b0;
   }
   
   /**
    * Returns the energy in Hartree
    * @param b the current distance in bohr
    * @return the energy in Hartree
    */
   public double getEnergy(double b){
      return Kb*(b-b0)*(b-b0);
   }
   
}
