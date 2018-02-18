package qchem;

import java.io.FileNotFoundException;

import jobqueue.Task;
import molecule.*;

/**
 * Task to carry out quantum chemistry calculations. <br>
 * Construct this class for a specified program, <br>
 * <pre>
 *    TaskQchem task = new TaskQchem("Gaussian");
 * </pre>
 * Then, set the following three:
 * <ol><li> The basename of input/output files:
 * <pre>
 *    task.setBasename("testH2O");
 * </pre>
 * </li>
 * <li> The molecule:
 * <pre>
 *    task.setMolecule(molecule);
 * </pre>
 * </li> 
 * <li> Input options,
 * <pre>
 *    task.setInputOptions("input.xml");
 * </pre>
 * </li></ol>
 * Optionally, MInfoIO can be set which specifies the format of minfo file.
 * 
 * The default constructor requires QuantChem, which is already set up 
 * for the target program. <br>
 * <pre>
 *    QuantChem qcpack = new QuantChem("Gaussian");
 *    task.appendQuantChem(qcpack);
 * </pre>
 * In this case, basename, molecule and inputoptions can be omitted, provided they are set to qcpack. 
 * 
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class TaskQChem extends Task {

   protected QuantChem qcpack;
   protected Molecule molecule;
   protected MInfoIO minfoIO;
   //private int chargeType;

   /**
    * Default constructor.
    */
   public TaskQChem(){
      
   }
   /**
    * Constructs the task for specified quantum chemistry program 
    * @param type the type of program (case insensitive)
    * @throws TypeNotSupportedException thrown when the specified type is not supported 
    */
   public TaskQChem(String type) throws TypeNotSupportedException {
      this.qcpack = new QuantChem(type);
      //this.chargeType = ElectronicDataReader.NoAtmCharge;
   }
   /**
    * Sets the QuantChem.  
    * @param qcpack the object of QuantChem
    */
   public void appendQuantChem(QuantChem qcpack){
      this.qcpack = qcpack;
   }
   /**
    * Sets the basename of the files for quantum chemistry jobs
    * @param basename basename of the files
    */
   public void setBasename(String basename){
      qcpack.setBasename(basename);
   }
   /**
    * Sets the molecule
    * @param molecule the molecule 
    */
   public void setMolecule(Molecule molecule){
      this.molecule = molecule;
   }
   /**
    * Sets the input options
    * @param fileName the file name of the input options
    * @throws InputOptionException thrown when the file contains error
    * @throws FileNotFoundException thrown when the file is not found
    */
   public void setInputOptions(String fileName) throws FileNotFoundException, InputOptionException{
      InputMaker im = qcpack.getInputMaker();
      im.setOptions(fileName);
   }
   /**
    * Sets MinfoIO. 
    * @param minfoIO Controls which data are dumped in the minfo files. 
    */
   public void setMinfoIO(MInfoIO minfoIO){
      this.minfoIO = minfoIO;
   }
   /**
    * Sets the type of atomic charge read from the output. 
    * @param type the type of atomic charge (MoleculeReader.XX, default = Mulliken)
    */
   //public void setAtomicChargeType(int type){
   //   this.chargeType = type;
   //}
   /**
    * Sets the command from Exec.
    */
   protected String[] getCommand() {
      return qcpack.getExec().getCommand();
   }
   /**
    * Generates the input file.
    */
   protected int preProcess(){
      InputMaker im = qcpack.getInputMaker();
      im.setResource(this.resource);
      if(molecule != null){
         im.setMolecule(this.molecule);         
      }else{
         this.molecule = im.getMolecule();
      }
      im.makeInputFile();
      
      return 0;
      
   }
   
   /**
    * Read the output and save to minfo file.
    * @return 0: regular, -1: error and cancel
    */
   protected int postProcess(){
      
      try{
         OutputReader outputReader = qcpack.getOutputReader();
         outputReader.checkFile();
         QChemToMol edataReader = new QChemToMol(outputReader);
         edataReader.appendMolecule(molecule);
         //edataReader.setCharge(chargeType);
         molecule = edataReader.read();
         
         if(minfoIO == null) minfoIO = new MInfoIO();
         minfoIO.unsetAllData();
         minfoIO.setElecData(true);
         minfoIO.appendMolecule(molecule);
         minfoIO.dumpMOL(qcpack.getBasename()+".minfo");

      }catch(Exception e){
         System.out.println(header+e.getMessage());
         return -1;
      }

      return 0;
   }

}
