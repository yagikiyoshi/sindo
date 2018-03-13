package sindo;

import java.util.*;

public class TestVQDPTreader2 {

   public static void main(String[] args) {
      VQDPTreader vread = new VQDPTreader();
      vread.setInfrared("test/sindo/vqdpt-IR_bR.data");
      VQDPTdata vqdpt_data = vread.read("test/sindo/vqdpt-w_bR.wfn");
      
      double zpe = vqdpt_data.getZPE();

      System.out.println("State   Energy    IR intensity");

      int nst = 0;
      ArrayList<VQDPTstate> stateList = vqdpt_data.getAllStates();
      //Collections.sort(stateList, new VQDPTComparator(1,true));
      for(int n=0; n<stateList.size(); n++) {
         VQDPTstate state = stateList.get(n);
         double energy = state.getEnergy();
         double intensity = state.getIRintensity();
         
         System.out.printf("%5d %10.2f %10.4f \n",nst, (energy-zpe), intensity);
         
         nst++;
      }
   }
}
