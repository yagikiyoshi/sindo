package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import molecule.*;
import vibration.*;
import sys.Utilities;

/**
 * The Controler of Localiza
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 *
 */
public class GenLocalMode extends JFrame implements ActionListener {

   private static final long serialVersionUID = 1L;
   
   private GUIData guiData;
   private int ID;   
   private FreqTable freqTable;
   private JTextField domain_field;
   private JRadioButton normal, local;
   private JRadioButton boys, pm;
   private JTextField thresh_field;
   private JButton    run;
   
   private Canvas canvas;
   
   public GenLocalMode(GUIData guiData, int ID){
      
      this.guiData = guiData;
      this.ID = ID;
      guiData.setLocaliza(ID, this);
      
      freqTable = guiData.getFreqTable(ID);
      Rectangle rv = freqTable.getBounds();
      
      this.setBounds(rv.x, rv.y+rv.height+10, 400, 200);
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.setLayout(new BorderLayout());
      
      canvas = guiData.getCanvas(ID);
      this.setTitle("Local Mode Controler ( "+canvas.getTitle()+" )");
      
      Molecule molecule = canvas.getMolecule();
      
      String field = "";
      if(molecule.getVibrationalData(0).getAtomIndex() != null){
         int nDomain = molecule.getNumOfVibrationalData();
         for(int n1=0; n1<nDomain; n1++){
            int[] atomIndex = molecule.getVibrationalData(n1).getAtomIndex();
            for(int n2=0; n2<atomIndex.length-1; n2++){
               field += (atomIndex[n2]+1)+",";                
            }
            field += (atomIndex[atomIndex.length-1]+1)+" ";
         }
      }
      
      JPanel p_domain = new JPanel();
      JLabel domain_label = new JLabel("domain");
      domain_field = new JTextField(20);
      if(field.length() != 0) domain_field.setText(field);
      
      p_domain.add(domain_label);
      p_domain.add(domain_field);
      
      JPanel pp = new JPanel();
      pp.setLayout(new GridLayout(3,2));
      
      JPanel p_coord = new JPanel();
      JLabel type = new JLabel("coord type");
      type.setHorizontalAlignment(JLabel.CENTER);
      
      normal = new JRadioButton("normal");
      normal.setSelected(true);
      normal.addActionListener(this);
      local  = new JRadioButton("local");
      local.addActionListener(this);
      
      ButtonGroup g1 = new ButtonGroup();
      g1.add(normal);
      g1.add(local);
      
      p_coord.add(normal);
      p_coord.add(local);      
      
      pp.add(type);
      pp.add(p_coord);
      
      JPanel p_localiza = new JPanel();
      JLabel l_localiza = new JLabel("localiza");
      l_localiza.setHorizontalAlignment(JLabel.CENTER);
      
      boys = new JRadioButton("Boys");
      boys.addActionListener(this);
      boys.setEnabled(false);
      pm   = new JRadioButton("Pipek-Mezey");
      pm.addActionListener(this);
      pm.setEnabled(false);
      
      ButtonGroup g2 = new ButtonGroup();
      g2.add(boys);
      g2.add(pm);
      
      p_localiza.add(boys);
      p_localiza.add(pm);
      
      pp.add(l_localiza);
      pp.add(p_localiza);
      
      JPanel p_thresh = new JPanel();
      JLabel l_thresh = new JLabel("threshold / cm-1");
      l_thresh.setHorizontalAlignment(JLabel.CENTER);      
      
      thresh_field = new JTextField(5);
      thresh_field.addActionListener(this);
      thresh_field.setEnabled(false);
      
      p_thresh.add(thresh_field);
      
      pp.add(l_thresh);
      pp.add(p_thresh);      
      
      JPanel p_button = new JPanel();
      run = new JButton("Run");
      run.addActionListener(this);
      
      p_button.add(run);
      
      Container con = this.getContentPane();
      con.setLayout(new BorderLayout());
      con.add(p_domain, BorderLayout.NORTH);
      con.add(pp, BorderLayout.CENTER);
      con.add(p_button, BorderLayout.SOUTH);
      
      this.pack();
      this.setVisible(true);

   }

