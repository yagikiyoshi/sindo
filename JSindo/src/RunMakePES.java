
import java.text.SimpleDateFormat;
import java.util.Date;

import makePES.*;
import sys.*;


public class RunMakePES {

   public static void main(String[] args){

      String xmlFile = null;
      for(int n=0; n<args.length; n++) {
         if(args[n].equals("-f")) {
            xmlFile = args[n+1]; 
         }
         if(args[n].equals("-h")) {
            System.out.println("USAGE: java RunMakePES [ -f xmlfile ]");
            System.exit(0);
         }
      }
      
      printtitle();
      
      PESInputReader dr = new PESInputReader();
      if(xmlFile != null) dr.setFilename(xmlFile);
      PESInputData mkPESData = dr.read();
      MakePES mkpes = new MakePES();
      mkpes.appendPESData(mkPESData);
      mkpes.genPES();
   }
   
   private static void printtitle(){
      
      Date now = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
      System.out.println(sdf.format(now));
      
      System.out.println("-------------------------------------------------------");
      System.out.println();
      System.out.println("      Make Potential Energy Surface for SINDO");
      System.out.println("                         Version "+VersionInfo.ID);
      System.out.println("                         Release "+VersionInfo.RELEASE);
      System.out.println();
      System.out.println("                COPYRIGHT "+VersionInfo.YEAR+":  ");
      System.out.println("                   Kiyoshi Yagi  kiyoshi.yagi@riken.jp ");
      System.out.println();
      System.out.println("-------------------------------------------------------");
   }
   

}
