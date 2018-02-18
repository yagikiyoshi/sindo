package qchem;

/** 
 * Generates the instance of InputMaker, OutputReader, Exec. <br>
 * The instance can be generated in two ways. In the first way, 
 * <pre>
 *    try{
 *       QuantChem qchem = new QuantChem("Gaussian");
 *    }catch(TypeNotSupportedException e){
 *       ...
 *    }
 * </pre>
 * The constructor brings up the InputMaker/OutputReader/Exec for the specified program. Then, the 
 * instances are accessible by, <br>
 * <pre>
 *    InputMaker inputMaker = qchem.getInputMaker();
 *    OutputReader outputReader = qchem.getOutputReader();
 *    Exec exec = qchem.getExec();
 * </pre>
 * The other way is to directly generate each instance, <br>
 * <pre>
 *    QuantChem qchem = new QuantChem();
 *    try{
 *       InputMaker inputMaker = qchem.getInputMaker("Gaussian");
 *       OutputReader outputReader = qchem.getOutputReader("NWChem");    
 *    }catch(TypeNotSupportedException e){
 *       ...
 *    }
 * </pre>
 * The instances are generated when the getter method is called. This way makes possible a flexible 
 * combination.<br>
 * TypeNotSupportedException is thrown when the specified program is unknown. 
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 * @see Exec InputMaker OutputReader
 */
public class QuantChem {
   
   private InputMaker inputMaker;
   private OutputReader outputReader;
   private Exec exec;
   private String basename;

   /**
    * Constructs the object without the InputMaker/OutputReader/Exec.
    */
   public QuantChem(){
      inputMaker = null;
      outputReader = null;
      exec = null;
   }
   /**
    * Constructs the object with the InputMaker/OutputReader/Exec for the 
    * specified program.
    * @param name the name of program
    * @throws TypeNotSupportedException If the specified name is not supported
    */
   public QuantChem(String name) throws TypeNotSupportedException {
      inputMaker = this.getInputMaker(name);
      outputReader = this.getOutputReader(name);
      exec = this.getExec(name);
   }
   /**
    * Returns the Exec for the specified program
    * @param name the name of the program
    * @return the object of Exec
    * @throws TypeNotSupportedException If the specified name is not supported
    */
   public Exec getExec(String name) throws TypeNotSupportedException {
      try {
         exec = (Exec)Class.forName("qchem.Exec"+name.toUpperCase()).newInstance();
      } catch (Exception e) {
         String message = e.getMessage();
         message = message + " Exec for " + name + " is not supported.";
         throw new TypeNotSupportedException(message);
      }
      return exec;
   }

   /**
    * Returns the Exec
    * @return the Exec.
    */
   public Exec getExec(){      
      return exec;
   }

   /**
    * Returns the InputMaker for the specified program
    * @param name the name of the program
    * @return the InputMaker
    * @throws TypeNotSupportedException If the specified name is not supported
    */
   public InputMaker getInputMaker(String name) throws TypeNotSupportedException {
      try {
         inputMaker = (InputMaker)Class.forName("qchem.InputMaker"+name.toUpperCase()).newInstance();
      } catch (Exception e) {
         String message = e.getMessage();
         message = message + " InputMaker for " + name + " is not supported.";
         throw new TypeNotSupportedException(message);
      }
      return inputMaker;
   }
   /**
    * Returns the InputMaker
    * @return the InputMaker
    */
   public InputMaker getInputMaker(){
      return inputMaker;      
   }
   /**
    * Returns the OutputReader for the specified program
    * @param name the name of the program
    * @return the OutputReader
    * @throws TypeNotSupportedException If the specified name is not supported
    */
   public OutputReader getOutputReader(String name) throws TypeNotSupportedException {
      try {
         outputReader = (OutputReader)Class.forName("qchem.OutputReader"+name.toUpperCase()).newInstance();
      } catch (Exception e) {
         String message = e.getMessage();
         message = message + " OutputReader for " + name + " is not supported.";
         throw new TypeNotSupportedException(message);
      }
      return outputReader;

   }
   /**
    * Returns the OutputReader 
    * @return the OutputReader
    */
   public OutputReader getOutputReader(){
      return outputReader;
   }
   /**
    * Set basename for InputMaker, Exec, and OutputMaker
    * @param basename The basename of the file
    */
   public void setBasename(String basename){
      this.inputMaker.setBasename(basename);
      this.exec.setBasename(basename);
      this.outputReader.setBasename(basename);
      this.basename = basename;
   }
   /**
    * Get basename
    * @return basename
    */
   public String getBasename(){
      return basename;
   }

}
