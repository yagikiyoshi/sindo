package sindo;

import java.util.*;

public class VibComparator implements Comparator<VibState>{

   int option;
   boolean reverse;
   
   /**
    * Default constructor of Comparator. Energy in increasing order.
    */
   public VibComparator() {
      this.option  = 0;
      this.reverse = false;
   }
   
   /**
    * Constructor of Comparator of normal (increasing) order. 
    * @param option 0: Energy, 1: IR intensity
    */
   public VibComparator(int option) {
      this.option  = option;
      this.reverse = false;
   }
   
   /**
    * Constructor of Comparator
    * @param option 0: Energy, 1: IR intensity
    * @param reverse If true, decreasing order
    */
   public VibComparator(int option, boolean reverse) {
      this.option  = option;
      this.reverse = reverse;
   }
   
   public int compare(VibState va, VibState vb){
      
      int ii=1, jj=-1;
      if(reverse) {
         ii = -1;
         jj = 1;
      }
         
      double energy_a = va.getEnergy();
      double energy_b = vb.getEnergy();
      double ir_a = va.getIRintensity();
      double ir_b = vb.getIRintensity();
      
      if(option == 1){
         if(ir_a > ir_b){
            return ii;
         }else{
            return jj;
         }
      }else {
         if(energy_a > energy_b) {
            return ii;
         }else {
            return jj;
         }
      }
         
   }
}
