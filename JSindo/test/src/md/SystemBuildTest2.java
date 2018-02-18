package md;


import java.io.IOException;

/**
 * Reads the system from 1C3W_prot.pdb and does the following:
 *  - insert retinal to LYS216 and rename the residue to LYR
 *  - remove the segment for retinal
 * In this example, the system is edited using openSegmentList, 
 * which attributes the existing system to be editable.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class SystemBuildTest2 {

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
      
      // Note that this call has to precede the open command.
      AtomMD[] retAtoms = sys.getSegment(1).getAtomALL();
      
      // Hereafter, ..List command takes effect.
      sys.openSegmentList();
      try{
         sys.removeSegmentList(1);

         Residue lyr = sys.getSegmentList(0).getResidueList(206);
         lyr.setName("LYR");
         lyr.getAtomList(8).setLabel("N16");
         lyr.addAtomList(retAtoms);
         //lyr.insertAtomList(4,retAtoms);
                  
      }catch(BuilderException e){
         e.printStackTrace();
         System.exit(-1);
      }
      sys.closeSegmentList();
      
      MDUtil mdutil = new MDUtil();
      mdutil.reNumberAtom(1, sys);

      PDBWriter pdbw = new PDBWriter();
      try{
         pdbw.write("test/md/1C3W_prot2.pdb", sys);
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

   }
}
