package md;

import java.io.*;

public abstract class FileReaderMD {

   protected SystemMD system;
   protected String fname;
   
   /**
    * Returns the system of this reader
    * @return The system
    */
   public SystemMD getSystemMD(){
      return system;
   }
   /**
    * Sets the system to be read
    * @param system The system
    */
   public void setSystemMD(SystemMD system){
      this.system = system;
   }
   /**
    * Sets the name of the file to be read 
    * @param fileName the name of the file
    */
   public void setFileName(String fileName){
      this.fname = fileName;
   }
   /**
    * Reads the information from the specified file
    * @return the new System
    * @throws IOException thrown when an IO error is detected
    */
   public SystemMD readAll() throws IOException{
      if(system == null){
         throw new IOException("The system to fill in the information is not given");
      }
      readAllChild();
      return system;
   }
   
   /**
    * Returns an instance of file reader. 
    * @param type type of the file. "PDB" and "CRD" are supported.
    * @return An instance of the reader.
    */
   public static FileReaderMD getInstance(String type){
      type = type.toLowerCase();
      if(type.equals("pdb")){
         return new PDBReader();
      }else if(type.equals("crd")){
         return new CRDReader();
      }else{
         return null;
      }
   }
   
   protected abstract SystemMD readAllChild() throws IOException; 
}
