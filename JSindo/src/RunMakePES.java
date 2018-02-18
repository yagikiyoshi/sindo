
import java.text.SimpleDateFormat;
import java.util.Date;

import makePES.*;
import sys.*;


public class RunMakePES {

   public static void main(String[] args){
      
      printtitle();
      
      PESInputReader dr = new PESInputReader();
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
