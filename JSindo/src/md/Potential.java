package md;

import java.util.HashMap;

/**
 * Potential energy function of the system
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public interface Potential {

   /**
    * Returns the energy of the system for a given coordinates
    * @param atoms [Natom] the atoms of the system 
    * @return the energy (Hartree)
    */
   public double getEnergy(AtomMD[] atoms);
   
   /**
    * Returns the energy by component
    * @return the energies with the name of component set to the keys
    */
   public HashMap<String,Double> getEnergyComponent();
   
   /**
    * Returns the gradient of the system for a given coordinates
    * @param atoms [Natom] the atoms of the system 
    * @return the gradient [Natom][3] (Hartree/bohr)
    */
   public double[][] getGradient(AtomMD[] atoms);

   /**
    * Sets the QM atoms to the Potential and remove unnecessary ff terms.
    * @param qmatoms The index of QM atoms
    */
   public void setQMatoms(int[] qmatoms);

}
