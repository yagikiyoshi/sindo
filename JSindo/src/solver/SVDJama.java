package solver;

import Jama.*;

public class SVDJama implements SVD {

   private SingularValueDecomposition svd;

   /**
    * Constructor is package private. Use Solver to construct from a public domain.
    */
   SVDJama(){
      
   }
   
   @Override
   public void calc(double[][] matrix) {
      Matrix rr = new Matrix(matrix);
      svd = rr.svd();
   }

   @Override
   public double[] getS() {
      return svd.getSingularValues();
   }

   @Override
   public double[][] getU() {
      return svd.getU().getArray();
   }

   @Override
   public double[][] getV() {
      return svd.getV().getArray();
   }

   @Override
   public double[][] getVt() {
      return svd.getV().transpose().getArray();
   }

   @Override
   public double[][] getUt() {
      return svd.getU().transpose().getArray();
   }

   @Override
   public double[][] getVUt() {
      Matrix v = svd.getV();
      Matrix vut = v.times(svd.getU().transpose());
      return vut.getArray();
   }

   @Override
   public double[][] getUVt() {
      Matrix u = svd.getU();
      Matrix uvt = u.times(svd.getV().transpose());
      return uvt.getArray();
   }

}
