package makePES;

import sys.XMLHandler;
import molecule.*;

/**
 * (Package private) Manages the data used in MakePES
 * @author Kiyoshi Yagi
 * @version 1.3
 * @since Sindo 3.2
 */
public class PESInputData {

   // General input parameters
   private String runType;
   private Molecule molecule;
   private String minfofile;
   private boolean removeFiles;
   private boolean runqchem;
   private String[] qchemTypes;
   private XMLHandler[] qchemInputs;
   private String[] titles;
   private boolean dipole;
   private boolean dryrun;
   private VibTransformer transform;
   private int MR;
   private int[][] activeModes;

   // QFF Data
   private double stepsize;
   private String ndifftype;
   private String mopfile;
   private boolean genhs;
   private String gradient_and_hessian;
   
   // GRID Data
   private int nGrid;
   private boolean fullmc;
   private String[] mc1,mc2,mc3;
   
   // HYBRID Data
   private double thresh_MCS;
   
   /**
    * This is a package private class
    */
   PESInputData(){
      
   }
   /**
    * Returns the type of calculation
    * @return the type (QFF/GRID)
    */
   public String getRunType(){
      return runType;
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
    * Returns whether or not to remove the output files of electronic structure calculations
    * @return Remove files if true
    */
   public boolean isRemoveFiles() {
      return removeFiles;
   }
   /**
    * Returns whether or not to create Quantum Chemistry inputs. 
    * @return Generate input files when true, or .grdxyz file when false.
    */
   public boolean isRunQchem() {
      return runqchem;
   }
   /**
    * Returns the mode representation
    * @return mode representation
    */
   public int getMR() {
      return MR;
   }
   /**
    * Returns the number of types of electronic structure program
    * @return the number of types
    */
   public int getNumOfQchemTypes(){
      return qchemTypes.length;
   }
   /**
    * Returns the type of electronic structure program
    * @param ID ID of the program in the order it appeared in makePES.xml 
    * @return the type of the program
    */
   public String getQchemTypes(int ID) {
      return qchemTypes[ID];
   }
   /**
    * Returns the input options for electronic structure calculation
    * @param ID ID of the program in the order it appeared in makePES.xml
    * @return the input options
    */
   public XMLHandler getQchemInputs(int ID) {
      return qchemInputs[ID];
   }
   /**
    * Returns the title of the electronic structure calculation
    * @param ID ID of the program in the order it appeared in makePES.xml
    * @return the title
    */
   public String getTitle(int ID) {
      return titles[ID];
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
    * Returns the stepsize for numerical differentiations (au)
    * @return the stepsize
    */
   public double getStepsize() {
      return stepsize;
   }
   /**
    * Returns the type of numerical differentiation 
    * @return either "ene", "grad", or "hess"
    */
   public String getNdifftype() {
      return ndifftype;
   }
   /**
    * Returns the name of mopfile
    * @return the name of mopfile (default = prop_no_1.mop)
    */
   public String getMopfile() {
      return mopfile;
   }
   /**
    * Returns if the generation of hs file is set
    * @return generate hs file if true (default = false)
    */
   public boolean isGenhs() {
      return genhs;
   }
   /**
    * Returns where the gradient and Hessian is retrieved from
    * @return "input" or "current"
    */
   public String getGradient_and_hessian() {
      return gradient_and_hessian;
   }
   /**
    * Returns the number of grid points
    * @return nGrid
    */
   public int getnGrid() {
      return nGrid;
   }
   public boolean isFullMC() {
      return fullmc;
   }
   public boolean isDryRun() {
      return dryrun;
   }
   /**
    * Returns the modes for 1MR
    * @return mr1[]
    */
   public String[] getMC1() {
      return mc1;
   }
   /**
    * Returns the modes for 2MR
    * @return mr2[]
    */
   public String[] getMC2() {
      return mc2;
   }
   /**
    * Returns the modes for 3MR
    * @return mr3[]
    */
   public String[] getMC3() {
      return mc3;
   }
   /**
    * Returns the threshold value in MCS to generate grid potentials
    * @return threshold in cm-1
    */
   public double getThresh_MCS() {
      return thresh_MCS;
   }
   /**
    * Returns the active modes
    * @return active modes
    */
   public int[][] getActiveModes() {
      return activeModes;
   }
   /**
    * Sets the type of calculation
    * @param runType the type of calculation (QFF/GRID)
    */
   void setRunType(String runType){
      this.runType = runType;
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
    * Sets whether or not to remove the output files of electronic structure calculations
    * @param removeFiles Remove files if true
    */
   void setRemoveFiles(boolean removeFiles) {
      this.removeFiles = removeFiles;
   }
   /**
    * Sets whether or not to create input files for Quantum Chemistry jobs.
    * @param qchem create input when true, create .grdxyz file when false
    */
   void setRunQchem(boolean qchem) {
      this.runqchem = qchem;
   }
   /**
    * Sets the mode representation
    * @param mR mode representation
    */
   void setMR(int mR) {
      MR = mR;
   }
   /**
    * Sets the type of electronic structure program
    * @param qchemTypes the type of the program
    */
   void setQchemTypes(String[] qchemTypes) {
      this.qchemTypes = qchemTypes;
   }
   /**
    * Sets the input options for electroic structure calculation
    * @param qchemInputs the input options
    */
   void setQchemInputs(XMLHandler[] qchemInputs) {
      this.qchemInputs = qchemInputs;
   }
   /**
    * Sets the title of the electronic structure calculation
    * @param title the title
    */
   void setTitle(String[] title) {
      this.titles = title;
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
    * Sets the stepsize for numerical differentiations (au)
    * @param stepsize the stepsize
    */
   void setStepsize(double stepsize) {
      this.stepsize = stepsize;
   }
   /**
    * Sets the type of numerical differentiations
    * @param ndifftype either "ene", "grad", or "hess"
    */
   void setNdifftype(String ndifftype) {
      this.ndifftype = ndifftype;
   }
   void setMopfile(String mopfile) {
      this.mopfile = mopfile;
   }
   void setGenhs(boolean genhs) {
      this.genhs = genhs;
   }
   void setGradient_and_hessian(String gradient_and_hessian) {
      this.gradient_and_hessian = gradient_and_hessian;
   }
   /**
    * Sets the number of grid points
    * @param nGrid
    */
   void setnGrid(int nGrid) {
      this.nGrid = nGrid;
   }
   void setFullMC(boolean fullmc) {
      this.fullmc = fullmc;
   }
   /**
    * Sets the modes for 1MR
    * @param mr1 1MR modes
    */
   void setMC1(String[] mr1) {
      this.mc1 = mr1;
   }
   /**
    * Sets the modes for 2MR
    * @param mr2 2MR modes
    */
   void setMC2(String[] mr2) {
      this.mc2 = mr2;
   }
   /**
    * Sets the modes for 3MR
    * @param mr3 3MR modes
    */
   void setMC3(String[] mr3) {
      this.mc3 = mr3;
   }
   /**
    * Sets the threshold value in MCS to generate grid potentials
    * @param thresh_MCS 
    */
   void setThresh_MCS(double thresh_MCS) {
      this.thresh_MCS = thresh_MCS;
   }
   /**
    * Sets the active modes
    * @param activeModes active modes
    */
   void setActiveModes(int[][] activeModes) {
      this.activeModes = activeModes;
   }
   /**
    * Sets the dryrun mode
    * @param dryrun dryrun if true
    */
   void setDryRun(boolean dryrun){
      this.dryrun = dryrun;
   }

   
}
