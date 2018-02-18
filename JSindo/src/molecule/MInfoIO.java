package molecule;

import java.io.*;
import java.util.*;

import atom.Atom;
import sys.*;

/**
 * Provides the classes to read and write ".minfo" files.
 * @author Kiyoshi Yagi
 * @version 1.5
 * @since Sindo 3.0
 */
public class MInfoIO extends MolUtil {
   
   private PrintStream ps = System.out;
   private PrintWriter pw;
   private boolean AtomData=true;
   private boolean ElecData=true;
   private boolean VibData=true;
   private int version=2;
   /**
    * Constructs the object of MInfoIO.
    */
   public MInfoIO(){
   }
   /**
    * Constructs the object of MInfoIO with a given Molecule
    * @param molecule the input Molecule object
    */
   public MInfoIO(Molecule molecule){
      super(molecule);
   }
   /**
    * Print the label, atomic number, mass, and xyz coordinates, and atomic charge if exists
    */
   public void printAtoms(){
      ArrayList<Atom> atoms = molecule.getAtomList();
      if(atoms.size() == 0) return;
      
      ps.println("[ Atomic Data ]");
      ps.println(atoms.size());
      for(int i=0; i<atoms.size(); i++){
         Atom ai = atoms.get(i);
         double[] xyz = ai.getXYZCoordinates();
         
         double mass=0.0;
         switch(version){
            case 1:
               mass = ai.getMass();
               break;
            case 2:
               mass = ai.getMass()*Constants.Emu2Amu;
               break;
         }
         ps.printf("%4s,  %4d, %12.4f,  %15.8f,  %15.8f,  %15.8f",
               ai.getLabel(),ai.getAtomicNum(),mass,xyz[0],xyz[1],xyz[2]);
         
         double charge = ai.getAtomicCharge();
         if(! Double.isNaN(charge)){
            ps.printf(",  %12.6f",charge);
         }
         ps.println();
      }
      
      ArrayList<Atom> subatoms = molecule.getSubAtomList();
      if(subatoms.size() > 0){
         ps.println(subatoms.size());
         for(int i=0; i<subatoms.size(); i++){
            Atom ai = subatoms.get(i);
            double[] xyz = ai.getXYZCoordinates();
            
            double mass=0.0;
            switch(version){
               case 1:
                  mass = ai.getMass();
                  break;
               case 2:
                  mass = ai.getMass()*Constants.Emu2Amu;
                  break;
            }
            ps.printf("%4s,  %4d, %12.4f,  %15.8f,  %15.8f,  %15.8f",
                  ai.getLabel(),ai.getAtomicNum(),mass,xyz[0],xyz[1],xyz[2]);
            
            double charge = ai.getAtomicCharge();
            if(! Double.isNaN(charge)){
               ps.printf(",  %12.6f",charge);
            }
            ps.println();
         }
      }
      
      ps.println();
   }
   /**
    * Print the information on the electronic structure (the energy, dipole, etc.)
    */
   public void printElecStruct(){
      
      ElectronicData edata = molecule.getElectronicData();
      if(edata != null){
         double energy = edata.getEnergy();
         if(! Double.isNaN(energy)){
            ps.println("[ Electronic Data ]");
            ps.println("Energy");         
            ps.println(energy);         
         }
         double charge = edata.getCharge();
         if(! Double.isNaN(charge)){
            ps.println("Charge");
            ps.println(charge);
         }
         double multiplicity = edata.getMultiplicity();
         if(! Double.isNaN(multiplicity)){
            ps.println("Multiplicity");
            ps.println(multiplicity);
         }
         
         this.print(edata.getGradient(), "Gradient");
         this.print(edata.getHessian(), "Hessian");
         this.print(edata.getDipole(), "Dipole Moment");
         this.print(edata.getPolarizability(), "Polarizability");
         this.print(edata.getHyperPolarizability(), "HyperPolarizability");
         this.print(edata.getDipoleDerivative(), "Dipole Derivative");
         this.print(edata.getPolarizabilityDerivative(), "Polarizability Derivative");
         ps.println();
         
      }
   }
   /**
    * Print the information on the vibrational structure (the frequency, displacement vectors)
    */
   public void printVibration(){
      double[][] vec = null;
      int nDomain = molecule.getNumOfVibrationalData();      
      if(nDomain > 0){
         ps.println("[ Vibrational Data ]");
                  
         if(nDomain > 1){
            ps.println("Number of Domain");
            ps.printf("%15d \n", nDomain);
         }

         int mode = 1;
         for(int n=0; n<nDomain; n++){

            VibrationalData vdata = molecule.getVibrationalData(n);
            int[] idx = vdata.getAtomIndex();
            if(idx != null){
               ps.println("Domain "+(n+1));
               this.print(idx, "Atom Index");
            }
            ps.println(vdata.getCoordType());

            if(vdata.getOmegaT() != null){
               this.print(vdata.getOmegaT(), "Translational Frequency");
               ps.println("Translational vector");
               vec = vdata.getTransVector();
               for(int i=0; i<3; i++){
                  this.print(vec[i], "T "+(i+1));
               }
               
               this.print(vdata.getOmegaR(), "Rotational Frequency");
               ps.println("Rotational vector");
               vec = vdata.getRotVector();
               for(int i=0; i<vdata.Nrot; i++){
                  this.print(vec[i], "R "+(i+1));
               }
               
            }

            this.print(vdata.getOmegaV(), "Vibrational Frequency");
            ps.println("Vibrational vector");
            vec = vdata.getVibVector();
            for(int i=0; i<vdata.Nfree; i++){
               this.print(vec[i], "Mode "+mode);
               mode++;
            }
         }
      }

   }

