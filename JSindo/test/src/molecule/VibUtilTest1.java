package molecule;

import java.io.IOException;

import sys.Constants;

/*
 * Read data from minfo, carry out normal mode analysis, and print the results.

[ Atomic Data ]
3
   O,     8,      15.9949,       0.00000000,       0.00000000,       0.12554367
   H,     1,       1.0078,       0.00000000,       1.42351138,      -0.99623455
   H,     1,       1.0078,      -0.00000000,      -1.42351138,      -0.99623455

Mode     Omega        Red.Mass     Infrared       Raman     Normal displacement vector
          cm-1          amu        km mol-1   Angs**4 amu-1
  0    1652.0977       1.0826      64.5257      4.2736     -0.000000  -0.000000   0.271501  -0.000000  -0.413120  -0.540810   0.000000   0.413120  -0.540810
  1    3855.3939       1.0452       5.7366     68.4190     -0.000000   0.000000  -0.195448  -0.000000  -0.573874   0.389318  -0.000000   0.573874   0.389318
  2    3975.7344       1.0809      55.2088     24.7823     -0.000000  -0.268573  -0.000000  -0.000000   0.534979  -0.421583   0.000000   0.534979   0.421583
  
*/

public class VibUtilTest1 {
   
   public static void main(String[] args){

      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/molecule/h2o-freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule h2o = minfo.getMolecule();

      VibUtil vutil = new VibUtil(h2o);
      vutil.calcNormalModes(false);
      VibrationalData vdata = h2o.getVibrationalData();
      double[] omega = vdata.getOmegaV();
      double[][] vec = vdata.getVibVector();
      double[] IRintensity = vutil.calcIRintensity();
      double[] RamanActivity = vutil.calcRamanActivity();
      double[] ReducedMass = vutil.calcReducedMass();
      
      minfo.printAtoms();
      System.out.println("Mode     Omega        Red.Mass     Infrared       Raman     Normal displacement vector");
      System.out.println("          cm-1          amu        km mol-1   Angs**4 amu-1");
      for(int i=0; i<IRintensity.length; i++){
         System.out.printf("%3d %12.4f     %8.4f     %8.4f    %8.4f   ",i,omega[i],ReducedMass[i]*Constants.Emu2Amu,IRintensity[i],RamanActivity[i]);
         for(int j=0; j<vec[i].length; j++){
            System.out.printf("  %9.6f", vec[i][j]);
         }
         System.out.println();
      }

   }

}
