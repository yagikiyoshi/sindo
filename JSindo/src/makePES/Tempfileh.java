package makePES;

import java.io.File;
import java.io.IOException;

import molecule.MInfoIO;
import sys.Utilities;

/**
 * Transforms the gradient, Hessian in Cartesian to vibrational coords, and save to 
 * temporary file.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class Tempfileh extends Tempfile implements Runnable {

   Tempfileh(int[] mm, int nn){
      super(mm,nn);
   }
   
   /**
    * Main process
    */
   public void run() {

      // System.out.println("Job for "+basename+" "+runmode);
      
      if(runmode.equals("dump")){
         MInfoIO minfo = new MInfoIO();
         double[][][] hessQ = new double[nn][][];
         
         for(int n=0; n<nn; n++){
            try{
               minfo.loadMOL(basename+"-"+n+".minfo");            
            }catch(IOException e){
               System.out.println("Error while saving Hessian to a temporary file.");
               System.out.println(e.getMessage());
               Utilities.terminate();
            }
            hessQ[n] = trans.hx2hq(minfo.getMolecule().getElectronicData().getHessian());
         }
         
         dumpTempfile(basename,hessQ);
         
      }else if(runmode.equals("remove")){
         File tempfile = new File(basename+".tempfile");
         tempfile.delete();

      }

   }
}
