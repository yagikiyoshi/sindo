package md;

import java.io.*;
import java.util.*;
import java.text.*;
import java.nio.*;
import java.nio.channels.FileChannel;

import sys.*;

/**
 * Writes the trajectory to a DCD file.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class DCDWriter {

   private int for_header_byte = 4;
   private ByteBuffer for_head;

   private int[] icntrl;
   private int int_byte = 4;
   private int real4_byte = 4;
   private int real8_byte = 8;
   
   public DCDWriter(){
      for_head = ByteBuffer.allocate(for_header_byte);
      for_head.order(ByteOrder.LITTLE_ENDIAN);
   }
   
   /*
   icntrl[0]  = nstep / outperiod            ! total # of frames   (NSET)
   icntrl[1]  = istep                        ! first time step     (NPRIV)
   icntrl[2]  = outperiod                    ! output period       (NSAVC)
   icntrl[3]  = nstep                        ! number of time step (NSTEP)
   icntrl[9] = transfer(ts_namd,icntrl(10))  ! length of time step (DELTA)
   icntrl[10] = 1                            ! flag for with unit-cell
   icntrl[19] = 24                           ! PRETEND TO BE CHARMM24 -JCP
    */
   private void setDefaultHeader(SystemMD system){
      
      icntrl = new int[20];
      icntrl[0] = system.getTrajectory().getNumOfFrames();
      icntrl[1] = 1;
      icntrl[2] = 1;
      icntrl[3] = icntrl[0];
      if(system.getBox() == null){
         icntrl[10] = 0;
      }else{
         icntrl[10] = 1;
      }
      icntrl[19] = 24;
      
   }
   
   /**
    * Sets the header of a dcd file. A default header is written when 
    * the header is not given.
    * @param icntrl int[20] with parameters
    */
   public void setHeader(int[] icntrl){
      this.icntrl = icntrl;
   }
   
   /**
    * Write the trajectory to a file
    * @param FileName Name of the dcd file
    * @param system SystemMD (must contain Trajectory)
    * @throws IOException thrown when IO error happened
    */
   public void write(String FileName, SystemMD system) throws IOException{
      
      FileOutputStream outStream = new FileOutputStream(FileName);
      FileChannel outchannel = outStream.getChannel();
      
      // first line
      String header = "CORD";
      if(icntrl == null){
         this.setDefaultHeader(system);
      }

      for_head.clear();
      for_head.putInt(icntrl.length*int_byte+header.length());
      for_head.flip();
      outchannel.write(for_head);
      
      ByteBuffer buf_header = ByteBuffer.allocate(header.length());
      buf_header.order(ByteOrder.LITTLE_ENDIAN);
      buf_header.clear();
      for(char c: header.toCharArray()){
         byte b = (byte)c;
         buf_header.put(b);
      }
      buf_header.flip();
      outchannel.write(buf_header);
      
      ByteBuffer buf_ii = ByteBuffer.allocate(icntrl.length*int_byte);
      buf_ii.order(ByteOrder.LITTLE_ENDIAN);
      buf_ii.clear();
      for(int n=0; n<icntrl.length; n++){
         buf_ii.putInt(icntrl[n]);
      }
      buf_ii.flip();
      outchannel.write(buf_ii);

      for_head.flip();
      outchannel.write(for_head);

      // second line
      Date now = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy", Locale.US);

      int title_byte = 80;
      int ntitle = 4;
      String[] title = new String[ntitle];
      title[0] = "REMARKS CREATED BY JSINDO";
      title[1] = "REMARDS DATE: "+sdf.format(now)+"CREATED BY USER:"+System.getProperty("user.name");
      title[2] = "";
      title[3] = "";
      for(int i=0; i<title.length; i++){
         for(int j=title[i].length(); j<title_byte; j++){
            title[i] += " ";
         }
      }

      for_head.clear();
      for_head.putInt(int_byte+title_byte*ntitle);
      for_head.flip();
      outchannel.write(for_head);
      
      ByteBuffer buff_title = ByteBuffer.allocate(int_byte+title_byte*ntitle);
      buff_title.order(ByteOrder.LITTLE_ENDIAN);
      buff_title.clear();
      buff_title.putInt(ntitle);
      for(int i=0; i<ntitle; i++){

         for(char c: title[i].toCharArray()){
            byte b = (byte)c;
            buff_title.put(b);
         }
      }
      buff_title.flip();
      outchannel.write(buff_title);
      
      for_head.flip();
      outchannel.write(for_head);

      // third line
      int natom = system.getNumOfAtom();
      for_head.clear();
      for_head.putInt(int_byte);
      for_head.flip();
      outchannel.write(for_head);

      ByteBuffer buff_natom = ByteBuffer.allocate(int_byte);
      buff_natom.order(ByteOrder.LITTLE_ENDIAN);
      buff_natom.clear();
      buff_natom.putInt(natom);
      buff_natom.flip();
      outchannel.write(buff_natom);
      
      for_head.flip();
      outchannel.write(for_head);
      
      // Now write the trajectory      
      Trajectory traj = system.getTrajectory();
      //System.out.println(traj.getNumOfFrames());
      for(int n=0; n<traj.getNumOfFrames(); n++){
         if(traj.isBoxes()){
            double[] box = new double[6];
            Box boxn = traj.getBox(n);
            box[0] = boxn.getXsize()*Constants.Bohr2Angs;
            box[2] = boxn.getYsize()*Constants.Bohr2Angs;
            box[5] = boxn.getZsize()*Constants.Bohr2Angs;
            
            for_head.clear();
            for_head.putInt(real8_byte*box.length);
            for_head.flip();
            outchannel.write(for_head);

            ByteBuffer buff_box = ByteBuffer.allocate(real8_byte*box.length);
            buff_box.order(ByteOrder.LITTLE_ENDIAN);
            for(int i=0; i<box.length; i++){
               buff_box.putDouble(box[i]);
            }
            buff_box.flip();
            outchannel.write(buff_box);
            
            for_head.flip();
            outchannel.write(for_head);

         }
         
         double[][] xyz = traj.getCoordinates(n);
         
         for(int i=0; i<3; i++){
            for_head.clear();
            for_head.putInt(real4_byte*natom);
            for_head.flip();
            outchannel.write(for_head);
            
            ByteBuffer buff_xyz = ByteBuffer.allocate(real4_byte*natom);
            buff_xyz.order(ByteOrder.LITTLE_ENDIAN);
            for(int j=0; j<natom; j++){
               buff_xyz.putFloat((float)(xyz[j][i]*Constants.Bohr2Angs));
            }
            buff_xyz.flip();
            outchannel.write(buff_xyz);
            
            for_head.flip();
            outchannel.write(for_head);            
         }
         
      }
      outchannel.close();
      outStream.close();
      
      // clear header
      icntrl = null;
      
   }
   

}
