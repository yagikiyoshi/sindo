package md;

import java.io.*;
import java.util.*;

import sys.Constants;

/**
 * Read the information from a PDB file
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class PDBReader extends FileReaderMD {

   private String alterLoc;
   private ANResolver anr;
   private boolean traj;
   
   public PDBReader(){
      super();
      anr = new ANResolver();
      alterLoc = null;
   }
   /**
    * Constructs specifying the name of the file 
    * @param fileName the name of the file to be read
    */
   public PDBReader(String fileName){
      this();
      super.fname = fileName;
   }
   
   /**
    * Returns the ANResolver in this reader
    * @return The ANResolver
    */
   public ANResolver getANResolver() {
      return anr;
   }

   /**
    * Sets the ANResolver for this reader
    * @param anr THe ANResolver
    */
   public void setANResolver(ANResolver anr) {
      this.anr = anr;
   }

   /**
    * Reads the segments from the specified PDB file
    * @return an array of segments
    * @throws IOException thrown when an IO error occurred
    */
   public Segment[] readSegments() throws IOException{
      
      system.initSegmentList();

      Segment current_seg = new Segment();
      current_seg.setName(null);
      
      ArrayList<Residue> residues=null;
      Residue current_res=null;
      
      String segName;
      int resID;

      String line = null;
      try{
         
         traj = false;
         
         BufferedReader  br = new BufferedReader(new FileReader(fname));
         line = br.readLine();
         while(line != null && line.indexOf("END") < 0){
            if(line.length() > 3){
               if(line.substring(0, 4).equalsIgnoreCase("ATOM") ||  line.substring(0, 6).equalsIgnoreCase("HETATM")){
                  AtomMD atom = this.readAtom(line);
                  
                  if(atom != null){ 
                     if(line.length() > 72){
                        segName = line.substring(72, 76).trim();
                        if(segName.length() == 0){
                           segName = "PROA";
                        }
                     }else{
                        segName = "PROA";
                     }

                     if(segName.length() > 0 && ! segName.equals(current_seg.getName())){

                        current_seg = new Segment();
                        current_seg.setName(segName);
                        
                        residues = current_seg.getResidueList();
                        current_res = new Residue();
                        current_res.setID(-1);

                        system.addSegmentList(current_seg);
                     }
                     
                     resID = Integer.parseInt(line.substring(22,26).trim());
                     
                     if(resID == current_res.getID()){
                        current_res.addAtomList(atom);
                        
                     }else{
                                             
                        current_res = new Residue();
                        current_res.setID(resID);
                        current_res.setName(line.substring(17, 21).trim());
                        current_res.addAtomList(atom);
                        
                        residues.add(current_res);

                     }
                  }
               }
            }
            
            line = br.readLine();
            
         }
         
         // if ATOM entry exists after "END" syntax, this is a PDB 
         // file with multiple snapshots, so we setup trajectory.
         while(line != null && line.indexOf("END") < 0){
            if(line.substring(0, 4).equalsIgnoreCase("ATOM") ||  line.substring(0, 6).equalsIgnoreCase("HETATM")){
               traj = true;
               break;
            }
            line = br.readLine();
         }
         
         br.close();

      }catch(BuilderException e){
         e.printStackTrace();
         return null;
      }

      system.closeSegmentList();
      return system.getSegmentALL();
      
   }

   private AtomMD readAtom(String line){
      AtomMD atom = new AtomMD();
      
      String altLoc = line.substring(16, 17);
      if(! altLoc.equals(" ")){
         if(alterLoc != null){
            if(! alterLoc.equals(altLoc)){
               return null;
            }
         }else{
            alterLoc = altLoc;
         }
      }
      
      int atomID = Integer.parseInt(line.substring(6, 11).trim());
      atom.setID(atomID);

      String label = line.substring(12, 16).trim();
      atom.setLabel(label);
      int an = anr.getAtomicNumber(label);
      if(an > 0) atom.setAtomicNum(an);
      
      double[] xyz = new double[3];
      xyz[0] = Double.parseDouble(line.substring(30, 38))/Constants.Bohr2Angs; 
      xyz[1] = Double.parseDouble(line.substring(38, 46))/Constants.Bohr2Angs; 
      xyz[2] = Double.parseDouble(line.substring(46, 54))/Constants.Bohr2Angs;
      atom.setXYZCoordinates(xyz);
      
      double occ = Double.parseDouble(line.substring(54, 60));
      atom.setOcc(occ);
      
      double beta = Double.parseDouble(line.substring(60,66));
      atom.setBeta(beta);
      
      return atom;
   }

   /**
    * Reads the coordinates of the atoms. It is assumed to be called after the modeling stage, that is,
    * the number of atoms/residues/segments and their order must be consistent with the system that is 
    * already set to the Reader. Only the number of atoms is checked, but the name (or type) and the 
    * orders is unchecked. 
    * @return xyz coordinates of the atoms (bohr)
    * @throws IOException thrown when an IO error is detected or when the system is not set.
    */
   public double[][] readCoordinates() throws IOException{

      if(system == null || system.getSegmentALL() == null){
         throw new IOException("Attempted to read coordinates before any segment is defined in the system.");
      }
      
      int Natom = system.getNumOfAtom();
      AtomMD[] atoms = system.getAtomALL();
      double[][] xyz = new double [Natom][];

      BufferedReader  br = new BufferedReader(new FileReader(fname));
      String line = null;
      while((line = br.readLine()).indexOf("END") < 0){
         if(line.length() > 3){
            if(line.substring(0, 4).equalsIgnoreCase("ATOM")){
               for(int nn=0; nn<Natom; nn++){
                  xyz[nn] = this.readCoordinates(line);
                  atoms[nn].setXYZCoordinates(xyz[nn]);
                  line = br.readLine();
               }
               break;
            }
         }
      }
      br.close();
      return xyz;
   }

   private double[] readCoordinates(String line){
      double[] xyz = new double[3];
      xyz[0] = Double.parseDouble(line.substring(30, 38))/Constants.Bohr2Angs; 
      xyz[1] = Double.parseDouble(line.substring(38, 46))/Constants.Bohr2Angs; 
      xyz[2] = Double.parseDouble(line.substring(46, 54))/Constants.Bohr2Angs;
      return xyz;
   }
   
   /**
    * Reads the periodic box from the specified PDB file
    * @return the periodic box
    * @throws IOException Exception while reading a box
    */
   public Box readBox() throws IOException{
      
      if(system == null){
         system = new SystemMD();
      }

      String line = null;
      BufferedReader  br = new BufferedReader(new FileReader(fname));
      line = br.readLine();
      while(line != null && line.indexOf("END") < 0){   
         if(line.length() > 5){
            if(line.substring(0, 6).equalsIgnoreCase("CRYST1")){
               Box box = this.readBox(line);
               system.setBox(box);
               break;
            }
         }
         line = br.readLine();
      }
      br.close();
      
      return system.getBox();
   }
   
   private Box readBox(String line){
      Box box = new Box();
      double[] boxsize = new double[6];
      boxsize[0] = Double.parseDouble(line.substring(6,15))/Constants.Bohr2Angs;
      boxsize[1] = Double.parseDouble(line.substring(15,24))/Constants.Bohr2Angs;
      boxsize[2] = Double.parseDouble(line.substring(24,33))/Constants.Bohr2Angs;
      boxsize[3] = Double.parseDouble(line.substring(33,40));
      boxsize[4] = Double.parseDouble(line.substring(40,47));
      boxsize[5] = Double.parseDouble(line.substring(47,54));
      box.setBox(boxsize);
      if(line.length() > 54){
         box.setGroup(line.substring(54,70));
      }
      return box;
   }

   public String readTitle() throws IOException{
      
      if(system == null){
         system = new SystemMD();
      }

      String title = null;
      
      String line = null;
      BufferedReader  br = new BufferedReader(new FileReader(fname));
      while((line = br.readLine()) != null){   
         if(line.length() > 5){
            if(line.substring(0, 5).equalsIgnoreCase("TITLE")){
               title = line.substring(5,line.length()).trim();
               system.setTitle(title);
               break;
            }
         }
      }
      br.close();
      
      return title;
      
   }
   
   /**
    * Reads the coordinates and optionally the boxes along the trajectory. It is assumed to be called
    * after the modeling stage, that is, the number of atoms/residues/segments and their order must be 
    * consistent with the system that is already set to the Reader. 
    * @return Instance of Trajectory 
    * @throws IOException thrown when an IO error is detected or when the system is not set.
    */
   public Trajectory readTrajectory() throws IOException{
      
      if(system == null || system.getSegmentALL() == null){
         throw new IOException("Attempted to read trajectory before any segment is defined in the system.");
      }

      Trajectory traj = new Trajectory();
      system.setTrajectory(traj);

      int Natom = system.getNumOfAtom();
      String line = null;
      BufferedReader  br = new BufferedReader(new FileReader(fname));
      
      while((line = br.readLine()) != null){
         if(line.length() > 5){
            if(line.substring(0, 6).equalsIgnoreCase("CRYST1")){
               traj.addBox(this.readBox(line));
            }
         }
         if(line.length() > 3){
            if(line.substring(0, 4).equalsIgnoreCase("ATOM")){
               double[][] xyz = new double[Natom][];
               for(int nn=0; nn<Natom; nn++){
                  xyz[nn] = this.readCoordinates(line);
                  line = br.readLine();
               }
               traj.addCoordinates(xyz);
            }
         }
      }
      
      br.close();
      
      return system.getTrajectory();
   }
   
   /**
    * Sets the label of an alternate location to be read from the PDB file
    * @param altLoc The label of an alternate location
    */
   public void setAlternateLocation(String altLoc){
      this.alterLoc = altLoc;
   }
   
   @Override
   protected SystemMD readAllChild() throws IOException {
      this.readSegments();
      if(traj){
         this.readTrajectory(); 
      }
      this.readBox();
      this.readTitle();
      return system;
   }

}
