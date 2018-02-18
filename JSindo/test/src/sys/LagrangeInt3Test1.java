package sys;

import makePES.*;

public class LagrangeInt3Test1 {
   
   public static void main(String[] args){

      GridData q6q5q2 = new GridData(3,"q6q5q2.pot");
      q6q5q2.readData();
      double[][] xg = q6q5q2.getGrid();
      double[] vxyz = q6q5q2.getValue()[0];
      
      LagrangeInt3 lg3 = new LagrangeInt3(xg[0],xg[1],xg[2],vxyz);
      
      double cc = Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu);
      double xx = -0.55/cc;
      double yy = -0.40/cc;
      double zz = -0.35/cc;
      double dd = 0.001/cc;
      double vv = lg3.getV(xx,yy,zz);
      double[] gg = lg3.getG();
      double[][] hh = lg3.getH();
      
      System.out.printf("%8.2f %8.2f %8.2f %12.6f \n", xx,yy,zz,vv);

      double vdxp = lg3.getV(xx+dd, yy, zz);
      double vdxm = lg3.getV(xx-dd, yy, zz);
      double vdyp = lg3.getV(xx, yy+dd, zz);
      double vdym = lg3.getV(xx, yy-dd, zz);
      double vdzp = lg3.getV(xx, yy, zz+dd);
      double vdzm = lg3.getV(xx, yy, zz-dd);
      double[] gn = new double[3];
      gn[0] = (vdxp - vdxm)/dd/2.0;
      gn[1] = (vdyp - vdym)/dd/2.0;
      gn[2] = (vdzp - vdzm)/dd/2.0;
            
      System.out.printf("%12.6f %12.6f %12.6f \n", gg[0],gg[1],gg[2]);
      System.out.printf("%12.6f %12.6f %12.6f \n", gg[0]-gn[0],gg[1]-gn[1],gg[2]-gn[2]);
      
      System.out.println();
      
      double[][] hn = new double[3][3];
      hn[0][0] = (vdxp+vdxm-2.0*vv)/dd/dd;
      hn[1][1] = (vdyp+vdym-2.0*vv)/dd/dd;
      hn[2][2] = (vdzp+vdzm-2.0*vv)/dd/dd;
      for(int i=0; i<3; i++){
         System.out.printf("%12.6f %12.6f %12.6f \n", hh[i][0],hh[i][1],hh[i][2]);
      }
      for(int i=0; i<3; i++){
         System.out.printf("%12.6f ", hh[i][i]-hn[i][i]);
      }
      System.out.println();
   }

}
