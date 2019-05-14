package makePES;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
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
public class InputReader {

   private String xmlFile = "makePES.xml";
   
   public void setFilename(String xmlFile) {
      this.xmlFile = xmlFile;
   }
   
   /**
    * Reads the data from makePES.xml (version 1)
    * @return The data stored in MakePESData.
    */
   public InputDataPES read(){
      
      InputDataPES makePESData = new InputDataPES();
      
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
      InputDataGrid gridData = null;
      InputDataQFF  qffData  = null;
      if(runtype.equals("QFF")) {
         qffData = new InputDataQFF();
         makePESData.setQFFInfo(qffData);
      }else if(runtype.equals("GRID")) {
         gridData = new InputDataGrid();
         makePESData.setGridInfo(gridData);
      }else if(runtype.equals("HYBRID")) {
         qffData = new InputDataQFF();
         gridData = new InputDataGrid();
         makePESData.setQFFInfo(qffData);
         makePESData.setGridInfo(gridData);
      }
      
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
      int[][] activeModes = this.setupActiveMode(amode, minfoIO.getMolecule());
      System.out.println("     - Active Modes:");

      for(int n=0; n<activeModes.length; n++){
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
         
         InputDataQC qcInfo = new InputDataQC();
         qcInfo.setDryrun(dryrun);
         qcInfo.setRemoveFile(removeFiles);
         qcInfo.setType("GENERIC");
         qcInfo.setTitle(title[0]);

         String qcindex = "0";
         makePESData.setQCInfo(qcindex, qcInfo);
         
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
         
         if(runtype.equals("QFF")){
            qcInfo.setXyzBasename(basename);
            File mkqff_eq = new File(MakeQFF.getBasename()+".minfo");
            if(! mkqff_eq.exists()) {
               System.out.println("            # " +MakeQFF.getBasename()+".minfo is not found. Switching to DryRun=true.");
               qcInfo.setDryrun(true);
            }
            qffData.setQcID(qcindex);
            
         }else if(runtype.equals("GRID")){
            qcInfo.setXyzBasename(basename);
            File makeGrid_dat = new File(basename+".dat");
            if(! makeGrid_dat.exists()) {
               System.out.println("            # " +basename+".dat is not found. Switching to DryRun=true.");
               qcInfo.setDryrun(true);
            }
            gridData.setQcID(qcindex);
         }
         
         System.out.println();
         
      }else{

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
            
            InputDataQC qcInfo = new InputDataQC();
            qcInfo.setDryrun(dryrun);
            qcInfo.setRemoveFile(removeFiles);
            qcInfo.setType(qchemTypes[n]);
            qcInfo.setInputOption(qchemInputs[n]);
            qcInfo.setTitle(titles[n]);
            
            makePESData.setQCInfo(Integer.toString(n), qcInfo);

         }
         
         if(runtype.equals("QFF")) {
            qffData.setQcID("0");
         }else if(runtype.equals("GRID")) {
            gridData.setQcID("0");
         }else if(runtype.equals("HYBRID")) {
            qffData.setQcID("0");
            gridData.setQcID("1");
         }
         
      }
      
