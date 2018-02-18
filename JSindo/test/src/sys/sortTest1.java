package sys;

public class sortTest1 {

   public static void main(String[] args){
      String[] ss = Utilities.sort("NH", "H");
      System.out.println(ss[0] + " " + ss[1]);

      String[] s1 = Utilities.sort("AA", "A1");
      System.out.println(s1[0] + " " + s1[1]);

      String[] s2 = Utilities.sort("ABC", "AbC");
      System.out.println(s2[0] + " " + s2[1]);
   }
}
