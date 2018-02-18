/**
 * Provides the classes to bring up a queuing system for parallel computations. <br>
 * <p> The classes in this package are intended for the following functions: </p>
 * <blockquote>
 * <ol>
 *    <li> Read the information of resources from a file, "resources.info". </li>
 *    <li> Provide a template class to define a task. </li>
 *    <li> Submit and carry out tasks on the resources specified in 1. </li>
 *    <li> Terminate the execution when a file "terminate" exists in the same folder. </li>
 * </ol>
 * </blockquote>
 * <p> -- Example code -- </p>
 * <blockquote><pre>
 *    QueueMngr qm = QueueMngr.getInstance();
 *    qm.printStatus();
 *    System.out.println("[Sending Tasks]");
 *    qm.start();
 *    for(int i=0; i$lt;10; i++){
 *      Task rj = new TaskTest(i);
 *      qm.submit(rj);
 *    }
 *    System.out.println("[End of Send]");
 *    qm.shutdown();
 *    System.out.println("[End of Program]"); 
 * </pre></blockquote>
 * 
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 * 
 */
package jobqueue;
