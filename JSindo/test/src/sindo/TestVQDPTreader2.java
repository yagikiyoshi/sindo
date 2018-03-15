package sindo;

import java.util.*;
import java.io.*;
import vibration.*;

public class TestVQDPTreader2 {

   public static void main(String[] args) {
      VQDPTreader vread = new VQDPTreader();
      vread.setInfrared("test/sindo/vqdpt-IR_bR.data");
      VQDPTdata vqdpt_data = vread.read("test/sindo/vqdpt-w_bR.wfn");
      
      double zpe = vqdpt_data.getZPE();

      System.out.println("State   Energy    IR intensity");

      int nst = 0;
      ArrayList<VibState> stateList = vqdpt_data.getAllStates();
      //Collections.sort(stateList, new VQDPTComparator(1,true));
      for(int n=0; n<stateList.size(); n++) {
         VibState state = stateList.get(n);
         double energy = state.getEnergy();
         double intensity = state.getIRintensity();
         
         System.out.printf("%5d %10.2f %10.4f \n",nst, (energy-zpe), intensity);
         
         nst++;
      }
      
      double gamma = 20.0;
      SindoUtil sutil = new SindoUtil();
      BandProfile spec = sutil.getIRspectrum(vqdpt_data, gamma);
      
      try{
         PrintWriter pw = new PrintWriter("test/sindo/spectrum.txt");
         int nstep = 2800;
         for(int ss=0; ss<nstep; ss++){            
            double omega = 800.0 + (double)ss;
            pw.printf("%12.2f %12.4f \n",omega, spec.getIntensity(omega));
         }
         pw.close();
      }catch(IOException e){
         e.printStackTrace();
      }
      

   }
}
