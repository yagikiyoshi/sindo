package md;

import java.io.IOException;

public class MDUtilTest {

   public static void main(String[] args) {
      PDBReader pdbr = new PDBReader("test/md/smbilayer.pdb");
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      SystemMD sys = pdbr.getSystemMD();
      Box box = sys.getBox();
      Segment sysALL = sys.getSegment(0);

      PDBWriter pdbw = new PDBWriter();

      try{
         SystemMD sm3 = new SystemMD();
         Segment seg1 = new Segment();
         seg1.addResidueList(sysALL.getResidue(1));
         seg1.addResidueList(sysALL.getResidue(5));
         seg1.addResidueList(sysALL.getResidue(19));
         sm3.addSegmentList(seg1);
         sm3.closeSegmentList();
         sm3.setTitle("SM 1 5 19");
         sm3.setBox(box);
         pdbw.write("test/md/resutilTest1.pdb", sm3);

         Residue res0 = seg1.getResidue(0);
         MDUtil util0 = new MDUtil();
         util0.appendBox(box);
         double[] com = util0.getCenterOfMass(res0);
         util0.shiftToPBCImage(com, seg1.getResidue(1));
         util0.shiftToPBCImage(com, seg1.getResidue(2));
         pdbw.write("test/md/resutilTest2.pdb", sm3);

         res0 = res0.clone();
         res0.addAtomList(seg1.getResidue(1).getAtomALL());
         res0.addAtomList(seg1.getResidue(2).getAtomALL());
         res0.setName("SM3");
         res0.setID(1);

         SystemMD sm2 = new SystemMD();
         Segment seg2 = new Segment();
         seg2.addResidueList(res0);
         sm2.addSegmentList(seg2);
         sm2.closeSegmentList();
         sm2.setTitle("SM 1 5 19");
         sm2.setBox(box);
         pdbw.write("test/md/resutilTest3.pdb", sm2);
         
      }catch(BuilderException e){
         e.printStackTrace();
      }catch(IOException e){
         e.printStackTrace();
      }

   }
}
