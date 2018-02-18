package vibration;

import java.io.*;
import sys.*;

public class BandProfileTest2 {

   public static void main(String[] args){

      double[] omegaData = null;
      double[] inttyData = null;
      try{
         BufferedReader br = new BufferedReader(new FileReader("test/vibration/band.info"));
         String line = br.readLine();
         int ndata = Integer.parseInt(line);
         
         omegaData = new double[ndata];
         inttyData = new double[ndata];
         
         for(int i=0; i<ndata; i++){
            line = br.readLine();
            double[] aa = Utilities.splitWithSpaceDouble(line);
            omegaData[i] = aa[0];
            inttyData[i] = aa[1];
         }
         br.close();
         
      }catch(IOException e){
         e.printStackTrace();
      }
      
      BandProfile intensity = new BandProfile(10.0);
      intensity.setBand(omegaData, inttyData);
      
      int nstep = 301;
      double omega = 1600;
      for(int i=0; i<nstep; i++){
         System.out.printf("%12.2f %12.4f \n", omega,intensity.getIntensity(omega));
         omega=omega+1.0;
      }
      
   }
}
