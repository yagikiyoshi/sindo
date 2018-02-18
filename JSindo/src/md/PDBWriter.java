package md;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import sys.Constants;

/**
 * Prints/writes the information of the system in PDB format.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class PDBWriter {

   private SystemMD system;
   private PrintWriter pw;
   private Segment[] segments;
   private boolean printTrajectory = false;

   /**
    * Sets to print the trajectory 
    */
   public void setPrintTrajectory(){
      this.printTrajectory = true;
   }
   
   /**
    * Sets to print a snapshot, not the trajectory
    */
   public void unsetPrintTrajectory(){
      this.printTrajectory = false;
   }
   
   /**
    * Inquire if the trajectory is printed
    * @return true to print trajectory
    */
   public boolean isPrintTrajectory(){
      return this.printTrajectory;
   }
   
   /**
    * Print the information to the console
    * @param segname Segment name
    * @param residues The residues to be printed
    */
   public void print(String segname, Residue[] residues){
      segments = new Segment[1];
      segments[0] = new Segment();
      segments[0].setName(segname);
      try {
         segments[0].addResidueList(residues);
      } catch (BuilderException e) {
         e.printStackTrace();
      }
      segments[0].closeResidueList();
      this.printAtoms();      
   }
   
   /**
    * Print the information to the console
    * @param segment The segments to be printed 
    */
   public void print(Segment segment){
      segments = new Segment[1];
      segments[0] = segment;
      this.printAtoms();
   }
   
   /**
    * Print the information to the console
    * @param system The system to be printed
    */
   public void print(SystemMD system){      
      this.segments = system.getSegmentALL();
      this.printAtoms();
   }
   
   /**
    * Writes the information to a file 
    * @param fileName The name of a file
    * @param segment The segment to be written
    * @throws IOException Thrown when I/O error occurred or segment has illegal block
    */
   public void write(String fileName, Segment segment) throws IOException {
      SystemMD sys = new SystemMD();
      try {
         sys.addSegmentList(segment);
      } catch (BuilderException e) {
         e.printStackTrace();
         throw new IOException("Error occured while adding segment :"+segment.getName());
      }
      sys.closeSegmentList();
      this.write(fileName, sys);
      
   }
   
   /**
    * Writes the information to a file. Note that current frame is written by
    * default. Set printTrajectory to print the whole trajectory.
    * @param fileName The name of a file
    * @param system The system to be written
    * @throws IOException Thrown when I/O error occurred
    */
   public void write(String fileName, SystemMD system) throws IOException {

      this.system = system;
      this.segments = system.getSegmentALL();
      this.pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
      
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss z");
      pw.println("REMARK  "+sdf.format(date)+" created by "+System.getProperty("user.name"));

      String title = system.getTitle();
      if(title != null){
         pw.println("TITLE  "+title);
      }else{
         //Segment[] segments = system.getSegmentALL();
         pw.printf("TITLE  PDB FOR ");
         for(int i=0; i<segments.length; i++){
            pw.printf(segments[i].getName()+" ");
         }
         pw.println();
      }

      Trajectory traj = system.getTrajectory();
      if(traj == null){
         this.writeBox();
         this.writeAtoms();
         pw.println("END");
                  
      }else{
         if(this.printTrajectory){
            system.setFrame(0);
            this.writeBox();
            this.writeAtoms();
            pw.println("END");

            int numOfframe = traj.getNumOfFrames();
            if(traj.isBoxes()){
               for(int n=1; n<numOfframe; n++){
                  system.setFrame(n);
                  this.writeBox();
                  this.writeAtoms();
                  pw.println("END");
               }
            }else{
               for(int n=1; n<numOfframe; n++){
                  system.setFrame(n);
                  this.writeAtoms();
                  pw.println("END");
               }
            }

         }else{
            this.writeBox();
            this.writeAtoms();
            pw.println("END");
            
         }
            
      }

      pw.close();
   }
   
   private void writeBox(){
      Box box = system.getBox();
      if(box != null){
         pw.println("REMARK    THIS IS A SIMULATION BOX");
         double[] boxSize = box.getBox();
         pw.printf("CRYST1 %8.3f %8.3f %8.3f %6.2f %6.2f %6.2f%s\n", 
               boxSize[0]*Constants.Bohr2Angs,
               boxSize[1]*Constants.Bohr2Angs,
               boxSize[2]*Constants.Bohr2Angs,
               boxSize[3],
               boxSize[4],
               boxSize[5],
               box.getGroup());
      }
   }
   
   private void writeAtoms(){
      //Segment[] segments = system.getSegmentALL();
      for(int nseg=0; nseg<segments.length; nseg++){
         Segment segment = segments[nseg];
         String segName = segment.getName();
         if(segName == null){
            segName = "";
         }
         Residue[] residues = segment.getResidueALL();
         for(int nres=0; nres<residues.length; nres++){
            Residue residue = residues[nres];
            String resName = residue.getName();
            int resID = residue.getID();
            AtomMD[] atoms = residue.getAtomALL();
            for(int natom=0; natom<atoms.length; natom++){
               AtomMD atom = atoms[natom];
               String name = atom.getLabel();
               int atomID = atom.getID();
               double[] xyz = atom.getXYZCoordinates();
               double occ = atom.getOcc();
               double beta = atom.getBeta();
               
               String s1 = null;
               if(name.length()<4){
                  s1 = "  %-3s";
               }else{
                  s1 = " %-4s";
               }
               
               String s2 = null;
               if(resID < 1000){
                  s2 = "%4d    ";
               }else{
                  s2 = "%5d   ";
               }
               pw.printf("ATOM%7d"+s1+" %-4s "+s2+"%8.3f%8.3f%8.3f%6.2f%6.2f      %-4s\n", 
                     atomID,name,resName,resID,
                     xyz[0]*Constants.Bohr2Angs,
                     xyz[1]*Constants.Bohr2Angs,
                     xyz[2]*Constants.Bohr2Angs,occ,beta,segName);         
            }
         }
      }
   }
   
   private void printAtoms(){
      //Segment[] segments = system.getSegmentALL();
      for(int nseg=0; nseg<segments.length; nseg++){
         Segment segment = segments[nseg];
         String segName = segment.getName();
         if(segName == null){
            segName = "";
         }
         Residue[] residues = segment.getResidueALL();
         for(int nres=0; nres<residues.length; nres++){
            Residue residue = residues[nres];
            String resName = residue.getName();
            int resID = residue.getID();
            AtomMD[] atoms = residue.getAtomALL();
            for(int natom=0; natom<atoms.length; natom++){
               AtomMD atom = atoms[natom];
               String name = atom.getLabel();
               int atomID = atom.getID();
               double[] xyz = atom.getXYZCoordinates();
               double occ = atom.getOcc();
               double beta = atom.getBeta();
               String s1 = null;
               if(name.length()<4){
                  s1 = "  %-3s";
               }else{
                  s1 = " %-4s";
               }
               
               String s2 = null;
               if(resID < 1000){
                  s2 = "%4d    ";
               }else{
                  s2 = "%5d   ";
               }
               System.out.printf("ATOM%7d"+s1+" %-4s "+s2+"%8.3f%8.3f%8.3f%6.2f%6.2f      %-4s\n", 
                     atomID,name,resName,resID,
                     xyz[0]*Constants.Bohr2Angs,
                     xyz[1]*Constants.Bohr2Angs,
                     xyz[2]*Constants.Bohr2Angs,occ,beta,segName);         

            }
         }
      }

   }

}
