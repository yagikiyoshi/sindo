
import md.*;
import java.io.*;

public class GetPotEnergy {

   public static void main(String[] args) {
      
      if (args.length == 0) {
         System.out.println("Usage: java GetPotEnergy [--throw N] [--average 10] log1 log2 ");
         System.out.println(" log1 log2 : logfiles");
         System.out.println(" (optional) --throw N: Throw the first N steps for calculating the average and RMSD");
         System.out.println(" (optional) --average N: Also obtain averaged value over N windows");
         System.exit(0);
      }
      
      int nthrow = 0;
      int nave   = 0;
      String[] logname = new String[args.length];
      int ndata = 0;
      for(int n=0; n<args.length; n++) {
         if (args[n].equals("--throw")) {
            nthrow = Integer.parseInt(args[n+1]);
            n++;
            
         }else if (args[n].equals("--average")) {
            nave = Integer.parseInt(args[n+1]);
            n++;

         }else {
            logname[ndata] = args[n];
            ndata++;
         }
      }
      
      double[] ave = new double[ndata];
      double[] rmsd = new double[ndata];

      for(int n=0; n<ndata; n++) {
         
         String log = logname[n];
         String dat = null;
         
         int idx = log.lastIndexOf(".");
         if (idx > 0) {
            dat = log.substring(0, idx)+"_pot.dat";
         }else {
            dat = log+".dat";
         }
         //System.out.println(dat);
         
         GenesisLog glog = new GenesisLog();
         glog.readInfo(log);
         
         Double[] time = glog.getData("TIME");
         Double[] pot_ene = glog.getData("POTENTIAL_ENE");
         
         ave[n] = 0.D+00;
         for(int i=nthrow; i<time.length; i++) {
            ave[n] += pot_ene[i];
         }
         ave[n] = ave[n] / (double)(pot_ene.length - nthrow);

         rmsd[n] = 0.D+00;
         for (int i=nthrow; i< pot_ene.length; i++) {
            rmsd[n] += (pot_ene[i] - ave[n])*(pot_ene[i] - ave[n]);
         }
         rmsd[n] = Math.sqrt(rmsd[n]/(double)(pot_ene.length - nthrow));
  

         for(int i=0; i<time.length; i++) {
            pot_ene[i] = pot_ene[i] - ave[n];
         }
         
         try {
            PrintWriter pw = new PrintWriter(dat);
            pw.printf("# average energy : %15.4f \n", ave[n]);
            
            if(nave > 0) {
               for(int i=0; i<time.length; i++) {
                  int ist = i - nave;
                  if(ist < 0) ist = 0;
                  Double eave = 0.0d;
                  for(int j=ist; j<=i; j++) {
                     eave += pot_ene[j];
                  }
                  eave = eave / (double)(i - ist +1);
                  pw.printf("%12.4f  %15.4f   %15.4f \n", time[i], pot_ene[i], eave);
               }
               
               
            }else {
               for(int i=0; i<time.length; i++) {
                  pw.printf("%12.4f  %15.4f \n", time[i], pot_ene[i]);
               }
               
            }
            pw.close();
            
         } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
         }

         
      }
      
      System.out.printf("ave  = ");
      for(int n=0; n<ndata; n++) {
         System.out.printf("%10.2f ", ave[n]);
      }
      System.out.println();
      System.out.printf("rmsd = ");
      for(int n=0; n<ndata; n++) {
         System.out.printf("%10.2f ", rmsd[n]);
      }
      System.out.println();
   }
}
