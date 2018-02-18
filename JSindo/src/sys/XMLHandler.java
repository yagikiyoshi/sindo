package sys;

import java.io.*;
import java.util.*;

/**
 * Wrapper class to read and write xml files.
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class XMLHandler {
   
   private Properties properties= new Properties();
   
   /**
    * Read xml file from a file
    * @param fileName The name of xml file
    * @throws FileNotFoundException Thrown when the file doesn't exist.
    * @throws XMLReadException Thrown when the file has a wrong format, or when some IO errors occurred.
    */
   public void readXMLFile(String fileName) throws XMLReadException, FileNotFoundException {
      File xmlfile = new File(fileName);
      if(! xmlfile.exists()){
         throw new FileNotFoundException(fileName + " is not found.");
      }
      try{
         properties.loadFromXML(new FileInputStream(fileName));
      } catch (InvalidPropertiesFormatException e) {
         throw new XMLReadException("Invalid Format in "+fileName+":"+e.toString().split(":")[2].trim()+".");
      } catch (IOException e) {
         throw new XMLReadException("Error while reading "+fileName+".");
      }
   }
   /**
    * Write the properties to a xml file.
    * @param fileName The name of xml file
    * @throws IOException if writing to the specified file results in an error.
    */
   public void writeXMLFile(String fileName) throws IOException {
      properties.storeToXML(new FileOutputStream(fileName), null);
   }
   /**
    * Returns the value of parameter which have a key
    * @param key The key of parameter
    * @return The value
    */
   public String getValue(String key) {
      return properties.getProperty(key);
   }
   /**
    * Sets the value of parameter with a key
    * @param key The key
    * @param value The value
    */
   public void setValue(String key, String value){
      properties.setProperty(key, value);
   }
   
   /**
    * Returns a set of keys
    * @return the keys in Enumeration
    */
   public Enumeration<Object> keys(){
      return properties.keys();
   }

}
