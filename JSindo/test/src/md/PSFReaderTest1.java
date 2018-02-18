package md;

import java.io.*;
import java.util.HashMap;

import ff.CHARMMff;

import sys.Constants;

public class PSFReaderTest1 {

   public static void main(String[] args){
      
      CHARMMff charmff = new CHARMMff();
      try{
         charmff.readParameter("test/md/par_all36_prot_lipid.prm");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      charmff.printParameter();

      SystemMD si = new SystemMD();

      PSFReader psfr = new PSFReader("test/md/si.psf");
      psfr.appendForceField(charmff);
      psfr.setSystemMD(si);
      try{
         psfr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Potential pot = si.getPotential();
      
      CRDReader crdr = new CRDReader("test/md/si_mini.crd");
      crdr.setSystemMD(si);
      try{
         crdr.readCoordinates();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      int nn=1;
      Residue[] residues = si.getResidueALL();
      for(int i=0; i<residues.length; i++){
         String resName = residues[i].getName();
         int resID = residues[i].getID();
         AtomMD[] atoms = residues[i].getAtomALL();
         for(int j=0; j<atoms.length; j++){
            double[] xyz = atoms[j].getXYZCoordinates();
            System.out.printf("%4d %4d %5s %5s %5s %8.4f %8.4f  %12.6f %12.6f %12.6f \n", 
                  nn, resID, resName, 
                  atoms[j].getLabel(),
                  atoms[j].getType(),
                  atoms[j].getAtomicCharge(),
                  atoms[j].getMass()*Constants.Emu2Amu,
                  xyz[0]*Constants.Bohr2Angs,
                  xyz[1]*Constants.Bohr2Angs,
                  xyz[2]*Constants.Bohr2Angs
                 );
            nn++;
         }
      }
      System.out.println();
      
      AtomMD[] atoms = si.getAtomALL();
      double energy = pot.getEnergy(atoms)*Constants.Hartree2kcalmol;
      System.out.printf("Energy = %12.6f \n",energy);

      HashMap<String,Double> comp = pot.getEnergyComponent();
      String[] keys = comp.keySet().toArray(new String[0]);
      for(int i=0; i<keys.length; i++){
         double eneComp = comp.get(keys[i])*Constants.Hartree2kcalmol;
         System.out.printf("Energy ( "+keys[i]+" ) = %12.6f \n",eneComp);
      }

   }
}
