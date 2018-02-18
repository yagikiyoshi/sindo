package qchem;

import java.io.IOException;
import molecule.*;

public class InputMakerTestPIMD {
   
   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("sample/PIMD/water.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule molecule = minfo.getMolecule();
      
      QuantChem qchem = new QuantChem();
      InputMaker im = null;
      try{
         im = qchem.getInputMaker("PIMD");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      im.setBasename("q1-0");
      im.setMolecule(molecule);
      
      System.out.println("Generate centroid.dat in q1-0");
      im.makeInputFile();

   }

}
