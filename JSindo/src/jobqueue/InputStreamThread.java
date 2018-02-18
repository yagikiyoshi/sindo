package jobqueue;

import java.io.*;

/**
 * (Package private class) Invokes a thread that flushes 
 * a buffer of the given stream.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class InputStreamThread implements Runnable{
   
   /**
    * Input/Error stream of the process
    */
   private InputStream is = null;
   
   /**
    * String added in front of the message
    */
   private String str;
   
   /**
    * Constructs a thread to flush the buffer of the process.
    * @param str String added in front of the message
    * @param is Input/Error stream of the process
    */
   InputStreamThread(String str, InputStream is){
      this.str = str;
      this.is = is;
   }
   
   /**
    * The procedure invoked by Thread.
    */
   public void run(){
      BufferedReader br=null;
      try{
         br = new BufferedReader(new InputStreamReader (is));
         String line;
         while((line=br.readLine()) != null)
            System.out.println(str+line);
      } catch (Exception e){
            e.printStackTrace();
      } finally {
         try{
            br.close();
         } catch (IOException e){
            e.printStackTrace();
         }
      }
   }

}
