package md;


import java.io.IOException;

/**
 * Reads the system from 1C3W_prot.pdb and does the following:
 *  - insert retinal to LYS216 and rename the residue to LYR
 *  - remove the segment for retinal
 * In this example, we first create the residue LYR (LYS216 + 
 * retinal). Then, LYS216 is replaced by the LYR in a copy of
 * BR created by the clone command.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class SystemBuildTest4 {

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
      
      Residue lyr = sys.getSegment(0).getResidue(206).clone();
      try {
         lyr.addAtomList(retAtoms);
      } catch (BuilderException e1) {
         e1.printStackTrace();
         System.exit(0);
      }
      lyr.closeAtomList();
      lyr.setName("LYR");
      lyr.getAtom(8).setLabel("N16");

      SystemMD sys2 = new SystemMD();
      try{
         Segment bR = sys.getSegment(0).clone();
         bR.removeResidueList(206);
         bR.insertResidueList(206, lyr);

         sys2.addSegmentList(bR);
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
         pdbw.write("test/md/1C3W_prot4.pdb", sys2);
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

   }
}
