package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.j3d.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import sys.Constants;
import sys.Utilities;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.*;
import molecule.*;
import vibration.*;

public class FreqTable extends JFrame implements ListSelectionListener, ActionListener, WindowListener, ChangeListener{

   private static final long serialVersionUID = 1L;
   private String[] columnNames = {"Mode","Frequency (cm-1)", "Reduced Mass (amu)", "Intensity (km mol-1)"};
   private Object[][] tableData;

   @SuppressWarnings("unused")
   private GUIData guiData;
   @SuppressWarnings("unused")
   private int ID;
   private JTable table;
   private JCheckBox ckbox1, ckbox2, ckbox3;
   private double[][] cOfvib;
   private double[] varVib;
   private JSlider sliderHeight, sliderRadius;
   private float heightValue, radiusValue;
   private float heightFactor, radiusFactor;
   private BranchGroup scene;
   private Canvas canvas;
   
   FreqTable(GUIData guiData, int ID){
      this.guiData = guiData;
      this.ID = ID;
      guiData.setFreqTable(ID, this);
      
      canvas = guiData.getCanvas(ID);
      Rectangle rv = canvas.getBounds();
      
      this.setBounds(rv.x+rv.width+10, rv.y, 400, 500);
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.setLayout(new BorderLayout());
      this.addWindowListener(this);
      
      Molecule molecule = canvas.getMolecule();
      String type = molecule.getVibrationalData().getCoordType().split(":")[0];
      this.setTitle(type + " ( " + canvas.getTitle() +" )");
      
      VibUtil vutil = new VibUtil(molecule);
      double[] omegaV = vutil.getOmega();
      double[] infrared = vutil.calcIRintensity();
      if(infrared == null){
         infrared = new double[omegaV.length];
      }
      
      double[] rdmass = vutil.calcReducedMass();
      
      tableData = new Object[omegaV.length][4];
      for(int i=0; i<omegaV.length; i++){
         tableData[i][0] = new Integer(i+1);
         tableData[i][1] = String.format("%14.4f", omegaV[i]);
         tableData[i][2] = String.format("%14.4f", rdmass[i]*Constants.Emu2Amu);
         tableData[i][3] = String.format("%14.4f", infrared[i]);
      }

      table = new JTable(new DefaultTableModel(tableData, columnNames){
         private static final long serialVersionUID = 1L;

         public boolean isCellEditable(int row, int column){
            return false;
         }
      });
      table.getSelectionModel().addListSelectionListener(this);
      
      JScrollPane sp = new JScrollPane(table);
      int ysize=(table.getRowCount()+2)*table.getRowHeight();
      if(ysize>400) ysize=400;
      sp.setPreferredSize(new Dimension(400,ysize));
      
      JPanel p1 = new JPanel();
      p1.setLayout(new BoxLayout(p1,BoxLayout.PAGE_AXIS));
      p1.add(sp);
      
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(4,1));
      ckbox1 = new JCheckBox("Show vibrational coordinates.");
      p.add(ckbox1);
      ckbox1.addActionListener(this);
      ckbox1.setSelected(true);
      
      ckbox2 = new JCheckBox("Invert the arrows.");
      p.add(ckbox2);
      ckbox2.addActionListener(this);
      
      //ckbox3 = new JCheckBox("Show center of vibration.");
      //p.add(ckbox3);
      //ckbox3.addActionListener(this);

      JPanel pHeight = new JPanel();
      pHeight.setLayout(new FlowLayout());      
      JLabel lh = new JLabel("Arrow Length");
      pHeight.add(lh);
      
      heightValue = 3.0f;
      heightFactor = heightValue/10.0f;
      sliderHeight = new JSlider(1,50,10);
      sliderHeight.addChangeListener(this);
      sliderHeight.setEnabled(true);
      pHeight.add(sliderHeight);
      p.add(pHeight);
      
      JPanel pRadius = new JPanel();
      pRadius.setLayout(new FlowLayout());
      JLabel lr = new JLabel("Arrow Radius");
      pRadius.add(lr);
      
      radiusValue = heightValue/10.0f;
      radiusFactor = radiusValue/10.0f;
      sliderRadius = new JSlider(1,50,10);
      sliderRadius.addChangeListener(this);
      sliderRadius.setEnabled(true);
      pRadius.add(sliderRadius);
      p.add(pRadius);
      
      p1.add(p);
      
