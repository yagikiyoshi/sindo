package sindo;

public class VibState {

   private Conf[] conf;
   private double[] CIcoeff;
   private double Energy;
   private double shift;
   private double IRintensity;
   private double factor;
   private Conf  mainConf=null;
   
   public VibState() {
      mainConf = null;
      factor = 1.0;
      shift  = 0.0;
   }
   
   public Conf getMainConf() {
      int idx = 0;
      if(mainConf == null) {
         double wmax = 0.0d;
         for(int n=0; n<CIcoeff.length; n++) {
            double wwn = CIcoeff[n]*CIcoeff[n];
            if(wwn > wmax) {
               idx  = n;
               wmax = wwn;
            }
         }
         mainConf = conf[idx];
      }
      
      return mainConf;
   }
   
   /**
    * Sets the VSCF configurations for this state
    * @param conf An array of VSCF conf.
    */
   void setConf(Conf[] conf) {
      this.conf = conf;
      mainConf  = null;
   }
   
   /**
    * Sets the CI coefficients for this state
    * @param CIcoeff An array of CI coeff.
    */
   void setCIcoeff(double[] CIcoeff) {
      this.CIcoeff = CIcoeff;
   }
   
   /**
    * Sets the energy of this state
    * @param energy The energy in cm-1
    */
   void setEnergy(double energy) {
      Energy = energy;
   }
   
   /**
    * Sets the IR intensity of this state
    * @param iRintensity The IR intensity
    */
   void setIRintensity(double iRintensity) {
      IRintensity = iRintensity;
   }
   
   /**
    * Returns VSCF configuration functions
    * @return an array of VSCF conf.
    */
   public Conf[] getConf() {
      return conf;
   }
   
   /**
    * Returns CI coefficients
    * @return an arrary of CI coeff.
    */
   public double[] getCIcoeff() {
      return CIcoeff;
   }
   
   /**
    * Returns the vibrational energy level in cm-1
    * @return The energy level
    */
   public double getEnergy() {
      return Energy+shift;
   }
   
   /**
    * Returns the IR intensity
    * @return The IR intensity
    */
   public double getIRintensity() {
      return IRintensity*factor;
   }
   
   /**
    * Shift the energy level by a given amount
    * @param shift the shift to be added. (E = E_org + shift)
    */
   public void shiftEnergy(double shift) {
      this.shift = shift;
   }

   /**
    * Disables a shift to the energy
    */
   public void unshiftEnergy() {
      this.shift = 0.0;
   }
   
   /**
    * Sets a scaling factor for IR intensity
    * @param factor the factor
    */
   public void scaleIR(double factor) {
      this.factor = factor;
   }
   
   /**
    * Disables a scaling factor for IR intensity
    */
   public void unscaleIR() {
      this.factor = 1.0;
   }
}
