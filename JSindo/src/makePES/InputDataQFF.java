package makePES;

public class InputDataQFF {

   private String qcID;
   private double stepsize;
   private String ndifftype;
   private String mopfile;
   private boolean genhs;
   private boolean interdomain_hc;
   private String gradient_and_hessian;
   private int MR;
   
   /**
    * This is a package private class
    */
   InputDataQFF(){
      
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
    * @param qcID ID of QC calc.
    */
   public void setQcID(String qcID) {
      this.qcID = qcID;
   }
   
   /**
    * Returns the order of mode coupling (1-4)
    * @return the order of mode coupling
    */
   public int getMR() {
      return this.MR;
   }

   /**
    * Sets the order of mode coupling (1-4)
    * @param MR the order of mode coupling
    */
   void setMR(int MR) {
      this.MR = MR;
   }

   /**
    * Returns the stepsize for numerical differentiations (au)
    * @return the stepsize
    */
   public double getStepsize() {
      return stepsize;
   }
   /**
    * Sets the stepsize for numerical differentiations (au)
    * @param stepsize the stepsize
    */
   void setStepsize(double stepsize) {
      this.stepsize = stepsize;
   }
   /**
    * Returns the type of numerical differentiation 
    * @return either "ene", "grad", or "hess"
    */
   public String getNdifftype() {
      return ndifftype;
   }
   /**
    * Sets the type of numerical differentiations
    * @param ndifftype either "ene", "grad", or "hess"
    */
   void setNdifftype(String ndifftype) {
      this.ndifftype = ndifftype;
   }
   /**
    * Returns the name of mopfile
    * @return the name of mopfile
    */
   public String getMopfile() {
      return mopfile;
   }
   /**
    * Sets the output filename (.mop) where QFF coefficients are written.
    * @param mopfile name of mopfile
    */
   void setMopfile(String mopfile) {
      this.mopfile = mopfile;
   }
   /**
    * Generates hs file if true. 
    * @return genhs
    */
   public boolean isGenhs() {
      return genhs;
   }
   /**
    * Generates hs file if true.
    * @param genhs true or false
    */
   void setGenhs(boolean genhs) {
      this.genhs = genhs;
   }
   /**
    * Calculates interdomain harmonic coupling if true.
    * @return interdomain_hc
    */
   public boolean isInterdomain_hc() {
      return interdomain_hc;
   }

   /**
    * Calculates interdomain harmonic coupling if true.
    * @param interdomain_hc true or false
    */
   public void setInterdomain_hc(boolean interdomain_hc) {
      this.interdomain_hc = interdomain_hc;
   }

   /**
    * Returns where the gradient and Hessian is retrieved from
    * @return "input" or "current"
    */
   public String getGradient_and_hessian() {
      return gradient_and_hessian;
   }
   /**
    * Sets where the gradient and Hessian is retrieved from
    * @param gradient_and_hessian "input" or "current"
    */
   void setGradient_and_hessian(String gradient_and_hessian) {
      this.gradient_and_hessian = gradient_and_hessian;
   }

}
