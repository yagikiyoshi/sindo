package qchem;

import java.io.IOException;

import sys.Constants;
import molecule.*;
import atom.*;

public class GaussianGeomOptTest1 {

   public static void main(String[] args){
      
      GaussianGeomOpt opt = new GaussianGeomOpt();
      opt.setBaseName("test/qchem/g09/FeB-his3-glu-NO_d");
      try {
         opt.readOutput();
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      opt.printStatus();
      Molecule NO_d = opt.getMolecule();
      

      for(int n=0; n<NO_d.getNat(); n++){
         Atom ai = NO_d.getAtom(n);
         double mass = ai.getMass()*Constants.Emu2Amu;
         double[] xyz = ai.getXYZCoordinates();
         for(int x=0; x<xyz.length; x++){
            xyz[x] = xyz[x] * Constants.Bohr2Angs;
         }
         System.out.printf("%4s,  %4d, %12.4f,  %15.8f,  %15.8f,  %15.8f \n",
               ai.getLabel(),ai.getAtomicNum(),mass,xyz[0],xyz[1],xyz[2]);

      }
      
      opt.setBaseName("test/qchem/g09/FeB-his3-glu-NO_s");
      try {
         opt.readOutput();
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      opt.printStatus();
      Molecule NO_s = opt.getMolecule();
      

      for(int n=0; n<NO_s.getNat(); n++){
         Atom ai = NO_s.getAtom(n);
         double mass = ai.getMass()*Constants.Emu2Amu;
         double[] xyz = ai.getXYZCoordinates();
         for(int x=0; x<xyz.length; x++){
            xyz[x] = xyz[x] * Constants.Bohr2Angs;
         }
         System.out.printf("%4s,  %4d, %12.4f,  %15.8f,  %15.8f,  %15.8f \n",
               ai.getLabel(),ai.getAtomicNum(),mass,xyz[0],xyz[1],xyz[2]);

      }
      
   }
}
