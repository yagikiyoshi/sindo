package sys;

import makePES.GridData;

public class LagrangeInt2Test2 {

   public static void main(String[] args){
 
      GridData q5 = new GridData(1,"q5.pot");
      GridData q6 = new GridData(1,"q6.pot");
      GridData q6q5 = new GridData(2,"q6q5.pot");

      double[][] datax = new double[2][];
      datax[0] = q5.getGrid(0);
      datax[1] = q5.getValue()[0];
      double[][] datay = new double[2][];
      datay[0] = q6.getGrid(0);
      datay[1] = q6.getValue()[0];
      double[] vdat = q6q5.getValue()[0];
      double[][] vdata = new double[datay[0].length][datax[0].length];

      int p=0;
      for(int i=0; i<datay[1].length; i++){
         for(int j=0; j<datax[0].length; j++){
            vdata[i][j] = vdat[p] + datax[1][j] + datay[1][i];
            p++;
         }
      }
      
      LagrangeInt2 lg2 = new LagrangeInt2(datax[0],datay[0],vdata);
      
      double[][] coeff = lg2.getCoeff();
      int Ngx = datax[0].length;
      int Ngy = datay[0].length;
      
      double dx = (datax[0][Ngx-1]-datax[0][0])/(Ngx-1);
      double dy = (datax[0][Ngy-1]-datay[0][0])/(Ngy-1);
      
      double[] xn = new double[Ngx];
      double[] yn = new double[Ngy];
      
      for(int i=0; i<Ngx; i++){
         double xx = datax[0][0]+i*dx;
         double yy = datay[0][0]+i*dy;
         
         xn[0] = 1.0;
         for(int j=1; j<Ngx; j++){
            xn[j] = xx*xn[j-1];
         }
         yn[0] = 1.0;
         for(int j=1; j<Ngy; j++){
            yn[j] = yy*yn[j-1];
         }

         double vp = 0.0;
         for(int j=0; j<Ngy; j++){
            for(int k=0; k<Ngx; k++){
               vp=vp + coeff[j][k]*yn[j]*xn[k];
            }
         }
         
         double vv = lg2.getV(xx,yy);
         
         System.out.printf("%8.2f %8.2f %12.6f %12.6f \n", xx,yy,vv,vp);
      }
   }
}
