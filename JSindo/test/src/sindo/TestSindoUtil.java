package sindo;

import vibration.*;

import java.io.*;

public class TestSindoUtil {

   public static void main(String[] args) {
      
      VQDPTreader vread = new VQDPTreader();
      vread.setInfrared("test/sindo/vqdpt-IR_eigen_18m.data");
      VQDPTdata vqdpt_data = vread.read("test/sindo/vqdpt-w_eigen_18m.wfn");
      
      SindoUtil sutil = new SindoUtil();
      BandProfile IRspec = sutil.getIRspectrum(vqdpt_data, 10.0);

      
      try {
         PrintWriter pw = new PrintWriter("test/sindo/vqdpt-IR_eigen_18m.spectrum");
         double ww = 600.0;
         double dw = 1.0;
         int nn = (int)((4000.0 - ww)/dw);
         for(int i=0; i<nn+1; i++){
            pw.printf("%8.1f  %12.6f \n", ww, IRspec.getIntensity(ww));
            ww += dw;
         }
         pw.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      
      
   }

      
}
