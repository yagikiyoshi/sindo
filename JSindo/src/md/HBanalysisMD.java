package md;

import java.util.*;
import atom.*;

/**
 * Utility class for hydrogen bond analysis in MD. 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class HBanalysisMD extends HBanalysis {

   private Box box;
   private int[] aType;
   private int[] dType;
   private int nType;
   
   /**
    * Constructs with default settings: r(DA) = 3.5 Angs, a(D..H-A)=20.0 degree, without PBC.
    */
   public HBanalysisMD(){
      super();
      box=null;
   }
   
   /**
    * Sets a periodic box used in the simulation. Cubic box is assumed in this utility.
    * @param box The cubic box
    */
   public void setBox(Box box){
      this.box = box;
   }
   
   /**
    * Sets the type IDs for the acceptor groups. ID is assumed to be in a range of 0 - numOfType-1.
    * @param aID the IDs of the acceptor groups
    */
   public void setAcceptorID(int[] aID){
      this.aType = aID;
   }
   
   /**
    * Sets the type IDs for the donor groups. ID is assumed to be in a range of 0 - numOfType-1.
    * @param dType the IDs of the acceptor groups
    */
   public void setDonorID(int[] dType){
      this.dType = dType;
   }
   
   /**
    * Sets the number of Types
    * @param numOfType the number of types
    */
   public void setNumOfID(int numOfType){
      this.nType = numOfType;
   }
   public boolean isHB(Atom[] D, Atom A){
      return this.isHB(D[0], D[1], A);
   }

   public boolean isHB(Atom D, Atom H, Atom A){
      if(box == null){
         return super.isHB(D, H, A);
      }else{
         if(D.equals(A)){
            return false;
         }
         Atom A1 = A.clone();
         double[] xyzA1 = A1.getXYZCoordinates();
         double[] xyzD = D.getXYZCoordinates();
         double[] boxsize = box.getBox();
         
         double[] vec = new double[3];
         for(int i=0; i<3; i++){
            vec[i] = 0.0d;
         }
         boolean shift = false;
         for(int i=0; i<3; i++){
            if((xyzD[i] - xyzA1[i]) > boxsize[i]*0.5d) {
               vec[i] = boxsize[i];
               shift = true;
            } else if((xyzD[i] - xyzA1[i]) < -boxsize[i]*0.5d){
               vec[i] =-boxsize[i];
               shift = true;
            }
         }
         if(shift){
            A1.shift(vec);
         }
         
         return super.isHB(D, H, A1);
      }      
   }
   
   /**
    * Returns the connectivity of donor-acceptor groups.
    * @return Integer[NumOfResidue][Type ID of the hydrogen bonded residues]
    */
   public Integer[][] getConnectivity(){
      
      if(! super.isCalcHB){
         super.calcHB();
      }
      
      @SuppressWarnings("unchecked")
      ArrayList<Integer>[] typeList = new ArrayList[nType];
      for(int i=0; i<typeList.length; i++){
         typeList[i] = new ArrayList<Integer>();
      }

      for(int i=0; i<byAcceptor.length; i++){
         for(int j=0; j<byAcceptor[i].length; j++){
            int aID = aType[i];
            int dID = dType[byAcceptor[i][j]];
            if(aID != dID){
               if(typeList[aID].indexOf(dID) == -1){
                  typeList[aID].add(dID);
               }
               if(typeList[dID].indexOf(aID) == -1){
                  typeList[dID].add(aID);
               }
            }
         }
      }
      
      Integer[][] byType = new Integer[nType][];
      for(int i=0; i<typeList.length; i++){
         byType[i] = typeList[i].toArray(new Integer[0]);
      }
      return byType;
   }
 
   public Integer[][] clusterize(){
      Integer[][] byType = this.getConnectivity();
      
      @SuppressWarnings("unchecked")
      ArrayList<Integer>[] clusterList = new ArrayList[nType];

      boolean[] isSelected = new boolean[nType];
      for(int i=0; i<isSelected.length; i++){
         isSelected[i] = false;
      }
      
      int nClust = 0;
      for(int i=0; i<nType; i++){
         if(isSelected[i]) continue;

         ArrayList<Integer> clust = new ArrayList<Integer>();
         clusterList[nClust] = clust;
         nClust++;

         clust.add(i);
         isSelected[i] = true;
         int prevSize = 0;
         int currSize = 1;
         while(prevSize < currSize){
            for(int j=prevSize; j<currSize; j++){
               int k1 = clust.get(j);
               for(int k2=0; k2<byType[k1].length; k2++){
                  if(! isSelected[byType[k1][k2]]){
                     clust.add(byType[k1][k2]);
                     isSelected[byType[k1][k2]] = true;
                  }
               }
            }
            prevSize = currSize;
            currSize = clust.size();
      }
    }

    Integer[][] cluster = new Integer[nClust][];
    for(int i=0; i<cluster.length; i++){
       cluster[i] = clusterList[i].toArray(new Integer[0]);
    }
    return cluster;

   }
}
