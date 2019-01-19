package makePES;

public class InputDataQFF {

   private String qcID;
   private double stepsize;
   private String ndifftype;
   private String mopfile;
   private boolean genhs;
   private String gradient_and_hessian;
   
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
    * @param qcID index of QC calc.
    */
   public void setQcID(String qcID) {
      this.qcID = qcID;
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
    * @return the name of mopfile (default = prop_no_1.mop)
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
    * Returns if the generation of hs file is set
    * @return generate hs file if true (default = false)
    */
   public boolean isGenhs() {
      return genhs;
   }
   void setGenhs(boolean genhs) {
      this.genhs = genhs;
   }
   /**
    * Returns where the gradient and Hessian is retrieved from
    * @return "input" or "current"
    */
   public String getGradient_and_hessian() {
      return gradient_and_hessian;
   }
   void setGradient_and_hessian(String gradient_and_hessian) {
      this.gradient_and_hessian = gradient_and_hessian;
   }

}