   private void localize(){
      
      System.out.println();
      System.out.println("Creating Local Modes... ");
      System.out.println();
      
      Molecule molecule = canvas.getMolecule();
      int Nat = molecule.getNat();
      
      // domain
      String domain_s = domain_field.getText().trim();            
      int[][] atomIndex = null;
      if(! domain_s.isEmpty() && ! domain_s.equalsIgnoreCase("all")){
      
         boolean[] selectedAtom = new boolean[Nat];
         for(int n=0; n<Nat; n++){
            selectedAtom[n] = false;
         }
         
         String[] ss = Utilities.splitWithSpaceString(domain_s);
         
         int nDomain = 0;
         for(int n1=0; n1<ss.length; n1++){
            nDomain++;
            if(ss[n1].equalsIgnoreCase("all")) break;            
         }
         
         atomIndex = new int[nDomain][];
         for(int n1=0; n1<nDomain; n1++){
            ArrayList<Integer> index1 = new ArrayList<Integer>();
            
            if(! ss[n1].equalsIgnoreCase("all")){
               String[] tt = ss[n1].split(",");
               
               for(int n2=0; n2<tt.length; n2++){
                  if(tt[n2].indexOf("-") < 0){
                     Integer mm = Integer.parseInt(tt[n2])-1;
                     if(selectedAtom[mm]){
                        System.out.println("ERROR: Duplicate selection of atoms.");
                        System.out.println("ERROR: Atom "+(mm+1)+" is seleted twice.");
                        System.out.println();
                        System.out.println("ERROR: Local mode creation is cancelled");
                        
                        return;
                     }
                     index1.add(mm);
                     selectedAtom[mm] = true;
                     
                  }else{
                     String[] uu = tt[n2].split("-");
                     Integer m1 = Integer.parseInt(uu[0])-1;
                     Integer m2 = Integer.parseInt(uu[1])-1;
                     for(int mm=m1; mm<=m2; mm++){
                        if(selectedAtom[mm]){
                           System.out.println("ERROR: Duplicate selection of atoms.");
                           System.out.println("ERROR: Atom "+(mm+1)+" is seleted twice.");
                           System.out.println();
                           System.out.println("ERROR: Local mode creation is cancelled");
                           
                           return;
                        }

                        index1.add(mm);
                        selectedAtom[mm] = true;
                     }
                  }
               }
               
            }else{
               for(int n2=0; n2<Nat; n2++){
                  if(! selectedAtom[n2]){
                     index1.add(n2);
                  }
               }               
               
            }
            
            atomIndex[n1] = new int[index1.size()];
            for(int n2=0; n2<index1.size(); n2++){
               atomIndex[n1][n2] = index1.get(n2);
            }
         }
         
      }
      
      if(atomIndex != null){
         System.out.println("o Number of domain: "+atomIndex.length);
         for(int n1=0; n1<atomIndex.length; n1++){
            System.out.print("   domain["+(n1+1)+"] ");
            for(int n2=0; n2<atomIndex[n1].length; n2++){
               System.out.print((atomIndex[n1][n2]+1)+" ");
            }
            System.out.println();
         }
         
      }else{
         System.out.println("o Domain is turned off.");
         
      }
      
      VibUtil vutil = new VibUtil(molecule);
      vutil.setDomain(atomIndex);

      if(normal.isSelected()){
         System.out.println("o Calculate normal modes.");
         vutil.calcNormalModes(false);                    
      }
      
      if(local.isSelected()){
         System.out.println("o Localize modes.");
         LocalCoord lco = new LocalCoord();
         lco.setMaxIteration(100);
         
         if(boys.isSelected()){
            lco.setBoys();
         }else if(pm.isSelected()){
            lco.setPipekMezey();
         }
         double eth = Double.parseDouble(thresh_field.getText());
         lco.setEthresh(eth);
         
         vutil.calcLocalModes(lco);

      }
      
      FreqTable freqTable = guiData.getFreqTable(ID);
      if(freqTable != null){
         freqTable.dispose();
         guiData.removeFreqTable(ID);         
      }
      
      freqTable = new FreqTable(guiData,ID);
      
   }
   
   
   @Override
   public void actionPerformed(ActionEvent event){
      
      if(event.getSource().equals(run)){
         this.localize();
      }
      if(event.getSource().equals(normal)){
         boys.setEnabled(false);
         pm.setEnabled(false);
         thresh_field.setEnabled(false);
      }
      if(event.getSource().equals(local)){
         boys.setEnabled(true);
         boys.setSelected(true);
         pm.setEnabled(true);
         thresh_field.setEnabled(true);
         thresh_field.setText("30.0");
      }
   }
}
