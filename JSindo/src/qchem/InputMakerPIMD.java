package qchem;

import java.io.*;

import atom.Atom;

/**
 * Generates the input file for PIMD.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 * @see InputMaker
 */
public class InputMakerPIMD extends InputMaker {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   InputMakerPIMD(){
   }

   @Override
   public void makeInputFile() {
      File workdir = new File(basename);
      if(! workdir.exists())  workdir.mkdir();
      try{
         FileWriter fw = new FileWriter(basename+"/centroid.dat");
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));         

         int Nat = molecule.getNat();
         
         for(int i=0; i<Nat; i++){
            Atom atom_i = molecule.getAtom(i);
            double[] xyz = atom_i.getXYZCoordinates();
            pw.printf(" %24.15e %24.15e %24.15e",xyz[0],xyz[1],xyz[2]);
            pw.println();
         }
         pw.println();
         pw.close();
         
      }catch(IOException e){
         e.printStackTrace();
      }
      
   }

   @Override
   protected void checkInputOptions() throws InputOptionException {
      // Do nothing
   }

   @Override
   protected void makeInputFilebyOption() {
      // Do nothing
   }

}
