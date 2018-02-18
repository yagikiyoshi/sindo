package solver;

/**
 * <p> Provides a class for matrix diagonalization, E = U'HU </p>
 * 
 * The instance is generated through Solver as,  <br>
 * <blockquote><pre>
 *    Diagonalizer solver = Solver.getDiagonalizer();
 * </pre></blockquote>
 * Then, append the matrix and diagonalize, 
 * <blockquote><pre>
 *    solver.diag(double[][] H);
 * </pre></blockquote>
 * The eigenvalues and eigenvectors are obtained by,
 * <blockquote><pre>
 *    double[] E = solver.getEigenValue();
 *    double[][] U = solver.getVector();
 * </pre></blockquote>
 * 
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */

public interface Diagonalizer {
   
   /**
    * Diagonalize the matrix (E = Lt H L )
    * @param matrix matrix to be diagonalized
    */
   public void diag(double[][] matrix);
   
   /**
    * Gets eigenvalues (E)
    * @return Vector of eigenvalues
    */
   public double[] getEigenValue();
   
   /**
    * Gets eigenvectors (L)
    * @return Matrix of eigenvectors
    */
   public double[][] getVector();
   
   /**
    * Gets eigenvectors (Lt)
    * @return Transpose of eigenvectors
    */
   public double[][] getTransposedVector();
   
}
