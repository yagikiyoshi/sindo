package vibration;

import qchem.*;
import molecule.*;

/*
 * Read data from test/vibration/co2-freq.fchk and obtain normal modes.
 * 

x0:
   0.0000   0.0000   2.2061
   0.0000   0.0000   0.0000
   0.0000   0.0000  -2.2061

Translational Vectors:
     0.01 cm-1 :    0.6030   0.0000   0.0000   0.5223   0.0000   0.0000   0.6030   0.0000   0.0000
     0.01 cm-1 :    0.0000   0.6030   0.0000   0.0000   0.5223   0.0000   0.0000   0.6030   0.0000
     0.07 cm-1 :    0.0000   0.0000   0.6030   0.0000   0.0000   0.5223   0.0000   0.0000   0.6030

Rotational Vectors:
     8.98 cm-1 :    0.7071   0.0000  -0.0000   0.0000   0.0000  -0.0000  -0.7071   0.0000  -0.0000
     8.98 cm-1 :    0.0000  -0.7071   0.0000   0.0000  -0.0000   0.0000   0.0000   0.7071   0.0000

Vibrational Vectors:
   655.18 cm-1 :    0.2611  -0.2611  -0.0000  -0.6030   0.6030   0.0000   0.2611  -0.2611   0.0000
   655.18 cm-1 :   -0.2611  -0.2611   0.0000   0.6030   0.6030  -0.0000  -0.2611  -0.2611  -0.0000
  1362.44 cm-1 :   -0.0000   0.0000  -0.7071   0.0000   0.0000   0.0000   0.0000   0.0000   0.7071
  2421.60 cm-1 :    0.0000  -0.0000   0.3693  -0.0000   0.0000  -0.8528  -0.0000   0.0000   0.3693

 */

public class NormalCoordTest1 {
   
   public static void main(String[] args){
      QuantChem qchem = new QuantChem();
      OutputReader or = null;
      try{
         or = qchem.getOutputReader("Gaussian");
      }catch(TypeNotSupportedException e){
         e.printStackTrace();
      }
      or.setBasename("test/vibration/co2-freq");
      
      QChemToMol mr = new QChemToMol(or);
      Molecule co2 = mr.read();
      
      NormalCoord normco = new NormalCoord();
      normco.setLinear(co2.isLinear());
      normco.calc(co2.getMass(), co2.getXYZCoordinates2(), co2.getElectronicData().getHessian());
   }

}
