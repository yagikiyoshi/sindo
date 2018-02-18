package makePES;

public class GridDataTest1 {

   public static void main(String[] args){
      GridData q5 = new GridData(1,"q5.pot");
      q5.readData();
      double[][] g5 = q5.getGrid();
      double[] pot = q5.getValue()[0];
      
      for(int i=0; i<g5[0].length; i++){
         System.out.printf("%12.4f  %12.6f \n",g5[0][i],pot[i]);
      }
      System.out.println();

      int nDim = 2;
      GridData q6q5 = new GridData(nDim,"q6q5.pot");
      q6q5.readData();
      g5 = q6q5.getGrid();
      pot = q6q5.getValue()[0];
      
      int[] ng = new int[nDim];
      for(int i=0; i<nDim; i++){
         ng[i] = g5[i].length;         
      }
      int[] pos = new int[nDim];
      for(int j=0; j<ng[1]; j++){
         pos[1] = j;
         for(int i=0; i<ng[0]; i++){
            pos[0] = i;
            System.out.printf("%12.4f  %12.6f  %12.6f \n",g5[0][i],g5[1][j],q6q5.getValue(pos)[0]);
         }
      }

      System.out.println();
      q6q5.setFileName("q6q5.pot.0");
      int[] mode = {5,6};
      String[] label = {"Energy"};
      q6q5.writeData("Test1", mode, label);

      
      nDim = 3;
      GridData q6q5q2 = new GridData(nDim,"q6q5q2.pot");
      q6q5q2.readData();
      g5 = q6q5q2.getGrid();
      pot = q6q5q2.getValue()[0];
      
      ng = new int[nDim];
      for(int i=0; i<nDim; i++){
         ng[i] = g5[i].length;         
      }
      pos = new int[nDim];
      //int nn=0;
      for(int k=0; k<ng[2]; k++){
         for(int j=0; j<ng[1]; j++){
            pos[1] = j;
            for(int i=0; i<ng[0]; i++){
               pos[0] = i;
               System.out.printf("%12.4f  %12.6f  %12.6f  %12.6f \n",g5[0][i],g5[1][j],g5[2][k],q6q5q2.getValue(pos)[0]);
            }
         }
      }
      System.out.println();
      
      q6q5q2.setFileName("q6q5q2.pot.0");
      int[] m3 = {2,5,6};
      q6q5q2.writeData("Test1", m3, label);

   }
}
