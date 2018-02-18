package md;

public class SegmentTest {

   public static void main(String[] args){
      
      Residue wat1 = new Residue();
      wat1.setName("WAT");
      wat1.setID(1);
      
      AtomMD atom1 = new AtomMD(8);
      atom1.setLabel("O");
      double[] o1xyz = {0.0, 0.0, 0.228563526};
      atom1.setID(1);
      atom1.setXYZCoordinates(o1xyz);
      
      AtomMD atom2 = new AtomMD(1);
      atom2.setLabel("H2");
      double[] h2xyz = {0.0, 1.42997917, -0.914254105};
      atom2.setID(2);
      atom2.setXYZCoordinates(h2xyz);

      AtomMD atom3 = new AtomMD(1);
      atom3.setLabel("H3");
      double[] h3xyz = {0.0, -1.42997917, -0.914254105};
      atom3.setXYZCoordinates(h3xyz);
      atom3.setID(3);

      try{
         wat1.addAtomList(atom1);
         wat1.addAtomList(atom2);
         wat1.addAtomList(atom3);
      }catch(BuilderException be){
         be.printStackTrace();
      }
      wat1.closeAtomList();
      
      Residue ox = wat1.clone();
      ox.setName("OXO");
      
      AtomMD atom4 = new AtomMD(1);
      atom4.setLabel("H4");
      double[] h4xyz = {-1.42997917, -0.914254105, 0.0};
      atom4.setXYZCoordinates(h4xyz);
      atom4.setID(4);
      try{
         ox.addAtomList(atom4);
      }catch(BuilderException be){
         be.printStackTrace();
      }
      ox.closeAtomList();
      
      atom1 = ox.getAtom(1);
      double[] h2xyz2 = {1.42997917, -0.914254105, 0.0};
      atom1.setXYZCoordinates(h2xyz2);
      
      Segment seg1 = new Segment();
      seg1.setName("SOL");
      try{
         seg1.addResidueList(wat1);
         seg1.addResidueList(ox);
      }catch(BuilderException be){
         be.printStackTrace();
      }
      seg1.closeResidueList();
      
      MDUtil util = new MDUtil();
      util.reNumberAtom(1, seg1);
      util.reNumberResidue(1, seg1);

      PDBWriter writer = new PDBWriter();
      writer.print(seg1);
            
   }
}
