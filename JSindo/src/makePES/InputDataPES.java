package makePES;

import java.util.*;
//import sys.XMLHandler;
import molecule.*;

/**
 * (Package private) Manages the data used in MakePES
 * @author Kiyoshi Yagi
 * @version 1.3
 * @since Sindo 3.2
 */
public class InputDataPES {

   // General input parameters
   private Molecule molecule;
   private String minfofile;
   private boolean dipole;
   private VibTransformer transform;
   private int MR;
   private int[][] activeModes;

   private boolean runqchem;

   // QC Data
   private HashMap<String, InputDataQC>QCInfoMap;
   
   // QFF Data
   private ArrayList<InputDataQFF> qffInfoArray;
   
   // Grid Data
   private ArrayList<InputDataGrid> gridInfoArray;
   
   // Constant values
   public static String MINFO_FOLDER = "minfo.files/";
   
   /**
    * This is a package private class
    */
   InputDataPES(){
      QCInfoMap = new HashMap<String, InputDataQC>();
      qffInfoArray  = new ArrayList<InputDataQFF>();
      gridInfoArray = new ArrayList<InputDataGrid>();
   }

   /**
    * Returns the target molecule
    * @return the molecule
    */
   public Molecule getMolecule() {
      return molecule;
   }
   /**
    * Returns the name of minfo file
    * @return the name of minfo file
    */
   public String getMinfofile() {
      return minfofile;
   }
   /**
    * Returns whether or not to create Quantum Chemistry inputs. 
    * @return Generate input files when true, or .grdxyz file when false.
    */
//   public boolean isRunQchem() {
//      return runqchem;
//   }
   /**
    * Returns the mode representation
    * @return mode representation
    */
   public int getMR() {
      return MR;
   }
   /**
    * Returns the active modes
    * @return active modes[ndomain][nmodes]
    */
   public int[][] getActiveModes() {
      return activeModes;
   }
   /**
    * Returns the input option for dipole moment surface
    * @return if true, generate dipole moment surface 
    */
   public boolean isDipole() {
      return dipole;
   }
   /**
    * Returns the class to deal with transformation between normal and Cartesian coordinates 
    * @return Transform (q/x)
    */
   public VibTransformer getTransform(){
      return transform;
   }
   
   /**
    * Returns the setting of a QC calc.
    * @param index Index of the QC calc.
    * @return QCInfo
    */
   public InputDataQC getQCInfo(String index) {
      return QCInfoMap.get(index);
   }
   /**
    * Returns all keys of QC data
    * @return keys of QC data
    */
   public String[] getQCInfoKeySet() {
      return QCInfoMap.keySet().toArray(new String[0]);
   }
   /**
    * Returns the information of QFF calc. in an array.
    * @return
    */
   public ArrayList<InputDataQFF> getQFFInfoArray(){
      return qffInfoArray;
   }
   /**
    * Returns the information of GRID calc. in an array.
    * @return
    */
   public ArrayList<InputDataGrid> getGridInfoArray(){
      return gridInfoArray;
   }

   /**
    * Sets the target molecule
    * @param molecule the target molecule
    */
   void setMolecule(Molecule molecule) {
      this.molecule = molecule;
   }
   /**
    * Sets the name of minfo file
    * @param minfofile name of the minfo file
    */
   void setMinfofile(String minfofile) {
      this.minfofile = minfofile;
   }
   /**
    * Sets whether or not to create input files for Quantum Chemistry jobs.
    * @param qchem create input when true, create .xyz file when false
    */
//   void setRunQchem(boolean qchem) {
//      this.runqchem = qchem;
//   }
   /**
    * Sets the mode representation
    * @param mR mode representation
    */
   void setMR(int mR) {
      MR = mR;
   }
   /**
    * Sets the active modes
    * @param activeModes active modes
    */
   void setActiveModes(int[][] activeModes) {
      this.activeModes = activeModes;
   }
   /**
    * Sets the information of QC calc.
    * @param index The index of this QC calc.
    * @param qcinfo The information of QC calc.
    */
   void setQCInfo(String index, InputDataQC qcinfo) {
      this.QCInfoMap.put(index, qcinfo);
   }
   /**
    * Sets the input option for generating the dipole moment surface 
    * @param dipole generate dipole moment, if true.
    */
   void setDipole(boolean dipole) {
      this.dipole = dipole;
   }
   /**
    * Sets the class to deal with transformation between normal and Cartesian coordinates 
    * @param transform the object of Transform
    */
   void setTransform(VibTransformer transform){
      this.transform = transform;
   }
   /**
    * Sets the options for QFF calculation
    * @param qffinfo information of QFF calculations
    */
   void setQFFInfo(InputDataQFF qffinfo) {
      this.qffInfoArray.add(qffinfo);
   }
   /**
    * Sets the options for GRID calculation
    * @param gridinfo information of GRID calculations
    */
   void setGridInfo(InputDataGrid gridinfo) {
      this.gridInfoArray.add(gridinfo);
   }

   
}
