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
   private XMLHandler inputOption;
   private String[] template;
   private boolean dryrun;
   private boolean removeFile;
   
   private boolean option;
   
   /**
    * This is a package private class
    */
   InputDataQC(){
      option = true;
   }
   
   public boolean isOption() {
      return option;
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
   public XMLHandler getInputOption() {
      return inputOption;
   }
   public void setInputOption(XMLHandler inputOption) {
      this.option = true;
      this.inputOption = inputOption;
   }
   public String[] getTemplate() {
      return template;
   }
   public void setTemplate(String[] template) {
      this.option = false;
      this.template = template;
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
   
}