   private void print(double[] vec, String title){
      if(vec != null){
         ps.println(title);
         ps.println(vec.length);
         for(int i=0; i<vec.length-1; i++){
            if(i%5==4) {
               ps.printf("%15.8e ", vec[i]);
               ps.println();
            }else {
               ps.printf("%15.8e, ", vec[i]);               
            }
         }
         ps.printf("%15.8e ", vec[vec.length-1]);
         ps.println();
      }
      
   }
   
   private void print(int[] vec, String title){
      if(vec != null){
         ps.println(title);
         ps.println(vec.length);
         for(int i=0; i<vec.length-1; i++){
            if(i%5==4) {
               ps.printf("%15d ", vec[i]);
               ps.println();
            }else {
               ps.printf("%15d, ", vec[i]);               
            }
         }
         ps.printf("%15d ", vec[vec.length-1]);
         ps.println();
      }
      
   }

   /**
    * Dump the data to the file with filename.
    * @param filename The name of the data file.
    * @throws IOException If the file cannot be opened.
    */
   public void dumpMOL(String filename) throws IOException {
      pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
      pw.println("# minfo File version "+version+":");
      pw.println("#");
      if(AtomData) this.dumpAtoms();
      if(ElecData) this.dumpElecStruct();
      if(VibData) this.dumpVibration();
      pw.close();
   }
   private void dumpAtoms(){
      ArrayList<Atom> atoms = molecule.getAtomList();
      if(atoms.size() == 0) return;
      
      pw.println("[ Atomic Data ]");
      pw.println(atoms.size());
      for(int i=0; i<atoms.size(); i++){
         Atom ai = atoms.get(i);
         double[] xyz = ai.getXYZCoordinates();
         
         double mass=0.0;
         switch(version){
            case 1:
               mass = ai.getMass();
               break;
            case 2:
               mass = ai.getMass()*Constants.Emu2Amu;
               break;
         }
         pw.printf("%4s,  %4d, %12.4f,  %15.8f,  %15.8f,  %15.8f",
               ai.getLabel(),ai.getAtomicNum(),mass,xyz[0],xyz[1],xyz[2]);
         
         double charge = ai.getAtomicCharge();
         if(! Double.isNaN(charge)){
            pw.printf(",  %12.6f",charge);
         }
         pw.println();
      }
      
      ArrayList<Atom> subatoms = molecule.getSubAtomList();
      if(subatoms.size() > 0) {
         pw.println(subatoms.size());
         for(int i=0; i<subatoms.size(); i++){
            Atom ai = subatoms.get(i);
            double[] xyz = ai.getXYZCoordinates();
            
            double mass=0.0;
            switch(version){
               case 1:
                  mass = ai.getMass();
                  break;
               case 2:
                  mass = ai.getMass()*Constants.Emu2Amu;
                  break;
            }
            pw.printf("%4s,  %4d, %12.4f,  %15.8f,  %15.8f,  %15.8f",
                  ai.getLabel(),ai.getAtomicNum(),mass,xyz[0],xyz[1],xyz[2]);
            
            double charge = ai.getAtomicCharge();
            if(! Double.isNaN(charge)){
               pw.printf(",  %12.6f",charge);
            }
            pw.println();
         }

      }
      
      pw.println();
   }
   private void dumpElecStruct(){
      
      ElectronicData edata = molecule.getElectronicData();
      if(edata != null){
         double energy = edata.getEnergy();
         if(! Double.isNaN(energy)){
            pw.println("[ Electronic Data ]");
            pw.println("Energy");         
            pw.println(energy);         
         }
         double charge = edata.getCharge();
         if(! Double.isNaN(charge)){
            pw.println("Charge");
            pw.println(charge);
         }
         double multiplicity = edata.getMultiplicity();
         if(! Double.isNaN(multiplicity)){
            pw.println("Multiplicity");
            pw.println(multiplicity);
         }
         
         this.dump(edata.getGradient(), "Gradient");
         this.dump(edata.getHessian(), "Hessian");
         this.dump(edata.getDipole(), "Dipole Moment");
         this.dump(edata.getPolarizability(), "Polarizability");
         this.dump(edata.getHyperPolarizability(), "HyperPolarizability");
         this.dump(edata.getDipoleDerivative(), "Dipole Derivative");
         this.dump(edata.getPolarizabilityDerivative(), "Polarizability Derivative");
         pw.println();
         
      }
   }
   private void dumpVibration(){
      double[][] vec = null;
      int nDomain = molecule.getNumOfVibrationalData();
      if(nDomain > 0){
         pw.println("[ Vibrational Data ]");
         
         if(molecule.getVibrationalData(0).getAtomIndex() != null){
            pw.println("Number of Domain");
            pw.printf("%15d \n", nDomain);
         }

         int mode = 1;
         for(int n=0; n<nDomain; n++){
            
            VibrationalData vdata = molecule.getVibrationalData(n);
            if(vdata.getAtomIndex() != null){
               int[] idx = Utilities.deepCopy(vdata.getAtomIndex());
               for(int i=0; i<idx.length; i++){
                  idx[i] = idx[i]+1;
               }
               pw.println("Domain "+(n+1));
               this.dump(idx, "Atom Index");
            }
            pw.println(vdata.getCoordType());

            if(vdata.getOmegaT() != null){
               this.dump(vdata.getOmegaT(), "Translational Frequency");
               pw.println("Translational vector");
               vec = vdata.getTransVector();
               for(int i=0; i<3; i++){
                  this.dump(vec[i], "T "+(i+1));
               }
               
               this.dump(vdata.getOmegaR(), "Rotational Frequency");
               pw.println("Rotational vector");
               vec = vdata.getRotVector();
               for(int i=0; i<vdata.Nrot; i++){
                  this.dump(vec[i], "R "+(i+1));
               }
               
            }

            this.dump(vdata.getOmegaV(), "Vibrational Frequency");
            pw.println("Vibrational vector");
            vec = vdata.getVibVector();
            for(int i=0; i<vdata.Nfree; i++){
               this.dump(vec[i], "Mode "+mode);
               mode++;
            }
            
         }
         
      }   
   }
   private void dump(double[] vec, String title){
      if(vec != null){
         pw.println(title);
         pw.println(vec.length);
         for(int i=0; i<vec.length-1; i++){
            if(i%5==4) {
               pw.printf("%15.8e ", vec[i]);
               pw.println();
            }else {
               pw.printf("%15.8e, ", vec[i]);               
            }
         }
         pw.printf("%15.8e ", vec[vec.length-1]);
         pw.println();
      }
      
   }
   private void dump(int[] vec, String title){
      if(vec != null){
         pw.println(title);
         pw.println(vec.length);
         for(int i=0; i<vec.length-1; i++){
            if(i%5==4) {
               pw.printf("%15d ", vec[i]);
               pw.println();
            }else {
               pw.printf("%15d, ", vec[i]);               
            }
         }
         pw.printf("%15d ", vec[vec.length-1]);
         pw.println();
      }
      
   }
   /**
    * Load the data of molecule from the file with filename. This method overwrites the existing molecule.
    * @param filename The name of the data file
    * @return Molecule read from the file
    * @throws FileNotFoundException Thrown when the file doesn't exists.
    * @throws IOException Thrown if the file is empty.
    */
   public Molecule loadMOL(String filename) throws FileNotFoundException, IOException {
      
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String line;

      molecule = new Molecule();

      line = br.readLine();

      // Throw IOException if the file is empty
      if(line == null){
         br.close();
         throw new IOException("Error in loadMOL. The file "+filename+" does not have a proper format.");
      }
      
      // The comment lines
      
      // The default version is 2
      version = 2;
      line = line.trim();
      while(line.indexOf("#") == 0 || line.length() == 0){
         if(line.indexOf("version") > 0){
            version = Integer.parseInt(line.substring(21,22));
         }
         line = br.readLine().trim();
      }

      while(true){
         
         if(line.trim().equals("[ Atomic Data ]") && AtomData){
            //System.out.println("Atomic data");
            this.readAtoms(br);
         }else if(line.trim().equals("[ Electronic Data ]") && ElecData){
            //System.out.println("Electronic data");
            this.readElectronicData(br);
         }else if(line.trim().equals("[ Vibrational Data ]") && VibData){
            //System.out.println("Vibrational data");
            this.readVibrationalData(br);
         }
         line = br.readLine();
         if(line == null) {
            br.close();
            return molecule;
         }

      }
      
   }
   
