package gui;


import java.util.*;
import javax.swing.*;

/**
 * The database of Swing components
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class GUIData {
   
   private Controler controler;
   private HashMap<Integer,Canvas> canvasList;
   private HashMap<Integer,FreqTable> freqTableList;
   private HashMap<Integer,GenLocalMode> localizaList;
   private HashMap<Integer,DrawAtomNumber> atomNumberList;
   private HashMap<Integer,DrawAtomLabel> atomLabelList;
   private JMenu[] menuList;
   private HashMap<String,JMenuItem> fileItem;
   private HashMap<String,JMenuItem> showItem;
   private HashMap<String,JMenuItem> toolItem;
   
   /**
    * Constructs the object which contains the database of GUI
    */
   public GUIData(){
      canvasList     = new HashMap<Integer,Canvas>(5);
      freqTableList  = new HashMap<Integer,FreqTable>(5);
      localizaList   = new HashMap<Integer,GenLocalMode>(5);
      atomNumberList = new HashMap<Integer,DrawAtomNumber>(5);
      atomLabelList  = new HashMap<Integer,DrawAtomLabel>(5);
      fileItem = new HashMap<String,JMenuItem>(5);
      showItem = new HashMap<String,JMenuItem>(5);
      toolItem = new HashMap<String,JMenuItem>(5);
   }
   
   public void setControler(Controler con){
      this.controler = con;
   }
   public Controler getControler(){
      return controler;
   }

   /**
    * Returns the Canvas with specified ID
    * @param ID ID number
    * @return Canvas
    */
   public Canvas getCanvas(int ID){
      return canvasList.get(ID);
   }
   /**
    * Sets the Canvas with ID
    * @param ID ID number
    * @param canvas Canvas
    */
   public void setCanvas(int ID, Canvas canvas){
      canvasList.put(ID, canvas);
   }
   /**
    * Removes the Canvas with ID from the list
    * @param ID ID number
    */
   public void removeCanvas(int ID){
      canvasList.remove(ID);
   }
   /**
    * Returns the FreqTable with specified ID
    * @param ID ID number
    * @return FreqTable
    */
   public FreqTable getFreqTable(int ID){
      return freqTableList.get(ID);
   }
   /**
    * Sets the FreqTable with ID
    * @param ID ID number
    * @param table FreqTable
    */
   public void setFreqTable(int ID, FreqTable table){
      freqTableList.put(ID, table);
   }
   /**
    * Removes the FreqTable with ID from the list
    * @param ID ID number
    */
   public void removeFreqTable(int ID){
      freqTableList.remove(ID);
   }
   /**
    * Returns the Loaliza with specified ID
    * @param ID ID number
    * @return Localiza
    */
   public GenLocalMode getLocaliza(int ID){
      return localizaList.get(ID);
   }
   /**
    * Sets the Localiza with ID
    * @param ID ID number
    * @param local Localiza
    */
   public void setLocaliza(int ID, GenLocalMode local){
      localizaList.put(ID, local);
   }
   /**
    * Removes the Localiza with ID from the list
    * @param ID ID number
    */
   public void removeLocaliza(int ID){
      localizaList.remove(ID);
   }
   /**
    * Returns the DrawAtomNumber with specified ID
    * @param ID ID number
    * @return DrawAtomNumber
    */
   public DrawAtomNumber getDrawAtomNumber(int ID){
      return atomNumberList.get(ID);
   }
   /**
    * Sets the DrawAtomNumber with ID
    * @param ID ID number
    * @param an DrawAtomNumber
    */
   public void setDrawAtomNumber(int ID, DrawAtomNumber an){
      atomNumberList.put(ID, an);
   }
   /**
    * Removes the DrawAtomNumber with ID from the list
    * @param ID ID number
    */
   public void removeDrawAtomNumber(int ID){
      atomNumberList.remove(ID);
   }
   /**
    * Returns the DrawAtomLabel with specified ID
    * @param ID ID number
    * @return DrawAtomLabel
    */
   public DrawAtomLabel getDrawAtomLabel(int ID){
      return atomLabelList.get(ID);
   }
   /**
    * Sets the DrawAtomLabel with ID
    * @param ID ID number
    * @param al DrawAtomLabel
    */
   public void setDrawAtomLabel(int ID, DrawAtomLabel al){
      atomLabelList.put(ID, al);
   }
   /**
    * Removes the DrawAtomLabel with ID from the list
    * @param ID ID number
    */
   public void removeDrawAtomLabel(int ID){
      atomLabelList.remove(ID);
   }
   /**
    * Sets an array of JMenu to the Database
    * @param menuList array of JMenu
    */
   public void setMenuList(JMenu[] menuList){
      this.menuList = menuList;
   }
   /**
    * Returns the JMenu with name
    * @param name name of the menu
    * @return menu
    */
   public JMenu getMenu(String name){
      JMenu menu = null;
      for(int i=0; i<menuList.length; i++){
         if(menuList[i].getText().equals(name)){
            menu = menuList[i];
            break;
         }
      }
      return menu;
   }
   /**
    * Returns the JMenuItem of file menu with a specified key
    * @param key (Open/Import/Export/Quit)
    * @return The item of file menu
    */
   public JMenuItem getFileItems(String key){
      return fileItem.get(key);
   }
   /**
    * Sets the List of items for file menu to the Database
    * @param key key of menu
    * @param item file menu
    */
   public void setFileItems(String key, JMenuItem item){
      this.fileItem.put(key, item);
   }
   /**
    * Returns the JMenuItem of show menu with a specified key
    * @param key (Label/AtomNumber)
    * @return The item of file menu
    */
   public JMenuItem getShowItems(String key){
      return showItem.get(key);
   }
   /**
    * Sets the List of items for show menu to the Database
    * @param key key for this show
    * @param item The item of show menu
    */
   public void setShowItems(String key, JMenuItem item){
      this.showItem.put(key, item);
   }
   /**
    * Returns the JMenuItem of tool menu with a specified key
    * @param key key for this tool
    * @return The item of tool menu
    */
   public JMenuItem getToolsItems(String key){
      return toolItem.get(key);
   }
   /**
    * Sets the List of items for tool menu to the Database
    * @param key key of this tool
    * @param item The item of tool menu
    */
   public void setToolsItems(String key, JMenuItem item){
      this.toolItem.put(key, item);
   }


}
