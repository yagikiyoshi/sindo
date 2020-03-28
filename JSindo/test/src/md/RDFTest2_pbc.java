package md;

import java.io.*;

/**
 * An example to calculate RDF for a PBC system. Note that the psf/dcd aren't 
 * included in the package so that this example doesn't run.
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 4.0
 */
public class RDFTest2_pbc {
   
   
   public static void main(String[] args) {
      SystemMD si = new SystemMD();

      PSFReader psfr = new PSFReader("../pbc/solvate.psf");
      psfr.setSystemMD(si);
      try{
         psfr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      DCDReader dcdr = new DCDReader();
      try {
         si.setTrajectory(dcdr.read("../pbc/step4_nvt_wrap.dcd"));
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      si.setFrameLast();

      int[] oxygen = new int[si.getNumOfAtom()/3];
      int[] hydrogen = new int[si.getNumOfAtom()*2/3];
      int n=0, m=0;
      for(int i=0; i<si.getNumOfAtom(); i++) {
         if (si.getAtom(i).getLabel().equals("OH2")) {
            oxygen[n] = i;
            n++;
         }else {
            hydrogen[m] = i;
            m++;
         }
      }
            
      RadialDistribution rdf = new RadialDistribution();
      
      double rmin=0.0;
      double rmax=20.0;
      double dr=0.1;
      
      rdf.setSystem(si);
      rdf.setCenterIndex(oxygen);
      rdf.setRange(rmin, rmax, dr);
      rdf.setFrame(0, 10);
      double[] rdff = rdf.calcRDF();
      
      try {
         PrintWriter pw = new PrintWriter(new File("../pbc/gofrOO_my.dat"));
         for(n=0; n<rdff.length; n++) {
            double rr = (rmin + dr*n)*sys.Constants.Bohr2Angs;
            pw.printf("%8.4f  %12.6f \n", rr, rdff[n]);
         }
         pw.close();
         
      }catch (IOException e) {
         e.printStackTrace();
      }
      
      rdf.setCenterIndex(oxygen, hydrogen);
      rdff = rdf.calcRDF();
      try {
         PrintWriter pw = new PrintWriter(new File("../pbc/gofrOH_my.dat"));
         for(n=0; n<rdff.length; n++) {
            double rr = (rmin + dr*n)*sys.Constants.Bohr2Angs;
            pw.printf("%8.4f  %12.6f \n", rr, rdff[n]);
         }
         pw.close();
         
      }catch (IOException e) {
         e.printStackTrace();
      }
   }

}
