package atom;

import atom.Atom;

public class AtomUtilTest3 {
   
   public static void main(String[] args){
      
      double[] c1xyz = { 0.046679,    0.662300,    0.000000};
      double[] o2xyz = { 0.046679,   -0.757134,    0.000000};
      double[] h3xyz = {-0.869304,   -1.043322,    0.000000};
      double[] h4xyz = { 1.086030,    0.975384,    0.000000};
      double[] h5xyz = {-0.435115,    1.075603,    0.887541};
      double[] h6xyz = {-0.435115,    1.075603,   -0.887541};

      Atom c1 = new Atom(6);
      c1.setXYZCoordinates(c1xyz);
      Atom o2 = new Atom(8);
      o2.setXYZCoordinates(o2xyz);
      Atom h3 = new Atom(1);
      h3.setXYZCoordinates(h3xyz);
      Atom h4 = new Atom(1);
      h4.setXYZCoordinates(h4xyz);
      Atom h5 = new Atom(1);
      h5.setXYZCoordinates(h5xyz);
      Atom h6 = new Atom(1);
      h6.setXYZCoordinates(h6xyz);

      AtomUtil autil = new AtomUtil();

      double r14 = autil.getBondLength(c1, h4);
      System.out.println("r14 = "+r14);
      
      double a214 = autil.getBondAngle(o2, c1, h4);
      System.out.println("a214 = "+a214);
      
      double d3214 = autil.getDihedralAngle(h3, o2, c1, h4);
      System.out.println("d3214 = "+d3214);
      
      double[] xyz = autil.genAtomXYZ(c1, r14, o2, a214, h3, d3214);
      System.out.printf("h4 = %12.6f %12.6f %12.6f %n",xyz[0],xyz[1],xyz[2]);

      double r15 = autil.getBondLength(c1, h5);
      System.out.println("r15 = "+r15);
      
      double a215 = autil.getBondAngle(o2, c1, h5);
      System.out.println("a215 = "+a215);

      double d3215 = autil.getDihedralAngle(h3, o2, c1, h5);
      System.out.println("d3215 = "+d3215);

      xyz = autil.genAtomXYZ(c1, r15, o2, a215, h3, d3215);
      System.out.printf("h5 = %12.6f %12.6f %12.6f %n",xyz[0],xyz[1],xyz[2]);

      double r16 = autil.getBondLength(c1, h6);
      System.out.println("r16 = "+r16);
      
      double a216 = autil.getBondAngle(o2, c1, h6);
      System.out.println("a216 = "+a216);

      double d3216 = autil.getDihedralAngle(h3, o2, c1, h6);
      System.out.println("d3216 = "+d3216);
      
      xyz = autil.genAtomXYZ(c1, r16, o2, a216, h3, d3216);
      System.out.printf("h6 = %12.6f %12.6f %12.6f %n",xyz[0],xyz[1],xyz[2]);

   }

}
