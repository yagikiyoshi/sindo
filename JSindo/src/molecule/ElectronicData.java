package molecule;

import sys.Utilities;

/**
 * (Package Private) Stores the data of the electronic structure calculation. 
 * The variables are NaN (Not a number) or null if the data is not set. Units in 
 * atomic units. 
 * This class includes the following data: <br>
 * <ol>
 * <li> Electronic energy (Hartree) </li>
 * <li> Gradient (Hartree/bohr) </li>
 * <li> Hessian (Hartree/bohr^2) </li>
 * <li> Charge (e) </li>
 * <li> Spin multiplicity </li>
 * <li> Dipole moment (au) </li>
 * <li> Polarizability (au) </li>
 * <li> Hyperpolarizability (au) </li>
 * <li> Gradient of the dipole moment (au) </li>
 * <li> Gradient of the polarizability (au) </li>
 * </ol>
 * 
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.0
 */
public class ElectronicData implements Cloneable {
   
   private double energy = Double.NaN;
   private double[] gradient = null;
   private double[] hessian = null;
   
   private double charge = Double.NaN;
   private double multiplicity = Double.NaN;
   private double[] dipole = null;
   private double[] dipoleDerivative = null;
   private double[] polarizability = null;
   private double[] polarDerivative = null;
   private double[] hyperpolar = null;
   
   /**
    * Constructor is package private. 
    */
   ElectronicData(){
      
   }
   /**
    * Returns the energy (Hartree).
    * @return the energy
    */
   public double getEnergy() {
      return energy;
   }
   /**
    * Returns the gradient (Hartree/bohr).
    * @return the gradient
    */
   public double[] getGradient() {
      return gradient;
   }
   /**
    * Returns the Hessian in one-dimensinal packed form (Hartree/bohr^2).
    * @return the Hessian
    */
   public double[] getHessian() {
      return hessian;
   }
   /**
    * Returns the charge (e).
    * @return the charge
    */
   public double getCharge() {
      return charge;
   }
   /**
    * Returns the spin multiplicity 
    * @return the spin multiplicity
    */
   public double getMultiplicity() {
      return multiplicity;
   }
   // TODO  Check the unit of dipole and dipole derivatives.
   /**
    * Returns the dipole moment (au).
    * @return the dipole moment
    */
   public double[] getDipole() {
      return dipole;
   }
   /**
    * Returns the dipole derivatives in one-dimensional packed form [3*Nat3] (au). 
    * @return the dipole derivatives [ux/x1,uy/x1,uz/x1, ux/x2, ..., uz/x_3N]
    */
   public double[] getDipoleDerivative() {
      return dipoleDerivative;
   }   
   /**
    * Returns the polarizability in one-dimensional packed form [xx,xy,yy,xz,yz,zz] (au). 
    * @return the polarizability
    */
   public double[] getPolarizability() {
      return polarizability;
   }
   /**
    * Returns the polarizability derivatives in one-dimensional packed form [6*Nat3] (au). 
    * @return the polarizability derivatives
    */
   public double[] getPolarizabilityDerivative() {
      return polarDerivative;
   }
   /**
    * Returns the hyperpolarizability in one-dimensional packed form [xxx,xxy,xyy,yyy,xxz,xyz,yyz,xzz,yzz,zzz] (au). 
    * @return the hyper polarizability
    */
   public double[] getHyperPolarizability() {
      return hyperpolar;
   }
   /**
    * Sets the energy (Hartree).
    * @param energy the energy
    */
   void setEnergy(double energy) {
      this.energy = energy;
   }
   /**
    * Sets the gradient (Hartree/borh).
    * @param gradient the gradient
    */
   void setGradient(double[] gradient) {
      this.gradient = gradient;
   }
   /**
    * Sets the Hessian in one-dimensinal packed form (Hartree/bohr^2).
    * @param hessian the Hessian
    */
   void setHessian(double[] hessian) {
      this.hessian = hessian;
   }
   /**
    * Sets the charge (e).
    * @param charge the charge
    */
   void setCharge(double charge) {
      this.charge = charge;
   }
   /**
    * Sets the spin multiplicity.
    * @param multiplicity the spin multiplicity
    */
   void setmultiplicity(double multiplicity) {
      this.multiplicity = multiplicity;
   }
   /**
    * Sets the dipole moment [x,y,z] (au).
    * @param dipole the dipole moment
    */
   void setDipole(double[] dipole) {
      this.dipole = dipole;
   }
   /**
    * Sets the dipole derivatives in one-dimensional packed form [3*Nat*3] (au).
    * @param dipoleDerivative the dipole derivatives
    */
   void setDipoleDerivative(double[] dipoleDerivative) {
      this.dipoleDerivative = dipoleDerivative;
   }
   /**
    * Sets the polarizability [xx,xy,yy,xz,yz,zz] (au)
    * @param polarizability
    */
   void setPolarizability(double[] polarizability) {
      this.polarizability = polarizability;
   }
   /**
    * Sets the polarizability derivatives [6*Nat*3] (au)
    * @param polarDerivative
    */
   void setPolarizabilityDerivative(double[] polarizabilityDerivative) {
      this.polarDerivative = polarizabilityDerivative;
   }
   /**
    * Sets the hyper-polarizability [xxx,xxy,xyy,yyy,xxz,xyz,yyz,xzz,yzz,zzz] (au)
    * @param hyperpolarizability
    */
   void setHyperPolarizability(double[] hyperpolarizability) {
      this.hyperpolar = hyperpolarizability;
   }
   
   public ElectronicData clone(){
      ElectronicData edata = null;
      try{
         edata = (ElectronicData)super.clone();
      }catch(CloneNotSupportedException e){
         e.printStackTrace();
         return null;
      }
      
      if(gradient != null){
         edata.setGradient(Utilities.deepCopy(gradient));
      }
      if(hessian != null){
         edata.setHessian(Utilities.deepCopy(hessian));
      }
      if(dipole != null){
         edata.setDipole(Utilities.deepCopy(dipole));
      }
      if(dipoleDerivative != null){
         edata.setDipoleDerivative(Utilities.deepCopy(dipoleDerivative));
      }
      if(polarizability != null){
         edata.setPolarizability(Utilities.deepCopy(polarizability));
      }
      if(polarDerivative != null){
         edata.setPolarizabilityDerivative(Utilities.deepCopy(polarDerivative));
      }
      if(hyperpolar != null){
         edata.setHyperPolarizability(Utilities.deepCopy(hyperpolar));
      }
      return edata;
   }
}
