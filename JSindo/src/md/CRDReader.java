package md;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import sys.Constants;
import sys.Utilities;

/**
 * Read the information from a CRD file
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class CRDReader extends FileReaderMD {

   private ANResolver anr;

   public CRDReader(){
      super();
      anr = new ANResolver();
   }
   /**
    * Constructs specifying the name of the file 
    * @param fileName the name of the file to be read
    */
   public CRDReader(String fileName){
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
    * Reads the segments from the specified CRD file
    * @return an array of segments
    * @throws IOException thrown when an IO error occurred
    */
   public Segment[] readSegments() throws IOException {
      
      system.initSegmentList();
      
      Segment current_seg = new Segment();
      current_seg.setName(null);
      
      ArrayList<Residue> residues=null;
      Residue current_res=null;
      
      String segName;
      int resID;

      String line = null;
      try{
         
         BufferedReader  br = new BufferedReader(new FileReader(fname));
         line = br.readLine();

         while(line.substring(0, 1).equals("*")){
            line = br.readLine();
         }
         line = br.readLine();

         while(line != null){
            String[] info = Utilities.splitWithSpaceString(line);
            
            AtomMD atom = new AtomMD();
            atom.setID(Integer.parseInt(info[0]));
            atom.setLabel(info[3]);
            int an = anr.getAtomicNumber(info[3]);
            if(an > 0) atom.setAtomicNum(an);
            
            double[] xyz = new double[3];
            xyz[0] = Double.parseDouble(info[4])/Constants.Bohr2Angs;
            xyz[1] = Double.parseDouble(info[5])/Constants.Bohr2Angs;
            xyz[2] = Double.parseDouble(info[6])/Constants.Bohr2Angs;
            atom.setXYZCoordinates(xyz);
            
            atom.setBeta(Double.parseDouble(info[9]));
            
            segName = info[7];
            if(! segName.equals(current_seg.getName())){
               current_seg = new Segment();
               current_seg.setName(segName);
               
               residues = current_seg.getResidueList();
               current_res = new Residue();
               current_res.setID(-1);
               
               system.addSegmentList(current_seg);
            }
            
            resID = Integer.parseInt(info[1]);
            
            if(resID == current_res.getID()){
               current_res.addAtomList(atom);
            }else{
               current_res = new Residue();
               current_res.setID(resID);
               current_res.setName(info[2]);
               current_res.addAtomList(atom);
               
               residues.add(current_res);
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
   
   /**
    * Reads the coordinates of the atoms. It is assumed to be called after the modeling stage, that is,
    * the number of atoms/residues/segments and their order must be consistent with the system that is 
    * already set to the Reader. Only the number of atoms is checked, but the name (or type) and the 
    * orders is unchecked. 
    * @return xyz coordinates of the atoms (bohr)
    * @throws IOException thrown when an IO error is detected.
    */
   public double[][] readCoordinates() throws IOException {
      
      int Natom = system.getNumOfAtom();
      AtomMD[] atoms = system.getAtomALL();
      double[][] xyz = new double [Natom][3];

      String line = null;
      BufferedReader  br = new BufferedReader(new FileReader(fname));
      line = br.readLine();

      while(line.substring(0, 1).equals("*")){
         line = br.readLine();
      }
      line = br.readLine();

      int nn=0;
      while(line != null){
         String[] info = Utilities.splitWithSpaceString(line);
         
         xyz[nn][0] = Double.parseDouble(info[4])/Constants.Bohr2Angs;
         xyz[nn][1] = Double.parseDouble(info[5])/Constants.Bohr2Angs;
         xyz[nn][2] = Double.parseDouble(info[6])/Constants.Bohr2Angs;
         atoms[nn].setXYZCoordinates(xyz[nn]);
         nn++;
                     
         line = br.readLine();
         
      }
      br.close();

      return xyz;
   }
   
   
   @Override
   protected SystemMD readAllChild() throws IOException {
      this.readSegments();
      return system;
   }

}
