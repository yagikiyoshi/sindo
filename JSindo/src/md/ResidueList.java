package md;

public interface ResidueList {

   /**
    * Returns the number of residues
    * @return the number of residues
    */
   public int getNumOfResidue();

   /**
    * Returns all residues
    * @return residues in array
    */
   public Residue[] getResidueALL();
   
   /**
    * Returns a residue
    * @param index the index of the residue
    * @return the residue
    */
   public Residue getResidue(int index);
   
}
