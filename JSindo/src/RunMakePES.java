
import java.text.SimpleDateFormat;
import java.util.Date;

import makePES.*;
import sys.*;


public class RunMakePES {

   public static void main(String[] args){

      int input_version = 2;
      String xmlFile = null;
      for(int n=0; n<args.length; n++) {
         if(args[n].equals("-f")) {
            xmlFile = args[n+1]; 
         }
         if(args[n].equals("-h") || args[n].equals("--help")) {
            System.out.println("USAGE: java RunMakePES [ -f xmlfile ] [--input-version 1|2]");
            System.exit(0);
         }
         if(args[n].equals("--input-version")) {
            input_version = Integer.parseInt(args[n+1]);
         }
      }
      
      printtitle();
      
      InputReader dr = new InputReader();
      if(xmlFile != null) dr.setFilename(xmlFile);
      
      InputDataPES mkPESData = null;
      switch(input_version){
      case 1:
         mkPESData = dr.read();
         break;
         
      case 2:
         mkPESData = dr.read2();
         break;

      default:
         System.out.println("Invalid option:--input-version "+input_version);
         System.out.println("The version must be 1 or 2.");
         System.exit(0);
         break;
      
      }

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
