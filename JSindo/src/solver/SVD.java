package solver;

/**
 * <p>Provides a class for singular value decomposition, A = USV' </p>
 * 
 * The instance is generated through Solver as,  <br>
 * <blockquote><pre>
 *    SingularValueDecomposition solver = Solver.getSVD();
 * </pre></blockquote>
 * Then, append the matrix and carry out SVD,
 * <blockquote><pre>
 *    solver.calc(double[][] A)
 * </pre></blockquote>
 * The singular values, U and V matrices are obtained by,
 * <blockquote><pre>
 *    double[] s = solver.getS();
 *    double[][] U = solver.getU();
 *    double[][] V = solver.getV();
 * </pre></blockquote>
 * Note that A = UsV' (V': transpose of V).
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */

public interface SVD {

   /**
    * Carry out SVD of a given matrix (A = USV')
    * @param matrix (m x n) matrix for SVD.
    */
   public void calc(double[][] matrix);

   /**
    * Returns the singular values.
    * @return Singular values.
    */
   public double[] getS();
   
   /**
    * Returns the left hand matrix, U.
    * @return U matrix
    */
   public double[][] getU();
   
   /**
    * Returns the right hand matrix, V.
    * @return V matrix
    */
   public double[][] getV();

   /**
    * Returns a transpose of the right hand matrix, V'.
    * @return V'
    */
   public double[][] getVt();
   
   /**
    * Returns a transpose of the left hand matrix, U'.
    * @return U'
    */
   public double[][] getUt();
   
   /**
    * Returns V * U'
    * @return VU'
    */
   public double[][] getVUt();
   
   /**
    * Returns U * V'
    * @return UV'
    */
   public double[][] getUVt();
}
