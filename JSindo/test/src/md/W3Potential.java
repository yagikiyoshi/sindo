package md;

import java.io.*;
import java.util.HashMap;

import ff.*;
import atom.*;
import sys.Constants;

public class W3Potential {

   public static void main(String[] args){
      
      CHARMMff charmff = new CHARMMff();
      try{
         charmff.readParameter("test/md/par_all36_prot_lipid.prm");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      PSFReader psfr = new PSFReader("test/md/w3-2.psf");
      psfr.appendForceField(charmff);
      try{
         psfr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      SystemMD w3 = psfr.getSystemMD();
      Potential pot = w3.getPotential();
      
      CRDReader crdr = new CRDReader("test/md/w3.crd");
      crdr.setSystemMD(w3);
      try{
         crdr.readCoordinates();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      
      Residue[] residues = w3.getResidueALL();
      
      {
         AtomMD[] allatom = w3.getAtomALL();
         AtomUtil autil = new AtomUtil();
         double[] xyz5 = autil.genAtomXYZ(allatom[3], 1.1/Constants.Bohr2Angs, allatom[0], 74.1, allatom[1], -173.9);
         allatom[5].setXYZCoordinates(xyz5);

         double[] xyz4 = autil.genAtomXYZ(allatom[3], 1.2/Constants.Bohr2Angs, allatom[5], 110.0, allatom[0], -144.3);
         allatom[4].setXYZCoordinates(xyz4);

         PDBWriter pdbw = new PDBWriter();
         try{
            pdbw.write("test/md/w3-2.pdb", w3);            
         }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
         }

         for(int i=0; i<allatom.length; i++){
            double[] xyz = allatom[i].getXYZCoordinates();
            System.out.printf("%10.5f%10.5f%10.5f \n", xyz[0]*Constants.Bohr2Angs, xyz[1]*Constants.Bohr2Angs, xyz[2]*Constants.Bohr2Angs);
         }
         System.out.println();
      }
      
      for(int i=0; i<residues.length; i++){
         String resName = residues[i].getName();
         int resID = residues[i].getID();
         AtomMD[] atoms = residues[i].getAtomALL();
         for(int j=0; j<atoms.length; j++){
            double[] xyz = atoms[j].getXYZCoordinates();
            VdW vdwj = atoms[j].getVdW();
            System.out.printf("%4d %4d %5s %5s %5s %8.4f %8.4f %12.6f %12.6f  %12.6f %12.6f %12.6f \n", 
                  atoms[j].getID(), resID, resName, 
                  atoms[j].getLabel(),
                  atoms[j].getType(),
                  atoms[j].getAtomicCharge(),
                  atoms[j].getMass()*Constants.Emu2Amu,
                  vdwj.getEpsilon()*Constants.Hartree2kcalmol,
                  vdwj.getRmin()*Constants.Bohr2Angs,
                  xyz[0]*Constants.Bohr2Angs,
                  xyz[1]*Constants.Bohr2Angs,
                  xyz[2]*Constants.Bohr2Angs
                 );
         }
      }
      System.out.println();
      
      AtomMD[] atoms = w3.getAtomALL();
      double energy = pot.getEnergy(atoms)*Constants.Hartree2kcalmol;
      System.out.printf("Energy = %12.6f \n",energy);

      HashMap<String,Double> comp = pot.getEnergyComponent();
      String[] keys = comp.keySet().toArray(new String[0]);
      for(int i=0; i<keys.length; i++){
         double eneComp = comp.get(keys[i])*Constants.Hartree2kcalmol;
         System.out.printf("Energy ( "+keys[i]+" ) = %12.6f \n",eneComp);
      }
      
      AtomUtil autil = new AtomUtil();

      double coulomb = 0.0d;
      double vdw = 0.0d;
      for(int i=1; i<residues.length; i++){
         AtomMD[] atomi = residues[i].getAtomALL();
         for(int j=0; j<i; j++){
            AtomMD[] atomj = residues[j].getAtomALL();
            
            for(int ni=0; ni<atomi.length; ni++){
               for(int nj=0; nj<atomj.length; nj++){
                  double rij = autil.getBondLength(atomi[ni], atomj[nj]);
                  coulomb += atomi[ni].getAtomicCharge()*atomj[nj].getAtomicCharge()/rij;
                  
                  VdW vdwi = atomi[ni].getVdW();
                  VdW vdwj = atomj[nj].getVdW();
                  double epsij = Math.sqrt(vdwi.getEpsilon()*vdwj.getEpsilon());
                  double rr  = (vdwi.getRmin() + vdwj.getRmin())/rij;
                  vdw += epsij*(Math.pow(rr, 12.0) - 2.0d * Math.pow(rr,6.0));
               }
            }
         }
      }
      // coulomb=coulomb*Constants.Hartree2kcalmol;
      // cc=332.0716 is set in CHARMM to convert Angs-1 to kcal mol-1
      // This constant means Bohr2Angs = 0.529189779, which is slightly different from ours (0.529177209).
      double cc = 332.0716d+0;
      coulomb=coulomb/Constants.Bohr2Angs*cc;
      vdw=vdw*Constants.Hartree2kcalmol;
      
      System.out.printf("Energy ( COULOMB ) = %12.6f \n", coulomb);
      System.out.printf("Energy ( VDW ) = %12.6f \n", vdw);

      energy += vdw + coulomb;
      System.out.printf("Total Energy = %12.6f \n",energy);
      System.out.println();
      
      // QM/MM
      int[] qmatoms = {3,4,5};
      pot.setQMatoms(qmatoms);
      for(int i=0; i<qmatoms.length; i++){
         atoms[qmatoms[i]].setAtomicCharge(0.0);
      }
      energy = pot.getEnergy(atoms);
      
      energy = pot.getEnergy(atoms)*Constants.Hartree2kcalmol;
      System.out.printf("Energy = %12.6f \n",energy);

      comp = pot.getEnergyComponent();
      for(int i=0; i<keys.length; i++){
         double eneComp = comp.get(keys[i])*Constants.Hartree2kcalmol;
         System.out.printf("Energy ( "+keys[i]+" ) = %12.6f \n",eneComp);
      }

      coulomb = 0.0d;
      for(int i=1; i<residues.length; i++){
         AtomMD[] atomi = residues[i].getAtomALL();
         for(int j=0; j<i; j++){
            AtomMD[] atomj = residues[j].getAtomALL();
            
            for(int ni=0; ni<atomi.length; ni++){
               for(int nj=0; nj<atomj.length; nj++){
                  double rij = autil.getBondLength(atomi[ni], atomj[nj]);
                  coulomb += atomi[ni].getAtomicCharge()*atomj[nj].getAtomicCharge()/rij;
                  
               }
            }
         }
      }
      // coulomb=coulomb*Constants.Hartree2kcalmol;
      // cc=332.0716 is set in CHARMM to convert Angs-1 to kcal mol-1
      // This constant means Bohr2Angs = 0.529189779, which is slightly different from ours (0.529177209).
      //double cc = 332.0716d+0;
      coulomb=coulomb/Constants.Bohr2Angs*cc;
      
      System.out.printf("Energy ( COULOMB ) = %12.6f \n", coulomb);
   }
}
