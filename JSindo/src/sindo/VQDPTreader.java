package sindo;

import java.io.*;

import sys.Utilities;

/**
 * Read the output data of VQDPT calculation
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since 3.4
 *
 */
public class VQDPTreader {

   private boolean readIR = false;
   private String IRdataFile = null;
   
   private double[] readBlockDouble(BufferedReader br, int size){
      double[] dd = new double[size];
      int i=0;
      try{
         double[] ld = null;
         while(i<size){
            ld = Utilities.splitWithSpaceDouble(br.readLine());
            for(int j=0; j<ld.length; j++){
               dd[i] = ld[j];
               i++;
            }
         }
            
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      return dd;
   }

   private int[] readBlockInteger(BufferedReader br, int size){
      int[] ii = new int[size];
      int i=0;
      try{
         int[] li = null;
         while(i<size){
            li = Utilities.splitWithSpaceInt(br.readLine());
            for(int j=0; j<li.length; j++){
               ii[i] = li[j];
               i++;
            }
         }
            
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      return ii;
   }

   public void setInfrared(){
      this.readIR = true;
      this.IRdataFile = "vqdpt-IR.data";
   }
   
   public void setInfrared(String irdataFile){
      this.readIR = true;
      this.IRdataFile = irdataFile;
   }
   
   public VQDPTdata read(){
      return this.read("vqdpt-w.wfn");
   }
   public VQDPTdata read(String outputFile){
      
      VQDPTdata vdata = new VQDPTdata();
      vdata.setInfared(readIR);
      
      try{
         BufferedReader br = new BufferedReader(new FileReader(outputFile));
         BufferedReader brIRdata = null;
         if(readIR){
            brIRdata = new BufferedReader(new FileReader(IRdataFile));
            brIRdata.readLine();
         }
         br.readLine();
         br.readLine();
         int ntarget = Integer.parseInt(br.readLine().trim());
         vdata.setNtarget(ntarget);
         
         Conf[] targetConf = new Conf[ntarget]; 
         br.readLine();
         for(int i=0; i<ntarget; i++){
            targetConf[i] = new Conf(br.readLine());
         }
         vdata.setTargetConf(targetConf);
         
         br.readLine();
         int[] options = Utilities.splitWithSpaceInt(br.readLine());
         vdata.setOptions(options[0], options[1], options[2], options[3]);
         
         br.readLine();
         double[] pSpace = Utilities.splitWithSpaceDouble(br.readLine());
         vdata.setPspaceOptions(pSpace[0], pSpace[1], pSpace[2], pSpace[3], pSpace[4]);
         
         br.readLine();
         int Ngroup = Integer.parseInt(br.readLine().trim());
         vdata.setNgroup(Ngroup);
         
         br.readLine();
         int[] npCnf = this.readBlockInteger(br, Ngroup);
         vdata.setNpCnf(npCnf);

         br.readLine();
         double zpe = Double.parseDouble(br.readLine());
         vdata.setZPE(zpe);
         
         for(int i=0; i<Ngroup; i++){
            VQDPTgroup vgroup = new VQDPTgroup();
            int np = npCnf[i];
            vgroup.setNp(np);

            br.readLine();
            if(np ==1){
               br.readLine();
               
               int[] index = new int[1];
               index[0] = 0;
               vgroup.setNtarget(1);
               vgroup.setIdxTarget(index);
               
               Conf[] pCnf = new Conf[1];
               pCnf[0] = new Conf(br.readLine());
               vgroup.setpConf(pCnf);
               
               br.readLine();
               double[] energy = new double[1];
               energy[0] = Double.parseDouble(br.readLine());
               vgroup.setEnergy(energy);
               
               double[][] CIcoeff = new double[1][1];
               CIcoeff[0][0] = 1.0d;
               vgroup.setCIcoeff(CIcoeff);

               if(readIR){
                  double[] IRintty = new double[1];
                  double[] data = Utilities.splitWithSpaceDouble(brIRdata.readLine().substring(0,36));
                  IRintty[0] = data[1];
                  vgroup.setIRintensity(IRintty);
               }
               
            }else{
               br.readLine();
               br.readLine();
               
               br.readLine();
               int ntarget_i = Integer.parseInt(br.readLine().trim());
               int[] index = this.readBlockInteger(br, ntarget_i);
               for(int j=0; j<ntarget_i; j++){
                  index[j] = index[j]-1;
               }
               vgroup.setNtarget(ntarget_i);
               vgroup.setIdxTarget(index);
               
               Conf[] pConf = new Conf[np];
               br.readLine();
               for(int j=0; j<np; j++){
                  pConf[j] = new Conf(br.readLine());
               }
               vgroup.setpConf(pConf);
               
               double[] energy = new double[np];
               double[][] CIcoeff = new double[np][];
               for(int j=0; j<np; j++){
                  br.readLine();
                  
                  br.readLine();
                  energy[j] = Double.parseDouble(br.readLine());
                  
                  br.readLine();
                  CIcoeff[j] = this.readBlockDouble(br, np);
               }
               vgroup.setEnergy(energy);
               vgroup.setCIcoeff(CIcoeff);
               
               if(readIR){
                  double[] IRintty = new double[np];
                  for(int j=0; j<np; j++){
                     double[] data = Utilities.splitWithSpaceDouble(brIRdata.readLine().substring(0,36));                     
                     IRintty[j] = data[1];
                  }
                  vgroup.setIRintensity(IRintty);
               }
            }
            vdata.addGroup(vgroup);
            
         }
         
         br.close();
         if(readIR) {
            brIRdata.close();
         }
         
      }catch(IOException e){
         System.out.println("Error while reading "+outputFile);
         System.out.println(e.getMessage());
      }
      
      return vdata;
   }
}
