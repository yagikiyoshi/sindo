
import java.io.*;
import makePES.QFFData;
import makePES.QFFUtil;
import molecule.*;
import sys.Utilities;

public class MergeMop {

   public void printUsage() {
      System.out.println("Usage: java MergeMop -mop1 mopfile1 -mop2 mopfile2 [-o newmop] --with-hc/--wo-hc -minfo minfofile");
      System.out.println("   mopfile1  : name of mop files");
      System.out.println("   mopfile2  : name of mop files (overlapping coeff. will be overwritten)");
      System.out.println("   newmop    : name of the merged mop file");
      System.out.println("   --with-hc/--without-hc: with or without harmonic coupling");
      System.out.println("   minfofile : name of minfo file");
      System.exit(0);
   }
   
   public static void main(String[] args) {
   
      System.out.println("MergeMop utility (1.0)");
      System.out.println();
      
      MergeMop mergemop = new MergeMop();

      if(args.length == 0) {
         mergemop.printUsage();
      }
      
      int na = 0;
      boolean with_hc = false;
      String mop1 = null;
      String mop2 = null;
      String newmop = null;
      String minfo = null;
      while(na < args.length) {
         if(args[na].equals("--with-hc")) {
            with_hc = true;
            na++;
         }else if (args[na].equals("--wo-hc")) {
            with_hc = false;
            na++;
         }else if (args[na].equals("-mop1")) {
            mop1 = args[na+1];
            na+=2;
         }else if (args[na].equals("-mop2")) {
            mop2 = args[na+1];
            na+=2;
         }else if (args[na].equals("-o")) {
            newmop = args[na+1];
            na+=2;
         }else if (args[na].equals("-minfo")){
            minfo = args[na+1];
            na+=2;
         }else {
            System.out.println("Error: Unkown option "+args[na]);
            System.out.println();
            mergemop.printUsage();
         }
         
      }

      if(mop1 == null) {
         System.out.println("Error: mop1 is not given.");
         System.out.println();
         mergemop.printUsage();
      }
      if(mop2 == null) {
         System.out.println("Error: mop2 is not given.");
         System.out.println();
         mergemop.printUsage();
      }
      if(newmop == null) {
         int idx = mop2.lastIndexOf(".");
         newmop = mop2.substring(0, idx)+"_merge"+mop2.substring(idx);
      }
      if(with_hc && minfo == null) {
         System.out.println("Error: minfo is needed when --with-hc.");
         System.out.println();
         mergemop.printUsage();
      }

      System.out.println(" o mop1   = "+mop1);
      System.out.println(" o mop2   = "+mop2);
      System.out.println(" o newmop = "+newmop);
      if(minfo != null) System.out.println(" o minfo  = "+minfo);
      System.out.println();

      QFFData qff1 = new QFFData();
      QFFData qff2 = new QFFData();
      
      QFFUtil qutil = new QFFUtil();
      try {
         System.out.print(" Reading "+mop1+" ... ");
         qutil.setQFFData(qff1);
         qutil.readmop(new File(mop1));
         System.out.println("[OK]");
         
         System.out.print(" Reading "+mop2+" ... ");
         qutil.setQFFData(qff2);
         double[] omega = qutil.readmop(new File(mop2));
         System.out.println("[OK]");
         
         Molecule molecule = new Molecule();
         if(minfo != null) {
            System.out.print(" Reading "+minfo+" ... ");
            MInfoIO minfoIO = new MInfoIO();
            molecule = minfoIO.loadMOL(minfo);
            System.out.println("[OK]");
            System.out.println();

            int nf=0;
            for(int nd=0; nd < molecule.getNumOfVibrationalData(); nd++) {
               nf+=molecule.getVibrationalData(nd).Nfree;
            }
            if(nf != qff2.getNfree()) {
               System.out.println("Error! The number of vibrational degrees of freedom don't match.");
               System.out.println(" Nfree (QFF) = "+qff2.getNfree());
               System.out.println(" Nfree (Minfo) = "+nf);
               Utilities.terminate();
            }
            System.out.print(" Merging "+mop1+" into "+mop2+" ... ");
            qutil.merge(qff1, molecule);
            System.out.println("[OK]");

         }else {
            System.out.print(" Merging "+mop1+" into "+mop2+" ... ");
            qutil.merge(qff1);
            System.out.println("[OK]");

         }
         
         
         qutil.writeMop(newmop, omega);
         System.out.println(" Merged mop written to "+newmop+".");
         System.out.println();
         
      } catch (IOException e) {
         System.out.println(e.getMessage());
      }
      
      
   }
}
