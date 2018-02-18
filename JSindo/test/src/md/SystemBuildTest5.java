package md;


import java.io.IOException;
import java.util.*;

/**
 * Reads the system from 1C3W_prot.pdb and does the following:
 *  - make a list of atoms to remove from the system
 *  - remove atoms using mdutil.deleteatom
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class SystemBuildTest5 {

   public static void main(String[] args) {

      SystemMD sys = new SystemMD();
      
      String pdbfile = "test/md/1C3W_prot.pdb";
      PDBReader pdbr = new PDBReader(pdbfile);
      pdbr.setSystemMD(sys);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      System.out.println(sys.getNumOfAtom());
      for(int i=0; i<sys.getNumOfSegment(); i++){
         Segment segi = sys.getSegment(i);
         System.out.println("seg: "+i);
         System.out.println("num_res = "+segi.getNumOfResidue());
         System.out.println("num_atm = "+segi.getNumOfAtom());
      }
      System.out.println();

      ArrayList<Integer> delatom_index = new ArrayList<Integer>();
      for(int i=0; i<sys.getNumOfAtom(); i++){
         AtomMD atom = sys.getAtom(i);
         String label = atom.getLabel();
         if(label.matches("N.*")){ 
            delatom_index.add(i);
         }
         if(label.matches("OH2")){
            delatom_index.add(i);
         }
      }
      
      System.out.println(delatom_index.size());
      System.out.println();
      
      MDUtil mdutil = new MDUtil();
      try {
         mdutil.deleteAtom(delatom_index.toArray(new Integer[0]), sys);
      } catch (BuilderException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      mdutil.reNumberAtom(1, sys);

      System.out.println(sys.getNumOfAtom());
      for(int i=0; i<sys.getNumOfSegment(); i++){
         Segment segi = sys.getSegment(i);
         System.out.println("seg: "+i);
         System.out.println("num_res = "+segi.getNumOfResidue());
         System.out.println("num_atm = "+segi.getNumOfAtom());
      }
      System.out.println();
      
      PDBWriter pdbw = new PDBWriter();
      try{
         pdbw.write("test/md/1C3W_prot5.pdb", sys);
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

   }
}
