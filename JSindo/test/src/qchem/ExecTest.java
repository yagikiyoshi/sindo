package qchem;


public class ExecTest {
   
   public static void main(String[] args){
      QuantChem qchem = new QuantChem();
      Exec exec = null;
      try{
         exec = qchem.getExec("Gaussian");
         //exec = qchem.getExec("PIMD");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
      }
      exec.setBasename("test");
      String[] command = exec.getCommand();
      for(int i=0; i<command.length; i++){
         System.out.printf(command[i]+" ");
      }
      System.out.println();
      
      exec.removeFiles();
      
   }

}
