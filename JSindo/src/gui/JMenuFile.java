package gui;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

import java.io.*;
import java.util.Arrays;

import javax.media.j3d.*;
import javax.imageio.ImageIO;

import com.sun.j3d.utils.universe.SimpleUniverse;

import qchem.*;
import sys.Constants;
import molecule.*;

/**
 * (Package private) The class for the File menu.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class JMenuFile extends JMenu implements ActionListener {

   private static final long serialVersionUID = 1L;

   private GUIData guiData;
   private String dirPath = null;

   public static String OPEN   = "Open";
   public static String CLOSE  = "Close";
   public static String SAVE   = "Save";
   public static String SAVEAS = "Save as";
   public static String IMPORT = "Import";
   public static String EXPORT = "Export";
   public static String QUIT   = "Quit";
   
   /**
    * Constructs the object for the file menu
    * @param guiData
    */
   JMenuFile(GUIData guiData){
      this.setText("File");
      this.guiData = guiData;
      dirPath = System.getProperty("user.dir");
      
      JMenuItem open = new JMenuItem(OPEN);
      open.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
      open.addActionListener(this);
      this.add(open);
      guiData.setFileItems(OPEN, open);

      JMenuItem close = new JMenuItem(CLOSE);
      close.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
      close.addActionListener(this);
      close.setEnabled(false);
      this.add(close);
      guiData.setFileItems(CLOSE, close);

      JMenuItem save = new JMenuItem(SAVE);
      save.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
      save.addActionListener(this);
      save.setEnabled(false);
      this.add(save);
      guiData.setFileItems(SAVE, save);

      JMenuItem saveas = new JMenuItem(SAVEAS);
      saveas.addActionListener(this);
      saveas.setEnabled(false);
      this.add(saveas);
      guiData.setFileItems(SAVEAS, saveas);

      this.addSeparator();

      JMenuItem imp = new JMenuItem(IMPORT);
      imp.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
      imp.addActionListener(this);
      this.add(imp);
      guiData.setFileItems(IMPORT, imp);

      JMenuItem exp = new JMenuItem(EXPORT);
      exp.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
      exp.addActionListener(this);
      exp.setEnabled(false);
      this.add(exp);
      guiData.setFileItems(EXPORT, exp);
      
      this.addSeparator();
      
      JMenuItem quit = new JMenuItem(QUIT);
      quit.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
      quit.addActionListener(this);
      this.add(quit);
      guiData.setFileItems(QUIT, quit);
      
      
   }
   
   public void actionPerformed(ActionEvent event){
      String com = event.getActionCommand();
      if(com.equals(OPEN)){
         JFileChooser filechooser = new JFileChooser(dirPath);
         filechooser.setAcceptAllFileFilterUsed(false);
         FileFilter minfo = new FileNameExtensionFilter("Molecular Info File (*.minfo)","minfo");
         filechooser.addChoosableFileFilter(minfo);
         filechooser.setAcceptAllFileFilterUsed(true);
         
         int selected = filechooser.showOpenDialog(this);
         if(selected == JFileChooser.APPROVE_OPTION){
            File file = filechooser.getSelectedFile();
            Canvas canvas = new Canvas(guiData);
            
            dirPath = file.getAbsolutePath();
            setName(canvas,file);

            try{
               MInfoIO util = new MInfoIO();
               canvas.appendMolecule(util.loadMOL(file.getAbsolutePath()));               
            }catch(IOException e1){
               System.out.println(e1.getMessage());
               return;
            }
            canvas.createSceneGraph();
            canvas.setVisible(true);
            
         }
         
      }
      if(com.equals(CLOSE)){
         Canvas canvas = guiData.getCanvas(Canvas.currentID);
         canvas.dispose();
         
      }
      if(com.equals(SAVE)){
         Canvas canvas = guiData.getCanvas(Canvas.currentID);
         MInfoIO minfoIO = new MInfoIO(canvas.getMolecule(false));         
         String filepath = canvas.getName();
         System.out.print("Save to "+filepath+" ....");
         try {
            minfoIO.dumpMOL(filepath);
         } catch (IOException e) {
            System.out.println("Error while saving the file.");
            System.out.println(e.getMessage());
         }
         System.out.println(" [OK]. ");
         
      }
      if(com.equals(SAVEAS)){
         JFileChooser filechooser = new JFileChooser(dirPath);

         int selected = filechooser.showSaveDialog(this);
         if(selected == JFileChooser.APPROVE_OPTION){
            Canvas canvas = guiData.getCanvas(Canvas.currentID);
            MInfoIO minfoIO = new MInfoIO(canvas.getMolecule(false));
            File file = filechooser.getSelectedFile();
            dirPath = file.getAbsolutePath();
            
            setName(canvas,file);
            System.out.print("Save to "+canvas.getName()+" ....");
            try {
               minfoIO.dumpMOL(canvas.getName());
            } catch (IOException e) {
               System.out.println("Error while saving the file.");
               System.out.println(e.getMessage());
            }
            System.out.println(" [OK]. ");
         }
         
      }
      if(com.equals(IMPORT)){
         JFileChooser filechooser = new JFileChooser(dirPath);
         
         QuantChem qchem = new QuantChem();
         for(int i=0; i<Constants.qchemType.length; i++){
            Exec exec = null;
            try {
               exec = qchem.getExec(Constants.qchemType[i]);
            } catch (TypeNotSupportedException e) {
               // Do nothing
            }
           filechooser.addChoosableFileFilter(exec.getFilter());
            
         }
         filechooser.setAcceptAllFileFilterUsed(false);
         
         int selected = filechooser.showOpenDialog(this);
         if(selected == JFileChooser.APPROVE_OPTION){
            File file = filechooser.getSelectedFile();
            dirPath = file.getAbsolutePath();

            String description = filechooser.getFileFilter().getDescription().toUpperCase();
            String type=null;
            for(int i=0; i<Constants.qchemType.length; i++){
               if(description.indexOf(Constants.qchemType[i])==0){
                  type = Constants.qchemType[i];
                  break;
               }
            }
            //System.out.println(type);
            
            String basename = file.getAbsolutePath();
            int pos = basename.lastIndexOf(".");
            if(pos > 0) basename = basename.substring(0, pos);
            
            OutputReader outputReader = null;
            try{
               outputReader = qchem.getOutputReader(type); 
            }catch(TypeNotSupportedException ee){
               // Do nothing
            }
            outputReader.setBasename(basename);
            QChemToMol reader = new QChemToMol(outputReader);
            
            try {
               reader.check();
            } catch (FileNotFoundException e) {
               // Do nothing
            } catch (OutputFileException e) {
               System.out.println(e.getMessage());
               return;
            }
            
            Canvas canvas = new Canvas(guiData);
            setName(canvas,file);
            canvas.appendMolecule(reader.read());
            canvas.createSceneGraph();
            canvas.setVisible(true);
            
         }
         
         
      }
      if(com.equals(EXPORT)){
         int idx = Canvas.currentID;
         if(idx>=0){
            System.out.println("Export Canvas : CurrentID = " + idx);
            Canvas currentCanvas = guiData.getCanvas(idx);
            System.out.println(currentCanvas.getTitle());

            Canvas3D canvas3D = currentCanvas.getUniverse().getCanvas();
            Point loc = canvas3D.getLocationOnScreen();

            Canvas3D offScreenCanvas3D = currentCanvas.getOffscreenCanvas3D();
            offScreenCanvas3D.setOffScreenLocation(loc);

            int off_screen_scale = 1;
            Dimension dim = canvas3D.getSize();
            System.out.println("width="+dim.width);
            System.out.println("height="+dim.height);
            dim.width *= off_screen_scale;
            dim.height *= off_screen_scale;
            
            // TODO These size is always constant!?
            Screen3D sOn  = canvas3D.getScreen3D();
            //System.out.println("Physical Screen Width="+sOn.getPhysicalScreenWidth());
            //System.out.println("Physical Screen Height="+sOn.getPhysicalScreenHeight());
            Dimension sdim = sOn.getSize();
            //System.out.println("Screen width="+sdim.width);
            //System.out.println("Screen height="+sdim.height);
            
            Screen3D sOff = offScreenCanvas3D.getScreen3D();
            sOff.setSize(sdim);
            sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth()
                * off_screen_scale);
            sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight()
                * off_screen_scale);

            // render to off screen
            BufferedImage bImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bImage);
            buffer.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
            offScreenCanvas3D.setOffScreenBuffer(buffer);
            offScreenCanvas3D.renderOffScreenBuffer();
            offScreenCanvas3D.waitForOffScreenRendering();

            /*
            String[] fs = ImageIO.getWriterFormatNames();
            System.out.println(Arrays.toString(fs));
            
            The above says,
            
            [JPG, jpg, bmp, BMP, gif, GIF, WBMP, png, PNG, wbmp, jpeg, JPEG]
            
            are supported, but only gif and png works in MacOSX. 
            JPG gives black screen. BMP doesn't even create a file... 
            */
            
            // write to file
            JFileChooser filechooser = new JFileChooser(dirPath);
            filechooser.setAcceptAllFileFilterUsed(false);
            FileFilter png = new FileNameExtensionFilter("PNG (*.png)","png");
            filechooser.addChoosableFileFilter(png);
            //filechooser.setAcceptAllFileFilterUsed(true);

            int selected = filechooser.showSaveDialog(this);
            if(selected == JFileChooser.APPROVE_OPTION){
               File file = filechooser.getSelectedFile();
               dirPath = file.getAbsolutePath();
               
               System.out.print("Save to "+file.getName()+" ....");
               try {
                  ImageIO.write(offScreenCanvas3D.getOffScreenBuffer().getRenderedImage(), "png", file);
               } catch (IOException e) {
                  System.out.println("Error while saving the file.");
                  System.out.println(e.getMessage());
               }
               System.out.println(" [OK]. ");
            }
            
         }
         
      }
      if(com.equals(QUIT)){
         System.exit(0);
         
      }
   }
   
   /*
    * Set the title and name of canvas
    */
   private void setName(Canvas canvas, File file){
      //System.out.println(file.getName());
      //System.out.println(file.getAbsoluteFile());
      //System.out.println(file.getParent());
      
      String basename = file.getName();
      int pos = basename.lastIndexOf(".");
      if(pos > 0) basename = basename.substring(0, pos);

      String path = file.getParent();

      canvas.setTitle(basename);
      canvas.setName(path+"/"+basename+".minfo");

   }

}
