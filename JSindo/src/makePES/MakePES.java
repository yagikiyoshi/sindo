package makePES;

import molecule.*;

/**
 * Make potential energy surface
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class MakePES {
   
   private PESInputData inputData;
   
   /**
    * Append the data for PES generation
    * @param makePESData input data, etc.
    */
   public void appendPESData(PESInputData makePESData){
      this.inputData = makePESData;
   }
   
   /**
    * Main module of PES generation
    */
   public void genPES(){
      
      Molecule molecule = inputData.getMolecule();
      int nd = molecule.getNumOfVibrationalData();
      if(nd > 1){
         VibUtil vutil = new VibUtil(molecule);
         vutil.combineAllVibData();
      }
      VibTransformer transform = new VibTransformer(molecule);
      inputData.setTransform(transform);
      
      String type = inputData.getRunType();
      if(type.equals("QFF")){
         MakeQFF mkqff = new MakeQFF(inputData);
         mkqff.runMkQFF();
      }
      if(type.equalsIgnoreCase("GRID")){
         MakeGrid mkgrid = new MakeGrid(inputData);
         mkgrid.runMakeGrid();
      }
      if(type.equalsIgnoreCase("HYBRID")){
         inputData.setRunType("QFF");
         MakeQFF mkqff = new MakeQFF(inputData);
         mkqff.runMkQFF();
         inputData.setRunType("HYBRID");
         MakeGrid mkgrid = new MakeGrid(inputData);
         mkgrid.runMakeGrid();         
      }

   }
   
}
