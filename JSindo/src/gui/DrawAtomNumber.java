package gui;

import java.awt.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.vecmath.*;
import molecule.*;
import sys.*;

public class DrawAtomNumber {

   private static final long serialVersionUID = 1L;
   
   private GUIData guiData;
   private int ID;
   private BranchGroup scene;
   private Canvas canvas;
   private boolean visual;
   
   DrawAtomNumber(GUIData guiData, int ID){
      this.guiData = guiData;
      this.ID = ID;
      guiData.setDrawAtomNumber(ID, this);
      
      canvas = guiData.getCanvas(ID);

   }
   
   public void createSceneGraph(){
      
      scene = new BranchGroup();
      scene.setCapability(BranchGroup.ALLOW_DETACH);
      
      float scale = canvas.getScale();
      
      Material m = new Material();
      m.setDiffuseColor(new Color3f(Color.BLACK));
      Appearance a = new Appearance();
      a.setMaterial(m);
      
      Molecule molecule = canvas.getMolecule();
      for(int n=0; n<molecule.getNat(); n++){
         double[] xyz = molecule.getAtom(n).getXYZCoordinates();         
         int anum = molecule.getAtom(n).getAtomicNum();
         float shift = PeriodicTable.radii[anum]*scale*0.6f;
         Vector3d pos = new Vector3d(xyz[0]+shift,xyz[1]+shift,xyz[2]+shift);
         
         TransformGroup tg = new TransformGroup();
         Transform3D trans = new Transform3D();
         trans.setTranslation(pos);
         tg.setTransform(trans);
         
         Text2D text = new Text2D(String.valueOf(n+1), new Color3f(Color.BLACK), "Dialog", 12, java.awt.Font.BOLD);
         OrientedShape3D os = new OrientedShape3D();
         os.setGeometry(text.getGeometry());
         os.setAppearance(text.getAppearance());

         tg.addChild(os);
         scene.addChild(tg);
         
      }
      
      scene.compile();

      SimpleUniverse universe = canvas.getUniverse();
      universe.addBranchGraph(scene);

      visual = true;
   }
   
   public void detachSceneGraph(){
      if(scene != null) {
         scene.detach();
         visual = false;
      }
   }
   
   public boolean isVisible(){
      return visual;
   }
}
