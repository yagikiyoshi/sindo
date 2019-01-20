package jobqueue;


public class QueueMngrTest1 {

   public static void main(String[] args){

      QueueMngr qm = QueueMngr.getInstance();
      qm.printResources();
//      System.out.println("[Sending Tasks]");
//      qm.start();
//      for(int i=0; i<10; i++){
//        Task rj = new TaskTest(i);
//        qm.submit(rj);
//      }
//      System.out.println("[End of Send]");
//      qm.shutdown();
      System.out.println("[End of Program]");
   }

}
