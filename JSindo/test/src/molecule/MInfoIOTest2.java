package molecule;

import java.io.IOException;

/*
 * Load a portion of data from the file and print.
 * 
 */

public class MInfoIOTest2 {

   public static void main(String[] args){

      MInfoIO minfo = new MInfoIO();
      minfo.unsetAllData();
      minfo.setAtomData(true);
      minfo.setElecData(false);
      minfo.setVibData(true);
      
      try{
         minfo.loadMOL("test/molecule/water_trimer.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      minfo.printAtoms();
      minfo.printElecStruct();
      minfo.printVibration();

   }

}
