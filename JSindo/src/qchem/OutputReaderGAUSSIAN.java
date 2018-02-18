package qchem;

import java.io.*;

import sys.*;

/**
 * Read the output of Gaussian.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 * @see OutputReader
 */
public class OutputReaderGAUSSIAN extends OutputReader {

   private BufferedReader brFchk = null;

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   OutputReaderGAUSSIAN(){
      
   }
   private int getSize(String line){
      return Integer.parseInt(line.substring(49, line.length()).trim());
   }
   private double[] readBlockDouble(BufferedReader br, int size){
      double[] dd = new double[size];
      int i=0;
      try{
         double[] ld = null;
         while(i<size){
            ld = Utilities.splitWithSpaceDouble(br.readLine());
            for(int j=0; j<ld.length; j++){
               dd[i] = ld[j];
               i++;
            }
         }
            
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      return dd;
   }
   
   public void checkFile() throws FileNotFoundException, OutputFileException {
      File file = new File(basename+".fchk");
      if(! file.exists()){
         throw new FileNotFoundException(basename + ".fchk is not found.");
      }
      
      double energy = this.readEnergy();
      if(Double.isNaN(energy)){
         throw new OutputFileException(basename + ".fchk has an error. The energy is not found.");
      }
   }
   public double readEnergy(){
      
      double energy = 0.0d;
      try{
         // Total Energy
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Total Energy");
         if(line != null){
            energy = Double.parseDouble(line.substring(44, line.length()).trim());
         }else{
            // return Not A Number if energy is not found
            energy = Double.NaN;
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return energy;
      /*
      double[] energy = new double[9];
      try{
         // Total Energy
         brFchk = new BufferedReader(new FileReader(file));
         String line=super.locate(brFchk, "Total Energy");
         if(line != null){
            energy[0] = Double.parseDouble(line.substring(44, line.length()).trim());
         }else{
            throw new OutputFileException("Energy not found in the fchk file.");
         }
         brFchk.close();
         
         // SCF Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "SCF Energy");
         if(line != null){
            energy[1] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();
         
         // MP2 Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "MP2 Energy");
         if(line != null){
            energy[2] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();

         // MP3 Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "MP3 Energy");
         if(line != null){
            energy[3] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();

         // MP4D Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "MP4D Energy");
         if(line != null){
            energy[4] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();

         // MP4DQ Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "MP4DQ Energy");
         if(line != null){
            energy[5] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();

         // MP4SDQ Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "MP4SDQ Energy");
         if(line != null){
            energy[6] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();

         // Coupled Cluster Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "Cluster Energy");
         if(line != null){
            energy[7] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();
         
         // CCSD(T) Energy
         brFchk = new BufferedReader(new FileReader(file));
         line=super.locate(brFchk, "Cluster Energy with triples");
         if(line != null){
            energy[8] = Double.parseDouble(line.substring(44, line.length()).trim());
         }
         brFchk.close();

      }catch(OutputFileException e){
         throw e;
      }catch(IOException e){
         // Some unexpected error while close()
         e.printStackTrace();
      }
      return energy;
      */
   }

   public double readCharge() {
      double charge = 0.0d;
      try{
         // Total charge
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Charge");
         if(line != null){
            charge = Double.parseDouble(line.substring(44, line.length()).trim());
         }else{
            // return Not A Number if energy is not found
            charge = Double.NaN;
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return charge;
   }
   public double readMultiplicity() {
      double multiplicity = 0.0d;
      try{
         // Total charge
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Multiplicity");
         if(line != null){
            multiplicity = Double.parseDouble(line.substring(44, line.length()).trim());
         }else{
            // return Not A Number if energy is not found
            multiplicity = Double.NaN;
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return multiplicity;
   }
   public double[] readGradient(){
      double[] gradient = null;
      
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Cartesian Gradient");
         if(line != null){
            int size = this.getSize(line);
            gradient = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return gradient;
   }

   public double[] readHessian(){
      double[] hessian = null;
      
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Cartesian Force Constants");
         if(line != null){
            int size = this.getSize(line);
            hessian = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return hessian;
   }

   public double[] readGeometry(){
      double[] geometry = null;
      
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Current cartesian coordinates");
         if(line != null){
            int size = this.getSize(line);
            geometry = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return geometry;
   }

   public double[] readAtomicNumber(){
      double[] atomicNumber = null;

      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Atomic numbers");
         if(line != null){
            int size = this.getSize(line);
            atomicNumber = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return atomicNumber;
   }
   
   public String[] readLabel(){
      double[] an = this.readAtomicNumber();
      String[] Label = new String[an.length];
      
      for(int n=0; n<an.length; n++){
         Label[n] = PeriodicTable.label[(int)an[n]];
      }
      return Label;
   }

   public double[] readAtomicMass(){
      double[] atomicMass = null;

      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line = super.locate(brFchk, "Real atomic weights");
         if(line != null){
            int size = getSize(line);
            atomicMass = this.readBlockDouble(brFchk, size);
            for(int i=0; i<atomicMass.length; i++){
               atomicMass[i] = atomicMass[i]/Constants.Emu2Amu;
            }
         }
         brFchk.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return atomicMass;
   }
   public double[] readMullikenCharge(){
      return this.getCharge("Mulliken");
   }
   public double[] readESPCharge(){
      return this.getCharge("ESP");
   }
   public double[] readNPACharge(){
      return this.getCharge("NPA");
   }
   private double[] getCharge(String type){
      double[] charge = null;
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, type);
         if(line != null){
            int size = getSize(line);
            charge = this.readBlockDouble(brFchk,size);
         }
         brFchk.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return charge;
   }

   public double[] readDipoleMoment(){
      double[] dipoleMoment = null;

      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line = super.locate(brFchk, "Dipole Moment");
         if(line != null){
            int size = getSize(line);
            dipoleMoment = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return dipoleMoment;
   }

   public double[] readDipoleDerivative(){
      double[] dipoleDerivative = null;

      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Dipole Derivatives");
         if(line != null){
            int size = getSize(line);
            dipoleDerivative = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return dipoleDerivative;
   }

   public double[] readPolarizability() {
      double[] polarizability = null;
      
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Polarizability");
         if(line != null){
            int size = getSize(line);
            polarizability = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return polarizability;
   }

   public double[] readPolarizabilityDerivative() {
      double[] polarderv = null;
      
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "Polarizability Derivatives");
         if(line != null){
            int size = getSize(line);
            polarderv = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return polarderv;
   }

   public double[] readHyperPolarizablity() {
      double[] hyperpolar = null;
      
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         String line=super.locate(brFchk, "HyperPolarizability");
         if(line != null){
            int size = getSize(line);
            hyperpolar = this.readBlockDouble(brFchk, size);
         }
         brFchk.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return hyperpolar;
      
   }
   @Override
   public String readTitle() {
      String title = null;
      try{
         brFchk = new BufferedReader(new FileReader(basename+".fchk"));
         title = brFchk.readLine();
      }catch(IOException e){
         super.printError(e.getMessage());         
      }
      return title;
   }
}
