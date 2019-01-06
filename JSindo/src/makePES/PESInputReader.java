package makePES;

import java.io.File;
import java.io.IOException;
import java.util.*;

import jobqueue.QueueMngr;

import molecule.*;
import qchem.InputMaker;
import qchem.QuantChem;
import qchem.TypeNotSupportedException;
import sys.Utilities;
import sys.XMLHandler;

/**
 * Reads the input data from makePES.xml.
 * @author Kiyoshi Yagi
 * @version 1.2
 * @since Sindo 3.2
 */
public class PESInputReader {

   private String xmlFile = "makePES.xml";
   
   public void setFilename(String xmlFile) {
      this.xmlFile = xmlFile;
   }
   
   /**
    * Reads the data from makePES.xml
    * @return The data stored in MakePESData.
    */
   public PESInputData read(){
      
      PESInputData makePESData = new PESInputData();
      
      System.out.println();
      System.out.println("Launch MakePES module");
      System.out.println();

      System.out.printf("  o Input options read via "+xmlFile+" ... ");
      XMLHandler options = new XMLHandler();
      try{
         options.readXMLFile(xmlFile);
      }catch(IOException e){
         System.out.println("Fatal error.");
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
      System.out.println(" [OK] ");
      
      String runtype = options.getValue("runtype");
      if(runtype == null){
         this.errorTermination("runtype entry is not found!");
      }
      runtype = runtype.trim().toUpperCase();
      if(! runtype.equals("QFF") && ! runtype.equals("GRID") && ! runtype.equals("HYBRID")){
         this.errorTermination("runtype = "+ runtype + " is not a valid option.");
      }
      System.out.println("     - RunType = " + runtype);
      makePESData.setRunType(runtype);
      
      String filename = options.getValue("molecule");
      if(filename == null){
         this.errorTermination("molecule entry is not found!");
      }
      filename = filename.trim();
      makePESData.setMinfofile(filename);
      System.out.printf("     - Molecular info via "+filename+" ... ");
      MInfoIO minfoIO = new MInfoIO();
      try{
         minfoIO.loadMOL(filename);
      }catch(IOException e){
         this.errorTermination(e.getMessage());
      }
      makePESData.setMolecule(minfoIO.getMolecule());
      if(minfoIO.getMolecule().getVibrationalData() == null){
         this.errorTermination("Vibrational data is not found in "+filename);
      }
      System.out.println(" [OK] ");
      
      Boolean interdomain = false;
      String intd = options.getValue("interdomain");
      if(intd == null || intd.equalsIgnoreCase("false")) {
         interdomain = false;
      }else if(intd.equalsIgnoreCase("true")){
         interdomain = true;
      }else{
         this.errorTermination("interdomain = "+intd+" is not a valid option.");
      }
      System.out.println("     - InterDomain coupling = " + interdomain);

      if(interdomain) {
         VibUtil vutil = new VibUtil(minfoIO.getMolecule());
         vutil.combineAllVibData();
      }
      
      String amode = options.getValue("activemode");
      int nd = minfoIO.getMolecule().getNumOfVibrationalData();
      int[][] activeModes = new int[nd][];
      if(amode == null){
         
         int ni=0;
         for(int n=0; n<nd; n++){
            int Nfree = minfoIO.getMolecule().getVibrationalData(n).Nfree;
            activeModes[n] = new int[Nfree];
            for(int i=0; i<Nfree; i++){
               activeModes[n][i] = ni;
               ni++;
            }            
         }
         
      }else{
         String[] temp = splitline(amode);
         int j=0;
         for(int i=0; i<temp.length; i++){
            if(temp[i].indexOf("-")==-1){
               j++;
            }else{
               String[] mm = temp[i].split("-");
               int m1 = Integer.parseInt(mm[0]);
               int m2 = Integer.parseInt(mm[1]);
               j = j + m2-m1+1;
            }
         }
         
         int[] modes = new int[j];
         j=0;
         for(int i=0; i<temp.length; i++){
            if(temp[i].indexOf("-")==-1){
               modes[j] = Integer.parseInt(temp[i])-1;
               j++;
            }else{
               String[] mm = temp[i].split("-");
               int m1 = Integer.parseInt(mm[0]);
               int m2 = Integer.parseInt(mm[1]);
               for(int m=m1; m<=m2; m++){
                  modes[j] = m-1;
                  j++;
               }
            }
         }
         
         Arrays.sort(modes);

         int ns=0;
         int nm=0;
         for(int n=0; n<nd; n++){
            int nf=minfoIO.getMolecule().getVibrationalData(n).Nfree;
            int[] ii = new int[nf];

            int ni=0;
            if(nm < modes.length){
               for(int i=0; i<nf; i++){
                  if(modes[nm] == ns){
                     ii[ni]=ns;
                     ni++;
                     nm++;
                     if(nm == modes.length) break;
                  }
                  ns++;
               }               
            }
            
            if(ni>0){
               activeModes[n] = new int[ni];
               for(int i=0; i<ni; i++){
                  activeModes[n][i] = ii[i];
               }
            }else{
               activeModes[n] = null;
            }
         }
         
         System.out.println("     - Active Modes:");

         for(int n=0; n<nd; n++){
            if(activeModes[n] == null) continue;
            System.out.println("        * Domain "+(n+1));
            System.out.print  ("          ");
            for(int i=0; i<activeModes[n].length-1; i++){
               System.out.printf("%3d ",activeModes[n][i]+1);
               if(i%10 == 9) {
                  System.out.println();
                  System.out.print  ("          ");
               }
            }
            {
               int i = activeModes[n].length-1;
               System.out.printf("%3d ",activeModes[n][i]+1);
               System.out.println();
            }     
         }

      }
      makePESData.setActiveModes(activeModes);
      
      String value = options.getValue("MR");
      int MR = 3;
      if(value != null){
         MR = Integer.parseInt(options.getValue("MR"));
      }
      System.out.println("     - MR      = " + MR);
      makePESData.setMR(MR);
      
      boolean dipole = false;
      String dpl = options.getValue("dipole");
      if(dpl == null || dpl.equalsIgnoreCase("false")){
         dipole = false;
      }else if(dpl.equalsIgnoreCase("true")){
         dipole = true;
      }else{
         this.errorTermination("dipole = "+dpl+" is not a valid option.");         
      }
      System.out.println("     - Dipole Moment Surface = " + dipole);
      makePESData.setDipole(dipole);
      
      System.out.println("     - Settings for Quantum Chemistry Jobs:");

      boolean removeFiles=false;
      String rf = options.getValue("removefiles");
      if(rf == null || rf.equalsIgnoreCase("false")) {
         removeFiles = false;
      }else if(rf.equalsIgnoreCase("true")){
         removeFiles = true;
      }else{
         this.errorTermination("removefiles = "+rf+" is not a valid option.");
      }
      System.out.println("        * Remove Files = " + removeFiles);
      makePESData.setRemoveFiles(removeFiles);
      
      boolean dryrun=false;
      String dr = options.getValue("dryrun");
      if(dr == null || dr.equalsIgnoreCase("false")) {
         dryrun = false;
      }else if(dr.equalsIgnoreCase("true")){
         dryrun = true;
      }else{
         this.errorTermination("dryrun = "+dr+" is not a valid option.");
      }
      System.out.println("        * DryRun       = " + dryrun);
      makePESData.setDryRun(dryrun);

      String temp = options.getValue("qchem");
      /** It seems that the return tag is "\n" when the string is read from 
       *  XML loader no matter what the OS is!? **/
      String[] ts = temp.split("\n");
      int nline = 0;
      for(int n=0; n<ts.length; n++){
         if(ts[n].trim().length() > 0){
            nline++;
         }
      }
      String[] lines = new String[nline];
      nline = 0;
      for(int n=0; n<ts.length; n++){
         if(ts[n].trim().length() > 0){
            lines[nline] = ts[n];
            nline++;
         }
      }
      
      if(lines[0].toUpperCase().indexOf("GENERIC") >= 0){
         makePESData.setRunQchem(false);
         System.out.print("        * Type         = GENERIC");

         lines[0] = lines[0].replaceAll(",", " ");
         String[] str = Utilities.splitWithSpaceString(lines[0]);
         String[] title = new String[1];
         
         title[0] = "";
         for(int i=1; i<str.length; i++){
            title[0] = title[0] +" "+ str[i];
         }
         System.out.println();
         System.out.println("          Title        = " +title[0]);
         makePESData.setTitle(title);

         String basename = options.getValue("xyzfile");
         if(basename == null) {
            if(runtype.equals("QFF")){
               basename = "makeQFF";
            }else {
               basename = "makeGrid";               
            }
         }else {
            basename = basename.trim();
         }
         System.out.println("          xyzfile basename = " + basename);
         makePESData.setXYZFile_basename(basename);
         
         if(runtype.equals("QFF")){
            File mkqff_eq = new File(MakeQFF.getBasename()+".minfo");
            if(! mkqff_eq.exists()) {
               System.out.println("            # " +mkqff_eq.getName()+" is not found. Switching to DryRun=true.");
               makePESData.setDryRun(true);
            }
            
         }else if(runtype.equals("GRID")){
            File makeGrid_dat = new File(basename+".dat");
            if(! makeGrid_dat.exists()) {
               System.out.println("            # " +basename+".dat is not found. Switching to DryRun=true.");
               makePESData.setDryRun(true);
            }
         }
         
         System.out.println();
         
      }else{
         makePESData.setRunQchem(true);
         
         int nn = lines.length;
         String[] qchemTypes = new String[nn];
         XMLHandler[] qchemInputs = new XMLHandler[nn];
         String[] titles = new String[nn];
         for(int n=0; n<lines.length; n++){
            lines[n] = lines[n].replaceAll(",", " ");
            String[] str = Utilities.splitWithSpaceString(lines[n]);
            
            QuantChem qcpack = null;
            qchemTypes[n] = str[0];
            try{
               qcpack = new QuantChem(qchemTypes[n]);
            }catch(TypeNotSupportedException e){
               this.errorTermination(e.getMessage());
            }
            
            InputMaker im = qcpack.getInputMaker();
            try{
               im.setOptions(str[1]);
               qchemInputs[n] = im.getOptions();
            }catch(Exception e){
               this.errorTermination(e.getMessage());
            }
            
            titles[n] = "";
            for(int i=2; i<str.length; i++){
               titles[n] = titles[n] +" "+ str[i];
            }
            
            qcpack.getExec().setBasename("basename");
            String[] cmd = qcpack.getExec().getCommand();
            System.out.println("        * Level-" + n);
            System.out.println("          Type         = " +qchemTypes[n]);
            System.out.println("          TemplateFile = " +str[1]);
            System.out.print  ("          ExecCommand  = ");
            for(int i=0; i<cmd.length; i++){
               System.out.print(cmd[i]+" ");
            }
            System.out.println();
            System.out.println("          Title        = " +titles[n]);
            System.out.println();
            
         }
         makePESData.setQchemTypes(qchemTypes);
         makePESData.setQchemInputs(qchemInputs);
         makePESData.setTitle(titles);
         
      }
      
      if(runtype.equals("QFF") || runtype.equals("HYBRID")){

         System.out.println("     - Settings for QFF:");
         
         double stepsize = 0.5; 
         String ss = options.getValue("stepsize");
         if(ss != null){
            stepsize = Double.parseDouble(ss.trim());
         }
         System.out.printf("        * Stepsize   = %-8.3f \n",stepsize);
         makePESData.setStepsize(stepsize);
         
         String ndifftype = options.getValue("ndifftype");
         if(ndifftype == null){
            ndifftype = "hess";
         }
         if(! ndifftype.equalsIgnoreCase("ene")  &&
            ! ndifftype.equalsIgnoreCase("grad") &&
            ! ndifftype.equalsIgnoreCase("hess")){
            
               this.errorTermination("ndifftype = " + ndifftype + " is not a valid option.");
         }         
         System.out.println("        * ndifftype = "+ndifftype);
         ndifftype = ndifftype.toUpperCase();
         makePESData.setNdifftype(ndifftype);
         
         String mopFile = options.getValue("mopfile");
         if(mopFile == null){
            mopFile = "prop_no_1.mop";
         }
         System.out.println("        * mopfile   = "+mopFile);
         makePESData.setMopfile(mopFile);
         
         boolean genhs = false;
         String hs = options.getValue("genhs");
         if(hs == null || hs.equalsIgnoreCase("false")){
            genhs = false;
         }else if(hs.equalsIgnoreCase("true")){
            genhs = true;
         }else{
            this.errorTermination("genhs = " + hs + " is not a valid option.");
         }
         System.out.println("        * Generate hs file = " + genhs);
         makePESData.setGenhs(genhs);

         String gh = options.getValue("gradient_and_hessian");
         if(gh == null || gh.equalsIgnoreCase("input")){
            gh = "INPUT";
         }else if(gh.equalsIgnoreCase("current")){
            gh = "CURRENT";
         }else{
            this.errorTermination("gradient_and_hessian = " + gh + "is not a valid option");
         }
         System.out.println("        * Gradient_and_Hessian = " + gh);
         makePESData.setGradient_and_hessian(gh);
         if(makePESData.getGradient_and_hessian().equals("INPUT")){
            ElectronicData edata = makePESData.getMolecule().getElectronicData();
            if(edata.getGradient() == null || edata.getHessian() == null){
               this.errorTermination("Gradient and/or Hessian not found in "+ filename + ". \n "
                     + "They are required when Gradient_and_Hessian is set to INPUT.");
            }
            
         }

         
         System.out.println();

      }
      
      if(runtype.equals("GRID")){
         
         System.out.println("     - Settings for Grid PES:");
         
         int nGrid = 11; 
         String ss = options.getValue("ngrid");
         if(ss != null){
            nGrid = Integer.parseInt(ss.trim());
         }
         System.out.printf("        * Grid points = %-8d \n",nGrid);
         makePESData.setnGrid(nGrid);
         
         boolean fullmc=false;
         String fmc = options.getValue("fullmc");
         if(fmc == null || fmc.equalsIgnoreCase("false")) {
            fullmc = false;
         }else if(fmc.equalsIgnoreCase("true")){
            fullmc = true;
         }else{
            this.errorTermination("fullmc = "+fmc+" is not a valid option.");
         }
         System.out.println("        * Full MC = " + fullmc);
         makePESData.setFullMC(fullmc);

         if(! fullmc){
            boolean isMc = false;
            
            ss = options.getValue("mc1");
            if(ss != null){
               isMc = true;
               String[] mr1 = splitline(ss);
               System.out.print("        * 1-mode coupling = ");
               for(int i=0; i<mr1.length-1; i++){
                  System.out.print(mr1[i]+", ");
               }
               System.out.print(mr1[mr1.length-1]);
               System.out.println();
               makePESData.setMC1(mr1);
            }

            ss = options.getValue("mc2");
            if(ss != null){
               isMc = true;
               String[] s1 = splitline(ss);
               if(s1.length%2 != 0){
                  errorTermination("Invalid number of input in mr2.");
               }
               String[] mr2 = new String[s1.length/2];
               
               System.out.print("        * 2-mode coupling = ");
               for(int i=0; i<mr2.length; i++){
                  mr2[i] = s1[2*i]+","+s1[2*i+1];
               }
               int i=0;
               while(i<mr2.length-1){
                  System.out.print("("+mr2[i]+"), ");
                  i++;
               }
               System.out.print("("+mr2[i]+")");
               System.out.println();
               makePESData.setMC2(mr2);
            }

            ss = options.getValue("mc3");
            if(ss != null){
               isMc = true;
               String[] s1 = splitline(ss);
               if(s1.length%3 != 0){
                  errorTermination("Invalid number of input in mr3.");
               }
               String[] mr3 = new String[s1.length/3];
               
               System.out.print("        * 3-mode coupling = ");
               for(int i=0; i<mr3.length; i++){
                  mr3[i] = s1[3*i]+","+s1[3*i+1]+","+s1[3*i+2];
               }
               int i=0;
               while(i<mr3.length-1){
                  System.out.print("("+mr3[i]+"), ");
                  i++;
               }
               System.out.print("("+mr3[i]+")");
               System.out.println();
               makePESData.setMC3(mr3);
            }
            
            if(! isMc){
               this.errorTermination("Mode coupling term is not given. Specify using [fullmc/mc1/mc2/mc3].");
            }

         }

         
         System.out.println();
      }
      
      if(runtype.equals("HYBRID")){
         System.out.println("     - Settings for Grid PES:");
         
         int nGrid = 11; 
         String ss = options.getValue("ngrid");
         if(ss != null){
            nGrid = Integer.parseInt(ss.trim());
         }
         System.out.printf("        * Grid points = %-8d \n",nGrid);
         makePESData.setnGrid(nGrid);
         
         double mcs = -1.0;
         ss = options.getValue("mcstrength");
         if(ss == null){
            this.errorTermination("mcstrength entry is not found!");
         }
         mcs = Double.parseDouble(ss);
         System.out.println("        * MC Strength = "+mcs);
         makePESData.setThresh_MCS(mcs);

         System.out.println();
      }

      if(makePESData.isRunQchem()){
         System.out.printf("  o Queue Manager via resources.info ... ");
         QueueMngr queue = QueueMngr.getInstance();
         System.out.println(" [OK] ");
         queue.printResources("     ");
         System.out.println();
         
      }
      
      System.out.println();
      System.out.println("MakePES is setup successfully!");
      System.out.println();
      
      return makePESData;

   }
   
   private String[] splitline(String ss){
   
      ss = ss.replaceAll(",", " ");
      ss = ss.replaceAll("\n"," ");
      String[] s1 = Utilities.splitWithSpaceString(ss);
      int len = s1.length;
      for(int i=0; i<s1.length; i++){
         // System.out.println(i+" "+s1[i]+" "+s1[i].indexOf("-")+" "+s1[i].length());
         if(s1[i].equals("-")){
            s1[i-1] = s1[i-1]+s1[i]+s1[i+1];
            s1[i] = null;
            s1[i+1] = null;
            i++;
            len = len-2;
         }else if(s1[i].indexOf("-")==0){
            s1[i-1] = s1[i-1]+s1[i];
            s1[i] = null;
            len = len-1;
         }else if(s1[i].indexOf("-")==s1[i].length()-1){
            s1[i] = s1[i]+s1[i+1];
            s1[i+1] = null;
            i++;
            len = len-1;
         }
      }
      String[] mm = new String[len];
      len=0;
      for(int i=0; i<s1.length; i++){
         if(s1[i] != null){
            mm[len] = s1[i];
            len++;
         }
      }
      return mm;

   }

   private void errorTermination(String message){
      System.out.println();
      System.out.println("Format error in makePES.xml.");
      System.out.println();
      System.out.print("Error:  ");
      System.out.println(message);
      System.out.println();
      Utilities.terminate();      
   }
   

}
