package molecule;

import java.io.IOException;
import sys.*;

/*
 * 
 * Harmonic vibrational analysis of water trimer.

[ Atomic Data ]
9
   O,     8,      15.9949,      -2.59193769,      -1.78276437,      -0.16165897
   H,     1,       1.0078,      -2.39320658,       0.00917417,      -0.10591014
   H,     1,       1.0078,      -3.62113654,      -2.18250998,       1.24497958
   O,     8,      15.9949,      -0.25780097,       3.11943624,       0.22317176
   H,     1,       1.0078,      -0.02579728,       4.18303969,      -1.19627294
   H,     1,       1.0078,       1.19591328,       2.05201650,       0.25017468
   O,     8,      15.9949,       2.84619193,      -1.32244977,      -0.13185311
   H,     1,       1.0078,       1.21706618,      -2.08553127,      -0.25723546
   H,     1,       1.0078,       3.68345033,      -2.20190430,       1.18061631

    1     157.4819       1.4276     150.6241       0.5616 
    2     169.6784       3.1787      96.8143       0.2049 
    3     181.9693       2.0167      24.5988       0.5912 
    4     186.4250       1.4852      58.7752       1.1145 
    5     210.7209       6.3622       5.0681       0.3891 
    6     238.4822       1.0636      39.8335       4.7551 
    7     325.0680       1.0905      36.6789       0.6107 
    8     339.2654       1.0824      16.6395       2.5968 
    9     445.0077       1.0543     144.0745       0.4023 
   10     550.0632       1.0452     250.4751       3.8395 
   11     634.1306       1.0714     466.4534       3.1918 
   12     853.7933       1.0450      14.4916       1.9194 
   13    1789.6679       1.0771      73.5971       4.3210 
   14    1798.0722       1.0775      94.6554       2.9117 
   15    1808.5593       1.0750      69.3585       2.2496 
   16    3981.8779       1.0561       2.4019     139.5086 
   17    4026.7000       1.0540     355.7049      28.2524 
   18    4029.0602       1.0537     334.7577      23.5618 
   19    4175.6734       1.0739     131.6270      40.8320 
   20    4178.8606       1.0738      96.8575      52.7469 
   21    4181.6795       1.0742      99.5257      51.7162 

    1      44.7328       4.9992       2.3571       0.1903 
    2     105.1634       5.7742       5.9530       0.0400 
    3     161.5441       7.4642       3.6608       0.0546 
    4     220.3266       1.0737     112.8124       3.0979 
    5     386.1047       1.0742     116.7454       0.5936 
    6     680.9164       1.0381     192.8060       2.5425 
    7    1797.1365       1.0765      75.1574       3.2267 
    8    4010.5363       1.0548     237.1817      65.1343 
    9    4178.7193       1.0737     107.9239      47.7611 
   10      41.2468       4.8660       2.3431       0.1837 
   11     107.8996       5.8810       6.7173       0.0314 
   12     163.3960       7.2417       3.3336       0.0407 
   13     256.5422       1.0723     111.9371       3.0545 
   14     384.6739       1.0839     118.6563       0.7496 
   15     692.9708       1.0371     193.7245       2.4449 
   16    1797.0739       1.0769      77.4127       3.2299 
   17    4011.7818       1.0547     237.4544      64.4618 
   18    4175.5737       1.0738     112.6768      47.9565 
   19      41.6334       4.5249       3.3284       0.2004 
   20     106.0985       5.5832       6.8155       0.0284 
   21     159.3017       7.6923       7.9142       0.1658 
   22     222.3239       1.0927     106.3202       3.2181 
   23     395.3430       1.0768     121.9674       0.8589 
   24     639.6753       1.0433     198.4857       2.8951 
   25    1798.9062       1.0760      73.0387       3.2985 
   26    4015.8211       1.0544     219.9437      63.1087 
   27    4181.4979       1.0742     107.9305      48.1341 

 */

public class VibUtilTest3 {

   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/molecule/water_trimer.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule w3 = minfo.getMolecule();
      minfo.printAtoms();
      
      VibUtil vutil = new VibUtil(w3);
      vutil.calcNormalModes(false);
      double[] omega  = vutil.getOmega();
      double[] rdmass = vutil.calcReducedMass();      
      double[] ir     = vutil.calcIRintensity();
      double[] raman  = vutil.calcRamanActivity();
      for(int i=0; i<omega.length; i++){
         System.out.printf("%5d %12.4f %12.4f %12.4f %12.4f \n", 
               (i+1), omega[i],rdmass[i]*Constants.Emu2Amu,ir[i],raman[i]);
      }
      System.out.println();
      
      int[][] atomIndex = new int[3][3];
      atomIndex[0][0] = 0;
      atomIndex[0][1] = 1;
      atomIndex[0][2] = 2;
      
      atomIndex[1][0] = 3;
      atomIndex[1][1] = 4;
      atomIndex[1][2] = 5;

      atomIndex[2][0] = 6;
      atomIndex[2][1] = 7;
      atomIndex[2][2] = 8;
      
      vutil.setDomain(atomIndex);
      vutil.calcNormalModes(false);
      omega  = vutil.getOmega();
      rdmass = vutil.calcReducedMass();      
      ir     = vutil.calcIRintensity();
      raman  = vutil.calcRamanActivity();
      for(int i=0; i<omega.length; i++){
         System.out.printf("%5d %12.4f %12.4f %12.4f %12.4f \n", 
               (i+1), omega[i],rdmass[i]*Constants.Emu2Amu,ir[i],raman[i]);
      }
      System.out.println();

      for(int n=0; n<atomIndex.length; n++){
         for(int i=0; i<w3.getVibrationalData(n).Nfree; i++){
            System.out.println("(Domain, Mode) = (" + n + ", " + i + "): Mode=" + vutil.getModeIndex(n, i));
         }
      }
      System.out.println();
      
      try {
         minfo.dumpMOL("test/molecule/water_trimer2.minfo");
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      int[] selectedModes = {8,7,6, 26,25,24};
      vutil.reduceVibModes(selectedModes);

      omega  = vutil.getOmega();
      rdmass = vutil.calcReducedMass();      
      ir     = vutil.calcIRintensity();
      raman  = vutil.calcRamanActivity();
      for(int i=0; i<omega.length; i++){
         System.out.printf("%5d %12.4f %12.4f %12.4f %12.4f \n", 
               (i+1), omega[i],rdmass[i]*Constants.Emu2Amu,ir[i],raman[i]);
      }
      System.out.println();

      try {
         minfo.dumpMOL("test/molecule/water_trimer3.minfo");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
