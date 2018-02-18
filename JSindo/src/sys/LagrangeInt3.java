package sys;

/**
 * Lagrange interpolation in 3-dimension
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */

public class LagrangeInt3 extends LagCore {

   private int[] ng;
   private double[][] xyg;
   private double[][][] vp1;
   
   private double[][] a1;
   private double[][] a2;
   private double[] bb;
   private double[] qq;
   /**
    * Constructor of 3D interpolation
    * @param xg the grid points in the x-direction
    * @param yg the grid points in the y-direction
    * @param zg the grid points in the z-direction
    * @param vv the values at grid points in the order of [x1y1z1, x2y1z1, .., xny1z1, x1y2z1, ..]
    */
   public LagrangeInt3(double[] xg, double[] yg, double[] zg, double[] vv){
      this(xg,yg,zg);
      int nx = xg.length;
      int ny = yg.length;
      int nz = zg.length;
      double[][][] v3 = new double[nz][ny][nx];
      
      int nn = 0;
      for(int k=0; k<nz; k++){
         for(int j=0; j<ny; j++){
            for(int i=0; i<nx; i++){
               v3[k][j][i] = vv[nn];
               nn++;
            }
         }
      }
      this.setValue(v3);
   }
   /**
    * Constructor of 3D interpolation
    * @param xg the grid points in the x-direction
    * @param yg the grid points in the y-direction
    * @param zg the grid points in the z-direction
    * @param vg the values at grid points [nz][ny][nx]
    */
   public LagrangeInt3(double[] xg, double[] yg, double[] zg, double[][][] vg){
      this(xg,yg,zg);
      this.setValue(vg);
   }
   LagrangeInt3(double[] xg, double[] yg, double[] zg){
      ng = new int[3];
      xyg = new double[3][];
      
      ng[0] = xg.length;
      ng[1] = yg.length;
      ng[2] = zg.length;
      
      xyg[0] = xg;
      xyg[1] = yg;
      xyg[2] = zg;

      qq = new double[3];
      a1 = new double[3][];
      bb = new double[3];
      
   }
   
