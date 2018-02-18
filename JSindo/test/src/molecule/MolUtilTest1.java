package molecule;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MolUtilTest1 {
   
   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try {
         minfo.loadMOL("test/molecule/h2co-freq.minfo");
      } catch (FileNotFoundException e1) {
         e1.printStackTrace();
      } catch (IOException e1) {
         e1.printStackTrace();
      }

      minfo.setVersion(2);
      minfo.printAtoms();

      double rCH = minfo.getBondLength(1, 3);
      double aHCO = minfo.getBondAngle(3, 1, 2);
      double dHCOH = minfo.getDihedralAngle(3, 1, 2, 4);
      
      System.out.printf("R(CH)  = %12.4f [bohr]\n", rCH);
      System.out.printf("A(HCO) = %12.2f [degree]\n", aHCO);
      System.out.printf("D(HCOH)= %12.2f [degree]\n", dHCOH);
      
   }
}
