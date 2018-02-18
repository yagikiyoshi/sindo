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
   private int[] mm;
   private double[] dq;
   private String basename;
   public static int level=0;

   /**
    * Constructs the task for QFF / Grid PES generation
    * @param makePESData Input options
    * @param mm mode combination (null for q=0)
    * @param dq displacements for each mode (bohr emu^1/2)
    * @param basename Basename of the minfo file.
    */
   public TaskGrid(PESInputData makePESData, int[] mm, double[] dq, String basename) {
      this.makePESData = makePESData;
      this.mm = mm;
      this.dq = dq;
      this.basename = basename;
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
         qcpack = new QuantChem(makePESData.getQchemTypes(level));
         qcpack.setBasename(basename);
         InputMaker inputMaker = qcpack.getInputMaker();
         inputMaker.setOptions(makePESData.getQchemInputs(level));
         inputMaker.setMolecule(currentMol);
         //inputMaker.setResource(resource);
         //inputMaker.makeInputFile();
      }catch(Exception e){
         // This exception never happens as it is already checked during setup.
      }
      
      // Generate the input
      super.preProcess();

      if(makePESData.isDryRun()){
         return 1;
      }else{
         return 0;         
      }

   }

   protected int postProcess() {
      
      if(makePESData.isDryRun()) return 0;

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
      
      if(makePESData.getRunType().equals("QFF") && makePESData.getNdifftype().equals("HESS") &&
            molecule.getElectronicData().getHessian() == null){
         System.out.println(header+" Hessian is not found in "+basename);
         System.out.println(header+" Error termination.");
         return -1;
      }

      if(makePESData.getRunType().equals("QFF") && makePESData.getNdifftype().equals("GRAD") &&
            molecule.getElectronicData().getGradient() == null){
         System.out.println(header+" Gradient is not found in "+basename);
         System.out.println(header+" Error termination.");
         return -1;
      }

      if(makePESData.isRemoveFiles()){
         Exec exec = qcpack.getExec();
         exec.removeFiles();
      }
      return 0;

   }

}
