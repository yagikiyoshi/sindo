package makePES;

import java.io.*;

import molecule.*;
import sys.*;

/**
 * Generate a file containing the property data.
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.2
 */
public abstract class GenGridfile {

   /**
    * Number of data
    */
   protected int nData;
   
   /**
    * Extension of the file
    */
   protected String ext;
   
   /**
    * Title of the data
    */
   protected String title;
   
   /**
    * Label of the   data
    */
   protected String[] label;

   private int[] mode=null;
   private double[][] xg=null;
   private double[][] value=null;
   private int nDim;
   private int nGrid;
   private double[] dd0;
   private GridData gridData;
   private GridFileName gfilename;
   private boolean data_from_minfo;
   
   protected abstract double[] getProperty(ElectronicData edata);
   
   protected void setOrigin(){
      File datafile = new File(gfilename.getEQFileName()+ext);
      try{
         BufferedReader br = new BufferedReader(new FileReader(datafile));
         br.readLine();
         int ndat = Integer.parseInt(br.readLine().trim());
         dd0 = new double[ndat];
         br.readLine();
         String[] aa = Utilities.splitWithSpaceString(br.readLine());
         for(int n=0; n<aa.length; n++){
            dd0[n] = Double.parseDouble(aa[n].trim());
         }
         br.close();
      }catch(FileNotFoundException e){
         System.out.println("Fatal error!");
         System.out.println(gfilename.getEQFileName()+ext+" is not found.");
         Utilities.terminate();
         
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
   }
   
   public void setGridFileName(GridFileName gfilename){
      this.gfilename = gfilename;
   }
   
   public void setDataMode(String dataMode){
      if(dataMode.equalsIgnoreCase("minfo")) {
         data_from_minfo = true;
      }else{
         data_from_minfo = false;
      }
   }
   
   public void setDataVales(double[][] data){
      this.value = data;
   }
   
   public void genFile(int[] mode, double[][] xg){
      this.mode = mode;
      this.xg = xg;

      nDim = xg.length;
      nGrid = xg[0].length;
      
      //this.setOrigin();
      
      String basename = gfilename.getGridFileName(mode);
      File datafile = new File(basename+ext);
      if(datafile.exists()){
         gridData = new GridData(nDim,basename+ext);
         gridData.readData();
         if(gridData.getGrid(0).length < nGrid) {
            gridData.savefile();
         }else {
            gridData = null;
            return;
         }
         gridData.setupInterpolater();
      }
      
      if(nDim == 1){
         this.genFile1();
      }else if(nDim == 2){
         this.genFile2();
      }else if(nDim == 3){
         this.genFile3();
      }

      // TODO Is there any way to check all data at once?
      for(int j=0; j<nData; j++){
         if( ! this.checkData(xg, value[j], basename)){
            System.out.println("   o "+basename+ext+"  [FAILED]");
            return;
         }
      }
      GridData newData = new GridData(nDim,basename+ext);
      newData.setGrid(xg);
      newData.setValue(value);
      newData.writeData(title, mode, label);
      
      System.out.println("   o "+basename+ext+"  [OK]");
      
      gridData = null;
   }
   
   private void genFile1(){
      
      if(data_from_minfo){
         value = new double[nData][nGrid];
         for(int i=0; i<nGrid; i++){
            if(Math.abs(xg[0][i]) < 1.e-10) {
               for(int j=0; j<nData; j++){
                  value[j][i] = 0.0;
               }
               continue;
            }
            if(gridData != null){
               double[] xx = {xg[0][i]};
               if(gridData.isInGrid(xx)){
                  double[] dd = gridData.getIntValue(xx);
                  for(int j=0; j<dd.length; j++){
                     value[j][i] = dd[j];
                  }
                  continue;
               }
            }
            
            Molecule molecule = readminfo(gfilename.getRunNameGrid(mode, nGrid, i));
            if(molecule != null){
               double[] dd = this.getProperty(molecule.getElectronicData());
               for(int j=0; j<dd.length; j++){
                  value[j][i] = dd[j] - dd0[j];
               }
            }else{
               for(int j=0; j<nData; j++){
                  value[j][i] = Double.NaN;
               }            
            }
         }
         
      }else{
         
         for(int i=0; i<nGrid; i++){
            if(Math.abs(xg[0][i]) < 1.e-10) {
               for(int j=0; j<nData; j++){
                  value[j][i] = 0.0;
               }
               continue;
            }
            if(gridData != null){
               double[] xx = {xg[0][i]};
               if(gridData.isInGrid(xx)){
                  double[] dd = gridData.getIntValue(xx);
                  for(int j=0; j<dd.length; j++){
                     value[j][i] = dd[j];
                  }
                  continue;
               }
            }
            
            if(! Double.isNaN(value[0][i])){
               for(int j=0; j<value.length; j++){
                  value[j][i] = value[j][i] - dd0[j];
               }               
            }else{
               String name="q"+(mode[0]+1)+"-"+nGrid+"-"+i;
               System.out.println("Warning: data for "+name+" is not found.");
               System.out.println("Continues by suppling the energy by inter/extrapolation.");
            }
         }
      }
      
   }
   
   private void genFile2(){
      int[] mx = {mode[0]};
      GridData xfunc = new GridData(1,gfilename.getGridFileName(mx)+ext);
      xfunc.readData();
      xfunc.setupInterpolater();
      
      int[] my = {mode[1]};
      GridData yfunc = new GridData(1,gfilename.getGridFileName(my)+ext);
      yfunc.readData();
      yfunc.setupInterpolater();

      /* debug
      System.out.println(gfilename.getGridFileName(mx)+ext);
      System.out.println(gfilename.getGridFileName(my)+ext);
      */
      
      if(data_from_minfo){
         
         value = new double[nData][nGrid*nGrid];
         
         int kk=0;
         for(int iy=0; iy<nGrid; iy++){
            double xgy = xg[1][iy];
            if(Math.abs(xgy) < 1.e-10) {
               for(int j=0; j<nData; j++){
                  for(int k=kk; k<kk+nGrid; k++){
                     value[j][k] = 0.0;
                  }                  
               }
               kk=kk+nGrid;
               continue;
            }
            
            double[] yy = {xgy};
            double[] vy = yfunc.getIntValue(yy); 

            for(int ix=0; ix<nGrid; ix++){
               double xgx = xg[0][ix];
               if(Math.abs(xgx) < 1.e-10) {
                  for(int j=0; j<nData; j++){
                     value[j][kk] = 0.0;
                  }
                  kk++;
                  continue;
               }
               
               if(gridData != null){
                  double[] xx = {xgx,xgy};
                  if(gridData.isInGrid(xx)){
                     double[] dd = gridData.getIntValue(xx);
                     for(int j=0; j<nData; j++){
                        value[j][kk] = dd[j];
                     }
                     kk++;
                     continue;
                  }
               }

               double[] xx = {xgx};
               double[] vx = xfunc.getIntValue(xx);

               /* debug
               for(int n=0; n<xx.length; n++) {
                  System.out.printf("%12.4f ", xx[n]);
               }
               for(int n=0; n<yy.length; n++) {
                  System.out.printf("%12.4f ", yy[n]);
               }
               System.out.printf(",  ");
               for(int n=0; n<vx.length; n++) {
                  System.out.printf("%12.6f ", vx[n]);
               }
               for(int n=0; n<vy.length; n++) {
                  System.out.printf("%12.6f ", vy[n]);
               }
               System.out.println();
               */
               
               Molecule molecule = readminfo(gfilename.getRunNameGrid(mode, nGrid, kk));
               if(molecule != null){
                 double[] dd = this.getProperty(molecule.getElectronicData());
                 for(int j=0; j<nData; j++){
                    value[j][kk] = dd[j] - vx[j] - vy[j] - dd0[j];
                 }
                  
               }else{
                 for(int j=0; j<nData; j++){
                    value[j][kk] = Double.NaN;
                 }
               }
               kk++;
            }
         }
         
      }else{
         
         int kk=0;
         for(int iy=0; iy<nGrid; iy++){
            double xgy = xg[1][iy];
            if(Math.abs(xgy) < 1.e-10) {
               for(int j=0; j<nData; j++){
                  for(int k=kk; k<kk+nGrid; k++){
                     value[j][k] = 0.0;
                  }                  
               }
               kk=kk+nGrid;
               continue;
            }
            
            double[] yy = {xgy};
            double[] vy = yfunc.getIntValue(yy); 
            
            for(int ix=0; ix<nGrid; ix++){
               double xgx = xg[0][ix];
               if(Math.abs(xgx) < 1.e-10) {
                  for(int j=0; j<nData; j++){
                     value[j][kk] = 0.0;
                  }
                  kk++;
                  continue;
               }
               
               if(gridData != null){
                  double[] xx = {xgx,xgy};
                  if(gridData.isInGrid(xx)){
                     double[] dd = gridData.getIntValue(xx);
                     for(int j=0; j<nData; j++){
                        value[j][kk] = dd[j];
                     }
                     kk++;
                     continue;
                  }
               }

               double[] xx = {xgx};
               double[] vx = xfunc.getIntValue(xx);

               if(! Double.isNaN(value[0][kk])){
                  for(int j=0; j<nData; j++){
                     value[j][kk] = value[j][kk] - vx[j] - vy[j] - dd0[j];
                  }                  
               }else{
                  String name="q"+(mode[1]+1)+"q"+(mode[0]+1)+"-"+nGrid+"-"+kk;
                  System.out.println("Warning: data for "+name+" is not found.");
                  System.out.println("Continues by suppling the energy by inter/extrapolation.");
               }
               kk++;
               
            }
         }
         
      }
   }

   private void genFile3(){
      int[] mx = {mode[0]};
      GridData xfunc = new GridData(1,gfilename.getGridFileName(mx)+ext);
      xfunc.readData();
      xfunc.setupInterpolater();
      
      int[] my = {mode[1]};
      GridData yfunc = new GridData(1,gfilename.getGridFileName(my)+ext);
      yfunc.readData();
      yfunc.setupInterpolater();
      
      int[] mz = {mode[2]};
      GridData zfunc = new GridData(1,gfilename.getGridFileName(mz)+ext);
      zfunc.readData();
      zfunc.setupInterpolater();
      
      int[] mxy = {mode[0],mode[1]};
      GridData xyfunc = new GridData(2,gfilename.getGridFileName(mxy)+ext);
      xyfunc.readData();
      xyfunc.setupInterpolater();
      
      int[] mxz = {mode[0],mode[2]};
      GridData xzfunc = new GridData(2,gfilename.getGridFileName(mxz)+ext);
      xzfunc.readData();
      xzfunc.setupInterpolater();
      
      int[] myz = {mode[1],mode[2]};
      GridData yzfunc = new GridData(2,gfilename.getGridFileName(myz)+ext);
      yzfunc.readData();
      yzfunc.setupInterpolater();
      
      if(data_from_minfo){
         value = new double[nData][nGrid*nGrid*nGrid];
         
         int kk=0;
         for(int iz=0; iz<nGrid; iz++){
            double xgz = xg[2][iz];
            if(Math.abs(xgz) < 1.e-10) {
               for(int j=0; j<nData; j++){
                  for(int k=kk; k<kk+nGrid*nGrid; k++){
                     value[j][k] = 0.0;
                  }                  
               }
               kk=kk+nGrid*nGrid;
               continue;
            }
            double[] zz = {xgz};
            double[] vz = zfunc.getIntValue(zz);

            for(int iy=0; iy<nGrid; iy++){
               double xgy = xg[1][iy];
               if(Math.abs(xgy) < 1.e-10) {
                  for(int j=0; j<nData; j++){
                     for(int k=kk; k<kk+nGrid; k++){                        
                        value[j][k] = 0.0;
                     }
                  }
                  kk=kk+nGrid;
                  continue;
               }
               double[] yy = {xgy};
               double[] vy = yfunc.getIntValue(yy);
               
               double[] yz = {xgy,xgz};
               double[] vyz = yzfunc.getIntValue(yz);

               for(int ix=0; ix<nGrid; ix++){
                  double xgx = xg[0][ix];
                  if(Math.abs(xgx) < 1.e-10) {
                     for(int j=0; j<nData; j++){
                        value[j][kk] = 0.0;
                     }
                     kk++;
                     continue;
                  }

                  double[] dq = {xgx,xgy,xgz};
                  if(gridData != null && gridData.isInGrid(dq)){
                     double[] dd = gridData.getIntValue(dq);
                     for(int j=0; j<nData; j++){
                        value[j][kk] = dd[j];
                     }
                     kk++;
                     continue;
                  }
                  
                  double[] xx = {xgx};
                  double[] vx = xfunc.getIntValue(xx);
                  
                  double[] xy = {xgx,xgy};
                  double[] vxy = xyfunc.getIntValue(xy);
                  
                  double[] xz = {xgx,xgz};
                  double[] vxz = xzfunc.getIntValue(xz);

                  Molecule molecule = readminfo(gfilename.getRunNameGrid(mode, nGrid, kk));
                  if(molecule != null){
                     double[] dd = this.getProperty(molecule.getElectronicData());
                     for(int j=0; j<nData; j++){
                        value[j][kk] = dd[j] - vxy[j] - vxz[j] - vyz[j] 
                              - vx[j] -vy[j] - vz[j] - dd0[j];
                     }
                     
                  }else{
                     for(int j=0; j<nData; j++){
                        value[j][kk] = Double.NaN;
                     }
                  }

                  kk++;

               }
            }
         }
         
      }else{
         
         int kk=0;
         for(int iz=0; iz<nGrid; iz++){
            double xgz = xg[2][iz];
            if(Math.abs(xgz) < 1.e-10) {
               for(int j=0; j<nData; j++){
                  for(int k=kk; k<kk+nGrid*nGrid; k++){
                     value[j][k] = 0.0;
                  }                  
               }
               kk=kk+nGrid*nGrid;
               continue;
            }
            double[] zz = {xgz};
            double[] vz = zfunc.getIntValue(zz);

            for(int iy=0; iy<nGrid; iy++){
               double xgy = xg[1][iy];
               if(Math.abs(xgy) < 1.e-10) {
                  for(int j=0; j<nData; j++){
                     for(int k=kk; k<kk+nGrid; k++){
                        value[j][k] = 0.0;
                     }                     
                  }
                  kk=kk+nGrid;
                  continue;
               }
               double[] yy = {xgy};
               double[] vy = yfunc.getIntValue(yy);
               
               double[] yz = {xgy,xgz};
               double[] vyz = yzfunc.getIntValue(yz);

               for(int ix=0; ix<nGrid; ix++){
                  double xgx = xg[0][ix];
                  if(Math.abs(xgx) < 1.e-10) {
                     for(int j=0; j<nData; j++){
                        value[j][kk] = 0.0;
                     }
                     kk++;
                     continue;
                  }

                  double[] dq = {xgx,xgy,xgz};
                  if(gridData != null && gridData.isInGrid(dq)){
                     double[] dd = gridData.getIntValue(dq);
                     for(int j=0; j<nData; j++){
                        value[j][kk] = dd[j];
                     }
                     kk++;
                     continue;
                  }
                  
                  double[] xx = {xgx};
                  double[] vx = xfunc.getIntValue(xx);
                  
                  double[] xy = {xgx,xgy};
                  double[] vxy = xyfunc.getIntValue(xy);
                  
                  double[] xz = {xgx,xgz};
                  double[] vxz = xzfunc.getIntValue(xz);

                  if(! Double.isNaN(value[0][kk])){
                     for(int j=0; j<nData; j++){
                        value[j][kk] = value[j][kk] - vxy[j] - vxz[j] - vyz[j] 
                                     - vx[j] -vy[j] - vz[j] - dd0[j];
                     }                     
                  }else{
                     String name="q"+(mode[2]+1)+"q"+(mode[1]+1)+"q"+(mode[0]+1)+"-"+nGrid+"-"+kk;
                     System.out.println("Warning: data for "+name+" is not found.");
                     System.out.println("Continues by suppling the energy by inter/extrapolation.");
                  }
                  
                  kk++;

               }
            }
         }
         
      }
   }
   
   private Molecule readminfo(String filename){
      Molecule molecule = null;
      MInfoIO minfo = new MInfoIO();
      try {
         molecule = minfo.loadMOL(filename+".minfo");
      } catch (FileNotFoundException e){
         System.out.println("Warning: "+filename+".minfo is not found.");
         System.out.println("Continues by suppling the energy by inter/extrapolation.");
         molecule = null;
         
      } catch (IOException e) {
         System.out.println("Error while reading "+filename+".");
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
      return molecule;
      
   }

   private boolean checkData(double[][] xx, double[] vv, String basename){

      int nDim = xx.length;
      int[] ng = new int[nDim];
      int[] nn = new int[nDim];
      
      ng[0] = 1; 
      for(int i=1; i<ng.length; i++){
         ng[i] = ng[i-1]*xx[i].length;
      }
      
      for(int kk=0; kk < vv.length; kk++){
         if(Double.isNaN(vv[kk])){

            int k = kk;
            for(int i=nDim-1; i>=0; i--){
               nn[i] = k/ng[i];
               k = k - nn[i]*ng[i];
            }

            //System.out.println(kk);
            //for(int i=0; i<nDim; i++){
            //   System.out.print(nn[i]+" ");
            //}
            //System.out.println();
            
            boolean recover = false;
            for(int i=0; i<nDim; i++){
               double[] xi = xx[i];
               double[] vi = new double[xi.length];
               
               int aa = 0;
               for(int ii = 0; ii<nDim; ii++){
                  if(ii == i) continue;
                  aa = aa + nn[ii]*ng[ii];
               }
               //System.out.println(i+" "+aa+" "+ng[i]);
               
               for(int j=0; j<vi.length; j++){
                  vi[j] = vv[aa+j*ng[i]];
               }
               
               recover = check1D(xi,vi);
               if(recover) {
                  for(int j=0; j<vi.length; j++){
                     if(Double.isNaN(vv[aa+j*ng[i]])){
                        vv[aa+j*ng[i]] = vi[j];
                     }
                  }
                  break;
               }
            }
            
            if(! recover){
               System.out.println("Unable to inter/extrapolate the data.");
               System.out.println("Too many error points in "+basename+".");
               System.out.println("Failed to generate current mode coupling term...");
               return false;
            }
         }
      }

      return true;
      
   }
   
   private boolean check1D(double[] xx, double[] vv){

      int ng = xx.length;

      //for(int i=0; i<ng; i++){
      //   System.out.println(xx[i] + " " + vv[i]);
      //}
      //System.out.println();

      int[] idx1 = new int[ng];
      int[] idx2 = new int[ng];
      int ns = 0;
      int nt = 0;
      for(int i=0; i<ng; i++){
         if(! Double.isNaN(vv[i])){
            idx1[ns] = i;
            ns++;
         }else{
            idx2[nt] = i;
            nt++;
         }
      }
      if(nt > ng/3){
         return false;
      }
      
      double[] xl = new double[ns];
      double[] vl = new double[ns];
      for(int i=0; i<ns; i++){
         xl[i] = xx[idx1[i]];
         vl[i] = vv[idx1[i]];
      }
      //for(int i=0; i<ns; i++){
      //   System.out.println(xl[i] + " " + vl[i]);
      //}
      //System.out.println();
      
      LagrangeInt1 lag1 = new LagrangeInt1(xl,vl);
      
      for(int i=0; i<nt; i++){
         vv[idx2[i]] = lag1.getV(xx[idx2[i]]);
      }
      
      //for(int i=0; i<ng; i++){
      //   System.out.println(xx[i] + " " + vv[i]);
      //}
      //System.out.println();

      return true;
   }
}
