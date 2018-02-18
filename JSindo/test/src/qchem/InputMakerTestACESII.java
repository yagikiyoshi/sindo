package qchem;

import atom.Atom;
import jobqueue.*;
import molecule.*;
import sys.*;

public class InputMakerTestACESII {
   
   public static void main(String[] args){
      
      Molecule molecule = new Molecule();
            
      Atom O1 = new Atom(8);
      double[] O1xyz = {0.0, 0.0, 2.20607458};
      O1.setXYZCoordinates(O1xyz);
      molecule.addAtom(O1);
      
      Atom C2 = new Atom(6);
      double[] C2xyz = {0.0, 0.0, 0.0};
      C2.setXYZCoordinates(C2xyz);
      molecule.addAtom(C2);
      
      Atom O3 = new Atom(8);
      double[] O3xyz = {0.0, 0.0, -2.20607458};
      O3.setXYZCoordinates(O3xyz);
      molecule.addAtom(O3);
      
      QuantChem qchem = new QuantChem();
      InputMaker im = null;
      try{
         im = qchem.getInputMaker("ACESII");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
         System.exit(-1);
      }
      try{
         im.setOptions("test/qchem/ACESIIinput.xml");
      }catch(Exception e){
         e.printStackTrace();
         Utilities.terminate();
      }
      im.setBasename("co2");
      im.setMolecule(molecule);
      
      Resource res = new Resource();
      String[] hosts = {"diva01","diva02"};
      res.setHostnames(hosts);
      res.setMemory(24);
      res.setScr(250);
      res.setPpn(8);
      
      im.setResource(res);      
      im.makeInputFile();

   }

}
