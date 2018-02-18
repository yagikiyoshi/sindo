package md;

/**
 * (Package private) Stores the data of periodic boundary box for MD. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class Box {

   private double xsize;
   private double ysize;
   private double zsize;
   private double alpha;
   private double beta;
   private double gamma;
   private String group ="";

   /** 
    * This is a package private class
   Box(){
   }
    */
   
   /**
    * Returns the size of box (bohr/degree)
    * @return [0]: xsize, [1]: ysize, [2]: zsize, [3]: alpha, [4]: beta, [5]: gamma
    */
   public double[] getBox(){
      double[] boxSize = new double[6];
      boxSize[0] = xsize;
      boxSize[1] = ysize;
      boxSize[2] = zsize;
      boxSize[3] = alpha;
      boxSize[4] = beta;
      boxSize[5] = gamma;
      return boxSize;
   }
   
   /**
    * Sets the size of box (bohr/degree)
    * @param boxSize [0]: xsize, [1]: ysize, [2]: zsize, [3]: alpha, [4]: beta, [5]: gamma
    */
   public void setBox(double[] boxSize){
      xsize = boxSize[0];
      ysize = boxSize[1];
      zsize = boxSize[2];
      alpha = boxSize[3];
      beta  = boxSize[4];
      gamma = boxSize[5];
   }

   /**
    * Returns the size of x
    * @return xsize (bohr)
    */
   public double getXsize() {
      return xsize;
   }
   /**
    * Sets the size of x
    * @param xsize (bohr)
    */
   public void setXsize(double xsize) {
      this.xsize = xsize;
   }

   /**
    * Returns the size of y
    * @return ysize (bohr)
    */
   public double getYsize() {
      return ysize;
   }

   /**
    * Sets the size of y
    * @param ysize (bohr)
    */
   public void setYsize(double ysize) {
      this.ysize = ysize;
   }

   /**
    * Returns the size of z
    * @return zsize (bohr)
    */
   public double getZsize() {
      return zsize;
   }

   /**
    * Sets the size of z
    * @param zsize (bohr)
    */
   public void setZsize(double zsize) {
      this.zsize = zsize;
   }

   /**
    * Returns alpha (degree)
    * @return alpha
    */
   public double getAlpha() {
      return alpha;
   }

   /**
    * Sets alpha (degree)
    * @param alpha box angle
    */
   public void setAlpha(double alpha) {
      this.alpha = alpha;
   }

   /**
    * Returns beta (degree)
    * @return beta box angle
    */
   public double getBeta() {
      return beta;
   }

   /**
    * Sets beta (degree)
    * @param beta box angle
    */
   public void setBeta(double beta) {
      this.beta = beta;
   }

   /**
    * Returns gamma (degree)
    * @return gamma box angle
    */
   public double getGamma() {
      return gamma;
   }

   /**
    * Sets gamma (degree)
    * @param gamma box angle
    */
   public void setGamma(double gamma) {
      this.gamma = gamma;
   }

   /**
    * Returns the symmetry group
    * @return description of the group
    */
   public String getGroup() {
      return group;
   }

   /**
    * Sets the symmetry group
    * @param group description of the group
    */
   public void setGroup(String group) {
      this.group = group;
   }

}
