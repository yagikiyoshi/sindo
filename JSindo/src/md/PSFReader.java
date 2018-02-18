package md;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import sys.Constants;
import sys.Utilities;

import ff.*;

/**
 * Read the information from a PSF file
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class PSFReader extends FileReaderMD {

   private ForceField ff;
   private int[][] pair;

   public PSFReader(){
      super();
      ff = null;
      pair = null;
   }

   /**
    * Constructs specifying the name of the file 
    * @param fileName the name of the file to be read
    */
   public PSFReader(String fileName){
      this();
      this.setFileName(fileName);
   }
   
   public void setFileName(String fileName){
      super.setFileName(fileName);
      pair = null;
   }
   
   /**
    * Appends a force field to generate the potential. 
    * @param ff Force field paramters
    */
   public void appendForceField(ForceField ff){
      this.ff = ff;
   }
   
   /**
    * Reads the segments from the specified CRD file
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
         
         BufferedReader  br = new BufferedReader(new FileReader(fname));
         line = br.readLine();
         while(line.indexOf("NATOM") == -1){
            line = br.readLine();
         }
         int natom = Integer.parseInt(Utilities.splitWithSpaceString(line)[0].trim());

         for(int n=0; n<natom; n++){
            line = br.readLine();
            String[] comp = Utilities.splitWithSpaceString(line);
            
            AtomMD atom_n = new AtomMD();
            atom_n.setID(Integer.parseInt(comp[0]));
            atom_n.setLabel(comp[4]);
            atom_n.setType(comp[5]);
            atom_n.setAtomicCharge(Double.parseDouble(comp[6]));
            atom_n.setMass(Double.parseDouble(comp[7])/Constants.Emu2Amu);
            
            segName = comp[1];
            if(! segName.equals(current_seg.getName())){
               
               current_seg = new Segment();
               current_seg.setName(segName);
               
               residues = current_seg.getResidueList();
               current_res = new Residue();
               current_res.setID(-1);

               system.addSegmentList(current_seg);
            }
            
            resID = Integer.parseInt(comp[2]);
            
            if(resID == current_res.getID()){
               current_res.addAtomList(atom_n);
               
            }else{
               current_res = new Residue();
               current_res.setID(resID);
               current_res.setName(comp[3]);
               current_res.addAtomList(atom_n);
               
               residues.add(current_res);
            }

         }
         
         br.close();
         
      }catch(BuilderException e){
         e.printStackTrace();
      }

      system.closeSegmentList();
      return system.getSegmentALL();
   }
   
   /**
    * Reads and returns the bond pairs in PSF file
    * @return int[n][2] n pairs of atom indices
    * @throws IOException Exception while reading PSF file
    */
   public int[][] getBondPairs() throws IOException {
      
      if(pair != null){
         return pair;
      }
      
      String line = null;
      
      BufferedReader  br = new BufferedReader(new FileReader(fname));
      line = br.readLine();
      while(line.indexOf("NBOND") == -1){
         line = br.readLine();
      }
      
      int nb = Integer.parseInt(Utilities.splitWithSpaceString(line)[0].trim());
      pair = new int[nb][2];

      nb = 0;
      while((line = br.readLine().trim()).length() != 0){
         int[] nn = Utilities.splitWithSpaceInt(line);
         for(int i=0; i<nn.length/2; i++){
            pair[nb][0] = nn[i*2]-1;
            pair[nb][1] = nn[i*2+1]-1;
            nb++;
         }
      }
      //for(int i=0; i<pair.length; i++){
      //   System.out.println("bond" + i + ":  ("+pair[i][0]+","+pair[i][1]+")");
      //}

      br.close();

      return pair;
      
   }
   
   /**
    * Generates a potential for the current system. 
    * @return the potential of the system. null if the force field is not set
    * @throws IOException thrown when an IO error occurred
    */
   public Potential genPotential() throws IOException{
      
      if(ff == null) {
         system.setPotential(null);
         return null;
      }
      
      AtomMD[] atoms = system.getAtomALL();
      for(int i=0; i<atoms.length; i++){
         atoms[i].setVdW(ff.getVdW(atoms[i].getType()));
      }

      PotCHARMM charmm = new PotCHARMM();

      if(pair == null){
         this.getBondPairs();
      }
      charmm.setupBonds(pair, atoms, ff);

      String line = null;
      
      BufferedReader  br = new BufferedReader(new FileReader(fname));
      line = br.readLine();
      while(line.indexOf("NTHETA") == -1){
         line = br.readLine();
      }
      
      int na = Integer.parseInt(Utilities.splitWithSpaceString(line)[0].trim());
      pair = new int[na][3];
      
      na = 0;
      while((line = br.readLine().trim()).length() != 0){
         int[] nn = Utilities.splitWithSpaceInt(line);
         for(int i=0; i<nn.length/3; i++){
            pair[na][0] = nn[i*3]-1;
            pair[na][1] = nn[i*3+1]-1;
            pair[na][2] = nn[i*3+2]-1;
            na++;
         }
      }
      //for(int i=0; i<pair.length; i++){
      //   System.out.println("angle" + i + ":  ("+pair[i][0]+","+pair[i][1]+","+pair[i][2]+")");
      //}

      charmm.setupAngleUB(pair, atoms, ff);
      
      while(line.indexOf("NPHI") == -1){
         line = br.readLine();
      }
      
      int nd = Integer.parseInt(Utilities.splitWithSpaceString(line)[0].trim());
      pair = new int[nd][4];

      nd = 0;
      while((line = br.readLine().trim()).length() != 0){
         int[] nn = Utilities.splitWithSpaceInt(line);
         for(int i=0; i<nn.length/4; i++){
            pair[nd][0] = nn[i*4]-1;
            pair[nd][1] = nn[i*4+1]-1;
            pair[nd][2] = nn[i*4+2]-1;
            pair[nd][3] = nn[i*4+3]-1;
            nd++;
         }
      }
      //for(int i=0; i<pair.length; i++){
      //   System.out.println("dihed" + i + ":  ("+pair[i][0]+","+pair[i][1]+","+pair[i][2]+","+pair[i][3]+")");
      //}

      charmm.setupDihedral(pair, atoms, ff);
      
      br.close();
      
      system.setPotential(charmm);
      return charmm;
      
   }
   
   @Override
   protected SystemMD readAllChild() throws IOException {
      this.readSegments();
      this.genPotential();
      return system;
   }

}
