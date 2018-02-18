package makePES;

import molecule.ElectronicData;

/**
 * Generate a file containing the DMS data.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 */
public class GenDipolefile extends GenGridfile {

   public GenDipolefile(String title, GridFileName gfilename){
      nData = 3;
      ext = ".dipole";
      label = new String[3];
      label[0] = "X";
      label[1] = "Y";
      label[2] = "Z";
      this.title = title;
      this.setGridFileName(gfilename);
      this.setOrigin();
   }

   protected double[] getProperty(ElectronicData edata) {
      double[] dipole = edata.getDipole();
      return dipole;
   }

}