   private void setValue(double[][][] vg){
      vp1 = new double[ng[2]][ng[1]][ng[0]];
      
      for(int i=0; i<ng[2]; i++){
         double pz = super.core1(i, xyg[2][i], xyg[2]);
         for(int j=0; j<ng[1]; j++){
            double py = super.core1(j, xyg[1][j], xyg[1]);
            for(int k=0; k<ng[0]; k++){
               vp1[i][j][k] = vg[i][j][k]/pz/py/super.core1(k, xyg[0][k], xyg[0]);               
            }
            
         }
      }

   }
   
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
   /**
    * Sets the current point to (xx,yy,zz) and returns the interpolated value.
    * @param xx the x value
    * @param yy the y value
    * @param zz the z value
    * @return the interpolated value
    */
   public double getV(double xx, double yy, double zz){
      
      qq[0] = xx;
      qq[1] = yy;
      qq[2] = zz;
      
      for(int xy=0; xy<3; xy++){
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
      for(int i=0; i<ng[2]; i++){
         double c1 = 0.0;
         for(int j=0; j<ng[1]; j++){
            double c0 = 0.0;
            for(int k=0; k<ng[0]; k++){
               c0 = c0 + vp1[i][j][k]*a1[0][k];
            }
            c1 = c1 + c0*a1[1][j];
         }
         vv = vv + c1*a1[2][i];
      }
      vv = vv*bb[0]*bb[1]*bb[2];
      
      return vv;
   }

   /**
    * Returns the gradient of the interpolated function at the current point.
    * @return the gradient
    */
   public double[] getG(){

      a2 = new double[3][];

      for(int xy=0; xy<3; xy++){
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
      
      double[] gg = new double[3];
      for(int k=0; k<ng[2]; k++){
         double c1 = 0.0;
         double d1 = 0.0;
         double e1 = 0.0;
         for(int i=0; i<ng[1]; i++){
            double c0 = 0.0;
            double d0 = 0.0;
            for(int j=0; j<ng[0]; j++){
               c0 = c0 + vp1[k][i][j]*a2[0][j];
               d0 = d0 + vp1[k][i][j]*a1[0][j];
            }
            c1 = c1 + c0*a1[1][i];
            d1 = d1 + d0*a2[1][i];
            e1 = e1 + d0*a1[1][i];
         }
         gg[0] = gg[0] + c1*a1[2][k];
         gg[1] = gg[1] + d1*a1[2][k];
         gg[2] = gg[2] + e1*a2[2][k];
         
      }
      gg[0] = gg[0]*bb[0]*bb[1]*bb[2];
      gg[1] = gg[1]*bb[0]*bb[1]*bb[2];
      gg[2] = gg[2]*bb[0]*bb[1]*bb[2];
      
      return gg;
      
   }

   /**
    * Returns the Hessian matrix of the interpolated function at the current point.
    * @return the Hessian matrix
    */
   public double[][] getH(){
      
      double[][] a3 = new double[3][];
      
      for(int xy=0; xy<3; xy++){
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
            a3[xy] = aa;

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
            a3[xy] = aa;
         }

      }
      
      double[][] hh = new double[3][3];
      for(int i=0; i<ng[2]; i++){
         double d02 = 0.0;
         double d20 = 0.0;
         double d11 = 0.0;
         double d01 = 0.0;
         double d10 = 0.0;
         double d00 = 0.0;
         for(int j=0; j<ng[1]; j++){
            double c2 = 0.0;
            double c1 = 0.0;
            double c0 = 0.0;
            for(int k=0; k<ng[0]; k++){
               c0 = c0 + vp1[i][j][k]*a1[0][k];
               c1 = c1 + vp1[i][j][k]*a2[0][k];
               c2 = c2 + vp1[i][j][k]*a3[0][k];
            }
            d02 = d02 + c2*a1[1][j];
            d20 = d20 + c0*a3[1][j];
            d11 = d11 + c1*a2[1][j];
            d01 = d01 + c1*a1[1][j];
            d10 = d10 + c0*a2[1][j];
            d00 = d00 + c0*a1[1][j];
            
         }
         hh[0][0] = hh[0][0] + d02*a1[2][i];
         hh[1][1] = hh[1][1] + d20*a1[2][i];
         hh[2][2] = hh[2][2] + d00*a3[2][i];
         hh[1][0] = hh[1][0] + d11*a1[2][i];
         hh[2][0] = hh[2][0] + d01*a2[2][i];
         hh[2][1] = hh[2][1] + d10*a2[2][i];
      }
      hh[0][0] = hh[0][0]*bb[0]*bb[1]*bb[2];
      hh[1][1] = hh[1][1]*bb[0]*bb[1]*bb[2];
      hh[2][2] = hh[2][2]*bb[0]*bb[1]*bb[2];
      hh[1][0] = hh[1][0]*bb[0]*bb[1]*bb[2];
      hh[2][0] = hh[2][0]*bb[0]*bb[1]*bb[2];
      hh[2][1] = hh[2][1]*bb[0]*bb[1]*bb[2];
      hh[0][1] = hh[1][0];
      hh[0][2] = hh[2][0];
      hh[1][2] = hh[2][1];
      
      return hh;
   }

   /**
    * Returns the coefficients of the polynomial function.
    * @return the coefficients [nz][ny][nx]
    */
   public double[][][] getCoeff(){
      double[][][] coeff = new double[ng[2]][ng[1]][ng[0]];
      
      for(int i3=0; i3<ng[2]; i3++){
         double[] cz = this.getC1(2, i3);
         for(int i2=0; i2<ng[1]; i2++){
            double[] cy = this.getC1(1, i2);
            for(int i1=0; i1<ng[0]; i1++){
               double[] cx = this.getC1(0, i1);
               
               for(int j3=0; j3<ng[2]; j3++){
                  for(int j2=0; j2<ng[1]; j2++){
                     for(int j1=0; j1<ng[0]; j1++){
                        coeff[j3][j2][j1] = coeff[j3][j2][j1] + vp1[i3][i2][i1]*cx[j1]*cy[j2]*cz[j3];
                     }
                  }
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
