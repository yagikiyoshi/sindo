package atom;

import java.io.IOException;

import molecule.MInfoIO;
import molecule.Molecule;
import sys.Constants;

public class HBTest1 {

   public static void main(String[] args){

      MInfoIO minfo = new MInfoIO();
      minfo.unsetAllData();
      minfo.setAtomData(true);
      
      Molecule hexamer = null;
      
      try{
         hexamer = minfo.loadMOL("test/atom/h2o_6-mp2dz.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      minfo.printAtoms();

      Atom[] acceptors = new Atom[6];
      for(int i=0; i<6; i++){
         acceptors[i] = hexamer.getAtom(i*3);
         System.out.printf("acceptor %d water[%1d] \n", i,i);
      }
      
      AtomUtil autil = new AtomUtil();
      Atom[][] donors = new Atom[12][];
      for(int i=0; i<6; i++){
         Atom[] oh1 = new Atom[2];
         oh1[0] = hexamer.getAtom(i*3);
         oh1[1] = hexamer.getAtom(i*3+1);
         Atom[] oh2 = new Atom[2];
         oh2[0] = hexamer.getAtom(i*3);
         oh2[1] = hexamer.getAtom(i*3+2);
         
         donors[i*2] = oh1;
         donors[i*2+1] = oh2;
         
         double roh1 = autil.getBondLength(oh1[0], oh1[1]);
         double roh2 = autil.getBondLength(oh2[0], oh2[1]);
         
         System.out.printf("donor %2d water[%1d] : roh1 = %8.4f \n", i*2,i, roh1*Constants.Bohr2Angs);
         System.out.printf("donor %2d water[%1d] : roh2 = %8.4f \n", i*2+1,i, roh2*Constants.Bohr2Angs);
      }
      
      HBanalysis hb = new HBanalysis();
      hb.setHBangle(30.0);
      hb.setAcceptorAtoms(acceptors);
      hb.setDonorAtoms(donors);
      hb.calcHB();
      
      System.out.println("# of HB: "+hb.getNumHB());
      Integer[][] byDonor = hb.getByDonor();
      for(int i=0; i<donors.length; i++){
         for(int j=0; j<byDonor[i].length; j++){
            System.out.println("is HB: D["+i+"]-A["+byDonor[i][j]+"]");
         }
      }
      System.out.println();
      
      Integer[][] byAcceptor = hb.getByAcceptor();
      for(int i=0; i<acceptors.length; i++){
         for(int j=0; j<byAcceptor[i].length; j++){
            System.out.println("is HB: A["+i+"]-D["+byAcceptor[i][j]+"]");            
         }
      }
      
   }

}
