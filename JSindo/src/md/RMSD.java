package md;

import atom.AtomUtil;
import solver.*;
// import sys.*;

/**
 * Calculate RMSD. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class RMSD {

   private AtomMD[] refAtoms;
   private double[] refCOM;
   private double refE0;
   private boolean orient = true;
   private double[] currentCOM;
   private double[][] umin;

   /**
    * Sets the reference to all atoms in a system
    * @param atoms list of atoms
    */
   public void setReference(AtomList atoms){
      this.setReference(atoms.getAtomALL());
   }
   
   /**
    * Sets the reference to selected atoms in a system
    * @param atomlist index of selected atoms
    * @param sys The system
    */
   public void setReference(Integer[] atomlist, AtomList sys){
      int[] list = new int[atomlist.length];
      for(int i=0; i<atomlist.length; i++){
         list[i] = atomlist[i];
      }
      this.setReference(list, sys);
   }
   /**
    * Sets the reference to selected atoms in a system
    * @param atomlist index of selected atoms
    * @param sys The system
    */
   public void setReference(int[] atomlist, AtomList sys){
      AtomMD[] atoms = new AtomMD[atomlist.length];
      for(int n=0; n<atomlist.length; n++){
         atoms[n] = sys.getAtom(atomlist[n]);
      }
      this.setReference(atoms);
   }

   /**
    * Sets the reference atoms. 
    * @param atoms list of atoms
    */
   public void setReference(AtomMD[] atoms){
      this.refAtoms = new AtomMD[atoms.length];
      for(int n=0; n<atoms.length; n++){
         refAtoms[n] = atoms[n].clone();
      }
      
      AtomUtil autil = new AtomUtil();
      refCOM = autil.getCenterOfMass(refAtoms).getXYZCoordinates();
      refE0 = 0.0d;
      for(int i=0; i<refAtoms.length; i++){
         double[] xyz = refAtoms[i].getXYZCoordinates();
         for(int j=0; j<xyz.length; j++){
            xyz[j] -= refCOM[j];
            refE0 += xyz[j]*xyz[j];
         }
      }
   }
   
   /**
    * Orient the input coordinates to match with the reference 
    * coordinates when calculating the RMSD.
    */
   public void enableOrient(){
      orient = true;
   }
   
   /**
    * Prevent to orient the input coordinates to match with the reference 
    * coordinates when calculating the RMSD.
    */
   public void disableOrient(){
      orient = false;
   }
   
   /**
    * Calculates RMSD for a given structure. 
    * @param sys The system
    * @return RMSD
    */
   public double calc(AtomList sys){
      double rmsd = this.calc0(sys.getAtomALL());
      if(orient){
         this.orient(sys);
      }
      return rmsd;
   }
   
   /**
    * Calculates RMSD for a given structure. 
    * @param atomlist index of selected atoms
    * @param sys The system
    * @return RMSD
    */
   public double calc(Integer[] atomlist, AtomList sys){
      int[] list = new int[atomlist.length];
      for(int i=0; i<atomlist.length; i++){
         list[i] = atomlist[i];
      }
      return calc(list, sys);
   }
   
   /**
    * Calculates RMSD for a given structure. 
    * @param atomlist index of selected atoms
    * @param sys The system
    * @return RMSD
    */
   public double calc(int[] atomlist, AtomList sys){
      
      if(atomlist.length != refAtoms.length){
         System.out.println("The number of atoms in the input differs from that of reference atoms.");
         return -1;
      }
      
      AtomMD[] fitAtoms = new AtomMD[atomlist.length];
      for(int n=0; n<atomlist.length; n++){         
         fitAtoms[n] = sys.getAtom(atomlist[n]);
      }
      
      double rmsd = this.calc0(fitAtoms);
      if(orient){
         this.orient(sys);
      }
      
      return rmsd;
   }
   
   private double calc0(AtomMD[] inAtoms){
      
      int natom = inAtoms.length;
      AtomMD[] currentAtoms = new AtomMD[natom];
      for(int n=0; n<natom; n++){
         currentAtoms[n] = inAtoms[n].clone();
      }

      AtomUtil autil = new AtomUtil();
      currentCOM = autil.getCenterOfMass(currentAtoms).getXYZCoordinates();
      
      for(int n=0; n<natom; n++){
         double[] xyz = currentAtoms[n].getXYZCoordinates();
         for(int i=0; i<3; i++){
            xyz[i] -= currentCOM[i];
         }
      }
      
      double currentE0 = 0.0d;
      for(int i=0; i<natom; i++){
         double[] xyz = currentAtoms[i].getXYZCoordinates();
         for(int j=0; j<xyz.length; j++){
            currentE0 += xyz[j]*xyz[j];
         }
      }

      double[][] rmat = new double[3][3];
      for(int i=0; i<3; i++){
         for(int j=0; j<3; j++){
            rmat[i][j] = 0.0d;
            for(int n=0; n<natom; n++){
               rmat[i][j] += refAtoms[n].getXYZCoordinates()[i] * currentAtoms[n].getXYZCoordinates()[j]; 
            }
         }
      }

      SVD svd = Solver.getSVD();
      svd.calc(rmat);
      double[] ss = svd.getS();
      umin = svd.getVUt();

      /*
      System.out.println("Singular Values.");
      for(int i=0; i<ss.length; i++){
         System.out.printf("%12.6f ",ss[i]);
      }
      System.out.println();
      System.out.println();

      double[][] uu = svd.getU();
      System.out.println("U matrix.");
      for(int i=0; i<uu.length; i++){
         for(int j=0; j<uu[0].length; j++){            
            System.out.printf("%12.6f ",uu[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      
      double[][] vv = svd.getV();
      System.out.println("V matrix.");
      for(int i=0; i<vv.length; i++){
         for(int j=0; j<vv[0].length; j++){            
            System.out.printf("%12.6f ",vv[i][j]);
         }
         System.out.println();
      }
      System.out.println();
      */

      double sum=0.0d;
      for(int n=0; n<3; n++){
         sum += ss[n];
      }
      double rmsd = 0.0d;
      double xx = refE0 + currentE0 - 2.0*sum;
      if(xx > 0.0d){
         rmsd = Math.sqrt(xx/(double)natom);
      }else{
         rmsd = 0.0d;
      }

      /* debug
      double rr = 0.0d;
      for(int n=0; n<atomlist.length; n++){
         double[] xyz_r = refAtoms[n].getXYZCoordinates();
         double[] xyz_c = currentAtoms[n].getXYZCoordinates();
         for(int i=0; i<3; i++){
            rr += (xyz_r[i]-xyz_c[i])*(xyz_r[i]-xyz_c[i]);
         }
      }
      rr=Math.sqrt(rr/(double)atomlist.length)*Constants.Bohr2Angs;
      System.out.printf("%12.6f \n",rr);
      */
      
      return rmsd;
      
   }
   
   public void orient(AtomList atomlist){
      
      int natom = atomlist.getNumOfAtom();
      
      double[][] xyz = new double[natom][];
      for(int n=0; n<natom; n++){
         xyz[n] = atomlist.getAtom(n).getXYZCoordinates();
         for(int i=0; i<3; i++){
            xyz[n][i] -= currentCOM[i];
         }
      }
      
      MatrixOperation mop = Solver.getMatrixOperation();
      double[][] xyz2 = mop.multiply(xyz, umin);
      for(int n=0; n<natom; n++){
         for(int i=0; i<3; i++){
            xyz[n][i] = xyz2[n][i];
         }
      }

      for(int n=0; n<natom; n++){
         for(int i=0; i<3; i++){
            xyz[n][i] += refCOM[i];
         }
      }
   }

   public double[] calcTraj(Integer[] atomlist, SystemMD sys){
      int[] list = new int[atomlist.length];
      for(int i=0; i<atomlist.length; i++){
         list[i] = atomlist[i];
      }
      return calcTraj(list, sys);

   }
   
   /**
    * Calculates RMSD for all structures in a trajectory. 
    * @param atomlist list of index of atoms in a system
    * @param sys The system
    * @return RMSD of each snapshot
    */
   public double[] calcTraj(int[] atomlist, SystemMD sys){
   
      if(sys.getTrajectory() == null){
         System.out.println("The input system does not have a trajectory.");
         return null;
      }
      
      int nframe = sys.getTrajectory().getNumOfFrames();
      double[] rmsd = new double[nframe];
      for(int n=0; n<nframe; n++){
         sys.setFrame(n);
         rmsd[n] = this.calc(atomlist,sys);
      }      
      return rmsd;
   }
   
   
}
