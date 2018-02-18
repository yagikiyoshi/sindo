package molecule;

import java.io.IOException;
import vibration.*;
import sys.*;

/*
 * 
 * Localized modes of water trimer.
    1     179.6282       4.5524      41.9987       0.3268 
    2     181.0186       1.1455      86.5784       1.6939 
    3     184.8434       5.3127      36.9953       0.5383 
    4     188.1821       4.7040      51.3933       0.1981 
    5     208.4061       1.0944      37.5762       3.3000 
    6     211.3971       1.1436     121.1720       1.5592 
    7     330.8119       1.0771      11.3695       1.0738 
    8     333.6670       1.0960      41.9489       2.1337 
    9     445.0077       1.0543     144.0745       0.4023 
   10     557.9497       1.0507     271.3904       3.5662 
   11     627.2028       1.0657     445.5382       3.4651 
   12     853.7933       1.0450      14.4916       1.9194 
   13    1797.9740       1.0770      82.2419       3.0722 
   14    1798.1342       1.0765      79.5972       3.0973 
   15    1800.2400       1.0760      75.7718       3.3128 
   16    4010.4314       1.0548     236.5234      65.0620 
   17    4011.6732       1.0546     236.8773      63.9212 
   18    4015.7077       1.0543     219.4637      62.3396 
   19    4175.7198       1.0738     111.8485      48.3920 
   20    4178.8595       1.0737     108.7403      48.4744 
   21    4181.6343       1.0743     107.4215      48.4287 

    1      98.8287       4.7351       3.4989       0.2731 
    2      97.9768       5.4333       5.8158       0.0427 
    3     143.3832       9.7111      14.2675       0.1341 
    4     218.5757       1.0635     101.2010       2.9328 
    5     386.1047       1.0742     116.7454       0.5936 
    6     680.9164       1.0381     192.8060       2.5425 
    7    1797.1365       1.0765      75.1574       3.2267 
    8    4010.5363       1.0548     237.1817      65.1343 
    9    4178.7193       1.0737     107.9239      47.7611 
   10      51.5791       4.8344       1.0026       0.1908 
   11     124.6168       5.3377       3.9306       0.0202 
   12     147.8247       8.3727       7.4608       0.0449 
   13     256.5422       1.0723     111.9371       3.0545 
   14     384.6739       1.0839     118.6563       0.7496 
   15     692.9708       1.0371     193.7245       2.4449 
   16    1797.0739       1.0769      77.4127       3.2299 
   17    4011.7818       1.0547     237.4544      64.4618 
   18    4175.5737       1.0738     112.6768      47.9565 
   19      89.1443       4.3227       5.6979       0.3101 
   20     102.6807       5.3611       5.8503       0.0270 
   21     141.9247       9.2927      12.8727       0.1606 
   22     221.7262       1.0872      99.9576       3.1149 
   23     395.3430       1.0768     121.9674       0.8589 
   24     639.6753       1.0433     198.4857       2.8951 
   25    1798.9062       1.0760      73.0387       3.2985 
   26    4015.8211       1.0544     219.9437      63.1087 
   27    4181.4979       1.0742     107.9305      48.1341 
 */

public class VibUtilTest4 {

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
      
      LocalCoord lco = new LocalCoord();
      double eth = 100.0;
      lco.setEthresh(eth);
      lco.setMaxIteration(100);
      lco.setBoys();

      VibUtil vutil = new VibUtil(w3);
      vutil.calcLocalModes(lco);
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
      vutil.calcLocalModes(lco);
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
         minfo.dumpMOL("test/molecule/water_trimer4.minfo");
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
}
