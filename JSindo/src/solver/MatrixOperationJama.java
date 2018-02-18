package solver;

import Jama.*;

/**
 * (Package private) Implements MatrixOperation for JAMA-1.0.2 <br> 
 * JAMA is available from:  http://math.nist.gov/javanumerics/jama/
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */

public class MatrixOperationJama implements MatrixOperation {

   /**
    * Constructor is package private. Use Solver to construct from a public domain.
    */
   MatrixOperationJama(){
      
   }
   
   @Override
   public double[][] plus(double[][] Ma, double[][] Mb) {
      Matrix A = new Matrix(Ma);
      Matrix B = new Matrix(Mb);
      Matrix C = A.plus(B);
      return C.getArray();
   }

   @Override
   public double[][] minus(double[][] Ma, double[][] Mb) {
      Matrix A = new Matrix(Ma);
      Matrix B = new Matrix(Mb);
      Matrix C = A.minus(B);
      return C.getArray();
   }

   @Override
   public double[][] multiply(double s, double[][] Ma) {
      Matrix A = new Matrix(Ma);
      A.times(s);
      return A.getArray();
   }

   @Override
   public double[][] multiply(double[][] Ma, double[][] Mb) {
      Matrix A = new Matrix(Ma);
      Matrix B = new Matrix(Mb);
      Matrix C = A.times(B);
      return C.getArray();
   }

   @Override
   public double trace(double[][] Ma) {
      Matrix A = new Matrix(Ma);
      return A.trace();
   }

   @Override
   public double[][] transpose(double[][] Ma) {
      Matrix A = new Matrix(Ma);
      return A.transpose().getArray();
   }

   @Override
   public double determinant(double[][] Ma) {
      Matrix A = new Matrix(Ma);
      return A.det();
   }

}
