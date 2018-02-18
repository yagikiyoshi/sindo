package md;

import java.io.*;

public class DCDWriterTest1 {

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
      
      System.out.print("Reading h2po4_prod.dcd...");
      DCDReader rdcd = new DCDReader();
      try{
         sys.setTrajectory(rdcd.read("test/md/h2po4_prod.dcd"));
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      sys.setFrame(0);
      System.out.println("Done!");

      MDUtil mdutil = new MDUtil();
      for(int n=0; n<sys.getTrajectory().getNumOfFrames(); n++){
         sys.setFrame(n);
         mdutil.appendBox(sys.getBox());
         for(int i=0; i<sys.getNumOfResidue(); i++){
            mdutil.shiftToPBCImage(sys.getResidue(i));         
         }
      }

      Trajectory traj2 = sys.getTrajectory().reduce(10);      
      System.out.println("Trajectory is wrapped and reduced to "+traj2.getNumOfFrames()+" frames.");
      sys.setTrajectory(traj2);
      
      DCDWriter wdcd = new DCDWriter();
      try{
         wdcd.write("test/md/h2po4_wrap.dcd", sys);
      }catch(IOException e){
         e.printStackTrace();
      }
      
   }
}
