package vibration;

import java.util.*;

/**
 * Utility class to obtain the vibrational spectrum.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class BandProfile {

   private double gamma,gamma2;
   private ArrayList<Band> bandList;
   
   /**
    * Default constructor with gamma = 10.0 for the Lorentz function 
    */
   public BandProfile(){
      this(10.0);
   }
   
   /**
    * Constructs the class with a specified Gamma value for the Lorentz function
    * @param gamma the half width at the half maximum of the Lorentzian 
    */
   public BandProfile(double gamma){
      this.gamma = gamma;
      this.gamma2 = gamma*gamma;
      this.bandList = new ArrayList<Band>();
   }
   
   /**
    * Sets the information of the vibrational bands
    * @param bands list of bands (includes position and intensity)
    */
   public void setBand(ArrayList<Band> bands){
      this.bandList = bands;
   }

   /**
    * Sets the information of the vibrational bands
    * @param omega band position
    * @param intensity band intensity
    */
   public void setBand(double[] omega, double[] intensity){
      for(int i=0; i<omega.length; i++){
         Band b = new Band();
         b.setPosition(omega[i]);
         b.setIntensity(intensity[i]);
         bandList.add(b);
      }
   }
   
   public void addBand(Band band){
      bandList.add(band);
   }
   
   /**
    * Returns the intensity at a given wavenumber
    * @param omega the wavenumber
    * @return Intensity
    */
   public double getIntensity(double omega){
      double intensity = 0.0;
      for(int i=0; i<bandList.size(); i++){
         Band band = bandList.get(i);
         intensity = intensity + this.Lorentzian(omega, band.getPosition(), band.getIntensity());
      }
      /*
      for(int i=0; i<ww.length; i++){
         intensity = intensity + this.Lorentzian(omega, ww[i], intty[i]);
      }
      */
      return intensity;
   }
   
   private double Lorentzian(double omega, double omega0, double A0){
      return 2.0 * A0 * gamma / (4.0 * (omega - omega0)*(omega - omega0) + gamma2) / Math.PI;
   }
   
}
