package jobqueue;

import java.util.concurrent.*;

import sys.Utilities;

/**
 * The main class that manages the queuing system. <br>
 * <p> The instance is accessed by the <i>getInstance</i>, <br> </p>
 * <blockquote>
 *    QueueMngr qm = QueueMngr.getInstance();
 * </blockquote> 
 * <p>
 * This class implements the singleton pattern, so the instance always points to the same object 
 * wherever it is called. At the first call, the instance reads available resources from 
 * "resource.info".
 * </p>
 * <p>
 * After the QueueMngr is started by the <i>start</i>, QueueMngr accepts tasks to be 
 * allocated to the resources by the <i>submit(Task)</i>, <br>
 * <blockquote><pre>
 *    qm.start();
 *    for(int i=0; i&lt;10; i++){
 *      Task rj = new TaskTest(i);
 *      qm.submit(rj);
 *    }
 *    System.out.println("[End of Send]");
 *    qm.shutdown();
 * </pre></blockquote>
 * The main (or current) thread waits until all the threads finish after the <i>shutdown</i>. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class QueueMngr extends ResourceMngr {
   
   /**
    * Instance of QueueMngr (Singleton). 
    */
   private static QueueMngr qmgr = new QueueMngr();
   
   /**
    * ExecutorService with fixed number (= #resource) thread pool.
    */
   private ExecutorService ex;
   
   /**
    * Number of active threads in the pool used for a safe termination. 
    */
   private int NumOfActiveThread;
   
   /**
    * Constructor is NOT public available; use getInstance instead.
    */
   private QueueMngr(){
      super();
   }
   
   /**
    * Returns an instance of QueueMngr
    * @return QueueMngr Queue Manager
    */
   public static QueueMngr getInstance(){
      return qmgr;
   }
   
   /**
    * (Re)Start the manager. Submission is accepted after this method. 
    */
   public void start(){
      ex = Executors.newFixedThreadPool(super.getNumOfResource());      
      NumOfActiveThread = super.getNumOfResource();
      //System.out.println("NumOfActiveThread = " + NumOfActiveThread);
   }
   
   /**
    * Submit a Task.
    * @param rj Task to be executed.
    */
   public void submit(Task rj){
      //rj.appendQueue(this);
      ex.execute(rj);
   }
   
   /**
    * Stop accepting any further submission and wait until all the task is processed. 
    */
   public void shutdown(){
      ex.shutdown();
      try{
         ex.awaitTermination(10000L, TimeUnit.DAYS);
      }catch(InterruptedException ie){
         ie.printStackTrace();
      }
   }

   /**
    * Used by Task to tell the manager that the current task is deactivated.
    */
   synchronized void deActivate(){
      NumOfActiveThread--;
      if(NumOfActiveThread == 0){
        //ex.shutdownNow();
        System.out.println();
        System.out.println("All Child Threads are terminated. Terminating the Process...");
        System.out.println();
        Utilities.terminate();
      }
   }
   
}
