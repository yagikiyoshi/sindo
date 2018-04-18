
import gui.*;
import javax.swing.*;

public class JSindo {

   public static void main(String[] args) {

      // Setup look and feel for Swing
      UIManager.LookAndFeelInfo[] lafInfo =UIManager.getInstalledLookAndFeels();
      int nn=0;
      for(int n=0; n<lafInfo.length; n++) {
         String lafClassName = lafInfo[n].getClassName();
         if(lafClassName.contains("nimbus")) {
            nn = n;
         }else if(lafClassName.contains("apple")) {
            nn = n;
            break;
         }
      }
      try {
         String lafClassName = lafInfo[nn].getClassName();
         //System.out.println("Set L&F to "+lafClassName);
         UIManager.setLookAndFeel(lafClassName);
      }catch(Exception ex) {
         System.out.println("Error L&F Setting");
      }            

      GUIData guiData = new GUIData();
      @SuppressWarnings("unused")
      Controler controler = new Controler(guiData);
      
   }

}
