package molecule;

import java.util.ArrayList;
import atom.*;

/**
 * Provides driver routines for Molecule.
 * @author Kiyoshi Yagi
 * @version 1.4
 * @since Sindo 3.0
 */
public class MolUtil {
   
   protected Molecule molecule;
   
   /**
    * Constructs a MolUtil object.
    */
   public MolUtil(){
      molecule = null;
   }
   /**
    * Constructs a MolUtil object with a given Molecule
    * @param molecule the input Molecule object
    */
   public MolUtil(Molecule molecule){
      this.molecule = molecule;
   }
   /**
    * Appends an object of Molecule
    * @param molecule Molecule
    */
   public void appendMolecule(Molecule molecule){
      this.molecule = molecule;
   }
   /**
    * Returns the Molecule
    * @return Molecule
    */
   public Molecule getMolecule(){
      return molecule;
   }
   /**
    * Returns the bond length (bohr)
    * @param i i-th atom
    * @param j j-th atom
    * @return The bond length
   */
   public double getBondLength(int i, int j){
      ArrayList<Atom> atomList = molecule.getAtomList();
      AtomUtil autil = new AtomUtil();
      return autil.getBondLength(atomList.get(i-1), atomList.get(j-1));
   }
   /**
    * Returns the bond angle of i-j-k  (degree)
    * @param i i-th atom
    * @param j j-th atom
    * @param k k-th atom
    * @return The bond angle
    */
   public double getBondAngle(int i, int j, int k){
      ArrayList<Atom> atomList = molecule.getAtomList();
      AtomUtil autil = new AtomUtil();
      return autil.getBondAngle(atomList.get(i-1), atomList.get(j-1), atomList.get(k-1));
   }
   /**
    * Returns the dihedral angle between i-j-k and j-k-l
    * @param i i-th atom
    * @param j j-th atom
    * @param k k-th atom
    * @param l l-th atom
    * @return The dihedral angle
    */
   public double getDihedralAngle(int i, int j, int k, int l){
      ArrayList<Atom> atomList = molecule.getAtomList();
      AtomUtil autil = new AtomUtil();
      return autil.getDihedralAngle(atomList.get(i-1), atomList.get(j-1), atomList.get(k-1), atomList.get(l-1));
   }
   /**
    * Returns the center of mass of the molecule (bohr).
    * @return Center of mass coordinates 
   public double[] getCenterOfMass(){
      Atom[] atomList = molecule.getAtomList().toArray(new Atom[0]);
      AtomUtil autil = new AtomUtil();
      return autil.getCenterOfMass(atomList).getXYZCoordinates();
   }
    */
   public double[] getCenterOfMass(){
      return this.getCenterOfMass(false);
   }
   /**
    * Returns the center of mass of the molecule (bohr).
    * @return Center of mass coordinates 
    */
   public double[] getCenterOfMass(boolean withSubatoms){

      Atom[] atomList = null;
      if (withSubatoms) {
         atomList = new Atom[molecule.getNat()+molecule.getNat_subatom()];
         for(int n=0; n<molecule.getNat(); n++) {
            atomList[n] = molecule.getAtom(n);
         }
         for(int n=0; n<molecule.getNat_subatom(); n++) {
            atomList[molecule.getNat()+n] = molecule.getSubAtom(n);
         }
      }else {
         atomList = molecule.getAtomList().toArray(new Atom[0]);
      }
      AtomUtil autil = new AtomUtil();
      return autil.getCenterOfMass(atomList).getXYZCoordinates();
   }
   /**
    * Returns a copy of Molecule containing a list of atoms. Note that other data are not copied. 
    * @return Molecule
    */
   public Molecule copyAtoms(){
      ArrayList<Atom> atomList = molecule.getAtomList();
      
      Molecule newMol = new Molecule();
      ArrayList<Atom> newAtoms = newMol.getAtomList();
      
      for(int i=0; i<molecule.getNat(); i++){
         //newMol.addAtom(molecule.getAtom(i).clone());
         newAtoms.add(atomList.get(i).clone());
      }
      return newMol;
   }

   /**
    * Returns a deep copy of Molecule
    * @param atoms contains a list of atoms if true
    * @param edata contains electronic data if true 
    * @param vdata contains vibrational data if true
    * @param vibdomain domain index (0,1,..,N-1). All domains are copied when null.
    * @return A copy of Molecule
    */
   public Molecule copyMolecule(boolean atoms, boolean edata, boolean vdata, int[] vibdomain){
      
      Molecule new_molecule = null;
      if(atoms){
         new_molecule = this.copyAtoms();
      }else{
         new_molecule = new Molecule();
      }
      
      if(edata){
         new_molecule.setElectronicData(molecule.getElectronicData().clone());
      }
      
      if(vdata){
         if(vibdomain == null){
            for(int n=0; n<molecule.getNumOfVibrationalData(); n++){
               new_molecule.addVibrationalData(molecule.getVibrationalData(n).clone());
            }
         }else{
            for(int n=0; n<vibdomain.length; n++){
               new_molecule.addVibrationalData(molecule.getVibrationalData(vibdomain[n]).clone());               
            }
         }
      }
      
      return new_molecule;
      
   }
}
