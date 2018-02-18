package jobqueue;

/** 
 * A test implementation of Task. <br>
 *   > echo hello "JobID"
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class TaskTest extends Task {

   private int taskID;
   
   /**
    * Constructs the object of Task
    * @param JobID ID of the current task
    */
   public TaskTest(int JobID){
      this.taskID = JobID;
   }
   
   /**
    * Set a command (echo hello ID)
    */
   protected String[] getCommand() {
      String[] command = {"echo","hello "+taskID};
      return command;
   }
   
   /**
    * Pre-process : Do nothing.
    */
   protected int preProcess(){
      return 0;
   }
   
   /**
    * Post-process (sleep for a 5 second)
    */
   protected int postProcess(){
      try{
         Thread.sleep(5000);
      }catch(InterruptedException e){
         e.printStackTrace();
         return -1;
      }
      return 0;
   }

}
