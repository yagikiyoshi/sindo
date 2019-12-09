package makePES;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import sys.Utilities;
import molecule.*;

/**
 * Super class for Tempfileh and Tempfileg.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class Tempfile {

   protected int nn;
   protected String basename;
   protected VibTransformer trans;
   protected String runmode;
   
   /**
    * Constructor for mode combination, mm.
    * @param mm mode combination (mm[0] > mm[1] > ..)
    * @param nn number of deviation points
    */
   Tempfile(int[] mm, int nn, MakeQFF makeQFF){
      switch(mm.length){
      case 1:
         basename = makeQFF.getBasename(mm[0]);         
         break;
         
      case 2:
         basename = makeQFF.getBasename(mm[0],mm[1]);
         break;
         
      case 3:
         basename = makeQFF.getBasename(mm[0],mm[1],mm[2]);         
         break;
      }
      this.nn = nn;
   }
   /**
    * Appends transformation module form Cartesian to normal coords.
    * @param trans Transformation module
    */
   public void appendTransform(VibTransformer trans){
      this.trans = trans;
   }
   /**
    * Sets the runmode (dump,remove)
    * @param runmode dump or remove
    */
   public void setRunMode(String runmode){
      this.runmode = runmode;
   }
   
   /**
    * Dumps the data to temporary file
    * @param basename basename of temporary file 
    * @param data the data to be stored
    */
   protected void dumpTempfile(String basename, double[][] data){
      try{
         DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(basename+".tempfile")));
         for(int i=0; i<data.length; i++){
            for(int j=0; j<data[0].length; j++){
               dos.writeDouble(data[i][j]);
            }
         }
         dos.close();

      }catch(IOException e){
         System.out.println("Error while generating a tempfile for "+basename);
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
   }
   
   /**
    * Dumps the data to temporary file
    * @param basename basename of temporary file 
    * @param data the data to be stored
    */
   protected void dumpTempfile(String basename, double[][][] data){
      try{
         DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(basename+".tempfile")));
         for(int i=0; i<data.length; i++){
            for(int j=0; j<data[0].length; j++){
               for(int k=0; k<=j; k++){
                  dos.writeDouble(data[i][j][k]);                  
               }
            }
         }
         dos.close();

      }catch(IOException e){
         System.out.println("Error while generating a tempfile for "+basename);
         System.out.println(e.getMessage());
         Utilities.terminate();
      }
   }

}
