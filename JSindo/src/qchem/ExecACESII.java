package qchem;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Provides interface methods for ACESII. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 */
public class ExecACESII extends Exec {
   
   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   ExecACESII(){
      
   }

   public String[] getCommand() {
      String cdir = System.getProperty("user.dir");
      String[] command = {"runACESII.sh",cdir,basename+".zmat"};
      return command;
   }

   public void removeFiles() {
      File zmat = new File(basename+".zmat");
      if(zmat.exists()) zmat.delete();
      File out = new File(basename+".out");
      if(out.exists()) out.delete();
      File grd = new File(basename+".GRD");
      if(grd.exists()) grd.delete();
      File dpl = new File(basename+".DIPOL");
      if(dpl.exists()) dpl.delete();
      File fcm = new File(basename+".FCMINT");
      if(fcm.exists()) fcm.delete();
      File dpldrv = new File(basename+".DIPDER");
      if(dpldrv.exists()) dpldrv.delete();

   }

   public FileFilter getFilter() {
      return new FileNameExtensionFilter("ACESII (*.out)","out");
   }

}
