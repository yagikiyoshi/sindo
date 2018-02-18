package md;

import java.util.*;

/**
 * Resolves the atomic number from the label
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class ANResolver {

   private HashMap<String,Integer> myLabels;
   
   public ANResolver(){
      myLabels = new HashMap<String,Integer>();
   }
   
   /**
    * Sets the atomic number for a given label
    * @param label the label of atom
    * @param atomicNumber the atomic number of label
    */
   public void addLabel(String label, Integer atomicNumber){
      String label0 = label.toUpperCase();
      myLabels.put(label0, atomicNumber);
   }
   
   /**
    * Returns the atomic number for a given species. The label is judged based on the CHARMM36 FF.
    * @param label0 the label of atom
    * @return the atomic number
    */
   public int getAtomicNumber(String label0){
      int an = -1;
      
      String label = label0.toUpperCase();
   
      if(myLabels.containsKey(label)){
         return myLabels.get(label);
      }
      
      //H, He
      if(label.charAt(0)=='H'){
         if(label.equals("HE")){
            an = 2;
         }else{
            an = 1;            
         }
         return an;
      }
      
      //C, Cs, Ca, Cl
      if(label.charAt(0) == 'C'){
         if(label.equals("CES")){
            an = 55;
         }else if(label.equals("CAL")){
            an = 20;
         }else if(label.equals("CLA")){
            an = 17;            
         }else if(label.length() > 3){
            if(label.subSequence(0, 4).equals("CLG")){
               an = 17;               
            }
         }else {
            an = 6;            
         }
         return an;
      }
      
      //Nitrogen
      if(label.charAt(0) == 'N'){
         if(label.equals("NE")){
            an = 10;
         }else{
            an = 7;
         }
         return an;
      }
      
      //Oxygen
      if(label.charAt(0) == 'O'){
         an = 8;
         return an;
      }
      
      //P, K
      if(label.charAt(0) == 'P'){
         if(label.equals("POT")){
            an = 19;
         }else{
            an = 15;
         }
         return an;
      }
      
      //Na, S
      if(label.charAt(0) == 'S'){
         if(label.equals("SOD")){
            an = 11;
         }else{
            an = 16;            
         }
         return an;
      }

      //Br
      if(label.charAt(0) == 'B'){
         an = 35;
         return an;
      }
      
      //I
      if(label.charAt(0) == 'I'){
         an = 53;
         return an;
      }
      
      //F
      if(label.charAt(0) == 'F'){
         if(label.equals("FE")){
            an = 26;
         }else{
            an = 9;
         }
         return an;
      }
      
      //Zn
      if(label.charAt(0) == 'Z'){
         an = 30;
         return an;
      }

      //Al
      if(label.charAt(0) == 'A'){
         an = 13;
         return an;
      }
      
      //Mg
      if(label.charAt(0) == 'M'){
         an = 12;
         return an;
      }
      // Dummy
      if(label.equals("DUM")){
         an = -1;
         return an;
      }
      
      //System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");

      return an;
   }
}
