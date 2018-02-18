package sys;

/**
 * Lagrange interpolation in 2-dimension
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */

public class LagrangeInt2 extends LagCore {

   private int[] ng;
   private double[][] xyg;
   private double[][] vp1;
   
   private double[][] a1;
   private double[][] a2;
   private double[] bb;
   private double[] qq;
   
   /**
    * Constructor of 2D interpolation
    * @param xg the grid points in the x-direction
    * @param yg the grid points in the y-direction
    * @param vg the values at grid points in the order of [x1y1, x2y1, .., xny1, x1y2, ..]
    */
   public LagrangeInt2(double[] xg, double[] yg, double[] vg){
      this(xg, yg);

      int nx = xg.length;
      int ny = yg.length;
      double[][] vv = new double[ny][nx];
      int p = 0;
      for(int j=0; j<ny; j++){
         for(int i=0; i<nx; i++){
            vv[j][i] = vg[p];
            p++;
         }
      }
      this.setValue(vv);
      
   }
   /**
    * Constructor of 2D interpolation
    * @param xg the grid points in the x-direction
    * @param yg the grid points in the y-direction
    * @param vg the values at grid points [ny][nx]
    */
   public LagrangeInt2(double[] xg, double[] yg, double[][] vg){
      this(xg, yg);
      this.setValue(vg);
      
   }
   /**
    * Constructor of 2D interpolation
    * @param xg the grid points in the x-direction
    * @param yg the grid points in the y-direction
    */
   LagrangeInt2(double[] xg, double[] yg){
      ng = new int[2];
      xyg = new double[2][];
      
      ng[0] = xg.length;
      ng[1] = yg.length;
      
      xyg[0] = xg;
      xyg[1] = yg;

      qq = new double[2];
      a1 = new double[2][];
      bb = new double[2];
      
   }

   // Set the value
   private void setValue(double[][] vg){
      vp1 = new double[ng[1]][ng[0]];
      for(int i=0; i<ng[1]; i++){
         double py = super.core1(i, xyg[1][i], xyg[1]);
         for(int j=0; j<ng[0]; j++){
            vp1[i][j] = vg[i][j]/py/super.core1(j, xyg[0][j], xyg[0]);
            
         }
      }
   }
   // check if xx is on the grid point
   private boolean isOngrid(int xy, double xx){
      boolean ongrid = false;
      for(int i=0; i<ng[xy]; i++){
         if(Math.abs(xx-xyg[xy][i])<1.0e-04){
            ongrid = true;
            break;
         }
      }
      return ongrid;
   }

   /**
    * Sets the current point to (xx,yy) and returns the interpolated value.
    * @param xx the x value
    * @param yy the y value
    * @return the interpolated value
    */
   public double getV(double xx, double yy){
      
      qq[0] = xx;
      qq[1] = yy;
      
      for(int xy=0; xy<2; xy++){
         boolean isOngrid = this.isOngrid(xy,qq[xy]);
         
         if(! isOngrid){
            double[] aa = new double[ng[xy]];
            for(int i=0; i<ng[xy]; i++){
               aa[i] = 1.0/(qq[xy]-xyg[xy][i]);
            }
            a1[xy] = aa;
            bb[xy] = super.core0(qq[xy], xyg[xy]);
            
         }else{
            double[] aa = new double[ng[xy]];
            for(int i=0; i<ng[xy]; i++){
               aa[i] = super.core1(i, qq[xy], xyg[xy]);
            }
            a1[xy] = aa;
            bb[xy] = 1.0;
               
         }
      }
      
      double vv=0.0;
      for(int i=0; i<ng[1]; i++){
         double c0 = 0.0;
         for(int j=0; j<ng[0]; j++){
            c0 = c0 + vp1[i][j]*a1[0][j];
         }
         vv = vv + c0*a1[1][i];
      }
      vv = vv*bb[0]*bb[1];
      
      return vv;
   }

   /**
    * Returns the gradient of the interpolated function at the current point.
    * @return the gradient
    */
   public double[] getG(){

      a2 = new double[2][];

      for(int xy=0; xy<2; xy++){
         boolean isOngrid = this.isOngrid(xy,qq[xy]);
         
         if(! isOngrid){
            double s0 = 0.0;
            for(int i=0; i<ng[xy]; i++){
               s0 = s0 + a1[xy][i];
            }
            
            double[] aa = new double[ng[xy]];
            for(int i=0; i<ng[xy]; i++){
               aa[i] = a1[xy][i]*(s0-a1[xy][i]);
            }
            a2[xy] = aa;
            
         }else{
            
            double[] aa = new double[ng[xy]];
            for(int i=0; i<ng[xy]; i++){
               for(int j=0; j<ng[xy]; j++){
                  if(i != j){
                     aa[i] = aa[i] + super.core2(i, j, qq[xy], xyg[xy]);
                  }
               }
            }
            a2[xy] = aa;
            
         }
      }
      
      double[] gg = new double[2];
      for(int i=0; i<ng[1]; i++){
         double c0 = 0.0;
         double d0 = 0.0;
         for(int j=0; j<ng[0]; j++){
            c0 = c0 + vp1[i][j]*a2[0][j];
            d0 = d0 + vp1[i][j]*a1[0][j];
         }
         gg[0] = gg[0] + c0*a1[1][i];
         gg[1] = gg[1] + d0*a2[1][i];
      }
      gg[0] = gg[0]*bb[0]*bb[1];
      gg[1] = gg[1]*bb[0]*bb[1];
      
      return gg;
      
   }

