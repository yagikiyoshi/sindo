package makePES;

import sys.Constants;

@SuppressWarnings("unused")
public class HOdvrTest {

   public static void main(String[] args){
      int n=7;
      HOdvr hodvr = new HOdvr(n,1451.5900d);
      double[][] DVRbasis = hodvr.getHODVRbasis();
      double[] gridPoints = hodvr.getGridPoints();

      for(int i=0; i<n; i++){
         //System.out.printf(" %8.4f : ", gridPoints[i]*Math.sqrt(Constants.Emu2Amu)*Constants.Bohr2Angs);
         System.out.printf(" %8.4f : ", gridPoints[i]);
         for(int j=0; j<n; j++){
            System.out.printf(" %8.4f", DVRbasis[j][i]);
         }
         System.out.println();
      }
   }
}
