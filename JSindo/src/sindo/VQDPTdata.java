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
   
   public int getNtarget() {
      return Ntarget;
   }
   public void setNtarget(int ntarget) {
      Ntarget = ntarget;
   }
   public Conf[] getTargetConf() {
      return targetConf;
   }
   public void setTargetConf(Conf[] targetConf) {
      this.targetConf = targetConf;
   }
   public int getNgroup() {
      return Ngroup;
   }
   public void setNgroup(int ngroup) {
      Ngroup = ngroup;
   }
   public int[] getNpCnf() {
      return NpCnf;
   }
   public void setNpCnf(int[] npCnf) {
      NpCnf = npCnf;
   }
   public double getZPE() {
      return ZPE;
   }
   public void setZPE(double zPE) {
      ZPE = zPE;
   }
   public void setOptions(int maxSum, int nCUP, int pqSum, int pSet){
      this.maxSum = maxSum;
      this.nCUP = nCUP;
      this.pqSum = pqSum;
      this.pSet = pSet;
   }
   /**
    * Returns the options for VQDPT calc.
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
   public void setPspaceOptions(double nGen, double p0, double p1, double p2, double p3){
      this.nGen = nGen;
      P0 = p0;
      P1 = p1;
      P2 = p2;
      P3 = p3;
   }
   /**
    * Returns the options for P space construction 
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
   
   public void addGroup(VQDPTgroup group){
      groups.add(group);
   }
   public VQDPTgroup getGroup(int index){
      return groups.get(index);
   }
   public VQDPTgroup[] getAllGroup(){
      return (VQDPTgroup[]) groups.toArray(new VQDPTgroup[groups.size()]);
   }

   public boolean isInfared() {
      return isInfared;
   }

   public void setInfared(boolean isInfared) {
      this.isInfared = isInfared;
   }
}
