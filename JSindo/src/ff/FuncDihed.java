package ff;

import java.util.*;

/**
 * Harmonic energy function for the bond
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class FuncDihed {

   private String[] atom;
   private ArrayList<FuncDihedChild> child;

   public FuncDihed(){
      child = new ArrayList<FuncDihedChild>();
   }
   
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
    * Sets the Dihedral function
    * @param childFunc Child function for dihedral
    */
   public void setChildFunction(FuncDihedChild childFunc){
      child.add(childFunc);
   }
   /**
    * Returns the dihedral functions
    * @return An array of dihedral functions
    */
   public FuncDihedChild[] getChildFunction(){
      return child.toArray(new FuncDihedChild[0]);
   }
   
   /**
    * Returns the energy in Hartree
    * @param chi the current dihedral angle in degree
    * @return the energy in Hartree
    */
   public double getEnergy(double chi){
      /*
      double aa = (nn*chi - delta)/180.0*Math.PI;
      return Kchi*(1.0 + Math.cos(aa));
      */
      double energy = 0.0d;
      for(int i=0; i<child.size(); i++){
         energy = energy + child.get(i).getEnergy(chi);
      }
      return energy;
   }
   
}
