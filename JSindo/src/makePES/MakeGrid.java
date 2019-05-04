package makePES;

import java.util.*;
import java.io.*;

import sys.*;
import molecule.*;
import jobqueue.QueueMngr;

/**
 * Generate grid potential in normal coordinates
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class MakeGrid {

   private InputDataPES inputData;
   private InputDataGrid gridData;
   private InputDataQC  qcData;
   private boolean isGeneric;
   private GridFileName gfilename;
   private int[][] activeModes;
   private int nGrid;
   private ArrayList<Integer> mc1 = null;
   private ArrayList<int[]> mc2 = null;
   private ArrayList<int[]> mc3 = null;
   private double[][] grid;
   private GrdXYZ grdXYZ;
   private QueueMngr queue;
   private boolean[] done1MR;
   private boolean[] done2MR;
   private boolean[] done3MR;
   private HashMap<String, double[][]> eneData;
   private HashMap<String, double[][]> dipoleData;
   private String gridDataFile;
   
   public MakeGrid(InputDataPES inputData, InputDataGrid gridData, InputDataQC qcData){
      
      System.out.println("Setup MakeGrid module");
      System.out.println();
      
      this.inputData = inputData;
      this.gridData  = gridData;
      this.qcData    = qcData;
      this.activeModes = inputData.getActiveModes();
      
      if(qcData.getType().equalsIgnoreCase(InputDataQC.GENERIC)) {
         this.isGeneric = true;
      }else {
         this.isGeneric = false;
      }

      nGrid = gridData.getnGrid();
      mc1 = new ArrayList<Integer>();
      mc2 = new ArrayList<int[]>();
      mc3 = new ArrayList<int[]>();

      if(gridData.getThresh_MCS() > 0.0f) {
         MCStrength mcs = this.setupMCS();
         this.setupMC2(mcs);
         
      } else {
         if(gridData.isFullMC()) {
            this.setupFullMC();
         }else{
            this.setupMC();            
         }
         
      }

      System.out.printf ("  o ngrid = %5d \n",nGrid);
      System.out.println("  o 1MR Grid: ");
      System.out.print("      ");
      int ret = 0;
      for(int i=0; i<mc1.size(); i++){
         int ii = mc1.get(i);
         
         System.out.print((ii+1)+" ");
         ret++;
         if(ret==10){
            System.out.println();
            System.out.print("      ");
            ret = 0;
         }

      }
      System.out.println();
      
      if(mc2.size() != 0){
         
         System.out.println("  o 2MR Grid: ");
         System.out.print("      ");
         ret = 0;
         for(int i=0; i<mc2.size(); i++){
            int[] aa = mc2.get(i);
            System.out.print("("+(aa[0]+1)+","+(aa[1]+1)+") ");
            ret++;
            if(ret==10){
               System.out.println();
               System.out.print("      ");
               ret = 0;
            }
            
         }
         System.out.println();
         
      }
      
      if(mc3.size() != 0){
         
         System.out.println("  o 3MR Grid: ");
         System.out.print("      ");
         ret = 0;
         for(int i=0; i<mc3.size(); i++){
            int[] aa = mc3.get(i);
            System.out.print("("+(aa[0]+1)+","+(aa[1]+1)+","+(aa[2]+1)+") ");
            ret++;
            if(ret==10){
               System.out.println();
               System.out.print("      ");
               ret = 0;
            }
            
         }
         System.out.println();
      }

      gfilename = new GridFileName();
      if(! isGeneric) {
         String runFolder = InputDataPES.MINFO_FOLDER;
         gfilename.setMinfoDirectoryName(runFolder);
         
         File minfoDir = new File(runFolder);
         if(! minfoDir.exists()){
            minfoDir.mkdir();
         }

      }else{
         gfilename.setMinfoDirectoryName("");
         gridDataFile = qcData.getXyzBasename()+".dat";
         
         File f = new File(gridDataFile);
         if(f.exists()) {
            qcData.setDryrun(false);
         }
      }

   }

   private MCStrength setupMCS(){

      QFFData qff = new QFFData();
      String mopFilename = gridData.getMopfile();

      if(mopFilename != null){
         File mopFile = new File(mopFilename);
         System.out.print("  o Setup MCS: Read QFF Data via "+mopFilename+" ... ");
         if(mopFile.exists()){
            qff.readmop(mopFile);
            
         }else {
            System.out.println("   "+mopFilename+" is not found! Exit with error.");
            Utilities.terminate();
            
         }
         
      }else{
         System.out.print("  o Setup MCS: Read QFF via 001.hs ... ");
         try{
            qff.readhs();
         }catch(FileNotFoundException e){
            System.out.println("   001.hs is not found! Exit with error.");
            Utilities.terminate();
         }
         
      }
      System.out.println(" [OK]");

      MCStrength mcs = new MCStrength();
      mcs.appendQFF(qff);

      return mcs;
      
   }
   
   private void setupFullMC(){
      int nd = activeModes.length;
      for(int n=0; n<nd; n++){
         if(activeModes[n] == null) continue;
         
         int Nfree = activeModes[n].length;
         for(int i=0; i<Nfree; i++){
            mc1.add(activeModes[n][i]);
         }
         
         int MR = inputData.getMR();
         if(MR>1){
            for(int i=0; i<Nfree; i++){
               for(int j=0; j<i; j++){
                  int[] i2 = new int[2];
                  i2[0] = activeModes[n][j];
                  i2[1] = activeModes[n][i];
                  mc2.add(i2);
               }
            }
         }
         
         if(MR>2){
            for(int i=0; i<Nfree; i++){
               for(int j=0; j<i; j++){
                  for(int k=0; k<j; k++){
                     int[] i3 = new int[3];
                     i3[0] = activeModes[n][k];
                     i3[1] = activeModes[n][j];
                     i3[2] = activeModes[n][i];
                     mc3.add(i3);
                  }
               }
            }
         }
         
      }
   }
   
   private void setupMC(){

      // setup mc1
      String[] temp = gridData.getMC1();
      if(temp != null){
         for(int i=0; i<temp.length; i++){
            
            if(temp[i].indexOf("-")==-1){
               mc1.add(Integer.parseInt(temp[i])-1);
            }else{
               String[] ab = temp[i].split("-");
               int ma = Integer.parseInt(ab[0])-1;
               int mb = Integer.parseInt(ab[1])-1;
               for(int m1=ma; m1<=mb; m1++){
                  mc1.add(m1);
               }
            }
         }
      }

      temp = gridData.getMC2();
      if(temp != null){
         for(int i=0; i<temp.length; i++){
            String[] s1 = temp[i].split(",");
            
            int[] ma = new int[2];
            int[] mb = new int[2];
            for(int j=0; j<2; j++){
               if(s1[j].indexOf("-")==-1){
                  ma[j] = Integer.parseInt(s1[j])-1;
                  mb[j] = ma[j];
               }else{
                  String[] ab = s1[j].split("-");
                  ma[j] = Integer.parseInt(ab[0])-1;
                  mb[j] = Integer.parseInt(ab[1])-1;
               }
            }
            for(int m1=ma[0]; m1<=mb[0]; m1++){
               if(mc1.indexOf(m1) == -1) mc1.add(m1);
               for(int m2=ma[1]; m2<=mb[1]; m2++){
                  if(m2==m1) continue;
                  if(mc1.indexOf(m2) == -1) mc1.add(m2);
                  
                  int[] i2 = new int[2];
                  i2[0] = m1;
                  i2[1] = m2;
                  Arrays.sort(i2);
                  mc2.add(i2);
                 
               }
            }
         }
      }

      temp = gridData.getMC3();
      if(temp != null){
         for(int i=0; i<temp.length; i++){
            String[] s1 = temp[i].split(",");
            
            int[] ma = new int[3];
            int[] mb = new int[3];
            for(int j=0; j<3; j++){
               if(s1[j].indexOf("-")==-1){
                  ma[j] = Integer.parseInt(s1[j])-1;
                  mb[j] = ma[j];
               }else{
                  String[] ab = s1[j].split("-");
                  ma[j] = Integer.parseInt(ab[0])-1;
                  mb[j] = Integer.parseInt(ab[1])-1;
               }
            }
            
            for(int m1=ma[0]; m1<=mb[0]; m1++){
               if(mc1.indexOf(m1) == -1) mc1.add(m1);
               
               for(int m2=ma[1]; m2<=mb[1]; m2++){
                  if(m1==m2) continue;
                  if(mc1.indexOf(m2) == -1) mc1.add(m2);
                  
                  for(int m3=ma[2]; m3<=mb[2]; m3++){
                     if(m1==m3 || m2==m3) continue;
                     if(mc1.indexOf(m3) == -1) mc1.add(m3);

                     int[] i3 = new int[3];
                     i3[0] = m1;
                     i3[1] = m2;
                     i3[2] = m3;
                     Arrays.sort(i3);
                     mc3.add(i3);
                     
                     int[] i2a = new int[2];
                     i2a[0] = i3[0];
                     i2a[1] = i3[1];

                     int[] i2b = new int[2];
                     i2b[0] = i3[0];
                     i2b[1] = i3[2];
                     
                     int[] i2c = new int[2];
                     i2c[0] = i3[1];
                     i2c[1] = i3[2];
                     
                     this.addElement(mc2, i2a);
                     this.addElement(mc2, i2b);
                     this.addElement(mc2, i2c);
                  }
               }
            }
            
         }
      }
      
   }
   
   private void setupMC2(MCStrength mcs){
   
      double mcsth = gridData.getThresh_MCS();
      
      int nd = activeModes.length;
      for(int n=0; n<nd; n++){
         if(activeModes[n] == null) continue;
         
         int Nfree = activeModes[n].length;
         
         for(int i=0; i<Nfree; i++){
            int mi = activeModes[n][i];
            for(int j=0; j<i; j++){
               int mj = activeModes[n][j];
               
               int[] i2 = new int[2];
               i2[0] = mj;
               i2[1] = mi;
               
               if(mcs.get2mrMCS(i2[1], i2[0])>mcsth){
                  this.addElement(mc2, i2);
                  if(mc1.indexOf(mi) == -1) mc1.add(mi);
                  if(mc1.indexOf(mj) == -1) mc1.add(mj);
      
               }
            }
         }
         
         int MR = inputData.getMR();
         if(MR>2){
            for(int i=0; i<Nfree; i++){
               int mi = activeModes[n][i];

               for(int j=0; j<i; j++){
                  int mj = activeModes[n][j];
                  
                  for(int k=0; k<j; k++){
                     int mk = activeModes[n][k];
                     
                     int[] i3 = new int[3];
                     i3[0] = mk;
                     i3[1] = mj;
                     i3[2] = mi;
                     
                     if(mcs.get3mrMCS(i3[2], i3[1], i3[0])>mcsth){

                        this.addElement(mc3, i3);
                        
                        int[] i2a = new int[2];
                        i2a[0] = i3[0];
                        i2a[1] = i3[1];
      
                        int[] i2b = new int[2];
                        i2b[0] = i3[0];
                        i2b[1] = i3[2];
                        
                        int[] i2c = new int[2];
                        i2c[0] = i3[1];
                        i2c[1] = i3[2];
                        
                        this.addElement(mc2, i2a);
                        this.addElement(mc2, i2b);
                        this.addElement(mc2, i2c);
      
                        if(mc1.indexOf(mi) == -1) mc1.add(mi);
                        if(mc1.indexOf(mj) == -1) mc1.add(mj);
                        if(mc1.indexOf(mk) == -1) mc1.add(mk);
                        
                     }
                  }
               }
            }   
         }
      }   
   }

   private void addElement(ArrayList<int[]> mc, int[] ii){
      boolean match;
      match = false;
      for(int j=0; j<mc.size(); j++){
         if(Arrays.equals(mc.get(j), ii)){
            match = true;
            break;
         }
      }
      if(! match){
         mc.add(ii);
      }

   }

   /**
    * Run MakeGrid routine
    */
   public void runMakeGrid(){
      setupGrid();

      System.out.println();
      System.out.println("Enter GridPES generation:");
      System.out.println();
      System.out.println("Execute electronic structure calculations.");
      System.out.println();

      if(! isGeneric) {
         queue = QueueMngr.getInstance();
         queue.start();         
      }else{
         grdXYZ = new GrdXYZ(qcData.getXyzBasename());
      }

      calcEQ();

      if(mc1.size() > 0){         
         calc1MRGrid();
      }
      if(mc2.size() > 0){
         calc2MRGrid();
      }      
      if(mc3.size() > 0){
         calc3MRGrid();
      }
      
      if(! isGeneric) {
         queue.shutdown();            
      }else{
         grdXYZ.close();            
      }
      
      System.out.println("End of electronic structure calculations.");
      System.out.println();

      if(qcData.isDryrun()){
         System.out.println("DryRun is done!");
         System.out.println();
         
      }else{
         System.out.println("Generating pot files.");
         System.out.println();
         
         if(isGeneric){
            readGridData();
         }

         genEQ();
         
         GenPotfile genpot = new GenPotfile(qcData.getTitle(), gfilename);
         
         if(! isGeneric) {
            genpot.setDataMode("minfo");
         }else{
            genpot.setDataMode("datfile");
         }
         
         GenDipolefile gendipole = null;
         if(inputData.isDipole()) {
            gendipole = new GenDipolefile(qcData.getTitle(), gfilename);

            if(! isGeneric) {
               gendipole.setDataMode("minfo");
            }else{
               gendipole.setDataMode("datfile");
            }            
         }
         
         if(mc1.size() > 0){            
            gen1MRGrid(genpot, gendipole);
         }
         if(mc2.size() > 0){
            gen2MRGrid(genpot, gendipole);
         }         
         if(mc3.size() > 0){
            gen3MRGrid(genpot, gendipole);
         }
         System.out.println();
         
      }
      
      System.out.println("End of GridPES generation:");
      System.out.println();
      
   }
   
   private void setupGrid(){
      int nMC1 = mc1.size();
      int nGrid = gridData.getnGrid();
      double[] omegaV = inputData.getMolecule().getVibrationalData().getOmegaV();
      
      grid = new double[nMC1][nGrid];
      
      for(int i=0; i<nMC1; i++){
         HOdvr hodvr = new HOdvr(nGrid,omegaV[mc1.get(i)]);
         grid[i]=hodvr.getGridPoints();
      }
   }
   
   private void calcEQ(){
      File q0pot = new File(gfilename.getEQFileName()+".pot");
      if(! q0pot.exists()){
         this.processGrid(null, null, gfilename.getRunNameEQ());
      }
   }
   
   private void calc1MRGrid(){
            
      done1MR = new boolean[mc1.size()];
      for(int p=0; p<done1MR.length; p++){
         done1MR[p] = false;
      }
      
      for(int p=0; p<mc1.size(); p++){

         // mode combination and the grid points
         int[] mm = {mc1.get(p)};
         double[] xg = grid[p];

         // Setup GridData if old pot file exists
         File potfile = new File(gfilename.getGridFileName(mm)+".pot");

         GridData potdata = null;
         if(potfile.exists()){
            potdata = new GridData(1,potfile);
            potdata.readData();
            double[] xgfile = potdata.getGrid(0);
            if(xgfile.length == xg.length){
               if(Utilities.compareVector(xg, xgfile, 1.0e-8)) {
                  done1MR[p] = true;
                  continue;
               }
            }
            
         }

         // Submit job if out of the grid range
         for(int i=0; i<nGrid; i++){
            if(Math.abs(xg[i]) < 1.e-10) continue;
            
            double[] dq = {xg[i]};
            if(potdata != null && potdata.isInGrid(dq)){
               continue;
            }
            this.processGrid(mm, dq, gfilename.getRunNameGrid(mm, nGrid, i));
         }

      }
      
   }

   private void calc2MRGrid(){

      done2MR = new boolean[mc2.size()];
      for(int p=0; p<done2MR.length; p++){
         done2MR[p] = false;
      }

      int nDim = 2;      
      for(int p=0; p<mc2.size(); p++){
         
         int[] mode = mc2.get(p);
         int p0 = mc1.indexOf(mode[0]);
         int p1 = mc1.indexOf(mode[1]);
         double[][] xg = new double[nDim][];
         xg[0] = grid[p0];
         xg[1] = grid[p1];
         
         // Setup GridData if old file exists
         File potfile = new File(gfilename.getGridFileName(mode)+".pot");
         GridData potdata = null;
         if(potfile.exists()){
            potdata = new GridData(nDim,potfile);
            potdata.readData();
            double[] xgfile = potdata.getGrid(0);
            double[] ygfile = potdata.getGrid(1);
            if(xgfile.length == xg[0].length && ygfile.length == xg[1].length){
               if(Utilities.compareVector(xg[0], xgfile, 1.0e-8) &&
                  Utilities.compareVector(xg[1], ygfile, 1.0e-8)) {
                  done2MR[p] = true;
                  continue;
               }
            }
            
         }

         // Submit job if out of the grid range
         int kk=0;
         for(int iy=0; iy<nGrid; iy++){
            double xgy = xg[1][iy];
            if(Math.abs(xgy) < 1.e-10) {
               kk=kk+nGrid;
               continue;
            }
            for(int ix=0; ix<nGrid; ix++){
               double xgx = xg[0][ix];
               if(Math.abs(xgx) < 1.e-10) {
                  kk++;
                  continue;
               }
               
               double[] dq = {xgx,xgy};
               if(potdata != null && potdata.isInGrid(dq)){
                  kk++;
                  continue;
               }
               
               this.processGrid(mode, dq, gfilename.getRunNameGrid(mode, nGrid, kk));
               kk++;

            }
            
         }

      }

   }
   
   private void calc3MRGrid(){

      done3MR = new boolean[mc3.size()];
      for(int p=0; p<done3MR.length; p++){
         done3MR[p] = false;
      }

      int nDim = 3;
      for(int p=0; p<mc3.size(); p++){
         
         int[] mode = mc3.get(p);
         int p0 = mc1.indexOf(mode[0]);
         int p1 = mc1.indexOf(mode[1]);
         int p2 = mc1.indexOf(mode[2]);
         double[][] xg = new double[nDim][];
         xg[0] = grid[p0];
         xg[1] = grid[p1];
         xg[2] = grid[p2];

         // Setup GridData if old file exists
         File potfile = new File(gfilename.getGridFileName(mode)+".pot");
         GridData potdata = null;
         if(potfile.exists()){
            potdata = new GridData(nDim,potfile);
            potdata.readData();
            double[][] xgfile = potdata.getGrid();
            if(xgfile[0].length == xg[0].length && xgfile[1].length == xg[1].length 
                  && xgfile[2].length == xg[2].length){
               if(Utilities.compareVector(xgfile[0], xg[0], 1.0e-8) && 
                     Utilities.compareVector(xgfile[1], xg[1], 1.0e-8) &&
                     Utilities.compareVector(xgfile[2], xg[2], 1.0e-8)) {
                  done3MR[p] = true;
                  continue;
               }
            }
         }

         // Submit job if out of the grid range
         int kk=0;
         for(int iz=0; iz<nGrid; iz++){
            double xgz = xg[2][iz];
            if(Math.abs(xgz) < 1.e-10) {
               kk=kk+nGrid*nGrid;
               continue;
            }
           for(int iy=0; iy<nGrid; iy++){
               double xgy = xg[1][iy];
               if(Math.abs(xgy) < 1.e-10) {
                  kk=kk+nGrid;
                  continue;
               }
               for(int ix=0; ix<nGrid; ix++){
                  double xgx = xg[0][ix];
                  if(Math.abs(xgx) < 1.e-10) {
                     kk++;
                     continue;
                  }
                  
                  double[] dq = {xgx,xgy,xgz};
                  if(potdata != null && potdata.isInGrid(dq)){
                     kk++;
                     continue;
                  }
                  
                  this.processGrid(mode, dq, gfilename.getRunNameGrid(mode, nGrid, kk));
                  kk++;

               }
            }
         }

      }

   }

   private void readGridData(){
            
      eneData = new HashMap<String, double[][]>();
      if(inputData.isDipole()){
         dipoleData = new HashMap<String, double[][]>();         
      }      
      
      {
         // q0
         String key = gfilename.getEQFileName();
         double[][] ene = new double[1][1];
         eneData.put(key, ene);
         
         if(inputData.isDipole()){
            double[][] dipole = new double[3][1];
            dipoleData.put(key, dipole);
         }
      }
      
      for(int p=0; p<mc1.size(); p++){
         if(done1MR[p]) continue;

         int[] mm = {mc1.get(p)};
         String key = gfilename.getGridFileName(mm);
         
         double[][] ene = new double[1][nGrid];
         for(int i=0; i<ene.length; i++){
            for(int j=0; j<nGrid; j++){
               ene[i][j] = Double.NaN;               
            }
         }
         
         eneData.put(key, ene);
         
         if(inputData.isDipole()){
            double[][] dipole = new double[3][nGrid];
            for(int i=0; i<dipole.length; i++){
               for(int j=0; j<nGrid; j++){
                  dipole[i][j] = Double.NaN;
               }
            }
            dipoleData.put(key, dipole);
         }
         
      }

      for(int p=0; p<mc2.size(); p++){
         if(done2MR[p]) continue;
         
         double[][] ene = new double[1][nGrid*nGrid];
         for(int i=0; i<ene.length; i++){
            for(int j=0; j<nGrid*nGrid; j++)
            ene[i][j] = Double.NaN;
         }
         String key = gfilename.getGridFileName(mc2.get(p));
         
         eneData.put(key, ene);
         
         if(inputData.isDipole()){
            double[][] dipole = new double[3][nGrid*nGrid];
            for(int i=0; i<dipole.length; i++){
               for(int j=0; j<nGrid*nGrid; j++){
                  dipole[i][j] = Double.NaN;
               }
            }
            dipoleData.put(key, dipole);
         }
      }
      
      for(int p=0; p<mc3.size(); p++){
         if(done3MR[p]) continue;
         
         double[][] ene = new double[1][nGrid*nGrid*nGrid];
         for(int i=0; i<ene.length; i++){
            for(int j=0; j<nGrid*nGrid*nGrid; j++){               
               ene[i][j] = Double.NaN;
            }
         }
         String key = gfilename.getGridFileName(mc3.get(p));
         
         eneData.put(key, ene);

         if(inputData.isDipole()){
            double[][] dipole = new double[3][nGrid*nGrid*nGrid];
            for(int i=0; i<dipole.length; i++){
               for(int j=0; j<nGrid*nGrid*nGrid; j++){
                  dipole[i][j] = Double.NaN;
               }
            }
            dipoleData.put(key, dipole);
         }
         
      }

      //String gridDataFile = qcData.getXyzBasename()+".dat";
      System.out.println("   Reading the data from "+gridDataFile+".");
      System.out.println();

      try{
         BufferedReader br = new BufferedReader(new FileReader(gridDataFile));
         String line = null;         
         while((line = br.readLine()) != null){
            String[] data = line.split(",");
            String[] name = data[0].split("-");
            
            String mc = name[1];
            int index = 0;
            if(name.length > 3){
               index = Integer.parseInt(name[3].trim());               
            }

            if(! eneData.containsKey(mc)) continue;
            
            double[][] ene = eneData.get(mc);               
            ene[0][index] = Double.parseDouble(data[1]);            
            
            if(inputData.isDipole()){
               double[][] dipole = dipoleData.get(mc);
               for(int i=0; i<3; i++){
                  dipole[i][index] = Double.parseDouble(data[2+i]);                  
               }
            }
            
         }
         br.close();
      }catch (FileNotFoundException e){
         System.out.println("   "+gridDataFile+" is not found! Exit with error.");
         Utilities.terminate();
         
      }catch (IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
            
   }
   
   private void genEQ(){
      
      Molecule molecule = null;
      
      // Energy
      File datafile = new File(gfilename.getEQFileName()+".pot");
      if(! datafile.exists()){
         double ene = 0.0;
         
         if(! isGeneric) {
            String minfoName = gfilename.getRunNameEQ()+".minfo";
            MInfoIO minfo = new MInfoIO();
            try {
               molecule = minfo.loadMOL(minfoName);
            } catch (FileNotFoundException e){
               System.out.println("Warning: "+minfoName+" is not found.");
               System.out.println("Continues by suppling the energy by inter/extrapolation.");
               molecule = null;
               
            } catch (IOException e) {
               System.out.println("Error while reading "+minfoName+".");
               System.out.println(e.getMessage());
               Utilities.terminate();
            }
            if(molecule == null){
               System.out.println("Fatal error while reading "+minfoName);
               System.out.println("Terminated with error");
               Utilities.terminate();
            }
            ene = molecule.getElectronicData().getEnergy();
            
         }else{
            String runName = gfilename.getEQFileName();            
            ene = eneData.get(runName)[0][0];
         }
         
         try{
            PrintStream ps = new PrintStream(datafile);
            ps.println("# Number of data");
            ps.printf("%6d \n", 1);
            ps.println("# Data at the reference geometry");
            ps.printf("%30.15f", ene);
            ps.println();
            ps.close();
            
         }catch(IOException e){
            e.printStackTrace();
         }
                  
      }

      if(! inputData.isDipole()) return;

      // Dipole
      datafile = new File(gfilename.getEQFileName()+".dipole");
      if(! datafile.exists()){

         double[] dipole = null;
         
         if(! isGeneric) {
            if(molecule == null){
               String minfoName = gfilename.getRunNameEQ()+".minfo";
               MInfoIO minfo = new MInfoIO();
               try {
                  molecule = minfo.loadMOL(minfoName);
               } catch (FileNotFoundException e){
                  System.out.println("Warning: "+minfoName+" is not found.");
                  System.out.println("Continues by suppling the energy by inter/extrapolation.");
                  molecule = null;
                  
               } catch (IOException e) {
                  System.out.println("Error while reading "+minfoName+".");
                  System.out.println(e.getMessage());
                  Utilities.terminate();
               }
               if(molecule == null){
                  System.out.println("Fatal error while reading "+minfoName);
                  System.out.println("Terminated with error");
                  Utilities.terminate();
               }
               
            }
            dipole = molecule.getElectronicData().getDipole();
         }else{
            String runName = gfilename.getEQFileName();
            double[][] dd = dipoleData.get(runName);
            dipole = new double[3];
            for(int i=0; i<3; i++){
               dipole[i] = dd[i][0];
            }

         }
         
         try{
            PrintStream ps = new PrintStream(datafile);
            ps.println("# Number of data");
            ps.printf("%6d \n", dipole.length);
            ps.println("# Data at the reference geometry");
            for(int i=0; i < dipole.length; i++){
               ps.printf("%20.10e", dipole[i]);
            }
            ps.println();
            ps.close();
            
         }catch(IOException e){
            e.printStackTrace();
         }
         
      }
      
   }
      
   private void gen1MRGrid(GenPotfile genpot, GenDipolefile gendipole){
      
      for(int p=0; p<mc1.size(); p++){
         if(done1MR[p]) continue;
         
         int[] mm = {mc1.get(p)};

         double[][] xgrid = new double[1][];
         xgrid[0] = grid[p];
         
         if(! isGeneric) {
            genpot.genFile(mm, xgrid);
            if(inputData.isDipole()) gendipole.genFile(mm,xgrid);
         }else{
            genpot.setDataVales(eneData.get(gfilename.getGridFileName(mm)));
            genpot.genFile(mm, xgrid);
            if(inputData.isDipole()){
               gendipole.setDataVales(dipoleData.get(gfilename.getGridFileName(mm)));
               gendipole.genFile(mm, xgrid);
            }
         }

      }

   }

   private void gen2MRGrid(GenPotfile genpot, GenDipolefile gendipole){

      /*
      ArrayList<GenGridfile> genfiles = new ArrayList<GenGridfile>(2);
      GenPotfile genpot = new GenPotfile(inputData.getTitle(titleID), gfilename);
      genpot.setGridFileName(gfilename);
      genfiles.add(genpot);
      if(inputData.isDipole()) {
         GenDipolefile gendipole = new GenDipolefile(inputData.getTitle(titleID), gfilename);
         gendipole.setGridFileName(gfilename);
         genfiles.add(gendipole);
      }
      */

      int nDim = 2;      
      for(int p=0; p<mc2.size(); p++){
         
         if(done2MR[p]) continue;
         
         int[] mode = mc2.get(p);
         int p0 = mc1.indexOf(mode[0]);
         int p1 = mc1.indexOf(mode[1]);
         double[][] xg = new double[nDim][];
         xg[0] = grid[p0];
         xg[1] = grid[p1];

         /*
         for(int tt=0; tt < genfiles.size(); tt++){
            genfiles.get(tt).genFile(mode, xg);
         }
         */

         if(! isGeneric) {
            genpot.genFile(mode, xg);
            if(inputData.isDipole()) gendipole.genFile(mode, xg);
         }else{
            genpot.setDataVales(eneData.get(gfilename.getGridFileName(mode)));
            genpot.genFile(mode, xg);
            if(inputData.isDipole()){
               gendipole.setDataVales(dipoleData.get(gfilename.getGridFileName(mode)));
               gendipole.genFile(mode, xg);
            }
         }
         
      }

   }
   
   private void gen3MRGrid(GenPotfile genpot, GenDipolefile gendipole){
      
      /*
      ArrayList<GenGridfile> genfiles = new ArrayList<GenGridfile>(2);
      GenPotfile genpot = new GenPotfile(inputData.getTitle(titleID), gfilename);
      genpot.setGridFileName(gfilename);
      genfiles.add(genpot);
      if(inputData.isDipole()) {
         GenDipolefile gendipole = new GenDipolefile(inputData.getTitle(titleID), gfilename);
         gendipole.setGridFileName(gfilename);
         genfiles.add(gendipole);
      }
       */
      
      int nDim = 3;
      for(int p=0; p<mc3.size(); p++){
         
         if(done3MR[p]) continue;
         
         int[] mode = mc3.get(p);
         int p0 = mc1.indexOf(mode[0]);
         int p1 = mc1.indexOf(mode[1]);
         int p2 = mc1.indexOf(mode[2]);
         double[][] xg = new double[nDim][];
         xg[0] = grid[p0];
         xg[1] = grid[p1];
         xg[2] = grid[p2];

         /*
         for(int tt=0; tt < genfiles.size(); tt++){
            genfiles.get(tt).genFile(mode, xg);
         }
         */
         if(! isGeneric) {
            genpot.genFile(mode, xg);
            if(inputData.isDipole()) gendipole.genFile(mode, xg);
         }else{
            genpot.setDataVales(eneData.get(gfilename.getGridFileName(mode)));
            genpot.genFile(mode, xg);
            if(inputData.isDipole()){
               gendipole.setDataVales(dipoleData.get(gfilename.getGridFileName(mode)));
               gendipole.genFile(mode, xg);
            }
         }

      }
         
      System.out.println();
      System.out.println("End of 3MR-GridPES generation:");
      System.out.println();

   }

   private void processGrid(int[] mm, double[] qq, String basename){
      if(! isGeneric) {
         TaskGrid task = new TaskGrid(inputData,qcData, mm,qq,basename);
         queue.submit(task);         
      }else{
         grdXYZ.write(inputData,mm,qq,basename);
      }

   }

}
