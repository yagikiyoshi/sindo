package md;

import java.util.ArrayList;
import java.util.HashMap;
import ff.*;
import atom.AtomUtil;

/**
 * Potential energy function based on CHARMM Force Field
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class PotCHARMM implements Potential {

   private FuncBond[] bondFunc;
   private int[][] bondPair;
   
   private FuncAngle[] angleFunc;
   private int[][] anglePair;
   
   private FuncUB[] ubFunc;
   private int[][] ubPair;
   
   private FuncDihed[] dihedFunc;
   private int[][] dihedPair;
   
   private HashMap<String,Double> energyComp;
   
   public void setupBonds(int[][] pair, AtomMD[] atoms, ForceField ff){
      this.bondPair = pair;
      
      int nb = pair.length;
      bondFunc = new FuncBond[nb];
      
      for(int n=0; n<nb; n++){
         String typ1 = atoms[pair[n][0]].getType();
         String typ2 = atoms[pair[n][1]].getType();
         bondFunc[n] = ff.getBond(typ1, typ2);
      }
      
   }
   
   public void setupAngleUB(int[][] pair, AtomMD[] atoms, ForceField ff){
      ArrayList<FuncUB> ubflist = new ArrayList<FuncUB>();
      ArrayList<int[]> ubplist = new ArrayList<int[]>();
      
      this.anglePair = pair;
      int na = pair.length;
      this.angleFunc = new FuncAngle[na];
      
      for(int n=0; n<na; n++){
         String typ1 = atoms[pair[n][0]].getType();
         String typ2 = atoms[pair[n][1]].getType();
         String typ3 = atoms[pair[n][2]].getType();
         this.angleFunc[n] = ff.getAngle(typ1, typ2, typ3);
         
         FuncUB ubf = ff.getUreyBradley(typ1, typ2, typ3);
         if(ubf != null){
            ubflist.add(ubf);
            int[] ubpair = new int[2];
            ubpair[0] = pair[n][0];
            ubpair[1] = pair[n][2];
            ubplist.add(ubpair);
         }
      }
      
      this.ubPair = new int[ubplist.size()][2];
      for(int n=0; n<ubplist.size(); n++){
         ubPair[n] = ubplist.get(n);
      }
      this.ubFunc = ubflist.toArray(new FuncUB[0]);
      
   }
   
   public void setupDihedral(int[][] pair, AtomMD[] atoms, ForceField ff){
      this.dihedPair = pair;
      int nd = pair.length; 
      this.dihedFunc = new FuncDihed[nd];
      
      for(int n=0; n<nd; n++){
         String typ1 = atoms[pair[n][0]].getType();
         String typ2 = atoms[pair[n][1]].getType();
         String typ3 = atoms[pair[n][2]].getType();
         String typ4 = atoms[pair[n][3]].getType();
         this.dihedFunc[n] = ff.getDiheral(typ1, typ2, typ3, typ4);
         
      }
   }
   
   //TODO
   public void setQMatoms(int[] qmatoms){
      
      ArrayList<FuncBond> bflist = new ArrayList<FuncBond>();
      ArrayList<int[]> pairlist = new ArrayList<int[]>();
      
      for(int i=0; i<bondPair.length; i++){
         
         boolean isBf = true;
         for(int j=0; j<bondPair[i].length; j++){
            for(int k=0; k<qmatoms.length; k++){
               if(bondPair[i][j] == qmatoms[k]){
                  isBf = false;
                  break;
               }
            }
            if(! isBf) break;
         }
         if(isBf){
            bflist.add(bondFunc[i]);
            pairlist.add(bondPair[i]);
         }
      }
      this.bondPair = new int[pairlist.size()][2];
      for(int n=0; n<pairlist.size(); n++){
         bondPair[n] = pairlist.get(n);
      }
      this.bondFunc = bflist.toArray(new FuncBond[0]);
      
      for(int n=0; n<bondPair.length; n++){
         System.out.printf("%4d %4d \n", bondPair[n][0], bondPair[n][1]);
      }
      System.out.println();
    
      ArrayList<FuncAngle> aflist = new ArrayList<FuncAngle>();
      pairlist = new ArrayList<int[]>();
      
      for(int i=0; i<anglePair.length; i++){
         
         boolean isAf = true;
         for(int j=0; j<anglePair[i].length; j++){
            for(int k=0; k<qmatoms.length; k++){
               if(anglePair[i][j] == qmatoms[k]){
                  isAf = false;
                  break;
               }
            }
            if(! isAf) break;
         }
         if(isAf){
            aflist.add(angleFunc[i]);
            pairlist.add(anglePair[i]);
         }
      }
      this.anglePair = new int[pairlist.size()][2];
      for(int n=0; n<pairlist.size(); n++){
         anglePair[n] = pairlist.get(n);
      }
      this.angleFunc = aflist.toArray(new FuncAngle[0]);
      
      for(int n=0; n<anglePair.length; n++){
         System.out.printf("%4d %4d %4d \n", anglePair[n][0], anglePair[n][1], anglePair[n][2]);
      }
      System.out.println();

   }
   
   @Override
   public double getEnergy(AtomMD[] atoms) {
      
      AtomUtil autil = new AtomUtil();
      
      double energy = 0.0;
      energyComp = new HashMap<String,Double>();
      
      double bondEnergy = 0.0;
      for(int i=0; i<bondPair.length; i++){
         int i1 = bondPair[i][0];
         int i2 = bondPair[i][1];
         double r12 = autil.getBondLength(atoms[i1], atoms[i2]);
         bondEnergy = bondEnergy + bondFunc[i].getEnergy(r12);
      }
      energy = energy + bondEnergy;
      energyComp.put("BOND", bondEnergy);
      
      double angleEnergy = 0.0;
      for(int i=0; i<anglePair.length; i++){
         int i1 = anglePair[i][0];
         int i2 = anglePair[i][1];
         int i3 = anglePair[i][2];
         double theta = autil.getBondAngle(atoms[i1], atoms[i2], atoms[i3]);
         angleEnergy = angleEnergy + angleFunc[i].getEnergy(theta);
      }
      energy = energy + angleEnergy;
      energyComp.put("ANGLE", angleEnergy);
      
      double ubEnergy = 0.0;
      for(int i=0; i<ubPair.length; i++){
         int i1 = ubPair[i][0];
         int i2 = ubPair[i][1];
         double ss = autil.getBondLength(atoms[i1], atoms[i2]);
         ubEnergy = ubEnergy + ubFunc[i].getEnergy(ss);
      }
      energy = energy + ubEnergy;
      energyComp.put("UREY-BRADLEY", ubEnergy);
      
      double dihedEnergy = 0.0;
      for(int i=0; i<dihedPair.length; i++){
         int i1 = dihedPair[i][0];
         int i2 = dihedPair[i][1];
         int i3 = dihedPair[i][2];
         int i4 = dihedPair[i][3];
         double chi = autil.getDihedralAngle(atoms[i1], atoms[i2], atoms[i3], atoms[i4]);
         double ene = dihedFunc[i].getEnergy(chi);
         dihedEnergy = dihedEnergy + ene;
      }
      energy = energy + dihedEnergy;
      energyComp.put("DIHEDRAL", dihedEnergy);
      
      return energy;
   }

   @Override
   public HashMap<String,Double> getEnergyComponent(){
      return energyComp;
   }

   @Override
   public double[][] getGradient(AtomMD[] atoms) {
      // TODO Auto-generated method stub
      return null;
   }

}
