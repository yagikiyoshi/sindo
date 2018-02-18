package sindo;

import java.util.*;

public class VQDPTComparator implements Comparator<VQDPTstate>{

   public int compare(VQDPTstate va, VQDPTstate vb){
      double ir_a = va.getIRintensity();
      double ir_b = vb.getIRintensity();
      
      if(ir_a > ir_b){
         return -1;
//      }else if (Math.abs(ir_a - ir_b) < 1e-5){
//         return 0;
      }else{
         return 1;
      }
   }
}
