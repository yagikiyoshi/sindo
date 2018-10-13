
import java.io.*;

//import qchem.WriteGaussian;
import molecule.MInfoIO;
import molecule.Molecule;
import molecule.VibUtil;
import sys.*;
import vibration.CoordProperty;
import vibration.CoordinateData;

public class OcVSCF {

   public static void main(String[] args){
      
      if(args.length<1){
         System.out.println("USAGE: java OcVSCF aaa [bbb]");
         System.out.println("          aaa ... basename of the minfo file.");
         System.out.println("          bbb ... filename of the transformation matrix (default=u1.dat).");
         System.exit(-1);
      }
      MInfoIO minfo = new MInfoIO();
      Molecule molecule = null;
      try {
         molecule = minfo.loadMOL(args[0]+".minfo");
      } catch (FileNotFoundException e1) {
         e1.printStackTrace();
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      
      VibUtil vutil = new VibUtil();
      vutil.appendMolecule(molecule);
      String u1dat=null;
      if(args.length == 1) {
         u1dat="u1.dat";
      }else if(args.length == 2){
         u1dat=args[1];
      }
      CoordinateData cdata = vutil.calcOptimizedModes(u1dat);
      
//      int Nfree = cdata.Nfree;
//      double[] omega = cdata.getOmegaV();
//
//      CoordProperty cprop = new CoordProperty(cdata);
//      cprop.calcProperties();
//      double[] rdmass = cprop.getReducedMass();
//      double[] dr = cprop.getVariantOfVibration();
      
//     for(int i=0; i<Nfree; i++){
//         System.out.printf("%5d %12.4f %12.4f %12.4f \n", (i+1), omega[i],rdmass[i]*Constants.Emu2Amu, dr[i]);
//      }
//      for(int i=1; i<Nfree; i++){
//         for(int j=0; j<i; j++){
//            double pij = cprop.getPij(i, j);
//            System.out.printf("%5d %5d %12.4f \n",(i+1),(j+1),pij);
//         }
//      }
      
      try {
         minfo.dumpMOL(args[0]+"_ocvscf.minfo");
      } catch (IOException e) {
         // Do nothing
      }

   }
}
