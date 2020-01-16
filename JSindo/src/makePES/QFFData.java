package makePES;

import java.util.*;

/**
 * Reads and provides the data of QFF.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class QFFData {
   
   private int Nfree, MR;
   private String title;
   private HashMap<String, Double> coeff;
   
   public QFFData() {
      coeff = new HashMap<String, Double>();
   }

   private String getIndex(Integer[] modes) {
      Arrays.sort(modes, Collections.reverseOrder());
      String ss = String.valueOf(modes[0]);
      for(int i=1; i<modes.length; i++) {
         ss += "-"+modes[i];
      }
      return ss;
   }

   /**
    * Return the number of degree of freedom
    * @return Nfree degree of freedom
    */
   public int getNfree(){
      return Nfree;
   }
   /**
    * Set the number of degree of freedom
    * @param Nfree degree of freedom
    */
   public void setNfree(int Nfree) {
      this.Nfree = Nfree;
   }
   /**
    * Return the mode representation
    * @return MR Mode representation
    */
   public int getMR(){
      return MR;
   }
   /**
    * Set the mode representation
    * @param MR Mode representation
    */
   public void setMR(int MR) {
      this.MR = MR;
   }
   /**
    * Return the title
    * @return title the title
    */
   public String getTitle() {
      return title;
   }
   /**
    * Set the title
    * @param title the title
    */
   public void setTitle(String title) {
      this.title = title;
   }
   /**
    * Get Map of QFF coefficients
    * @return HashMap of QFF coefficients
    */
   public HashMap<String, Double> getCoeff(){
      return coeff;
   }
   /**
    * Get QFF coefficients
    * @param modes Mode combination
    * @return coefficient. Null if not present.
    */
   public Double getCoeff(Integer[] modes) {
      String index = this.getIndex(modes);
      return coeff.get(index);
   }
   
   private double getCoeff(int mi) {
      Integer[] mm = {mi};
      Double c = getCoeff(mm);
      if(c != null) {
         return c;
      }else {
         return 0.0d;
      }
   }
   
   private double getCoeff(int mi, int mj) {
      Integer[] mm = {mi,mj};
      Double c = getCoeff(mm);
      if(c != null) {
         return c;
      }else {
         return 0.0d;
      }
   }
   
   private double getCoeff(int mi, int mj, int mk) {
      Integer[] mm = {mi,mj,mk};
      Double c = getCoeff(mm);
      if(c != null) {
         return c;
      }else {
         return 0.0d;
      }
   }
   
   private double getCoeff(int mi, int mj, int mk, int ml) {
      Integer[] mm = {mi,mj,mk,ml};
      Double c = getCoeff(mm);
      if(c != null) {
         return c;
      }else {
         return 0.0d;
      }
   }
   
   /**
    * Puts QFF coefficient. Note that the pre-factors are multiplied;
    *    cii = 1/2 * hii
    *    ciij = 1/2 * tiij
    *    ciiij = 1/6 * uiiij
    *    ciijj = 1/4 * uiijj
    * @param modes Mode combination
    * @param c Coefficients in au
    */
   public void putCoeff(Integer[] modes, double c) {
      coeff.put(this.getIndex(modes), c);
   }
   
   /**
    * Return Gi in au.
    * @param i mode
    * @return gi
    */
   public double getCi(int i){
      return getCoeff(i);
   }
   /**
    * Return Hii/2 in au.
    * @param i mode
    * @return hii/2
    */
   public double getCii(int i){
      return getCoeff(i,i);
   }
   /**
    * Return Tiii/6 in au.
    * @param i mode
    * @return tiii/6
    */
   public double getCiii(int i){
      return getCoeff(i,i,i);
   }

   /**
    * Return Uiiii/24 in au.
    * @param i mode
    * @return uiiii/24
    */
   public double getCiiii(int i){
      return getCoeff(i,i,i,i);
   }

   /**
    * Return Hij (i&gt;j) in au.
    * @param i mode i
    * @param j mode j
    * @return Hij
    */
   public double getCij(int i, int j){
      return getCoeff(i,j);
   }

   /**
    * Return Tiij/2 (i&gt;j) in au
    * @param i mode i
    * @param j mode j
    * @return Tiij/2
    */
   public double getCiij(int i, int j){
      return getCoeff(i,i,j);
   }

   /**
    * Return Tijj/2 (i&gt;j) in au
    * @param i mode i
    * @param j mode j
    * @return Tijj/2
    */
   public double getCijj(int i, int j){
      return getCoeff(i,j,j);
   }

   /**
    * Return Uiijj/4 (i&gt;j) in au
    * @param i mode i
    * @param j mode j
    * @return uiijj/4
    */
   public double getCiijj(int i, int j){
      return getCoeff(i,i,j,j);
   }

   /** Return Uiiij/6 (i&gt;j) in au.
    * @param i mode i
    * @param j mode j
    * @return uiiij/6
    */
   public double getCiiij(int i, int j){
      return getCoeff(i,i,i,j);
   }

   /** Return Uijjj/6 (i&gt;j) in au.
    * @param i mode i
    * @param j mode j
    * @return uijjj/6
    */
   public double getCijjj(int i, int j){
      return getCoeff(i,j,j,j);
   }

   /**
    * Return Tijk (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return tijk
    */
   public double getCijk(int i, int j, int k){
      return getCoeff(i,j,k);
   }

   /**
    * Return Uiijk/2 (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return uiijk/2
    */
   public double getCiijk(int i, int j, int k){
      return getCoeff(i,i,j,k);
   }

   /**
    * Return Uijjk/2 (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return uijjk/2
    */
   public double getCijjk(int i, int j, int k){
      return getCoeff(i,j,j,k);
   }

   /**
    * Return Uijkk/2 (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return uijkk/2
    */
   public double getCijkk(int i, int j, int k){
      return getCoeff(i,j,k,k);
   }

   /**
    * Return Uijkl (i&gt;j&gt;k&gt;l) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @param l mode l
    * @return uijkl
    */
   public double getCijkl(int i, int j, int k, int l){
      return getCoeff(i,j,k,l);
   }

}
