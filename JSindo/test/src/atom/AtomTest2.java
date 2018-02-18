package atom;

public class AtomTest2 {

   public static void main(String[] args){
      
      Atom mdatom = new Atom();
      mdatom.setLabel("O32");
      mdatom.setID(2);
      mdatom.setAtomicNum(8);
      double[] xyz = {37.870,  23.612,  63.784};
      mdatom.setXYZCoordinates(xyz);

      Atom mdatom2 = mdatom.clone();
      mdatom2.setID(3);
      xyz[1] = 1.0;
      
      System.out.println("mdatom:");
      System.out.println(mdatom);
      System.out.println();

      System.out.println("mdatom2:");
      System.out.println(mdatom2);
      
   }
}
