package md;

public class AtomMDTest {

   public static void main(String[] args){
      AtomMD atom1 = new AtomMD(8);
      atom1.setLabel("O1");
      atom1.setAtomicCharge(-0.66);
      atom1.setBeta(1.0);
      atom1.setType("OG");
      double[] xyz = { -2.601, 0.341, 1.197};
      atom1.setXYZCoordinates(xyz);

      AtomMD atom2 = atom1.clone();
      xyz = atom2.getXYZCoordinates();
      xyz[0] = 2.2;
      
      System.out.println(atom1);
      System.out.println(atom2);
   }
}
