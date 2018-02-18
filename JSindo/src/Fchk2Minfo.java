

import java.io.FileNotFoundException;
import java.io.IOException;

import atom.Atom;

import molecule.*;
import qchem.*;


public class Fchk2Minfo {
   
   public static void main(String[] args){

      if(args.length<1){
         System.out.println("USAGE: java Fchk2Minfo aaa");
         System.out.println("          aaa ... basename of the fchk file.");
         System.exit(-1);
      }

      QuantChem qchem = new QuantChem();
      OutputReader outputReader = null;  
      try{
         outputReader = qchem.getOutputReader("Gaussian");         
         outputReader.setBasename(args[0]);
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
      
      QChemToMol mr = new QChemToMol(outputReader);
      mr.setCharge(QChemToMol.NoAtmCharge);
      Molecule molecule = mr.read();

      MInfoIO minfo = new MInfoIO(molecule);
      double[] com = minfo.getCenterOfMass();
      for(int n=0; n<molecule.getNat(); n++){
         Atom atom = molecule.getAtom(n);
         double[] xyz = atom.getXYZCoordinates();
         for(int x=0; x<3; x++){
            xyz[x] = xyz[x] - com[x];
         }
      }

      VibUtil vutil = new VibUtil();
      vutil.appendMolecule(molecule);
      vutil.setDomain(null);
      vutil.calcNormalModes(false);
      
      minfo.printAtoms();
      minfo.printElecStruct();
      minfo.printVibration();

      try {
         minfo.dumpMOL(args[0]+".minfo");
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
   }

}
