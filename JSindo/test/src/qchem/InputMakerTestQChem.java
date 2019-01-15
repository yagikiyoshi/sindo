package qchem;

import java.io.*;
import atom.Atom;
import jobqueue.*;
import molecule.*;
import sys.*;

public class InputMakerTestQChem {
   
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
         im.setOptions("test/qchem/qc/QChemInput.xml");
      }catch(Exception e){
         e.printStackTrace();
         Utilities.terminate();
      }
      im.setBasename("test/qchem/qc/h2co");
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