   // Read atomic data from the reader
   private void readAtoms(BufferedReader br) throws IOException {

      String line;
      int Nat = Integer.parseInt(br.readLine().trim());
      ArrayList<Atom> atomList = molecule.getAtomList();
      for(int i=0; i<Nat; i++){
         line = br.readLine();
         String[] aa = line.split(",");
         Atom atom = new Atom(Integer.parseInt(aa[1].trim()));
         atom.setLabel(aa[0].trim());
         switch(version){
            case 1:
               atom.setMass(Double.parseDouble(aa[2].trim()));
               break;
            case 2:
               atom.setMass(Double.parseDouble(aa[2].trim())/Constants.Emu2Amu);
               break;
         }
         double[] xyz = {Double.parseDouble(aa[3].trim()), Double.parseDouble(aa[4].trim()), Double.parseDouble(aa[5].trim())};
         if(aa.length > 6){
            atom.setAtomicCharge(Double.parseDouble(aa[6].trim()));
         }
         atom.setXYZCoordinates(xyz);
         atom.setID(i);
         //molecule.addAtom(atom);
         atomList.add(atom);
      }
      
      line = br.readLine();
      if(line != null && line.trim().length() > 0){
         // read subatoms
         Nat = Integer.parseInt(line.trim());
         ArrayList<Atom> subatomList = molecule.getSubAtomList();
         for(int i=0; i<Nat; i++){
            line = br.readLine();
            String[] aa = line.split(",");
            Atom atom = new Atom(Integer.parseInt(aa[1].trim()));
            atom.setLabel(aa[0].trim());
            switch(version){
               case 1:
                  atom.setMass(Double.parseDouble(aa[2].trim()));
                  break;
               case 2:
                  atom.setMass(Double.parseDouble(aa[2].trim())/Constants.Emu2Amu);
                  break;
            }
            double[] xyz = {Double.parseDouble(aa[3].trim()), Double.parseDouble(aa[4].trim()), Double.parseDouble(aa[5].trim())};
            if(aa.length > 6){
               atom.setAtomicCharge(Double.parseDouble(aa[6].trim()));
            }
            atom.setXYZCoordinates(xyz);
            atom.setID(i);
            subatomList.add(atom);
         }
      }
      
   }
   
