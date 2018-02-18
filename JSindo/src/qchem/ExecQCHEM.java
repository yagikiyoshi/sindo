package qchem;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;

public class ExecQCHEM extends Exec {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   ExecQCHEM(){
      
   }
   
   public String[] getCommand() {
      String ss = null;
      if(resource != null){
         ss = String.valueOf(resource.getPpn());
      }else{
         ss = "ppn_given_by_resource";
      }
      String cdir = System.getProperty("user.dir");     
      String[] command = {"runQChem.sh",cdir,basename+".inp",ss};
      return command;
   }

   @Override
   public void removeFiles() {
      File inp = new File(basename+".inp");
      if(inp.exists()) inp.delete();
      File fchk = new File(basename+".FChk");
      if(fchk.exists()) fchk.delete();
      File out = new File(basename+".out");
      if(out.exists()) out.delete();  
   }

   @Override
   public FileFilter getFilter() {
      return new FileNameExtensionFilter("QChem (*.FChk)","FChk");
   }

}
