package md;

public class ANResolverTest {

   public static void main(String[] args){
      ANResolver anr = new ANResolver();
      
      System.out.println("C1 : "+anr.getAtomicNumber("C1"));
      System.out.println("CAL : "+anr.getAtomicNumber("CAL"));
      System.out.println("N12 : "+anr.getAtomicNumber("N12"));
      System.out.println("0C2 : "+anr.getAtomicNumber("0C2"));

      anr.addLabel("0C2", 6);
      System.out.println("0C2 : "+anr.getAtomicNumber("0C2"));
   }
   
}
