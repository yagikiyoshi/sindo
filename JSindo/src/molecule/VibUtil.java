package molecule;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import sys.*;
import vibration.*;

/**
 * Provides interface methods between molecule and vibration.
 * @author Kiyoshi Yagi
 * @version 1.4
 * @since Sindo 3.0
 */
public class VibUtil extends MolUtil {
   
   private int[][] domainIndex;
   private int[] modeIndex;
   
   /**
    * Constructs a VibCoord object.
    */
   public VibUtil(){
      super();
   }
   /**
    * Constructs a VibCoord object with a given Molecule
    * @param molecule the input Molecule object
    */
   public VibUtil(Molecule molecule){
      this.appendMolecule(molecule);
   }
   
   public void appendMolecule(Molecule molecule){
      super.appendMolecule(molecule);
      this.setupDomainIndex();
      this.setupModeIndex();
   }
   
   private void setupDomainIndex(){
      domainIndex = null;
      int nDomain = molecule.getNumOfVibrationalData();      
      if(nDomain > 0 && molecule.getVibrationalData(0).getAtomIndex() != null){
         domainIndex = new int[nDomain][];
         for(int n=0; n<nDomain; n++){
            domainIndex[n] = molecule.getVibrationalData(n).getAtomIndex();
         }
      }
   }
   private void setupModeIndex(){
      modeIndex = null;
      int nDomain = molecule.getNumOfVibrationalData();
      if(nDomain > 0){
         modeIndex = new int[nDomain];
         int nf = 0;
         for(int n=0; n<nDomain; n++){
            modeIndex[n] = nf;
            nf += molecule.getVibrationalData(n).Nfree;
         }
      }
   }
   
   /**
    * Sets the domain for localized vibrational modes
    * @param atomIndex index of atoms in a domain (int[Ndomain][Natom]) 
    */
   public void setDomain(int[][] atomIndex){
      this.domainIndex = atomIndex;
   }
   /**
    * Unsets the domain
    */
   public void unsetDomain(){
      this.domainIndex = null;
   }
   /**
    * Returns the mode index in the system
    * @param nDomain The index of domain
    * @param nMode The index of mode in the domain
    * @return The index of mode in the system
    */
   public int getModeIndex(int nDomain, int nMode){
      return modeIndex[nDomain] + nMode;
   }
   /**
    * Calculates and returns the normal coordinates of the given molecule. Local normal 
    * modes are created if domains are set. Returns null if hessian is not present in 
    * the electronic data. 
    * @return normal coordinates
    */
   public CoordinateData calcNormalModes(){
      return this.calcNormalModes(true);
   }
   /**
    * Calculates and returns the normal coordinates of the given molecule. Local normal 
    * modes are created if domains are set. Returns null if hessian is not present in 
    * the electronic data. 
    * @param print prints the results to the screen when true.
    * @return normal coordinates
    */
   public CoordinateData calcNormalModes(boolean print){
      
      double[] mass = molecule.getMass();
      double[][] xyz = molecule.getXYZCoordinates2();
      double[] hessian = molecule.getElectronicData().getHessian();
      if(hessian == null) return null;
            
      molecule.clearVibrationalData();

      NormalCoord normco = new NormalCoord();
      normco.setLinear(molecule.isLinear());
      normco.setPrint(print);

      CoordinateData normcoData = null;
      if(domainIndex == null){
         normco.setTransRot(true);
         normcoData = normco.calc(mass, xyz, hessian);
         
         this.addVibrationalData(normcoData,"Normal modes");
         
      }else{
         for(int d=0; d < domainIndex.length; d++){

            int rNat = domainIndex[d].length;
            int rNat3 = rNat*3;
            
            double[] rmass = new double[rNat];
            double[][] rxyz = new double[rNat][3];
            
            for(int n=0; n<rNat; n++){
               rmass[n] = mass[domainIndex[d][n]];
               rxyz[n] = xyz[domainIndex[d][n]];
            }
            
            int[] idx = new int[rNat3];
            int k=0;
            for(int n=0; n<rNat; n++){
               for(int m=0; m<3; m++){
                  idx[k] = domainIndex[d][n]*3+m;
                  k++;
               }
            }

            double[] rhessian = new double[rNat3*(rNat3+1)/2];
            int k1=0;
            int k2=0;
            for(int i=0; i<rNat3; i++){
               for(int j=0; j<=i; j++){
                  k1 = (i+1)*i/2 + j;
                  k2 = (idx[i]+1)*idx[i]/2 + idx[j];
                  rhessian[k1] = hessian[k2];
               }
            }
            
            normco.setTransRot(false);         
            normcoData = normco.calc(rmass, rxyz, rhessian);

            this.addVibrationalData(normcoData,"Local Normal modes",domainIndex[d]);
         }
         
      }
      
      this.setupModeIndex();
      
      return normcoData;
      
   }
   
