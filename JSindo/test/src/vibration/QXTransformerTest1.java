package vibration;

import java.io.IOException;
import molecule.*;


/*
 * Read data from sample/Molecules/h2o-freq.fchk, obtain normal modes, and make displacement along the modes.
 *
Point0:   r1 =       1.8124, r2 =        1.8124
Point1:   r1 =       2.1550, r2 =        1.4700
Point2:   r1 =       2.4978, r2 =        1.1280
Point3:   r1 =       2.8406, r2 =        0.7870
Point4:   r1 =       3.1835, r2 =        0.4491

 */
public class QXTransformerTest1 {
   
   public static void main(String[] args){

      MInfoIO minfo = new MInfoIO();
      try{
         minfo.loadMOL("test/molecule/h2o-freq.minfo");
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }

      Molecule h2o = minfo.getMolecule();
      int Nat = h2o.getNat();

      VibUtil util = new VibUtil(h2o);
      CoordinateData normco = util.calcNormalModes();

      QXTransformer qxTrans = new QXTransformer();
      qxTrans.appendCoordinate(normco);
      
      int Nfree = normco.Nfree;
      double[] qq = new double[Nfree];
      for(int i=0; i<Nfree; i++){
         qq[i] = 0.0d;
      }
      
      Molecule h2o_dev = util.copyAtoms();
      MolUtil molUtil = new MolUtil(h2o_dev);

      double[][] x0 = h2o.getXYZCoordinates2();
      double[][] xx = h2o_dev.getXYZCoordinates2();

      for(int n=0; n<5; n++){
         System.out.print("Point"+n+":  ");
         qq[2] = 20.0d*n;
         double[][] dx = qxTrans.dq2dx(2,qq[2]);
         for(int i=0; i<Nat; i++){
            for(int j=0; j<3; j++){
               xx[i][j] = x0[i][j] + dx[i][j];
            }
         }
         
         double r1 = molUtil.getBondLength(1, 2);
         double r2 = molUtil.getBondLength(1, 3);
         System.out.printf(" r1 = %12.4f, r2 =  %12.4f", r1,r2);
         System.out.println();
         
      }

   }

}
