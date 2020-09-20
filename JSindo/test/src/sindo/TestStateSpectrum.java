package sindo;

import vibration.*;
import java.io.*;

public class TestStateSpectrum {

   public static void main(String[] args) {
      
      VQDPTreader vread = new VQDPTreader();
      vread.setInfrared("test/sindo/vqdpt-IR_eigen_18m.data");
      VQDPTdata vqdpt_data = vread.read("test/sindo/vqdpt-w_eigen_18m.wfn");
      
      StateSpectrum spec = new StateSpectrum("eigen");
      
      spec.setTarget(new Conf("1 19 1"));
      spec.setTarget(new Conf("1 20 1"));
      spec.setTarget(new Conf("1 21 1"));
      spec.setTarget(new Conf("1 22 1"));
      spec.setTarget(new Conf("1 23 1"));
      spec.setTarget(new Conf("1 24 1"));
      
      spec.extract(vqdpt_data);
      BandProfile spectrum = spec.getBandProfile(5.0);
      try{
         PrintWriter pw = new PrintWriter("test/sindo/"+spec.getLabel()+".spectrum");
         int nstep = 1001;
         for(int ss=0; ss<nstep; ss++){
            
            double omega = 3000.0 + (double)ss;
            
            pw.printf("%12.2f",omega);
            pw.printf("%12.4f",spectrum.getIntensity(omega));
            pw.println();
         }
         pw.close();
      }catch(IOException e){
         e.printStackTrace();
      }

      System.out.println(spec.printStates());
      

   }
   
}