   /**
    * Calculates the local coordinates of the given molecule with a specified setting
    * of LocalCoord
    * @param localco the object of LocalCoord
    * @return LocalCoord (null if error)
    */
   public CoordinateData calcLocalModes(LocalCoord localco){

      // Set the normal modes for initial vectors
      int nVibData = molecule.getNumOfVibrationalData();
      if(nVibData == 0){
         CoordinateData coord = this.calcNormalModes(false);
         if(coord == null) return null;
      }
      
      CoordinateData localcoData = null;
      if(domainIndex == null){

         MolToVib m2v = new MolToVib();
         localcoData = m2v.getCoordinateData(molecule);

         molecule.clearVibrationalData();
         localco.calc(localcoData);
         
         String comment = "Localized modes: Ethresh = "+localco.getEthresh() + 
               " Conv = " + localco.getZthresh() + " Method = " + localco.getMethod();
         this.addVibrationalData(localcoData,comment);
         
      }else{

         int nDomain = domainIndex.length;
         if(nDomain != nVibData){
            CoordinateData coord = this.calcNormalModes(false);
            if(coord == null) return null;
         }
         
         MolToVib m2v = new MolToVib();
         CoordinateData[] localcoData_n = new CoordinateData[nDomain];         
         for(int n=0; n<nDomain; n++){
            localcoData_n[n] = m2v.getCoordinateData(molecule, n); 
         }
         
         molecule.clearVibrationalData();
         for(int n=0; n<nDomain; n++){
            localco.calc(localcoData_n[n]);
            String comment = "Localized modes: Ethresh = "+localco.getEthresh() + 
                  " Conv = " + localco.getZthresh() + " Method = " + localco.getMethod();
            this.addVibrationalData(localcoData_n[n],comment,domainIndex[n]);
         }
         
      }
      
      this.setupModeIndex();
      
      return localcoData;
      
   }
   
   /**
    * Calculates the (VSCF) optimized coordinates from the transformation matrix.
    * 
    * @param u1dat Filename of the transformation matrix.
    * @return CoordinateData of optimized coordinates (null if error).
    */
   public CoordinateData calcOptimizedModes(String u1dat){

      // Check the number of vibrational domain
      int nVibData = molecule.getNumOfVibrationalData();
      if(nVibData > 1){
         System.out.println("Fatal Error!");
         System.out.println("More than one domain is detected in calcOptimizedModes.");
         System.out.println("Transformation for multiple domains is not implemented.");
         System.out.println("The module is aborted.");
         return null;
         
      }else if (nVibData == 0){
         System.out.println("Fatal Error!");
         System.out.println("Vibrational data is not found!");
         return null;
         
      }
      
      int Nfree = molecule.getVibrationalData().Nfree;
      double[][] U1 = new double[Nfree][Nfree];
      String comment = null;
      try {
         BufferedReader br = new BufferedReader(new FileReader(u1dat));
         String line;
         
         line = br.readLine();
         comment = "Optimized modes: "+line;
         
         line = br.readLine();
         
         double[] dd = Utilities.readData(br);
         
         int k=0;
         for(int i=0; i<Nfree; i++){
            for(int j=0; j<Nfree; j++){
               U1[j][i] = dd[k];
               k++;
            }
         }
         
      } catch (FileNotFoundException e) {
         System.out.println("Error in calcOptimizedModes. u1.dat is not found.");
         return null;
      } catch (IOException e){
         System.out.println("Error in calcOptimizedModes. Error while reading u1.dat.");
         return null;
      }

      // Get the vibrational modes in the data
      MolToVib m2v = new MolToVib();
      CoordinateData optcoData = m2v.getCoordinateData(molecule);
      molecule.clearVibrationalData();

      if(domainIndex == null){
         TransformCoord transCoord = new TransformCoord(optcoData);
         transCoord.setTransformationMatrix(U1);
         transCoord.setHessian(molecule.getElectronicData().getHessian());
         transCoord.transform();
         
         this.addVibrationalData(optcoData, comment);

      }else{
         TransformCoord transCoord = new TransformCoord(optcoData);
         transCoord.setTransformationMatrix(U1);
         
         int nd   = 0;
         int nat  = domainIndex[nd].length;
         int nat3 = nat*3;
         double[] fhess = molecule.getElectronicData().getHessian();
         double[] rhess = new double[nat3*(nat3+1)/2];
         
         int[] idx = new int[nat3];
         int k=0;
         for(int i=0; i<nat; i++){
            for(int j=0; j<3; j++){
               idx[k] = domainIndex[nd][i]*3+j;
               k++;
            }
         }
         
         k=0;
         for(int i=0; i<nat3; i++){
            int ii=idx[i];
            for(int j=0; j<=i; j++){
               int jj=idx[j];
               int kk=ii*(ii+1)/2+jj;
               rhess[k] = fhess[kk];
               k++;
            }
         }
         
         transCoord.setHessian(rhess);
         transCoord.transform();
         
         this.addVibrationalData(optcoData, comment, domainIndex[nd]);

      }

      return optcoData;
   }
   
