package solver;

/**
 * Provides the classes for matrix operations. <br>
 * 
 * <p> The instance is generated through Solver as,  <br>
 * <blockquote><pre>
 *    MatrixOperation operation = Solver.getMatrixOperation();
 * </pre></blockquote>
 * Then, the operation is done by,
 * <blockquote><pre>
 *    // define the matrices
 *    double[][] A = new double[nsize][nsize];
 *    A[0][0] = ...;
 *    double[][] B = new double[nsize][nsize];
 *    B[0][0] = ...;
 *    // C=A*B
 *    double[][] C = operation.multply(A,B);
 * </pre></blockquote>
 *  
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public interface MatrixOperation {
   
   /**
    * Sum of the two matrix A and B
    * @param Ma Matrix A
    * @param Mb Matrix B
    * @return C=A+B
    */
   double[][] plus(double[][] Ma, double[][] Mb);
   
   /**
    * Subtraction from matrix A to B
    * @param Ma Matrix A
    * @param Mb Matrix B
    * @return C=A-B
    */
   double[][] minus(double[][] Ma, double[][] Mb);

   /**
    * Multiplication of the element of A by a scalar s
    * @param s Scalar number s
    * @param Ma Matrix A
    * @return C=s*A
    */
   double [][] multiply(double s, double[][] Ma);

   /**
    * Multiplication of the matrix A by B
    * @param Ma Matrix A
    * @param Mb Matrix B
    * @return C=A*B
    */
   double[][] multiply(double[][] Ma, double[][] Mb);
   
   /** 
    * Trace of the matrix A
    * @param Ma Matrix A
    * @return tr(A)
    */
   double trace(double[][] Ma);

   /**
    * Transpose of the matrix A
    * @param Ma Matrix A
    * @return Cij = Aji
    */
   double[][] transpose(double[][] Ma);
   
   /**
    * Determinant of the matrix A
    * @param Ma Matrix A
    * @return Det(A)
    */
   double determinant(double[][] Ma);
   
}
