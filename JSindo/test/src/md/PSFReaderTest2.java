package md;

import java.io.*;

public class PSFReaderTest2 {

   public static void main(String[] args){
      
      PSFReader psfr = new PSFReader("test/md/si.psf");
      int[][] bondPair = null;
      try {
         bondPair = psfr.getBondPairs();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      SystemMD si = new SystemMD();
      CRDReader crdr = new CRDReader("test/md/si_mini.crd");
      crdr.setSystemMD(si);
      try{
         crdr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      AtomMD[] atoms = si.getAtomALL();
      for(int n=0; n<bondPair.length; n++){
         int n1 = bondPair[n][0];
         int n2 = bondPair[n][1];
         System.out.printf(" %4d : %3d %3s - %3d %3s \n",n,n1+1,atoms[n1].getLabel(),n2+1,atoms[n2].getLabel());
      }
      
   }
}
