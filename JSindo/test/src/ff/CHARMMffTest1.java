package ff;

import java.io.IOException;

public class CHARMMffTest1 {

   public static void main(String[] args){
      CHARMMff cff = new CHARMMff();
      try{
         cff.readParameter("test/md/par_all36_prot_lipid.prm");
         cff.readParameter("test/md/par_test.prm", true);
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      cff.printParameter();
      System.out.println();
      System.exit(0);
      
      FuncBond bondf = cff.getBond("CST", "OST");
      String[] atom = bondf.getAtomType();
      double kb = bondf.getKb();
      double b0 = bondf.getB0();
      System.out.printf("%-5s ",atom[0]);
      System.out.printf("%-5s ",atom[1]);
      System.out.printf("%-10.5f ",kb);
      System.out.printf("%-10.5f ",b0);
      System.out.println();
      
   }
}
