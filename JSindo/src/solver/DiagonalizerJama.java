package solver;

import Jama.*;

/**
 * (Package private) Implements Diagonalizer for JAMA-1.0.2 <br> 
 * JAMA is available from:  http://math.nist.gov/javanumerics/jama/
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class DiagonalizerJama implements Diagonalizer {
   
   private Matrix matrix;
   private EigenvalueDecomposition eig;
   
   /**
    * Constructor is package private. Use Solver to construct from a public domain.
    */
   DiagonalizerJama(){
      
   }
   
   public void diag(double[][] mat){
      matrix = new Matrix(mat);
      eig = matrix.eig();
   }
   public double[] getEigenValue(){
      double[][] valueMat = eig.getD().getArray();
      int size = valueMat.length;
      double[] value = new double[size];
      for(int i=0; i<size; i++){
         value[i] = valueMat[i][i];
      }
      return value;
   }
   public double[][] getVector(){
      return eig.getV().getArray();
   }
   public double[][] getTransposedVector(){
      return eig.getV().transpose().getArray();
   }

}
