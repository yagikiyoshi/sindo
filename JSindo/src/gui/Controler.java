package gui;

import java.awt.Font;

import javax.swing.*;

/**
 * The Controler window
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class Controler extends JFrame {
   
   private static final long serialVersionUID = 1L;

   /**
    * Constructs the object for the main window
    * @param guiData the database of GUI components
    */
   public Controler(GUIData guiData){
      
      this.setTitle("JSindo");
      this.setBounds(30, 30, 400, 150);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JMenuBar menuBar = new JMenuBar();
      this.setJMenuBar(menuBar);

      JMenu[] menuList = new JMenu[4];
      menuList[0] = new JMenuFile(guiData);
      menuBar.add(menuList[0]);

      menuList[1] = new JMenuShow(guiData);
      menuBar.add(menuList[1]);
      
      menuList[2] = new JMenuTools(guiData);
      menuBar.add(menuList[2]);
      
      menuList[3] = new JMenuHelp(this);
      menuBar.add(menuList[3]);
      
      guiData.setMenuList(menuList);
      guiData.setControler(this);

      this.setVisible(true);
   }
   
}