      if(runtype.equals("QFF") || runtype.equals("HYBRID")){

         System.out.println("     - Settings for QFF:");
         
         double stepsize = 0.5; 
         String ss = options.getValue("stepsize");
         if(ss != null){
            stepsize = Double.parseDouble(ss.trim());
         }
         System.out.printf("        * Stepsize   = %-8.3f \n",stepsize);
         qffData.setStepsize(stepsize);
         
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
         qffData.setNdifftype(ndifftype);
         
         String mopFile = options.getValue("mopfile");
         if(mopFile == null){
            mopFile = "prop_no_1.mop";
         }
         System.out.println("        * mopfile   = "+mopFile);
         qffData.setMopfile(mopFile);
         if(runtype.equals("HYBRID")) {
            gridData.setMopfile(mopFile);
         }
         
         Boolean interdomain_hc = false;
         String intd_hc = options.getValue("interdomain_hc");
         if(intd_hc == null || intd_hc.equalsIgnoreCase("true")) {
            interdomain_hc = true;
         }else if(intd_hc.equalsIgnoreCase("false")){
            interdomain_hc = false;
         }else{
            this.errorTermination("interdomain_hc = "+intd_hc+" is not a valid option.");
         }
         System.out.println("        * InterDomain_hc (Harmonic Coupling) = " + interdomain_hc);
         qffData.setInterdomain_hc(interdomain_hc);

         boolean genhs = false;
         String hs = options.getValue("genhs");
         if(hs == null || hs.equalsIgnoreCase("false")){
            genhs = false;
         }else if(hs.equalsIgnoreCase("true")){
            genhs = true;
         }else{
            this.errorTermination("genhs = " + hs + " is not a valid option.");
         }
         System.out.println("        * Genhs (Generate hs file) = " + genhs);
         qffData.setGenhs(genhs);

         String gh = options.getValue("gradient_and_hessian");
         if(gh == null || gh.equalsIgnoreCase("input")){
            gh = "INPUT";
         }else if(gh.equalsIgnoreCase("current")){
            gh = "CURRENT";
         }else{
            this.errorTermination("gradient_and_hessian = " + gh + "is not a valid option");
         }
         System.out.println("        * Gradient_and_Hessian = " + gh);
         qffData.setGradient_and_hessian(gh);
         if(gh.equals("INPUT")){
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
         gridData.setnGrid(nGrid);
         
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
         gridData.setFullMC(fullmc);

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
               gridData.setMC1(mr1);
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
               gridData.setMC2(mr2);
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
               gridData.setMC3(mr3);
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
         gridData.setnGrid(nGrid);
         
         double mcs = -1.0;
         ss = options.getValue("mcstrength");
         if(ss == null){
            this.errorTermination("mcstrength entry is not found!");
         }
         mcs = Double.parseDouble(ss);
         System.out.println("        * MC Strength = "+mcs);
         gridData.setThresh_MCS(mcs);

         System.out.println();
      }
      
      System.out.println();
      System.out.println("MakePES is setup successfully!");
      System.out.println();
      
      return makePESData;

   }

