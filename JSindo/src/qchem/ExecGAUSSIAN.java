package qchem;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

/**
 * Provides interface methods for Gaussian. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class ExecGAUSSIAN extends Exec {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   ExecGAUSSIAN(){
      
   }

   public String[] getCommand() {
      String cdir = System.getProperty("user.dir");
      String[] command = {"runGaussian.sh",cdir,basename+".inp"};
      return command;
   }
   public void removeFiles(){
      File inp = new File(basename+".inp");
      if(inp.exists()) inp.delete();
      File fchk = new File(basename+".fchk");
      if(fchk.exists()) fchk.delete();
      File chk = new File(basename+".chk");
      if(chk.exists()) chk.delete();
      File out = new File(basename+".out");
      if(out.exists()) out.delete();  
   }
   public FileFilter getFilter(){
      return new FileNameExtensionFilter("Gaussian (*.fchk)","fchk");
   }

}
