package sindo;
import java.util.*;

/**
 * Stores the output data of VQDPT calculation
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since 3.4
 *
 */
public class VQDPTdata {

   private int Ntarget;
   private Conf[] targetConf;
   private int maxSum, nCUP, pqSum, pSet;
   private double nGen, P0,P1,P2,P3;
   private int Ngroup;
   private int[] NpCnf;
   private double ZPE;
   private ArrayList<VQDPTgroup> groups;
   private boolean isInfared;
   
   public VQDPTdata(){
      groups = new ArrayList<VQDPTgroup>();
   }
   
   void setNtarget(int ntarget) {
      Ntarget = ntarget;
   }
   
   void setTargetConf(Conf[] targetConf) {
      this.targetConf = targetConf;
   }
   
   void setNgroup(int ngroup) {
      Ngroup = ngroup;
   }

   void setNpCnf(int[] npCnf) {
      NpCnf = npCnf;
   }

   void setZPE(double zPE) {
      ZPE = zPE;
   }

   void setOptions(int maxSum, int nCUP, int pqSum, int pSet){
      this.maxSum = maxSum;
      this.nCUP = nCUP;
      this.pqSum = pqSum;
      this.pSet = pSet;
   }

   void addGroup(VQDPTgroup group){
      groups.add(group);
   }

   void setPspaceOptions(double nGen, double p0, double p1, double p2, double p3){
      this.nGen = nGen;
      P0 = p0;
      P1 = p1;
      P2 = p2;
      P3 = p3;
   }

   void setInfared(boolean isInfared) {
      this.isInfared = isInfared;
   }

   /**
    * Returns the number of target configurations
    * @return the number of target
    */
   public int getNtarget() {
      return Ntarget;
   }
   
   /**
    * Returns target configurations
    * @return target configurations
    */
   public Conf[] getTargetConf() {
      return targetConf;
   }
   
   /**
    * Returns the number of VQDPT groups
    * @return the number of groups
    */
   public int getNgroup() {
      return Ngroup;
   }
   
   /**
    * Returns the number of configurations in P space
    * @return number of P-conf for each group
    */
   public int[] getNpCnf() {
      return NpCnf;
   }
   
   /**
    * Returns the zero-point energy (in cm-1)
    * @return zero-point energy
    */
   public double getZPE() {
      return ZPE;
   }
   
   /**
    * Returns the options used for VQDPT calc.
    * @return options[maxSum,nCUP,pqSum,pSet]
    */
   public int[] getOptions(){
      int[] options = new int[4];
      options[0] = maxSum;
      options[1] = nCUP;
      options[2] = pqSum;
      options[3] = pSet;
      return options;
   }
   
   /**
    * Returns the options used for P space construction 
    * @return options[nGen,P0,P1,P2,P3]
    */
   public double[] getPspaceOptions(){
      double[] options = new double[5];
      options[0] = nGen;
      options[1] = P0;
      options[2] = P1;
      options[3] = P2;
      options[4] = P3;
      return options;
   }
   
   /**
    * Returns a VQDPT group
    * @param index an index of the group
    * @return VQDPT group
    */
   public VQDPTgroup getGroup(int index){
      return groups.get(index);
   }
   
   /**
    * Returns all VQDPT groups of this data
    * @return An array of VQDPT group
    */
   public VQDPTgroup[] getAllGroup(){
      return (VQDPTgroup[]) groups.toArray(new VQDPTgroup[groups.size()]);
   }
   
   /**
    * Returns all VQDPT states. The states are sorted by energy. 
    * Note that the group information are not retained.
    * @return An array of VQDPT states
    */
   public ArrayList<VibState> getAllStates() {
      return this.getAllStates(true);
   }
   
   /**
    * Returns all VQDPT states. Note that the group information are not retained.
    * @param sortByEnergy sort the states by energy in an increasing order
    * @return An array of VQDPT states
    */
   public ArrayList<VibState> getAllStates(boolean sortByEnergy) {
      
      ArrayList<VibState> list = new ArrayList<VibState>();
      for(int n=0; n<groups.size(); n++) {
         list.addAll(groups.get(n).getVibState());
      }
      if(sortByEnergy) {
         Collections.sort(list, new VibComparator());
         
      }
      
      return list;
   }

   /**
    * Checks whether IR data is set
    * @return If true, IR data is set 
    */
   public boolean isInfared() {
      return isInfared;
   }
}
