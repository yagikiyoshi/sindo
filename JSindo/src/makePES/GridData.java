package makePES;

import java.io.*;
import sys.*;

/**
 * Interface with the grid data files
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class GridData {
   
   private int nDim;
   private int nData;
   private int[] nGrid;
   private double[][] xGrid;
   private double[][] value;
   private File file;
   private LagrangeInt1[] lag1;
   private LagrangeInt2[] lag2;
   private LagrangeInt3[] lag3;

   /**
    * Constructor
    * @param nDim the dimension of the data
    */
   
   public GridData(int nDim){
      this.nDim = nDim;
      nGrid = new int[nDim];
      xGrid = new double[nDim][];
   }
   /**
    * Constructor
    * @param nDim the dimension of the data
    * @param name the name of the data file
    */
   public GridData(int nDim, String name){
      this(nDim);
      this.setFileName(name);
   }
   /**
    * Constructor
    * @param nDim the dimension of the data
    * @param file the data file
    */
   public GridData(int nDim, File file){
      this(nDim);
      this.setFile(file);
   }
   
   /**
    * Set the data file by name
    * @param name the name of the data file
    */
   public void setFileName(String name){
      this.setFile(new File(name));
   }
   
   /**
    * Set the data file
    * @param file the data file
    */
   public void setFile(File file){
      this.file = file;
   }

   /**
    * Write the data to a file
    * @param title the title of the data written in the first line.
    * @param mode the mode combination
    * @param label the label of the data
    */
   public void writeData(String title, int[] mode, String[] label){
      try{
         PrintStream ps = new PrintStream(file);
         ps.println(title);
         ps.println("# Number of grids and data");
         int tot = 1;
         for(int i=0; i<nDim; i++){
            ps.printf("%6d ", nGrid[i]);
            tot = tot*nGrid[i];
         }
         ps.printf("%6d ", nData);
         ps.println();
         
         ps.print("#   ");
         for(int i=0; i<nDim; i++){
            ps.printf("q%-14d ", mode[i]+1);
         }
         //ps.print("    ");
         for(int i=0; i<nData-1; i++){
            label[i] = label[i].trim();
            ps.print(label[i]);
            for(int j=0; j<20-label[i].length(); j++){
               ps.print(" ");
            }
         }
         ps.println(label[nData-1]);
         
         int[] count = new int[nDim];
         
         //double cc = Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu);
         for(int i=0; i<tot; i++){
            
            int dd = 1;
            for(int j=0; j<nDim; j++){
               //ps.printf("%15.8f ", xGrid[j][count[j]]*cc);
               ps.printf("%15.8f ", xGrid[j][count[j]]);
               if(i%dd == dd-1) count[j]++;
               if(count[j]==nGrid[j]) count[j]=0;
               dd = dd*nGrid[j];
            }
            for(int j=0; j<nData; j++){
               ps.printf("%20.10e", value[j][i]); 
            }
            ps.println();
            //ps.printf("%20.10f \n", value[i]);
         }
         ps.close();
         
      }catch(IOException e){
         e.printStackTrace();
      }
      
   }

   /**
    * Read the data from the specified file.
    */
   public void readData(){

      try{
         BufferedReader br =  new BufferedReader(new FileReader(file));
         br.readLine();
         br.readLine();
         String[] nn = Utilities.splitWithSpaceString(br.readLine());
         int tot = 1;
         for(int i=0; i<nDim; i++){
            nGrid[i] = Integer.parseInt(nn[i]);            
            xGrid[i] = new double[nGrid[i]];
            tot = tot*nGrid[i];
         }
         if(nn.length == nDim+1) {
            nData = Integer.parseInt(nn[nDim]);
         }else{
            nData = 1;
         }
         value = new double[nData][tot];
         double[][] xx = new double[nDim][tot];

         br.readLine();
         
         for(int i=0; i<tot; i++){
            String line = br.readLine();
            String[] aa = Utilities.splitWithSpaceString(line);
            for(int j=0; j<nDim; j++){
               xx[j][i] = Double.parseDouble(aa[j]);
            }
            for(int j=0; j<nData; j++){
               value[j][i] = Double.parseDouble(aa[nDim+j]);               
            }
         }
         
         br.close();

         int dd = 1;
         for(int i=0; i<nDim; i++){
            for(int j=0; j<nGrid[i]; j++){
               xGrid[i][j] = xx[i][j*dd];               
            }
            dd = dd*nGrid[i];
         }
      }catch(IOException e){
         System.out.println("Error while reading "+file.getName());
         System.out.println(e.getMessage());
      }
   }
   
   /**
    * Returns the grid points.
    * @return the grid points in Bohr (emu)^1/2
    */
   public double[][] getGrid(){
      return xGrid;
   }
   /**
    * Returns the grid points
    * @param nD the dimension (x=0,y=1,z=2)
    * @return the grid points
    */
   public double[] getGrid(int nD){
      return xGrid[nD];
   }
   /**
    * Sets the grid points.
    * @param xGrid the grid points in Bohr (emu)^1/2
    */
   public void setGrid(double[][] xGrid){
      this.xGrid = xGrid;
      for(int i=0; i<nDim; i++){
         this.nGrid[i] = xGrid[i].length;
      }
   }

   /**
    * Sets the grid points 
    * @param nD the dimension (x=0,y=1,z=2)
    * @param xx the grid points
    */
   public void setGrid(int nD, double[] xx){
      this.xGrid[nD] = xx;
      this.nGrid[nD] = xx.length;
   }

   /**
    * Return the function value.
    * @return the function value[nData][nGrid^nDim] in au.
    */
   public double[][] getValue(){
      return value;
   }
   /**
    * Sets the function value.
    * @param value the function values in au. [nData][nGrid^nDim].
    */
   public void setValue(double[][] value){
      this.value = value;
      this.nData = value.length;
   }
   /**
    * Returns the function values for the specified position of the grid
    * @param pos the position of the grid
    * @return the function values
    */
   public double[] getValue(int[] pos){
      int kk = 0;
      int dd = 1;
      for(int i=0; i<nDim; i++){
         kk = kk + dd*pos[i];
         dd = dd*nGrid[i];
      }
      double[] vv = new double[nData];
      for(int i=0; i<nData; i++){
         vv[i] = value[i][kk];
      }
      return vv;
   }
   /**
    * Save the current data file with ".org" extension.
    */
   public void savefile(){
      File savefile = new File(file.getName()+".org");
      if(savefile.exists()){
         System.out.println("Error while making backup of the original pot file. "+savefile.getName()+" already exists.");
         Utilities.terminate();
      }
      file.renameTo(savefile);
   }
   /**
    * Returns whether the given xx is within the grid or not.
    * @param xx the coordinates
    * @return false if out of the grid
    */
   public boolean isInGrid(double[] xx){
      boolean inGrid = true;
      double ratio = 1.01;
      for(int i=0; i<xGrid.length; i++){
         if(xx[i]< xGrid[i][0]*ratio || xx[i] > xGrid[i][nGrid[i]-1]*ratio){
            inGrid = false;
            break;
         }
      }
      return inGrid;
   }

   /**
    * Setup an interpolation function for the current data
    */
   public void setupInterpolater(){
      if(nDim == 1){
         lag1 = new LagrangeInt1[nData];
         for(int i=0; i<nData; i++){
            lag1[i] = new LagrangeInt1(xGrid[0],value[i]);            
         }
         //lag1 = new LagrangeInt1(xGrid[0],value);

      }
      if(nDim == 2){
         lag2 = new LagrangeInt2[nData];
         for(int i=0; i<nData; i++){
            lag2[i] = new LagrangeInt2(xGrid[0],xGrid[1],value[i]);
         }
         //lag2 = new LagrangeInt2(xGrid[0],xGrid[1],value);
         
      }
      if(nDim == 3){
         lag3 = new LagrangeInt3[nData];
         for(int i=0; i<nData; i++){
            lag3[i] = new LagrangeInt3(xGrid[0],xGrid[1],xGrid[2],value[i]); 
         }
         //lag3 = new LagrangeInt3(xGrid[0],xGrid[1],xGrid[2],value);
         
      }
   }
   /**
    * Returns the value at xx obtained by Lagrange interpolation
    * @param xx the position
    * @return the interpolated function value
    */
   public double[] getIntValue(double[] xx){
      double[] value = new double[nData];
      if(nDim == 1){
         for(int i=0; i<nData; i++){
            value[i] = lag1[i].getV(xx[0]);
         }
         //value = lag1.getV(xx[0]);

      }
      if(nDim == 2){
         for(int i=0; i<nData; i++){
            value[i] = lag2[i].getV(xx[0], xx[1]);
         }
         //value = lag2.getV(xx[0], xx[1]);
         
      }
      if(nDim == 3){
         for(int i=0; i<nData; i++){
            value[i] = lag3[i].getV(xx[0], xx[1], xx[2]);
         }
         //value = lag3.getV(xx[0], xx[1], xx[2]);
         
      }
      return value;

   }
}
