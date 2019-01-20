package jobqueue;

import java.util.*;
import java.io.*;

import sys.Constants;
import sys.Utilities;

/**
 * (Protected class) Manages the resources.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class ResourceMngr {
   
   /**
    * A list of all resources
    */
   private ArrayList<Resource> list = new ArrayList<Resource>();
   
   /**
    * Constructor reads the resources from "resources.info". 
    */
   protected ResourceMngr(){
      try{
         String line;
         String[] aaa,bbb;
         
         BufferedReader br = new BufferedReader(new FileReader("resources.info"));
         line = br.readLine().trim();
         int c =0;
         while((c = line.indexOf("#"))==0 || line.length() == 0){
            line = br.readLine().trim();
         }
         if((c=line.indexOf("#"))>0){
            line = line.substring(0, c);            
         }

         int ID = 0;
         Resource res1 = new Resource();
         res1.setID(ID);
         line = line.replaceAll(",", " ");
         aaa = Utilities.splitWithSpaceString(line);
         /*
         if(aaa.length != 4) {
            System.out.println();
            System.out.println("Error in ResourceMngr!");
            System.out.println("Format error in the first line of resources.info.");
            System.out.println("All parameters, HOSTNAME, ppn, mem, and scr must be present.");
            Utilities.terminate();
            
         }
         */
         
         bbb = aaa[0].split(":");
         res1.setHostnames(bbb);
         for(int i=1; i<aaa.length; i++){
            bbb = aaa[i].split("=");
            this.setter(bbb[0].trim(), Integer.parseInt(bbb[1]), res1);
         }
         list.add(res1);
         ID++;
         
         while((line = br.readLine()) != null){
            line=line.trim();
            c=line.indexOf("#");
            if(c==0){
               continue;
            } 
            if(c>0){
               line = line.substring(0, c);            
            }
            //System.out.println(line);
            if(line.length()==0){
               continue;
            }
            Resource res2 = res1.createClone();
            res2.setID(ID);
            line = line.replaceAll(",", " ");
            aaa = Utilities.splitWithSpaceString(line);
            bbb = aaa[0].split(":");
            res2.setHostnames(bbb);
            for(int i=1; i<aaa.length; i++){
               bbb = aaa[i].split("=");
               this.setter(bbb[0].trim(), Integer.parseInt(bbb[1]),res2);
            }
            list.add(res2);
            ID++;
            
         }
         
         br.close();
         // System.exit(0);
      }catch(FileNotFoundException e){
         System.out.println();
         System.out.println("Error in ResourceMngr!");
         System.out.println("resources.info is not found!");
         Utilities.terminate();
      }catch(IOException e){
         e.printStackTrace();
         Utilities.terminate();
      }
      /*
      for(int i=0; i<list.size(); i++){
         list.get(i).printStat();
      }
      */
      
      Constants.remoteShell = System.getenv("SINDO_RSH");
      if(this.getNumOfResource() > 2 && Constants.remoteShell == null) {
         System.out.println();
         System.out.println();
         System.out.println("       Error in ResourceMngr! SINDO_RSH is null!");
         System.out.println("       Set the environment variable SINDO_RSH to ssh or rsh. You can do this by typing in sh/bash ");
         System.out.println("          export SINDO_RSH=ssh");
         System.out.println("       or in csh/tcsh");
         System.out.println("          setenv SINDO_RSH ssh");
         System.out.println();
         System.out.println();
         Utilities.terminate();         
      }
      
   }
   /**
    * Set paramters by key and value to Resource
    * @param key The name of the parameter (ppn/mem/scr)
    * @param value The value to be set.
    * @param res The resource
    */
   private void setter(String key, int value, Resource res){
      if(key.equalsIgnoreCase("ppn")){
         res.setPpn(value);
      }else if(key.equalsIgnoreCase("mem")){
         res.setMemory(value);
      }else if(key.equalsIgnoreCase("scr")){
         res.setScr(value);
      }else{
         System.out.println("Format error in resource.info.");
         System.out.println("--");
         System.out.println("'"+key+"' is not a valid key.");
         System.out.println("Available keys are:");
         System.out.println("   ppn ... # of processes per node");
         System.out.println("   mem ... Size of memory in GB");
         System.out.println("   scr ... Size of scratch disk in GB");
         System.out.println("--");
         Utilities.terminate();
      }
   }

   /**
    * Get number of resources
    * @return Number of resources
    */
   public int getNumOfResource(){
      return list.size();
   }
   
   /**
    * Get resource
    * @return An available resource 
    */
   public synchronized Resource getResource(){
      Resource ri=null;
      for(int i=0; i<list.size(); i++){
         ri = list.get(i);
         if(ri.isFree()){
            ri.setBusy();
            break;
         }
      }
      return ri;
   }
   
   /**
    * Release a resource. 
    * @param ri A resource to be released
    */
   public synchronized void releaseResource(Resource ri){
      ri.setFree();
   }

   /**
    * Print all the resource data to standard output
    */
   public void printResources(){
      this.printResources("");
   }
   /**
    * Print all the resource data to standard output
    * @param spacer Header of the print
    */
   public void printResources(String spacer){
      for(int i=0; i<list.size(); i++){
         System.out.println(spacer+"-----");
         list.get(i).printStat(spacer);
      }
      System.out.println(spacer+"-----");
   }
}
