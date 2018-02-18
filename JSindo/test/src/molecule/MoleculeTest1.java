package molecule;

import atom.Atom;

public class MoleculeTest1 {
   
   public static void main(String[] args){
      Molecule mol = new Molecule();
      
      Atom o1 = new Atom(8);
      double[] o1xyz = {0.0, 0.0, 0.228563526};
      o1.setXYZCoordinates(o1xyz);
      mol.addAtom(o1);
      
      Atom h2 = new Atom(1);
      double[] h2xyz = {0.0, 1.42997917, -0.914254105};
      h2.setXYZCoordinates(h2xyz);
      mol.addAtom(h2);
      
      Atom h3 = new Atom(1);
      double[] h3xyz = {0.0, -1.42997917, -0.914254105};
      h3.setXYZCoordinates(h3xyz);
      mol.addAtom(h3);

      double[][] xyz = mol.getXYZCoordinates2();
      double[] mass = mol.getMass();
      int Nat = mol.getNat();
      
      for(int n=0; n<Nat; n++){
         System.out.printf("%3d %12.2f %12.6f %12.6f %12.6f \n", n,mass[n],xyz[n][0],xyz[n][1],xyz[n][2]);
      }
      
      if(mol.isLinear()){
         System.out.println("This is a linear molecule.");
         
      }else{
         System.out.println("This is a non-linear molecule.");
      }
      
   }

}
