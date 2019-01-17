package makePES;

import java.io.*;

import jobqueue.QueueMngr;
import sys.Constants;
import sys.Utilities;
import molecule.*;


/**
 * Make quartic force field in normal coordinates
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class MakeQFF {
   
   private int MR;
   private int[][] activeModes;
   private double[] deltaQ;
   private PESInputData inputData;
   private InputDataQFF qffData;
   private InputDataQC  qcData;
   private GrdXYZ grdXYZ;
   private QueueMngr queue;
   
   /**
    * Constructs with the input data
    * @param makePESData Input data
    */
   public MakeQFF(PESInputData makePESData, InputDataQFF qffData, InputDataQC qcData){

      System.out.println("Setup MakeQFF module");
      System.out.println();
      
      this.inputData = makePESData;
      this.qffData   = qffData;
      this.qcData    = qcData;
      this.activeModes = makePESData.getActiveModes();
      
      VibrationalData vdata = makePESData.getMolecule().getVibrationalData();
      MR = makePESData.getMR();

      deltaQ = Utilities.deepCopy(vdata.getOmegaV());
      double stepsize = qffData.getStepsize();
      {
         int i=0;
         while(deltaQ[i]<0.0d){
            deltaQ[i] = stepsize/Math.sqrt(-deltaQ[i]/Constants.Hartree2wvn);
            i++;
         }
         while(i<deltaQ.length){
            deltaQ[i] = stepsize/Math.sqrt(deltaQ[i]/Constants.Hartree2wvn);
            //System.out.println(deltaQ[i]*Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu));
            i++;
         }         
      }

   }
   
   public static String getBasename(){
       return PESInputData.MINFO_FOLDER+"mkqff-eq";
   }
   public static String getBasename(int mi){
       return PESInputData.MINFO_FOLDER+"mkqff"+mi;
   }
   public static String getBasename(int mi, int mj){
      return PESInputData.MINFO_FOLDER+"mkqff"+mi+"_"+mj;
   }
   public static String getBasename(int mi, int mj, int mk){
      return PESInputData.MINFO_FOLDER+"mkqff"+mi+"_"+mj+"_"+mk;
   }
   
   /**
    * Main module of QFF generation
    */
   public void runMkQFF(){

      File hs = new File("001.hs");
      File mop = new File(qffData.getMopfile());
      if(hs.exists() || mop.exists()){
         System.out.println();
         System.out.println("QFF data file already exists. Exit MakeQFF module.");
         System.out.println();
         return;

      }
      
      System.out.println();
      System.out.println("Enter QFF generation:");
      System.out.println();
      System.out.println("Execute electronic structure calculations.");
      System.out.println();

      String minfo_folder = null;
      if(inputData.isRunQchem()){
         File minfodir = new File(PESInputData.MINFO_FOLDER);
         if(! minfodir.exists()){
            minfodir.mkdir();
         }

         queue = QueueMngr.getInstance();
         queue.start();
         
      }else{
         minfo_folder = PESInputData.MINFO_FOLDER;
         PESInputData.MINFO_FOLDER = "";
         grdXYZ = new GrdXYZ(qffData.getXYZFile_basename());
         
      }
      
      this.processGrid(null, null, MakeQFF.getBasename());
      
      if(qffData.getNdifftype().equals("HESS")){
         this.runHessian();
      }else if(qffData.getNdifftype().equals("GRAD")){
         this.runGradient();
      }else if(qffData.getNdifftype().equals("ENE")){
         // TODO
         // this.runEnergy();            
      }
      
      if(inputData.isRunQchem()){
         queue.shutdown();            
      }else{
         grdXYZ.close();
         PESInputData.MINFO_FOLDER = minfo_folder;
      }
      
      System.out.println("End of electronic structure calculations.");
      System.out.println();
      
      if(qcData.isDryrun()){
         System.out.println("DryRun is done!");
         System.out.println();
         
      }else{
         this.workTempfiles("dump");
         if(qffData.isGenhs()) this.calcQFF_hs();
         
         if(qffData.getNdifftype().equals("HESS")){
            this.calcQFF_mop_hess();
         }else if(qffData.getNdifftype().equals("GRAD")){
            this.calcQFF_mop_grad();
         }else if(qffData.getNdifftype().equals("ENE")){
            // TODO
            // this.calcQFF_mop_ene;            
         }

         this.workTempfiles("remove");            
      }
      
      System.out.println();
      System.out.println("End of QFF generation.");
      System.out.println();

   }
   
   private void runGradient(){

      for(int n=0; n<activeModes.length; n++){
         if(activeModes[n] == null) continue;
         
         int Nfree = activeModes[n].length;
         for(int i=0; i<Nfree; i++){
            int ii = activeModes[n][i];
            String   si = MakeQFF.getBasename(ii);
            int[]    mi = {ii};
            
            double[] qi0 = {-3.0d*deltaQ[ii]};
            this.processGrid(mi, qi0, si+"-"+0);
            
            double[] qi1 = {-deltaQ[ii]};
            this.processGrid(mi, qi1, si+"-"+1);
            
            double[] qi2 = {deltaQ[ii]};
            this.processGrid(mi, qi2, si+"-"+2);
            
            double[] qi3 = {3.0d*deltaQ[ii]};
            this.processGrid(mi, qi3, si+"-"+3);

         }
         
         for(int i=0; i<Nfree; i++){
            int ii = activeModes[n][i];
            for(int j=0; j<i; j++){
               int jj = activeModes[n][j];
               String sij = MakeQFF.getBasename(ii, jj);
               int[] mij = {ii,jj};
               
               double[] qij0 = {deltaQ[ii],deltaQ[jj]};
               this.processGrid(mij, qij0, sij+"-"+0);
               
               double[] qij1 = {-deltaQ[ii],deltaQ[jj]};
               this.processGrid(mij, qij1, sij+"-"+1);
               
               double[] qij2 = {deltaQ[ii],-deltaQ[jj]};
               this.processGrid(mij, qij2, sij+"-"+2);
               
               double[] qij3 = {-deltaQ[ii],-deltaQ[jj]};
               this.processGrid(mij, qij3, sij+"-"+3);

            }
         }
         
         if(MR == 4){

            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               for(int j=0; j<i; j++){
                  int jj = activeModes[n][j];
                  for(int k=0; k<j; k++){
                     int kk = activeModes[n][k];
                     
                     String sijk = MakeQFF.getBasename(ii, jj, kk);
                     
                     int[] mijk = {ii,jj,kk};
                     
                     double[] qijk0 = {deltaQ[ii],deltaQ[jj],deltaQ[kk]};
                     this.processGrid(mijk, qijk0, sijk+"-"+0);
                     
                     double[] qijk1 = {-deltaQ[ii],deltaQ[jj],deltaQ[kk]};
                     this.processGrid(mijk, qijk1, sijk+"-"+1);
                     
                     double[] qijk2 = {deltaQ[ii],-deltaQ[jj],deltaQ[kk]};
                     this.processGrid(mijk, qijk2, sijk+"-"+2);
                     
                     double[] qijk3 = {-deltaQ[ii],-deltaQ[jj],deltaQ[kk]};
                     this.processGrid(mijk, qijk3, sijk+"-"+3);
     
                     double[] qijk4 = {deltaQ[ii],deltaQ[jj],-deltaQ[kk]};
                     this.processGrid(mijk, qijk4, sijk+"-"+4);
                     
                     double[] qijk5 = {-deltaQ[ii],deltaQ[jj],-deltaQ[kk]};
                     this.processGrid(mijk, qijk5, sijk+"-"+5);
                     
                     double[] qijk6 = {deltaQ[ii],-deltaQ[jj],-deltaQ[kk]};
                     this.processGrid(mijk, qijk6, sijk+"-"+6);
                     
                     double[] qijk7 = {-deltaQ[ii],-deltaQ[jj],-deltaQ[kk]};
                     this.processGrid(mijk, qijk7, sijk+"-"+7);
                  }
               }
            }
         }
      }
      
   }
   
   private void runHessian(){

      for(int n=0; n<activeModes.length; n++){
         if(activeModes[n] == null) continue;

         int Nfree = activeModes[n].length;
         for(int i=0; i<Nfree; i++){
            int ii = activeModes[n][i];

            String   si = MakeQFF.getBasename(ii);
            int[]    mi = {ii};
            
            double[] qi0 = {deltaQ[ii]};
            this.processGrid(mi, qi0, si+"-"+0);
            
            double[] qi1 = {-deltaQ[ii]};
            this.processGrid(mi, qi1, si+"-"+1);
            
         }

         if(MR == 4) {
            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               for(int j=0; j<i; j++){
                  int jj = activeModes[n][j];

                  String sij = MakeQFF.getBasename(ii, jj);
                  int[] mij = {ii,jj};
                  
                  double[] qij0 = {deltaQ[ii],deltaQ[jj]};
                  this.processGrid(mij, qij0, sij+"-"+0);
                  
                  double[] qij1 = {-deltaQ[ii],deltaQ[jj]};
                  this.processGrid(mij, qij1, sij+"-"+1);
                  
                  double[] qij2 = {deltaQ[ii],-deltaQ[jj]};
                  this.processGrid(mij, qij2, sij+"-"+2);
                  
                  double[] qij3 = {-deltaQ[ii],-deltaQ[jj]};
                  this.processGrid(mij, qij3, sij+"-"+3);
                  
               }
            }
         }
      }

   }
   
   private void processGrid(int[] mm, double[] qq, String basename){
      if(inputData.isRunQchem()){
         TaskGrid task = new TaskGrid(inputData,qcData, mm,qq,basename);
         task.setNdifftype(qffData.getNdifftype());
         queue.submit(task);
         
      }else{
         grdXYZ.write(inputData,mm,qq,basename);
         
      }

   }
   
   private void workTempfiles(String runmode) {

      if(runmode.equals("dump")){
         /* --- Store data in tempfile -- */
         
         System.out.println();
         System.out.printf("Storing electronic structure data in tempfile ...  ");
         
      }else if(runmode.equals("remove")){
         /* --- Remove all tempfiles -- */
         
         System.out.println();
         System.out.printf("Removing the tempfiles ...  ");
         
      }

      VibTransformer trans = inputData.getTransform();
     
      for(int n=0; n<activeModes.length; n++){
         if(activeModes[n] == null) continue;
         
         int Nfree = activeModes[n].length;
         if(qffData.getNdifftype().equals("HESS")){
            {
               Thread[] thread = new Thread[Nfree];
               for(int i=0; i<Nfree; i++){
                  int[] mode = new int[1];
                  mode[0] = activeModes[n][i];
                  Tempfileh tempfile = new Tempfileh(mode,2);
                  tempfile.appendTransform(trans);
                  tempfile.setRunMode(runmode);
                  thread[i] = new Thread(tempfile);
                  thread[i].start();
               }
               try {
                  for(int i=0; i<thread.length; i++){
                     thread[i].join();
                  }
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
            
            if(MR == 4) {
               for(int i=1; i<Nfree; i++){
                  Thread[] thread = new Thread[i];
                  for(int j=0; j<i; j++){
                     int[] mode = new int[2];
                     mode[0] = activeModes[n][i];
                     mode[1] = activeModes[n][j];
                     Tempfileh tempfile = new Tempfileh(mode,4);
                     tempfile.appendTransform(trans);
                     tempfile.setRunMode(runmode);
                     thread[j] = new Thread(tempfile);
                     thread[j].start();
                     
                  }
                  try {
                     for(int j=0; j<thread.length; j++){
                        thread[j].join();
                     }
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
            }

         }else{         
            {
               Thread[] thread = new Thread[Nfree];
               for(int i=0; i<Nfree; i++){
                  int[] mode = new int[1];
                  mode[0] = activeModes[n][i];
                  Tempfileg tempfile = new Tempfileg(mode,4);
                  tempfile.appendTransform(trans);
                  tempfile.setRunMode(runmode);
                  thread[i] = new Thread(tempfile);
                  thread[i].start();
               }
               try {
                  for(int i=0; i<thread.length; i++){
                     thread[i].join();
                  }
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }

            for(int i=1; i<Nfree; i++){
               Thread[] thread = new Thread[i];
               for(int j=0; j<i; j++){
                  int[] mode = new int[2];
                  mode[0] = activeModes[n][i];
                  mode[1] = activeModes[n][j];
                  Tempfileg tempfile = new Tempfileg(mode,4);
                  tempfile.appendTransform(trans);
                  tempfile.setRunMode(runmode);
                  thread[j] = new Thread(tempfile);
                  thread[j].start();
                  
               }
               try {
                  for(int j=0; j<thread.length; j++){
                     thread[j].join();
                  }
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
            
            if(MR == 4){
               for(int i=2; i<Nfree; i++){
                  for(int j=1; j<i; j++){
                     Thread[] thread = new Thread[j];
                     for(int k=0; k<j; k++){
                        int[] mode = new int[3];
                        mode[0] = activeModes[n][i];
                        mode[1] = activeModes[n][j];
                        mode[2] = activeModes[n][k];
                        Tempfileg tempfile = new Tempfileg(mode,8);
                        tempfile.appendTransform(trans);
                        tempfile.setRunMode(runmode);
                        //tempfile.run();
                        thread[k] = new Thread(tempfile);
                        thread[k].start();                     
                     }
                     try {
                        for(int k=0; k<thread.length; k++){
                           thread[k].join();
                        }
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                  }
               }
            }
         }      
      }
      
      System.out.println(" Done!");
      
   }
   
   private void calcQFF_hs(){

      /* --- Now compute coefficients and print -- */
      
      System.out.println();
      System.out.printf("Generating 001.hs...");
      
      if(activeModes.length > 1){
         System.out.println("Error!");
         System.out.println("Multi-domain is not supported for generating hs file.");
         return;
      }
      
      int Nfree = inputData.getMolecule().getVibrationalData().Nfree;
      MInfoIO minfo = new MInfoIO();
      VibTransformer trans = inputData.getTransform();

      try{
         minfo.loadMOL(MakeQFF.getBasename()+".minfo");
      }catch(IOException e){
         System.out.println("Error while reading "+MakeQFF.getBasename()+".minfo.");
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
      double[][] hess = trans.hx2hq(minfo.getMolecule().getElectronicData().getHessian());
      
      double[] aa = inputData.getMolecule().getElectronicData().getGradient();
      double[] grad0;
      if(aa != null){
         grad0 = trans.gx2gq(inputData.getMolecule().getElectronicData().getGradient());
      }else{
         grad0 = new double[Nfree];
      }
      double[][] hess0 = trans.hx2hq(inputData.getMolecule().getElectronicData().getHessian());

      double conv1 = 1.0/(Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu));
      double conv2 = conv1*conv1;
      double conv3 = conv2*conv1;
      double conv4 = conv2*conv2;
      
      try{
         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("001.hs")));
         
         pw.println("# Energy / hartree");
         pw.println("   0.000000000000D+00");
         pw.println("# Geometry / Angs amu1/2");
         for(int i=0; i<Nfree; i++){
            pw.print("     0.0000000D+00");
            if(i%3==2) pw.println();
         }
         
         pw.println("#1MR "+qcData.getTitle());
         
         double[] tiii = new double[Nfree];
         double[] uiiii = new double[Nfree];
         
         for(int i=0; i<Nfree; i++){
            double[][][] hess2 = readHessian(i);
            tiii[i] = (hess2[0][i][i] - hess2[1][i][i])/deltaQ[i]*0.5d;
            uiiii[i] = (hess2[0][i][i] + hess2[1][i][i] - 2.0d*hess[i][i])/deltaQ[i]/deltaQ[i];
         }

         pw.println("# Gradient / hartree Angs^-1 amu^-1/2");
         for(int i=0; i<Nfree; i++){
            pw.printf("%4d%20.10e %n", i+1,grad0[i]*conv1);
         }
         pw.println("# Hessian(i,i) / hartree Angs^-2 amu^-1");
         for(int i=0; i<Nfree; i++){
            pw.printf("%4d%20.10e %n", i+1,hess0[i][i]*conv2);
         }
         pw.println("# Cubic(i,i,i) / hartree Angs^-3 amu^-3/2");
         for(int i=0; i<Nfree; i++){
            pw.printf("%4d%20.10e %n", i+1,tiii[i]*conv3);
         }
         pw.println("# Quartic(i,i,i,i) / hartree Angs^-4 amu^-2");
         for(int i=0; i<Nfree; i++){
            pw.printf("%4d%20.10e %n", i+1,uiiii[i]*conv4);
         }

         pw.println("#2MR "+qcData.getTitle());

         double[] tiij = new double[Nfree*(Nfree-1)/2];
         double[] tijj = new double[Nfree*(Nfree-1)/2];
         double[] uiijj = new double[Nfree*(Nfree-1)/2];
         double[] uiiij = new double[Nfree*(Nfree-1)/2];
         double[] uijjj = new double[Nfree*(Nfree-1)/2];
         
         int nn=0;
         for(int i=0; i<Nfree; i++){
            double[][][] hi = readHessian(i);

            for(int j=0; j<i; j++){
               double[][][] hj = readHessian(j);

               tiij[nn] = ((hj[0][i][i] - hj[1][i][i])/deltaQ[j]*0.5d
                               + (hi[0][i][j] - hi[1][i][j])/deltaQ[i])/3.0d;        
               tijj[nn] = ((hi[0][j][j] - hi[1][j][j])/deltaQ[i]*0.5d
                               + (hj[0][i][j] - hj[1][i][j])/deltaQ[j])/3.0d;
               
               uiiij[nn] = (hi[0][i][j] + hi[1][i][j] - 2.0d*hess[i][j])/deltaQ[i]/deltaQ[i];
               uijjj[nn] = (hj[0][i][j] + hj[1][i][j] - 2.0d*hess[i][j])/deltaQ[j]/deltaQ[j];
               uiijj[nn] = ((hj[0][i][i] + hj[1][i][i] - 2.0d*hess[i][i])/deltaQ[j]/deltaQ[j]
                          + (hi[0][j][j] + hi[1][j][j] - 2.0d*hess[j][j])/deltaQ[i]/deltaQ[i])/2.0d;

               nn++;
            }
         }
         
         pw.println("# Hessian(i,j) / hartree Angs^-2 amu^-1");
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               //pw.printf("%4d%4d%20.10e %n", i+1,j+1,hess[i][j]*conv2);
               pw.printf("%4d%4d%20.10e %n", i+1,j+1,hess0[i][j]*conv2);
            }
         }
         pw.println("# Quartic(i,i,j,j) / hartree Angs^-4 amu^-2");
         nn=0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               pw.printf("%4d%4d%20.10e %n", i+1,j+1,uiijj[nn]*conv4);
               nn++;
            }
         }
         pw.println("# Cubic(i,i,j) / hartree Angs^-3 amu^-3/2");
         nn=0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               pw.printf("%4d%4d%20.10e %n", i+1,j+1,tiij[nn]*conv3);
               pw.printf("%4d%4d%20.10e %n", j+1,i+1,tijj[nn]*conv3);
               nn++;
            }
         }
         pw.println("# Quartic(i,i,i,j) / hartree Angs^-4 amu^-2");
         nn=0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               pw.printf("%4d%4d%20.10e %n", i+1,j+1,uiiij[nn]*conv4);
               pw.printf("%4d%4d%20.10e %n", j+1,i+1,uijjj[nn]*conv4);
               nn++;
            }
         }
         

         pw.println("#3MR "+qcData.getTitle());

         double[] tijk = new double[Nfree*(Nfree-1)*(Nfree-2)/6];
         double[] uiijk = new double[Nfree*(Nfree-1)*(Nfree-2)/6];
         double[] uijjk = new double[Nfree*(Nfree-1)*(Nfree-2)/6];
         double[] uijkk = new double[Nfree*(Nfree-1)*(Nfree-2)/6];
         
         nn=0;
         for(int i=0; i<Nfree; i++){
            double[][][] hi = readHessian(i);

            for(int j=0; j<i; j++){
               double[][][] hj = readHessian(j);

               for(int k=0; k<j; k++){
                  double[][][] hk = readHessian(k);

                  tijk[nn] = ((hi[0][j][k] - hi[1][j][k])/deltaQ[i]*0.5d
                            +(hj[0][i][k] - hj[1][i][k])/deltaQ[j]*0.5d
                            +(hk[0][i][j] - hk[1][i][j])/deltaQ[k]*0.5d )/3.0d;
     
                  uiijk[nn] = (hi[0][j][k] + hi[1][j][k] - 2.0d*hess[j][k])/deltaQ[i]/deltaQ[i];
                  uijjk[nn] = (hj[0][i][k] + hj[1][i][k] - 2.0d*hess[i][k])/deltaQ[j]/deltaQ[j];
                  uijkk[nn] = (hk[0][i][j] + hk[1][i][j] - 2.0d*hess[i][j])/deltaQ[k]/deltaQ[k];                     
                  
                  nn++;
               }
            }
         }
         pw.println("# Cubic(i,j,k) / hartree Angs^-3 amu^-3/2");
         nn=0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               for(int k=0; k<j; k++){
                  pw.printf("%4d%4d%4d%20.10e %n", i+1,j+1,k+1,tijk[nn]*conv3);
                  nn++;
               }
            }
         }
         pw.println("# Quartic(i,i,j,k) / hartree Angs^-4 amu^-2");
         nn=0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<i; j++){
               for(int k=0; k<j; k++){
                  pw.printf("%4d%4d%4d%20.10e %n", i+1,j+1,k+1,uiijk[nn]*conv4);
                  pw.printf("%4d%4d%4d%20.10e %n", j+1,i+1,k+1,uijjk[nn]*conv4);
                  pw.printf("%4d%4d%4d%20.10e %n", k+1,i+1,j+1,uijkk[nn]*conv4);
                  nn++;
               }
            }
         }

         pw.close();
         
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      
      System.out.printf(" Done!");
      System.out.println();
      
      
   }

   private void calcQFF_mop_hess(){

      /* --- Now compute coefficients and print -- */
      
      System.out.println();
      System.out.printf("Generating "+qffData.getMopfile()+"...");
      
      MInfoIO minfo = new MInfoIO();
      VibTransformer trans = inputData.getTransform();
      try{
         minfo.loadMOL(MakeQFF.getBasename()+".minfo");         
      }catch(IOException e){
         System.out.println("Error while reading "+MakeQFF.getBasename()+".minfo.");
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
      double[][] hess = trans.hx2hq(minfo.getMolecule().getElectronicData().getHessian());

      try{
         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(qffData.getMopfile())));
         
         double[] sqfreq = this.printMopHeader(pw);

         double[]   grad0 = null;
         double[][] hess0 = null;
         if(qffData.getGradient_and_hessian().equals("INPUT")){
            
            ElectronicData edata = inputData.getMolecule().getElectronicData();
            grad0 = trans.gx2gq(edata.getGradient());
            hess0 = trans.hx2hq(edata.getHessian());
            
         }else{
            grad0 = trans.gx2gq(minfo.getMolecule().getElectronicData().getGradient());
            hess0 = hess;
         }

         for(int n=0; n<activeModes.length; n++){
            if(activeModes[n] == null) continue;
            
            int Nfree = activeModes[n].length;
            for(int i=0; i<Nfree; i++){
               int ii=activeModes[n][i];
               
               double si = sqfreq[ii];
               double sii = si*si;
               
               double[][][] hess2 = readHessian(ii);
               double tiii = (hess2[0][ii][ii] - hess2[1][ii][ii])/deltaQ[ii]*0.5d/6.0d/sii/si;
               double uiiii = (hess2[0][ii][ii] + hess2[1][ii][ii] - 2.0d*hess[ii][ii])/deltaQ[ii]/deltaQ[ii]/24.0d/sii/sii;
               
               int i1=ii+1;
               pw.printf("%29.22e%5d%n",grad0[ii]/si,i1);
               pw.printf("%29.22e%5d%<5d%n",hess0[ii][ii]/sii*0.5d,i1);
               pw.printf("%29.22e%5d%<5d%<5d%n",tiii,i1);
               pw.printf("%29.22e%5d%<5d%<5d%<5d%n",uiiii,i1);
            }
         }

         for(int n=0; n<activeModes.length; n++){
            if(activeModes[n] == null) continue;
            
            int Nfree = activeModes[n].length;
            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               
               double[][][] hi = readHessian(ii);
               double si = sqfreq[ii];
               double sii = si*si;

               for(int j=0; j<i; j++){
                  int jj = activeModes[n][j];
                  
                  double[][][] hj = readHessian(jj);
                  double sj = sqfreq[jj];
                  double sjj = sj*sj;
                  double sij = si*sj;

                  double tiij = ((hj[0][ii][ii] - hj[1][ii][ii])/deltaQ[jj]*0.5d
                               + (hi[0][ii][jj] - hi[1][ii][jj])/deltaQ[ii])/6.0d/sij/si;        
                  double tijj = ((hi[0][jj][jj] - hi[1][jj][jj])/deltaQ[ii]*0.5d
                               + (hj[0][ii][jj] - hj[1][ii][jj])/deltaQ[jj])/6.0d/sij/sj ;
                  
                  double uiiij=0.0d, uiijj=0.0d, uijjj=0.0d;
                  if(MR != 4) {
                     uiiij = (hi[0][ii][jj] + hi[1][ii][jj] - 2.0d*hess[ii][jj])/deltaQ[ii]/deltaQ[ii];
                     uijjj = (hj[0][ii][jj] + hj[1][ii][jj] - 2.0d*hess[ii][jj])/deltaQ[jj]/deltaQ[jj];
                     uiijj = ((hj[0][ii][ii] + hj[1][ii][ii] - 2.0d*hess[ii][ii])/deltaQ[jj]/deltaQ[jj]
                            + (hi[0][jj][jj] + hi[1][jj][jj] - 2.0d*hess[jj][jj])/deltaQ[ii]/deltaQ[ii])/2.0d;
                  }else{
                     double[][][] hij = readHessian(ii,jj);
                     uiiij = (hij[0][ii][ii] - hij[1][ii][ii] - hij[2][ii][ii] + hij[3][ii][ii])/deltaQ[ii]/deltaQ[jj]*0.25d;
                     uijjj = (hij[0][jj][jj] - hij[1][jj][jj] - hij[2][jj][jj] + hij[3][jj][jj])/deltaQ[ii]/deltaQ[jj]*0.25d;
                     uiijj = (hij[0][ii][jj] - hij[1][ii][jj] - hij[2][ii][jj] + hij[3][ii][jj])/deltaQ[ii]/deltaQ[jj]*0.25d;
                     
                  }
                  uiiij = uiiij/6.0d/sii/sij;
                  uiijj = uiijj/4.0d/sii/sjj;
                  uijjj = uijjj/6.0d/sij/sjj;

                  int i1=ii+1;
                  int j1=jj+1;
                  pw.printf("%29.22e%5d%5d%n",hess0[ii][jj]/si/sj,j1,i1);
                  pw.printf("%29.22e%5d%5d%<5d%n",tiij,j1,i1);
                  pw.printf("%29.22e%5d%5d%<5d%<5d%n",uiiij,j1,i1);
                  pw.printf("%29.22e%5d%<5d%5d%n",tijj,j1,i1);
                  pw.printf("%29.22e%5d%<5d%5d%<5d%n",uiijj,j1,i1);
                  pw.printf("%29.22e%5d%<5d%<5d%5d%n",uijjj,j1,i1);

               }
            }
         }
         
         // inter-domain harmonic coupling
         for(int n1=0; n1<activeModes.length; n1++){
            if(activeModes[n1] == null) continue;
            
            int nf1 = activeModes[n1].length;
            
            for(int n2=0; n2<n1; n2++){
               if(activeModes[n2] == null) continue;
               
               int nf2 = activeModes[n2].length;
               
               for(int i=0; i<nf1; i++){
                  int ii = activeModes[n1][i];
                  
                  double si = sqfreq[ii];

                  for(int j=0; j<nf2; j++){
                     int jj = activeModes[n2][j];
                     double sj = sqfreq[jj];

                     int i1=ii+1;
                     int j1=jj+1;
                     pw.printf("%29.22e%5d%5d%n",hess0[ii][jj]/si/sj,j1,i1);
                  }
               }
            }
         }
         
         double[][][] hij=null, hik=null, hjk=null;
         for(int n=0; n<activeModes.length; n++){
            if(activeModes[n] == null) continue;
            
            int Nfree = activeModes[n].length;
            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               double[][][] hi = readHessian(ii);
               double si = sqfreq[ii];
               double sii = si*si;

               for(int j=0; j<i; j++){
                  int jj = activeModes[n][j];
                  double[][][] hj = readHessian(jj);
                  double sj = sqfreq[jj];
                  double sjj = sj*sj;
                  double sij = si*sj;
                  if(MR==4) {
                     hij = readHessian(ii,jj);
                  }

                  for(int k=0; k<j; k++){
                     int kk = activeModes[n][k];
                     double[][][] hk = readHessian(kk);
                     double sk = sqfreq[kk];
                     double skk = sk*sk;
                     double sik = si*sk;
                     double sjk = sj*sk;

                     double tijk = ((hi[0][jj][kk] - hi[1][jj][kk])/deltaQ[ii]*0.5d
                                   +(hj[0][ii][kk] - hj[1][ii][kk])/deltaQ[jj]*0.5d
                                   +(hk[0][ii][jj] - hk[1][ii][jj])/deltaQ[kk]*0.5d )/3.0d/sij/sk;
        
                     double uiijk=0.0d, uijjk=0.0d, uijkk=0.0d;
                     if(MR != 4){
                        uiijk = (hi[0][jj][kk] + hi[1][jj][kk] - 2.0d*hess[jj][kk])/deltaQ[ii]/deltaQ[ii];
                        uijjk = (hj[0][ii][kk] + hj[1][ii][kk] - 2.0d*hess[ii][kk])/deltaQ[jj]/deltaQ[jj];
                        uijkk = (hk[0][ii][jj] + hk[1][ii][jj] - 2.0d*hess[ii][jj])/deltaQ[kk]/deltaQ[kk];                     
                     }else{
                        hik = readHessian(ii,kk);
                        hjk = readHessian(jj,kk);
                        uiijk = ((hij[0][ii][kk] - hij[1][ii][kk] - hij[2][ii][kk] + hij[3][ii][kk])/deltaQ[ii]/deltaQ[jj]
                               + (hik[0][ii][jj] - hik[1][ii][jj] - hik[2][ii][jj] + hik[3][ii][jj])/deltaQ[ii]/deltaQ[kk])*0.125d;
                        uijjk = ((hij[0][jj][kk] - hij[1][jj][kk] - hij[2][jj][kk] + hij[3][jj][kk])/deltaQ[ii]/deltaQ[jj]
                               + (hjk[0][ii][jj] - hjk[1][ii][jj] - hjk[2][ii][jj] + hjk[3][ii][jj])/deltaQ[jj]/deltaQ[kk])*0.125d;
                        uijkk = ((hik[0][jj][kk] - hik[1][jj][kk] - hik[2][jj][kk] + hik[3][jj][kk])/deltaQ[ii]/deltaQ[kk]
                               + (hjk[0][ii][kk] - hjk[1][ii][kk] - hjk[2][ii][kk] + hjk[3][ii][kk])/deltaQ[jj]/deltaQ[kk])*0.125d;
                        
                     }
                     uiijk = uiijk *0.5d/sii/sjk;
                     uijjk = uijjk *0.5d/sjj/sik;
                     uijkk = uijkk *0.5d/sij/skk;

                     int i1=ii+1;
                     int j1=jj+1;
                     int k1=kk+1;
                     pw.printf("%29.22e%5d%5d%5d%n",tijk,k1,j1,i1);
                     pw.printf("%29.22e%5d%5d%5d%<5d%n",uiijk,k1,j1,i1);
                     pw.printf("%29.22e%5d%5d%<5d%5d%n",uijjk,k1,j1,i1);
                     pw.printf("%29.22e%5d%<5d%5d%5d%n",uijkk,k1,j1,i1);
                  }
               }
            }
         }
         
         if(MR==4){
            for(int n=0; n<activeModes.length; n++){
               if(activeModes[n] == null) continue;
               
               int Nfree = activeModes[n].length;
               for(int i=0; i<Nfree; i++){
                  int ii = activeModes[n][i];
                  double si = sqfreq[ii];

                  for(int j=0; j<i; j++){
                     int jj = activeModes[n][j];
                     hij = readHessian(ii,jj);
                     double sj = sqfreq[jj];

                     for(int k=0; k<j; k++){
                        int kk = activeModes[n][k];
                        hik = readHessian(ii,kk);
                        hjk = readHessian(jj,kk);
                        double sk = sqfreq[kk];
                        
                        for(int l=0; l<k; l++){
                           int ll = activeModes[n][l];
                           double[][][] hil = readHessian(ii,ll);
                           double[][][] hjl = readHessian(jj,ll);
                           double[][][] hkl = readHessian(kk,ll);
                           double sl = sqfreq[ll];
                           
                           double uijkl = 0.0d;
                           uijkl = ((hij[0][kk][ll] - hij[1][kk][ll] - hij[2][kk][ll] + hij[3][kk][ll])/deltaQ[ii]/deltaQ[jj]
                           + (hik[0][jj][ll] - hik[1][jj][ll] - hik[2][jj][ll] + hik[3][jj][ll])/deltaQ[ii]/deltaQ[kk]
                           + (hil[0][jj][kk] - hil[1][jj][kk] - hil[2][jj][kk] + hil[3][jj][kk])/deltaQ[ii]/deltaQ[ll]
                           + (hjk[0][ii][ll] - hjk[1][ii][ll] - hjk[2][ii][ll] + hjk[3][ii][ll])/deltaQ[jj]/deltaQ[kk]
                           + (hjl[0][ii][kk] - hjl[1][ii][kk] - hjl[2][ii][kk] + hjl[3][ii][kk])/deltaQ[jj]/deltaQ[ll]
                           + (hkl[0][ii][jj] - hkl[1][ii][jj] - hkl[2][ii][jj] + hkl[3][ii][jj])/deltaQ[kk]/deltaQ[ll])*0.25d/6.0d;
                           uijkl = uijkl/si/sj/sk/sl;
                           
                           int i1=ii+1;
                           int j1=jj+1;
                           int k1=kk+1;
                           int l1=ll+1;
                           pw.printf("%29.22e%5d%5d%5d%5d%n",uijkl,l1,k1,j1,i1);
                           
                        }
                     }
                  }
               }
            }
         }

         pw.close();
         
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      
      System.out.println(" Done!");
      
   }
   
   private double[][][] readHessian(int mi){
      int Nfree = inputData.getMolecule().getVibrationalData().Nfree;
      
      double[][][] hess = new double[2][Nfree][Nfree];
      try{
         DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(getBasename(mi)+".tempfile")));
         for(int n=0; n<2; n++){
            for(int i=0; i<Nfree; i++){
               for(int j=0; j<=i; j++){
                  hess[n][i][j] = dos.readDouble();
                  hess[n][j][i] = hess[n][i][j];
               }
            }
         }
         dos.close();
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }

      return hess;
   }
   private double[][][] readHessian(int mi, int mj){
      int Nfree = inputData.getMolecule().getVibrationalData().Nfree;
      
      double[][][] hess = new double[4][Nfree][Nfree];
      try{
         DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(getBasename(mi,mj)+".tempfile")));
         for(int n=0; n<4; n++){
            for(int i=0; i<Nfree; i++){
               for(int j=0; j<=i; j++){
                  hess[n][i][j] = dos.readDouble();                  
                  hess[n][j][i] = hess[n][i][j];
               }
            }
         }
         dos.close();         
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      return hess;
      
   }
   private void calcQFF_mop_grad(){

      /* --- Now compute coefficients and print -- */
      
      System.out.println();
      System.out.printf("Generating "+qffData.getMopfile()+"...");
      
      MInfoIO minfo = new MInfoIO();
      VibTransformer trans = inputData.getTransform();

      try{
         minfo.loadMOL(MakeQFF.getBasename()+".minfo");         
      }catch(IOException e){
         System.out.println("Error while reading "+MakeQFF.getBasename()+".minfo.");
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
      double[] grad = trans.gx2gq(minfo.getMolecule().getElectronicData().getGradient());

      try{
         PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(qffData.getMopfile())));

         double[] sqfreq = this.printMopHeader(pw);

         double[] grad0 = null;
         double[][] hess0 = null;
         if(qffData.getGradient_and_hessian().equals("INPUT")){
            ElectronicData edata = inputData.getMolecule().getElectronicData();
            grad0 = trans.gx2gq(edata.getGradient());
            hess0 = trans.hx2hq(edata.getHessian());
         }

         for(int n=0; n<activeModes.length; n++){
            if(activeModes[n] == null) continue;
            
            int Nfree = activeModes[n].length;
            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               double si  = sqfreq[ii];
               double sii = si*si;
               
               double[][] gi = readGradient(ii);
               
               double tiii = (gi[2][ii] + gi[1][ii] - 2.0d*grad[ii])/deltaQ[ii]/deltaQ[ii]/6.0d/sii/si;
               double uiiii = (gi[3][ii] - 3.0d*gi[2][ii] + 3.0d*gi[1][ii] - gi[0][ii])
                                  /deltaQ[ii]/deltaQ[ii]/deltaQ[ii]/8.0d/24.0d/sii/sii;

               int i1=ii+1;
               if(qffData.getGradient_and_hessian().equals("INPUT")){
                  pw.printf("%29.22e%5d%n",grad0[ii]/si,i1);
                  pw.printf("%29.22e%5d%<5d%n",hess0[ii][ii]/sii*0.5d,i1);               
               }else{
                  double hess = (gi[2][ii] - gi[1][ii])/deltaQ[ii]*0.5d;
                  pw.printf("%29.22e%5d%n",grad[ii]/si,i1);
                  pw.printf("%29.22e%5d%<5d%n",hess/sii*0.5d,i1);                              
               }
               pw.printf("%29.22e%5d%<5d%<5d%n",tiii,i1);
               pw.printf("%29.22e%5d%<5d%<5d%<5d%n",uiiii,i1);

            }
         }
         for(int n=0; n<activeModes.length; n++){
            if(activeModes[n] == null) continue;
            
            int Nfree = activeModes[n].length;
            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               double[][] gi = readGradient(ii);
               double si = sqfreq[ii];
               double sii = si*si;

               for(int j=0; j<i; j++){
                  int jj = activeModes[n][j];
                  double[][] gj  = readGradient(jj);
                  double[][] gij = readGradient(ii,jj); 
                  double sj = sqfreq[jj];
                  double sjj = sj*sj;
                  double sij = si*sj;

                  double tiij = ((gi[2][jj] + gi[1][jj] - 2.0d*grad[jj])/deltaQ[ii]/deltaQ[ii]
                              +  (gij[0][ii] - gij[1][ii] - gij[2][ii] + gij[3][ii])/deltaQ[ii]/deltaQ[jj]*0.25d*2.0d)
                              /3.0d;        
                  double tijj = ((gj[2][ii] + gj[1][ii] - 2.0d*grad[ii])/deltaQ[jj]/deltaQ[jj]
                              +  (gij[0][jj] - gij[1][jj] - gij[2][jj] + gij[3][jj])/deltaQ[ii]/deltaQ[jj]*0.25d*2.0d)
                              /3.0d;

                  double uiiij = ((gi[3][jj] - 3.0d*gi[2][jj] + 3.0d*gi[1][jj] - gi[0][jj])/deltaQ[ii]/deltaQ[ii]/deltaQ[ii]/8.0d
                               +  (gij[0][ii] + gij[1][ii] - gij[2][ii] - gij[3][ii] - 2.0d*gj[2][ii] + 2.0d*gj[1][ii])/deltaQ[ii]/deltaQ[ii]/deltaQ[jj]/2.0d*3.0d)
                               /4.0d;
                  double uiijj = ((gij[0][ii] - gij[1][ii] + gij[2][ii] - gij[3][ii] - 2.0d*gi[2][ii] + 2.0d*gi[1][ii])/deltaQ[ii]/deltaQ[jj]/deltaQ[jj]/2.0d
                               +  (gij[0][jj] + gij[1][jj] - gij[2][jj] - gij[3][jj] - 2.0d*gj[2][jj] + 2.0d*gj[1][jj])/deltaQ[ii]/deltaQ[ii]/deltaQ[jj]/2.0d)
                               /2.0d;
                  double uijjj = ((gj[3][ii] - 3.0d*gj[2][ii] + 3.0d*gj[1][ii] - gj[0][ii])/deltaQ[jj]/deltaQ[jj]/deltaQ[jj]/8.0d
                               +  (gij[0][jj] - gij[1][jj] + gij[2][jj] - gij[3][jj] - 2.0d*gi[2][jj] + 2.0d*gi[1][jj])/deltaQ[ii]/deltaQ[jj]/deltaQ[jj]/2.0d*3.0d)
                               /4.0d;
                  
                  tiij = tiij/2.0d/sij/si;
                  tijj = tijj/2.0d/sij/sj;
                  uiiij = uiiij/6.0d/sii/sij;
                  uiijj = uiijj/4.0d/sii/sjj;
                  uijjj = uijjj/6.0d/sij/sjj;

                  int i1=ii+1;
                  int j1=jj+1;
                  if(qffData.getGradient_and_hessian().equals("INPUT")){
                     pw.printf("%29.22e%5d%5d%n",hess0[ii][jj]/si/sj,j1,i1);
                  }else{
                     double hess = ((gj[2][ii] - gj[1][ii])/deltaQ[jj]
                                  + (gi[2][jj] - gi[1][jj])/deltaQ[ii])*0.25d;
                     pw.printf("%29.22e%5d%5d%n",hess/si/sj,j1,i1);
                  }
                  pw.printf("%29.22e%5d%5d%<5d%n",tiij,j1,i1);
                  pw.printf("%29.22e%5d%5d%<5d%<5d%n",uiiij,j1,i1);
                  pw.printf("%29.22e%5d%<5d%5d%n",tijj,j1,i1);
                  pw.printf("%29.22e%5d%<5d%5d%<5d%n",uiijj,j1,i1);
                  pw.printf("%29.22e%5d%<5d%<5d%5d%n",uijjj,j1,i1);

               }
            }
         }
         
         // inter-domain harmonic coupling
         for(int n1=0; n1<activeModes.length; n1++){
            if(activeModes[n1] == null) continue;
            
            int nf1 = activeModes[n1].length;
            
            for(int n2=0; n2<n1; n2++){
               if(activeModes[n2] == null) continue;
               
               int nf2 = activeModes[n2].length;
               
               for(int i=0; i<nf1; i++){
                  int ii = activeModes[n1][i];
                  double[][] gi = readGradient(ii);
                  double si = sqfreq[ii];

                  for(int j=0; j<nf2; j++){
                     int jj = activeModes[n2][j];
                     double[][] gj  = readGradient(jj);
                     double sj = sqfreq[jj];

                     int i1=ii+1;
                     int j1=jj+1;
                     if(qffData.getGradient_and_hessian().equals("INPUT")){
                        pw.printf("%29.22e%5d%5d%n",hess0[ii][jj]/si/sj,j1,i1);
                     }else{
                        double hess = ((gj[2][ii] - gj[1][ii])/deltaQ[jj]
                                     + (gi[2][jj] - gi[1][jj])/deltaQ[ii])*0.25d;
                        pw.printf("%29.22e%5d%5d%n",hess/si/sj,j1,i1);
                     }

                  }
               }
            }
            
         }
         
         double[][] gij=null, gik=null, gjk=null;
         for(int n=0; n<activeModes.length; n++){
            if(activeModes[n] == null) continue;
            
            int Nfree = activeModes[n].length;
            for(int i=0; i<Nfree; i++){
               int ii = activeModes[n][i];
               double[][] gi = readGradient(ii);
               double si = sqfreq[ii];
               double sii = si*si;

               for(int j=0; j<i; j++){
                  int jj = activeModes[n][j];
                  double[][] gj = readGradient(jj);
                  gij = readGradient(ii,jj);
                  double sj = sqfreq[jj];
                  double sjj = sj*sj;
                  double sij = si*sj;

                  for(int k=0; k<j; k++){
                     int kk = activeModes[n][k];
                     double[][] gk = readGradient(kk);
                     gjk = readGradient(jj,kk);
                     gik = readGradient(ii,kk);
                     double sk = sqfreq[kk];
                     double skk = sk*sk;
                     double sik = si*sk;
                     double sjk = sj*sk;

                     double tijk = ((gjk[0][ii] - gjk[1][ii] - gjk[2][ii] + gjk[3][ii])/deltaQ[jj]/deltaQ[kk]*0.25
                                  + (gik[0][jj] - gik[1][jj] - gik[2][jj] + gik[3][jj])/deltaQ[ii]/deltaQ[kk]*0.25
                                  + (gij[0][kk] - gij[1][kk] - gij[2][kk] + gij[3][kk])/deltaQ[ii]/deltaQ[jj]*0.25)
                                  /3.0d/sij/sk;
        
                     double uiijk=0.0d, uijjk=0.0d, uijkk=0.0d;
                     if(MR != 4){
                        uiijk = ((gij[0][kk] + gij[1][kk] - gij[2][kk] - gij[3][kk] - 2.0d*gj[2][kk] + 2.0d*gj[1][kk])/deltaQ[ii]/deltaQ[ii]/deltaQ[jj]*0.5d
                               + (gik[0][jj] + gik[1][jj] - gik[2][jj] - gik[3][jj] - 2.0d*gk[2][jj] + 2.0d*gk[1][jj])/deltaQ[ii]/deltaQ[ii]/deltaQ[kk]*0.5d)
                               /2.0d;
                        uijjk = ((gij[0][kk] - gij[1][kk] + gij[2][kk] - gij[3][kk] - 2.0d*gi[2][kk] + 2.0d*gi[1][kk])/deltaQ[ii]/deltaQ[jj]/deltaQ[jj]*0.5d
                               + (gjk[0][ii] + gjk[1][ii] - gjk[2][ii] - gjk[3][ii] - 2.0d*gk[2][ii] + 2.0d*gk[1][ii])/deltaQ[jj]/deltaQ[jj]/deltaQ[kk]*0.5d)
                               /2.0d;
                        uijkk = ((gjk[0][ii] - gjk[1][ii] + gjk[2][ii] - gjk[3][ii] - 2.0d*gj[2][ii] + 2.0d*gj[1][ii])/deltaQ[jj]/deltaQ[kk]/deltaQ[kk]*0.5d
                               + (gik[0][jj] - gik[1][jj] + gik[2][jj] - gik[3][jj] - 2.0d*gi[2][jj] + 2.0d*gi[1][jj])/deltaQ[ii]/deltaQ[kk]/deltaQ[kk]*0.5d)
                               /2.0d;
                     }else{
                        double[][] gijk = readGradient(ii,jj,kk);
                        uiijk = ((gijk[0][ii] - gijk[1][ii] - gijk[2][ii] + gijk[3][ii] 
                                - gijk[4][ii] + gijk[5][ii] + gijk[6][ii] - gijk[7][ii])/deltaQ[ii]/deltaQ[jj]/deltaQ[kk]/8.0d*2.0d
                              + (gij[0][kk] + gij[1][kk] - gij[2][kk] - gij[3][kk] - 2.0d*gj[2][kk] + 2.0d*gj[1][kk])/deltaQ[ii]/deltaQ[ii]/deltaQ[jj]*0.5d
                              + (gik[0][jj] + gik[1][jj] - gik[2][jj] - gik[3][jj] - 2.0d*gk[2][jj] + 2.0d*gk[1][jj])/deltaQ[ii]/deltaQ[ii]/deltaQ[kk]*0.5d)
                              /4.0d;
                       uijjk = ((gijk[0][jj] - gijk[1][jj] - gijk[2][jj] + gijk[3][jj] 
                               - gijk[4][jj] + gijk[5][jj] + gijk[6][jj] - gijk[7][jj])/deltaQ[ii]/deltaQ[jj]/deltaQ[kk]/8.0d*2.0d
                             + (gij[0][kk] - gij[1][kk] + gij[2][kk] - gij[3][kk] - 2.0d*gi[2][kk] + 2.0d*gi[1][kk])/deltaQ[ii]/deltaQ[jj]/deltaQ[jj]*0.5d
                             + (gjk[0][ii] + gjk[1][ii] - gjk[2][ii] - gjk[3][ii] - 2.0d*gk[2][ii] + 2.0d*gk[1][ii])/deltaQ[jj]/deltaQ[jj]/deltaQ[kk]*0.5d)
                             /4.0d;
                       uijkk = ((gijk[0][kk] - gijk[1][kk] - gijk[2][kk] + gijk[3][kk] 
                               - gijk[4][kk] + gijk[5][kk] + gijk[6][kk] - gijk[7][kk])/deltaQ[ii]/deltaQ[jj]/deltaQ[kk]/8.0d*2.0d
                             + (gjk[0][ii] - gjk[1][ii] + gjk[2][ii] - gjk[3][ii] - 2.0d*gj[2][ii] + 2.0d*gj[1][ii])/deltaQ[jj]/deltaQ[kk]/deltaQ[kk]*0.5d
                             + (gik[0][jj] - gik[1][jj] + gik[2][jj] - gik[3][jj] - 2.0d*gi[2][jj] + 2.0d*gi[1][jj])/deltaQ[ii]/deltaQ[kk]/deltaQ[kk]*0.5d)
                             /4.0d;                     
                     }
                     uiijk = uiijk *0.5d/sii/sjk;
                     uijjk = uijjk *0.5d/sjj/sik;
                     uijkk = uijkk *0.5d/sij/skk;

                     int i1=ii+1;
                     int j1=jj+1;
                     int k1=kk+1;
                     pw.printf("%29.22e%5d%5d%5d%n",tijk,k1,j1,i1);
                     pw.printf("%29.22e%5d%5d%5d%<5d%n",uiijk,k1,j1,i1);
                     pw.printf("%29.22e%5d%5d%<5d%5d%n",uijjk,k1,j1,i1);
                     pw.printf("%29.22e%5d%<5d%5d%5d%n",uijkk,k1,j1,i1);
                     
                  }
               }
            }
         }

         if(MR==4){
            for(int n=0; n<activeModes.length; n++){
               if(activeModes[n] == null) continue;
               
               int Nfree = activeModes[n].length;
               for(int i=0; i<Nfree; i++){
                  int ii = activeModes[n][i];
                  double si = sqfreq[ii];

                  for(int j=0; j<i; j++){
                     int jj = activeModes[n][j];
                     double sj = sqfreq[jj];

                     for(int k=0; k<j; k++){
                        int kk = activeModes[n][k];
                        double[][] gijk = readGradient(ii,jj,kk);
                        double sk = sqfreq[kk];
                        
                        for(int l=0; l<k; l++){
                           int ll = activeModes[n][l];
                           double[][] gijl = readGradient(ii,jj,ll);
                           double[][] gikl = readGradient(ii,kk,ll);
                           double[][] gjkl = readGradient(jj,kk,ll);
                           double sl = sqfreq[ll];
                           
                           double uijkl = 0.0d;
                           uijkl = ((gijk[0][ll] - gijk[1][ll] - gijk[2][ll] + gijk[3][ll] 
                                   - gijk[4][ll] + gijk[5][ll] + gijk[6][ll] - gijk[7][ll])/deltaQ[ii]/deltaQ[jj]/deltaQ[kk]/8.0d
                                 +  (gijl[0][kk] - gijl[1][kk] - gijl[2][kk] + gijl[3][kk] 
                                   - gijl[4][kk] + gijl[5][kk] + gijl[6][kk] - gijl[7][kk])/deltaQ[ii]/deltaQ[jj]/deltaQ[ll]/8.0d
                                 +  (gikl[0][jj] - gikl[1][jj] - gikl[2][jj] + gikl[3][jj] 
                                   - gikl[4][jj] + gikl[5][jj] + gikl[6][jj] - gikl[7][jj])/deltaQ[ii]/deltaQ[kk]/deltaQ[ll]/8.0d
                                 +  (gjkl[0][ii] - gjkl[1][ii] - gjkl[2][ii] + gjkl[3][ii] 
                                   - gjkl[4][ii] + gjkl[5][ii] + gjkl[6][ii] - gjkl[7][ii])/deltaQ[jj]/deltaQ[kk]/deltaQ[ll]/8.0d)
                                 /4.0d;
                           uijkl = uijkl/si/sj/sk/sl;
                           
                           int i1=ii+1;
                           int j1=jj+1;
                           int k1=kk+1;
                           int l1=ll+1;
                           pw.printf("%29.22e%5d%5d%5d%5d%n",uijkl,l1,k1,j1,i1);
                           
                        }
                     }
                  }
               }
            }
            
         }

         pw.close();
         
      }catch (IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      
      System.out.println(" Done!");
      
   }
   private double[][] readGradient(int mi){
      int Nfree = inputData.getMolecule().getVibrationalData().Nfree;

      double[][] grad = new double[4][Nfree];
      try{
         DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(getBasename(mi)+".tempfile")));
         for(int n=0; n<4; n++){
            for(int i=0; i<Nfree; i++){
               grad[n][i] = dos.readDouble();
            }
         }
         dos.close();
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }

      return grad;
   }
   private double[][] readGradient(int mi, int mj){
      int Nfree = inputData.getMolecule().getVibrationalData().Nfree;
      
      double[][] grad = new double[4][Nfree];
      try{
         DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(getBasename(mi,mj)+".tempfile")));
         for(int n=0; n<4; n++){
            for(int i=0; i<Nfree; i++){
               grad[n][i] = dos.readDouble();
            }
         }
         dos.close();         
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      return grad;
      
   }
   private double[][] readGradient(int mi, int mj, int mk){
      int Nfree = inputData.getMolecule().getVibrationalData().Nfree;

      double[][] grad = new double[8][Nfree];
      try{
         DataInputStream dos = new DataInputStream(new BufferedInputStream(new FileInputStream(getBasename(mi,mj,mk)+".tempfile")));
         for(int n=0; n<8; n++){
            for(int i=0; i<Nfree; i++){
               grad[n][i] = dos.readDouble();
            }
         }
         dos.close();         
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      return grad;
      
   }
   
   private double[] printMopHeader(PrintWriter pw){
      
      double[] sqfreq = Utilities.deepCopy(inputData.getMolecule().getVibrationalData().getOmegaV());
      int Nfree = sqfreq.length;
      pw.println("SCALING FREQUENCIES N_FRQS="+Nfree);

      {
         int i=0;
         while(sqfreq[i]<0.0d){
            sqfreq[i] = -sqfreq[i]/Constants.Hartree2wvn;
            pw.printf("%28.22e",sqfreq[i]);
            pw.println();
            sqfreq[i] = Math.sqrt(sqfreq[i]);
            i++;
         }
         while(i<sqfreq.length){
            sqfreq[i] = sqfreq[i]/Constants.Hartree2wvn;
            pw.printf("%28.22e",sqfreq[i]);
            pw.println();
            sqfreq[i] = Math.sqrt(sqfreq[i]);
            i++;            
         }
      }
            
      pw.println("DALTON_FOR_MIDAS "+qcData.getTitle());

      return sqfreq;
      
   }
}
