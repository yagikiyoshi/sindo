package molecule;

import sys.Utilities;
import vibration.*;

public class VibTransformer{

   private QXTransformer trans;
   private VibrationalData vdata;
   private int[] atomindex3;
   private double[][] x0;
   
   public VibTransformer(Molecule molin){
      this(molin, 0);      
   }
   
   public VibTransformer(Molecule molin, int nd){
      this.setup(molin, nd);
   }   
   
   public void setup(Molecule molin, int nd){      
      MolToVib m2v = new MolToVib();
      CoordinateData coord = m2v.getCoordinateData(molin,nd);
      trans = new QXTransformer();
      trans.appendCoordinate(coord);

      this.vdata = molin.getVibrationalData(nd);
      
      if(vdata.getAtomIndex() != null){
         int[] atomindex  = vdata.getAtomIndex();
         atomindex3 = new int[atomindex.length*3];
         for(int i=0; i<atomindex.length; i++){
            atomindex3[i*3]   = atomindex[i]*3;
            atomindex3[i*3+1] = atomindex[i]*3+1;
            atomindex3[i*3+2] = atomindex[i]*3+2;
         }
      }
      
      this.x0 = Utilities.deepCopy(molin.getXYZCoordinates2());
      
   }

   /**
    * Transforms qq to xyz coordinates, i.e., <br>
    *   x_new = x_0 + L x qq <br>
    * Note that the current geometry is overwritten.
    * @param mm mode combinations
    * @param qq vibrational coordinates
    * @return the xyz coordinates of the molecule
    */
   public double[][] q2x(int[] mm, double[] qq){      
      double[][] xx = Utilities.deepCopy(x0);      
      return this.dq2x(xx, mm, qq);      
   }
   
   /**
    * Adds deviations dq to the current geometry, i.e., <br>
    *   x_new = x_current + L x dq <br>
    * Note that the deviation is added to the current geometry.
    * @param xx the current geometry
    * @param mm mode combinations
    * @param dq deviations
    * @return the xyz coordinates of the molecule
    */
   public double[][] dq2x(double[][] xx, int[] mm, double[] dq){
      
      int[] atomIndex = vdata.getAtomIndex();
      
      if(atomIndex != null){
         for(int i=0; i<mm.length; i++){
            double[][] xi = trans.dq2dx(mm[i], dq[i]);
            for(int n=0; n<xi.length; n++){
               int na = atomIndex[n];
               for(int m=0; m<3; m++){
                  xx[na][m] = xx[na][m] + xi[n][m];
               }
            }
         }
         
      }else{
         for(int i=0; i<mm.length; i++){
            double[][] xi = trans.dq2dx(mm[i], dq[i]);
            for(int n=0; n<xi.length; n++){
               for(int m=0; m<3; m++){
                  xx[n][m] = xx[n][m] + xi[n][m];
               }
            }
         }
         
      }
      return xx;
   }
   
   /**
    * Returns a gradient in terms of vibrational coordinates.
    * @param fullgrad gradient
    * @return Gradient in Q
    */
   public double[] gx2gq(double[] fullgrad){
      
      double[] gradQ = null;
      if(atomindex3 == null){
         gradQ = trans.gx2gq(fullgrad);
      }else{
         double[] redgrad = new double[atomindex3.length];
         
         for(int i1=0; i1<atomindex3.length; i1++){
            redgrad[i1] = fullgrad[atomindex3[i1]];
         }
         gradQ = trans.gx2gq(redgrad);
      }
      return gradQ;
   }

   /**
    * Returns a Hessian matrix in terms of vibrational coordinates.
    * @param fullhess hessian
    * @return Hessian in Q
    */
   public double[][] hx2hq(double[] fullhess){
      
      double[][] hessQ = null;
      if(atomindex3 == null){
         hessQ = trans.hx2hq(fullhess);
      }else{
         double[] redhess = new double[atomindex3.length*(atomindex3.length+1)/2];
         
         for(int i1=0; i1<atomindex3.length; i1++){
            int i2 = atomindex3[i1];
            for(int j1=0; j1<=i1; j1++){
               int j2 = atomindex3[j1];
               
               int k1 = i1*(i1+1)/2 + j1;
               int k2 = 0;
               if(i2 > j2){
                  k2 = i2*(i2+1)/2 + j2;
               }else{
                  k2 = j2*(j2+1)/2 + i2;
               }
               redhess[k1] = fullhess[k2];
            }
         }               
         hessQ = trans.hx2hq(redhess);
      }
      
      return hessQ;
   }
}