   /**
    * Calculates the infrared intensity with double harmonic approx (harmonic potential + linear dipole).
    * @return return the infrared intensity of the fundamental transitions (km/mol).
    */
   public double[] calcIRintensity(){

      if(molecule.getElectronicData() == null) return null;
      
      int nDomain = molecule.getNumOfVibrationalData();
      if(nDomain == 0) return null;
      this.setupDomainIndex();
      
      double[] mass = molecule.getMass();
      double[] dderv = molecule.getElectronicData().getDipoleDerivative();      
      if(dderv == null) return null;
      
      int nf = 0;
      for(int n=0; n<nDomain; n++){
         nf += molecule.getVibrationalData(n).Nfree;
      }         
      double[] infrared = new double[nf];
      
      MolToVib m2v = new MolToVib();
      CoordProperty cp = new CoordProperty();      

      if(domainIndex == null){
         
         int Nat = molecule.getNat();
         int Nat3 = Nat*3;
         
         double[][] dd = new double[Nat3][3];
         int k=0;
         for(int i=0; i<Nat; i++){
            double mx = Math.sqrt(mass[i]);
            for(int j=0; j<3; j++){
               for(int xx=0; xx<3; xx++){
                  dd[k][xx] = dderv[k*3+xx]/mx;
               }
               k++;
            }
         }
         
         CoordinateData cdata = m2v.getCoordinateData(molecule);
         cp.appendCoordinate(cdata);
         infrared = cp.getInfrared(dd);

      }else{
         
         nf=0;
         for(int n=0; n<nDomain; n++){
            
            int[] atomIndex = domainIndex[n];
            int Nat = atomIndex.length;
            int Nat3 = Nat*3;
            
            double[][] dd = new double[Nat3][3];
            int k=0;
            for(int i=0; i<Nat; i++){
               double mx = Math.sqrt(mass[atomIndex[i]]);
               for(int j=0; j<3; j++){
                  int kk=atomIndex[i]*3+j;
                  for(int xx=0; xx<3; xx++){
                     dd[k][xx] = dderv[kk*3+xx]/mx;
                  }
                  k++;
               }
            }
            
            CoordinateData cdata = m2v.getCoordinateData(molecule, n);
            cp.appendCoordinate(cdata);
            double[] ir_n = cp.getInfrared(dd);
            for(int i=0; i<ir_n.length; i++){
               infrared[nf] = ir_n[i];
               nf++;
            }

         }
         
      }
      
      return infrared;
   }
   
