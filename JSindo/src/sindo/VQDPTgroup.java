package sindo;

import java.util.*;

/**
 * Stores the output data of VQDPT calculation
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since 3.4
 *
 */
public class VQDPTgroup {

   private int Np;
   private Conf[] pConf;
   private double[][] CIcoeff;
   private double[] Energy;
   private double[] IRintensity;
   private int Ntarget;
   private int[] idxTarget;
   
   public int getNp() {
      return Np;
   }
   public void setNp(int np) {
      Np = np;
   }
   public Conf[] getpConf() {
      return pConf;
   }
   public void setpConf(Conf[] pConf) {      
      this.pConf = pConf;
   }
   public double[][] getCIcoeff() {
      return CIcoeff;
   }
   public void setCIcoeff(double[][] cIcoeff) {
      CIcoeff = cIcoeff;
   }
   public double[] getEnergy() {
      return Energy;
   }
   public void setEnergy(double[] energy) {
      Energy = energy;
   }
   public double[] getIRintensity() {
      return IRintensity;
   }
   public void setIRintensity(double[] iRintensity) {
      IRintensity = iRintensity;
   }
   public int getNtarget() {
      return Ntarget;
   }
   public void setNtarget(int ntarget) {
      Ntarget = ntarget;
   }
   public int[] getIdxTarget() {
      return idxTarget;
   }
   public void setIdxTarget(int[] idxTarget) {
      this.idxTarget = idxTarget;
   }
   
   public ArrayList<VibState> getVibState(){
      
      ArrayList<VibState> state = new ArrayList<VibState>(Np);      
      for(int n=0; n<Np; n++){
         VibState vs = new VibState();
         vs.setEnergy(Energy[n]);
         vs.setCIcoeff(CIcoeff[n]);
         vs.setConf(pConf);
         if(IRintensity != null){            
            vs.setIRintensity(IRintensity[n]);
         }
         state.add(vs);
      }
      
      return state;
   }

   
}
