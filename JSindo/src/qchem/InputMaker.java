
package qchem;

import sys.*;
import jobqueue.*;
import molecule.Molecule;
import java.io.*;

/**
 * Generates the input file for electronic structure calculation. <br>
 * The instance of this class is generated through QuantChem class as, <br>
 * <pre>
 *    QuantChem qchem = new QuantChem();
 *    try{
 *       InputMaker im = qchem.getInputMaker("Gaussian");
 *    }catch(TypeNotSupportedException e){
 *       ...
 *    }
 * </pre>
 * The instance requires the basename, resource, and options, which are set by 
 * setters. Then, the <i>makeInputFile</i> generates the input file.
 * <pre>
 *    im.setOptions("sample/Gaussian/GaussianTemplate1.xml");         
 *    im.setBasename("co2");
 *    im.setMolecule(molecule);
 *    im.setResource(res);      
 *    im.makeInputFile();
 * </pre> 
 * 
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public abstract class InputMaker {

   /**
    * Input options in xml
    */
   protected XMLHandler inputOptions=null;
   /**
    * Resource where the job runs
    */
   protected Resource resource=null;
   /**
    * The geometry, atomic number, etc
    */
   protected Molecule molecule=null;
   /**
    * Basename of the input file
    */
   protected String basename=null;

   /**
    * Set the basename of the input file 
    * @param base basename
    */
   public void setBasename(String base) {
      basename = base;
   }
   /**
    * Set the resource where the calculation is carried out
    * @param resource Resource 
    */
   public void setResource(Resource resource) {
      this.resource = resource;
   }
   /**
    * Set the input options 
    * @param fileName The file name of the template file
    * @throws FileNotFoundException thrown when the template file is not found
    * @throws InputOptionException thrown when input option is not valid
    */
   public void setOptions(String fileName) throws FileNotFoundException, InputOptionException {
      inputOptions = new XMLHandler();
      try{
         inputOptions.readXMLFile(fileName);        
      }catch(XMLReadException e){
         String message = e.getMessage();
         message = message + " Error in "+fileName.trim()+".";
         throw new InputOptionException(message);
      }
      try{
         this.setOptions(inputOptions);
      }catch(InputOptionException e){
         String message = e.getMessage();
         message = message + " Error in "+fileName.trim()+".";
         throw new InputOptionException(message);
      }
   }
   /**
    * Set the input options
    * @param inputOptions The input options 
    * @throws InputOptionException throws when the option is not valid
    */
   public void setOptions(XMLHandler inputOptions) throws InputOptionException {
      this.inputOptions = inputOptions;
      this.checkInputOptions();
   }
   /**
    * Returns the input options
    * @return the input options
    */
   public XMLHandler getOptions(){
      return this.inputOptions;
   }
   /** 
    * Set the information of molecule
    * @param molecule The information of molecule
    */
   public void setMolecule(Molecule molecule) {
      this.molecule = molecule;
   }
   /**
    * Gets the information of molecule
    * @return the information of molecule
    */
   public Molecule getMolecule(){
      return this.molecule;
   }
   
   /**
    * Generates the input file
    */
   public abstract void makeInputFile();
   /**
    * Checks the input options
    * @throws InputOptionException throws when the option is not valid
    */
   protected abstract void checkInputOptions() throws InputOptionException;

}
