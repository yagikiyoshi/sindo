package qchem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import atom.Atom;
import sys.Constants;

/**
 * Generates the input file for Molpro.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 * @see InputMaker
 */
public class InputMakerMOLPRO extends InputMaker {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   InputMakerMOLPRO(){
      
   }

   @Override
   public void makeInputFile() {
      String inputFile = basename+".inp";
      try{
         FileWriter fw = new FileWriter(inputFile);
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));         

         int mem=resource.getMemory();
         if(mem>0){            
            pw.println("memory,"+mem+"000,m");
         }
         pw.println();

         pw.println("geomtyp=xyz");
         pw.println("geometry={");
         
         int Nat = molecule.getNat();
         
         pw.println(Nat);
         pw.println("   CARTESIAN COORDINATES");
         
         for(int i=0; i<Nat; i++){
            Atom atom_i = molecule.getAtom(i);
            String label = atom_i.getLabel();
            double[] xyz = atom_i.getXYZCoordinates();
            pw.printf(label+" %12.6f %12.6f %12.6f  ", 
                  xyz[0]*Constants.Bohr2Angs, 
                  xyz[1]*Constants.Bohr2Angs, 
                  xyz[2]*Constants.Bohr2Angs);
            pw.println();
         }
         pw.println("}");
         pw.println();
         
         String com = inputOptions.getValue("command1");
         pw.println(com);
         pw.println();

         pw.close();
         
      }catch(IOException e){
         e.printStackTrace();
      }

   }

   @Override
   protected void checkInputOptions() throws InputOptionException {
      // TODO Auto-generated method stub

   }

}
