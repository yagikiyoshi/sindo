package vibration;

import molecule.*;

/**
 * Convert molecule data into CoordinateData. 
 * @author Kiyoshi Yagi
 * @version 2.0
 * @since Sindo 3.0
 */
public class MolToVib {
   
   /**
    * Convert the vibrational data (of first domain) in Molecule into CoordinateData
    * @param  molecule Molecule that contains vibrational Data
    * @return CoordinateData
    */
   public CoordinateData getCoordinateData(Molecule molecule){
      return this.getCoordinateData(molecule, 0);
   }
   
   /**
    * Convert the vibrational data of n-th domain in Molecule into CoordinateData
    * @param molecule Molecule that contains vibrational Data
    * @param nDomain the index of domain
    * @return CoordinateData
    */
   public CoordinateData getCoordinateData(Molecule molecule, int nDomain){

      VibrationalData vdata = molecule.getVibrationalData(nDomain);
      CoordinateData coord = new CoordinateData();
      coord.setOmegaT(vdata.getOmegaT());
      coord.setOmegaR(vdata.getOmegaR());
      coord.setOmegaV(vdata.getOmegaV());
      coord.setCT(vdata.getTransVector());
      coord.setCR(vdata.getRotVector());
      coord.setCV(vdata.getVibVector());
      coord.Nfree = vdata.Nfree;
      coord.Nrot = vdata.Nrot;
      
      
      int[] atomIndex = vdata.getAtomIndex();
      if(atomIndex == null){
         coord.Nat = molecule.getNat();
         coord.setX0(molecule.getXYZCoordinates2());
         coord.setMass(molecule.getMass());
         
      }else{
         int Nat = atomIndex.length;

         double[][] x0 = molecule.getXYZCoordinates2();
         double[]   m0 = molecule.getMass();

         double[][] x1 = new double[Nat][];
         double[]   m1 = new double[Nat];
         for(int n=0; n<Nat; n++){
            x1[n] = x0[atomIndex[n]];
            m1[n] = m0[atomIndex[n]];
         }

         coord.Nat = Nat;
         coord.setX0(x1);
         coord.setMass(m1);
         
      }
      
      return coord;
   }
   
}