      getContentPane().add(p1,BorderLayout.CENTER);
      this.pack();
      this.setVisible(true);

   }

   public void createSceneGraph(){
      
      scene = new BranchGroup();
      scene.setCapability(BranchGroup.ALLOW_DETACH);
      
      int nrow = table.getSelectedRow();
      Molecule molecule = canvas.getMolecule();
      int ndomain = molecule.getNumOfVibrationalData();
      
      double[][] xx = molecule.getXYZCoordinates2();
      if(ndomain == 1 && molecule.getVibrationalData(0).getAtomIndex() == null){
         int mode = nrow;
         double[]  vec = molecule.getVibrationalData().getVibVector()[mode];
                     
         for(int i=0; i<molecule.getNat(); i++){
            double[] vec_i = new double[3];
            vec_i[0] = vec[i*3];
            vec_i[1] = vec[i*3+1];
            vec_i[2] = vec[i*3+2];
            if(ckbox2.isSelected()){
               for(int j=0; j<vec_i.length; j++){
                  vec_i[j] = -vec_i[j];
               }
            }         
            double len = Utilities.normalize(vec_i);
            // System.out.printf("%12.6f,  %12.6f,  %12.6f \n", vec_i[0], vec_i[1], vec_i[2]);
            if(len > 0.1){
               scene.addChild(createArrow(xx[i],vec_i,(float)len));
            }         
         }
         
      }else{
         int mode   = 0;
         int domain = 0;
         int nf = 0;
         for(int n=0; n<ndomain; n++){
            int nf_n = molecule.getVibrationalData(n).Nfree;
            if(nf+nf_n > nrow){
               domain = n;
               mode   = nrow - nf;
               break;
            }
            nf += nf_n;
         }
         
         VibrationalData vdata = molecule.getVibrationalData(domain);
         double[] vec = vdata.getVibVector()[mode];
         int[] atomIndex = vdata.getAtomIndex();
                  
         for(int i=0; i<atomIndex.length; i++){
            double[] vec_i = new double[3];
            vec_i[0] = vec[i*3];
            vec_i[1] = vec[i*3+1];
            vec_i[2] = vec[i*3+2];
            if(ckbox2.isSelected()){
               for(int j=0; j<vec_i.length; j++){
                  vec_i[j] = -vec_i[j];
               }
            }     
            double len = Utilities.normalize(vec_i);
            if(len > 0.1){
               scene.addChild(createArrow(xx[atomIndex[i]],vec_i,(float)len));
            }         
         }

      }

      /*
      double[][] xx = molecule.getXYZCoordinates2();
      double[]  vec = molecule.getVibrationalData().getVibVector()[mode];
                  
      for(int i=0; i<molecule.getNat(); i++){
         double[] vec_i = new double[3];
         vec_i[0] = vec[i*3];
         vec_i[1] = vec[i*3+1];
         vec_i[2] = vec[i*3+2];
         if(ckbox2.isSelected()){
            for(int j=0; j<vec_i.length; j++){
               vec_i[j] = -vec_i[j];
            }
         }         
         double len = Utilities.normalize(vec_i);
         // System.out.printf("%12.6f,  %12.6f,  %12.6f \n", vec_i[0], vec_i[1], vec_i[2]);
         if(len > 0.1){
            scene.addChild(createArrow(xx[i],vec_i,(float)len));
         }         
      }
      */
      
      //if(ckbox3.isSelected()){
      //   //System.out.printf("%5d %12.4f %12.4f %12.4f var = %f12.4 \n", (mode+1), cOfvib[mode][0], cOfvib[mode][1], cOfvib[mode][2], varVib[mode]);
      //   scene.addChild(createSphere(cOfvib[mode],(float)varVib[mode]));
      //}
      
      scene.compile();
      
      SimpleUniverse universe = canvas.getUniverse();
      universe.addBranchGraph(scene);

   }
   
   /*
   private Node createSphere(double[] xyz, float rr){
      Transform3D t3arrow = new Transform3D();
      t3arrow.setTranslation(new Vector3d(xyz));      
      TransformGroup arrow = new TransformGroup(t3arrow);
      
      Color3f arrowColor = new Color3f(Color.PINK);
      PolygonAttributes pa = new PolygonAttributes();
      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
      
      Appearance ap = new Appearance();
      Material ma = new Material();
      ma.setDiffuseColor(arrowColor);
      ap.setMaterial(ma);
      ap.setPolygonAttributes(pa);
      
      Sphere sphere = new Sphere(rr,Sphere.GENERATE_NORMALS,50,ap);
      arrow.addChild(sphere);
      
      return arrow;

   }
   */
   
   private Node createArrow(double[] xyz, double[] vec, float len){
      Transform3D t3arrow = new Transform3D();
      t3arrow.setTranslation(new Vector3d(xyz));
      
      if(Math.abs(vec[1]) < 0.999){
         double[] aa = new double[4];
         aa[0] = vec[2];
         aa[2] =-vec[0];
         aa[3] = Math.acos(vec[1]);
         t3arrow.setRotation(new AxisAngle4d(aa));
         
      }else{
         // The arrow is pointing along the z-direction, but up or down?
         double[] aa = new double[4];
         aa[0] = 1.0;
         aa[2] = 0.0;
         aa[3] = Math.acos(vec[1]);
         t3arrow.setRotation(new AxisAngle4d(aa));
         
      }
      
      TransformGroup arrow = new TransformGroup(t3arrow);
      
      BranchGroup branch = new BranchGroup();
      arrow.addChild(branch);

      Color3f arrowColor = new Color3f(Color.GREEN);
      
      float cyl_height = heightValue*len*canvas.getScale();
      float cyl_radius = radiusValue*canvas.getScale();
      Transform3D t3cyl = new Transform3D();
      t3cyl.setTranslation(new Vector3f(0.0f,cyl_height/2.0f,0.0f));
      TransformGroup tgcyl = new TransformGroup(t3cyl);
      branch.addChild(tgcyl);
      
      Appearance ap = new Appearance();
      Material ma = new Material();
      ma.setDiffuseColor(arrowColor);
      ap.setMaterial(ma);

      Cylinder cylinder = new Cylinder(cyl_radius,cyl_height,Cylinder.GENERATE_NORMALS,50,1,ap);
      tgcyl.addChild(cylinder);

      //float cn_height = 0.06f*factor*canvas.getScale();
      float cn_height = cyl_height*0.5f;
      float cn_radius = cyl_radius*1.5f;
      Transform3D t3cone = new Transform3D();
      t3cone.setTranslation(new Vector3f(0.0f,cyl_height+cn_height/3.0f,0.0f));
      TransformGroup tgcone = new TransformGroup(t3cone);
      branch.addChild(tgcone);
      
      Appearance ap2 = new Appearance();
      Material ma2 = new Material();
      ma.setDiffuseColor(arrowColor);
      ap2.setMaterial(ma2);
      
      Cone cone = new Cone(cn_radius,cn_height,Cone.GENERATE_NORMALS,50,1,ap);
      tgcone.addChild(cone);
      
      return arrow;
   }

   @Override
   public void valueChanged(ListSelectionEvent e) {

      if (e.getValueIsAdjusting()) return;
      
      if(ckbox1.isSelected()){
         if(scene != null){
            scene.detach();
         }
         this.createSceneGraph();
      }
      
   }
   
   public void actionPerformed(ActionEvent event){
      
      if(event.getSource().equals(ckbox1)){
         //System.out.println("ckbox1");
         if(!ckbox1.isSelected()){
            sliderHeight.setEnabled(false);
            if(scene != null) scene.detach();
         }
         if(ckbox1.isSelected()) {
        	 	sliderHeight.setEnabled(true);
        	 	this.createSceneGraph();
         }
      }
      if(event.getSource().equals(ckbox2)){
         //System.out.println("ckbox2");
         if(scene != null){
            scene.detach();
            this.createSceneGraph();
         }
      }
      if(event.getSource().equals(ckbox3)){
         //System.out.println("ckbox3");
         if(ckbox3.isSelected() && cOfvib==null){
            Molecule molecule = canvas.getMolecule();
            MolToVib m2v = new MolToVib();
            CoordinateData cdata = m2v.getCoordinateData(molecule);
            CoordProperty cprop = new CoordProperty(cdata);
            cOfvib = cprop.getCenterOfVibration();
            varVib = cprop.getVariantOfVibration();
         }         
         if(scene != null){
            scene.detach();
            this.createSceneGraph();
         }
      }

   }

   public void dispose(){
      if(scene != null){
         scene.detach();
      }
      super.dispose();
   }
   
   @Override
   public void windowOpened(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowClosing(WindowEvent e) {
      if(scene != null){
         scene.detach();
      }
      
   }

   @Override
   public void windowClosed(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowIconified(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowDeiconified(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowActivated(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowDeactivated(WindowEvent e) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void stateChanged(ChangeEvent e) {
      if(!sliderHeight.getValueIsAdjusting()){
         heightValue = heightFactor*(float)sliderHeight.getValue();
         if(scene != null){
            scene.detach();
            this.createSceneGraph();
         }
      }
      
      if(!sliderRadius.getValueIsAdjusting()){
         radiusValue = radiusFactor*(float)sliderRadius.getValue();
         if(scene != null){
            scene.detach();
            this.createSceneGraph();
         }
      }
   }
}