   // Read electronic data from the reader
   private void readElectronicData(BufferedReader br) throws IOException {
      
      String line;
      ElectronicData edata = new ElectronicData();

      // Energy
      br.readLine();
      edata.setEnergy(Double.parseDouble(br.readLine().trim()));
      
      while(! (line = br.readLine()).trim().isEmpty()){
         if(line.trim().equals("Charge")){
            edata.setCharge(Double.parseDouble(br.readLine().trim()));
         }else if(line.trim().equals("Multiplicity")){
            edata.setmultiplicity(Double.parseDouble(br.readLine().trim()));            
         }else if(line.trim().equals("Gradient")){
            edata.setGradient(Utilities.readData(br));
         }else if(line.trim().equals("Hessian")){
            edata.setHessian(Utilities.readData(br));
         }else if(line.trim().equals("Dipole Moment")){
            edata.setDipole(Utilities.readData(br));
         }else if(line.trim().equals("Polarizability")){
            edata.setPolarizability(Utilities.readData(br));
         }else if(line.trim().equals("HyperPolarizability")){
            edata.setHyperPolarizability(Utilities.readData(br));
         }else if(line.trim().equals("Dipole Derivative")){
            edata.setDipoleDerivative(Utilities.readData(br));
         }else if(line.trim().equals("Polarizability Derivative")){
            edata.setPolarizabilityDerivative(Utilities.readData(br));
         }
      }

      molecule.setElectronicData(edata);

   }
   
