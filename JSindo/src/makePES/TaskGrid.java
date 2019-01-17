package makePES;

import java.io.File;

import qchem.*;
import molecule.*;

/**
 * Task for Grid PES generation.
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class TaskGrid extends TaskQChem {

   private PESInputData makePESData;
   private InputDataQC  qcData;
   private int[] mm;
   private double[] dq;
   private String basename;
   private String ndifftype;

   /**
    * Constructs the task for QFF / Grid PES generation
    * @param makePESData Input options
    * @param mm mode combination (null for q=0)
    * @param dq displacements for each mode (bohr emu^1/2)
    * @param basename Basename of the minfo file.
    */
   public TaskGrid(PESInputData makePESData, InputDataQC qcData, int[] mm, double[] dq, String basename) {
      this.makePESData = makePESData;
      this.qcData      = qcData;
      this.mm = mm;
      this.dq = dq;
      this.basename = basename;
      this.ndifftype = null;
   }
   
   public void setNdifftype(String ndifftype) {
      this.ndifftype = ndifftype;
   }

   @Override
   protected int preProcess() {
      
      File minfo = new File(basename+".minfo");
      if(minfo.exists()) {
         // Skip the QChem job
         return 1;
      }

      MolUtil util = new MolUtil(makePESData.getMolecule());
      Molecule currentMol = util.copyAtoms();

      if(mm != null){
         VibTransformer trans = makePESData.getTransform();
         trans.dq2x(currentMol.getXYZCoordinates2(), mm, dq);
      }
      
      // Setup QuantChem
      try{
         qcpack = new QuantChem(qcData.getType());
         qcpack.setBasename(basename);
         InputMaker inputMaker = qcpack.getInputMaker();
         if(qcData.isOption()) {
            inputMaker.setOptions(qcData.getInputOption());
         }else {
            inputMaker.setTemplateFile(qcData.getTemplate());
         }
         inputMaker.setMolecule(currentMol);
      }catch(Exception e){
         // This exception never happens as it is already checked during setup.
      }
      
      // Generate the input
      super.preProcess();

      if(qcData.isDryrun()){
         return 1;
      }else{
         return 0;         
      }

   }

   protected int postProcess() {
      
      if(qcData.isDryrun()) return 0;

      int stat = super.postProcess();
      if(stat == -1) {
         System.out.println(header+"Quantum Chemitry job ended in error for "+basename);
         System.out.println(header+"Recommended to check the output of qchem run.");
         System.out.println(header+"The program continues by suppling the energy by inter/extrapolation.");
         return 0;
      }

      if(molecule.getElectronicData().getEnergy() == Double.NaN){
         System.out.println(header+"Energy is not found in "+basename);
         System.out.println(header+"Recommended to check the output of qchem run.");
      }
      
      if(ndifftype != null) {
         if(ndifftype.equals("HESS") && molecule.getElectronicData().getHessian() == null){
            System.out.println(header+" Hessian is not found in "+basename);
            System.out.println(header+" Error termination.");
            return -1;
         }

         if(ndifftype.equals("GRAD") &&  molecule.getElectronicData().getGradient() == null){
            System.out.println(header+" Gradient is not found in "+basename);
            System.out.println(header+" Error termination.");
            return -1;
         }
      }

      if(qcData.isRemoveFile()){
         Exec exec = qcpack.getExec();
         exec.removeFiles();
      }
      return 0;

   }

}
