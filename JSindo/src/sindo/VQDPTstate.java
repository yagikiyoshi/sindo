package sindo;

public class VQDPTstate implements Comparable<VQDPTstate>{

   private Conf[] pConf;
   private double[] CIcoeff;
   private double Energy;
   private double IRintensity;
   
   public Conf[] getpConf() {
      return pConf;
   }
   public void setpConf(Conf[] pConf) {
      this.pConf = pConf;
   }
   public double[] getCIcoeff() {
      return CIcoeff;
   }
   public void setCIcoeff(double[] CIcoeff) {
      this.CIcoeff = CIcoeff;
   }
   public double getEnergy() {
      return Energy;
   }
   public void setEnergy(double energy) {
      Energy = energy;
   }
   public double getIRintensity() {
      return IRintensity;
   }
   public void setIRintensity(double iRintensity) {
      IRintensity = iRintensity;
   }
   
   public int compareTo(VQDPTstate o) {
      
      if(this.Energy > o.Energy){
         return 1;
      }else{
         return -1;
      }
      
   }

}
