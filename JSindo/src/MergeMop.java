
import java.io.*;
import makePES.QFFData;
import makePES.QFFUtil;

public class MergeMop {

   public static void main(String[] args) {
      if(args.length < 3) {
         System.out.println("Usage: java MergeMop mop1 mop2 newmop");
         System.out.println("           mop1  : name of mop files with higher priority)");
         System.out.println("           mop2  : name of mop files with lower priority");
         System.out.println("           newmop: name of the merged mop file");
         System.exit(0);
      }
      
      QFFData qff1 = new QFFData();
      QFFData qff2 = new QFFData();
      
      QFFUtil qutil = new QFFUtil();
      try {
         qutil.setQFFData(qff1);
         qutil.readmop(new File(args[0]));
         
         qutil.setQFFData(qff2);
         double[] omega = qutil.readmop(new File(args[1]));
         qutil.merge(qff1);
         qutil.writeMop(args[2], omega);
         
      } catch (IOException e) {
         System.out.println(e.getMessage());
      }
      
      
   }
}
