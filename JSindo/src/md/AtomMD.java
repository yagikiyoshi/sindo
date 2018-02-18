package md;

import atom.Atom;
import ff.VdW;

public class AtomMD extends Atom implements Cloneable {

   private double beta;
   private double occ;
   private String type;
   private VdW vdw;

   /**
    * Constructor of AtomMD (sets beta, occ, and resID to zero)
    */
   public AtomMD(){
      super();
      this.beta=0.0;
      this.occ=0.0;
      this.vdw=null;
   }
   /**
    * Constructs an AtomMD with a specified atomic number
    * @param atomicNumber atomic number
    */
   public AtomMD(int atomicNumber){
      super(atomicNumber);
      this.beta=0.0;
      this.occ=0.0;
      this.vdw=null;
   }
   /**
    * Returns the beta value
    * @return beta value
    */
   public double getBeta() {
      return beta;
   }
   /**
    * Sets the beta value
    * @param beta beta value
    */
   public void setBeta(double beta) {
      this.beta = beta;
   }   
   /**
    * Returns the occupation number
    * @return Occupation number
    */
   public double getOcc() {
      return occ;
   }
   /**
    * Sets the occupation number
    * @param occ Occupation number
    */
   public void setOcc(double occ) {
      this.occ = occ;
   }
   /**
    * Returns the type of the atom
    * @return The type
    */
   public String getType() {
      return type;
   }
   /**
    * Sets the type of the atom
    * @param type the type of the atom
    */
   public void setType(String type) {
      this.type = type;
   }
   /**
    * Returns the van der Waals parameters
    * @return the VdW instance (null if not set)
    */
   public VdW getVdW(){
      return vdw;
   }
   /**
    * Sets van der Waals parameters
    * @param vdw the VdW instance
    */
   public void setVdW(VdW vdw){
      this.vdw = vdw;
   }
   
   public AtomMD clone(){
      return (AtomMD)super.clone();
   }

   public String toString(){
      String ss = super.toString();
      ss = ss + String.format("beta,occ = %8.2f %8.2f %n", beta,occ);
      ss = ss + String.format("type = %8s %n", type);
      return ss;

   }
   
}
