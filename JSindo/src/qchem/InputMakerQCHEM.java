package qchem;

import java.io.*;
import java.util.*;
import atom.*;
import sys.*;

/**
 * Generates the input file for QChem
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 * @see InputMaker
 *
 */
public class InputMakerQCHEM extends InputMaker {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   InputMakerQCHEM(){
      
   }
   
   public void makeInputFile() {
      String inputFile = basename+".inp";
      try{
         FileWriter fw = new FileWriter(inputFile);
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));         

         Enumeration<Object> keyset = inputOptions.keys();
         
         while(keyset.hasMoreElements()){
            String key = (String) keyset.nextElement();
            String key2 = key.trim().toUpperCase();
            
            System.out.println(key2);
            pw.println("$"+key2);
            
            String line = inputOptions.getValue(key);
            String[] ts = line.split("\n");

            for(int n=0; n<ts.length; n++){
               if(ts[n].trim().length() > 0){
                  pw.println(ts[n]);
               }
            }
            if(key2.equals("REM")){
               pw.println("MEM_TOTAL    "+resource.getMemory()*1000);
            }

            if(key2.equals("MOLECULE")){
               int Nat = molecule.getNat();
               
               for(int i=0; i<Nat; i++){
                  Atom atom_i = molecule.getAtom(i);
                  String label = atom_i.getLabel();
                  double[] xyz = atom_i.getXYZCoordinates();
                  pw.printf("%-4s %12.6f %12.6f %12.6f  ", 
                        label,
                        xyz[0]*Constants.Bohr2Angs, 
                        xyz[1]*Constants.Bohr2Angs, 
                        xyz[2]*Constants.Bohr2Angs);
                  pw.println();
               }
            }
            
            
            pw.println("$END");
            pw.println();
            
         }
         
         pw.close();

      }catch(IOException e){
         e.printStackTrace();
      }
   }

   @Override
   protected void checkInputOptions() throws InputOptionException {
      
      Enumeration<Object> keyset = inputOptions.keys();

      String rem = null;
      String remkey = null;
      while(keyset.hasMoreElements()){
         String key = (String)keyset.nextElement();
         if(key.trim().equalsIgnoreCase("rem")){
            rem = inputOptions.getValue(key);
            remkey = key;
            break;
         }
      }
      
      // We must have "REM" entry
      if(rem == null){
         String message = "Input option for QChem must have an energy, rem. ";
         throw new InputOptionException(message);
      }
      rem = rem.toUpperCase();
      
      // MEM_TOTAL is given from resource.info
      if(rem.indexOf("MEM_TOTAL") >= 0){
         String message = "Invalid input option for QChem."
               + "MEM_TOTAL should be specified by resources.info.\n";
         throw new InputOptionException(message);         
      }
      
      String[] lines = rem.split("\n");
      rem = "";
      boolean isGUI = false;
      for(int n=0; n<lines.length; n++){
         if(lines[n].trim().length() > 0){
            if(lines[n].indexOf("GUI") >= 0){
               isGUI = true;
               rem = rem + "GUI          2\n";
               
            }else if(lines[n].indexOf("MEM_STATIC") >= 0 ){
               System.out.println();
               System.out.println("WARNING in InputMakerQCHEM.");
               System.out.println("WARNING: MEM_STATIC is found in the input option.");
               System.out.println("WARNING: Check that the value is compatible with MEM_TOTAL.");
               System.out.println();
               
               if(resource != null){
                  String[] ss = Utilities.splitWithSpaceString(lines[n]);
                  int mem_static = Integer.parseInt(ss[1]);
                  int mem_total  = resource.getMemory()*1000;
                  
                  if(mem_static > mem_total){
                     String message = "Invalid input option for QChem."
                           + "MEM_STATIC should be larger than MEM_TOTAL specified by resources.info.\n";
                     throw new InputOptionException(message);         
                  }
                  
               }
               
            }else{
               rem = rem + lines[n] +"\n";
            }
         }
      }
      if(! isGUI){
         rem = rem + "GUI          2\n";         
      }

      inputOptions.setValue(remkey, rem);
      
      

   }

}
