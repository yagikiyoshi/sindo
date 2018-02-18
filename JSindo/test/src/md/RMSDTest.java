package md;

import java.io.*;
import sys.Constants;

public class RMSDTest {

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
      
      /*
      Residue po4 = sys.getResidue(0);
      int[] atomlist = new int[po4.getNumOfAtom()];
      for(int i=0; i<atomlist.length; i++){
         atomlist[i] = po4.getAtom(i).getID()-1;
      }
      */
      
      int[] atomlist = new int[5];
      atomlist[0] = 0;
      atomlist[1] = 1;
      atomlist[2] = 3;
      atomlist[3] = 5;
      atomlist[4] = 6;
      
      sys.setFrame(0);
      RMSD rmsd = new RMSD();
      rmsd.setReference(atomlist,sys);
      double[] rmsdTraj = rmsd.calcTraj(atomlist, sys);
      for(int i=0; i<rmsdTraj.length; i++){
         System.out.printf("%4d %12.4f \n",i,rmsdTraj[i]*Constants.Bohr2Angs);         
      }
      
      DCDWriter wdcd = new DCDWriter();
      try{
         wdcd.write("test/md/h2po4_fit.dcd", sys);
      }catch(IOException e){
         e.printStackTrace();
      }


   }
   

}
