package md;

/**
 * Calculate radial distribution functions (RDF) 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 4.0
 */
public class RadialDistribution {
   
   private SystemMD system;
   private int[] idxA = null;
   private int[] idxB = null;
   private double rmax, rmin, dr;
   private int startFrame=0;
   private int endFrame=-1;
   private double inp_vol = -1.0;
   private double[] numberDensity = null;
   
   /**
    * Sets the system
    * @param system System
    */
   public void setSystem(SystemMD system) {
      this.system = system;
   }
   
   /**
    * Sets the indices of atom A to calculate RDF of A-A
    * @param centerA indices of atom A
    */
   public void setCenterIndex(int[] centerA) {
      this.idxA = centerA;
      this.idxB = null;
   }

   /**
    * Sets the indices of atom A and B to calculate RDF of A-B
    * @param centerA indices of atom A
    * @param centerB indices of atom B
    */
   public void setCenterIndex(int[] centerA, int[] centerB) {
      this.idxA = centerA;
      this.idxB = centerB;
   }
   
   /**
    * Sets the range of RDF
    * @param rmin minimum of r
    * @param rmax maximum of r
    * @param dr   interval
    */
   public void setRange(double rmin, double rmax, double dr) {
      this.rmin = rmin;
      this.rmax = rmax;
      this.dr   = dr;
   }
   
   /**
    * Sets the starting and ending frame number for the analysis
    * @param startFrame starting frame
    * @param endFrame   ending frame
    */
   public void setFrame(int startFrame, int endFrame) {
      this.startFrame = startFrame;
      this.endFrame   = endFrame;
   }

   /**
    * Sets the volume of the system. Needed for non-PBC system
    * @param volume the volume of the system
    */
   public void setVolume(double volume) {
      this.inp_vol = volume;
   }
   
   /**
    * Calculate the RDF
    * @return returns the RDF from rmin to rmax every dr
    */
   public double[] calcRDF() {
      int ngrid = (int) ((rmax - rmin)/dr);
      double[] rdf = new double[ngrid];
      
      Trajectory traj = system.getTrajectory();

      int sframe, eframe;
      if(this.startFrame < 0) {
         sframe = 0;
      }else {
         sframe = this.startFrame;
      }
      
      if(this.endFrame < 0) {
         eframe = traj.getNumOfFrames();
      }else {
         eframe = this.endFrame+1;
      }

      double vol   = 0.0;
      double rho   = 0.0;
      if(traj.isBoxes()) {
         for (int n=sframe; n<eframe; n++) {
            Box box = traj.getBox(n);
            double[] boxsize = new double[3];
            boxsize[0] = box.getXsize();
            boxsize[1] = box.getYsize();
            boxsize[2] = box.getZsize();
            vol += boxsize[0]*boxsize[1]*boxsize[2];
         }
         vol = vol/(double)(eframe - sframe);
         
      } else if (this.inp_vol > 0.0) {
         vol = this.inp_vol;
      }
      
      if(idxB != null) {
         rho = (double)(idxB.length)/vol;
      }else {
         rho = (double)(idxA.length)/vol;
      }

      numberDensity = new double[ngrid];
      for (int n=sframe; n<eframe; n++) {

         double[] boxsize = null;
         if(traj.isBoxes()) {
            Box box = traj.getBox(n);
            boxsize = new double[3];
            boxsize[0] = box.getXsize();
            boxsize[1] = box.getYsize();
            boxsize[2] = box.getZsize();
         }

         if (idxB != null) {

            for (int i=0; i<idxA.length; i++) {
               double[] ai = traj.getCoordinates(n)[idxA[i]];

               for(int j=0; j<idxB.length; j++) {
                  double[] aj = traj.getCoordinates(n)[idxB[j]];
                  
                  double rr = this.calcRij(ai, aj, boxsize);
                  if (rr > rmin && rr < rmax) {
                     int ng = (int)((rr-rmin)/dr);
                     numberDensity[ng]+=1.0;
                  }
                  
               }
            }
            
         } else {

            for (int i=0; i<idxA.length; i++) {
               double[] ai = traj.getCoordinates(n)[idxA[i]];

               for(int j=0; j<i; j++) {
                  double[] aj = traj.getCoordinates(n)[idxA[j]];
                  
                  double rr = this.calcRij(ai, aj, boxsize);
                  if (rr > rmin && rr < rmax) {
                     int ng = (int)((rr-rmin)/dr);
                     numberDensity[ng]+=2.0;
                  }
                  
               }
            }
            
         }
      }
      
      for(int ng = 0; ng<rdf.length; ng++) {
         numberDensity[ng] = numberDensity[ng]/(double)((eframe - sframe)*idxA.length);
      }
      
      for(int ng = 0; ng<rdf.length; ng++) {
         double rr = rmin+dr*ng+dr/2.0;
         rdf[ng] = numberDensity[ng]/(4.0*Math.PI*dr*rr*rr*rho);
      }
      
      return rdf;
      
   }
   
   /**
    * Gets the number density
    * @return the number density
    */
   public double[] getNumberDensity() {
      return numberDensity;
      
   }
   
   private double calcRij(double[] ai, double[] aj, double[] boxsize) {
      
      double[] rij = new double[3];
      for(int k=0; k<rij.length; k++) {
         rij[k] = ai[k] - aj[k];
      }
      
      if(boxsize != null) {
         for(int xx=0; xx<boxsize.length; xx++) {
            if(rij[xx] > boxsize[xx]/2.0d) {
               rij[xx] -= boxsize[xx];
            }else if (rij[xx] < -boxsize[xx]/2.0d) {
               rij[xx] += boxsize[xx];
            }                        
         }
      }
      
      double rr = 0.0d;
      for(int k=0; k<rij.length; k++) {
         rr += rij[k]*rij[k];
      }
      rr = Math.sqrt(rr);

      return rr;
   }
   

}
