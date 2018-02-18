package atom;

import atom.Atom;
import sys.Constants;

public class AtomUtilTest2 {
   
   public static void main(String[] args){
      
      double[] a1xyz = { 0.00000,        0.00000,        0.00000};
      double[] a2xyz = { 1.00000,        0.00000,        0.00000};
      double[] a3xyz = {-0.32557,        0.94552,        0.00000};
      double[] a4xyz = {-0.32557,       -0.47276,       -0.81884};
      for(int i=0; i<3; i++){
         a1xyz[i] = a1xyz[i]/Constants.Bohr2Angs;
         a2xyz[i] = a2xyz[i]/Constants.Bohr2Angs;
         a3xyz[i] = a3xyz[i]/Constants.Bohr2Angs;
         a4xyz[i] = a4xyz[i]/Constants.Bohr2Angs;
      }

      Atom c1 = new Atom(6);
      c1.setXYZCoordinates(a1xyz);
      Atom h2 = new Atom(1);
      h2.setXYZCoordinates(a2xyz);
      Atom h3 = new Atom(1);
      h3.setXYZCoordinates(a3xyz);
      Atom h4 = new Atom(1);
      h4.setXYZCoordinates(a4xyz);

      AtomUtil autil = new AtomUtil();

      double d3214 = autil.getDihedralAngle(h3, h2, c1, h4);
      System.out.println("d3214 = "+d3214);
      
      double d3124 = autil.getDihedralAngle(h3, c1, h2, h4);
      System.out.println("d3124 = "+d3124);

      double d4132 = autil.getDihedralAngle(h4, c1, h3, h2);
      System.out.println("d4132 = "+d4132);
      
   }

}
