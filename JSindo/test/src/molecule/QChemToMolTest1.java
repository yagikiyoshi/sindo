package molecule;

import java.io.FileNotFoundException;
import java.io.IOException;

import qchem.OutputFileException;
import qchem.OutputReader;
import qchem.QuantChem;
import qchem.TypeNotSupportedException;

/*
 * Read the electronic data from Gaussian output checkpoint file, and print the data.
 * 
 */
public class QChemToMolTest1 {
   
   public static void main(String[] args){

      QuantChem qchem = new QuantChem();
      OutputReader outputReader = null;  
      try{
         outputReader = qchem.getOutputReader("Gaussian");         
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      outputReader.setBasename("test/qchem/h2o-freq");
      
      QChemToMol edataReader = new QChemToMol();
      edataReader.appendReader(outputReader);
      
      try {
         edataReader.check();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         System.exit(-1);
      } catch (OutputFileException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      edataReader.setCharge(QChemToMol.NoAtmCharge);

      Molecule mol = edataReader.read();
      
      MInfoIO minfo = new MInfoIO();
      minfo.appendMolecule(mol);
      minfo.printAtoms();
      minfo.printElecStruct();
      try {
         minfo.dumpMOL("test/molecule/h2o-freq.minfo");
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      
   }

}
