package vibration;

/**
 * Provides the information of the vibrational band.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class Band {

   private double pos;
   private double itt;
   private String name;
   private int mode;
   
   /**
    * Sets a position of the band
    * @param position the position
    */
   public void setPosition(double position){
      this.pos = position;
   }
   /** 
    * Returns the position of the band
    * @return the position
    */
   public double getPosition(){
      return this.pos;
   }
   /**
    * Sets an intensity of the band
    * @param intensity the intensity
    */
   public void setIntensity(double intensity){
      this.itt = intensity;
   }
   /**
    * Returns the intensity of the band
    * @return the intensity
    */
   public double getIntensity(){
      return this.itt;
   }
   /**
    * Sets the name of the band 
    * @param name the name
    */
   public void setName(String name) {
      this.name = name;
   }
   /**
    * Returns the name of the band
    * @return the name
    */
   public String getName() {
      return name;
   }
   /**
    * Returns the mode number
    * @return the mode number
    */
   public int getModeNumber() {
      return mode;
   }
   /**
    * Sets the mode number
    * @param mode the mode number
    */
   public void setModeNumber(int mode) {
      this.mode = mode;
   }
}
