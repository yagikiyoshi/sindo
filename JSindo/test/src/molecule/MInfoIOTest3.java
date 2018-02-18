package molecule;

import java.io.IOException;

/*
 * Load a portion of data from the file and print.
 * 
 */

public class MInfoIOTest3 {

   public static void main(String[] args){

      MInfoIO minfo = new MInfoIO();
      minfo.unsetAllData();
      minfo.setAtomData(true);
      minfo.setElecData(true);
      minfo.setVibData(true);
      
      try{
         minfo.loadMOL("test/molecule/br.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      minfo.printAtoms();
      //minfo.printElecStruct();
      minfo.printVibration();

   }

}
