package md;

import java.io.*;

public class ClusterTest1 {

   public static void main(String[] args){
      
      SystemMD si = new SystemMD();
      PDBReader pdbr = new PDBReader("test/md/si_mini.pdb");
      pdbr.setSystemMD(si);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      PSFReader psfr = new PSFReader("test/md/si.psf");
      int[][] bondPair = null;
      try {
         bondPair = psfr.getBondPairs();
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      
      Cluster cluster = new Cluster();
      cluster.setSystemMD(si);
      cluster.setBondPairs(bondPair);
      
      int[] atomid = new int[6];
      atomid[0] =  3;
      atomid[1] = 10;
      atomid[2] = 11;
      atomid[3] = 12;
      atomid[4] = 13;
      atomid[5] = 14;
      
      //cluster.setTermHydrogen(false);
      SystemMD amide = cluster.getCluster(atomid);
      
      PDBWriter pdbw = new PDBWriter();
      
      System.out.println("Num of atom = "+amide.getNumOfAtom());
      pdbw.print(amide);
      
      try {
         pdbw.write("test/md/cluster.pdb", amide);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         System.exit(-1);
      }
      
   }
}
