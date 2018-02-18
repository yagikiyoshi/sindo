package jobqueue;

import java.io.*;

import sys.Constants;

/**
 * <p>(Abstract class) Provides a template of the task to be submitted to the 
 * QueueMngr. In order to inherent this class, one needs to implement the 
 * following three methods:
 * <ol>
 * <li> void setCommand() ... set the unix command </li>
 * <li> void preProcess() ... the method is called before the execution </li>
 * <li> void postProcess() ... the method is called after the execution </li>
 * </ol>
 * <p> 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 * @see TaskTest
 */
public abstract class Task implements Runnable {
   
   protected Resource resource;
   protected String header;

   /**
    * Returns a command to be executed.
    * @return String array of command
    */
   protected abstract String[] getCommand();
   /**
    * Invoked before the command is executed.
    * @return 0: exec, -1: abort, else skip exec 
    */
   protected abstract int preProcess();
   /**
    * Invoked after the command is executed.
    * @return -1: abort, else do nothing 
    */
   protected abstract int postProcess();
   
   /**
    * The procedure invoked by Thread.
    */
   public void run(){

      QueueMngr qMngr = QueueMngr.getInstance();
      resource = qMngr.getResource();
      
      int ID=resource.getID();
      String[] nodes=resource.getHostnames();
      if(Constants.remoteShell != null && nodes != null){
         header="Thread"+ID+"@"+ nodes[0]+"> ";
      }else{
         header="Thread"+ID+"> ";
      }
      
      File term = new File("terminate");
      if(term.exists()){
         System.out.println(header 
               +" terminate signal received. Waiting for other threads to end...");
         qMngr.deActivate();
         while(true){
            try{
               Thread.sleep(100000000L);
            }catch(InterruptedException e){
               // Do nothing..
            }
         }
      }

      
      int stat = this.preProcess();

      if(stat == 0){
         String[] cmdArray;
         String[] command = this.getCommand();
         if(Constants.remoteShell != null && nodes != null){
            cmdArray = new String[command.length+2];
            cmdArray[0] = Constants.remoteShell;
            cmdArray[1] = nodes[0];
            for(int i=0; i<command.length; i++){
               cmdArray[i+2]=command[i];
            }
            
         }else{
            cmdArray = new String[command.length];
            for(int i=0; i<command.length; i++){
               cmdArray[i]=command[i];
            }
         }

         RunProcess rp = new RunProcess();
         rp.exec(header,cmdArray);
         
         stat = this.postProcess();
         
      }
      qMngr.releaseResource(resource);
      
      if(term.exists() || stat == -1){
         System.out.println(header 
               +" terminate signal received. Waiting for other threads to end...");

         qMngr.deActivate();
         
         if(! term.exists()){
            try{
               term.createNewFile();
            }catch(IOException e){
               System.out.println("Failed to create terminate file.");
            }
         }
         
         while(true){
            try{
               Thread.sleep(100000000L);
            }catch(InterruptedException e){
               // Do nothing..
            }
         }
      }
   }

}
