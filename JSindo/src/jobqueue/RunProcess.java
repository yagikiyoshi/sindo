package jobqueue;

import java.io.*;

/**
 * Invoke a process which executes the command at the resource. 
 * If the resource has more than one node, the command is executed 
 * at the first node (node1 in the following example).
 * <p> Example <br>
 * <blockquote><pre>
 *    // Setup a resource
 *    Resource resource = new Resource();
 *    resource.setID(0);
 *    String[] hostnames = {"node1","node2"};
 *    resource.setHostnames(hostnames);
 *    
 *    // Invoke a process that executes "java -version"
 *    RunProcess test = new RunProcess();
 *    String[] cmd = {"java","-version"};
 *    test.exec(resource,cmd);
 * </pre></blockquote>
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class RunProcess {

   /**
    * Execute the process
    * @param header The header of output
    * @param cmdArray The command to be executed
    * @return The end status of the process
    */
   public int exec(String header, String[] cmdArray){

      int c=0;
      Process p=null;
      InputStream in = null;
      InputStream er = null;
      try{
         p = Runtime.getRuntime().exec(cmdArray);
         in = p.getInputStream();
         er = p.getErrorStream();
         
         InputStreamThread inthread = new InputStreamThread(header,in);        
         Thread stdRun = new Thread(inthread);
         stdRun.start();
         
         InputStreamThread erthread = new InputStreamThread(header,er);
         Thread errRun = new Thread(erthread);
         errRun.start();
         
         // wait for the process to end
         c = p.waitFor();
         stdRun.join();
         errRun.join();
         in.close();
         er.close();
         p.getOutputStream().close();
         p.destroy();
         
         //System.out.println(c);
         
      }catch(InterruptedException e){
         //e.printStackTrace();
         System.out.println(header +"Interrpted!");
         p.destroy();
      } catch (IOException e) {
         System.out.println("Error while executing a process.");
         e.printStackTrace();
      }
      return c;
   }
   
}
