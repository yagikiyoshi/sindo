package sys;

import makePES.GridData;

public class LagrangeInt1Test1 {
   
   public static void main(String[] args){

      GridData q2 = new GridData(1,"q2.pot");
      q2.readData();
      double[] xg = q2.getGrid(0);
      for(int i=0; i<xg.length; i++){
         xg[i] = xg[i]*Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu);
      }
      double[] vg = q2.getValue()[0];
      LagrangeInt1 lg1 = new LagrangeInt1(xg,vg);
      
      double xx = -0.6;
      double dx = 0.005;
      for(int i=0; i<30; i++){
         double vv = lg1.getV(xx);
         double ga = lg1.getG();
         double ha = lg1.getH();
         double vp = lg1.getV(xx+dx);
         double vm = lg1.getV(xx-dx);
         double gg = (vp-vm)/dx*0.5;
         double hh = (vp+vm-2.0*vv)/dx/dx;
         System.out.printf("%8.2f %12.6f %12.6f %12.6f %12.6f %12.6f \n", xx,vv,ga,gg,ha,hh);
         xx = xx + 0.04;
      }
      
   }

}
