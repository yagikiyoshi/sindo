package qchem;

import java.io.File;

import javax.swing.filechooser.FileFilter;
//import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Provides interface methods for PIMD. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 */
public class ExecPIMD extends Exec {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   ExecPIMD(){
      
   }

   public String[] getCommand() {
      String cdir = System.getProperty("user.dir");
      String[] command = {"runPIMD.sh",cdir,basename};
      return command;
   }

   @Override
   public void removeFiles() {
      File forces = new File(basename+"-forces.out");
      if(forces.exists()) {
         forces.delete();
      }
      File dipole = new File(basename+"-dipole.out");
      if(dipole.exists()){
         dipole.delete();
      }
      File sindo = new File(basename+"-sindo.out");
      if(sindo.exists()){
         sindo.delete();
      }

   }

   @Override
   public FileFilter getFilter() {
      //TODO filter for PIMD
      //return new FileNameExtensionFilter("PIMD (*.out)","out");
      return null;
   }

}
