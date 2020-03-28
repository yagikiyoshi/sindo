package md;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import sys.Constants;
import md.*;

/**
 * Read a trajectory from a DCD file
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class DCDReader {

   private Trajectory traj;
   private int for_header_byte = 4;
   private ByteBuffer for_head;

   public DCDReader(){
      for_head = ByteBuffer.allocate(for_header_byte);
   }

   /**
    * Sets an instance of Trajectory class, where the information will be stored
    * @param traj An instance of Trajectory 
    */
   public void setTrajectory(Trajectory traj){
      this.traj = traj;
   }

   /**
    * Reads a trajectory from a dcd file
    * @param fname The name of the file
    * @param append true for append mode (default = true)
    * @return A trajectory read from the file
    * @throws IOException thrown when the IO error happened
    */
   public Trajectory read(String fname, boolean append) throws IOException {
      if(! append){
         traj = new Trajectory();
      }
      return this.read(fname);
   }
   
   /**
    * Reads a trajectory from a dcd file. Note that the trajectory is 
    * read in an append mode.
    * @param fname The name of the file
    * @return A trajectory read from the file
    * @throws IOException thrown when the IO error happened
    */
   public Trajectory read(String fname) throws IOException{
      
      if(traj == null){
         traj = new Trajectory();
      }
      
      int four       = 4;
      int eight      = 8;
      ByteBuffer b4  = ByteBuffer.allocate(four);
      ByteBuffer b8  = ByteBuffer.allocate(eight);
      ByteBuffer bl  = null;
      byte[] b4a = new byte[four];
      byte[] b8a = new byte[eight];
      
      FileInputStream inp = new FileInputStream(fname);
      FileChannel inchannel = inp.getChannel();
      
      // First line
      bl = this.readLine(inchannel);
      if(bl.limit() != 84){
         inp.close();
         throw new IOException("DCD Header is not 84 bytes!");
      }

      // HEADER
      bl.get(b4a);
      ByteBuffer b_head = ByteBuffer.wrap(b4a);
      b_head.order(ByteOrder.LITTLE_ENDIAN);
      String header = new String(b_head.array());
      //System.out.println(header);

      // icntr
      /*
      icntrl[0]  = nstep / outperiod            ! total # of frames   (NSET)
      icntrl[1]  = istep                        ! first time step     (NPRIV)
      icntrl[2]  = outperiod                    ! output period       (NSAVC)
      icntrl[3]  = nstep                        ! number of time step (NSTEP)
      icntrl[9] = transfer(ts_namd,icntrl(10)) ! length of time step (DELTA)
      icntrl[10] = 1                            ! flag for with unit-cell
      icntrl[19] = 24                           ! PRETEND TO BE CHARMM24 -JCP
       */

      int ndata = 20;
      int[] icntrl = new int[ndata];
      for(int i=0; i<ndata; i++){
         bl.get(b4a);
         b4 = ByteBuffer.wrap(b4a);
         b4.order(ByteOrder.LITTLE_ENDIAN);
         icntrl[i] = b4.getInt();
         // System.out.println(i+" "+icntrl[i]);         
      }
      
      // Second line
      bl = this.readLine(inchannel);

      bl.get(b4a);
      b4 = ByteBuffer.wrap(b4a);
      b4.order(ByteOrder.LITTLE_ENDIAN);
      int ntitle = b4.getInt();
      //System.out.println(ntitle);
      
      String[] title = new String[ntitle];
      int title_byte = (bl.limit() - four)/ntitle;
      //System.out.println(title_byte);
      for(int i=0; i<ntitle; i++){
         byte[] bt = new byte[title_byte];
         bl.get(bt);
         ByteBuffer b_title = ByteBuffer.wrap(bt);
         b_title.order(ByteOrder.LITTLE_ENDIAN);
         title[i] = new String(b_title.array());
         //System.out.println(i+" "+title[i]);
      }
      
      // Third line
      bl = this.readLine(inchannel);

      bl.get(b4a);
      b4 = ByteBuffer.wrap(b4a);
      b4.order(ByteOrder.LITTLE_ENDIAN);
      int natALL = b4.getInt();
      //System.out.println(natALL);

      // Now read trajectory
      while(true) {
         if(icntrl[10] == 1){
            bl = this.readLine(inchannel);
            if(bl == null) break;
            
            double[] dcd_cell = new double[6];
            for(int i=0; i<dcd_cell.length; i++){
               bl.get(b8a);
               b8 = ByteBuffer.wrap(b8a);
               b8.order(ByteOrder.LITTLE_ENDIAN);
               dcd_cell[i] = b8.getDouble();
            }
            
            Box box = new Box();
            box.setXsize(dcd_cell[0]/Constants.Bohr2Angs);
            box.setYsize(dcd_cell[2]/Constants.Bohr2Angs);
            box.setZsize(dcd_cell[5]/Constants.Bohr2Angs);
            box.setAlpha(90.0d);
            box.setBeta(90.0d);
            box.setGamma(90.0d);
            
            traj.addBox(box);
            
            //System.out.printf("%5d %8.4f %8.4f %8.4f \n",n,dcd_cell[0],dcd_cell[2],dcd_cell[5]);
         }
         
         int len = 0;
         double[][] coord = new double[natALL][3];
         for(int i=0; i<3; i++){

            for_head.clear();
            len = inchannel.read(for_head);
            if(len < 0) break;
            
            for_head.flip();            
            
            ByteBuffer buf  = ByteBuffer.allocate(natALL*four);
            buf.clear();
            inchannel.read(buf);
            buf.flip();
            for(int j=0; j<natALL; j++){
               buf.get(b4a);
               b4 = ByteBuffer.wrap(b4a);
               b4.order(ByteOrder.LITTLE_ENDIAN);
               coord[j][i] = (double)b4.getFloat()/Constants.Bohr2Angs;               
            }
            for_head.clear();
            inchannel.read(for_head);
            for_head.flip();
            
         }
         if(len < 0) break;
         traj.addCoordinates(coord);
      }
      
      inchannel.close();
      inp.close();

      return traj;
      
   }
   
   private ByteBuffer readLine(FileChannel inchannel) throws IOException{
      
      for_head.clear();
      int len = inchannel.read(for_head);
      if(len < 0) return null;
      
      for_head.flip();
      for_head.order(ByteOrder.LITTLE_ENDIAN);
      int head_size = for_head.getInt();

      ByteBuffer bl = ByteBuffer.allocate(head_size);
      bl.clear();
      inchannel.read(bl);
      bl.flip();
      
      for_head.clear();
      inchannel.read(for_head);
      for_head.flip();

      return bl;

   }
}
