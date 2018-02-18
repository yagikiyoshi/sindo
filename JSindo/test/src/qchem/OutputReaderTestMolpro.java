package qchem;

import java.io.FileNotFoundException;

import molecule.QChemToMol;
import molecule.MInfoIO;

public class OutputReaderTestMolpro {

   public static void main(String[] args) {
      QuantChem qchem = new QuantChem();
      OutputReader outputReader = null;  
      try{
         outputReader = qchem.getOutputReader("molpro");
         outputReader.setBasename("sample/Molpro/ArPtCO");
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
      
      double[] hessian = outputReader.readHessian();
      for(int i=0; i<hessian.length; i++){
         System.out.printf(" %15.8e ",hessian[i]);
         if(i%5==4) System.out.println();
      }
      System.out.println();

      double[] geometry = outputReader.readGeometry();
      if(geometry != null){
         System.out.println("Geometry");
         for(int i=0; i<geometry.length; i++){
            System.out.printf(" %15.8e ",geometry[i]);
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

      double[] dipoleDer = outputReader.readDipoleDerivative();
      if(dipoleDer != null){
         System.out.println("Dipole Derivative");
         for(int i=0; i<dipoleDer.length; i++){
            System.out.printf(" %15.8e ", dipoleDer[i]);
            if(i%5==4) System.out.println();
         }
         System.out.println();
      }
      
      QChemToMol mr = new QChemToMol(outputReader);
      mr.setCharge(QChemToMol.Mulliken);
      MInfoIO minfo = new MInfoIO(mr.read());
      minfo.printAtoms();
      minfo.printElecStruct();

   }

}
