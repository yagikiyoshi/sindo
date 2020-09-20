
import md.*;
import sys.Constants;

import java.io.*;

public class GetCenterOfMass {

   public static void main(String[] args) {
      if(args.length == 0) {
         System.out.println("USAGE: java GetCenterOfMass aa.pdb [segname]");
         System.exit(0);
      }
      
      SystemMD sys = new SystemMD();
      
      PDBReader pdbr = new PDBReader(args[0]);
      pdbr.setSystemMD(sys);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      AtomList alist = null;
      if(args.length == 1) {
         alist = sys;
      } else if (args.length == 2) {
         alist = sys.getSegment(args[1]);
      }

      MDUtil mdutil = new MDUtil();
      double[] com = mdutil.getCenterOfMass(alist);
      int atomindex = 0;
      double xx = 10.0;
      for(int n=0; n<sys.getNumOfAtom(); n++) {
         double[] xyz = sys.getAtom(n).getXYZCoordinates();
         
         double rr = 0.0;
         for(int i=0; i<xyz.length; i++) {
            rr += (xyz[i]-com[i])*(xyz[i]-com[i]);
         }
         
         if(rr < xx) {
            atomindex = n;
            xx = rr;
         }
      }
      
      System.out.println("Center of Mass:");
      for(int n=0; n<com.length; n++) {
         System.out.printf("%12.3f ",com[n]*Constants.Bohr2Angs);
      }
      System.out.println();
      System.out.println("Nearby atom");
      System.out.printf("%8d",atomindex+1);
      System.out.printf("%8s",sys.getSegmentByAtom(atomindex).getName());
      Residue residue = sys.getResidueByAtom(atomindex);
      System.out.printf("%5s",residue.getName());
      System.out.printf("%4d",residue.getID());
      
      AtomMD atom = sys.getAtom(atomindex);
      System.out.printf("%5s",atom.getLabel());
      double[] xyz = atom.getXYZCoordinates();
      for(int n=0; n<xyz.length; n++) {
         System.out.printf("%12.3f ",xyz[n]*Constants.Bohr2Angs);
      }
      
      System.out.println();
      
      
   }
}
