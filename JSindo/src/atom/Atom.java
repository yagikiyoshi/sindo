package atom;

import sys.*;

/**
 * Stores the data of atom. Units in atomic units.
 * This class includes the following data: <br>
 * <ol>
 * <li> Label </li>
 * <li> Mass (emu) </li>
 * <li> Atomic number </li>
 * <li> Cartesian coordinates (bohr)</li>
 * <li> Charge of atom (e)</li>
 * <li> Beta value </li>
 * <li> Occupation number </li>
 * <li> ID </li>
 * </ol>
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class Atom implements Cloneable {
   
   private String label;
   private Double mass;
   private Integer atomicNum;
   private double[] xyz;
   private double charge;
   private int ID;
      
   /**
    * Generates an empty atom.
    */
   public Atom(){
      this.label=null;
      this.mass=0.0;
      this.atomicNum=-1;
      this.xyz=null;
      this.charge=Double.NaN;
      this.ID=-1;
   }
   
   /**
    * Generates an atom with atomic number.
    * @param atomicNumber Atomic number
    */
   public Atom(int atomicNumber){
      this();
      this.atomicNum = atomicNumber;
      this.label = PeriodicTable.label[atomicNumber];         
      this.mass = PeriodicTable.mass[atomicNumber][0]/Constants.Emu2Amu;         
   }
   
   /**
    * Returns the label
    * @return label
    */
   public String getLabel(){
      return label;
   }
   /**
    * Sets the label
    * @param label label
    */
   public void setLabel(String label){
      this.label = label;
   }
   /**
    * Returns the mass of atom (emu)
    * @return mass 
    */
   public double getMass(){
      return mass;
   }
   /**
    * Sets the mass of atom (emu)
    * @param mass mass
    */
   public void setMass(double mass){
      this.mass = mass;
   }
   /**
    * Set the atom to deuterium.
    */
   public void setDeuterium(){
      this.mass = PeriodicTable.mass[1][1]/Constants.Emu2Amu;
      this.atomicNum = 1;
   }

   /**
    * Set the atom to C13
    */
   public void setC13(){
      this.mass = PeriodicTable.mass[6][1]/Constants.Emu2Amu;
      this.atomicNum = 6;
   }

   /**
    * Sets the atomic number
    * @param atomicNumber atomic number
    */
   public void setAtomicNum(int atomicNumber) {
      this.atomicNum = atomicNumber;
      if(label == null){
         this.label = PeriodicTable.label[atomicNum];         
      }
      if(mass == 0.0){
         this.mass = PeriodicTable.mass[atomicNum][0]/Constants.Emu2Amu;         
      }
   }
   /**
    * Returns the atomic number
    * @return Atomic number
    */
   public int getAtomicNum(){
      return this.atomicNum;
   }
   /**
    * Returns the atomic charge
    * @return Atomic charge
    */
   public double getAtomicCharge() {
      return charge;
   }
   /**
    * Sets the atomic charge
    * @param charge atomic charge
    */
   public void setAtomicCharge(double charge) {
      this.charge = charge;
   }
   /**
    * Returns XYZ coordinates of the atom (bohr)
    * @return XYZ coordinates double[3]
    */
   public double[] getXYZCoordinates() {
      return xyz;
   }
   /**
    * Sets XYZ coordinates of the atom (bohr)
    * @param xyz XYZ coordinates double[3]
    */
   public void setXYZCoordinates(double[] xyz) {
      this.xyz = xyz;
   }

   /**
    * Returns the ID of the atom
    * @return ID
    */
   public int getID() {
      return ID;
   }
   /**
    * Sets the ID of the atom
    * @param iD ID
    */
   public void setID(int iD) {
      ID = iD;
   }

   /**
    * Shift the position of the atom by an input vector
    * @param vector the shifting vector
    */
   public void shift(double[] vector){
      for(int i=0; i<3; i++){
         xyz[i] = xyz[i] + vector[i];
      }
   }

   public Atom clone(){
      Atom atm = null;
      try{
         atm = (Atom)super.clone();         
      }catch(CloneNotSupportedException e){
         e.printStackTrace();
         return null;
      }
      if(xyz != null){
         double[] newxyz = new double[3];
         for(int j=0; j<3; j++){
            newxyz[j] = xyz[j];
         }
         atm.setXYZCoordinates(newxyz);
      }
      return atm;
   }

   public String toString(){
      String s = "";
      if(label != null){
         s = s + String.format("Label= %s %n", label);
      }
      if(mass != 0.0){
         s = s + String.format("Mass= %8.2f %n", mass*Constants.Emu2Amu);
      }
      if(atomicNum >0){
         s = s + String.format("AtomicNumber = %4d %n", atomicNum);
      }
      if(xyz != null){
         s = s + String.format("xyz = %8.2f %8.2f %8.2f %n", xyz[0],xyz[1],xyz[2]);
      }
      s = s + String.format("charge = %8.2f %n", charge);
      if(ID > 0){
         s = s + String.format("ID = %8d %n",ID);
      }
      return s;
   }
}