   /**
    * Reads the data from makePES.xml (version 2)
    * @return The data stored in MakePESData.
    */
   public InputDataPES read2() {

      // keywords
      String key_value = "value";
      String key_id    = "id";
      
      // Setup makePESData
      InputDataPES makePESData = new InputDataPES();
      
      // Default values
      String  filename    = null;
      int[][] activeModes = null;
      boolean interdomain = false;
      int     MR          = 3;
      boolean dipole      = false;
      makePESData.setMR(MR);
      makePESData.setDipole(dipole);
      
      // Utilities
      MInfoIO minfoIO = new MInfoIO();

      System.out.println();
      System.out.println("Launch MakePES module");
      System.out.println();

      System.out.printf("  o Input options read via "+xmlFile+" ... ");

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      Document doc = null;
      try {
         DocumentBuilder db = dbf.newDocumentBuilder();
         doc = db.parse(new File(xmlFile));
      } catch (IOException e) {
         System.out.println("Fatal error.");
         System.out.println(e.getMessage());
         Utilities.terminate();
      } catch (Exception e) {
         e.printStackTrace();
         this.errorTermination(e.getMessage());
      }
      
      System.out.println(" [OK] ");

      Element makePES = doc.getDocumentElement();
      
      NodeList options = makePES.getChildNodes();
      
      // Search for minfofile first.
      for(int i=0; i<options.getLength(); i++) {
         Node node = options.item(i);
         if(node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            Element element = (Element)node;
            if(name.equalsIgnoreCase("minfofile")) {
               filename = element.getAttribute("value").trim();
               System.out.printf("     - Molecular info via "+filename+" ... ");
               try{
                  minfoIO.loadMOL(filename);
               }catch(IOException e){
                  this.errorTermination(e.getMessage());
               }
               if(minfoIO.getMolecule().getVibrationalData() == null){
                  this.errorTermination("Vibrational data is not found in "+filename);
               }
               System.out.println(" [OK] ");
               break;
            }
         }
      }
      if(filename == null){
         this.errorTermination("minfofile entry is not found!");
      }
      makePESData.setMinfofile(filename);
      makePESData.setMolecule(minfoIO.getMolecule());

      // Set up other parameters.
      String actv = null;
      int num_of_qc   = 0;
      int num_of_qff  = 0;
      int num_of_grid = 0;
      
      for(int i=0; i<options.getLength(); i++) {
         Node node = options.item(i);
         if(node.getNodeType() == Node.ELEMENT_NODE) {
            String  name    = node.getNodeName();
            Element element = (Element)node;
            String  value   = element.getAttribute(key_value).trim();

            if(name.equalsIgnoreCase("minfofile")) {
               continue;
               
            } else if(name.equalsIgnoreCase("mr")) {
               try {
                  MR = Integer.parseInt(value);
               }catch (NumberFormatException e) {
                  this.errorTermination("MR = "+value+" is not a valid option.");
               }
               makePESData.setMR(MR);

            }else if(name.equalsIgnoreCase("activemode")) {
               actv = value;

            }else if(name.equalsIgnoreCase("dipole")) {
               if(value.equalsIgnoreCase("false")) {
                  dipole = false;
               }else if(value.equalsIgnoreCase("true")){
                  dipole = true;
               }else{
                  this.errorTermination("dipole = "+value+" is not a valid option.");
               }
               makePESData.setDipole(dipole);
               
            }else if(name.equalsIgnoreCase("interdomain")) {
               if(value.equalsIgnoreCase("false")) {
                  interdomain = false;
               }else if(value.equalsIgnoreCase("true")){
                  interdomain = true;
               }else{
                  this.errorTermination("interdomain = "+value+" is not a valid option.");
               }
               
            } else if(name.equalsIgnoreCase("qchem")) {
               
               num_of_qc++;
               
               // setup qcdata
               InputDataQC qcinfo = new InputDataQC();
               if(! element.getAttribute(key_id).isEmpty()) {
                  makePESData.setQCInfo(element.getAttribute(key_id), qcinfo);
               }else{
                  makePESData.setQCInfo(Integer.toString(num_of_qc), qcinfo);
               }
              
               // default
               qcinfo.setType(null);
               qcinfo.setDryrun(false);
               qcinfo.setRemoveFile(false);
               qcinfo.setInputFile(null);
               qcinfo.setXyzBasename(null);

               NodeList qclist = node.getChildNodes();
               for(int j=0; j<qclist.getLength(); j++) {
                  Node qcNode = qclist.item(j);
                  if(qcNode.getNodeType() == Node.ELEMENT_NODE) {
                     String  qcName    = qcNode.getNodeName();
                     Element qffElement = (Element)qcNode;
                     String  qcvalue   = qffElement.getAttribute(key_value).trim();
                     
                     if(qcName.equalsIgnoreCase("program")){
                        qcinfo.setType(qcvalue);

                     } else if(qcName.equalsIgnoreCase("dryrun")) {
                        if(qcvalue.equalsIgnoreCase("true")) {
                           qcinfo.setDryrun(true);
                        } else if(qcvalue.equalsIgnoreCase("false")) {
                           qcinfo.setDryrun(false);
                        } else {
                           this.errorTermination("dryrun = [TRUE|FALSE]. "+qcvalue+" is not a valid option.");
                        }
                        
                     } else if(qcName.equalsIgnoreCase("removefiles")) {
                        if(qcvalue.equalsIgnoreCase("true")) {
                           qcinfo.setRemoveFile(true);
                        } else if(qcvalue.equalsIgnoreCase("false")) {
                           qcinfo.setRemoveFile(false);
                        } else {
                           this.errorTermination("removefiles = [TRUE|FALSE]. "+qcvalue+" is not a valid option.");
                        }
                        
                     } else if(qcName.equalsIgnoreCase("title")) {
                        qcinfo.setTitle(qcvalue);
                        
                     } else if(qcName.equalsIgnoreCase("xyzfile")) {
                        qcinfo.setXyzBasename(qcvalue);
                        
                     } else if(qcName.equalsIgnoreCase("template")) {
                        qcinfo.setInputFile(qcvalue);
                        String  xml = qffElement.getAttribute("xml").trim();
                        if(xml.equalsIgnoreCase("yes") || xml.equalsIgnoreCase("true")) {
                           qcinfo.setOption(true);
                        } else {
                           qcinfo.setOption(false);
                        }

                     } else {
                        this.errorTermination("keyword "+qcName+" is not a valid option.");
                     }
                     
                  }
               }

               if(qcinfo.getType() == null) {
                  this.errorTermination("program is not specified in a qchem group.");
                  
               }else if(qcinfo.getType().equalsIgnoreCase(InputDataQC.GENERIC)) {
                  if(qcinfo.getXyzBasename() == null) {
                     this.errorTermination("XYZfile is not specified.");
                  }
                  
               }else {                  
                  if(qcinfo.getInputFile() == null) {
                     this.errorTermination("template file is not specified for qchem jobs.");
                  }
                  try{
                     QuantChem qcpack = new QuantChem(qcinfo.getType());
                     InputMaker im = qcpack.getInputMaker();
                     if(qcinfo.isOption()) {
                        im.setOptions(qcinfo.getInputFile());
                        qcinfo.setInputOption(im.getOptions());
                     }else {
                        im.setTemplateFile(qcinfo.getInputFile());
                        qcinfo.setInputTemplate(im.getTemplateFile());
                     }
                     
                  }catch(Exception e){
                     this.errorTermination(e.getMessage());
                  }
               }
               
            }else if(name.equalsIgnoreCase("qff")) {
               
               num_of_qff++;
               
               if(num_of_qff > 1) {
                  this.errorTermination("There are more than one entries for QFF. QFF entry must be at most one.");
               }
               
               // setup qffdata
               InputDataQFF qffdata = new InputDataQFF();
               makePESData.setQFFInfo(qffdata);

               // default values
               qffdata.setQcID("1");
               qffdata.setStepsize(0.5d);
               qffdata.setNdifftype("hess");
               qffdata.setMopfile("prop_no_1.mop");
               qffdata.setGenhs(false);
               qffdata.setGradient_and_hessian("input");
               qffdata.setInterdomain_hc(true);
               
               NodeList qfflist = node.getChildNodes();
               for(int j=0; j<qfflist.getLength(); j++) {
                  Node qffNode = qfflist.item(j);
                  if(qffNode.getNodeType() == Node.ELEMENT_NODE) {
                     String  qffName    = qffNode.getNodeName();
                     Element qffElement = (Element)qffNode;
                     String  qffvalue   = qffElement.getAttribute(key_value).trim();
                     
                     if(qffName.equalsIgnoreCase("qcid")){
                        qffdata.setQcID(qffvalue);
                        
                     } else if(qffName.equalsIgnoreCase("stepsize")){
                        double stp = 0.0;
                        try {
                           stp = Double.parseDouble(qffvalue);
                        }catch (NumberFormatException e) {
                           this.errorTermination("stepsize must be a real number. "+qffvalue+" is not a valid option.");
                        }
                        qffdata.setStepsize(stp);
                        
                     } else if(qffName.equalsIgnoreCase("ndifftype")) {
                        if(qffvalue.equalsIgnoreCase("grad") || qffvalue.equalsIgnoreCase("hess")) { 
                           qffdata.setNdifftype(qffvalue);
                        } else {
                           this.errorTermination("ndifftype = [HESS|GRAD]. "+qffvalue+" is not a valid option.");
                        }
                        
                     } else if(qffName.equalsIgnoreCase("mopfile")) {
                        qffdata.setMopfile(qffvalue);
                        
                     } else if(qffName.equalsIgnoreCase("genhs")) {
                        if(qffvalue.equalsIgnoreCase("true")) {
                           qffdata.setGenhs(true);
                        } else if(qffvalue.equalsIgnoreCase("false")) {
                           qffdata.setGenhs(false);
                        } else {
                           this.errorTermination("genhs = [TRUE|FALSE]. "+qffvalue+" is not a valid option.");
                        }
                        
                     } else if(qffName.equalsIgnoreCase("gradient_and_hessian")) {
                        if(qffvalue.equalsIgnoreCase("input") | qffvalue.equalsIgnoreCase("current")) {
                           qffdata.setGradient_and_hessian(qffvalue);
                        } else {
                           this.errorTermination("gradient_and_hessian = [INPUT|CURRENT]. "+qffvalue+" is not a valid option.");
                        }
                        
                     } else if(qffName.equalsIgnoreCase("interdomain_hc")) {
                        if(qffvalue.equalsIgnoreCase("true")) {
                           qffdata.setInterdomain_hc(true);
                        } else if(qffvalue.equalsIgnoreCase("false")) {
                           qffdata.setInterdomain_hc(false);
                        } else {
                           this.errorTermination("interdomain_hc = [TRUE|FALSE]. "+qffvalue+" is not a valid option.");
                        }
                     }
                  }
               }
               
            }else if(name.equalsIgnoreCase("grid")) {
               
               num_of_grid++;

               // setup griddata
               InputDataGrid griddata = new InputDataGrid();
               makePESData.setGridInfo(griddata);

               // default values
               griddata.setQcID("1");
               griddata.setnGrid(11);
               griddata.setFullMC(false);
               griddata.setMC1(null);
               griddata.setMC2(null);
               griddata.setMC3(null);
               griddata.setThresh_MCS(-1.0d);
               griddata.setMopfile("prop_no_1.mop");
               
               boolean isMc = false;
               
               NodeList gridlist = node.getChildNodes();
               for(int j=0; j<gridlist.getLength(); j++) {
                  Node gridNode = gridlist.item(j);
                  if(gridNode.getNodeType() == Node.ELEMENT_NODE) {
                     String  gridName    = gridNode.getNodeName();
                     Element gridElement = (Element)gridNode;
                     String  gridvalue   = gridElement.getAttribute(key_value).trim();
                     
                     if(gridName.equalsIgnoreCase("qcid")){
                        griddata.setQcID(gridvalue);
                        
                     } else if(gridName.equalsIgnoreCase("ngrid")){
                        int ngrid = 0;
                        try {
                           ngrid = Integer.parseInt(gridvalue);
                        }catch (NumberFormatException e) {
                           this.errorTermination("ngrid must be an integer number. "+gridvalue+" is not a valid option.");
                        }
                        griddata.setnGrid(ngrid);
                           
                     } else if(gridName.equalsIgnoreCase("fullmc")) {
                        isMc = true;
                        if(gridvalue.equalsIgnoreCase("true")) {
                           griddata.setFullMC(true);
                        } else if(gridvalue.equalsIgnoreCase("false")) {
                           griddata.setFullMC(false);
                        } else {
                           this.errorTermination("fullmc= [TRUE|FALSE]. "+gridvalue+" is not a valid option.");
                        }

                     } else if(gridName.equalsIgnoreCase("mc1")) {
                        isMc = true;
                        String[] mr1 = splitline(gridvalue);
                        griddata.setMC1(mr1);

                     } else if(gridName.equalsIgnoreCase("mc2")) {
                        isMc = true;
                        String[] s1 = splitline(gridvalue);
                        if(s1.length%2 != 0){
                           errorTermination("Invalid number of input in mr2.");
                        }
                        String[] mr2 = new String[s1.length/2];
                        for(int ii=0; ii<mr2.length; ii++){
                           mr2[ii] = s1[2*ii]+","+s1[2*ii+1];
                        }
                        
                        griddata.setMC2(mr2);
                        
                     } else if(gridName.equalsIgnoreCase("mc3")) {
                        isMc = true;
                        String[] s1 = splitline(gridvalue);
                        if(s1.length%3 != 0){
                           errorTermination("Invalid number of input in mr3.");
                        }
                        String[] mr3 = new String[s1.length/3];
                        
                        for(int ii=0; ii<mr3.length; ii++){
                           mr3[ii] = s1[3*ii]+","+s1[3*ii+1]+","+s1[3*ii+2];
                        }

                        griddata.setMC3(mr3);
                        
                     } else if(gridName.equalsIgnoreCase("mopfile")) {
                        griddata.setMopfile(gridvalue);
                        
                     } else if(gridName.equalsIgnoreCase("mcsstrength")){
                        double mcs = 0.0;
                        try {
                           mcs = Double.parseDouble(gridvalue);
                        }catch (NumberFormatException e) {
                           this.errorTermination("stepsize must be a real number. "+gridvalue+" is not a valid option.");
                        }
                        griddata.setThresh_MCS(mcs);
                        isMc = true;
                     }
                  }
               }
               if(! isMc && griddata.getThresh_MCS() > 0.0d) {
                  this.errorTermination("keyword for mc is not found! One of fullmc, mc1, mc2, mc3, or mcsstrength must be present.");
                  
               }
               
            } else {
               this.errorTermination("keyword "+name+" is not a valid option.");
            }
            
         }
      }
      
      // Check for errors
      if(num_of_qc == 0){
         this.errorTermination("No qchem is found in the input!");
      }
      if(num_of_qff == 0 && num_of_grid == 0){
         this.errorTermination("No job found! One of qff or grid should be present.");
      }
      
      // final setup
      if(interdomain) {
         VibUtil vutil = new VibUtil(minfoIO.getMolecule());
         vutil.combineAllVibData();
      }
      activeModes = this.setupActiveMode(actv, minfoIO.getMolecule());
      makePESData.setActiveModes(activeModes);
      
      String[] qcIDs = makePESData.getQCInfoKeySet();
      ArrayList<InputDataQFF> qffArray = makePESData.getQFFInfoArray();
      for(int n=0; n<qffArray.size(); n++) {
         boolean foundID = false;
         for(String qcID: qcIDs) {
            if(qcID.equals(qffArray.get(n).getQcID())) {
               foundID = true;
               break;
            }
         }
         if(! foundID) {
            this.errorTermination("QCID: "+ qffArray.get(n).getQcID()+" for QFF is not found. .");
         }
      }
      ArrayList<InputDataGrid> gridArray = makePESData.getGridInfoArray();
      for(int n=0; n<gridArray.size(); n++) {
         boolean foundID = false;
         for(String qcID: qcIDs) {
            if(qcID.equals(gridArray.get(n).getQcID())) {
               foundID = true;
               break;
            }
         }
         if(! foundID) {
            this.errorTermination("QCID: "+ gridArray.get(n).getQcID()+" for GRID is not found. .");
         }
      }

      if(num_of_grid > 0) {
         String qcID0 = gridArray.get(0).getQcID();
         for(int n=1; n<gridArray.size(); n++) {
            if(! qcID0.equals(gridArray.get(n).getQcID())) {
               String ss = "";
               for(int nn=0; nn<gridArray.size(); nn++) {
                  ss += "    grid-"+nn+": QCID = "+ gridArray.get(nn).getQcID()+"\n";
               }
               this.errorTermination("QCID for GRID must be the same for all. \n"+ ss);
            }
         }
      }
      
      // Print settings
      System.out.println("     - InterDomain = " + interdomain);
      System.out.println("     - ActiveModes:");

      for(int n=0; n<activeModes.length; n++){
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
      
      System.out.println("     - MR      = " + MR);
      System.out.println("     - Dipole  = " + dipole);
      System.out.println();
      
      System.out.printf("  o Options for Quantum Chemistry jobs \n\n");
      //String[] qcIDs = makePESData.getQCInfoKeySet();
      for(String qcID: qcIDs) {
         InputDataQC qcdata = makePESData.getQCInfo(qcID);
         String program = qcdata.getType();

         System.out.println("    QCID: " + qcID);
         System.out.println("     - Program      = " + program);
         System.out.println("     - Title        = " + qcdata.getTitle());
         
         if(program.equalsIgnoreCase(InputDataQC.GENERIC)) {
            System.out.println("     - xyzfile      = " + qcdata.getXyzBasename());
            
         }else {
            System.out.println("     - Removefiles  = " + qcdata.isRemoveFile());
            System.out.println("     - Dryryn       = " + qcdata.isDryrun());
            System.out.printf ("     - Template     = " + qcdata.getInputFile());
            if(qcdata.isOption()) {
               System.out.println("  [xml formatted options]");
            }else {
               System.out.println();
            }
            
            try {
               QuantChem qcpack = new QuantChem(qcdata.getType());
               qcpack.getExec().setBasename("basename");
               System.out.print  ("     - ExecCommand  = ");
               String[] cmd = qcpack.getExec().getCommand();
               for(int i=0; i<cmd.length; i++){
                  System.out.print(cmd[i]+" ");
               }
               System.out.println();
            } catch (TypeNotSupportedException e) {
               e.printStackTrace();
            }


         }
         System.out.println();
         
      }
      
      if(qffArray.size() > 0) {
         System.out.printf("  o Options for QFF \n\n");
      }
      for(int n=0; n<qffArray.size(); n++) {
         InputDataQFF qffdata = qffArray.get(n);
         System.out.println("     - QCID         = " + qffdata.getQcID());
         System.out.printf ("     - stepsize     = %-12.2f \n", qffdata.getStepsize());
         System.out.println("     - ndifftype    = " + qffdata.getNdifftype());
         System.out.println("     - mopfile      = " + qffdata.getMopfile());
         System.out.println("     - intradomain_hc       = " + qffdata.isInterdomain_hc());
         System.out.println("     - gradient_and_hessian = " + qffdata.getGradient_and_hessian());
         if(qffdata.isGenhs()) {
            System.out.println("     - genhs        = " + qffdata.isGenhs());
         }
         System.out.println();

      }
      
      if(gridArray.size() > 0) {
         System.out.printf("  o Options for Grid \n\n");
      }
      for(int n=0; n<gridArray.size(); n++) {
         InputDataGrid griddata = gridArray.get(n);
         System.out.println("     - QCID         = " + griddata.getQcID());

         if(griddata.isFullMC()) {
            System.out.println("     - FullMC       = " + griddata.isFullMC());
            
         }else if (griddata.getMC1() != null || 
                   griddata.getMC2() != null || 
                   griddata.getMC3() != null) {
            
            String[] mr1 = griddata.getMC1();
            if(mr1 != null) {
               System.out.print("     - 1-mode coupling = ");
               for(int i=0; i<mr1.length-1; i++){
                  System.out.print(mr1[i]+", ");
               }
               System.out.print(mr1[mr1.length-1]);
               System.out.println();
               
            }

            String[] mr2 = griddata.getMC2(); 
            if(mr2 != null) {
               System.out.print("     - 2-mode coupling = ");
               int i=0;
               while(i<mr2.length-1){
                  System.out.print("("+mr2[i]+"), ");
                  i++;
               }
               System.out.print("("+mr2[i]+")");
               System.out.println();
               
            }

            String[] mr3 = griddata.getMC3();
            if(mr3 != null) {
               System.out.print("     - 3-mode coupling = ");
               int i=0;
               while(i<mr3.length-1){
                  System.out.print("("+mr3[i]+"), ");
                  i++;
               }
               System.out.print("("+mr3[i]+")");
               System.out.println();
               
            }
            
         }else if (griddata.getThresh_MCS() > 0.0d) {
            System.out.println("     - mopfile      = " + griddata.getMopfile());
            System.out.println("     - MCSthresh    = "+griddata.getThresh_MCS());
            
         }
         System.out.println();


      }
      
      return makePESData;

   }
   
   private int[][] setupActiveMode(String amode, Molecule molecule){
      
      int nd = molecule.getNumOfVibrationalData();
      int[][] activeModes = new int[nd][];
      if(amode == null){
         
         int ni=0;
         for(int n=0; n<nd; n++){
            int Nfree = molecule.getVibrationalData(n).Nfree;
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
            int nf=molecule.getVibrationalData(n).Nfree;
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
         
      }

      return activeModes;
      
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
      System.out.println("Format error in "+xmlFile+".");
      System.out.println();
      System.out.print("Error:  ");
      System.out.println(message);
      System.out.println();
      Utilities.terminate();      
   }
   

}
