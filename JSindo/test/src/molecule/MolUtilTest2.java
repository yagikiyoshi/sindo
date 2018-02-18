package molecule;

import java.io.*;

public class MolUtilTest2 {

   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/molecule/water_trimer2.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule w3 = minfo.getMolecule();

      MolUtil mutil = new MolUtil();
      mutil.appendMolecule(w3);
      
      int[] vibdomain = {0,2};
      Molecule w3_copy = mutil.copyMolecule(true, true, true, vibdomain);

      // The following lines don't affect the result because of deep copy 
      double[] hessian = w3.getElectronicData().getHessian();
      hessian[0] = -3333.0;
      
      double[] omegaV = w3.getVibrationalData(0).getOmegaV();
      omegaV[0] = 10000.0;
      
      //-----------------------------------------------------------------
      
      minfo.appendMolecule(w3_copy);
      minfo.printAtoms();
      minfo.printElecStruct();
      minfo.printVibration();
      
   }
}
