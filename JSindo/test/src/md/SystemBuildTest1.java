package md;


import java.io.IOException;

/**
 * Reads a native PDB file (1C3W.pdb) for bacteriorhodopsine, sets the protein (BR),
 * retinal (RET) and internal waters (TIP3) in segments, and writes to a file 
 * (1C3W_prot.pdb).
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class SystemBuildTest1 {

   public static void main(String[] args) {

      SystemMD pdb = new SystemMD();
      
      String pdbfile = "test/md/1C3W.pdb";
      PDBReader pdbr = new PDBReader(pdbfile);
      pdbr.setSystemMD(pdb);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      Box box = pdb.getBox();
      
      MDUtil mdutil = new MDUtil();
      mdutil.styleCheck(pdb);

      Segment all = pdb.getSegment(0);
      int Nres = all.getNumOfResidue();
      Residue retinal = all.getResidue(Nres-24);
      //System.out.println(ret.getName());

      SystemMD prot = new SystemMD();
      prot.setBox(box);
      prot.setTitle("br, water, retinal of 1C3W");

      try{
         Segment bR = new Segment();
         bR.setName("BR");
         prot.addSegmentList(bR);

         for(int i=0; i<222; i++){
            Residue resi = all.getResidue(i);
            bR.addResidueList(resi);
         }

         Segment ret = new Segment();
         ret.setName("RET");
         ret.addResidueList(retinal);
         prot.addSegmentList(ret);
         
         Segment water = new Segment();
         water.setName("WAT");
         prot.addSegmentList(water);
         
         for(int i=Nres-23; i<Nres; i++){
            Residue resi = all.getResidue(i);
            resi.setName("TIP3");
            resi.getAtom(0).setLabel("OH2");
            water.addResidueList(resi);
         }
         
         prot.closeSegmentList();
         
      }catch(BuilderException e){
         e.printStackTrace();
         System.exit(-1);
      }

      PDBWriter pdbw = new PDBWriter();      
      try{
         mdutil.reNumberAtom(1, prot);
         
         for(int i=1; i<prot.getNumOfSegment(); i++){
            mdutil.reNumberResidue(1, prot.getSegment(i));            
         }
         pdbw.write("test/md/1C3W_prot.pdb", prot);
         
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }      
   }
}
