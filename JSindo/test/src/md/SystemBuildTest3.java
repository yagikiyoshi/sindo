package md;


import java.io.IOException;

/**
 * Reads the system from 1C3W_prot.pdb and does the following:
 *  - insert retinal to LYS216 and rename the residue to LYR
 *  - remove the segment for retinal
 * In this example, we make a copy of the original system using 
 * the clone command, and edit the copied system which is set 
 * to be editable.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class SystemBuildTest3 {

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
      
      AtomMD[] retAtoms = sys.getSegment(1).getAtomALL();

      SystemMD sys2 = new SystemMD();
      try{
         Segment bR = sys.getSegment(0).clone();
         sys2.addSegmentList(bR);
         Residue lyr = bR.getResidueList(206);
         lyr.setName("LYR");
         lyr.getAtomList(8).setLabel("N16");
         lyr.addAtomList(retAtoms);

         sys2.addSegmentList(sys.getSegment(2));
         sys2.setBox(sys.getBox());
         sys2.setTitle(sys.getTitle());
      }catch(BuilderException b){
         b.printStackTrace();
         System.exit(-1);
      }      
      sys2.closeSegmentList();
      
      MDUtil mdutil = new MDUtil();
      mdutil.reNumberAtom(1, sys2);

      PDBWriter pdbw = new PDBWriter();
      try{
         pdbw.write("test/md/1C3W_prot3.pdb", sys2);
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

   }
}
