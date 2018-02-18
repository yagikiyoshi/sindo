package vibration;

/**
 * (Package private) Driver class for LocalCoord.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public abstract class Localiza {
   
   /**
    * Number of atom
    */
   protected int Nat;
   /**
    * Nat * 3
    */
   protected int Nat3;
   /**
    * Number of vibrational degrees of freedom
    */
   protected int Nfree;
   /**
    * Vibrational displacement vectors
    */
   protected double[][] CL;
   
   /**
    * This class is package private.
    */
   Localiza(){
      
   }
   /**
    * Setup the driver class
    * @param coordData Coordinate Data
    */
   public void setup(CoordinateData coordData){
      this.CL = coordData.getCV();
      Nat = coordData.Nat;
      Nfree = coordData.Nfree;
      Nat3 = Nat*3;
   }
   /**
    * Returns the K value (i&gt;j and k&gt;l).
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @param l mode l
    * @return K value
    */
   public abstract double getK(int i, int j, int k, int l);
   /**
    * Returns the Z value
    * @param modes combination of modes
    * @return Z value
    */
   public abstract double getZeta(int[] modes);
   /**
    * Updates the information
    * @param i mode i
    * @param j mode j
    */
   public abstract void update(int i, int j);

}
