
import md.*;
import java.io.*;

public class ReduceDCD {
   public static void main(String[] args) {
      
      if(args.length != 4) {
         System.out.println("USAGE: java ExtractDCD aa.pdb aa.dcd bb.dcd nred");
         System.out.println("  aa.pdb ... pdb file");
         System.out.println("  aa.dcd ... existing dcd file");
         System.out.println("  aa.pdb ... new dcd file");
         System.out.println("  nred   ... number of reduction");
         System.exit(0);
      }

      String fpdb = args[0];
      String fdcd_old = args[1];
      String fdcd_new = args[2];
      int nred  = Integer.parseInt(args[3]);
      
      SystemMD sys = new SystemMD();

      PDBReader pdbr = new PDBReader(fpdb);
      pdbr.setSystemMD(sys);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      System.out.print("Reading "+fdcd_old+" ...");
      DCDReader rdcd = new DCDReader();
      try{
         sys.setTrajectory(rdcd.read(fdcd_old));
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      sys.setFrame(0);
      System.out.println("Done!");

      Trajectory trj = sys.getTrajectory().reduce(nred);
      sys.setTrajectory(trj);

      DCDWriter wdcd = new DCDWriter();
      try{
         wdcd.write(fdcd_new, sys);
      }catch(IOException e){
         e.printStackTrace();
      }

      
   }
}
