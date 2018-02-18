package md;

import java.io.*;

public class DCDReaderTest1 {

   public static void main(String[] args){
      SystemMD sys = new SystemMD();
      
      PSFReader psfr = new PSFReader("test/md/h2po4.psf");
      psfr.setSystemMD(sys);
      try{
         psfr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      System.out.print("Reading prod.dcd...");
      DCDReader rdcd = new DCDReader();
      try{
         sys.setTrajectory(rdcd.read("test/md/h2po4_prod.dcd"));
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      sys.setFrame(0);
      System.out.println("Done!");
      
      Trajectory traj = sys.getTrajectory();
      double[][] xyz = traj.getCoordinates(99);
      System.out.printf("%12.6f %12.6f %12.6f \n",xyz[0][0],xyz[0][1],xyz[0][2]);
   }

}
