package sindo;

import sys.Utilities;

/**
 * VSCF configuration <br>
 * o nCUP : the number of modes excited
 * o mm[nCUP] : the excited modes
 * o vv[nCUP] : the quantum numbers of the modes
 *  
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.4
 *
 */
public class Conf {

   private int nCUP;
   private int[] mm,vv;
   
   /**
    * Construct VSCF configuration
    * @param nCUP the number of modes excited
    * @param mm the excited modes
    * @param vv the quantum numbers of the modes
    */
   public Conf(int nCUP, int[] mm, int[] vv){
      this.nCUP = nCUP;
      this.mm = mm;
      this.vv = vv;
   }
   
   /**
    * Construct VSCF configuration from one line data
    * @param vscf_conf The data separated with space: "nCUP mm1 mm2 .. mm(nCUP) vv1 vv2 ... vv(nCUP)"
    */
   public Conf(String vscf_conf){
      int[] conf = Utilities.splitWithSpaceInt(vscf_conf);
      nCUP = conf[0];
      mm = new int[nCUP];
      vv = new int[nCUP];
      for(int i=0; i<nCUP; i++){
         mm[i] = conf[i+1];
         vv[i] = conf[i+nCUP+1];
      }
   }
   
   /**
    * Returns the number of modes excited
    * @return nCUP
    */
   public int getnCUP(){
      return nCUP;
   }
   
   /**
    * Returns the index of modes excited 
    * @return modes[nCUP]
    */
   public int[] getMode(){
      return mm;         
   }
   
   /**
    * Returns the excitation levels
    * @return vv[nCUP]
    */
   public int[] getExc(){
      return vv;
   }
 
   /**
    * Returns true if the current configuration is the fundamentals
    * @return true if fundamentals
    */
   public boolean isFundamental(){
      boolean fund = false;
      if(nCUP == 1 && vv[0] == 1){
         fund = true;
      }
      return fund;
   }
   /**
    * Returns the VSCF configuration
    * @return mm1_vv1 mm2_vv2 ...
    */
   public String print(){
      String conf = "";
      for(int i=0; i<nCUP; i++){
         conf = conf + mm[i]+"_"+vv[i]+" ";
      }
      return conf;
   }
   
   /**
    * Returns the VSCF configuration with input mode id
    * @param mode_id id of the mode
    * @return id[mm1]_vv1 id[mm2]_vv2 ...
    */
   public String print(int[] mode_id){
      String conf = "";
      for(int i=0; i<nCUP; i++){
         conf = conf + mode_id[mm[i]-1]+"_"+vv[i]+" ";
      }
      return conf;
   }
   
   public double getSign(int[] mode_sign){
      double sign = 1.0d;
      for(int i=0; i<nCUP; i++){
         sign = sign * Math.pow(mode_sign[mm[i]-1],vv[i]);
      }
      return sign;
      
   }
   
}
