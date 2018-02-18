package sys;

public class UtilitiesTest1 {

   public static void main(String[] args){
      String s1 = "  Cluster index    Cluster center   # of structures    Cluster radius ";
      String[] ss = Utilities.splitWithSpaceString(s1);
      for(int i=0; i<ss.length; i++){
         System.out.println(i+" "+ss[i]);
      }
      System.out.println();
      
      String s3 = "  11 12 13 14 15 ";
      int[] ii = Utilities.splitWithSpaceInt(s3);
      for(int i=0; i<ii.length; i++){
         System.out.println(i+" "+ii[i]);
      }
      System.out.println();
      
      String s4 = "  -2.981  -2.252   2.374  1.00  0.00";
      double[] dd = Utilities.splitWithSpaceDouble(s4);
      for(int i=0; i<dd.length; i++){
         System.out.println(i+" "+dd[i]);
      }
      System.out.println();
      
   }
}
