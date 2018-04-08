package gui;


import java.awt.event.*;
import javax.swing.*;
import molecule.*;

/**
 * (Package private) The class for the Tools menu.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class JMenuTools extends JMenu implements ActionListener {

   private static final long serialVersionUID = 1L;

   private GUIData guiData;

   public static String HARMONIC = "Harmonic Analysis";
   public static String LOCAL    = "Create Local Modes";
   public static String MAKEPES  = "Make PES";
   public static String SINDO    = "Sindo";

   // The system is regarded to be non-isolated system, if 
   // the translational frequency is larger than this value. 
   private double UpperTransFreq = 50.0;
         
   /**
    * Constructs the object for the file menu
    * @param guiData
    */
   JMenuTools(GUIData guiData){
      this.setText("Tools");
      this.guiData = guiData;
      
      JMenuItem harmonic = new JMenuItem(HARMONIC);
      harmonic.addActionListener(this);
      harmonic.setEnabled(false);
      this.add(harmonic);
      guiData.setToolsItems(HARMONIC, harmonic);

      JMenuItem local = new JMenuItem(LOCAL);
      local.addActionListener(this);
      local.setEnabled(false);
      this.add(local);
      guiData.setToolsItems(LOCAL, local);
      
      /*
      JMenuItem pes = new JMenuItem(MAKEPES);
      pes.addActionListener(this);
      pes.setEnabled(false);
      this.add(pes);
      guiData.setToolsItems(MAKEPES, pes);

      JMenuItem sindo = new JMenuItem(SINDO);
      sindo.addActionListener(this);
      sindo.setEnabled(false);
      this.add(sindo);
      guiData.setToolsItems(SINDO, sindo);
      */
      
   }
   
   public void actionPerformed(ActionEvent event){
      String com = event.getActionCommand();

      if(com.equals(HARMONIC)){
         if(guiData.getFreqTable(Canvas.currentID) != null){
            /*
            FreqTable freqtable = guiData.getFreqTable(Canvas.currentID);
            freqtable.setVisible(true);
            freqtable.toFront();
            
            return;
            */
            guiData.getFreqTable(Canvas.currentID).dispose();
            guiData.removeFreqTable(Canvas.currentID);            
         }
         
         Canvas canvas = guiData.getCanvas(Canvas.currentID);
         Molecule molecule = canvas.getMolecule();
         
         System.out.println(HARMONIC);
         VibUtil vutil = new VibUtil();
         vutil.appendMolecule(molecule);
         vutil.setDomain(null);
         vutil.calcNormalModes(false);
         
         VibrationalData vdata = molecule.getVibrationalData();
         double[] omega = vdata.getOmegaT();
         for(int n=0; n<omega.length; n++) {
            if(omega[n] > UpperTransFreq) {
               System.out.printf(" -Translational frequency is too large (> %5.1f cm-1). \n",UpperTransFreq);
               System.out.printf(" -Creating single domain local normal modes. \n");
               int[][] domain = new int[1][molecule.getNat()];
               for(int m=0; m<domain[0].length; m++) {
                  domain[0][m] = m;
               }
               vutil.setDomain(domain);
               vutil.calcNormalModes(false);
               break;
            }
         }
         
         @SuppressWarnings("unused")
         FreqTable freqtable = new FreqTable(guiData,Canvas.currentID);
         
         guiData.getShowItems(JMenuShow.VIBDATA).setEnabled(true);
         guiData.getToolsItems(LOCAL).setEnabled(true);
         //guiData.getToolsItems(MAKEPES).setEnabled(true);
         //guiData.getToolsItems(SINDO).setEnabled(true);
         
         
      }else if(com.equals(LOCAL)){
         // This menu is enabled only when vibrationalData is present.
         FreqTable freqtable = null;
         if(guiData.getFreqTable(Canvas.currentID) != null){
            freqtable = guiData.getFreqTable(Canvas.currentID);
            freqtable.setVisible(true);
            freqtable.toFront();
         }else{
            freqtable = new FreqTable(guiData,Canvas.currentID);
         }

         if(guiData.getLocaliza(Canvas.currentID) != null){

            GenLocalMode localiza = guiData.getLocaliza(Canvas.currentID);
            localiza.setVisible(true);
            localiza.toFront();
            
            return;
         }
         
         @SuppressWarnings("unused")
         GenLocalMode localiza = new GenLocalMode(guiData,Canvas.currentID);

         
      }else if(com.equals(MAKEPES)){
         System.out.println("Make PES");
         
      }else if(com.equals(SINDO)){
         System.out.println("SINDO");
         
      }
   }

}
