package molecule;

import java.io.*;
import java.util.ArrayList;
import qchem.*;
import atom.Atom;

/**
 * Reads the output file of quantum chemistry program and set the data to electronic data in Moleclue. <br>
 * The "read" method reads only the results of electronic structure calculations (energy, gradient, etc.), and 
 * not the information of atoms (species, coordinates) by default. The exception is when the molecule 
 * is not set or when the existing molecule don't have any atomic data.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 * @see OutputReader
 */
public class QChemToMol extends MolUtil {

   private OutputReader outputReader;
   
   private boolean isReadAtomicData = false;
   
   // By default, atomic charge is not read.
   private int atm_charge=-1;
   /**
    * Option not to read atomic charge
    */
   public static int NoAtmCharge = -1;
   /**
    * Option to read Mulliken charge
    */
   public static int Mulliken = 0;
   /**
    * Option to read atomic charge from electrostatic potential
    */
   public static int ESP = 1;
   /**
    * Option to read atomic charge from natural population analysis
    */
   public static int NPA = 2;

   /**
    * Constructs the object of MoleculeReader
    */
   public QChemToMol(){
   }
   /**
    * Constructs MoleculeReader with outputReader.
    * @param outputReader OutputReader from which the data is provided.
    */
   public QChemToMol(OutputReader outputReader){
      this.outputReader = outputReader;
   }
   /**
    * Append OutputReader 
    * @param outputReader OutputReader from which the data is provided.
    */
   public void appendReader(OutputReader outputReader){
      this.outputReader = outputReader;
   }
   
   /**
    * Checks the Output file. --- Why not during the read process?
    * @throws FileNotFoundException when the file doesn't exists
    * @throws OutputFileException when the output contains error data
    */
   public void check() throws FileNotFoundException, OutputFileException{
      outputReader.checkFile();
   }
   /**
    * Set which type of atomic charge is read. The option is <br>
    * <ol>
    * <li>MoleculeReader.NoAtmCharge : Turn off reading atomic charge </li>
    * <li>MoleculeReader.Mulliken : Mulliken charge </li>
    * <li>MoleculeReader.ESP : Atomic charge from electrostatic potential </li>
    * <li>MoleculeReader.NPA : Atomic charge from natrual population analysis </li>
    * </ol>
    * @param typeOfCharge One of the above
    */
   public void setCharge(int typeOfCharge){
      this.atm_charge = typeOfCharge;
   }
   /**
    * Force to read atomic data when true.
    * @param readAtomicData the option to force read the atomic data
    */
   public void setReadAtomicData(boolean readAtomicData){
      this.isReadAtomicData = readAtomicData;
   }
   /**
    * Reads the data of the output into Molecule. This method overwrites the existing electronic data.
    * The atomic data is optionally read when (1) the molecule is unset, (2) the molecule doesn't have it, or 
    * (3) forced by option (readAtomicData). 
    * @return Molecule with data
    */
   public Molecule read(){
      
      if(molecule == null){
         molecule = new Molecule();
         this.readAtomicData();
      }else if(molecule.getAtomList().size() == 0){
         this.readAtomicData();
      }else if(this.isReadAtomicData){
         //molecule.deleteAllAtoms();
         molecule.getAtomList().clear();
         this.readAtomicData();
      }
      
      ElectronicData edata = new ElectronicData();
      edata.setEnergy(outputReader.readEnergy());
      edata.setCharge(outputReader.readCharge());
      edata.setmultiplicity(outputReader.readMultiplicity());
      edata.setGradient(outputReader.readGradient());
      edata.setHessian(outputReader.readHessian());
      edata.setDipole(outputReader.readDipoleMoment());
      edata.setDipoleDerivative(outputReader.readDipoleDerivative());
      edata.setPolarizability(outputReader.readPolarizability());
      edata.setPolarizabilityDerivative(outputReader.readPolarizabilityDerivative());
      edata.setHyperPolarizability(outputReader.readHyperPolarizablity());
      
      molecule.setElectronicData(edata);
      molecule.setTitle(outputReader.readTitle());
      
      return molecule;
      
   }
   
   private void readAtomicData(){
      
      double[] atomicNum = outputReader.readAtomicNumber();
      if(atomicNum == null){
         System.out.println("Warning - Warning - Warning - Warning - Warning - Warning");
         System.out.println("While reading " + outputReader.getBasename() + ":");
         System.out.println("Atomic Number was not provided. ElectronicDataReader continues without");
         System.out.println("reading the atomic data.");
         return;
      }
      double[] cart = outputReader.readGeometry();
      ArrayList<Atom> atomList = molecule.getAtomList();
      for(int i=0; i<atomicNum.length; i++){
         Atom atmi = new Atom((int)atomicNum[i]);
         double[] xyz = new double[3];
         for(int j=0; j<3; j++){
            xyz[j] = cart[i*3+j];
         }
         atmi.setXYZCoordinates(xyz);
         //molecule.addAtom(atmi);
         atomList.add(atmi);
      }
      double[] mass = outputReader.readAtomicMass();
      if(mass != null){
         for(int i=0; i<mass.length; i++){
            //molecule.getAtom(i).setMass(mass[i]);
            atomList.get(i).setMass(mass[i]);
         }
      }
      double[] atomicCharge=null;
      if(atm_charge == QChemToMol.Mulliken){
         atomicCharge = outputReader.readMullikenCharge();
      }else if(atm_charge == QChemToMol.ESP){
         atomicCharge = outputReader.readESPCharge();
      }else if(atm_charge == QChemToMol.NPA){
         atomicCharge = outputReader.readNPACharge();
      }
      if(atomicCharge != null){
         for(int i=0; i<atomicCharge.length; i++){
            //molecule.getAtom(i).setAtomicCharge(atomicCharge[i]);
            atomList.get(i).setAtomicCharge(atomicCharge[i]);
         }
      }
      String[] label = outputReader.readLabel();
      if(label != null){
         for(int i=0; i<label.length; i++){
            //molecule.getAtom(i).setLabel(label[i]);
            atomList.get(i).setLabel(label[i]);
         }         
      }

   }
}
