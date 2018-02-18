package qchem;

import jobqueue.*;

public class ExecTestQChem {
   
   public static void main(String[] args){
      QuantChem qchem = new QuantChem();
      Exec exec = null;
      try{
         exec = qchem.getExec("qchem");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
      }
      exec.setBasename("test");
      
      Resource res = new Resource();
      String[] hosts = {"diva01","diva02"};
      res.setHostnames(hosts);
      res.setMemory(2);
      res.setScr(250);
      res.setPpn(8);
      exec.setResource(res);

      String[] command = exec.getCommand();
      for(int i=0; i<command.length; i++){
         System.out.printf(command[i]+" ");
      }
      System.out.println();
      
      exec.removeFiles();
      
   }

}
