package ff;

import sys.Utilities;

/**
 * Provides force functions.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public abstract class ForceField {

   protected FuncBond[] bonds;
   protected FuncAngle[] angles;
   protected FuncUB[] ureyBradley;
   protected FuncDihed[] diheds;
   protected FuncDihed[] dihedx;
   protected VdW[] vdws;
   
   /**
    * Returns the bond function for specified atom types
    * @param atm1 The type of atom1
    * @param atm2 The type of atom2
    * @return the bond function
    */
   public FuncBond getBond(String atm1, String atm2){
      FuncBond bondf = null;
      String[] atm = Utilities.sort(atm1, atm2);
      for(int i=0; i<bonds.length; i++){
         String[] type = bonds[i].getAtomType();
         if(atm[0].equals(type[0]) && atm[1].equals(type[1])){
            bondf = bonds[i];
            break;
         }
      }
      return bondf;
   }
   
   /**
    * Returns the angle function for specified atom types
    * @param atm1 The type of atom1
    * @param atm2 The type of atom2
    * @param atm3 The type of atom3
    * @return the angle function
    */
   public FuncAngle getAngle(String atm1, String atm2, String atm3){
      FuncAngle anglef = null;
      String[] atm = Utilities.sort(atm1,atm3);
      for(int i=0; i<angles.length; i++){
         String[] type = angles[i].getAtomType();
         if(atm[0].equals(type[0]) && atm2.equals(type[1]) && atm[1].equals(type[2])){
            anglef = angles[i];
            break;
         }
      }
      return anglef;
   }

   /**
    * Returns the angle function for specified atom types
    * @param atm1 The type of atom1
    * @param atm2 The type of atom2
    * @param atm3 The type of atom3
    * @return the Urey-Bradley function
    */
   public FuncUB getUreyBradley(String atm1, String atm2, String atm3){
      FuncUB ubf = null;
      String[] atm = Utilities.sort(atm1,atm3);
      for(int i=0; i<ureyBradley.length; i++){
         String[] type = ureyBradley[i].getAtomType();
         if(atm[0].equals(type[0]) && atm2.equals(type[1]) && atm[1].equals(type[2])){
            ubf = ureyBradley[i];
            break;
         }
      }
      return ubf;
   }
   
   public FuncDihed getDiheral(String atm1, String atm2, String atm3, String atm4){
      FuncDihed dihedf = null;
      String[] atm = new String[4];
      if(atm1.compareTo(atm4) < 0){
         atm[0] = atm1;
         atm[1] = atm2;
         atm[2] = atm3;
         atm[3] = atm4;
      }else{
         atm[0] = atm4;
         atm[1] = atm3;
         atm[2] = atm2;
         atm[3] = atm1;
      }
      for(int i=0; i<diheds.length; i++){
         String[] type = diheds[i].getAtomType();
         boolean bb = true;
         for(int j=0; j<4; j++){
            if(! atm[j].equals(type[j])){
               bb = false;
               break;
            }
         }
         if(bb){
            dihedf = diheds[i];
            break;
         }
      }
      if(dihedf == null){
         // X atm2 atm3 X
         if(atm2.compareTo(atm3) < 0){
            atm[1] = atm2;
            atm[2] = atm3;
         }else{
            atm[1] = atm3;
            atm[2] = atm2;
         }
         for(int i=0; i<dihedx.length; i++){
            String[] type = dihedx[i].getAtomType();
            if(atm[1].equals(type[1]) && atm[2].equals(type[2])){
               dihedf = dihedx[i];
               break;
            }
         }
      }
      return dihedf;
   }
   
   /**
    * Returns the van der Waals parameters for specified atom type
    * @param atm1 The type of atom
    * @return the VdW parameter
    */
   public VdW getVdW(String atm1){
      VdW vv = null;
      for(int i=0; i<vdws.length; i++){
         if(atm1.equals(vdws[i].getAtomType())){
            vv = vdws[i];
            break;
         }
      }
      return vv;
   }

   /**
    * Prints the parameters of the force functions
    */
   abstract public void printParameter();
   
}
