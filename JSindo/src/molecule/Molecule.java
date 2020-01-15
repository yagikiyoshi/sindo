package molecule;

import java.util.*;

import atom.*;
import sys.Utilities;

/**
 * Stores the data of molecule. <br> 
 * This class has list of atoms, ElectronicData, and VibrationalData. The data is accessed by, <br>
 * <blockquote><pre>
 * Molecule molecule = new Molecule();
 * Atom atom = molecule.getAtom(n);
 * ElectronicData edata = molecule.getElectronicData();
 * VibrationalData vdata = molecule.getVibrationalData(n);
 * </pre></blockquote>
 * 
 * @author Kiyoshi Yagi
 * @version 1.5
 * @since Sindo 3.0
 * @see Atom ElectronicData VibrationalData
 */
public class Molecule {
   
   private String title;
   protected ArrayList<Atom> atomList;
   protected ArrayList<Atom> subatomList;
   private ElectronicData edata;
   private ArrayList<VibrationalData> vdata;

   /**
    * Constructs a Molecule object.
    */
   public Molecule(){
      atomList    = new ArrayList<Atom>();
      subatomList = new ArrayList<Atom>();
      vdata       = new ArrayList<VibrationalData>();
   }

   /**
    * Returns the number of atoms in this molecule
    * @return the number of atoms
    */
   public int getNat(){
      return atomList.size();
   }
   
   /**
    * Returns ElectronicData.
    * @return the data of electronic structure calculations
    */
   public ElectronicData getElectronicData(){
      return edata;
   }
   /**
    * Sets ElectronicData
    * @param edata the data of electronic structure calculations
    */
   public void setElectronicData(ElectronicData edata){
      this.edata = edata;
   }
   /**
    * Returns the number of vibrational data (= domain) in the molecule
    * @return the number of vibrational data
    */
   public int getNumOfVibrationalData(){
      return vdata.size();
   }

   /**
    * Returns VibrationalData of the 1st domain
    * @return the data of vibrational modes
    */   
   public VibrationalData getVibrationalData(){

      /*
      if(vdata.size() == 0){
         return null;
      }
      */
      return this.getVibrationalData(0);
   }
   /**
    * Returns VibrationalData of the n-th domain
    * @param n the index of list
    * @return the data of vibrational modes
    */
   public VibrationalData getVibrationalData(int n){
      return vdata.get(n);         
   }
   /**
    * Sets VibrationalData to the first position of a list
    * @param vibrationaldata the data of vibrational modes
    */
   public void setVibrationalData(VibrationalData vibrationaldata){
      if(vdata.size() == 0){
         vdata.add(vibrationaldata);
      }else{         
         vdata.set(0,vibrationaldata);
      }
   }
   /**
    * Sets(replaces) Vibrational data to a specified position of a list
    * @param the index of data which is replaced
    * @param vibrationaldata new vibrational data at this position
    */
   public void setVibrationalData(int index, VibrationalData vibrationaldata) {
      vdata.set(index, vibrationaldata);
   }
   /**
    * Adds VibrationalData to a list
    * @param vibrationaldata the data of vibrational modes
    */
   public void addVibrationalData(VibrationalData vibrationaldata){
      vdata.add(vibrationaldata);
   }
   /**
    * Removes the n-th data from the list
    * @param n the index of the list
    */
   public void removeVibrationalData(int n){
      vdata.remove(n);
   }
   /**
    * Remove the whole list of VibrationalData
    */
   public void clearVibrationalData(){
      vdata.clear();;
   }
   /**
    * Returns the ArrayList of Atom
    * @return all the atoms
    */
   public ArrayList<Atom> getAtomList(){
      return atomList;
   }
   /**
    * Returns the ArrayList of subAtom
    * @return all the subatoms
    */
   public ArrayList<Atom> getSubAtomList(){
      return subatomList;
   }
   /**
    * Returns an atom at a specified index
    * @param i the index (0 to Natom-1)
    * @return the specified atom
    */
   public Atom getAtom(int i){
      return atomList.get(i);
   }
   /**
    * Adds an atom to the list
    * @param atom the atom to be added
    */
   public void addAtom(Atom atom){
      atomList.add(atom);
   }
   /**
    * Deletes and resets the list of atoms.
    */
   /*public void deleteAllAtoms(){
      atoms.clear();
   }*/
   /**
    * Returns the Atom in the i-th element.
    * @param i the element
    * @return the Atom
    */
   /*public Atom getAtom(int i){
      return atoms.get(i);
   }*/
   /**
    * Add an atom to the list.
    * @param atom the atom
    */
   /*public void addAtom(Atom atom){
      Nat++;
      atoms.add(atom);
      if(Nat > 2 && linear){
         double[] vec2 = new double[3];
         for(int i=0; i<3; i++){
            vec2[i] = atoms.get(0).getXYZCoordinates()[i] - atoms.get(Nat-1).getXYZCoordinates()[i];
         }
         Utilities.normalize(vec2);
         if(Math.abs(Utilities.dotProduct(vec1, vec2))<0.9998d){
            linear = false;
         }
   
      }else if(Nat == 2){
         vec1 = new double[3];
         for(int i=0; i<3; i++){
            vec1[i] = atoms.get(0).getXYZCoordinates()[i] - atoms.get(1).getXYZCoordinates()[i];
         }
         Utilities.normalize(vec1);
         
      }
   }*/
   /**
    * Set (Replace) the i-th atom (i=0-Nat-1) by the new one
    * @param iatom the i-th atom (i=0-Nat-1)
    * @param atom the new atom
    */
   /*public void setAtom(int iatom, Atom atom){
      if(iatom < Nat){
         atoms.set(iatom, atom);
      }else{
         this.addAtom(atom);
      }
   }*/
   /**
    * Provides the deep copy of xyz coordinates (bohr).
    * @return XYZ coordinates ([Nat*3])
    */
   public double[] getXYZCoordinates1(){
      int Nat = this.atomList.size();
      double[] xyz = new double[Nat*3];
      for(int i=0; i<Nat; i++){
         double[] xyz_i = atomList.get(i).getXYZCoordinates();
         for(int j=0; j<3; j++){
            xyz[i*3+j] = xyz_i[j];
         }
      }
      return xyz;
   }
   /**
    * Provides the shallow copy of xyz coordinates (bohr).
    * @return XYZ coordinates ([Nat][3])
    */
   public double[][] getXYZCoordinates2(){
      int Nat = this.atomList.size();
      double[][] xyz = new double[Nat][];
      for(int i=0; i<Nat; i++){
         xyz[i] = atomList.get(i).getXYZCoordinates();
      }
      return xyz;
   }
   /**
    * Provides the deep copy of mass of each atom (emu).
    * @return mass ([Nat])
    */
   public double[] getMass(){
      int Nat = atomList.size();
      double[] mass = new double[Nat];
      for(int i=0; i<Nat; i++){
         mass[i] = atomList.get(i).getMass();
      }
      return mass;      
   }

