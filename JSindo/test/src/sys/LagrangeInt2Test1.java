package sys;

import makePES.*;

public class LagrangeInt2Test1 {
   
   public static void main(String[] args){
      
      GridData q5 = new GridData(1,"q5.pot");
      q5.readData();
      GridData q6 = new GridData(1,"q6.pot");
      q6.readData();
      GridData q6q5 = new GridData(2,"q6q5.pot");
      q6q5.readData();
      double[] vxg = q5.getValue()[0];
      double[] vyg = q6.getValue()[0];
      double[] xg = q5.getGrid(0);
      double[] yg = q6.getGrid(0);
      double[] vdat = q6q5.getValue()[0];
      double[][] vdata = new double[vyg.length][vxg.length];
      
      int k = 0;
      for(int i=0; i<vyg.length; i++){
         for(int j=0; j<vxg.length; j++){
            vdata[i][j] = vdat[k] + vxg[j] + vyg[i];
            k++;
         }
      }
      
      LagrangeInt2 lg2 = new LagrangeInt2(xg,yg,vdata);
      
      double cc = Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu);
      double xx = -0.5/cc;
      double yy = -0.28/cc;
      double dd = 0.001/cc;
      for(int i=0; i<16; i++){
         double vv = lg2.getV(xx,yy);
         double[] gg = lg2.getG();
         double[][] hh = lg2.getH();
         System.out.printf("%8.2f %12.6f \n", xx,vv);

         double vp = lg2.getV(xx+dd,yy);
         double vm = lg2.getV(xx-dd,yy);
         double g0 = (vp-vm)/dd*0.5;
         double h0 = (vp+vm-2.0*vv)/dd/dd;
         vp = lg2.getV(xx,yy+dd);
         vm = lg2.getV(xx,yy-dd);
         double g1 = (vp-vm)/dd*0.5;
         double h1 = (vp+vm-2.0*vv)/dd/dd;
         System.out.printf("         %12.6f %12.6f \n", gg[0],gg[0]-g0);
         System.out.printf("         %12.6f %12.6f \n", gg[1],gg[1]-g1);

         System.out.printf("         %12.6f %12.6f \n", hh[0][0],hh[0][0]-h0);
         System.out.printf("         %12.6f %12.6f \n", hh[1][1],hh[1][1]-h1);
         System.out.printf("         %12.6f \n", hh[0][1]);
         xx = xx + 0.0625/cc;
      }
      
   }
}
