package ff;

/**
 * Non-bonded van der Waals parameters
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class VdW {

   private String atom;
   private double eps;
   private double rmin2;
   private double eps_14;
   private double rmin2_14;
   
   /**
    * Returns the atom type
    * @return the atom type
    */
   public String getAtomType(){
      return atom;
   }
   
   /**
    * Sets the atom type
    * @param atom the atom type
    */
   public void setAtomType(String atom){
      this.atom = atom;
   }
   
   /**
    * Returns eps_i, where eps,i,j = sqrt(eps,i * eps,j)
    * @return eps_i in Hartree
    */
   public double getEpsilon(){
      return eps;
   }
   
   /**
    * Sets eps_i, where eps,i,j = sqrt(eps,i * eps,j)
    * @param eps in Hartree
    */
   public void setEpsilon(double eps){
      this.eps = eps;
   }
   
   /**
    * Returns Rmin/2,i, where Rmin,i,j = Rmin/2,i + Rmin/2,j
    * @return Rmin/2,i in Bohr
    */
   public double getRmin(){
      return rmin2;
   }
   
   /**
    * Sets Rmin/2,i where Rmin,i,j = Rmin/2,i + Rmin/2,j
    * @param rmin2 Rmin/2,i in Bohr
    */
   public void setRmin(double rmin2){
      this.rmin2 = rmin2;
   }
   
   /**
    * Returns eps_i for 1-4 interaction
    * @return eps_i in Hartree
    */
   public double getEpsilon_14(){
      return eps_14;
   }
   
   /**
    * Sets eps_i for 1-4 interaction
    * @param eps_14 eps_i in Hartree
    */
   public void setEpsilon_14(double eps_14){
      this.eps_14 = eps_14;
   }
   
   /**
    * Returns Rmin/2,i for 1-4 interaction
    * @return Rmin/2,i in Bohr
    */
   public double getRmin_14(){
      return rmin2_14;
   }
   
   /**
    * Sets Rmin/2,i for 1-4 interaction
    * @param rmin_14 Rmin/2,i in Bohr
    */
   public void setRmin_14(double rmin_14){
      this.rmin2_14 = rmin_14;
   }
}