   /**
    * Calculates the Raman activity with double harmonic approx (harmonic potential + linear polarizability).
    * @return return the Raman activity of the fundamental transitions (Angs^4/amu).
    */
   public double[] calcRamanActivity(){
      
      if(molecule.getElectronicData() == null) return null;
      
      int nDomain = molecule.getNumOfVibrationalData();
      if(nDomain == 0) return null;
      this.setupDomainIndex();
      
      double[] mass = molecule.getMass();
      double[] pderv = molecule.getElectronicData().getPolarizabilityDerivative();
      if(pderv == null) return null;

      int nf = 0;
      for(int n=0; n<nDomain; n++){
         nf += molecule.getVibrationalData(n).Nfree;
      }
      double[] raman = new double[nf];

      MolToVib m2v = new MolToVib();
      CoordProperty cp = new CoordProperty();      

      if(domainIndex == null){
         
         int Nat = molecule.getNat();
         int Nat3 = Nat*3;
         
         double[][] pd = new double[Nat3][6];
         int k=0;
         for(int i=0; i<Nat; i++){
            double mx = Math.sqrt(mass[i]);
            for(int j=0; j<3; j++){
               for(int xy=0; xy<6; xy++){
                  pd[k][xy] = pderv[k*6+xy]/mx;
               }
               k++;
            }
         }
         
         CoordinateData cdata = m2v.getCoordinateData(molecule);
         cp.appendCoordinate(cdata);
         raman = cp.getRaman(pd);

      }else{
         
         nf=0;
         for(int n=0; n<nDomain; n++){
            
            int[] atomIndex = domainIndex[n];
            int Nat = atomIndex.length;
            int Nat3 = Nat*3;
            
            double[][] pd = new double[Nat3][6];
            int k=0;
            for(int i=0; i<Nat; i++){
               double mx = Math.sqrt(mass[atomIndex[i]]);
               for(int j=0; j<3; j++){
                  int kk=atomIndex[i]*3+j;
                  for(int xy=0; xy<6; xy++){
                     pd[k][xy] = pderv[kk*6+xy]/mx;
                  }
                  k++;
               }
            }

            CoordinateData cdata = m2v.getCoordinateData(molecule, n);
            cp.appendCoordinate(cdata);
            double[] raman_n = cp.getRaman(pd);
            for(int i=0; i<raman_n.length; i++){
               raman[nf] = raman_n[i];
               nf++;
            }

         }

      }
      
      return raman;
   }

   /**
    * Returns reduced mass of the vibrational modes
    * @return double[Nfree] in emu
    */
   public double[] calcReducedMass(){
      int nDomain = molecule.getNumOfVibrationalData();
      if(nDomain == 0) return null;
      this.setupDomainIndex();

      int nf = 0;
      for(int n=0; n<nDomain; n++){
         nf += molecule.getVibrationalData(n).Nfree;
      }
      double[] rdmass = new double[nf];

      MolToVib m2v = new MolToVib();
      CoordProperty cp = new CoordProperty();      

      if(domainIndex == null){
         
         CoordinateData cdata = m2v.getCoordinateData(molecule);
         cp.appendCoordinate(cdata);
         rdmass = cp.getReducedMass();

      }else{
         
         nf=0;
         for(int n=0; n<nDomain; n++){
            
            CoordinateData cdata = m2v.getCoordinateData(molecule, n);
            cp.appendCoordinate(cdata);
            double[] rdmass_n = cp.getReducedMass();
            for(int i=0; i<rdmass_n.length; i++){
               rdmass[nf] = rdmass_n[i];
               nf++;
            }

         }

      }

      return rdmass;
   }
   
   /**
    * Returns the harmonic frequency of all modes in the molecule
    * @return double[Nfree] the frequency in cm-1
    */
   public double[] getOmega(){
      int nDomain = molecule.getNumOfVibrationalData();
      if(nDomain == 0) return null;
      
      int nf=0;
      for(int n=0; n<nDomain; n++){
         nf += molecule.getVibrationalData(n).Nfree;
      }
      double[] omega = new double[nf];
      
      nf=0;
      for(int n=0; n<nDomain; n++){
         VibrationalData vdata = molecule.getVibrationalData(n);
         double[] omega_n = vdata.getOmegaV();
         for(int i=0; i<vdata.Nfree; i++){
            omega[nf] = omega_n[i];
            nf++;
         }
      }
      
      return omega;
   }
   /**
    * Add vibrational data to the molecule
    * @param coordData Data of the translation/rotation/vibration coordinates
    */
   private void addVibrationalData(CoordinateData coordData){
      this.addVibrationalData(coordData, "", null);
   }

