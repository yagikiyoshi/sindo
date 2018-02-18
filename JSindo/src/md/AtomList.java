package md;

public interface AtomList {

   /**
    * Returns the number of atoms 
    * @return The number of atoms
    */
   public int getNumOfAtom();

   /**
    * Returns all atoms
    * @return the atoms in array
    */
   public AtomMD[] getAtomALL();
   
   /** Returns an atom in the residue
    * @param index the index (0 - NumOfAtom-1)
    * @return the atom 
    */
   public AtomMD getAtom(int index);
   
   
}
