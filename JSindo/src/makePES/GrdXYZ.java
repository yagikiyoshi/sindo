package makePES;

import java.io.*;
import sys.*;
import atom.*;
import molecule.*;

public class GrdXYZ {

   PrintWriter pw;
   
   /**
    * Constructs an instance with a specified name for the grid data 
    * @param filename the filename (filename+".xyz")
    */
   public GrdXYZ(String filename) {
      this.open(filename);
   }
   
   /**
    * Opens a file with a specified name 
    * @param filename the filename (filename+".xyz") 
    */
   public void open(String filename) {
      filename = filename+".xyz";
      
      try{
         pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
      }catch (IOException e){
         this.terminate(e);
      }
      
      System.out.println("   Writing the coordinates to "+filename);
      System.out.println();
      
   }
   /**
    * Closes the file
    */
   public void close(){
      pw.close();
   }
   
   public void write(InputDataPES makePESData, int[] mm, double[] dq, String basename){
      
      File minfo = new File(basename+".minfo");
      if(minfo.exists()) {
         // Skip to write this grid
         return;
      }

      MolUtil util = new MolUtil(makePESData.getMolecule());
      Molecule currentMol = util.copyAtoms();

      if(mm != null){
         VibTransformer trans = makePESData.getTransform();
         trans.dq2x(currentMol.getXYZCoordinates2(), mm, dq);         
      }
      
      double aa = Constants.Bohr2Angs;
      
      pw.printf("%10d \n",currentMol.getNat());
      pw.println(basename);
      
      for(int n=0; n<currentMol.getNat(); n++){
         Atom ai = currentMol.getAtom(n);
         double[] xyz = ai.getXYZCoordinates();
         
         pw.printf("%4s  %20.10f  %20.10f  %20.10f",
               ai.getLabel(),xyz[0]*aa,xyz[1]*aa,xyz[2]*aa);
         pw.println();

      }

   }
   
   private void terminate(Exception e){
      System.out.println();
      System.out.println("Error while setting up GrdXYZ.");
      System.out.println(e.getMessage());
      System.out.println();
      System.out.println();
      Utilities.terminate();
   }
}
