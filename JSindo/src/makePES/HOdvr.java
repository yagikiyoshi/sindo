package makePES;

import solver.Diagonalizer;
import solver.Solver;
import sys.Constants;

/**
 * DVR grid for harmonic oscillator wavefunction
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class HOdvr {
   
   /**
    * Grid points in a.u.
    */
   private double[] gridPoints;
   /**
    * DVR basis functions
    */
   private double[][] DVRbasis;

   /**
    * 
    * @param n The number of grid points
    * @param omega Harmonic frequency in cm-1
    */
   public HOdvr(int n, double omega){
      double[][] mat = new double[n][n];
      //for(int i=0; i<n; i++){
      //   for(int j=0; j<n; j++){
      //      mat[j][i] = 0.0d;
      //   }
      //}
      for(int i=1; i<n; i++){
         mat[i-1][i] = Math.sqrt(((double)i)/2.0);
         mat[i][i-1] = mat[i-1][i];
      }
      Diagonalizer solver = Solver.getDiagonalizer();
      solver.diag(mat);
      gridPoints = solver.getEigenValue();
      DVRbasis = solver.getVector();

      double rsomg = 1.0d/Math.sqrt(omega/Constants.Hartree2wvn);
      for(int i=0; i<n; i++){
         gridPoints[i] = gridPoints[i]*rsomg;
      }
      for(int i=0; i<n; i++){
         if(DVRbasis[0][i]<0.0d){
            for(int j=0; j<n; j++){
               DVRbasis[j][i]=-DVRbasis[j][i];
            }
         }
      }
   }
   
   /**
    * Get grid points
    * @return Grid points in a.u.
    */
   public double[] getGridPoints(){
      return gridPoints;
   }
   
   /**
    * Get DVR basis functions
    * @return DVR basis functions
    */
   public double[][] getHODVRbasis(){
      return DVRbasis;
   }
}
