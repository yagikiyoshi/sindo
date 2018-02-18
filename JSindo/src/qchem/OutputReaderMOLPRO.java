package qchem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import sys.Constants;
import sys.PeriodicTable;
import sys.Utilities;

/**
 * Read the output of MOLPRO.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 * @see OutputReader
 */
public class OutputReaderMOLPRO extends OutputReader {

   private BufferedReader brOut = null;

   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   OutputReaderMOLPRO(){
      
   }
   
   public void checkFile() throws FileNotFoundException, OutputFileException {
      File file = new File(basename+".out");
      if(! file.exists()){
         throw new FileNotFoundException(basename + ".out is not found.");
      }
      
      double energy = this.readEnergy();
      if(Double.isNaN(energy)){
         throw new OutputFileException(basename + ".out has an error. The energy is not found.");
      }
   }

   public double readEnergy() {
      double energy = Double.NaN;
      try{

         String line = null;

         brOut = new BufferedReader(new FileReader(basename+".out"));
         while((line = brOut.readLine()) != null){
            
            //KS or HF energy
            if(line.indexOf("STATE 1.1 Energy") > 0){
               energy = Double.parseDouble(line.substring(24, line.length()).trim());
            }
            
            // MP2 energy 
            if(line.indexOf("MP2 total energy") > 0){
               energy = Double.parseDouble(line.substring(30, line.length()).trim());               
            }
            
            // QCISD energy
            if(line.indexOf("QCISD total energy") > 0){
               energy = Double.parseDouble(line.substring(30, line.length()).trim());               
            }
                        
            // QCISD(T) energy
            if(line.indexOf("QCISD(T) total energy") > 0){
               energy = Double.parseDouble(line.substring(30, line.length()).trim());               
            }
            
            // Geometry optimization 
            if(line.indexOf("ITER.   ENERGY(OLD)    ENERGY(NEW)") > 0){
               while((line = brOut.readLine()).length() > 0){
                  double[] dd = Utilities.splitWithSpaceDouble(line);
                  energy = dd[2];
               }
               break;
            }
         }
         
         brOut.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return energy;
   }

   public double readCharge() {
      // TODO Where is the charge written?
      return Double.NaN;
   }

   public double readMultiplicity() {
      // TODO Where is the multiplicity written?
      return Double.NaN;
   }
   
   public double[] readGradient() {
      // TODO Auto-generated method stub
      return null;
   }

   public double[] readHessian() {
      
      double[] hessian = null;
      
      try{
         brOut = new BufferedReader(new FileReader(basename+".out"));
         
         int Nat = 0;
         String line=super.locate(brOut, "ATOMIC COORDINATES");
         brOut.readLine();
         brOut.readLine();
         brOut.readLine();
         while((line = brOut.readLine()).length() > 0){
            Nat++;
         }
         
         line=super.locate(brOut, "Force Constants");
         if(line != null){
            
            int Nat3 = Nat*3;
            int nCol = 5;

            int nn = 0;
            int mod = Nat3%nCol;
            if(mod == 0){
               nn = Nat3/nCol;
            }else{
               nn = (Nat3-mod)/nCol + 1;
            }

            hessian = new double[Nat3*Nat3];
            double[][] hess2 = new double[Nat3][Nat3];

            for(int n=0; n<nn; n++){
               
               brOut.readLine();
               for(int i=n*nCol; i<Nat3; i++){
                  String[] aa = Utilities.splitWithSpaceString(brOut.readLine());
                  int jj = 0;
                  for(int j=n*nCol; j<n*nCol+aa.length-1; j++){
                     hess2[i][j] = Double.parseDouble(aa[jj+1]);
                     jj++;
                  }
               }
               
            }
            
            int k=0;
            for(int i=0; i<Nat3; i++){
               for(int j=0; j<Nat3; j++){
                  hessian[k] = hess2[i][j];
                  k++;
               }
            }

         }
         
         brOut.close();
         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      
      return hessian;
   }

   public double[] readDipoleMoment() {
      double[] dipoleMoment = null;
      try{

         String line = null;
         
         brOut = new BufferedReader(new FileReader(basename+".out"));
         while((line = brOut.readLine()) != null){
            if(line.indexOf("STATE 1.1 Dipole") > 0){
               dipoleMoment = Utilities.splitWithSpaceDouble(line.substring(35, line.length()).trim());
            }
            
            if(line.indexOf("Permanent Dipole Moment") > 0){
               line = brOut.readLine();
               dipoleMoment = Utilities.splitWithSpaceDouble(line.substring(10, line.length()).trim());
               for(int i=0; i<dipoleMoment.length; i++){
                  dipoleMoment[i] = dipoleMoment[i]/2.541579997d;
               }
            }
   
         }

         brOut.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return dipoleMoment;
   }

   public double[] readDipoleDerivative() {
      double[] dipoleDerivative = null;

      try{
         brOut = new BufferedReader(new FileReader(basename+".out"));
         
         int Nat = 0;
         String line=super.locate(brOut, "ATOMIC COORDINATES");
         brOut.readLine();
         brOut.readLine();
         brOut.readLine();
         while((line = brOut.readLine()).length() > 0){
            Nat++;
         }
         
         line=super.locate(brOut, "Dipole Moment Derivatives");
         if(line != null){
            
            int Nat3 = Nat*3;
            int nCol = 8;

            int nn = 0;
            int mod = Nat3%nCol;
            if(mod == 0){
               nn = Nat3/nCol;
            }else{
               nn = (Nat3-mod)/nCol + 1;
            }

            double[][] dd = new double[3][Nat3];
            
            for(int n=0; n<nn; n++){
               brOut.readLine();
               for(int i=0; i<3; i++){
                  double[] ld = Utilities.splitWithSpaceDouble(brOut.readLine());
                  
                  int jj=0;
                  for(int j=n*nCol; j<n*nCol+ld.length-1; j++){
                     dd[i][j] = ld[jj+1];
                     jj++;
                  }
               }
            }

            dipoleDerivative = new double[Nat3*3];
            int k=0;
            for(int i=0; i<Nat3; i++){
               for(int j=0; j<3; j++){
                  // Debye/Angs -> au
                  dipoleDerivative[k] = dd[j][i]/2.541579997d*Constants.Bohr2Angs;
                  k++;
               }
            }
         }
         brOut.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      
      return dipoleDerivative;
   }

   @Override
   public double[] readGeometry() {

      ArrayList<Double> geometry = new ArrayList<Double>();
      
      try{
         brOut = new BufferedReader(new FileReader(basename+".out"));
         super.locate(brOut, "ATOMIC COORDINATES");
         brOut.readLine();
         brOut.readLine();
         brOut.readLine();
         String[] aa = null;
         while((aa = Utilities.splitWithSpaceString(brOut.readLine())).length > 0){
            
            geometry.add(Double.parseDouble(aa[3]));
            geometry.add(Double.parseDouble(aa[4]));
            geometry.add(Double.parseDouble(aa[5]));
            
         }

         if(super.locate(brOut, "Atomic Coordinates") != null){
            geometry = new ArrayList<Double>();
            
            brOut.readLine();
            brOut.readLine();
            brOut.readLine();
            while((aa = Utilities.splitWithSpaceString(brOut.readLine())).length > 0){
               
               geometry.add(Double.parseDouble(aa[3]));
               geometry.add(Double.parseDouble(aa[4]));
               geometry.add(Double.parseDouble(aa[5]));
               
            }
         }

         brOut.close();
         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      
      double[] gg =  new double[geometry.size()];
      for(int i=0; i<geometry.size(); i++){
         gg[i] = geometry.get(i);
      }
      
      return gg;
      
   }

   @Override
   public double[] readAtomicNumber() {
      String[] label = this.readLabel();
      double[] atomicNumber = new double[label.length];
      
      for(int n=0; n<label.length; n++){
         atomicNumber[n] = (double)PeriodicTable.getAtomicNumber(label[n]);
      }
      return atomicNumber;
   }

   @Override
   public double[] readAtomicMass() {
      
      ArrayList<Double> mass = new ArrayList<Double>();
      
      try{
         brOut = new BufferedReader(new FileReader(basename+".out"));
         if(super.locate(brOut, "Atomic Masses") == null){
            brOut.close();
            return null;
         }
         
         String[] aa = null;
         while((aa = Utilities.splitWithSpaceString(brOut.readLine())).length > 0){
            
            for(int n=2; n<aa.length; n++){
               mass.add(Double.parseDouble(aa[n]));
            }
            
         }

         brOut.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      
      double[] mm = new double[mass.size()];
      for(int n=0; n<mass.size(); n++){
         mm[n] = mass.get(n)/Constants.Emu2Amu;
      }
      
      return mm;
   }

   @Override
   public double[] readMullikenCharge() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public double[] readESPCharge() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public double[] readNPACharge() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String[] readLabel() {
      
      ArrayList<String> label = new ArrayList<String>();
      
      try{
         brOut = new BufferedReader(new FileReader(basename+".out"));
         super.locate(brOut, "ATOMIC COORDINATES");
         brOut.readLine();
         brOut.readLine();
         brOut.readLine();
         String[] aa = null;
         while((aa = Utilities.splitWithSpaceString(brOut.readLine())).length > 0){
            
            label.add(aa[1]);
            
         }

         brOut.close();
         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      
      return label.toArray(new String[0]);
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
