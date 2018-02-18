package qchem;

import javax.swing.filechooser.FileFilter;
import jobqueue.*;

/**
 * Provides interface methods for electronic structure calculations. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public abstract class Exec {

   /**
    * Filename used in the command
    */
   protected String basename;
   
   /**
    * Resource information 
    */
   protected Resource resource;
   
   /**
    * Sets a basename
    * @param basename basename of the file
    */
   public void setBasename(String basename){
      this.basename = basename;
   }
   /**
    * Sets a resource  
    * @param resource resource information
    */
   public void setResource(Resource resource){
      this.resource = resource;
   }
   /**
    * Get a command to invoke the quantum chemistry program
    * @return Strings of command (e.g. {"runGaussian","/home/yagi","test.com"})
    */
   public abstract String[] getCommand();
   /**
    * Removes the input and output files of the quantum chemistry program
    */
   public abstract void removeFiles();
   /**
    * Provides the filter for filechooser  
    * @return FileFilter
    */
   public abstract FileFilter getFilter();
}
