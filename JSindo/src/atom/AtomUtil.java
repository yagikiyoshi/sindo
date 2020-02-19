package atom;

import sys.Utilities;

/**
 * Provides utilities that deal with Atom class.
 * This class provides a way to calculate: <br>
 * <ol>
 * <li> Bond length (bohr) </li>
 * <li> Bond angle (degree) </li>
 * <li> Dihedral angles (angle) </li>
 * <li> New xyz coordinates based on Z-matrix input </li>
 * <li> Center of mass </li>
 * </ol>
 * 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class AtomUtil {

   /**
    * Returns the bond length (bohr)
    * @param atomi atom i
    * @param atomj atom j
    * @return bond length (bohr)
   */
   public double getBondLength(Atom atomi, Atom atomj){
      return this.getBondLength(atomi.getXYZCoordinates(), atomj.getXYZCoordinates());
   }
   /**
    * Returns the bond length (bohr)
    * @param atomi coordinates of atom i
    * @param atomj coordinates of atom j
    * @return bond length (bohr)
   */
   public double getBondLength(double[] atomi, double[] atomj){
      return Utilities.getNorm(atomi, atomj);
   }
   /**
    * Returns the bond angle of i-j-k  (degree)
    * @param atomi atom i
    * @param atomj atom j
    * @param atomk atom k
    * @return bond angle i-j-k (degree)
    */
   public double getBondAngle(Atom atomi, Atom atomj, Atom atomk){
      double[] dd = new double[2];
      return getBondAngle(atomi, atomj, atomk, dd);
   }

   /**
    * Returns the bond angle of i-j-k  (degree) and the bond lengths dji and djk (bohr)
    * @param atomi atom i
    * @param atomj atom j
    * @param atomk atom k
    * @param dd Distance between j-i and j-k: [0] dji, [1] djk
    * @return bond angle i-j-k (degree)
    */
   public double getBondAngle(Atom atomi, Atom atomj, Atom atomk, double[] dd){
      double[] ai = atomi.getXYZCoordinates();
      double[] aj = atomj.getXYZCoordinates();
      double[] ak = atomk.getXYZCoordinates();
      return this.getBondAngle(ai, aj, ak, dd);
   }
   
   /**
    * Returns the bond angle of i-j-k  (degree)
    * @param ai xyz coordinates of atom i
    * @param aj xyz coordinates of atom j
    * @param ak xyz coordinates of atom k
    * @return bond angle i-j-k (degree)
    */
   public double getBondAngle(double[] ai, double[] aj, double[] ak){
      double[] dd = new double[2];
      return getBondAngle(ai, aj, ak, dd);
   }
   
   /**
    * Returns the bond angle of i-j-k  (degree) and the bond lengths dji and djk (bohr)
    * @param ai xyz coordinates of atom i
    * @param aj xyz coordinates of atom j
    * @param ak xyz coordinates of atom k
    * @param dd Distance between j-i and j-k: [0] dji, [1] djk
    * @return bond angle i-j-k (degree)
    */
   public double getBondAngle(double[] ai, double[] aj, double[] ak, double[] dd){
      double[] rji = new double[3];
      double[] rjk = new double[3];
      for(int n=0; n<3; n++){
         rji[n] = ai[n] - aj[n];
         rjk[n] = ak[n] - aj[n];
      }
      dd[0] = Utilities.normalize(rji);
      dd[1] = Utilities.normalize(rjk);
      double ovlp = Utilities.dotProduct(rji, rjk);
      return Math.acos(ovlp)/Math.PI*180.0d;      
   }
   
   /**
    * Returns the dihedral angle between i-j-k and j-k-l
    * @param atomi atom i
    * @param atomj atom j
    * @param atomk atom k
    * @param atoml atom l
    * @return dihedral angle ijk-jkl (degree). NaN if either ijk or jkl are in line.
    */
   public double getDihedralAngle(Atom atomi, Atom atomj, Atom atomk, Atom atoml){
      double[] ai = atomi.getXYZCoordinates();
      double[] aj = atomj.getXYZCoordinates();
      double[] ak = atomk.getXYZCoordinates();
      double[] al = atoml.getXYZCoordinates();

      return this.getDihedralAngle(ai, aj, ak, al);
   }
   
   /**
    * Returns the dihedral angle between i-j-k and j-k-l
    * @param ai xyz coordinates of atom i
    * @param aj xyz coordinates of atom j
    * @param ak xyz coordinates of atom k
    * @param al xyz coordinates of atom l
    * @return dihedral angle ijk-jkl (degree). NaN if either ijk or jkl are in line.
    */
   public double getDihedralAngle(double[] ai, double[] aj, double[] ak, double[] al){
      
      double[] vji = new double[3]; 
      double[] vjk = new double[3];
      double[] vjl = new double[3];
      for(int n=0; n<3; n++){
         vji[n] = ai[n] - aj[n];
         vjk[n] = ak[n] - aj[n];
         vjl[n] = al[n] - aj[n];
      }

      Utilities.normalize(vjk);

      double ajk = Utilities.dotProduct(vji, vjk);
      for(int n=0; n<3; n++){
         vji[n] = vji[n] - ajk*vjk[n];
      }
      double dd = Utilities.normalize(vji);
      if(dd < 1e-06){
         return Double.NaN;
      }
      
      double ajl = Utilities.dotProduct(vjl, vjk);
      for(int n=0; n<3; n++){
         vjl[n] = vjl[n] - ajl*vjk[n];
      }
      dd = Utilities.normalize(vjl);
      if(dd < 1e-06){
         return Double.NaN;
      }

      double mod = Utilities.dotProduct(vji, vjl);
      
      double dihed = 0.0;
      if(mod > 1.0){
         dihed = 0.0;
      }else if(mod < -1.0){
         dihed = 180.0;
      }else{
         dihed = Math.acos(mod)/Math.PI*180.0;
      }
      
      double[] aa = Utilities.vectorProduct(vjk, vji);
      double ss = Utilities.dotProduct(aa, vjl);
      //System.out.println(ss);
      if(ss < 0.0){
         dihed = -dihed;
      }
      
      return dihed;
   }
   /**
    * Generates a position of an atom based on Z-matrix input
    * @param atm1 Atom1
    * @param rr   Distance between 0-1
    * @param atm2 Atom2
    * @param thet Angle of 0-1-2 (degree)
    * @param atm3 Atom3
    * @param phi  Dihedral angle of 0-1-2-3 (degree)
    * @return xyz coordinates of the new atom (bohr)
    */
   public double[] genAtomXYZ(Atom atm1, double rr, Atom atm2, double thet, Atom atm3, double phi){
      
      thet = thet/180.0*Math.PI;
      //phi = phi/180.0*Math.PI;
      
      double[] a1xyz = atm1.getXYZCoordinates();
      double[] a2xyz = atm2.getXYZCoordinates();
      double[] a3xyz = atm3.getXYZCoordinates();
      
      double[] v3 = Utilities.rotZ(a2xyz, a1xyz, a3xyz, phi);
      
      double[] v21 = new double[3];
      for(int n=0; n<3; n++){
         v21[n] = a1xyz[n] - a2xyz[n];
      }
      Utilities.normalize(v21);
      
      for(int n=0; n<3; n++){
         v3[n] = v3[n] - a2xyz[n];
      }
      double aa = Utilities.dotProduct(v3, v21);
      for(int n=0; n<3; n++){
         v3[n] = v3[n] - aa*v21[n];
      }
      Utilities.normalize(v3);
      
      double[] a4xyz = new double[3];
      for(int n=0; n<3; n++){
         a4xyz[n] = a1xyz[n] - rr*Math.cos(thet)*v21[n] + rr*Math.sin(thet)*v3[n];
      }
      
      return a4xyz;
   }
   /**
    * Calculates the center of mass of input atoms
    * @param atoms The input atoms
    * @return Atom with the center of mass coordinates and total mass
    */
   public Atom getCenterOfMass(Atom[] atoms){
      Atom com = new Atom();
      com.setLabel("COM");
      
      double[] xyz = new double[3];
      for(int i=0; i<3; i++){
         xyz[i] = 0.0d;
      }
      
      double totmass=0.0d;
      for(int i=0; i<atoms.length; i++){
         double massi = atoms[i].getMass();
         double[] xyzi = atoms[i].getXYZCoordinates();

         totmass = totmass + massi;
         for(int j=0; j<3; j++){
            xyz[j] = xyz[j] + xyzi[j]*massi;
         }
      }
      for(int i=0; i<3; i++){
         xyz[i] = xyz[i]/totmass;
      }
      
      com.setMass(totmass);
      com.setXYZCoordinates(xyz);
      
      return com;
   }
   
}
