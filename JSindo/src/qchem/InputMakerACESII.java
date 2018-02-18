package qchem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import atom.Atom;


/**
 * Generates the input file for ACESII.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 * @see InputMaker
 */
public class InputMakerACESII extends InputMaker {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   InputMakerACESII(){
      
   }
   public void makeInputFile() {
      String inputFile = basename+".zmat";
      try{
         FileWriter fw = new FileWriter(inputFile);
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
         pw.println(inputOptions.getValue("title"));

         int Nat = molecule.getNat();
         
         for(int i=0; i<Nat; i++){
            Atom atom_i = molecule.getAtom(i);
            String label = atom_i.getLabel();
            double[] xyz = atom_i.getXYZCoordinates();
            pw.printf(label+" %12.6f %12.6f %12.6f  ", 
                  xyz[0], xyz[1], xyz[2]);
            pw.println();
         }
         pw.println();
         
         pw.println("*ACES2");
         pw.println("COORD=CARTESIAN,UNITS=BOHR");

         int charge = Integer.parseInt(inputOptions.getValue("charge"));
         pw.printf("CHARGE=%-3d",charge);
         pw.println();

         int mult = Integer.parseInt(inputOptions.getValue("multiplicity"));
         pw.printf("MULT=%-3d",mult);
         pw.println();
         
         int mem=resource.getMemory();
         if(mem>0){            
            pw.println("MEMOERY_SIZE="+mem+"GB");
         }
         
         String com = inputOptions.getValue("command");
         com = com.trim().toUpperCase();
         pw.println(com);

         pw.println();
         
         pw.close();

      }catch(IOException e){
         e.printStackTrace();
      }

   }

   protected void checkInputOptions() throws InputOptionException {
      // Check that the input options have no error

      // We must have "command" entry.
      String com = inputOptions.getValue("command");
      if(com == null){
         String message = "Input option for ACESII must have an entry, command.";
         InputOptionException e = new InputOptionException(message);
         throw e;
      }
      com=com.toUpperCase();
      
      // It is better to turn off symmetry to prevent a re-orientation 
      // of the XYZ-coordinates, which confuses the energy derivatives, electric 
      // moments, etc.
      //if(com.indexOf("SYMMETRY")<0 ){
      //   com = com + " SYMMETRY=NONE";
      //}
      //if(com.indexOf("NOREORI")<0 ){
      //   com = com + " NOREORI=ON";
      //}

   }

}
