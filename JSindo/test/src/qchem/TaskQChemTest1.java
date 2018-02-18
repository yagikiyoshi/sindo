package qchem;

import java.io.*;
import molecule.*;
import jobqueue.*;

public class TaskQChemTest1 {

   public static void main(String[] args){
      
      // Read the molecule from a minfo file 
      MInfoIO util = new MInfoIO();
      util.unsetAllData();
      util.setAtomData(true);
      try{
         util.loadMOL("sample/H2O/h2o_freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule molecule = util.getMolecule();


      // Setup Task
      TaskQChem task = null;
      try{
         task = new TaskQChem("Gaussian");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      task.setBasename("test1");
      task.setMolecule(molecule);
      
      // Set Input options
      try{
         task.setInputOptions("sample/Gaussian/GaussianTemplate1.xml");
      }catch(FileNotFoundException e){
         e.printStackTrace();
         System.exit(-1);
      }catch(InputOptionException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      // Set MinfoIO (optional)
      MInfoIO minfoIO = new MInfoIO();
      minfoIO.unsetAllData();
      minfoIO.setElecData(true);
      task.setMinfoIO(minfoIO);
      
      QueueMngr qm = QueueMngr.getInstance();
      qm.printResources();
      System.out.println("[Sending Tasks]");
      qm.start();
      qm.submit(task);
      System.out.println("[End of Send]");
      qm.shutdown();
      System.out.println("[End of Program]");
   }

}