   /**
    * Add vibrational data to the molecule
    * @param coordData Data of the coordinates
    * @param coordType Type of the coordinates
    */
   private void addVibrationalData(CoordinateData coordData, String coordType){
      this.addVibrationalData(coordData, coordType, null);
   }

   /**
    * Add vibrational data to the molecule
    * @param coordData Data of the coordinates
    * @param coordType Type of the coordinates
    * @param atomIndex Atom index of the domain
    */
   private void addVibrationalData(CoordinateData coordData, String coordType, 
         int[] atomIndex){
      
      VibrationalData vdata = new VibrationalData();
      molecule.addVibrationalData(vdata);
      
      vdata.setCoordType(coordType);
      vdata.setAtomIndex(atomIndex);
      
      if(coordData.getOmegaT() != null){
         vdata.setOmegaT(coordData.getOmegaT());
         vdata.setTransVector(coordData.getCT());         
      }
      if(coordData.getOmegaR() != null){
         vdata.setOmegaR(coordData.getOmegaR());
         vdata.setRotVector(coordData.getCR());         
      }
      
      vdata.setOmegaV(coordData.getOmegaV());
      vdata.setVibVector(coordData.getCV());      
      
   }

   /**
    * Reduces the number of vibrational modes in VibrationalData. The 
    * new modes are renumbered as in the sequence given in the input array.
    * @param selectedModes an array containing the indices {0 to (Nfree-1)} of selected vibrational modes. 
    */
   public void reduceVibModes(int[] selectedModes){

      int nd = molecule.getNumOfVibrationalData();
      if(nd == 1){
         VibrationalData vdata = molecule.getVibrationalData();
         double[][] cv = vdata.getVibVector();
         double[] omega = vdata.getOmegaV();
         
         double[][] cv_new = new double[cv[0].length][selectedModes.length];
         double[] omega_new = new double[selectedModes.length];
         
         for(int n=0; n<selectedModes.length; n++){
            cv_new[n] = cv[selectedModes[n]];
            omega_new[n] = omega[selectedModes[n]];
         }
         
         vdata.setVibVector(cv_new);
         vdata.setOmegaV(omega_new);

      }else{
         
         boolean[] remove = new boolean[nd];
         boolean[] isDone = new boolean[selectedModes.length];
         for(int i=0; i<selectedModes.length; i++){
            isDone[i] = false;
         }
         
         int nf=0;
         for(int n=0; n<nd; n++){
            VibrationalData vdata = molecule.getVibrationalData(n);
            ArrayList<Integer> modeList = new ArrayList<Integer>();

            for(int i=0; i<selectedModes.length; i++){
               if(! isDone[i]){
                  for(int m=0; m<vdata.Nfree; m++){
                     if(selectedModes[i] == nf+m){
                        modeList.add(m);
                        isDone[i] = true;
                     }
                  }                  
               }
            }
            nf += vdata.Nfree;

            if(modeList.size() != 0){
               remove[n] = false;
               double[][] cv = vdata.getVibVector();
               double[] omega = vdata.getOmegaV();
               
               double[][] cv_new = new double[cv[0].length][modeList.size()];
               double[] omega_new = new double[modeList.size()];
               
               for(int m=0; m<modeList.size(); m++){
                  cv_new[m] = cv[modeList.get(m)];
                  omega_new[m] = omega[modeList.get(m)];
               }
               
               vdata.setVibVector(cv_new);
               vdata.setOmegaV(omega_new);

            }else{
               remove[n] = true;
            }
            
         }
         
         for(int n=0; n<nd; n++){
            if(remove[n]){
               molecule.removeVibrationalData(n);
            }
         }
         this.setupDomainIndex();
         
      }
      
   }
   
   /**
    * Returns a weight of the vibrational mode for a given group of atoms 
    * @param mode index of the mode (0 &lt;= mode &lt; Nfree)
    * @param atomIndex index of atoms (0 &lt;= index &lt; Nat)
    * @return weight
    */
   public double weightOfAtoms(int mode, int[] atomIndex){
      
      double[][] cl = molecule.getVibrationalData().getVibVector();
      double ww = 0.0d;
      for(int i=0; i<atomIndex.length; i++){
         int ij=3*atomIndex[i];
         for(int j=0; j<3; j++){
            ww = ww + cl[mode][ij]*cl[mode][ij];
            ij++;
         }
      }
      
      return ww;
   }
   
