package vibration;

import java.io.IOException;

import sys.Constants;

import molecule.*;

/*
 * Read data from minfo, calculate the normal modes, and analyze the properties of modes.
 * 
    1    1658.9631       1.0819       1.4285 
    2    3751.7293       1.0458       1.4291 
    3    3853.0679       1.0801       1.4285 
 *
 */
public class CoordPropertyTest1 {

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
      double[] omega = coord.getOmegaV();
      Integer Nfree = omega.length;
      
      CoordProperty cprop = new CoordProperty(coord);
      double[] rdmass = cprop.getReducedMass();
      double[] dr = cprop.getVariantOfVibration();
      
      for(int i=0; i<Nfree; i++){
         System.out.printf("%5d %12.4f %12.4f %12.4f \n", (i+1), omega[i],rdmass[i]*Constants.Emu2Amu, dr[i]);
      }

   }
}
