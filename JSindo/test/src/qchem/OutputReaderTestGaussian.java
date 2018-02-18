package qchem;

import java.io.FileNotFoundException;

public class OutputReaderTestGaussian {

   public static void main(String[] args) {
      QuantChem qchem = new QuantChem();
      OutputReader outputReader = null;  
      try{
         outputReader = qchem.getOutputReader("Gaussian");
         outputReader.setBasename("test/qchem/g09/h2o-freq");
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
      double charge = outputReader.readCharge();
      double multiplicity = outputReader.readMultiplicity();
      System.out.println("Charge = "+charge);
      System.out.println("Multiplicity = "+multiplicity);
      
      double[] gradient = outputReader.readGradient();
      if(gradient != null){
         System.out.println("Gradient");
         for(int i=0; i<gradient.length; i++){
            System.out.printf(" %15.8e ",gradient[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();         
      }

      double[] hessian = outputReader.readHessian();
      if(hessian != null){
         System.out.println("Hessian");
         for(int i=0; i<hessian.length; i++){
            System.out.printf(" %15.8e ",hessian[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();         
      }

      double[] geometry = outputReader.readGeometry();
      if(geometry != null){
         System.out.println("Geometry");
         for(int i=0; i<geometry.length; i++){
            System.out.printf(" %15.8e ",geometry[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();         
      }

      double[] atomicNum = outputReader.readAtomicNumber();
      if(atomicNum != null){
         System.out.println("Atomic Number");
         for(int i=0; i<atomicNum.length; i++){
            System.out.printf(" %15.8e ",atomicNum[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();         
      }
      
      double[] mcharge = outputReader.readMullikenCharge();
      if(mcharge != null){
         System.out.println("Mulliken Charge");
         for(int i=0; i<mcharge.length; i++){
            System.out.printf(" %15.8e ", mcharge[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();         
      }
      
      double[] espcharge = outputReader.readESPCharge();
      if(espcharge != null){
         System.out.println("ESP Charge");
         for(int i=0; i<espcharge.length; i++){
            System.out.printf(" %15.8e ", espcharge[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();
      }
      
      double[] npacharge = outputReader.readNPACharge();
      if(npacharge != null){
         System.out.println("NPA Charge");
         for(int i=0; i<npacharge.length; i++){
            System.out.printf(" %15.8e ", npacharge[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();
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
      
      double[] polar = outputReader.readPolarizability();
      if(polar != null){
         System.out.println("Polarizability");
         for(int i=0; i<polar.length; i++){
            System.out.printf(" %15.8e ", polar[i]);
            if(i%5==4) System.out.println();            
         }
         System.out.println();
      }

      double[] hyper = outputReader.readHyperPolarizablity();
      if(hyper != null){
         System.out.println("Hyperpolarizability");
         for(int i=0; i<hyper.length; i++){
            System.out.printf(" %15.8e ", hyper[i]);
            if(i%5==4) System.out.println();            
         }
         System.out.println();
      }

      double[] dipoleDer = outputReader.readDipoleDerivative();
      if(dipoleDer != null){
         System.out.println("Dipole Derivative");
         for(int i=0; i<dipoleDer.length; i++){
            System.out.printf(" %15.8e ", dipoleDer[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();
      }
      
      double[] polarDer = outputReader.readPolarizabilityDerivative();
      if(polarDer != null){
         System.out.println("Polarizability Derivative");
         for(int i=0; i<polarDer.length; i++){
            System.out.printf(" %15.8e ", polarDer[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();
         
      }

      
   }

}
