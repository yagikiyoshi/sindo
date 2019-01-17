package makePES;

public class InputDataQFF {

   private String qcindex;
   private double stepsize;
   private String ndifftype;
   private String xyz_basename;
   private String mopfile;
   private boolean genhs;
   private String gradient_and_hessian;
   
   /**
    * This is a package private class
    */
   InputDataQFF(){
      
   }
   
   /**
    * Returns the index of QC calc for this job
    * @return index of QC calc.
    */
   public String getQCindex() {
      return qcindex;
   }
   /**
    * Sets the index of QC calc for this job
    * @param qcindex index of QC calc.
    */
   public void setQCindex(String qcindex) {
      this.qcindex = qcindex;
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
    * Returns the basename of a xyz file for qchem = generic
    * @return basename
    */
   public String getXYZFile_basename() {
      return this.xyz_basename;
   }
   /**
    * Sets the basename of a xyz file for qchem = generic
    * @param basename basename
    */
   void setXYZFile_basename(String basename) {
      this.xyz_basename = basename;
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
