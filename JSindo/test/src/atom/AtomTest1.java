package atom;

import atom.Atom;
import sys.Constants;

public class AtomTest1 {

   public static void main(String[] args){
      Atom H = new Atom(1);
      Atom D = new Atom(1);
      D.setDeuterium();
      Atom N = new Atom(7);
      Atom O = new Atom(8);
      
      String aa = "cde";
      System.out.println(aa.compareTo("abc"));
      
      System.out.println("M(H)="+H.getMass()*Constants.Emu2Amu);
      System.out.println("M(D)="+D.getMass()*Constants.Emu2Amu);
      System.out.println("M(N)="+N.getMass()*Constants.Emu2Amu);
      System.out.println("M(O)="+O.getMass()*Constants.Emu2Amu);
   }
}
