package sys;

import makePES.GridData;

public class LagrangeInt3Test2 {

   public static void main(String[] args){
 
      GridData q6q5q2 = new GridData(3,"q6q5q2.pot");
      q6q5q2.readData();
      double[][] xg = q6q5q2.getGrid();
      double[] vxyz = q6q5q2.getValue()[0];
      
      LagrangeInt3 lg3 = new LagrangeInt3(xg[0],xg[1],xg[2],vxyz);
      
      double[][][] coeff = lg3.getCoeff();
      int Ngx = xg[0].length;
      int Ngy = xg[1].length;
      int Ngz = xg[2].length;
      
      double dx = (xg[0][Ngx-1]-xg[0][0])/(Ngx-1);
      double dy = (xg[1][Ngy-1]-xg[1][0])/(Ngy-1);
      double dz = (xg[2][Ngz-1]-xg[2][0])/(Ngz-1);
      
      double[] xn = new double[Ngx];
      double[] yn = new double[Ngy];
      double[] zn = new double[Ngz];
      
      for(int i=0; i<Ngx; i++){
         double xx = xg[0][0]+i*dx;
         double yy = xg[1][0]+i*dy;
         double zz = xg[2][0]+i*dz;
         
         xn[0] = 1.0;
         for(int j=1; j<Ngx; j++){
            xn[j] = xx*xn[j-1];
         }
         yn[0] = 1.0;
         for(int j=1; j<Ngy; j++){
            yn[j] = yy*yn[j-1];
         }
         zn[0] = 1.0;
         for(int j=1; j<Ngz; j++){
            zn[j] = zz*zn[j-1];
         }

         double vp = 0.0;
         for(int l=0; l<Ngz; l++){
            for(int j=0; j<Ngy; j++){
               for(int k=0; k<Ngx; k++){
                  vp=vp + coeff[l][j][k]*zn[l]*yn[j]*xn[k];
               }
            }
         }
         
         double vv = lg3.getV(xx,yy,zz);
         
         System.out.printf("%8.2f %8.2f %8.2f %12.6f %12.6f \n", xx,yy,zz,vv,vp);
      }
   }
}
