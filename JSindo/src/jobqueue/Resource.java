   package jobqueue;

/**
 * Entity object for a resource. <br>
 * This object includes the following information which are accessible through setter/getter: <br>
 * <ol>
 * <li> ID : the ID of this resource</li>
 * <li> memory : the amount of memory</li>
 * <li> scr : the amount of scratch disk</li>
 * <li> ppn : the # of cores/processors</li>
 * <li> hostnames : the name of node(s)</li>
 * </ol>
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 * @see RunProcess
 */
public class Resource implements Cloneable{

   /**
    * The ID of this resource.
    */
   private int ID;
   /**
    * The number of nodes (default=1).
    */
   private int nodes=1;
   /**
    * The amount of available memory in GB (default=-1 : unlimited).
    */
   private int memory=-1;
   /**
    * The amount of available scratch in GB (default=-1 : unlimited).
    */
   private int scr=-1;
   /**
    * The number of available processors (=core) per node (default=1).
    */
   private int ppn=1;
   /**
    * The name of hosts.
    */
   private String[] hostnames=null;
   /**
    * True if this resource if open.
    */
   private boolean free=true;
   
   /**
    * Set an ID
    * @param ID The ID of this resource
    */
   public void setID(int ID){
      this.ID=ID;
   }
   /**
    * Get an ID
    * @return The ID of this resource
    */
   public int getID(){
      return ID;
   }
   /**
    * Get the number of nodes
    * @return nodes The number of nodes
    */
   public int getNodes(){
      return nodes;
   }
   /**
    * Set the available memory (GB)
    * @param memory The available memory
    */
   public void setMemory(int memory){
      this.memory=memory;
   }
   /**
    * Get the available memory (GB)
    * @return The available memory (GB)
    */
   public int getMemory(){
      return memory;
   }
   /**
    * Set the size of scratch disk (GB)
    * @param scr The size of scratch disk (GB)
    */
   public void setScr(int scr){
      this.scr=scr;
   }
   /**
    * Get the size of scratch disk (GB)
    * @return The size of scratch disk
    */
   public int getScr(){
      return scr;
   }
   /**
    * Set the number of processors (cores) per node.
    * @param ppn The number of processors (cores) per node.
    */
   public void setPpn(int ppn){
      this.ppn=ppn;
   }
   /**
    * Get the number of processors (cores) per node.
    * @return The number of processors (cores) per node.
    */
   public int getPpn(){
      return ppn;
   }
   /**
    * Set the hostname(s) of the node(s).
    * @param hostnames Hostname(s)
    */
   public void setHostnames(String[] hostnames){
      this.hostnames=hostnames;
      nodes = hostnames.length;
   }
   /**
    * Get the hostname(s) of the node(s)
    * @return Hostname(s)
    */
   public String[] getHostnames(){
      return hostnames;
   }
   /**
    * Set this resource to be free.
    */
   public void setFree(){
      free=true;
   }
   /**
    * Set this resource to be busy.
    */
   public void setBusy(){
      free=false;
   }
   /**
    * Inquire if the resource is free
    * @return true if it is free.
    */
   public boolean isFree(){
      return free;
   }
   /**
    * Create a clone of the current resource.
    * @return A cloned resource
    */
   public Resource createClone(){
      Resource resource = null;
      try{
         resource = (Resource)clone();
      }catch(CloneNotSupportedException e){
         // Do nothing
      }
      resource.hostnames = null;
      return resource;
   }
   /**
    * Print the status of current resources.
    * @param spacer The spaces to make an indent down.
    */
   public void printStat(String spacer){
      System.out.println(spacer+"ID       : "+ID);
      System.out.print  (spacer+"Hosts    :");
      for(int i=0; i<nodes; i++){
         System.out.print(" "+hostnames[i]);
      }
      System.out.println();
      System.out.println(spacer+"Ppn      : "+ppn);
      if(memory>0){
         System.out.println(spacer+"Memory   : "+memory+" GB");         
      }else{
         System.out.println(spacer+"Memory   : unlimit");
      }
      if(scr>0){
         System.out.println(spacer+"SCR      : "+scr+" GB");         
      }else{
         System.out.println(spacer+"SCR      : unlimit");
      }
      if(free){
         System.out.println(spacer+"Status   : FREE");         
      }else{
         System.out.println(spacer+"Status   : BUSY");
      }
   }

}
