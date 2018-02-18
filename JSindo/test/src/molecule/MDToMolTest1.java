package molecule;

import java.io.IOException;

import md.*;

public class MDToMolTest1 {

   public static void main(String[] args){
      
      String pdbfile = "test/md/1C3W-2.pdb";
      PDBReader pdbr = new PDBReader(pdbfile);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      SystemMD sys = pdbr.getSystemMD();
      
      SystemMD sys2 = new SystemMD();
      Segment segment = new Segment();
      try{
         for(int i=0; i<20; i++){
            segment.addResidueList(sys.getSegment(0).getResidue(i));         
         }
         sys2.addSegmentList(segment);
      }catch(BuilderException e){
         e.printStackTrace();
         System.exit(-1);
      }
      sys2.closeSegmentList();
      sys2.setTitle("peptide");

      PDBWriter pdbw = new PDBWriter();
      try {
         pdbw.write("test/molecule/peptide.pdb", sys2);
      } catch (IOException e1) {
         e1.printStackTrace();
         System.exit(-1);
      }

      MDToMol mdToMol = new MDToMol();
      mdToMol.appendSystemMD(sys2);
      Molecule mol = mdToMol.getMolecule();
      double[] com = mdToMol.getCenterOfMass();

      double[][] xyz = mol.getXYZCoordinates2();
      for(int i=0; i<mol.getNat(); i++){
         for(int j=0; j<3; j++){
            xyz[i][j] = xyz[i][j] - com[j];
         }
      }
      
      MInfoIO minfoIO = new MInfoIO();
      minfoIO.appendMolecule(mol);
      try{
         minfoIO.dumpMOL("test/molecule/peptide.minfo");         
      }catch(IOException e){
         System.out.println(e.getMessage());
         System.exit(-1);
      }

   }
}
