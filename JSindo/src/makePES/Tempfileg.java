package makePES;

import java.io.File;
import java.io.IOException;

import molecule.MInfoIO;
import sys.Utilities;

/**
 * Transforms the gradient in Cartesian to vibrational coords, and save to 
 * temporary file.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class Tempfileg extends Tempfile implements Runnable {
   
   Tempfileg(int[] mm, int nn, MakeQFF makeQFF){
      super(mm,nn, makeQFF);
   }
   
   /**
    * Main process
    */
   public void run() {

      //System.out.println("Job for "+basename+" "+runmode);
      
      if(runmode.equalsIgnoreCase("dump")){
         MInfoIO minfo = new MInfoIO();
         double[][] gradQ = new double[nn][];
         
         for(int n=0; n<nn; n++){
            try{
               minfo.loadMOL(basename+"-"+n+".minfo");            
            }catch(IOException e){
               System.out.println("Error while saving Gradient to a temporary file.");
               System.out.println(e.getMessage());
               Utilities.terminate();
            }
            gradQ[n] = trans.gx2gq(minfo.getMolecule().getElectronicData().getGradient());
         }
         
         dumpTempfile(basename,gradQ);
         
      }else if(runmode.equalsIgnoreCase("remove")){
         File tempfile = new File(basename+".tempfile");
         tempfile.delete();

      }

   }

}