   /**
    * Returns the number of subatoms in this molecule
    * @return the number of subatoms
    */
   public int getNat_subatom(){
      return subatomList.size();
   }
   /**
    * Provides the deep copy of xyz coordinates of subatoms (bohr).
    * @return XYZ coordinates ([Nat*3])
    */
   public double[] getXYZCoordinates1_subatom(){
      int Nat = this.subatomList.size();
      double[] xyz = new double[Nat*3];
      for(int i=0; i<Nat; i++){
         double[] xyz_i = subatomList.get(i).getXYZCoordinates();
         for(int j=0; j<3; j++){
            xyz[i*3+j] = xyz_i[j];
         }
      }
      return xyz;
   }
   /**
    * Provides the shallow copy of xyz coordinates of subatoms (bohr).
    * @return XYZ coordinates ([Nat][3])
    */
   public double[][] getXYZCoordinates2_subatom(){
      int Nat = this.subatomList.size();
      double[][] xyz = new double[Nat][];
      for(int i=0; i<Nat; i++){
         xyz[i] = subatomList.get(i).getXYZCoordinates();
      }
      return xyz;
   }
   /**
    * Returns a sub atom at a specified index
    * @param i the index (0 to Natom-1)
    * @return the specified atom
    */
   public Atom getSubAtom(int i){
      return subatomList.get(i);
   }

   /**
    * Returns true if the molecule is linear.
    * @return linear 
    */
   public boolean isLinear(){
      int Nat = atomList.size();
      if(Nat > 2){
         double[] vec1 = new double[3];
         double[] vec2 = new double[3];
         
         for(int i=0; i<3; i++){
            vec1[i] = atomList.get(0).getXYZCoordinates()[i] - atomList.get(1).getXYZCoordinates()[i];
         }
         Utilities.normalize(vec1);
         
         for(int n=2; n<Nat; n++){
            for(int i=0; i<3; i++){
               vec2[i] = atomList.get(0).getXYZCoordinates()[i] - atomList.get(n).getXYZCoordinates()[i];
            }
            Utilities.normalize(vec2);
            if(Math.abs(Utilities.dotProduct(vec1, vec2))<0.9998d){
               return false;
            }            
         }
      }
      return true;
   }

   /**
    * Returns the title of this molecule
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * Sets the title of this molecule
    * @param title the title
    */
   public void setTitle(String title) {
      this.title = title;
   }

}
