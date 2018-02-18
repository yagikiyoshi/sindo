/**
 * Provides the classes to interface with solver libraries such as JAMA. <br>
 * 
 * The package is intended for, 
 * <blockquote><ol>
 * <li>An easy access or switch to the linear algebra routines</li>
 * <li>Matrix operations, e.g., multiplication, summation, etc.</li>
 * <li>Diagonalization</li>
 * </ol></blockquote>
 * -- Example code -- <br>
 * <blockquote><pre>
 *    Diagonalizer solver = Solver.getDiagonalizer(Solver.JAMA);
 *    solver.appendMatrix(mat);
 *    solver.diag();
 *    double[] Ene = solver.getEigenValue();
 *    double[][] basis = solver.getVector();
 * </pre></blockquote>
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
package solver;
