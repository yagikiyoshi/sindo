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
public class CRDWriter {

   private SystemMD system;
   private PrintWriter pw;
   private Segment[] segments;
   private boolean ioextend;
   private String format;
   private String f0  = "%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4d%10.5f\n";
   private String fex = "%10d%10d  %-8s  %-8s%20.10f%20.10f%20.10f  %-8s  %-8d%20.10f\n";
   
   public CRDWriter(){
      this.setIoextend(false);
   }
   /**
    * Returns the value of IOextend
    * @return true if extended format is on.
    */
   public boolean isIOextend() {
      return ioextend;
   }

   /**
    * Sets the value of IOextend
    * @param ioextend true/false to switch the extended format on/off
    */
   public void setIoextend(boolean ioextend) {
      this.ioextend = ioextend;
      if(ioextend){
         format = fex;
      }else{
         format = f0;     
      }
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
    * Writes the information to a file. Note that the coordinates of a current 
    * frame is written.
    * @param fileName The name of a file
    * @param system The system to be written
    * @throws IOException Thrown when I/O error occurred
    */
   public void write(String fileName, SystemMD system) throws IOException {

      this.system = system;
      this.segments = system.getSegmentALL();
      this.pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
      
      String title = system.getTitle();
      if(title != null){
         pw.println("* "+title);
      }else{
         pw.printf("* COORD FOR ");
         for(int i=0; i<segments.length; i++){
            pw.printf(segments[i].getName()+" ");
         }
         pw.println();
      }
      
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss z");
      pw.println("*  DATE: "+sdf.format(date)+" CREATED BY "+System.getProperty("user.name"));
      pw.println("*");
      
      if(! ioextend){
         pw.printf("%5d\n",system.getNumOfAtom());
      }else{
         pw.printf("%10d  EXT\n",system.getNumOfAtom());
      }

      this.writeBox();
      this.writeAtoms();

      pw.close();
   }
   
   private void writeBox(){
      Box box = system.getBox();
      if(box != null){
         // TODO Do we need box in crd format?
         /*
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
               */
      }
   }
   
   private void writeAtoms(){
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
               //pw.printf("%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4d%10.5f\n", 
               pw.printf(format, 
                     atomID,resID,resName,name,
                     xyz[0]*Constants.Bohr2Angs,
                     xyz[1]*Constants.Bohr2Angs,
                     xyz[2]*Constants.Bohr2Angs,segName,nres+1,0.0);
            }
         }
      }
   }
   
   private void printAtoms(){
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
               System.out.printf("%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4d%10.5f\n", 
                     atomID,resID,resName,name,
                     xyz[0]*Constants.Bohr2Angs,
                     xyz[1]*Constants.Bohr2Angs,
                     xyz[2]*Constants.Bohr2Angs,segName,nres+1,0.0);
            }
         }
      }

   }

}
