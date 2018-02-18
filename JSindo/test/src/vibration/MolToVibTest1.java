package vibration;

import java.io.IOException;

import molecule.*;

public class MolToVibTest1 {

   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/vibration/h2o-freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
      }
      VibUtil vutil = new VibUtil(minfo.getMolecule());
      vutil.calcNormalModes(false);

      MolToVib m2v = new MolToVib();
      CoordinateData coord = m2v.getCoordinateData(minfo.getMolecule());
      coord.print();
      
   }
}
