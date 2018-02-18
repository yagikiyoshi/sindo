package qchem;

import java.io.*;
import java.util.*;

import sys.Constants;
import sys.PeriodicTable;
import sys.Utilities;

/**
 * Read the output of ACESII.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.2
 * @see OutputReader
 */
public class OutputReaderACESII extends OutputReader {


   private BufferedReader brOutput = null;
   private int num_of_atom = 0;
   private double[] geometry = null;
   private double[] atomicNumber = null;
   private double[] mass = null;
   private String[] labels = null;
   
   /**
    * Constructor is not directly accessible. Use QuantChem class.
    */
   OutputReaderACESII(){
   }
   
   private void readMolecule(){
      try{
         brOutput = new BufferedReader(new FileReader(basename+".out"));
         String line=super.locate(brOutput, "X              Y              Z");
         brOutput.readLine();

         num_of_atom = 0;
         
         ArrayList<Double> geom = new ArrayList<Double>(); 
         ArrayList<Double> anum = new ArrayList<Double>(); 
         ArrayList<String> lbl = new ArrayList<String>();
         
         line = brOutput.readLine();
         while(line.indexOf("---------") == -1){
            num_of_atom++;
            lbl.add(line.substring(1, 6).trim());
            anum.add(Double.parseDouble(line.substring(7, 16)));
            geom.add(Double.parseDouble(line.substring(17, 35)));
            geom.add(Double.parseDouble(line.substring(36, 50)));
            geom.add(Double.parseDouble(line.substring(51, 65)));         
            line = brOutput.readLine();            
         }

         brOutput.close();
         
         geometry = new double[geom.size()];
         for(int i=0; i<geom.size(); i++){
            geometry[i] = geom.get(i)/Constants.Bohr2Angs;
         }
         
         atomicNumber = new double[anum.size()];
         for(int i=0; i<anum.size(); i++){
            atomicNumber[i] = anum.get(i);
         }
         labels = new String[lbl.size()];
         lbl.toArray(labels);
         
         mass = new double[num_of_atom];
         for(int i=0; i<num_of_atom; i++){
            mass[i] = PeriodicTable.mass[(int)atomicNumber[i]][0]/Constants.Emu2Amu;
         }
         
      }catch(IOException e){
         super.printError(e.getMessage());
      }
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
      double energy = 0.0d;
      try{
         brOutput = new BufferedReader(new FileReader(basename+".out"));
         
         String line=super.locate(brOutput, "ASV");
         line=super.locate(brOutput, "CALCLEVEL");
         int level = Integer.parseInt((line.substring(38, 48).trim()));

         if(level == 10){
            // CCSD
            line=super.locate(brOutput, "CCSD        energy is");
            if(line != null){
               energy = Double.parseDouble(line.substring(37, 57)); 
            }else{
               // return Not A Number if energy is not found
               energy = Double.NaN;
            } 
         }else if(level == 22){
            // CCSD(T)
            line=super.locate(brOutput, "CCSD(T)        =");
            if(line != null){
               energy = Double.parseDouble(line.substring(27, 47)); 
            }else{
               // return Not A Number if energy is not found
               energy = Double.NaN;
            } 
            
         }else{
            // return Not A Number if energy is not found
            energy = Double.NaN;
         }

         brOutput.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return energy;
   }

   public double readCharge() {
      double charge = 0;
      try{
         brOutput = new BufferedReader(new FileReader(basename+".out"));
         
         String line=super.locate(brOutput, "ASV");
         line=super.locate(brOutput, "CHARGE");
         charge = Double.parseDouble(line.substring(38, 48).trim());

         brOutput.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return charge;
   }

   public double readMultiplicity() {
      double multiplicity = 0;
      try{
         brOutput = new BufferedReader(new FileReader(basename+".out"));

         String line=super.locate(brOutput, "ASV");
         line=super.locate(brOutput, "MULTIPLICTY");
         multiplicity = Double.parseDouble(line.substring(38, 48).trim());

         brOutput.close();
      }catch(IOException e){
         super.printError(e.getMessage());
      }
      return multiplicity;
   }

   @Override
   public double[] readGradient() {
      File file = new File(basename+".GRD");
      if(! file.exists()){
         return null;
         
      }else{
         // TODO Read gradient from GRD file
         // The data in GRD is in reoriented coordinates when symmetry is used.
         // It has to be oriented back to the input coordinates?
         
      }
      return null;
   }

   @Override
   public double[] readHessian() {
      File file = new File(basename+".FCMINT");
      if(! file.exists()){
         return null;
         
      }else{
         
         double[] hessian = null;
         try{
            BufferedReader brFC = new BufferedReader(new FileReader(basename+".FCMINT"));
            ArrayList<Double> hess = new ArrayList<Double>();
            
            String line = brFC.readLine();
            while(line != null){
               double[] aa = Utilities.splitWithSpaceDouble(line);
               hess.add(aa[0]);
               hess.add(aa[1]);
               hess.add(aa[2]);               
               //hess.add(Double.parseDouble(line.substring(0, 18)));
               //hess.add(Double.parseDouble(line.substring(19, 37)));
               //hess.add(Double.parseDouble(line.substring(38, 56)));
               line = brFC.readLine();
            }
            brFC.close();
            
            int nat = (int)Math.sqrt((double)hess.size());
            
            hessian = new double[nat*(nat+1)/2];
            for(int i=0; i<nat; i++){
               for(int j=0; j<=i; j++){
                  hessian[i*(i+1)/2+j] = hess.get(i*nat+j);
               }
            }
            
         }catch(IOException e){
            super.printError(e.getMessage());
         }
         
         return hessian;
  
      }
   }

   @Override
   public double[] readDipoleMoment() {
      File file = new File(basename+".DIPOL");
      if(! file.exists()){
         return null;
         
      }else{
         
         double[] dipole = new double[3];
         try{
            BufferedReader brDIPOL = new BufferedReader(new FileReader(basename+".DIPOL"));
            String line = brDIPOL.readLine();
            dipole = Utilities.splitWithSpaceDouble(line);
            brDIPOL.close();
            
         }catch(IOException e){
            super.printError(e.getMessage());
         }
         
         return dipole;
  
      }
   }

   @Override
   public double[] readDipoleDerivative() {
      File file = new File(basename+".DIPDER");
      if(! file.exists()){
         return null;
         
      }else{
         if(num_of_atom ==0) {
            this.readMolecule();
         }
         double[] dipderv = new double[num_of_atom*3*3];
         try{
            BufferedReader brDIPDER = new BufferedReader(new FileReader(basename+".DIPDER"));
            String line = null;
            for(int xyz=0; xyz<3; xyz++){
               brDIPDER.readLine();
               for(int n=0; n<num_of_atom; n++){
                  line = brDIPDER.readLine();
                  double[] aa = Utilities.splitWithSpaceDouble(line);
                  dipderv[3*3*n+xyz] = aa[1];
                  dipderv[3*(3*n+1)+xyz] = aa[2];
                  dipderv[3*(3*n+2)+xyz] = aa[3];
               }
               
            }
            
            brDIPDER.close();
            
         }catch(IOException e){
            
         }
         return dipderv;
      }
   }

   @Override
   public double[] readGeometry() {
      if(num_of_atom ==0) this.readMolecule();
      return geometry;
   }

   @Override
   public double[] readAtomicNumber() {
      if(num_of_atom ==0) this.readMolecule();
      return atomicNumber;
   }

   @Override
   public double[] readAtomicMass() {
      if(num_of_atom ==0) this.readMolecule();
      return mass;
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
      if(num_of_atom ==0) this.readMolecule();
      return labels;
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
