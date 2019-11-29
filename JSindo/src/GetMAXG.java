
import md.*;
import java.io.*;

public class GetMAXG {

   public static void main(String[] args) {
      
      if (args.length == 0) {
         System.out.println("Usage: java GetMAXG log");
         System.out.println(" log : logfiles");
         System.exit(0);
      }
      
      String[] logname = new String[args.length];
      int ndata = 0;
      for(int n=0; n<args.length; n++) {
         logname[ndata] = args[n];
         ndata++;
      }
      
      for(int n=0; n<ndata; n++) {
         
         String log = logname[n];
         String dat = null;
         
         int idx = log.lastIndexOf(".");
         if (idx > 0) {
            dat = log.substring(0, idx)+".dat";
         }else {
            dat = log+".dat";
         }
         //System.out.println(dat);
         
         GenesisLog glog = new GenesisLog();
         glog.readInfo(log);
         
         Double[] step = glog.getData("STEP");
         Double[] total_ene = glog.getData("POTENTIAL_ENE");
         Double[] rmsg  = glog.getData("RMSG");
         Double[] maxg  = glog.getData("MAXG");
         
         try {
            PrintWriter pw = new PrintWriter(dat);
            
            double min_maxg = 1000.0;
            int    step_maxg = 0;
            for(int i=0; i<step.length; i++) {
               if(min_maxg > maxg[i]) {
                  min_maxg = maxg[i];
                  step_maxg = i;
               }
               pw.printf("%12d  %15.4f %15.4f %15.4f \n", step[i].intValue(), total_ene[i], rmsg[i], maxg[i]);
            }
            
            pw.println();
            pw.println("Step of min_MAXG: "+step[step_maxg].intValue());
            pw.printf("%12d  %15.4f %15.4f %15.4f \n", step[step_maxg].intValue(), total_ene[step_maxg], rmsg[step_maxg], maxg[step_maxg]);
               
            pw.close();
            
         } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
         }

      }
      
   }
}
