package solver;

public class SingularValueDecompositionTest {

   public static void main(String[] args){
      int m1 = 4;
      int m2 = 3;
      double[][] A = new double[m1][];
      
      double[] A0 = { 17.023928, -18.313115, 5.044997};
      double[] A1 = { -1.550576,  14.443000, 8.485760};
      double[] A2 = {-13.157130,  -8.476017, 3.444236};
      double[] A3 = { 17.023928, -18.313115, 5.044997};
      
      A[0] = A0;
      A[1] = A1; 
      A[2] = A2;
      A[3] = A3;
      
      SVD svd = Solver.getSVD();
      svd.calc(A);
      
      double[] ss = svd.getS();
      double[][] uu = svd.getU();
      double[][] vv = svd.getVt();
      
      System.out.println("Singular Values.");
      for(int i=0; i<ss.length; i++){
         System.out.printf("%12.6f ",ss[i]);
      }
      System.out.println();
      System.out.println();

      System.out.println("U matrix.");
      for(int i=0; i<uu.length; i++){
         for(int j=0; j<uu[0].length; j++){            
            System.out.printf("%12.6f ",uu[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      
      System.out.println("Transposed V matrix.");
      for(int i=0; i<vv.length; i++){
         for(int j=0; j<vv[0].length; j++){            
            System.out.printf("%12.6f ",vv[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      
      int nn = ss.length;
      double[][] s1 = new double[nn][nn];
      for(int i=0; i<nn; i++){
         s1[i][i] = ss[i];
      }
      
      double[][] aa = new double[m1][nn];
      for(int i=0; i<m1; i++){
         for(int j=0; j<nn; j++){
            for(int k=0; k<nn; k++){
               aa[i][j] += uu[i][k]*s1[k][j];
            }
         }
      }
      
      double[][] bb = new double[m1][m2];
      for(int i=0; i<m1; i++){
         for(int j=0; j<m2; j++){
            for(int k=0; k<nn; k++){
               bb[i][j] += aa[i][k]*vv[k][j];
            }
         }
      }
      
      System.out.println("Reconstruced A matrix.");
      for(int i=0; i<uu.length; i++){
         for(int j=0; j<uu[0].length; j++){            
            System.out.printf("%12.6f ",bb[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      
   }
}
