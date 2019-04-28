package qchem;

import java.io.*;
import java.util.*;

import molecule.*;
import sys.*;
import atom.*;

public class GaussianGeomOpt {

   private String basename;
   private Molecule molecule = null;
   private ArrayList<double[][]> traj_min;
   private boolean force_isConv;
   private int niter, nstep;
   private ArrayList<Double>   scf_energy;
   private ArrayList<double[][]> td_info;
   private ArrayList<double[]> forceList;
   private ArrayList<double[]> mulliken_charge;
   private ArrayList<double[]> mulliken_spin;
   
   public void setBaseName(String basename) {
      this.basename = basename;
   }
   
   public void setNstep(int n) {
      nstep = n-1;
      return;
   }
   
   public Molecule readOutput() throws IOException {
      
      molecule     = new Molecule();
      traj_min     = new ArrayList<double[][]>();
      scf_energy   = new ArrayList<Double>();
      td_info      = new ArrayList<double[][]>();
      force_isConv = false;
      forceList    = new ArrayList<double[]>();
      mulliken_charge = new ArrayList<double[]>();
      mulliken_spin   = new ArrayList<double[]>();
      nstep = -1;
      
      BufferedReader br = new BufferedReader(new FileReader(basename+".out"));
      
      String line = br.readLine();
      while(line.indexOf("Leave Link    1") == -1){
         line = br.readLine();
      }
      br.readLine();
      br.readLine();
      molecule.setTitle(br.readLine());

      int Nat=0;
      while(true){
         line = br.readLine();
         if(line.indexOf("NAtoms") != -1){
            String[] ss = Utilities.splitWithSpaceString(line);
            Nat = Integer.parseInt(ss[1]);
            break;
         }
      }
      //System.out.println(Nat);

      double[][] current_xyz = new double[Nat][];
      traj_min.add(current_xyz);

      while(line.indexOf("Input orientation") == -1 
            && line.indexOf("Standard orientation") == -1){
         line = br.readLine();
      }
      
      br.readLine();
      br.readLine();
      br.readLine();
      br.readLine();
      for(int n=0; n<Nat; n++){
         String[] ss = Utilities.splitWithSpaceString(br.readLine());
         Atom atom = new Atom(Integer.parseInt(ss[1]));
         double[] xyz = new double[3];
         xyz[0] = Double.parseDouble(ss[3])/Constants.Bohr2Angs;
         xyz[1] = Double.parseDouble(ss[4])/Constants.Bohr2Angs;
         xyz[2] = Double.parseDouble(ss[5])/Constants.Bohr2Angs;

         atom.setXYZCoordinates(xyz);
         molecule.addAtom(atom);
         
         current_xyz[n] = xyz;
         
      }

      niter = 0;
      int nn = 0;
      double current_scf_energy = 0.0d;
      double rmsforce = 100.0;
      
      while((line = br.readLine()) != null){
         
         if(line.indexOf("Input orientation") != -1 
               || line.indexOf("Standard orientation") != -1){
         
            current_xyz = new double[Nat][];
            traj_min.add(current_xyz);
            
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            for(int n=0; n<Nat; n++){
               String[] ss = Utilities.splitWithSpaceString(br.readLine());
               double[] xyz = new double[3];
               xyz[0] = Double.parseDouble(ss[3])/Constants.Bohr2Angs;
               xyz[1] = Double.parseDouble(ss[4])/Constants.Bohr2Angs;
               xyz[2] = Double.parseDouble(ss[5])/Constants.Bohr2Angs;

               molecule.getAtom(n).setXYZCoordinates(xyz);
               
               current_xyz[n] = xyz;

            }
            
         }

         if(line.indexOf("SCF Done") != -1) {
            String[] ss = Utilities.splitWithSpaceString(line);
            current_scf_energy = Double.parseDouble(ss[4]);
            scf_energy.add(Double.parseDouble(ss[4]));
         }
         
         if(line.indexOf("Excitation energies") != -1) {
            ArrayList<double[]> data = new ArrayList<double[]>();
            while ((line = br.readLine()).indexOf("Leave Link") == -1) {
               if(line.indexOf("Excited State") != -1) {
                  String[] ss = Utilities.splitWithSpaceString(line);
                  double[] dd = new double[2];
                  dd[0] = Double.parseDouble(ss[4])/Constants.Hartree2eV + current_scf_energy;
                  dd[1] = Double.parseDouble(ss[8].substring(2));
                  data.add(dd);
               }
            }
            double[][] e_and_f = data.toArray(new double[0][0]);
            td_info.add(e_and_f);
         }
         
         if(line.indexOf("Maximum Force") != -1){
            String[] s1 = Utilities.splitWithSpaceString(line);
            String[] s2 = Utilities.splitWithSpaceString(br.readLine());

            if(! force_isConv && s1[4].equals("YES") && s2[4].equals("YES")){
               force_isConv = true;
               niter = nn;
            }

            double[] ff = new double[2];
            if(s1[2].indexOf("*") == -1){
               ff[0] = Double.parseDouble(s1[2]);
            }else{
               ff[0] = 100.0d;
            }
            if(s2[2].indexOf("*") == -1){
               ff[1] = Double.parseDouble(s2[2]);
            }else{
               ff[1] = 100.0d;
            }
            forceList.add(ff);
            
            if(ff[1] < rmsforce){
               rmsforce = ff[1];
               if(! force_isConv) niter = nn;
            }
            
            nn++;
         }
         
         if(line.indexOf("Mulliken charges:") != -1 ){
            double[] current_charge = new double[Nat];

            br.readLine();
            for(int i=0; i<Nat; i++){
               String[] ss = Utilities.splitWithSpaceString(br.readLine());
               current_charge[i] = Double.parseDouble(ss[2]);
            }
            mulliken_charge.add(current_charge);
            
         }else if(line.indexOf("Mulliken charges and spin densities:") != -1){
            double[] current_charge = new double[Nat];
            double[] current_spin   = new double[Nat];
            
            br.readLine();
            for(int i=0; i<Nat; i++){
               String[] ss = Utilities.splitWithSpaceString(br.readLine());
               current_charge[i] = Double.parseDouble(ss[2]);
               current_spin[i]   = Double.parseDouble(ss[3]);
            }
            mulliken_charge.add(current_charge);
            mulliken_spin.add(current_spin);
         }

         if(line.indexOf("Normal termination") != -1){
            niter = nn-1;
            nstep = niter;
         }

         
      }
      
      br.close();
      
      // Remove the last duplicate structures
      traj_min.remove(traj_min.size()-1);
      return molecule;
      
   }
   
