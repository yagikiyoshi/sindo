package makePES;

import molecule.*;
import java.util.*;

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
      
      ArrayList<InputDataQFF> qffData_array = inputData.getQFFInfoArray();
      for(int n=0; n<qffData_array.size() ; n++) {
         InputDataQFF qffData = qffData_array.get(n);
         InputDataQC  qcInfo  = inputData.getQCInfo(qffData.getQCindex());
         MakeQFF mkqff = new MakeQFF(inputData, qffData, qcInfo);
         mkqff.runMkQFF();
      }
      
      ArrayList<InputDataGrid> gridData_array = inputData.getGridInfoArray();
      for(int n=0; n<gridData_array.size(); n++) {
         InputDataGrid gridData = gridData_array.get(n);
         InputDataQC  qcInfo  = inputData.getQCInfo(gridData.getQCindex());
         MakeGrid mkgrid = new MakeGrid(inputData, gridData, qcInfo);
         mkgrid.runMakeGrid();         
         
      }

   }
   
}
