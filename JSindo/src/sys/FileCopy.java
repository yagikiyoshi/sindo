package sys;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileCopy {


   public void copy(String srcName, String destName){
      File fsrc = new File(srcName);
      File fdest = new File(destName);
      this.copy(fsrc, fdest);
   }
   
   public void copy(File srcPath, File destPath) {

      FileChannel srcChannel = null;
      FileChannel destChannel = null;

      try {
          srcChannel = new FileInputStream(srcPath).getChannel();
          destChannel = new FileOutputStream(destPath).getChannel();

          srcChannel.transferTo(0, srcChannel.size(), destChannel);

      } catch (IOException e) {
          e.printStackTrace();

      } finally {
          if (srcChannel != null) {
              try {
                  srcChannel.close();
              } catch (IOException e) {
              }
          }
          if (destChannel != null) {
              try {
                  destChannel.close();
              } catch (IOException e) {
              }
          }
      }
   }
}
