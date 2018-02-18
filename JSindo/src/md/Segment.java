package md;

import java.util.*;

/**
 * Stores the information of segment
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.6
 */
public class Segment implements Cloneable, AtomList, ResidueList{

   private String name;
   private ArrayList<Residue> residueList;
   private Residue[] residues;
   private AtomMD[] atoms;
   private boolean addResidue;
	
   public Segment(){
      residueList = new ArrayList<Residue>();
      addResidue = true;
      residues = null;
      atoms = null;
   }
	
   /**
    * Returns the name of this segment
    * @return the name
    */
   public String getName() {
      return name;
   }
   /**
    * Sets the name of this segment
    * @param segName the name
    */
   public void setName(String segName) {
      this.name = segName;
   }
   /**
    * (Package private) Resets the residueList to be editable
    */
   void openResidueList(){
      if(! this.addResidue){
         this.addResidue = true;
         
         residueList = new ArrayList<Residue>();
         for(int i=0; i<residues.length; i++){
            residueList.add(residues[i]);
            residues[i].openAtomList();
         }
         
         residues = null;
         atoms = null;
      }
   }
   /**
    * Closes the residue list and residues are not accepted any further
    */
   public void closeResidueList(){
      if(this.addResidue){
         this.addResidue = false;

         residues = residueList.toArray(new Residue[0]);
         residueList = null;
         
         int natm = 0;
         for(int i=0; i<residues.length; i++){
            residues[i].closeAtomList();
            natm = natm + residues[i].getNumOfAtom();
         }

         atoms = new AtomMD[natm];
         
         natm = 0;
         for(int i=0; i<residues.length; i++){
            AtomMD[] atomsi = residues[i].getAtomALL();
            System.arraycopy(atomsi, 0, atoms, natm, atomsi.length);
            natm = natm + atomsi.length;
         }
         
      }
   }
   /**
    * Adds a residue to the list
    * @param residue the residue to be added
    * @throws BuilderException thrown when the segment is closed
    */
   public void addResidueList(Residue residue) throws BuilderException {
      if(addResidue){
         residueList.add(residue);
      }else{
         throw new BuilderException("Attempted to add a residue to an uneditable segment");
      }
   }

  /**
   * Adds an array of residues to the list
   * @param residues the array of residues to be added
    * @throws BuilderException thrown when the segment is closed
   */
   public void addResidueList(Residue[] residues) throws BuilderException{
      if(addResidue){
         for(int i=0; i<residues.length; i++){
            residueList.add(residues[i]);
         }
      }else{
         throw new BuilderException("Attempted to add a residue to an uneditable segment");
      }
   }

   /**
    * Insert a residue to specified index
    * @param index The index
    * @param residue The residue to be inserted
    * @throws BuilderException thrown when the segment is closed
    */
   public void insertResidueList(int index, Residue residue) throws BuilderException {
      if(addResidue){
         residueList.add(index,residue);
      }else{
         throw new BuilderException("Attempted to insert a residue to an uneditable segment");
      }
   }

   /**
    * Insert an array of residues to a specified index
    * @param index The index
    * @param residues The array of residues to be inserted
    * @throws BuilderException thrown when the segment is closed
    */
   public void insertResidueList(int index, Residue[] residues) throws BuilderException {
      if(addResidue){
         for(int i=0; i<residues.length; i++){
            residueList.add(index+i,residues[i]);
         }
      }else{
         throw new BuilderException("Attempted to insert a residue to an uneditable segment");
      }
   }
   /**
    * Removes a residue from the list
    * @param index the index of the residue to be removed
    * @throws BuilderException thrown when the segment is closed
    */
   public void removeResidueList(int index) throws BuilderException {
      if(addResidue){
         residueList.remove(index);
      }else{
         throw new BuilderException("Attempted to remove a residue from an uneditable segment");         
      }
   }
   
   /**
    * Returns a residue from the list
    * @param index The index of the list
    * @return The residue
    * @throws BuilderException thrown when the segment is closed
    */
   public Residue getResidueList(int index) throws BuilderException {
      if(addResidue){
         return residueList.get(index);
      }else{
         throw new BuilderException("Attempted to get a residue from an uneditable segment"); 
      }
   }
   /**
    * Returns the list of residues
    * @return list of residues
    * @throws BuilderException Uneditable segment does not have resudueList
    */
   public ArrayList<Residue> getResidueList() throws BuilderException {
      if(addResidue){
         return residueList;
      }else{
         throw new BuilderException("Attempted to get a list of residue from an uneditable segment"); 
      }
   }

   /**
    * Initialize the residue list
    */
   private void initResidueList(){
      residueList = new ArrayList<Residue>();
      addResidue = true;
      residues = null;
   }
   
   public int getNumOfResidue(){
      if(addResidue){
         return residueList.size();
      }else{         
         return residues.length;
      }
   }

   public Residue[] getResidueALL(){
      return residues;
   }

   public Residue getResidue(int index){
      Residue res = null;
      if(addResidue){
         res = residueList.get(index);
      }else{
         res = residues[index];
      }
      return res;
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
   
   public Segment clone(){
      Segment segment = null;
      try{
         segment = (Segment)super.clone();
         segment.initResidueList();
         if(residueList != null){
            for(int i=0; i<residueList.size(); i++){
               segment.addResidueList(residueList.get(i).clone());
            }
         }else{
            for(int i=0; i<residues.length; i++){
               segment.addResidueList(residues[i].clone());
            }
         }
      }catch(CloneNotSupportedException e){
         e.printStackTrace();
      }catch(BuilderException e){
         e.printStackTrace();
      }
      return segment;
   }
   
}
