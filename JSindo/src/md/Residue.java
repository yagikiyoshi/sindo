package md;

import java.util.*;

/**
 * Stores the information of residue
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.6
 */
public class Residue implements Cloneable, AtomList{

   private String name;
   private int ID;
   private ArrayList<AtomMD> atomList;
   private AtomMD[] atoms;
   private boolean addAtom;
	
   public Residue(){
      atomList = new ArrayList<AtomMD>();
      addAtom = true;
      atoms = null;
   }

   /**
    * Returns the name of this residue
    * @return the name
    */
   public String getName() {
      return name;
   }
   /**
    * Sets the name of this residue
    * @param resName the name
    */
   public void setName(String resName) {
      name = resName;
   }
   /**
    * Returns the ID of this residue
    * @return the ID
    */
   public int getID() {
      return ID;
   }
   /**
    * Sets the ID of this residue
    * @param resID ID of this residue
    */
   public void setID(int resID) {
      ID = resID;
   }
   /**
    * (Package private) Resets the atomsList to be editable.
    */
   void openAtomList(){
      if(! this.addAtom){
         this.addAtom = true;
         atomList = new ArrayList<AtomMD>();
         for(int n=0; n<atoms.length; n++){
            atomList.add(atoms[n]);
         }
         atoms = null;
      }
   }
   /**
    * Closes the atomList and atoms are not accepted any further
    */
   public void closeAtomList() {
      if(this.addAtom){
         this.addAtom = false;
         atoms = atomList.toArray(new AtomMD[0]);
         atomList = null;
      }
   }
   /**
    * Adds an atom to the list
    * @param atom the atom to be added
    * @throws BuilderException thrown when the residue is closed
    */
   public void addAtomList(AtomMD atom) throws BuilderException{
      if(addAtom){
         atomList.add(atom);         
      }else{
         throw new BuilderException("Attempted to add an atom to an uneditable residue");
      }
   }
   /**
    * Adds an array of atoms to the list
    * @param atoms the array of atoms to be added 
    * @throws BuilderException thrown when the residue is closed
    */
   public void addAtomList(AtomMD[] atoms) throws BuilderException{
      if(addAtom){
         for(int i=0; i<atoms.length; i++){
            atomList.add(atoms[i]);            
         }
      }else{
         throw new BuilderException("Attempting to add an atom to an uneditable residue");
      }
   }
   /**
    * Inserts an atom to specified index
    * @param index The index 
    * @param atom The atom to be inserted
    * @throws BuilderException thrown when the residue is closed
    */
   public void insertAtomList(int index, AtomMD atom) throws BuilderException{
      if(addAtom){
         atomList.add(index, atom);         
      }else{
         throw new BuilderException("Attempted to insert an atom to an uneditable residue");
      }
   }
   
   /**
    * Inserts an array of atoms to specified index
    * @param index The index 
    * @param atoms The array of atoms to be inserted
    * @throws BuilderException thrown when the residue is closed
    */
   public void insertAtomList(int index, AtomMD[] atoms) throws BuilderException{
      if(addAtom){
         for(int i=0; i<atoms.length; i++){
            atomList.add(index+i, atoms[i]);            
         }
      }else{
         throw new BuilderException("Attempting to insert an atom to an uneditable residue");
      }
   }
   /**
    * Removes an atom from the list, and shifts any subsequent elements to the left 
    * (subtracts one from their indices).
    * @param index The index of atom to be removed
    * @throws BuilderException thrown when the residue is closed
    */
   public void removeAtomList(int index) throws BuilderException{
      if(addAtom){
         atomList.remove(index);
      }else{
         throw new BuilderException("Attempted to remove an atom from an uneditable residue");
      }
   }
   /**
    * Returns an atoms from the list
    * @param index The index of the list
    * @return The atom
    * @throws BuilderException thrown when the residue is closed
    */
   public AtomMD getAtomList(int index) throws BuilderException {
      if(addAtom){
         return atomList.get(index);
      }else{
         throw new BuilderException("Attempted to get an atom from an uneditable residue"); 
      }
   }
   /**
    * Returns the list of atoms
    * @return list of atoms
    * @throws BuilderException thrown when the residue is closed
    */
   public ArrayList<AtomMD> getAtomList() throws BuilderException {
      if(addAtom){
         return atomList;
      }else{
         throw new BuilderException("Attempted to get a list of atoms from an uneditable residue");         
      }
   }
   /**
    * Initialize the atom list
    */
   private void initAtomList(){
      atomList = new ArrayList<AtomMD>();
      addAtom = true;
      atoms = null;
   }
   public int getNumOfAtom(){
      return atoms.length;
   }

   public AtomMD[] getAtomALL(){
      return atoms;
   }
   
   public AtomMD getAtom(int index){
      return atoms[index];
   }
   
   public AtomMD getAtom(String name){
      AtomMD atom = null;
      for(int i=0; i<atoms.length; i++){
         if(atoms[i].getLabel().equals(name)){
            atom = atoms[i];
            break;
         }
      }
      return atom;

   }
   
   /**
    * Returns a clone of the current Residue
    */
   public Residue clone(){
      Residue residue = null;
	   try{
	      residue = (Residue)super.clone();
	      residue.initAtomList();
	      if(atomList != null){
	         for(int i=0; i<atomList.size(); i++){
	            residue.addAtomList(atomList.get(i).clone());
	         }	         
	      }else{
	         for(int i=0; i<atoms.length; i++){
	            residue.addAtomList(atoms[i].clone());
	         }
	      }
	      
	   }catch(CloneNotSupportedException e){
	      e.printStackTrace();
	   }catch(BuilderException e) {
         e.printStackTrace();
      }
	   return residue;
   }
	

}
