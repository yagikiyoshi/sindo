package vibration;

import java.io.*;

import molecule.MInfoIO;
import molecule.Molecule;
import molecule.VibUtil;
import qchem.*;

public class LocalCoordTest1 {
   
   public static void main(String[] args){
      
      if(args.length<1){
         System.out.println("USAGE: java LocalCoordTest1 aaa");
         System.out.println("          aaa ... basename of the minfo file.");
         System.exit(-1);
      }

      MInfoIO minfo = new MInfoIO();
      Molecule molecule = null;
      try {
         molecule = minfo.loadMOL(args[0]+".minfo");
      } catch (FileNotFoundException e1) {
         e1.printStackTrace();
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      
      VibUtil vibCoord = new VibUtil();
      vibCoord.appendMolecule(molecule);
      LocalCoord lco = new LocalCoord();
      lco.setBoys();
      lco.setEthresh(800);
      vibCoord.calcLocalModes(lco);

      WriteGaussian wgau = new WriteGaussian();
      wgau.writeFreqOutput(args[0]+"-boys.out", molecule);
      
      try {
         minfo.dumpMOL(args[0]+"-boys.minfo");
      } catch (IOException e) {
         // Do nothing
      }

      /*
      lco.setPipekMezey();
      lco.setEthresh(200);
      vibCoord.calcLocalModes(lco);
      wgau.writeFreqOutput(args[0]+"-pm.out", molecule);
      try {
         minfo.dumpMOL(args[0]+"-pm.minfo");
      } catch (IOException e) {
         // Do nothing
      }
      */
      
   }

}
