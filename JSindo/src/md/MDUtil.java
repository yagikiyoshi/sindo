package md;

import java.util.*;
import sys.Constants;
import sys.Utilities;
import atom.AtomUtil;

/**
 * Provides driver routines for manipulating the system.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class MDUtil {

   private Box box;
   private double[] boxsize;

   /**
    * Appends a box
    * @param box Box of the simulation
    */
   public void appendBox(Box box){
      this.box = box;
      boxsize = new double[3];
      boxsize[0] = box.getXsize()*0.5d;
      boxsize[1] = box.getYsize()*0.5d;
      boxsize[2] = box.getZsize()*0.5d;
   }

   /**
    * Shift the position of the atom by an input vector for a given AtomList holder
    * @param vector the shifting vector
    * @param atoms the AtomList holder (Residue, Segment, SystemMD) in which the atoms are to be shifted
    */
   public void shift(double[] vector, AtomList atoms){
      AtomMD[] atomList = atoms.getAtomALL();
      for(int i=0; i<atomList.length; i++){
         atomList[i].shift(vector);
      }
   }

   /**
    * Shifts the current residue to a mirror image closest to an input 
    * position. Does nothing if the Box is not appended.
    * @param position double[3] The position in Cartesian coordinates (bohr)
    * @param atoms AtomList holder (Residue, Segment, SystemMD)
    */
   public void shiftToPBCImage(double[] position, AtomList atoms){
      if(box == null) return;
      
      double[] mycom = this.getCenterOfMass(atoms);
      
      double[] vec = new double[3];
      for(int i=0; i<3; i++){
         vec[i] = 0.0d;
         double d0 = position[i] - mycom[i];
         /*
         if(d0 > boxsize[i]){
            vec[i] = box.getBox()[i];
         }else if(d0 < -boxsize[i]){
            vec[i] = -box.getBox()[i];
         }
         */
         int idx = (int)Math.round(d0/box.getBox()[i]);
         vec[i] = box.getBox()[i]*idx;
      }
      
      this.shift(vec,atoms);
   }

   /**
    * Shifts the current residue to a mirror image to the current box. 
    * Does nothing if the Box is not appended.
    * @param atoms AtomList holder (Residue, Segment, SystemMD)
    */
   public void shiftToPBCImage(AtomList atoms){
      double[] pos = new double[3];
      this.shiftToPBCImage(pos, atoms);
   }

   /**
    * Returns the center of mass 
    * @param atoms the AtomList holder (Residue, Segment, SystemMD)
    * @return the center of mass 
    */
   public double[] getCenterOfMass(AtomList atoms){
      AtomMD[] atomList = atoms.getAtomALL();
      AtomUtil autil = new AtomUtil();
      return autil.getCenterOfMass(atomList).getXYZCoordinates();      
   }

   /**
    * Renumber the ID of atom starting from a given number
    * @param nn the start number
    * @param atoms the AtomList holder (Residue, Segment, SystemMD) in which the atom IDs are to be changed
    */
   public void reNumberAtom(int nn, AtomList atoms){
      AtomMD[] atomList = atoms.getAtomALL();
      for(int i=0; i<atomList.length; i++){
         atomList[i].setID(nn);
         nn++;
      }
   }
   
   /**
    * Renumber the ID of residue starting from a given number
    * @param nn the start number
    * @param residues the ResidueList holder (Segment, SystemMD) in which the residue IDs are to be changed
    */
   public void reNumberResidue(int nn, ResidueList residues){
      Residue[] resList = residues.getResidueALL();
      for(int i=0; i<resList.length; i++){
         resList[i].setID(nn);
         nn++;
      }
   }
   
   /**
    * Terminate with hydrogen. 
    * @param aa The atom to be terminated with hydrogen
    * @param bb The atom is turned into hydrogen, shifted to a 1.0 Angs distance of aa.
    */
   public void termWithHydrogen(AtomMD aa, AtomMD bb){
      bb.setLabel("H");
      bb.setAtomicNum(1);
      
      double[] axyz = aa.getXYZCoordinates();
      double[] bxyz = bb.getXYZCoordinates();
      double[] vv = new double[3];
      for(int n=0; n<vv.length; n++){
         vv[n] = bxyz[n] - axyz[n];
      }
      Utilities.normalize(vv);
      
      for(int n=0; n<axyz.length; n++){
         bxyz[n] = axyz[n] + vv[n]/Constants.Bohr2Angs;
      }
      
   }

   /**
    * Style check: convert PDB native to MD force field synonym (still under construction)
    * @param sysMD The system to be checked (each segment has to be one protein, so that the last residue is the C-terminus)
    */
   public void styleCheck(SystemMD sysMD){

      sysMD.openSegmentList();
      
      try{
         ArrayList<Segment> segments = sysMD.getSegmentList();
      
         for(int nseg=0; nseg<segments.size(); nseg++){
            Segment current_seg=segments.get(nseg);
            ArrayList<Residue> residues = current_seg.getResidueList();
            
            for(int nres=0; nres<residues.size(); nres++){
               Residue current_res = residues.get(nres);
               
               // ILE: CD1 -> CD
               if(current_res.getName().equals("ILE")){
                  int Nat = current_res.getAtomList().size();
                  for(int natom=0; natom<Nat; natom++){
                     AtomMD atom = current_res.getAtomList().get(natom);
                     if(atom.getLabel().equals("CD1")){
                        atom.setLabel("CD");
                     }
                  }
               }
               
               // HIS -> HSD
               if(current_res.getName().equals("HIS")){
                  current_res.setName("HSD");
               }
               
            }
            
            // C terminus (COO-): O -> OT1, OXT -> OT2
            Residue last_residue = residues.get(residues.size()-1);
            int Nat = last_residue.getAtomList().size();
            
            boolean coo = false;
            for(int natom=0; natom<Nat; natom++){
               AtomMD atom = last_residue.getAtomList().get(natom);
               if(atom.getLabel().equals("OXT")){
                  coo = true;
                  break;
               }
            }
            
            if(coo){
               for(int natom=0; natom<Nat; natom++){
                  AtomMD atom = last_residue.getAtomList().get(natom);
                  String name = atom.getLabel();
                  if(name.equals("O")){
                     atom.setLabel("OT1");
                  }
                  if(name.equals("OXT")){
                     atom.setLabel("OT2");
                  }
               }
            }
         }
         
      }catch(BuilderException e){
         e.printStackTrace();
      }

      sysMD.closeSegmentList();
      
   }

   /**
    * Returns a subspace of given trajectory
    * @param inpTraj input trajectory
    * @param index indices of atoms that defines a subspace
    * @return Trajectory of the subspace
    */
   public Trajectory subSpaceTraj(Trajectory inpTraj, int[] index){
      Trajectory traj = new Trajectory();
      for(int n=0; n<inpTraj.getNumOfFrames(); n++){
         double[][] inpCoord = inpTraj.getCoordinates(n);
         double[][] coord = new double[index.length][];
         for(int i=0; i<index.length; i++){
            coord[i] = inpCoord[index[i]];
         }
         traj.addCoordinates(coord);
      }
      return traj;
   }
   
   /**
    * Delete atom from the system
    * @param atom_index index of atom to be deleted (0, 1, 2, ..., Natom)
    * @param sys_in SystemMD
    * @throws BuilderException Exception while building the system 
    */
   public void deleteAtom(Integer[] atom_index, SystemMD sys_in) throws BuilderException{
      
      Integer[] segid = new Integer[atom_index.length];
      Integer[] resid = new Integer[atom_index.length];
      Integer[] atmid = new Integer[atom_index.length];
      
      for(int nd=0; nd<atom_index.length; nd++){
         int nn = 0;
         for(int ns=0; ns<sys_in.getNumOfSegment(); ns++){
            if(atom_index[nd] < nn + sys_in.getSegment(ns).getNumOfAtom()){
               segid[nd] = ns;
               break;
            }
            nn += sys_in.getSegment(ns).getNumOfAtom();
         }
         
         Segment seg = sys_in.getSegment(segid[nd]);
         for(int nr = 0; nr<seg.getNumOfResidue(); nr++){
            if(atom_index[nd] < nn + seg.getResidue(nr).getNumOfAtom()){
               resid[nd] = nr;
               break;
            }
            nn += seg.getResidue(nr).getNumOfAtom();
         }
         
         atmid[nd] = atom_index[nd] - nn;

         /*
         System.out.println(nd);
         System.out.println("segid = "+segid[nd]);
         System.out.println("resid = "+resid[nd]);
         System.out.println("atmid = "+atmid[nd]);
         System.out.println();
         */
      }

      sys_in.openSegmentList();
      for(int nd=0; nd<atom_index.length; nd++){
         /*
         System.out.println(nd);
         System.out.println("segid = "+segid[nd]);
         System.out.println("resid = "+resid[nd]);
         System.out.println("atmid = "+atmid[nd]);
         System.out.println();
         */
         Segment seg = sys_in.getSegmentList(segid[nd]);
         Residue res = seg.getResidueList(resid[nd]);
         res.removeAtomList(atmid[nd]);
         for(int md=nd+1; md<atom_index.length; md++){
            /* The following means this:
            if(segid[nd] == segid[md] && resid[nd] == resid[md] && 
               atmid[nd]  < atmid[md]){
            */
            if(segid[nd].compareTo(segid[md]) == 0 &&
               resid[nd].compareTo(resid[md]) == 0 &&
               atmid[nd].compareTo(atmid[md]) == -1){
               
               atmid[md] -= 1;

            }
         }
         
         if(res.getAtomList().size() == 0){
            seg.removeResidueList(resid[nd]);
            for(int md=nd+1; md<atom_index.length; md++){
               //if(segid[nd] == segid[md] && resid[nd] < resid[md]){
               if(segid[nd].compareTo(segid[md]) == 0 &&
                  resid[nd].compareTo(resid[md]) == -1){

                  resid[md] -= 1;
               }
            }
         }
         
         if(seg.getResidueList().size() == 0){
            sys_in.removeSegmentList(segid[nd]);
            for(int md=nd+1; md<atom_index.length; md++){
               //if(segid[nd] < segid[md]){
               if(segid[nd].compareTo(segid[md]) == -1){
                  segid[md] -= 1;
               }
            }
         }
      }

      sys_in.closeSegmentList();
      
   }
   
}
