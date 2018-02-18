package qchem;

import java.io.*;

/**
 * Read the output of electronic structure calculations. <br>
 * The instance of this class is generated through QuantChem class as, <br>
 * <pre>
 *    QuantChem qchem = new QuantChem();
 *    try{
 *       OutputReader or = qchem.getOutputReader("Gaussian");
 *    }catch(TypeNotSupportedException e){
 *       ...
 *    }
 * </pre>
 * The instance reads the data from an output file by, <br>
 * <pre>
 *    or.setBasename("h2o");
 *    try{
 *       or.checkFile();
 *    }catch(FileNotFoundException e){
 *       ...
 *    }
 *    double energy = or.readEnergy();
 *    double[] dipole = or.readDipoleMoment();
 * </pre>
 * The return value is NaN (for double) or null (for array) if the data is not found.
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public abstract class OutputReader {

   protected String basename;

   /**
    * Read the file until the keyword is found.
    * @param br the reader
    * @param key the keyword
    * @return the line where the keyword is located
    */
   protected String locate(BufferedReader br, String key){
      String line=null;
      try{
         line=br.readLine();
         while(line != null){
            if(line.indexOf(key) >= 0){
               break;
            }
            line = br.readLine();
         }         
      }catch(IOException e){
         printError(e.getMessage());
      }
      return line;
   }
   /**
    * Prints the error while reading the output file
    * @param message Message of the error.
    */
   protected void printError(String message){
      System.out.println("Error while reading " + basename);
      System.out.println(message);      
   }
   /**
    * Set the basename of the output file.
    * @param basename The basename of the output file
    */
   public void setBasename(String basename){
      this.basename = basename;
   }
   /**
    * Returns the basename of the output file
    * @return basename of the output file
    */
   public String getBasename(){
      return basename;
   }

   /**
    * Checks if the specified output file exists.
    * @throws FileNotFoundException If the file does not exist
    * @throws OutputFileException If the output file contains error
    */
   public abstract void checkFile() throws FileNotFoundException, OutputFileException;
   /**
    * Read the energy (NaN if not found).
    * @return the energy (Hartree)
    */
   public abstract double readEnergy();
   /**
    * Read the charge (NaN if not found)
    * @return the charge
    */
   public abstract double readCharge();
   /**
    * Read the multiplicity (NaN if not found)
    * @return the multiplicity
    */
   public abstract double readMultiplicity();
   /**
    * Read the gradient [Nat*3] (Hartree/bohr)
    * @return the gradient 
    */
   public abstract double[] readGradient();
   /** 
    * Read the down-half of the Hessian matrix [(Nat*3+1)*Nat*3/2] (Hartree/bohr^2)
    * @return the Hessian matrix  
    */
   public abstract double[] readHessian();
   /**
    * Read the dipole moment [3] (au)
    * @return dipole moment 
    */
   public abstract double[] readDipoleMoment();

   /**
    * Read the polarizability [xx,xy,yy,xz,yz,zz] (au)
    * @return polarizability
    */
   public abstract double[] readPolarizability();
   /**
    * Read the hyperpolarizability [xxx,xxy,xyy,yyy,xxz,xyz,yyz,xzz,yzz,zzz] (au)
    * @return hyperpolarizability
    */
   public abstract double[] readHyperPolarizablity();
   /**
    * Read the dipole derivative [3*Nat*3] (au)
    * @return dipole derivative
    */
   public abstract double[] readDipoleDerivative();

   /**
    * Read the polarizability derivative [6*Nat*3] (au)
    * @return polarizability derivative
    */
   public abstract double[] readPolarizabilityDerivative();
   
   /**
    * Read the geometry [Nat*3] (bohr)
    * @return the geometry 
    */
   public abstract double[] readGeometry();
   
   // Atomic number must be implemented since it is used to generate the Atoms in MoleculeReader.
   /**
    * Read the atomic number of each atom [Nat]
    * @return the atomic number 
    */
   public abstract double[] readAtomicNumber();
   /**
    * Read the mass of each atom [Nat] (emu)
    * @return the mass
    */
   public abstract double[] readAtomicMass();
   /**
    * Read the Mulliken atomic charge [Nat]
    * @return Mulliken charge
    */
   public abstract double[] readMullikenCharge();
   /**
    * Read the atomic charge derived from the electrostatic potential [Nat]
    * @return ESP charge
    */
   public abstract double[] readESPCharge();
   /**
    * Read the atomic charge obtained from natural population analysis [Nat]
    * @return NPA charge
    */
   public abstract double[] readNPACharge();
   /**
    * Read the label of each atom [Nat]
    * @return label
    */
   public abstract String[] readLabel();
   /**
    * Read the title of the job
    * @return title
    */
   public abstract String readTitle();
}
