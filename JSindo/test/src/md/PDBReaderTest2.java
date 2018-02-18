package md;

import java.io.IOException;

import sys.Constants;

public class PDBReaderTest2 {

   public static void main(String[] args){
      
      SystemMD sm = new SystemMD();
      
      PDBReader pdbr = new PDBReader("test/md/trajTest.pdb");
      pdbr.setSystemMD(sm);
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      //SystemMD sm = pdbr.getSystemMD();
      AtomMD[] atoms = sm.getAtomALL();

      Trajectory traj = sm.getTrajectory();
      int nframe = traj.getNumOfFrames();
      System.out.println("Num of frame = "+nframe);
      System.out.println("---");
      for(int i=0; i<nframe; i++){
         System.out.println("Frame #"+i);
         sm.setFrame(i);
         Box box = sm.getBox();
         System.out.printf("xsize = %8.3f, ysize = %8.3f, zsize = %8.3f \n", 
               box.getXsize()*Constants.Bohr2Angs,
               box.getYsize()*Constants.Bohr2Angs,
               box.getZsize()*Constants.Bohr2Angs);
         double[] xyz = atoms[0].getXYZCoordinates();
         String label = atoms[0].getLabel();
         System.out.printf("%4s %8.3f %8.3f %8.3f \n", label, 
               xyz[0]*Constants.Bohr2Angs, 
               xyz[1]*Constants.Bohr2Angs, 
               xyz[2]*Constants.Bohr2Angs);
         System.out.println("---");
      }
      
      Residue sm11 = sm.getResidue(11);
      
      SystemMD sysSM11 = new SystemMD();
      sysSM11.setTitle("SM11");
      
      Segment seg = new Segment();
      try {
         seg.addResidueList(sm11.clone());
         sysSM11.addSegmentList(seg);
      } catch (BuilderException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      sysSM11.closeSegmentList();

      int Natom = sm11.getNumOfAtom();
      
      Trajectory trajSM11 = new Trajectory();
      sysSM11.setTrajectory(trajSM11);
      
      for(int n=0; n<nframe; n++){
         sm.setFrame(n);
         double[][] xyz = new double[Natom][];
         for(int i=0; i<Natom; i++){
            xyz[i] = sm11.getAtom(i).getXYZCoordinates();
         }
         trajSM11.addCoordinates(xyz);
         trajSM11.addBox(sm.getBox());
      }
      sysSM11.setFrame(0);
      
      MDUtil mdutil = new MDUtil();
      mdutil.reNumberAtom(1, sysSM11);
      mdutil.reNumberResidue(1, sysSM11);
      //mdutil.appendBox(sysSM11.getBox());
      //double[] com = mdutil.getCenterOfMass(sysSM11);
      
      PDBWriter pdbw = new PDBWriter();
      try {
         pdbw.write("test/md/trajTestSM11.pdb", sysSM11);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(-1);
      }
      
   }
}
