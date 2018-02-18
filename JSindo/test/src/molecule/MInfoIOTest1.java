package molecule;

import java.io.IOException;

/*
 * Load the data of H2O from the minfo file and print the data.
 */

public class MInfoIOTest1 {

   public static void main(String[] args){

      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/molecule/h2o-freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      minfo.setVersion(2);
      minfo.printAtoms();
      minfo.printElecStruct();
      minfo.printVibration();
      
   }

}
