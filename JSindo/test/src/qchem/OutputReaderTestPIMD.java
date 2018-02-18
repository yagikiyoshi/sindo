package qchem;

import java.io.FileNotFoundException;

public class OutputReaderTestPIMD {

   public static void main(String[] args) {
      QuantChem qchem = new QuantChem();
      OutputReader outputReader = null;  
      try{
         outputReader = qchem.getOutputReader("PIMD");
         
         outputReader.setBasename("q1-0");
         outputReader.setBasename("sample/PIMD/q1-0");
         outputReader.checkFile();
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }catch(FileNotFoundException e){
         e.printStackTrace();
         System.exit(-1);
      }catch(OutputFileException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      double energy = outputReader.readEnergy();
      if(! Double.isNaN(energy)){
         System.out.println("Energy = "+energy);         
      }else{
         System.out.println("Energy is not found. Terminated with error.");
         System.exit(-1);
      }
      double[] dipole = outputReader.readDipoleMoment();
      if(dipole != null){
         System.out.println("Dipole Moment");
         for(int i=0; i<dipole.length; i++){
            System.out.printf(" %15.8e ", dipole[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();
      }
      
      double[] geometry = outputReader.readGeometry();
      double[] atomicNum = outputReader.readAtomicNumber();
      double[] atomicMass = outputReader.readAtomicMass();
      String[] label = outputReader.readLabel();
      
      if(geometry != null){
         System.out.println("Geometry");
         for(int i=0; i<atomicNum.length; i++){
            System.out.printf(label[i]+" %5.1f %12.4f %15.8e %15.8e %15.8e",atomicNum[i],atomicMass[i],
                  geometry[3*i],geometry[3*i+1],geometry[3*i+2]);
            System.out.println();
         }
         System.out.println();         
      }

      double[] gradient = outputReader.readGradient();
      if(gradient != null){
         System.out.println("Gradient");
         for(int i=0; i<gradient.length; i++){
            System.out.printf(" %15.8e ",gradient[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();         
      }


      
   }

}
