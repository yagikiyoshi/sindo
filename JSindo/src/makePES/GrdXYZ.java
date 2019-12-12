package makePES;

import java.io.*;
import java.util.*;
import sys.*;
import atom.*;
import molecule.*;

public class GrdXYZ {

   File f;
   PrintWriter pw;
   int num_grid;
   ArrayList<String> datFilename;
   
   /**
    * Constructs an instance with a specified name for the grid data 
    * @param filename the filename (filename+".xyz")
    */
   public GrdXYZ(String filename) {
      num_grid = 0;
      datFilename = null;
      this.open(filename);
   }
   
   /**
    * Opens a file with a specified name 
    * @param filename the filename (filename+".xyz") 
    */
   public void open(String basename) {
      String filename = basename+".xyz";
      
      f = new File(filename);
      if(f.exists()) {
         boolean loop = true;
         int i = 0;
         while(loop) {
            String backup = filename+"_"+i;
            File g = new File(backup);
            if(! g.exists()) {
               System.out.println("   Warning: "+filename+" already exists.");
               System.out.println("   Warning: Renamed to "+backup);
               System.out.println();
               f.renameTo(g);
               f = new File(filename);
               loop = false;
            }
            i++;
         }
      }
      try{
         pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
      }catch (IOException e){
         this.terminate(e);
      }
      
      String datfile = basename+".dat";
      File d = new File(datfile);
      if(d.exists()) {
         datFilename = new ArrayList<String>();
         try {
            BufferedReader br = new BufferedReader(new FileReader(datfile));
            String line=null;
            while((line = br.readLine()) != null) {
               String[] ss = line.split(",");
               datFilename.add(ss[0]);
            }
            br.close();
            
         }catch(IOException e) {
            this.terminate(e);
         }
      }
      
   }
   /**
    * Closes the file
    */
   public void close(){
      pw.close();
      if(num_grid == 0) {
         f.delete();
      }
   }
   
   /**
    * Returns the number of grid points written in the file
    * @return the number of grid points.
    */
   public int getNumOfGrid() {
      return num_grid;
   }
   
   /**
    * Writes a grid point to the xyz file
    * @param makePESData PES data
    * @param mm Mode numbers
    * @param dq Coordinates
    * @param basename Name of the grid
    */
   public void write(InputDataPES makePESData, int[] mm, double[] dq, String basename){
      
      File minfo = new File(makePESData.getMinfofolder()+basename+".minfo");
      if(minfo.exists()) {
         // Skip to write this grid
         return;
      }

      if(datFilename != null) {
         if(datFilename.contains(basename)) {
            // Skip to write this grid
            return;
         }
      }
      
      num_grid++;
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
