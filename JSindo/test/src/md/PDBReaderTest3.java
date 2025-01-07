package md;

import java.io.IOException;

import sys.Constants;
import sys.Utilities;

public class PDBReaderTest3 {

   public static void main(String[] args){
      
      SystemMD si = new SystemMD();
      
      PDBReader pdbr = new PDBReader("test/md/step2_solvator.pdb");
      pdbr.setSystemMD(si);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      //SystemMD si = pdbr.getSystemMD();
      
      int natom = si.getNumOfAtom();
      AtomMD[] atoms = si.getAtomALL();
      for(int i=0; i<natom; i++){
         double[] xyz = atoms[i].getXYZCoordinates();
         xyz[0] = xyz[0] + 25.0/Constants.Bohr2Angs;
      }
      
      PDBWriter pdbw = new PDBWriter();
      try {
         pdbw.write("test/md/step2_solvator_shiftx.pdb", si);
      }catch(IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      
      
      
      
   }
}
