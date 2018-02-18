package sys;

import makePES.GridData;

public class LagrangeInt1Test2 {

   public static void main(String[] args){
      
      GridData q5 = new GridData(1,"q5.pot");
      q5.readData();
      
      double[] xg = q5.getGrid(0);
      double[] vg = q5.getValue()[0];
      int Ng = xg.length;
      
      LagrangeInt1 lg1 = new LagrangeInt1(xg,vg);
      double[] coeff = lg1.getCoeff();
      for(int i=0; i<Ng; i++){
         System.out.printf("%12.6f \n",coeff[i]);
      }
      double dx = (xg[Ng-1]-xg[0])/(Ng-1);
      for(int i=0; i<Ng; i++){
         double xx = xg[0]+i*dx;
         double vv = lg1.getV(xx);
         double vp = coeff[0];
         double xn = xx;
         for(int j=1; j<Ng; j++){
            vp=vp + coeff[j]*xn;
            xn=xn*xx;
         }
         System.out.printf("%8.2f %12.6f %12.6f \n", xx,vv,vp);
      }
      

   }
}
