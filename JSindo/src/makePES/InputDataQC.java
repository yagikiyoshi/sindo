package makePES;

import sys.XMLHandler;

/**
 * (Package private) Manages the data used in MakePES
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 4.0
 */
public class InputDataQC {

   // parameters for QC run
   private String type;
   private String title;
   private String inputFile;
   private XMLHandler inputOption;
   private String[] inputTemplate;
   private boolean dryrun;
   private boolean removeFile;
   private String xyzBasename;
   
   private boolean option;
   
   public static String GENERIC="generic";
   
   /**
    * This is a package private class
    */
   InputDataQC(){
      option = true;
   }
   
   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public boolean isOption() {
      return option;
   }
   public void setOption(boolean option) {
      this.option = option;
   }
   public String getInputFile() {
      return inputFile;
   }
   public void setInputFile(String inputFile) {
      this.inputFile = inputFile;
   }
   public XMLHandler getInputOption() {
      return inputOption;
   }
   public void setInputOption(XMLHandler inputOption) {
      this.option = true;
      this.inputOption = inputOption;
   }
   public String[] getInputTemplate() {
      return inputTemplate;
   }
   public void setInputTemplate(String[] template) {
      this.option = false;
      this.inputTemplate = template;
   }
   public boolean isDryrun() {
      return dryrun;
   }
   public void setDryrun(boolean dryrun) {
      this.dryrun = dryrun;
   }
   public boolean isRemoveFile() {
      return removeFile;
   }
   public void setRemoveFile(boolean removeFile) {
      this.removeFile = removeFile;
   }
   /**
    * Returns the basename of a xyz file for qchem = generic
    * @return basename
    */
   public String getXyzBasename() {
      return xyzBasename;
   }
   /**
    * Sets the basename of a xyz file for qchem = generic
    * @param basename basename
    */

   public void setXyzBasename(String xyzBasename) {
      this.xyzBasename = xyzBasename;
   }
   
}