   /**
    * Returns the number of iteration for optimization
    * @return The number of iteration
    */
   public int getNumOfIteration() {
      return niter+1;
   }

   /**
    * Returns the molecule at the current step
    * @return the molecule
    */
   public Molecule getMolecule(){
      if(molecule != null){
         for(int n=0; n<molecule.getNat(); n++){
            molecule.getAtom(n).setXYZCoordinates(traj_min.get(nstep)[n]);
         }
      }
      return this.molecule;
   }
   
   /**
    * Returns the Mulliken charge at the last step
    * @return atomic charge (double[natom])
    */
   public double[] getCharge(){
      if(mulliken_charge.size() > 0){         
         return mulliken_charge.get(nstep);
      }else{
         return null;
      }
   }

   /**
    * Returns the spin density at the last step
    * @return spin density (double[natom])
    */
   public double[] getSpin(){
      if(mulliken_spin.size() > 0){
         return mulliken_spin.get(nstep);         
      }else{
         return null;
      }
   }
   
   /**
    * Returns the SCF energy at the current step
    * @return An arraylist of scf energy
    */
   public double getSCF_energy(){
      return scf_energy.get(nstep);
   }

   /**
    * Returns the information of TD-HF/DFT at the current step
    * @return double[nstate][energy, oscillator strength]
    */
   public double[][] getTD(){
      if(td_info.size() > 0) {
         return td_info.get(nstep);         
      }else {
         return null;
      }
   }

   /**
    * Returns the force at the current step
    * @return double[0]=maxforce, double[1]=rmsforce
    */
   public double[] getForce(){
      return forceList.get(nstep);
   }

   /**
    * Returns the molecule at the last step
    * @return the molecule
    */
   public Molecule getLastMolecule(){
      
      if(molecule != null){
         for(int n=0; n<molecule.getNat(); n++){
            molecule.getAtom(n).setXYZCoordinates(traj_min.get(niter)[n]);
         }         
      }
      return this.molecule;
   }
   
   /**
    * Returns the Mulliken charge at the last step
    * @return atomic charge (double[natom])
    */
   public double[] getLastCharge(){
      if(mulliken_charge.size() > 0){         
         return mulliken_charge.get(niter);
      }else{
         return null;
      }
   }

   /**
    * Returns the spin density at the last step
    * @return spin density (double[natom])
    */
   public double[] getLastSpin(){
      if(mulliken_spin.size() > 0){
         return mulliken_spin.get(niter);         
      }else{
         return null;
      }
   }
   
   /**
    * Returns the SCF energy at the last step
    * @return the SCF energy
    */
   public double getLastSCF_energy() {
      return scf_energy.get(niter);
   }
   
   /**
    * Returns the TD-HF/DFT information at the last step
    * @return the TD info
    */
   public double[][] getLastTD() {
      if(td_info.size() > 0) {
         return td_info.get(niter);
      }else {
         return null;
      }
   }

   /**
    * Returns the force at the last step
    * @return double[nstate][energy, oscillator strength]
    */
   public double[] getLastForce() {
      return forceList.get(niter);
   }
   
   /**
    * Returns the trajectory  
    * @return An arraylist of xyz coordinates (double[Nat][3])
    */
   public ArrayList<double[][]> getTrajectory(){
      return traj_min;
   }
   
   /**
    * Returns whether the optimization converged.
    * @return True when converged.
    */
   public boolean isConverged(){
      return force_isConv;
   }
   
   public void printStatus(){

      double[] ff = forceList.get(niter);
      double maxforce = ff[0];
      double rmsforce = ff[1];
      
      if(niter == 0){
         System.out.println("The optimization is terminated at the first geometry...");
         System.out.printf(" maxforce = %12.6f  (G09threshold=0.000450) \n",maxforce);
         System.out.printf(" rmsforce = %12.6f  (G09threshold=0.000300) \n",rmsforce);
      }else if(force_isConv){
         System.out.printf("The force is converged at the %3d-th step \n",niter+1);
         System.out.printf(" maxforce = %12.6f  (G09threshold=0.000450) \n",maxforce);
         System.out.printf(" rmsforce = %12.6f  (G09threshold=0.000300) \n",rmsforce);
      }else {
         System.out.printf("The next geometry is taken from the %3d-th step \n",niter+1);
         System.out.printf(" maxforce = %12.6f  (G09threshold=0.000450) \n",maxforce);
         System.out.printf(" rmsforce = %12.6f  (G09threshold=0.000300) \n",rmsforce);
      }
      

   }

}
