package gui;


import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Rectangle;
import javax.swing.*;
import sys.*;

/**
 * (Package private) The class for the Tools menu.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class JMenuHelp extends JMenu implements ActionListener {

   private static final long serialVersionUID = 1L;

   private Controler controler;
   
   /**
    * Constructs the object for the file menu
    */
   JMenuHelp(Controler con){
      this.setText("Help");
      
      JMenuItem about = new JMenuItem("About SINDO");
      about.addActionListener(this);
      this.add(about);
      
      this.controler = con;
   }
   
   public void actionPerformed(ActionEvent event){
      String com = event.getActionCommand();

      if(com.equals("About SINDO")){

         JLabel label1 = new JLabel();
         label1.setText("JSindo "+VersionInfo.ID);
         label1.setHorizontalAlignment(CENTER);
         JLabel label2 = new JLabel();
         label2.setText("Release "+VersionInfo.RELEASE);
         label2.setHorizontalAlignment(CENTER);
         JPanel labelPanel = new JPanel();
         labelPanel.setLayout(new GridLayout(2,1));
         labelPanel.add(label1);
         labelPanel.add(label2);
         
         Rectangle rv = controler.getBounds();

         JFrame jframe = new JFrame();
         jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         jframe.setBounds(rv.x+rv.width/4, rv.y+rv.height/2, 200, 100);
         jframe.getContentPane().add(labelPanel);
         jframe.setVisible(true);
         
      }
   }

}
