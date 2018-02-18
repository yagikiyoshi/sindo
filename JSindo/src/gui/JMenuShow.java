package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


/**
 * (Package private) The class for the Show menu 
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class JMenuShow extends JMenu implements ActionListener {

   private static final long serialVersionUID = 1L;
   
   private GUIData guiData;
   private String checkmark="\u2713 ";
   
   public static String LABEL      ="Label";
   public static String ATOMNUMBER ="Atom Number";
   public static String VIBDATA    ="Vibrational Data";
   public static String SUBATOM    ="Subatoms";
   
   JMenuShow(GUIData guiData){
      this.setText("Show");
      this.guiData = guiData;
      
      JMenuItem label = new JMenuItem(LABEL);
      label.addActionListener(this);
      label.setEnabled(false);
      this.add(label);
      guiData.setShowItems(LABEL, label);
      
      JMenuItem atomNumber = new JMenuItem(ATOMNUMBER);
      atomNumber.addActionListener(this);
      atomNumber.setEnabled(false);
      this.add(atomNumber);
      guiData.setShowItems(ATOMNUMBER, atomNumber);

      JMenuItem vibData = new JMenuItem(VIBDATA);
      vibData.addActionListener(this);
      vibData.setEnabled(false);
      this.add(vibData);
      guiData.setShowItems(VIBDATA, vibData);
      
      /*
      JMenuItem subatom = new JMenuItem(SUBATOM);
      subatom.addActionListener(this);
      subatom.setEnabled(false);
      this.add(subatom);
      guiData.setShowItems(SUBATOM, subatom);
      */
      
   }
   
   @Override
   public void actionPerformed(ActionEvent event) {
      String com = event.getActionCommand();
      
      if(com.equals(LABEL) || com.equals(checkmark+LABEL)){
         
         JMenuItem label = guiData.getShowItems(LABEL);
         DrawAtomLabel atomlabel = null;
         if(guiData.getDrawAtomLabel(Canvas.currentID) == null){
            atomlabel = new DrawAtomLabel(guiData,Canvas.currentID);
            atomlabel.createSceneGraph();
            label.setText(checkmark+LABEL);
         }else{
            atomlabel = guiData.getDrawAtomLabel(Canvas.currentID);
            if(! atomlabel.isVisible()){
               atomlabel.createSceneGraph();
               label.setText(checkmark+LABEL);
            }else{
               atomlabel.detachSceneGraph();
               label.setText(LABEL);
            }
         }
      }
      
      if(com.equals(ATOMNUMBER) || com.equals(checkmark+ATOMNUMBER) ){
         
         JMenuItem number = guiData.getShowItems(ATOMNUMBER);
         DrawAtomNumber atomnumber = null; 
         if(guiData.getDrawAtomNumber(Canvas.currentID) == null){
            atomnumber = new DrawAtomNumber(guiData,Canvas.currentID);            
            atomnumber.createSceneGraph();
            number.setText(checkmark+ATOMNUMBER);
         }else{
            atomnumber = guiData.getDrawAtomNumber(Canvas.currentID);
            if(! atomnumber.isVisible()){
               atomnumber.createSceneGraph();
               number.setText(checkmark+ATOMNUMBER);
            }else{
               atomnumber.detachSceneGraph();
               number.setText(ATOMNUMBER);
            }
         }
         
      }
      
      if(com.equals(VIBDATA)){
         
         FreqTable freqTable = guiData.getFreqTable(Canvas.currentID);
         if(freqTable == null){
            freqTable = new FreqTable(guiData,Canvas.currentID);
         }else{
            if(! freqTable.isVisible()){
               freqTable.setVisible(true);
               freqTable.toFront();               
            }else{
               freqTable.setVisible(false);
            }

         }
      }

      if(com.equals(SUBATOM)){
         System.out.println("Show subatoms!");
      }
   }

}
