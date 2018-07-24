package molecule;

import atom.Atom;
import sys.PeriodicTable;
import md.*;

/**
 * An interface between the md and molecule packages
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 *
 */
public class MDToMol extends MolUtil {

   /**
    * Converts a system for MD to Molecule. Note that Molecule contains Atom[], not 
    * AtomMD[], so the information of the atoms for SystemMD are lost at this point.
    * @param sys SystemMD to be converted to molecule
    */
   public void appendSystemMD(SystemMD sys){
      
      molecule = new Molecule();
      for(int i=0; i<sys.getNumOfSegment(); i++){
         Segment segi = sys.getSegment(i);
         for(int j=0; j<segi.getNumOfResidue(); j++){
            Residue resj = segi.getResidue(j);
            for(int k=0; k<resj.getNumOfAtom(); k++){
               AtomMD atomk = resj.getAtom(k);
               molecule.addAtom(atomk);
            }
         }
      }
   }
   
   /**
    * Renames the label of atoms to a simple name from the periodic table.
    */
   public void renameAtoms(){
      for(int n=0; n<molecule.getNat(); n++){
         Atom atom = molecule.getAtom(n);
         String label = PeriodicTable.label[atom.getAtomicNum()];
         atom.setLabel(label);
      }
   }
}
