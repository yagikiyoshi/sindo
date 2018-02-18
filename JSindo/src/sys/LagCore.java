package sys;

/**
 * Core class for Lagrange interpolation.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class LagCore {
   
   protected double core0(double x, double[] xg){
      double p0=1.0;
      for(int i=0; i<xg.length; i++){
         p0 = p0 * (x-xg[i]);
      }
      return p0;
   }
   protected double core1(int mi, double x, double[] xg){
      double p0=1.0;
      for(int i=0; i<xg.length; i++){
         if(i != mi) p0 = p0 * (x-xg[i]);
      }
      return p0;
   }
   protected double core2(int mi, int mj, double x, double[] xg){
      double p0=1.0;
      for(int i=0; i<xg.length; i++){
         if(i != mi && i != mj) p0 = p0 * (x-xg[i]);
      }
      return p0;
   }
   protected double core3(int mi, int mj, int mk, double x, double[] xg){
      double p0=1.0;
      for(int i=0; i<xg.length; i++){
         if(i != mi && i != mj && i != mk) p0 = p0 * (x-xg[i]);
      }
      return p0;
   }

}