   /**
    * Returns the Hessian matrix of the interpolated function at the current point.
    * @return the Hessian matrix
    */
   public double[][] getH(){
      
      double[][] A3 = new double[2][];
      
      for(int xy=0; xy<2; xy++){
         boolean isOngrid = this.isOngrid(xy,qq[xy]);
         
         if(! isOngrid){
            double s0 = 0.0;
            for(int i=0; i<ng[xy]; i++){
               s0 = s0 + a1[xy][i];
            }
            
            double[] aa = new double[ng[xy]];
            for(int i=0; i<ng[xy]; i++){
               double c0 = 0.0;
               for(int j=0; j<ng[xy]; j++){
                  if(j != i){
                     c0 = c0 + a1[xy][j]*(s0 - a1[xy][j] - a1[xy][i]);
                  }
               }
               aa[i] = c0*a1[xy][i];
            }
            A3[xy] = aa;

         }else{
            
            double[] aa = new double[ng[xy]];
            for(int i=0; i<ng[xy]; i++){
               for(int j=0; j<ng[xy]; j++){
                  if(j != i){
                     for(int k=0; k<ng[xy]; k++){
                        if(k != i && k != j){
                           aa[i] = aa[i] + super.core3(i, j, k, qq[xy], xyg[xy]);
                        }
                     }
                  }
               }
            }
            A3[xy] = aa;
         }

      }
      
      double[][] hh = new double[2][2];
      for(int i=0; i<ng[1]; i++){
         double c0 = 0.0;
         double d0 = 0.0;
         double e0 = 0.0;
         for(int j=0; j<ng[0]; j++){
            c0 = c0 + vp1[i][j]*A3[0][j];
            d0 = d0 + vp1[i][j]*a1[0][j];
            e0 = e0 + vp1[i][j]*a2[0][j];
         }
         hh[0][0] = hh[0][0] + c0*a1[1][i];
         hh[1][1] = hh[1][1] + d0*A3[1][i];
         hh[0][1] = hh[0][1] + e0*a2[1][i];
         
      }
      hh[0][0] = hh[0][0]*bb[0]*bb[1];
      hh[1][1] = hh[1][1]*bb[0]*bb[1];
      hh[0][1] = hh[0][1]*bb[0]*bb[1];
      hh[1][0] = hh[0][1];
      
      return hh;
   }
   /**
    * Returns the coefficients of the polynomial function.
    * @return the coefficients [ny][nx]
    */
   public double[][] getCoeff(){
      double[][] coeff = new double[ng[1]][ng[0]];
      
      for(int i2=0; i2<ng[1]; i2++){
         double[] cy = this.getC1(1, i2);
         for(int i1=0; i1<ng[0]; i1++){
            double[] cx = this.getC1(0, i1);
            
            for(int j2=0; j2<ng[1]; j2++){
               for(int j1=0; j1<ng[0]; j1++){
                  coeff[j2][j1] = coeff[j2][j1] + vp1[i2][i1]*cx[j1]*cy[j2];
               }
            }
         }
      }
      return coeff;
   }
   
   private double[] getC1(int xy, int ii){
      
      int ngi = ng[xy];
      double[] c1 = new double[ngi];
      c1[ngi-1] = 1.0;
      
      for(int j=0; j<ii; j++){
         double[] c2 = new double[ngi];
         for(int k=0; k<=j; k++){
            c2[ngi-k-1] = c2[ngi-k-1]+c1[ngi-k-1];
            c2[ngi-k-2] = c2[ngi-k-2]-c1[ngi-k-1]*xyg[xy][j];
         }
         //System.out.println(i+" "+j);
         //for(int k=0; k<Ng; k++){
         //   System.out.printf("%10.4f ",c2[k]);
         //}
         //System.out.println();
         //System.exit(0);
         for(int k=0; k<ngi; k++){
            c1[k] = c2[k];
         }
      }
      for(int j=ii+1; j<ngi; j++){
         double[] c2 = new double[ngi];
         for(int k=0; k<=j-1; k++){
            c2[ngi-k-1] = c2[ngi-k-1]+c1[ngi-k-1];
            c2[ngi-k-2] = c2[ngi-k-2]-c1[ngi-k-1]*xyg[xy][j];
         }
         //System.out.println(i+" "+j);
         //for(int k=0; k<Ng; k++){
         //   System.out.printf("%10.4f ",c2[k]);
         //}
         //System.out.println();
         for(int k=0; k<ngi; k++){
            c1[k] = c2[k];
         }
      }

      return c1;
   }


}
