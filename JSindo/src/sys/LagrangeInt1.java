package sys;

/**
 * Lagrange interpolation in 1-dimension
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */

public class LagrangeInt1 extends LagCore {

   private int ng;
   private double[] xg;
   private double[] vp1;
   private double xx;
   private double[] A1;
   private double Bb;
   
   /**
    * Constructor of 1D interpolation
    * @param xg the grid points
    * @param vg the values at the grid points
    */
   public LagrangeInt1(double[] xg, double[] vg){
      this.ng = xg.length;
      this.xg = xg;
     
      vp1 = new double[ng];
      
      for(int i=0; i<ng; i++){
         vp1[i] = vg[i]/super.core1(i, xg[i], xg);
      }
   }

   // check if xx is on the grid point
   private boolean isOngrid(double xx){
      boolean ongrid = false;
      for(int i=0; i<ng; i++){
         if(Math.abs(xx-xg[i])<1.0e-04){
            ongrid = true;
            break;
         }
      }
      return ongrid;
   }

   /**
    * Sets the current point to xx and returns the interpolated value.
    * @param xx the x value
    * @return the interpolated value
    */
   public double getV(double xx){
      this.xx = xx;
      boolean isOngrid = this.isOngrid(xx);
      
      double vv=0.0;
      if(! isOngrid){
         A1 = new double[ng];
         for(int i=0; i<ng; i++){
            A1[i] = xx - xg[i];
         }
         Bb = super.core0(xx, xg);
         for(int i=0; i<ng; i++){
            vv=vv + vp1[i]/A1[i];
         }
         vv=vv*Bb;
      }else{
         for(int i=0; i<ng; i++){
            vv=vv + vp1[i]*super.core1(i, xx, xg);
         }
      }
      return vv;
   }
   
   /**
    * Returns the gradient of the interpolated function at the current point.
    * @return the gradient
    */
   public double getG(){
      boolean isOngrid = this.isOngrid(xx);
      
      double vv= 0.0;
      if(! isOngrid){
         
         double s0 = 0.0;
         for(int i=0; i<ng; i++){
            s0 = s0 + 1.0/A1[i];
         }
         for(int i=0; i<ng; i++){
            vv = vv + vp1[i]/A1[i]*(s0-1.0/A1[i]);
         }
         vv=vv*Bb;

      }else{
         for(int i=0; i<ng; i++){
            double dd=0.0;
            for(int j=0; j<ng; j++){
               if(j != i){
                  dd = dd + super.core2(i, j, xx, xg);
               }
            }
            vv = vv + vp1[i]*dd;
         }

      }
      return vv;
   }
   
   /**
    * Returns the second-order derivative of the interpolated function at the current point.
    * @return the second-order derivative
    */
   public double getH(){
      boolean isOngrid = this.isOngrid(xx);

      double vv = 0.0;
      if(! isOngrid){
         //double p0 = super.core0(xx, xg);

         double s0 = 0.0;
         for(int i=0; i<ng; i++){
            s0 = s0 + 1.0/A1[i];
         }
         
         vv=0.0;
         for(int i=0; i<ng; i++){
            double si = s0 - 1.0/A1[i];
            double dd = 0.0;
            for(int j=0; j<ng; j++){
               if(j != i){
                  dd = dd + (si - 1.0/A1[j])/A1[j];
               }
            }
            vv = vv + vp1[i]/A1[i]*dd;
         }
         vv = vv*Bb;

      }else{
         vv = 0.0;
         for(int i=0; i<ng; i++){
            double dd=0.0;
            for(int j=0; j<ng; j++){
               if(j != i){
                  for(int k=0; k<ng; k++){
                     if(k != i && k != j){
                        dd = dd + super.core3(i, j, k, xx, xg);                        
                     }
                  }
               }
            }
            vv = vv + vp1[i]*dd;
         }


      }
      return vv;
   }
   /**
    * Returns the coefficients of the polynomial function.
    * @return the coefficients
    */
   public double[] getCoeff(){
      double[] coeff = new double[ng];
      
      for(int i=0; i<ng; i++){
         double[] c1 = new double[ng];
         c1[ng-1] = 1.0;
         for(int j=0; j<i; j++){
            double[] c2 = new double[ng];
            for(int k=0; k<=j; k++){
               c2[ng-k-1] = c2[ng-k-1]+c1[ng-k-1];
               c2[ng-k-2] = c2[ng-k-2]-c1[ng-k-1]*xg[j];
            }
            //System.out.println(i+" "+j);
            //for(int k=0; k<Ng; k++){
            //   System.out.printf("%10.4f ",c2[k]);
            //}
            //System.out.println();
            //System.exit(0);
            for(int k=0; k<ng; k++){
               c1[k] = c2[k];
            }
         }
         for(int j=i+1; j<ng; j++){
            double[] c2 = new double[ng];
            for(int k=0; k<=j-1; k++){
               c2[ng-k-1] = c2[ng-k-1]+c1[ng-k-1];
               c2[ng-k-2] = c2[ng-k-2]-c1[ng-k-1]*xg[j];
            }
            //System.out.println(i+" "+j);
            //for(int k=0; k<Ng; k++){
            //   System.out.printf("%10.4f ",c2[k]);
            //}
            //System.out.println();
            for(int k=0; k<ng; k++){
               c1[k] = c2[k];
            }
         }
         for(int j=0; j<ng; j++){
            coeff[j] = coeff[j] + c1[j]*vp1[i];
         }
      }
      
      return coeff;
   }

}
