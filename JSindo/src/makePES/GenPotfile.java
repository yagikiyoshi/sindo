package makePES;

import molecule.*;

/**
 * Generate a file containing the PES data.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 */
public class GenPotfile extends GenGridfile {

   public GenPotfile(String title, GridFileName gfilename){
      nData = 1;
      ext = ".pot";
      label = new String[1];
      label[0] = "Energy";
      this.title = title;
      this.setGridFileName(gfilename);
      this.setOrigin();
   }

   protected double[] getProperty(ElectronicData edata) {
      double[] energy = {edata.getEnergy()};
      return energy;
   }

}