   // Read electronic data from the reader
   private void readVibrationalData(BufferedReader br) throws IOException {
      
      molecule.clearVibrationalData();
      
      String line = br.readLine();      
      if(line.indexOf("Domain")>=0){
         int nDomain = Integer.parseInt(br.readLine().trim());         
         for(int n=0; n<nDomain; n++){
            VibrationalData vdata = new VibrationalData();
            br.readLine();
            br.readLine();
            int[] atomIndex = Utilities.readIntData(br);
            for(int i=0; i<atomIndex.length; i++){
               atomIndex[i] = atomIndex[i]-1;
            }
            vdata.setAtomIndex(atomIndex);
            vdata.setCoordType(br.readLine().trim());
            this.readVibVectors(br, vdata);
            molecule.addVibrationalData(vdata);
            
         }
         
      }else{
         VibrationalData vdata = new VibrationalData();
         vdata.setCoordType(line.trim());
         this.readVibVectors(br, vdata);
         molecule.addVibrationalData(vdata);
      }
   }
   
   private void readVibVectors(BufferedReader br, VibrationalData vdata) throws IOException{

      String line;
      
      int tr = -1;
      line = br.readLine();
      if(line.indexOf("Trans")>=0){
         tr = 1;
         // Translation
         double[] omegat = Utilities.readData(br);
         vdata.setOmegaT(omegat);
         
         double[][] vect = new double[omegat.length][];
         
         br.readLine();
         for(int i=0; i<omegat.length; i++){
            br.readLine();
            vect[i] = Utilities.readData(br);
         }
         vdata.setTransVector(vect);

         // Rotation
         br.readLine();
         double[] omegar = Utilities.readData(br);
         vdata.setOmegaR(omegar);
         
         double[][] vecr = new double[omegar.length][];
         
         br.readLine();
         for(int i=0; i<omegar.length; i++){
            br.readLine();
            vecr[i] = Utilities.readData(br);
         }
         vdata.setRotVector(vecr);

      }
      
      // Vibration
      if(tr==1) br.readLine();
      double[] omegav = Utilities.readData(br);
      vdata.setOmegaV(omegav);
      
      double[][] vecv = new double[omegav.length][];
      
      br.readLine();
      for(int i=0; i<omegav.length; i++){
         br.readLine();
         vecv[i] = Utilities.readData(br);
      }
      vdata.setVibVector(vecv);
      

   }
   /**
    * Turns the read/write of Atomic data on/off. 
    * @param atomData the switch for the atom data
    */
   public void setAtomData(boolean atomData) {
      AtomData = atomData;
   }
   /**
    * Turns the read/write of Electronic data on/off.
    * @param elecData the switch for the electronic data
    */
   public void setElecData(boolean elecData) {
      ElecData = elecData;
   }
   /**
    * Turns the read/write of vibrational data on/off.
    * @param vibData the switch for the vibrational data
    */
   public void setVibData(boolean vibData) {
      VibData = vibData;
   }
   /**
    * Turns on the read/write of all data.
    */
   public void setAllData(){
      AtomData = true;
      ElecData = true;
      VibData = true;
   }
   /**
    * Turns off the read/write of all data.
    */
   public void unsetAllData(){
      AtomData = false;
      ElecData = false;
      VibData = false;
   }
   /**
    * Sets the version of minfo file
    * @param ver = 1 or 2
    */
   public void setVersion(int ver){
      version = ver;
   }
}
