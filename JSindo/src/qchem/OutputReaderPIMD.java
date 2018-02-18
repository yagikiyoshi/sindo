package qchem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import sys.Utilities;

/**
 * Read the output of PIMD.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 * @see OutputReader
 */
public class OutputReaderPIMD extends OutputReader {

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   OutputReaderPIMD(){
      
   }

   @Override
   public void checkFile() throws FileNotFoundException, OutputFileException {
      File file = new File(basename+"-forces.out");
      if(! file.exists()){
         throw new FileNotFoundException(basename + "-forces.out is not found.");
      }
      
      double energy = this.readEnergy();
      if(Double.isNaN(energy)){
         throw new OutputFileException(basename + "-forces.out has an error. The energy is not found.");
      }

   }

   @Override
   public double readEnergy() {
      double energy = 0.0d;
      try{
         // Total Energy
         BufferedReader brforces = new BufferedReader(new FileReader(basename+"-forces.out"));
         String line=brforces.readLine();
         if(line != null){
            line = line.replaceAll("D", "E");
            energy = Double.parseDouble(line.trim());
         }else{
            // return Not A Number if energy is not found
            energy = Double.NaN;
         }
         brforces.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return energy;
   }

   @Override
   public double readCharge() {
      return 0;
   }

   @Override
   public double readMultiplicity() {
      return 0;
   }

   @Override
   //TODO 
   public double[] readGradient() {
      return null;
   }

   @Override
   public double[] readHessian() {
      return null;
   }

   @Override
   public double[] readDipoleMoment() {
      double[] dipole = null;
      try{
         // Total Energy
         BufferedReader brforces = new BufferedReader(new FileReader(basename+"-dipole.out"));
         String line=brforces.readLine();
         if(line != null){
            String[] data = Utilities.splitWithSpaceString(line);
            dipole = new double[3];
            for(int i=0; i<3; i++){
               dipole[i] = Double.parseDouble(data[i+1]);
            }
         }
         brforces.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return dipole;
   }

   @Override
   public double[] readDipoleDerivative() {
      return null;
   }

   @Override
   public double[] readGeometry() {
      double[] geometry = null;
      
      try{
         BufferedReader brPIMD = new BufferedReader(new FileReader(basename+"-sindo.out"));
         String line=brPIMD.readLine();
         if(line != null){
            int Nat = Integer.parseInt(line.trim());
            geometry = new double[Nat*3];
            int nn=0;
            for(int i=0; i<Nat; i++){
               line = brPIMD.readLine();
               String[] data = Utilities.splitWithSpaceString(line);
               geometry[nn] = Double.parseDouble(data[3]);
               geometry[nn+1] = Double.parseDouble(data[4]);
               geometry[nn+2] = Double.parseDouble(data[5]);
               nn = nn+3;
            }
         }
         brPIMD.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return geometry;
   }

   @Override
   public double[] readAtomicNumber() {
      double[] atomicNumber = null;
      
      try{
         BufferedReader brPIMD = new BufferedReader(new FileReader(basename+"-sindo.out"));
         String line=brPIMD.readLine();
         if(line != null){
            int Nat = Integer.parseInt(line.trim());
            atomicNumber = new double[Nat];
            for(int i=0; i<Nat; i++){
               line = brPIMD.readLine();
               String[] data = Utilities.splitWithSpaceString(line);
               atomicNumber[i] = Double.parseDouble(data[1]);
            }
         }
         brPIMD.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return atomicNumber;
   }

   @Override
   public double[] readAtomicMass() {
      double[] atomicMass = null;
      
      try{
         BufferedReader brPIMD = new BufferedReader(new FileReader(basename+"-sindo.out"));
         String line=brPIMD.readLine();
         if(line != null){
            int Nat = Integer.parseInt(line.trim());
            atomicMass = new double[Nat];
            for(int i=0; i<Nat; i++){
               line = brPIMD.readLine();
               String[] data = Utilities.splitWithSpaceString(line);
               atomicMass[i] = Double.parseDouble(data[2]);
            }
         }
         brPIMD.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return atomicMass;
   }

   @Override
   public double[] readMullikenCharge() {
      return null;
   }

   @Override
   public double[] readESPCharge() {
      return null;
   }

   @Override
   public double[] readNPACharge() {
      return null;
   }

   @Override
   public String[] readLabel() {
      String[] label = null;
      
      try{
         BufferedReader brPIMD = new BufferedReader(new FileReader(basename+"-sindo.out"));
         String line=brPIMD.readLine();
         if(line != null){
            int Nat = Integer.parseInt(line.trim());
            label = new String[Nat];
            for(int i=0; i<Nat; i++){
               line = brPIMD.readLine();
               String[] data = Utilities.splitWithSpaceString(line);
               label[i] = data[0];
            }
         }
         brPIMD.close();         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return label;
   }

   @Override
   public double[] readPolarizability() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public double[] readPolarizabilityDerivative() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public double[] readHyperPolarizablity() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String readTitle() {
      // TODO Auto-generated method stub
      return null;
   }

}
