package atom;

import java.util.*;
import sys.Constants;

/**
 * Utility class for hydrogen bond analysis. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class HBanalysis {

   private double HBdistance;
   private double HBangle;   
   private Atom[][] donors;
   private Atom[] acceptors;
   private int nHB;
   protected Integer[][] byDonor;
   protected Integer[][] byAcceptor;
   private AtomUtil autil;
   private boolean HBinVMD;
   private double rDA;
   private double rAH;
   private double angle;
   protected boolean isCalcHB;
   
   /**
    * Constructs with default settings: r(DA) = 3.5 Angs, a(A...D-H)=20.0 degree.
    */
   public HBanalysis(){
      HBdistance=3.5d/Constants.Bohr2Angs;
      HBangle=20.0d;
      donors=null;
      acceptors=null;

      autil = new AtomUtil();
      isCalcHB = false;
      HBinVMD  = false;
   }
   
   /**
    * Set the hydrogen bond distance. Default = 3.5 Angs.
    * @param distance the HB distance in Angs
    */
   public void setHBdistance(double distance){
      HBdistance = distance/Constants.Bohr2Angs;
   }
   /**
    * Set the hydrogen bond angle (H-D..A angle). Default = 20.0
    * @param angle the HB angle in degree
    */
   public void setHBangle(double angle){
      HBangle = angle;
   }
   /**
    * Sets the donor atoms
    * @param donors [0] Donor Atom, [1] Hydrogen Atom
    */
   public void setDonorAtoms(Atom[][] donors) {
      this.donors = donors;
      isCalcHB = false;
   }
   /** 
    * Sets the acceptor atoms
    * @param acceptors acceptor atoms
    */
   public void setAcceptorAtoms(Atom[] acceptors) {
      this.acceptors = acceptors;
      isCalcHB = false;
   }
   /**
    * Sets the definition of HB in VMD (angle = 180.0 - DHA). Default is false.
    */
   public void setHBinVMD(){
      this.HBinVMD = true;
   }
   /**
    * Unsets the definition of HB in VMD (angle = 180.0 - DHA)
    */
   public void unsetHBinVMD(){
      this.HBinVMD = false;
   }
   /**
    * Judge if atoms D and A are hydrogen bonded. 
    * @param D [0] Donor atom, [1] Hydrogen atom
    * @param A Acceptor atom
    * @return True if hydrogen bonded
    */
   public boolean isHB(Atom[] D, Atom A){
      return this.isHB(D[0], D[1], A);
   }
   /**
    * Judge if atoms D-H and A are hydrogen bonded. 
    * @param D Donor atom
    * @param H Hydrogen atom
    * @param A Acceptor atom
    * @return True if hydrogen bonded
    */
   public boolean isHB(Atom D, Atom H, Atom A){
      
      if(D.equals(A)) return false;
      
      rDA = autil.getBondLength(A, D);
      rAH = autil.getBondLength(A, H);
      angle = 0.0;
      if(! HBinVMD){
         angle = autil.getBondAngle(A, D, H);
      }else{
         angle = 180.0 - autil.getBondAngle(D, H, A);
      }
      
      if(rDA > HBdistance){
         return false;
      }
      if(angle > HBangle){
         return false;
      }else{
         return true;         
      }

   }
   
   /**
    * Returns the r(D...A) for the current atoms.
    * @return rDA in bohr
    */
   public double getrDA(){
      return rDA;
   }
   
   /**
    * Returns the r(A...H) for the current atoms.
    * @return rAH in bohr
    */
   public double getrAH(){
      return rAH;
   }
   
   /**
    * Returns the angle for the current atoms. (A...D-H or D-H...A)
    * @return the angle in degree
    */
   public double getAngle(){
      return angle;
   }
   /**
    * Calculates the HB patterns for given donor/acceptor set of atoms.
    */
   public void calcHB(){
      if(isCalcHB) return;
      
      nHB=0;
      byDonor=null;
      byAcceptor=null;

      if(donors == null || acceptors == null){
         return;
      }
      
      @SuppressWarnings("unchecked")
      ArrayList<Integer>[] donorList = new ArrayList[donors.length];
      for(int i=0; i<donorList.length; i++){
         donorList[i] = new ArrayList<Integer>();
      }
      @SuppressWarnings("unchecked")
      ArrayList<Integer>[] acceptorList = new ArrayList[acceptors.length];
      for(int i=0; i<acceptorList.length; i++){
         acceptorList[i] = new ArrayList<Integer>();
      }
      
      for(int i=0; i<donors.length; i++){
         for(int j=0; j<acceptors.length; j++){
            if(this.isHB(donors[i], acceptors[j])){
               donorList[i].add(j);
               acceptorList[j].add(i);
               nHB++;                  
            }
         }
      }
      
      byDonor = new Integer[donors.length][];
      for(int i=0; i<donors.length; i++){
         byDonor[i] = donorList[i].toArray(new Integer[0]);
      }

      byAcceptor = new Integer[acceptors.length][];
      for(int i=0; i<acceptors.length; i++){
         byAcceptor[i] = acceptorList[i].toArray(new Integer[0]);
      }
      
   }
   
   public int getNumHB(){
      return nHB;
   }
   
   public Integer[][] getByAcceptor(){
      return byAcceptor;
   }
   
   public Integer[][] getByDonor(){
      return byDonor;
   }
}
