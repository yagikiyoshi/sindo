package qchem;

import java.io.*;
import jobqueue.*;
import molecule.*;
import sys.*;

public class InputMakerTestGaussian2 {
   
   public static void main(String[] args){
      
      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/qchem/h2co-freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Molecule molecule = minfo.getMolecule();
      
      QuantChem qchem = new QuantChem();
      InputMaker im = null;
      try{
         im = qchem.getInputMaker("Gaussian");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      try{
         im.setTemplateFile("test/qchem/g09/GaussianTemplate");
      }catch(Exception e){
         e.printStackTrace();
         Utilities.terminate();
      }
      im.setBasename("test/qchem/g09/h2co");
      im.setMolecule(molecule);

      im.makeInputFile();

   }

}
