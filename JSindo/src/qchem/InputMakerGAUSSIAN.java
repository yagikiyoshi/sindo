package qchem;

import sys.*;
import java.io.*;

import atom.Atom;
/**
 * Generates the input file for Gaussian.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 * @see InputMaker
 */
public class InputMakerGAUSSIAN extends InputMaker {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   InputMakerGAUSSIAN(){
      
   }
   public void makeInputFilebyOption() {
      String inputFile = basename+".com";
      try{
         FileWriter fw = new FileWriter(inputFile);
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));         

         pw.println("%chk="+basename+".chk");
         int mem=resource.getMemory();
         if(mem>0){            
            pw.println("%mem="+mem+"GB");
         }
         int nnodes=resource.getNodes();
         if(nnodes>1){
            pw.print("%LindaWorker=");
            String[] hosts = resource.getHostnames();
            for(int i=0; i<nnodes-1; i++){
               pw.print(hosts[i]+", ");               
            }
            pw.println(hosts[nnodes-1]);
         }
         int ppn=resource.getPpn();
         if(ppn>1){
            pw.println("%Nprocshared="+ppn);
         }
         
         String com = inputOptions.getValue("command1");
         com = com.trim().toUpperCase();

         int scr=resource.getScr();
         if(scr>0){
            com = com.toUpperCase();
            if(com.indexOf("MAXDISK")<0){
               com = com + " MAXDISK="+scr+"GB";
            }
         }
         pw.println(com);
         pw.println();
         
         pw.println(inputOptions.getValue("title"));
         pw.println();
         
         int charge = Integer.parseInt(inputOptions.getValue("charge"));
         int mult = Integer.parseInt(inputOptions.getValue("multiplicity"));
         pw.printf("%-3d %-3d",charge,mult);
         pw.println();
         
         String line = inputOptions.getValue("oniom");
         String[] oniom = null;
         if(line != null){
            oniom = line.split(",");            
         }

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
            if(oniom != null) {
               pw.print(oniom[i]);
            }
            pw.println();
         }
         pw.println();
         
         String com2;
         if((com2 = inputOptions.getValue("command2")) != null){
            String[] comm2 = com2.split("\n");
            int istart = 0;
            if(comm2[0].trim().length() == 0){
               istart++;
            }
            for(int i=istart; i<comm2.length; i++){
               pw.println(comm2[i].trim());                  
            }
            //pw.println(com2);
            pw.println();            
         }
         
         pw.close();
         
      }catch(IOException e){
         e.printStackTrace();
      }
      
   }
   
//   public String getInputFilename(){
//      return basename+".com";
//   }
   protected void checkInputOptions() throws InputOptionException {
      // Check that the input options have no error

      // We must have "command1" entry.
      String com = inputOptions.getValue("command1");
      if(com == null){
         String message = "Input option for GAUSSIAN must have an entry, command1.";
         InputOptionException e = new InputOptionException(message);
         throw e;
      }
      com=com.toUpperCase();
      
      // It is better to use "Nosymmetry" flag to prevent a re-orientation 
      // of the XYZ-coordinates, which confuses the energy derivatives, electric 
      // moments, etc.
      if(com.indexOf("NOSYMMETRY")<0 ){
         com = com + " NOSYMMETRY";
      }
      
      // Add "Density=Current" so that electric moments are punched in the fchk file 
      // for electron correlation methods.
      //if(com.indexOf("DENSITY")<0 ){
      //   com = com + " DENSITY=CURRENT";
      //}
      
      inputOptions.setValue("command1", com);
      

      // Add a default title if "title" entry is not given.
      String title = inputOptions.getValue("title");
      if(title == null){
         title = "no title";
         inputOptions.setValue("title", title);
      }

   }

}
