package molecule;

import java.io.IOException;

public class VibUtilTest2 {

   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/molecule/h2co-freq.minfo");
         minfo.setVersion(2);
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule h2co = minfo.getMolecule();

      VibUtil vutil = new VibUtil(h2co);
      int[] selectedModes = {5,4};
      vutil.reduceVibModes(selectedModes);
      
      minfo.printAtoms();
      minfo.printVibration();

      minfo.setElecData(false);
      try {
         minfo.dumpMOL("test/molecule/h2co-freq2.minfo");
      } catch (IOException e) {
         e.printStackTrace();
      }
      
   }
}
