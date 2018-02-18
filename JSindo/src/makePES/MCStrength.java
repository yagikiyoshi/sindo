package makePES;

import sys.Constants;

/**
 * Given the QFFData, this class provides the mode coupling strength. <br> 
 * P.Seidler et al, Chem. Phys. Lett. 483, 138-142 (2009).
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class MCStrength {
   
   private int Nfree;
   private QFFData qff;
   private double dd = 1.0e-08;
   private double[] omg,omginv1, omginv2;

   /**
    * Append qff data for MCS calculation
    * @param qff The data of qff
    */
   public void appendQFF(QFFData qff){
      this.qff = qff;
      Nfree = qff.getNfree();
      
      omg = new double[Nfree];
      omginv1 = new double[Nfree];
      omginv2 = new double[Nfree];
      for(int i=0; i<Nfree; i++){
         omg[i] = Math.sqrt(qff.getCii(i)*2.0);
         //System.out.println(omg[i]*Constants.Hartree2wvn);
         omginv1[i] = 0.5/omg[i];
         omginv2[i] = omginv1[i]*omginv1[i];
      }
   }
   
   /**
    * MCS for 2MR term (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return MCS value (cm-1)
    */
   public double get2mrMCS(int i, int j){
      
      double aa = this.getSij(i, j);
      double a0 = this.getSiij(i, j);
      double a1 = this.getSijj(i, j);
      double a2 = this.getSiiij(i, j);
      double a3 = this.getSijjj(i, j);
      double a4 = this.getSiijj(i, j);

      aa = aa + a0 + a1 + a2 + a3 + a4;
      return aa*Constants.Hartree2wvn;      
   }
   
   /**
    * MCS for 3MR term (i&gt;j&gt;k)
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return MCS value (cm-1)
    */
   public double get3mrMCS(int i, int j, int k){
      double aa = this.getSijk(i, j, k);
      double a1 = this.getSiijk(i, j, k);
      double a2 = this.getSijjk(i, j, k);
      double a3 = this.getSijkk(i, j, k);
      
      aa = aa + a1 + a2 + a3;
      return aa*Constants.Hartree2wvn;      
   }
   public double get4mrMCS(int i, int j, int k, int l){
      double aa = this.getSijkl(i, j, k, l);
      return aa*Constants.Hartree2wvn;
   }
   /**
    * Returns the strength of cij (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return strength of cij
    */
   public double getSij(int i, int j){
      // 1:1 resonance
      double cc = qff.getCij(i, j);
      double aa = cc*cc/Math.abs(omg[i] - omg[j]+dd)*omginv1[i]*omginv1[j];
      //System.out.println(i+" "+j+" "+a3*Constants.Hartree2wvn);
      return aa;
   }
   /**
    * Returns the strength of ciij (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return strength of ciij
    */
   public double getSiij(int i, int j){
      // 2:1 (Fermi) resonance
      double cc = qff.getCiij(i, j);
      double aa = 2.0*cc*cc/Math.abs(omg[j]-2.0*omg[i]+dd)*omginv1[j]*omginv2[i];
      return aa;
   }
   /**
    * Returns the strength of cijj (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return strength of cijj
    */
   public double getSijj(int i, int j){
      // 2:1 (Fermi) resonance
      double cc = qff.getCijj(i, j);
      double aa = 2.0*cc*cc/Math.abs(omg[i]-2.0*omg[j]+dd)*omginv1[i]*omginv2[j];
      return aa;
   }
   /**
    * Returns the strength of cijk (i&gt;j&gt;k)
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return strength of cijk
    */
   public double getSijk(int i, int j, int k){
      // 1:1:1 resonance
      double cijk = qff.getCijk(i, j, k);
      cijk = cijk*cijk;
      double a1 = 1.0/Math.abs(omg[i]-omg[j]-omg[k]+dd)
                + 1.0/Math.abs(omg[j]-omg[i]-omg[k]+dd)
                + 1.0/Math.abs(omg[k]-omg[j]-omg[i]+dd);
      a1 = a1*cijk*omginv1[i]*omginv1[j]*omginv1[k];
      return a1;
   }
   /**
    * Returns the strength of ciiij (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return the strength of ciiij
    */
   public double getSiiij(int i, int j){
      double cc = qff.getCiiij(i, j);
      cc=cc*cc;

      // 1:1 & 3:1 resonance
      double aa = 9.0/Math.abs(omg[j] - omg[i]-dd)
                + 6.0/Math.abs(omg[j] - 3.0*omg[i]+dd);
      aa = aa*cc*omginv2[i]*omginv1[i]*omginv1[j];

      return aa;
   }
   /**
    * Returns the strength of cijjj (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return the strength of cijjj
    */
   public double getSijjj(int i, int j){
      double cc = qff.getCijjj(i, j);
      cc=cc*cc;

      // 1:1 & 3:1 resonance
      double aa = 9.0/Math.abs(omg[i] - omg[j]+dd)
                + 6.0/Math.abs(omg[i] - 3.0*omg[j]+dd);
      aa = aa*cc*omginv2[j]*omginv1[j]*omginv1[i];
      return aa;
   }
   /**
    * Returns the strength of ciijj (i&gt;j)
    * @param i mode i
    * @param j mode j
    * @return the strength of ciijj
    */
   public double getSiijj(int i, int j){
      double cc = qff.getCiijj(i, j);
      
      // Diagonal term
      double a1 = Math.abs(cc)*omginv1[i]*omginv1[j];
      //System.out.println(i+" "+j+" "+a1*Constants.Hartree2wvn);
      
      // 2:2 resonance
      double aa = 2.0/Math.abs(omg[i] - omg[j]+dd);
      aa = aa*cc*cc*omginv2[i]*omginv2[j];

      return aa+a1;
   }
   /**
    * Returns the strength of ciijk (i&gt;j&gt;k)
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return the strength of ciijk
    */
   public double getSiijk(int i, int j, int k){
      // 2:1:1 resonance
      double ciijk = qff.getCiijk(i, j, k);
      ciijk = ciijk*ciijk;
      double a2 = 1.0/Math.abs(omg[j]-2.0*omg[i]-omg[k]+dd)
                + 1.0/Math.abs(omg[k]-2.0*omg[i]-omg[j]+dd)
                + 1.0/Math.abs(2.0*omg[i]-omg[j]-omg[k]+dd);
      a2 = a2*2.0*ciijk*omginv2[i]*omginv1[j]*omginv1[k];
      
      return a2;
   }
   /**
    * Returns the strength of cijjk (i&gt;j&gt;k)
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return the strength of cijjk
    */
   public double getSijjk(int i, int j, int k){
      // 2:1:1 resonance
      double cijjk = qff.getCijjk(i, j, k);
      cijjk = cijjk*cijjk;
      double a3 = 1.0/Math.abs(omg[i]-2.0*omg[j]-omg[k]+dd)
                + 1.0/Math.abs(omg[k]-2.0*omg[j]-omg[i]+dd)
                + 1.0/Math.abs(2.0*omg[j]-omg[k]-omg[i]+dd);
      a3 = a3*2.0*cijjk*omginv1[i]*omginv2[j]*omginv1[k];
      
      return a3;
   }
   /**
    * Returns the strength of cijkk (i&gt;j&gt;k)
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return the strength of cijkk
    */
   public double getSijkk(int i, int j, int k){
      // 2:1:1 resonance
      double cijkk = qff.getCijkk(i, j, k);
      cijkk = cijkk*cijkk;
      double a4 = 1.0/Math.abs(omg[i]-2.0*omg[k]-omg[j]+dd)
                + 1.0/Math.abs(omg[j]-2.0*omg[k]-omg[i]+dd)
                + 1.0/Math.abs(2.0*omg[k]-omg[i]-omg[j]+dd);
      a4 = a4*2.0*cijkk*omginv1[i]*omginv1[j]*omginv2[k];

      return a4;
   }
   /**
    * Returns the strength of cijkl
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @param l mode l
    * @return the strength of cijkl
    */
   public double getSijkl(int i, int j, int k, int l){
      double cijkl = qff.getCijkl(i, j, k, l);
      cijkl = cijkl*cijkl;
      double aa = 1.0/Math.abs(omg[i] - omg[j] - omg[k] - omg[l]+dd)
                + 1.0/Math.abs(omg[j] - omg[i] - omg[k] - omg[l]+dd)
                + 1.0/Math.abs(omg[k] - omg[i] - omg[j] - omg[l]+dd)
                + 1.0/Math.abs(omg[l] - omg[i] - omg[j] - omg[k]+dd)
                + 1.0/Math.abs(omg[i] + omg[j] - omg[k] - omg[l]+dd)
                + 1.0/Math.abs(omg[i] + omg[k] - omg[j] - omg[l]+dd)
                + 1.0/Math.abs(omg[i] + omg[l] - omg[j] - omg[k]+dd);
      aa = aa*cijkl*omginv1[i]*omginv1[j]*omginv1[k]*omginv1[l];
      
      return aa;
   }
   /**
    * Returns the QFF data
    * @return the QFFData
    */
   public QFFData getQFF(){
      return qff;
   }
}
