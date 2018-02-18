package qchem;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Provides interface methods for MOLPRO. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 */
public class ExecMOLPRO extends Exec {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   ExecMOLPRO(){
      
   }
   
   public String[] getCommand() {
      String cdir = System.getProperty("user.dir");
      String[] command = {"runMOLPRO.sh",cdir,basename+".com"};
      return command;
   }

   @Override
   public void removeFiles() {
      // TODO Auto-generated method stub

   }

   public FileFilter getFilter() {
      return new FileNameExtensionFilter("MOLPRO (*.out)","out");
   }

}
