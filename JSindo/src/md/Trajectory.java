package md;

import java.util.*;

/**
 * Store and manage the information of the trajectory
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class Trajectory {

   private ArrayList<double[][]> coor;
   private ArrayList<Box> boxes;
   
   public Trajectory(){
      coor = new ArrayList<double[][]>();
      boxes = new ArrayList<Box>();
   }
   /**
    * Returns the number of frames in the trajectory
    * @return the number of frames
    */
   public int getNumOfFrames(){
      return coor.size();
   }
   /**
    * Adds the xyz coordinates to the end of collection
    * @param xyz The coordinates to be added in Bohr (double[Nat][3])
    */
   public void addCoordinates(double[][] xyz){
      this.coor.add(xyz);
   }
   
   /**
    * Returns the coordinates at the n-th step in the trajectory
    * @param n the index of trajectory
    * @return the coordinates (double[Nat][3])
    */
   public double[][] getCoordinates(int n){
      return this.coor.get(n);
   }
   
   /**
    * Adds the box to the end of collection
    * @param box The box to be added
    */
   public void addBox(Box box){
      this.boxes.add(box);
   }
   
   /**
    * Returns the box at the n-th step in the trajectory
    * @param n the index of trajectory
    * @return the box 
    */
   public Box getBox(int n){
      return this.boxes.get(n);
   }
   
   /**
    * Returns true if the Box exists along the trajectory 
    * @return true or false
    */
   public boolean isBoxes(){
      if(boxes.size() > 1){
         return true;
      }else{
         return false;
      }
   }
   
   /**
    * Reduce the size of trajectory
    * @param n The new trajectory takes every n-step of the original one, i.e., 0, (n-1), (2n-1), ...
    * @return New trajectory
    */
   public Trajectory reduce(int n){
      int nf = this.getNumOfFrames();
      Trajectory traj = new Trajectory();
      for(int nn=0; nn<nf; nn+=n){
         traj.addCoordinates(coor.get(nn));
      }
      
      if(this.isBoxes()){
         for(int nn=0; nn<nf; nn+=n){
            traj.addBox(boxes.get(nn));
         }         
      }
      return traj;
   }
   
}
