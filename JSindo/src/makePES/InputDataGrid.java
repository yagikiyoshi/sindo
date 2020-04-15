package makePES;

public class InputDataGrid {

   // GRID Data
   private String qcID;
   private int nGrid;
   private boolean fullmc;
   private String[] mc1,mc2,mc3;
   private String mopfile;
   private double thresh_MCS;
   private int MR;
   
   /**
    * This is a package private class
    */
   InputDataGrid(){
      thresh_MCS = -1.0f;
   }
   
   /**
    * Returns the ID of QC calc for this job
    * @return ID of QC calc.
    */
   public String getQcID() {
      return qcID;
   }
   /**
    * Sets the ID of QC calc for this job
    * @param grid_qcID ID of QC calc.
    */
   public void setQcID(String grid_qcID) {
      this.qcID = grid_qcID;
   }
   /**
    * Returns the order of mode coupling (1-3)
    * @return the order of mode coupling
    */
   public int getMR() {
      return this.MR;
   }

   /**
    * Sets the order of mode coupling (1-3)
    * @param MR the order of mode coupling
    */
   void setMR(int MR) {
      this.MR = MR;
   }

   /**
    * Returns the number of grid points
    * @return nGrid
    */
   public int getnGrid() {
      return nGrid;
   }
   /**
    * Sets the number of grid points
    * @param nGrid
    */
   void setnGrid(int nGrid) {
      this.nGrid = nGrid;
   }
   /**
    * True if full mode coupling is specified.
    * @return fullmc
    */
   public boolean isFullMC() {
      return fullmc;
   }
   /**
    * Sets the option for fullmc
    * @param fullmc input option
    */
   void setFullMC(boolean fullmc) {
      this.fullmc = fullmc;
   }
   /**
    * Returns the modes for 1MR
    * @return mr1[]
    */
   public String[] getMC1() {
      return mc1;
   }
   /**
    * Sets the modes for 1MR
    * @param mr1 1MR modes
    */
   void setMC1(String[] mr1) {
      this.mc1 = mr1;
   }
   /**
    * Returns the modes for 2MR
    * @return mr2[]
    */
   public String[] getMC2() {
      return mc2;
   }
   /**
    * Sets the modes for 2MR
    * @param mr2 2MR modes
    */
   void setMC2(String[] mr2) {
      this.mc2 = mr2;
   }
   /**
    * Returns the modes for 3MR
    * @return mr3[]
    */
   public String[] getMC3() {
      return mc3;
   }
   /**
    * Sets the modes for 3MR
    * @param mr3 3MR modes
    */
   void setMC3(String[] mr3) {
      this.mc3 = mr3;
   }
   /**
    * Returns the threshold value in MCS to generate grid potentials, 
    * or a negative value (-1) if MCS is not used.
    * @return threshold in cm-1. 
    */
   public double getThresh_MCS() {
      return thresh_MCS;
   }
   /**
    * Sets the threshold value in MCS to generate grid potentials
    * @param thresh_MCS 
    */
   void setThresh_MCS(double thresh_MCS) {
      this.thresh_MCS = thresh_MCS;
   }
   /**
    * Returns the name of mopfile
    * @return the name of mopfile (default = prop_no_1.mop)
    */
   public String getMopfile() {
      return mopfile;
   }
   /**
    * Sets the name of mopfile from which MCS strength is calculated.
    * @param mopfile
    */
   void setMopfile(String mopfile) {
      this.mopfile = mopfile;
   }
}
