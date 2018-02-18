package sys;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Provides utility methods (vector operations, copy, terminate, etc.)
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class Utilities {

   /**
    * Returns the norm of the given vector a.
    * @param a vector
    * @return |a|
    */
   public static double getNorm(double[] a){
      double norm=0.0d;
      for(int i=0; i<a.length; i++){
         norm = norm + a[i]*a[i];
      }
      norm = Math.sqrt(norm);
      return norm;
   }
   /**
    * Returns the norm of (a-b).
    * @param a vector
    * @param b vector
    * @return |a-b|
    */
   public static double getNorm(double[] a, double[] b){
      double[] c = new double[a.length];
      for(int i=0; i<a.length; i++){
         c[i] = a[i] - b[i];
      }
      return Utilities.getNorm(c);
   }
   /**
    * Normalizes the vector
    * @param a vector to be normalized
    * @return |a|
    */
   public static double normalize(double[] a){
      double norm = Utilities.getNorm(a);
      for(int i=0; i<a.length; i++){
         a[i] = a[i]/norm;
      }
      return norm;
   }
   /**
    * Returns a dot product of a and b
    * @param a vector
    * @param b vector
    * @return a*b
    */
   public static double dotProduct(double[] a, double[] b){
      double prod=0.0d;
      for(int i=0; i<a.length; i++){
         prod = prod + a[i]*b[i];
      }
      return prod;
   }
   /**
    * Returns a vector product of a and b
    * @param a vector [3]
    * @param b vector [3]
    * @return a x b [3]
    */
   public static double[] vectorProduct(double[] a, double[] b){
      double[] c = new double[3];
      c[0] = a[1]*b[2] - a[2]*b[1];
      c[1] = a[2]*b[0] - a[0]*b[2];
      c[2] = a[0]*b[1] - a[1]*b[0];
      return c;
   }
   /**
    * Rotates c around an axis a to b by an angle thet
    * @param a position of a [3]
    * @param b position of b [3]
    * @param c position of c [3]
    * @param thet angle (degree)
    * @return the transformed position
    */
   public static double[] rotZ(double[] a, double[] b, double[] c, double thet){
      
      thet = thet/180.0*Math.PI;
      
      double[] zvec = new double[3];
      for(int n=0; n<3; n++){
         zvec[n] = b[n] - a[n];
      }
      Utilities.normalize(zvec);
      
      double[] xvec = new double[3];
      for(int n=0; n<3; n++){
         xvec[n] = c[n] - a[n];
      }
      
      double zz = Utilities.dotProduct(xvec, zvec);
      double[] orgn = new double[3];
      for(int n=0; n<3; n++){
         orgn[n] = a[n]    + zz*zvec[n];
         xvec[n] = xvec[n] - zz*zvec[n];
      }
      double rr = Utilities.normalize(xvec);
      double[] yvec = Utilities.vectorProduct(zvec, xvec);
      
      double[] r = new double[3];
      for(int n=0; n<3; n++){
         r[n] = orgn[n] + rr*(xvec[n]*Math.cos(thet)+yvec[n]*Math.sin(thet));     
      }
      return r;
   }
   /**
    * Split a sequence of integer with space and return in array 
    * @param n a sequence of integer
    * @return array of integer
    */
   public static int[] splitWithSpaceInt(String n){
      String[] ss = Utilities.splitWithSpaceString(n);
      int[] bb = new int[ss.length];
      for(int i=0; i<bb.length; i++){
         bb[i] = Integer.parseInt(ss[i]);
      }
      return bb;

   }
   /**
    * Split a sequence of double with space and return in array
    * @param n a sequence of double
    * @return array of double
    */
   public static double[] splitWithSpaceDouble(String n){
      String[] ss = Utilities.splitWithSpaceString(n);
      double[] bb = new double[ss.length];
      for(int i=0; i<bb.length; i++){
         bb[i] = Double.parseDouble(ss[i]);
      }
      return bb;
   }
   /**
    * Split the String with space and return in array
    * @param n String
    * @return array of String
    */   
   public static String[] splitWithSpaceString(String n){
      return n.trim().split("\\s+");
   }
   /**
    * Returns a deep copy of array
    * @param aa int[]
    * @return a copy of aa
    */
   public static int[] deepCopy(int[] aa){
      int[] bb = new int[aa.length];
      for(int i=0; i<aa.length; i++){
         bb[i] = aa[i];
      }
      return bb;
   }
   /**
    * Returns a deep copy of array
    * @param aa int[][]
    * @return a copy of aa
    */
   public static int[][] deepCopy(int[][] aa){
      int[][] bb = new int[aa.length][aa[0].length];
      for(int i=0; i<aa.length; i++){
         for(int j=0; j<aa[0].length; j++){
            bb[i][j] = aa[i][j];            
         }
      }
      return bb;
   }
   /**
    * Returns a deep copy of array
    * @param aa double[]
    * @return a copy of aa
    */
   public static double[] deepCopy(double[] aa){
      double[] bb = new double[aa.length];
      for(int i=0; i<aa.length; i++){
         bb[i] = aa[i];
      }
      return bb;
   }
   /**
    * Returns a deep copy of array
    * @param aa double[][]
    * @return a copy of aa
    */
   public static double[][] deepCopy(double[][] aa){
      double[][] bb = new double[aa.length][aa[0].length];
      for(int i=0; i<aa.length; i++){
         for(int j=0; j<aa[0].length; j++){
            bb[i][j] = aa[i][j];            
         }
      }
      return bb;
   }
   /**
    * Compares the values of two vectors. Returns true if the difference of the all elements 
    * is less than a threshold value. The size of the vector must be the same. 
    * @param aa the first vector
    * @param bb the second vector
    * @param thresh the threshold value
    * @return true or false
    */
   public static boolean compareVector(double[] aa, double[] bb, double thresh){
      boolean comp = true;
      for(int i=0; i<aa.length; i++){
         
      }
      return comp;
   }
   /**
    * Reads a block of data through a BufferedReader. The data should be formatted as: <br>
    *  n (number of data) <br>
    *  0.1, 0.2, 0.3, 0.4, 0.5 (five number in each line separated with camma) <br>
    *  0.6, 0.7, ... <br>
    *  The block data is returned.
    * @param br The BufferedReader linked to a data file.
    * @return The block of data
    * @throws IOException Thrown when IO error occurred while reading the line.
    */
   public static double[] readData(BufferedReader br) throws IOException{
      int len = Integer.parseInt(br.readLine().trim());
      int ll = len/5;
      if(len%5 !=0 ) ll++;
      
      String[] aa;
      double[] dd = new double[len];
      for(int i=0; i<ll; i++){
         aa = br.readLine().split(",");
         for(int j=0; j<aa.length; j++){
            dd[i*5+j] = Double.parseDouble(aa[j]);
         }
      }
      return dd;
   }
   /**
    * Reads a block of integer data through a BufferedReader. The data should be formatted as: <br>
    *  n (number of data) <br>
    *  1, 2, 3, 4, 5 (five number in each line separated with camma) <br>
    *  6, 7, ... <br>
    *  The block data is returned.
    * @param br The BufferedReader linked to a data file.
    * @return The block of data
    * @throws IOException Thrown when IO error occurred while reading the line.
    */
   public static int[] readIntData(BufferedReader br) throws IOException{
      int len = Integer.parseInt(br.readLine().trim());
      int ll = len/5;
      if(len%5 !=0 ) ll++;
      
      String[] aa;
      int[] dd = new int[len];
      for(int i=0; i<ll; i++){
         aa = br.readLine().split(",");
         for(int j=0; j<aa.length; j++){
            dd[i*5+j] = Integer.parseInt(aa[j].trim());
         }
      }
      return dd;
   }
   /**
    * Sorts an array of String in lexicographical order. 
    * @param str The array of String to be ordered
    * @return The ordered array
    */
   public static String[] sort(String[] str){
      for(int i=0; i<str.length; i++){
         String sti = str[i];
         for(int j=i; j<str.length; j++){
            String stj = str[j];
            if(sti.compareTo(stj) > 0){
               str[i] = stj;
               str[j] = sti;
               sti = stj;
            }
         }
      }
      return str;
   }
   /**
    * Sorts input Strings in lexicographical order 
    * @param str1 the first String
    * @param str2 the second String
    * @return The ordered String in array
    */
   public static String[] sort(String str1, String str2){
      String[] str = new String[2];
      str[0] = str1;
      str[1] = str2;
      return Utilities.sort(str);
   }
   /**
    * Sorts input Strings in lexicographical order 
    * @param str1 the first String
    * @param str2 the second String
    * @param str3 the third String
    * @return The ordered String in array
    */
   public static String[] sort(String str1, String str2, String str3){
      String[] str = new String[3];
      str[0] = str1;
      str[1] = str2;
      str[2] = str3;
      return Utilities.sort(str);
   }
   /**
    * Sorts input Strings in lexicographical order 
    * @param str1 the first String
    * @param str2 the second String
    * @param str3 the third String
    * @param str4 the fourth String
    * @return The ordered String in array
    */
   public static String[] sort(String str1, String str2, String str3, String str4){
      String[] str = new String[4];
      str[0] = str1;
      str[1] = str2;
      str[2] = str3;
      str[3] = str4;
      return Utilities.sort(str);
   }
   /**
    * Terminate sequence.
    */
   public static void terminate(){
      System.out.println();
      System.out.println("Terminated with error...");
      System.out.println();
      System.exit(-1);
   }
}
