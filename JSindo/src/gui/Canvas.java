package gui;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.vecmath.*;
import javax.media.j3d.*;
import sys.PeriodicTable;
import atom.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.*;
import molecule.*;

/**
 * The canvas to show the molecule using Java3D.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class Canvas extends JFrame implements WindowListener {

   /**
    * Indicates which canvas is active. Negative (-1) means there is no canvas that 
    * is active.
    */
   public static int currentID = -1;

   private static final long serialVersionUID = 1L;
   private static int IDcounter = 100;

   private GUIData guiData;
   private int ID;
   private SimpleUniverse universe;
   private Molecule molecule;
   
   private float scale = 0.1f;
   private double[][] xyz_org;
   private double[][] xyz_int;
   private boolean is_xyz_org;
   
   private double[][] xyz_sub_org;
   private double[][] xyz_sub_int;

   private Canvas3D offscreenCanvas3D;
   //public static final int OFF_SCREEN_SCALE = 1;

   /**
    * Constructs the object and registers to the database.
    * @param guiData The Database
    */
   public Canvas(GUIData guiData){
      this.guiData = guiData;
      ID = IDcounter;
      IDcounter++;
      guiData.setCanvas(ID, this);
      
      Controler controler = guiData.getControler();
      Rectangle rv = controler.getBounds();

      this.setBounds(rv.x+50, rv.y+rv.height+10, 500, 500);
      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.setLayout(new BorderLayout());
      this.addWindowListener(this);
      
      GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
      Canvas3D canvas = new Canvas3D(config);
      this.add(canvas,BorderLayout.CENTER);
      
      OrbitBehavior orbit = new OrbitBehavior(canvas,OrbitBehavior.REVERSE_ALL);
      orbit.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),1000.0));
      
      universe = new SimpleUniverse(canvas);
      ViewingPlatform vf = universe.getViewingPlatform();
      //vf.setNominalViewingTransform();
      vf.setViewPlatformBehavior(orbit);
      PlatformGeometry pg = new PlatformGeometry();
      pg.addChild(this.createLight());
      vf.setPlatformGeometry(pg);
      
      universe.getViewer().getView().setBackClipPolicy(View.VIRTUAL_EYE);
      universe.getViewer().getView().setBackClipDistance(300.0);
      universe.getViewer().getView().setFrontClipPolicy(View.VIRTUAL_EYE);
      universe.getViewer().getView().setFrontClipDistance(0.5);

      offscreenCanvas3D = new Canvas3D(config,true);        
      universe.getViewer().getView().addCanvas3D(offscreenCanvas3D);

   }
   /**
    * Creates the scene
    */
   public void createSceneGraph(){
      BranchGroup objRoot = new BranchGroup();
      //objRoot.addChild(this.createLight());

      Background back = new Background(new Color3f(Color.white));
      BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.POSITIVE_INFINITY);
      back.setApplicationBounds(bounds);
      objRoot.addChild(back);
      
      for(int i=0; i<molecule.getNat(); i++){
         objRoot.addChild(createAtom(molecule.getAtom(i)));
      }

      AtomUtil autil = new AtomUtil();
      for(int i=0; i<molecule.getNat(); i++){
         Atom atomi = molecule.getAtom(i);
         for(int j=0; j<i; j++){
            Atom atomj = molecule.getAtom(j);
            double rij = autil.getBondLength(atomi, atomj);
            float ri = PeriodicTable.radii[atomi.getAtomicNum()];
            float rj = PeriodicTable.radii[atomj.getAtomicNum()];
            if(rij < (ri+rj)*scale*1.2f){
               objRoot.addChild(createBond(atomi, atomj, rij));
            }
         }
      }
      
      int nat_sum = molecule.getNat_subatom();
      if(nat_sum > 0) {
         for(int i=0; i<nat_sum; i++){
            objRoot.addChild(createAtom(molecule.getSubAtom(i)));
         }
         
         for(int i=0; i<nat_sum; i++){
            Atom atomi = molecule.getSubAtom(i);
            for(int j=0; j<i; j++){
               Atom atomj = molecule.getSubAtom(j);
               double rij = autil.getBondLength(atomi, atomj);
               float ri = PeriodicTable.radii[atomi.getAtomicNum()];
               float rj = PeriodicTable.radii[atomj.getAtomicNum()];
               if(rij < (ri+rj)*scale*1.2f){
                  objRoot.addChild(createBond(atomi, atomj, rij));
               }
            }            
         }
         
         for(int i=0; i<nat_sum; i++){
            Atom atomi = molecule.getSubAtom(i);
            for(int j=0; j<molecule.getNat(); j++){
               Atom atomj = molecule.getAtom(j);
               double rij = autil.getBondLength(atomi, atomj);
               float ri = PeriodicTable.radii[atomi.getAtomicNum()];
               float rj = PeriodicTable.radii[atomj.getAtomicNum()];
               if(rij < (ri+rj)*scale*1.2f){
                  objRoot.addChild(createBond(atomi, atomj, rij));
               }
            }
         }
      }
      
      /*
      objRoot.addChild(createHBond(molecule.getAtom(45),molecule.getAtom(32)));
      objRoot.addChild(createHBond(molecule.getAtom(56),molecule.getAtom(4)));
      objRoot.addChild(createHBond(molecule.getAtom(70),molecule.getAtom(10)));
      objRoot.addChild(createHBond(molecule.getAtom(44),molecule.getAtom(18)));      
      */
      
      double xmax=Double.NEGATIVE_INFINITY, xmin=Double.POSITIVE_INFINITY;
      double ymax=Double.NEGATIVE_INFINITY, ymin=Double.POSITIVE_INFINITY;
      for(int i=0; i<molecule.getNat(); i++){
         double[] xyz = molecule.getAtom(i).getXYZCoordinates();
         if(xyz[0]>xmax) xmax = xyz[0];
         if(xyz[0]<xmin) xmin = xyz[0];
         if(xyz[1]>ymax) ymax = xyz[1];
         if(xyz[1]<ymin) ymin = xyz[1];
      }
      double xlen = xmax - xmin;
      double ylen = ymax - ymin;
      double zz=0.0;
      if(xlen>ylen){
         zz = xlen;
      }else{
         zz = ylen;
      }
      //System.out.printf("%12.4f %12.4f %12.4f \n", (xmax+xmin)/2, (ymax+ymin)/2, zz*2.5);
      Transform3D viewTrans = new Transform3D();
      viewTrans.setTranslation(new Vector3d((xmax+xmin)/2.0,(ymax+ymin)/2.0,zz*2.5));
      universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);
      
      objRoot.compile();
      universe.addBranchGraph(objRoot);
      
   }
   
   private Node createLight(){
      BranchGroup bg = new BranchGroup();
      BoundingSphere bounds = new BoundingSphere(new Point3d(),100.0);
      
      DirectionalLight light1 = new DirectionalLight(true, new Color3f(0.8f,0.8f,0.8f), new Vector3f(0.0f,0.0f,-1.0f));
      //PointLight light1 = new PointLight(true, new Color3f(0.8f,0.8f,0.8f),
      //      new Point3f(0.0f,0.0f,0.0f),
      //      new Point3f(0.8f,0.8f,0.8f));
      light1.setInfluencingBounds(bounds);
      bg.addChild(light1);
      AmbientLight amlight = new AmbientLight(new Color3f(1.0f,1.0f,1.0f));
      amlight.setInfluencingBounds(bounds);
      bg.addChild(amlight);
      
      return bg;
   }
   
   private Node createAtom(Atom atom){
      int anum = atom.getAtomicNum();
      double[] xyz = atom.getXYZCoordinates();
      Vector3d pos = new Vector3d(xyz[0],xyz[1],xyz[2]);
      TransformGroup tg = new TransformGroup();
      Transform3D trans = new Transform3D();
      trans.setTranslation(pos);
      tg.setTransform(trans);
      
      Color3f color = new Color3f(PeriodicTable.getColor(anum));
      Material material = new Material();
      material.setLightingEnable(true);
      material.setDiffuseColor(color);
      //material.setAmbientColor(0.4f,0.4f,0.4f);
      //material.setSpecularColor(1.0f, 1.0f, 1.0f);
      //material.setShininess(64.0f);

      PolygonAttributes pa = new PolygonAttributes();
      //pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
      
      Appearance ap = new Appearance();
      ap.setMaterial(material);
      ap.setPolygonAttributes(pa);
      Sphere sphere = new  Sphere(PeriodicTable.radii[anum]*scale*0.6f,Sphere.GENERATE_NORMALS,50,ap);
      
      tg.addChild(sphere);
      
      return tg;

   }
   
   private Node createBond(Atom ai, Atom aj, double rij){

      double[] xi = ai.getXYZCoordinates();
      double[] xj = aj.getXYZCoordinates();

      double[] xx = new double[3];
      for(int i=0; i<3; i++){
         xx[i] = (xi[i]+xj[i])/2.0;
      }
      Vector3d pos = new Vector3d(xx);
      Transform3D t3d = new Transform3D();
      t3d.setTranslation(pos);

      double[] xij = new double[3];
      for(int i=0; i<3; i++){
         xij[i] = (xi[i]-xj[i])/rij;
         //System.out.println(xij[i]);
      }

      if(Math.abs(xij[1])<0.999){
         double th = Math.acos(xij[1]);
         //System.out.println("th = "+th/Math.PI*180.0);

         Transform3D rotate2 = new Transform3D();
         rotate2.setRotation(new AxisAngle4f((float)xij[2],0.0f,-(float)xij[0],(float)th));
         t3d.mul(rotate2);
         
      }
      
      Color3f color = new Color3f(Color.gray);
      Material material = new Material();
      material.setLightingEnable(true);
      material.setDiffuseColor(color);
      Appearance ap = new Appearance();
      ap.setMaterial(material);
      
      Cylinder cylinder = new Cylinder( scale*0.1f, (float)rij, Cylinder.GENERATE_NORMALS, 15, 1, ap);
      //Cone cylinder = new Cone(0.1f, (float)rij, Cone.GENERATE_NORMALS, 50,1,ap);
      
      TransformGroup tg = new TransformGroup();
      tg.setTransform(t3d);
      tg.addChild(cylinder);
      
      return tg;
   }
   
   private Node createHBond(Atom ai, Atom aj){
      
      double[] xi = ai.getXYZCoordinates();
      double[] xj = aj.getXYZCoordinates();      
      
      Point3d[] vertices = new Point3d[2];
      vertices[0] = new Point3d(xi);
      vertices[1] = new Point3d(xj);
      
      LineArray geometry = new LineArray(vertices.length, 
            GeometryArray.COORDINATES | GeometryArray.COLOR_3);
      geometry.setCoordinates(0,vertices);
      geometry.setColor(0, new Color3f(1.0f,(153.0f/225.0f),(51.0f/225.0f)));
      geometry.setColor(1, new Color3f(1.0f,(153.0f/225.0f),(51.0f/225.0f)));
      
      LineAttributes lattr = new LineAttributes();
      lattr.setLineWidth(2.0f);
      lattr.setLinePattern(LineAttributes.PATTERN_DASH);
      lattr.setLineAntialiasingEnable(true);
      Appearance aleft = new Appearance();
      aleft.setLineAttributes(lattr);
      
      Shape3D shape = new Shape3D(geometry,aleft);
      
      return shape;
      
   }
   /**
    * Append the molecule 
    * @param molecule the molecule
    */
   public void appendMolecule(Molecule molecule){
      
      MolUtil mutil = new MolUtil();
      mutil.appendMolecule(molecule);
      double[] com = mutil.getCenterOfMass();
      
      xyz_org = molecule.getXYZCoordinates2();
      
      for(int n=0; n<molecule.getNat(); n++){
         double[] xyz = new double[3];
         for(int i=0; i<com.length; i++){
            xyz[i] = (xyz_org[n][i] - com[i])*scale;
         }
         molecule.getAtom(n).setXYZCoordinates(xyz);
      }
      xyz_int = molecule.getXYZCoordinates2();
      
      int nat_sub = molecule.getNat_subatom();
      if(nat_sub > 0){
         xyz_sub_org = molecule.getXYZCoordinates2_subatom();
         
         for(int n=0; n<nat_sub; n++){
            double[] xyz = new double[3];
            for(int i=0; i<3; i++){
               xyz[i] = (xyz_sub_org[n][i] - com[i])*scale;
            }
            molecule.getSubAtom(n).setXYZCoordinates(xyz);
         }
         xyz_sub_int = molecule.getXYZCoordinates2_subatom();
         
      }

      is_xyz_org = false;
      this.molecule = molecule;
   }
   /**
    * Returns the molecule of this canvas with scaled coordinates
    * @return the molecule
    */
   public Molecule getMolecule(){
      return this.getMolecule(true);
   }
   /**
    * Returns the molecule of this canvas
    * @param scaledCoordinates in scaled coordinates if true, and in input coordinates if false
    * @return the molecule
    */
   public Molecule getMolecule(boolean scaledCoordinates){
      
      if(scaledCoordinates){
         if(is_xyz_org){
            for(int n=0; n<molecule.getNat(); n++){
               molecule.getAtom(n).setXYZCoordinates(xyz_int[n]);
            }
            
            int nat_sub = molecule.getNat_subatom();
            if(nat_sub > 0) {
               for(int n=0; n<nat_sub; n++){
                  molecule.getSubAtom(n).setXYZCoordinates(xyz_sub_int[n]);
               }
            }
            
            is_xyz_org = false;
         }
      }else{
         if(! is_xyz_org){
            for(int n=0; n<molecule.getNat(); n++){
               molecule.getAtom(n).setXYZCoordinates(xyz_org[n]);
            }
            
            int nat_sub = molecule.getNat_subatom();
            if(nat_sub > 0) {
               for(int n=0; n<nat_sub; n++){
                  molecule.getSubAtom(n).setXYZCoordinates(xyz_sub_org[n]);
               }
            }

            is_xyz_org = true;            
         }
      }
      /*
      for(int n=0; n<molecule.getNat(); n++){
         molecule.getAtom(n).setXYZCoordinates(xyz_org[n]);
      }
      */
      return this.molecule;
   }
   /**
    * Returns the SimpleUniverse of this canvas
    * @return the SimpleUniverse
    */
   public SimpleUniverse getUniverse(){
      return this.universe;
   }
   /**
    * Returns the off-screen canvas
    * @return the off-screen canvas
    */
   public Canvas3D getOffscreenCanvas3D(){
      return this.offscreenCanvas3D;
   }
   /**
    * Returns the scale factor
    * @return scale factor
    */
   public float getScale(){
      return scale;
   }
   
   @Override
   public void windowActivated(WindowEvent arg0) {
      currentID = this.ID;
      guiData.getFileItems(JMenuFile.CLOSE).setEnabled(true);
      guiData.getFileItems(JMenuFile.SAVE).setEnabled(true);
      guiData.getFileItems(JMenuFile.SAVEAS).setEnabled(true);
      guiData.getFileItems(JMenuFile.EXPORT).setEnabled(true);
      guiData.getShowItems(JMenuShow.LABEL).setEnabled(true);
      guiData.getShowItems(JMenuShow.ATOMNUMBER).setEnabled(true);
      if(molecule.getNumOfVibrationalData() != 0){
         guiData.getShowItems(JMenuShow.VIBDATA).setEnabled(true);
      }else{
         guiData.getShowItems(JMenuShow.VIBDATA).setEnabled(false);
      }
      /*
      if(molecule.getNat_subatom() > 0){
         guiData.getShowItems(JMenuShow.SUBATOM).setEnabled(true);
      }else{
         guiData.getShowItems(JMenuShow.SUBATOM).setEnabled(false);         
      }
      */
      
      ElectronicData edata = molecule.getElectronicData();
      if(edata == null || edata.getHessian() == null){
         guiData.getToolsItems(JMenuTools.HARMONIC).setEnabled(false);
         guiData.getToolsItems(JMenuTools.LOCAL).setEnabled(false);         
      }else{
         guiData.getToolsItems(JMenuTools.HARMONIC).setEnabled(true);         
         guiData.getToolsItems(JMenuTools.LOCAL).setEnabled(true);
      }

      //System.out.println("active ID = "+ID);
      
   }

   @Override
   public void windowClosed(WindowEvent arg0) {
      currentID = -1;
      guiData.removeCanvas(ID);
      if(guiData.getFreqTable(ID) != null) {
         guiData.getFreqTable(ID).dispose();
         guiData.removeFreqTable(ID);
      }
      if(guiData.getLocaliza(ID) != null){
         guiData.getLocaliza(ID).dispose();
         guiData.removeLocaliza(ID);
      }
      guiData.getFileItems(JMenuFile.CLOSE).setEnabled(false);
      guiData.getFileItems(JMenuFile.SAVE).setEnabled(false);
      guiData.getFileItems(JMenuFile.SAVEAS).setEnabled(false);
      guiData.getFileItems(JMenuFile.EXPORT).setEnabled(false);
      guiData.getShowItems(JMenuShow.LABEL).setEnabled(false);
      guiData.getShowItems(JMenuShow.ATOMNUMBER).setEnabled(false);
      guiData.getShowItems(JMenuShow.VIBDATA).setEnabled(false);
      //guiData.getShowItems(JMenuShow.SUBATOM).setEnabled(false);
      guiData.getToolsItems(JMenuTools.HARMONIC).setEnabled(false);
      guiData.getToolsItems(JMenuTools.LOCAL).setEnabled(false);
      //guiData.getToolsItems(JMenuTools.MAKEPES).setEnabled(false);
      //guiData.getToolsItems(JMenuTools.SINDO).setEnabled(false);
      
      //System.out.println("Removed ID = "+ID);
         
   }

   @Override
   public void windowClosing(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowDeactivated(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowDeiconified(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowIconified(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void windowOpened(WindowEvent arg0) {
      // TODO Auto-generated method stub
      
   }

}
