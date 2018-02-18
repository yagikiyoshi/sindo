package molecule;

import java.io.IOException;
import qchem.OutputReader;
import qchem.QuantChem;
import qchem.TypeNotSupportedException;

/*
 * Append the electronic data from Gaussian checkpoint file to the existing molecule.
 * 
 */
public class QChemToMolTest2 {
   
   public static void main(String[] args){

      // load only the atomic data for H2CO.
      MInfoIO minfo = new MInfoIO();
      minfo.unsetAllData();
      minfo.setAtomData(true);
      try{
         minfo.loadMOL("test/molecule/h2co-freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule mol = minfo.getMolecule();

      // Setup output reader for H2O
      QuantChem qchem = new QuantChem();
      OutputReader outputReader = null;  
      try{
         outputReader = qchem.getOutputReader("Gaussian");         
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      outputReader.setBasename("test/molecule/h2o-freq");
      
      // Setup electronic data reader
      QChemToMol edataReader = new QChemToMol();
      edataReader.appendReader(outputReader);
      edataReader.appendMolecule(mol);
      
      edataReader.read();

      // This prints edata for H2O with H2CO molecule (weird!)
      minfo.setVersion(2);
      minfo.printAtoms();
      minfo.printElecStruct();
      
      // Force to read atomic data
      edataReader.setReadAtomicData(true);
      edataReader.read();
      minfo.printAtoms();
      minfo.printElecStruct();
      
      
   }

}
