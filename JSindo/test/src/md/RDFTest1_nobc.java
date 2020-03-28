package md;

import java.io.*;

/**
 * An example to calculate RDF for a non-PBC system. Note that the psf/dcd aren't 
 * included in the package so that this example doesn't run.
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 4.0
 */
public class RDFTest1_nobc {
   
   
   public static void main(String[] args) {
      SystemMD si = new SystemMD();

      PSFReader psfr = new PSFReader("../nobc25/snapshot_100.psf");
      psfr.setSystemMD(si);
      try{
         psfr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      DCDReader dcdr = new DCDReader();
      try {
         si.setTrajectory(dcdr.read("../nobc25/step5_nvt.dcd"));
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      si.setFrameLast();

      int[] oxygen = new int[si.getNumOfAtom()/3];
      int n=0;
      for(int i=0; i<si.getNumOfAtom(); i++) {
         if (si.getAtom(i).getLabel().equals("OH2")) {
            oxygen[n] = i;
            n++;
         }
      }
      
      double rsph = 25.0/sys.Constants.Bohr2Angs;
      double volume = 4.0/3.0*Math.PI*rsph*rsph*rsph;
      
      
      RadialDistribution rdf = new RadialDistribution();
      
      double rmin=0.0;
      double rmax=20.0;
      double dr=0.1;
      
      rdf.setSystem(si);
      rdf.setCenterIndex(oxygen);
      rdf.setRange(rmin, rmax, dr);
      rdf.setFrame(0, -1);
      rdf.setVolume(volume);
      double[] rdff = rdf.calcRDF();
      
      for(n=0; n<rdff.length; n++) {
         double rr = (rmin + dr*n)*sys.Constants.Bohr2Angs;
         System.out.printf("%8.4f  %12.6f \n", rr, rdff[n]);
      }
   }

}
