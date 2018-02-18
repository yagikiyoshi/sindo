package atom;

import atom.Atom;

public class AtomUtilTest1 {
   
   public static void main(String[] args){
      
      Atom o1 = new Atom(8);
      double[] o1xyz = {0.0, 0.0, 0.228563526};
      o1.setXYZCoordinates(o1xyz);
      
      Atom h2 = new Atom(1);
      double[] h2xyz = {0.0, 1.42997917, -0.914254105};
      h2.setXYZCoordinates(h2xyz);
      
      Atom h3 = new Atom(1);
      double[] h3xyz = {0.0, -1.42997917, -0.914254105};
      h3.setXYZCoordinates(h3xyz);
      
      System.out.println(o1);
      System.out.println(h2);
      System.out.println(h3);
      
      AtomUtil autil = new AtomUtil();
      
      double oh2 = autil.getBondLength(o1, h2);
      double oh3 = autil.getBondLength(o1, h3);
      System.out.printf("R(OH2)= %8.4f %n", oh2); 
      System.out.printf("R(OH3)= %8.4f %n", oh3); 
      
      double[] dd = new double[2];
      double h2oh3 = autil.getBondAngle(h2, o1, h3, dd);
      System.out.printf("A(H2OH3)= %8.2f %n", h2oh3); 
      System.out.printf("R(OH2)= %8.4f %n", dd[0]); 
      System.out.printf("R(OH3)= %8.4f %n", dd[1]); 

   }

}