   /**
    * Combine all vibrational data in current molecule. Note that, after 
    * this method is called, the original domain info is lost, 
    * and thus the domain data cannot be retrieved. 
    */
   public void combineAllVibData(){
      int nd = this.molecule.getNumOfVibrationalData();
      if(nd == 0){
         System.err.println("No vibrational domain is defined!");
         return;
      }
      
      VibrationalData vd = this.molecule.getVibrationalData(0);
      for(int n=1; n<nd; n++){
         VibrationalData v2 = this.molecule.getVibrationalData(n);
         vd = this.combineVibData(vd, v2);         
      }
      
      this.molecule.clearVibrationalData();
      this.molecule.addVibrationalData(vd);
      
   }
   
   /**
    * Combine vibrational data. The data must be domain localized modes, i.e., 
    * don't have trans/rot modes. Also, the domain of input data must not have 
    * an overlap.
    * @param v1 Input vibrational data of domain1.
    * @param v2 Input vibrational data of domain2.
    * @return Combined vibrational data.
    */
   public VibrationalData combineVibData(VibrationalData v1, VibrationalData v2){
      
      int[] atomindex1 = v1.getAtomIndex();
      int[] atomindex2 = v2.getAtomIndex();
      if(atomindex1 == null || atomindex2 == null){
         System.err.println("Error while combining vibrational data");
         System.err.println("Input data does not have a defined domain.");         
         return null;
      }
      
      int nat = atomindex1.length + atomindex2.length;
      int[] atomindex = new int[nat];
      int[] index1 = new int[atomindex1.length];
      int[] index2 = new int[atomindex2.length];
      
      int n1=0;
      int n2=0;
      for(int n=0; n<nat; n++){
         if(atomindex1[n1] == atomindex2[n2]){
            System.err.println("Error while combining vibrational data");
            System.err.println("Input data have overlap of atoms.");
            return null;
         }
         
         if(atomindex1[n1] < atomindex2[n2]){
            atomindex[n] = atomindex1[n1];
            index1[n1] = n;
            n1++;
            
            if(n1 == atomindex1.length) {
               for(int m=n+1; m<nat; m++){
                  atomindex[m] = atomindex2[n2];
                  index2[n2] = m;
                  n2++;
               }
               break;
            }
            
         }else if (atomindex2[n2] < atomindex1[n1]){
            atomindex[n] = atomindex2[n2];
            index2[n2] = n;
            n2++;

            if(n2 == atomindex2.length){
               for(int m=n+1; m<nat; m++){
                  atomindex[m] = atomindex1[n1];
                  index1[n1] = m;
                  n1++;
               }
               break;
            }
            
         }
      }
      
      double[] omega1 = v1.getOmegaV();
      double[] omega2 = v2.getOmegaV();
      
      int nf = omega1.length + omega2.length;
      double[] omega = new double[nf];
      
      double[][] cv1 = v1.getVibVector();
      double[][] cv2 = v2.getVibVector();
      double[][] cv = new double[nf][nat*3];
      
      int nc=0;
      for(int n=0; n<omega1.length; n++){
         omega[nc] = omega1[n];
         for(int m=0; m<atomindex1.length; m++){
            int na = index1[m];
            for(int i=0; i<3; i++){
               cv[nc][na*3+i] = cv1[n][m*3+i];
            }
         }
         nc++;
      }
      for(int n=0; n<omega2.length; n++){
         omega[nc] = omega2[n];
         for(int m=0; m<atomindex2.length; m++){
            int na = index2[m];
            for(int i=0; i<3; i++){
               cv[nc][na*3+i] = cv2[n][m*3+i];
            }
         }
         nc++;
      }
      
      VibrationalData vdata = new VibrationalData();      
      vdata.setCoordType("combined");
      vdata.setAtomIndex(atomindex);
      vdata.setOmegaV(omega);
      vdata.setVibVector(cv);
      
      return vdata;
   }
   
}
