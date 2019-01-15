package qchem;

import java.io.*;
import atom.Atom;
import jobqueue.*;
import molecule.*;
import sys.*;

public class InputMakerTestQChem2 {
   
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
         im = qchem.getInputMaker("QChem");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      try{
         im.setTemplateFile("test/qchem/qc/QChemTemplate");
      }catch(Exception e){
         e.printStackTrace();
         Utilities.terminate();
      }
      im.setBasename("test/qchem/qc/h2co_temp");
      im.setMolecule(molecule);
      
      Resource res = new Resource();
      String[] hosts = {"diva01","diva02"};
      res.setHostnames(hosts);
      res.setMemory(2);
      res.setScr(250);
      res.setPpn(8);
      
      im.setResource(res);      
      im.makeInputFile();

   }

}
