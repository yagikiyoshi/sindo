import java.io.*;

import vibration.CoordProperty;
import vibration.CoordinateData;
import vibration.MolToVib;

import makePES.MCStrength;
import makePES.QFFData;
import molecule.MInfoIO;


public class AnalyzeFC {

   private int[] histogram;
   private int nn;
   private double d0;
   private double[] ss;
   private double cutoff;
   private int[] activeModes; 
   private MCStrength mcs;
   
   public AnalyzeFC(){
      d0 = 100.0;
      nn = 12;
      ss = new double[nn];
      cutoff = 1.0e+10;
      double dd = d0;
      for(int i=0; i<nn; i++){
         ss[i] = dd;
         dd = dd/10.0;
      }
   }
   
   public static void main(String[] args) {
      
      if(args.length < 1){
         System.out.println("USAGE: > java AnalyzeFC xxx [cutoff]");
         System.out.println("         xxx = mop file");
         System.out.println("      cutoff = print cutoff (default = disabled)");
         System.exit(-1);
      }

      QFFData data = new QFFData();
      File mopFile = new File(args[0]);
      data.readmop(mopFile);
      int MR = data.getMR();
      int Nfree = data.getNfree();

      MCStrength mcs = new MCStrength();
      mcs.appendQFF(data);
      
      AnalyzeFC ana = new AnalyzeFC();
      ana.appendMCS(mcs);
      if(args.length > 1){
         ana.setCutoff(Double.parseDouble(args[1]));
      }
      
      int[] activemodes;
      if(args.length > 2){
         activemodes = new int[args.length-2];
         for(int i=0; i<activemodes.length; i++){
            activemodes[i] = Integer.parseInt(args[2+i])-1;
         }
      }else{
         activemodes = new int[Nfree];
         for(int i=0; i<Nfree; i++){
            activemodes[i] = i;
         }
      }
      ana.setActivemodes(activemodes);
      
      double[] xx = ana.getXaxis();
      int[] mr2 = ana.makeHistogram("2MR");
      int[] mr3 = ana.makeHistogram("3MR");
      if(MR <= 3) {
         System.out.printf("      MCS         2MR      3MR    \n");
         for(int i=0; i<xx.length; i++){
            System.out.printf(" %10.1e  %8d %8d \n", xx[i],mr2[i],mr3[i]);
         }
      }else{
         System.out.printf("      MCS         2MR      3MR      4MR \n");
         int[] mr4 = ana.makeHistogram("4MR");
         for(int i=0; i<xx.length; i++){
            System.out.printf(" %10.1e  %8d %8d %8d \n", xx[i],mr2[i],mr3[i],mr4[i]);
         }
      }

   }
   
   public void appendMCS(MCStrength mcs){
      this.mcs = mcs;
   }
   
   public void setCutoff(double cutoff){
      this.cutoff = cutoff;
   }
   
   public void setActivemodes(int[] modes){
      this.activeModes = modes;
   }
   
   public int[] makeHistogram(String option){
      histogram = new int[nn];
      int Nfree = mcs.getQFF().getNfree();
      
      if(option.equals("2MR")){
         double aa = 0.0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               aa = mcs.get2mrMCS(i, j);
               this.hist(aa);
               if(aa > cutoff){
                  System.out.printf("%5d %5d             %12.6f \n",(i+1),(j+1),aa);
               }
            }
         }
         
      }else if(option.equals("3MR")){
         double aa = 0.0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               for(int k=0; k<j; k++){
                  aa = mcs.get3mrMCS(i, j, k);
                  this.hist(aa);                  
                  if(aa > cutoff){
                     System.out.printf("%5d %5d %5d       %12.6f \n",(i+1),(j+1),(k+1),aa);
                  }
               }
            }
         }
         
      }else if(option.equals("4MR")){
         double aa = 0.0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               for(int k=0; k<j; k++){
                  for(int l=0; l<k; l++){
                     aa = mcs.get4mrMCS(i, j, k, l);
                     this.hist(aa);
                     if(aa > cutoff){
                        System.out.printf("%5d %5d %5d %5d %12.6f \n",(i+1),(j+1),(k+1),(l+1),aa);
                     }
                  }
               }
            }
         }
         
      }
      
      return histogram;
   }

   public double[] getXaxis(){
      return ss;
   }
   
   private void hist(double aa){
      
      for(int i=0; i<nn-1; i++){
         if(aa > ss[i]){
            histogram[i]++;
            return;
         }
      }
      histogram[nn-1]++;
      
   }
}
