
package qchem;

import sys.*;
import jobqueue.*;
import molecule.Molecule;
import java.io.*;
import java.util.ArrayList;

import atom.Atom;

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
    * True when template file is used for generating the input
    */
   private boolean use_template=false;
   
   protected String[] template;
   
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
    * @throws InputOptionException thrown when the option is not valid
    */
   public void setOptions(XMLHandler inputOptions) throws InputOptionException {
      this.inputOptions = inputOptions;
      this.checkInputOptions();
      this.use_template = false;
   }
   /**
    * Sets the template file
    * @param templateFile the name of the template file
    * @throws IOException thrown when the file does not exist or when error.
    */
   public void setTemplateFile(String templateFile) throws FileNotFoundException, IOException {
      BufferedReader br = new BufferedReader(new FileReader(templateFile));
      ArrayList<String> array = new ArrayList<String>();
      
      String line = null;
      while((line = br.readLine()) != null) {
         array.add(line);
      }
      br.close();
      
      this.setTemplateFile(array.toArray(new String[0]));
      
   }
   /**
    * Sets the template file
    * @param template The content of a template file, each line in a string.
    */
   public void setTemplateFile(String[] template) {
      this.template = template;
      this.use_template = true;
   }
   /**
    * Returns the input options
    * @return the input options
    */
   public XMLHandler getOptions(){
      return this.inputOptions;
   }
   /**
    * Returns the template file in an String array
    * @return The template file each line in a String
    */
   public String[] getTemplateFile() {
      return this.template;
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
   public void makeInputFile() {
      if (! use_template) {
         this.makeInputFilebyOption();
         return;
      }
      
      String inputFile = basename+".inp";
      try{
         FileWriter fw = new FileWriter(inputFile);
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

         for(String line: template) {
            String str = line.toLowerCase();
            if(str.indexOf("#coord") != -1) {
               this.printCoordinates(pw);;
            }else if(str.indexOf("#basename#") != -1) {
               line = str.replaceAll("#basename#", basename);
               pw.println(line);
            }else {
               pw.println(line);
            }
         }

         pw.close();
         
      }catch(IOException e){
         e.printStackTrace();
      }

   }
   
   protected void printCoordinates(PrintWriter pw) {
      int Nat = molecule.getNat();
      
      for(int i=0; i<Nat; i++){
         Atom atom_i = molecule.getAtom(i);
         String label = atom_i.getLabel();
         double[] xyz = atom_i.getXYZCoordinates();
         pw.printf("%-4s %12.6f %12.6f %12.6f  ", 
               label,
               xyz[0]*Constants.Bohr2Angs, 
               xyz[1]*Constants.Bohr2Angs, 
               xyz[2]*Constants.Bohr2Angs);
         pw.println();
      }
   }
   
   /**
    * Generates the input file from option
    */
   protected abstract void makeInputFilebyOption();
   /**
    * Checks the input options
    * @throws InputOptionException throws when the option is not valid
    */
   protected abstract void checkInputOptions() throws InputOptionException;

}
