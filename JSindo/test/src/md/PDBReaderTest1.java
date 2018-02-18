package md;

import java.io.IOException;

import sys.Constants;
import sys.Utilities;

public class PDBReaderTest1 {

   public static void main(String[] args){
      
      SystemMD si = new SystemMD();
      
      PDBReader pdbr = new PDBReader("test/md/si_init.pdb");
      pdbr.setSystemMD(si);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      //SystemMD si = pdbr.getSystemMD();
      
      Residue[] residues = si.getResidueALL();
      for(int i=0; i<residues.length; i++){
         System.out.print(residues[i].getName()+" ");
      }
      System.out.println();

      AtomMD[] atoms = si.getAtomALL();
      double[][] xyz_init = new double[atoms.length][3];
      
      for(int i=0; i<atoms.length; i++){
         xyz_init[i] = atoms[i].getXYZCoordinates();
      }
      
      pdbr.setFileName("test/md/si_mini.pdb");
      double[][] xyz_mini=null;
      try{
         xyz_mini = pdbr.readCoordinates();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      for(int i=0; i<atoms.length; i++){
         String label = atoms[i].getLabel();
         System.out.printf("%4s %8.3f %8.3f %8.3f \n", label, 
               xyz_init[i][0]*Constants.Bohr2Angs, 
               xyz_init[i][1]*Constants.Bohr2Angs, 
               xyz_init[i][2]*Constants.Bohr2Angs);
      }
      System.out.println();

      for(int i=0; i<atoms.length; i++){
         String label = atoms[i].getLabel();
         System.out.printf("%4s %8.3f %8.3f %8.3f \n", label, 
               xyz_mini[i][0]*Constants.Bohr2Angs, 
               xyz_mini[i][1]*Constants.Bohr2Angs, 
               xyz_mini[i][2]*Constants.Bohr2Angs);
      }
      System.out.println();
      
      for(int i=0; i<atoms.length; i++){
         String label = atoms[i].getLabel();
         System.out.printf("%4s %8.3f %8.3f %8.3f ", label, 
               (xyz_mini[i][0]-xyz_init[i][0])*Constants.Bohr2Angs, 
               (xyz_mini[i][1]-xyz_init[i][1])*Constants.Bohr2Angs, 
               (xyz_mini[i][2]-xyz_init[i][2])*Constants.Bohr2Angs);
         double dd = Utilities.getNorm(xyz_mini[i], xyz_init[i]);
         System.out.printf("%8.3f \n", dd*Constants.Bohr2Angs); 
      }
      
   }
}
