package md;

import java.util.ArrayList;

/**
 * Stores the information of the whole system
 * @author Kiyoshi Yagi
 * @version 1.1
 * @since Sindo 3.6
 */
public class SystemMD implements AtomList, ResidueList, SegmentList {

   private String title;
   private ArrayList<Segment> segArray;
   private boolean addSegment;
   private Segment[] segments;
   private Residue[] residues;
   private AtomMD[] atoms;
   private Trajectory traj;
   private Box box;
   private Potential potential;
   
   /**
    * Construct
    */
   public SystemMD(){
      segArray = new ArrayList<Segment>();
      addSegment = true;
      segments = null;
      residues = null;
      atoms = null;
      traj = null;
      box = null;
      potential = null;
   }
   
   /**
    * Returns the title of the system
    * @return Title of the system
    */
   public String getTitle() {
      return title;
   }

   /**
    * Sets a title of the system
    * @param title The title of the system
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * Call this method to start system build, which makes all residues and 
    * segments editable. 
    */
   public void openSegmentList(){
      
      if(! addSegment){
         addSegment = true;
         
         segArray = new ArrayList<Segment>();
         for(int i=0; i<segments.length; i++){            
            segArray.add(segments[i]);
            segments[i].openResidueList();
         }
         
         segments = null;
         residues = null;
         atoms    = null;
         
      }      
   }
   
   /**
    * Call this method when the system build is done.
    */
   public void closeSegmentList(){
      if(addSegment){
         addSegment = false;
         segments = segArray.toArray(new Segment[0]);
         segArray = null;
         
         int nres=0;
         int natm=0;
         for(int i1=0; i1<segments.length; i1++){
            Segment segi1 = segments[i1];
            segi1.closeResidueList();
            nres = nres + segi1.getNumOfResidue();
            natm = natm + segi1.getNumOfAtom();
         }

         residues = new Residue[nres];
         atoms = new AtomMD[natm];
         
         nres=0;
         natm=0;
         for(int i1=0; i1<segments.length; i1++){
            Residue[] resi1 = segments[i1].getResidueALL();
            System.arraycopy(resi1, 0, residues, nres, resi1.length);
            nres=nres + resi1.length;
            
            AtomMD[] atmi1 = segments[i1].getAtomALL();
            System.arraycopy(atmi1, 0, atoms, natm, atmi1.length);
            natm=natm + atmi1.length;
         }         
      }
   }

   /**
    * Adds a segment to the system
    * @param segment the segment to be added
    * @throws BuilderException thrown when the system is closed
    */
   public void addSegmentList(Segment segment) throws BuilderException{
      if(addSegment){
         segArray.add(segment);
      }else{
         throw new BuilderException("Attempted to add a residue to an uneditable system");
      }
   }
   
   /**
    * Adds an array of segments to the list
    * @param segments the array of segments to be added
     * @throws BuilderException thrown when the system is closed
    */
   public void addSegmentList(Segment[] segments) throws BuilderException{
      if(addSegment){
         for(int i=0; i<segments.length; i++){
            segArray.add(segments[i]);
         }
      }else{
         throw new BuilderException("Attempted to add a residue to an uneditable system");
      }
   }

   /**
    * Inserts a segment to the system
    * @param index the index
    * @param segment the segment to be inserted
    * @throws BuilderException thrown when the system is closed
    */
   public void insertSegmentList(int index, Segment segment) throws BuilderException{
      if(addSegment){
         segArray.add(segment);
      }else{
         throw new BuilderException("Attempted to insert a residue to an uneditable system");
      }
   }
   
   /**
    * Inserts an array of segments to the list
    * @param index the index
    * @param segments the array of segments to be inserted
     * @throws BuilderException thrown when the system is closed
    */
   public void insertSegmentList(int index, Segment[] segments) throws BuilderException{
      if(addSegment){
         for(int i=0; i<segments.length; i++){
            segArray.add(index+i, segments[i]);
         }
      }else{
         throw new BuilderException("Attempted to insert a residue to an uneditable system");
      }
   }
   /**
    * Removes a segment from the list, and shifts any subsequent element to the left
    * (subtracts one from their indices).
    * @param index The index of segment to be removed
    * @throws BuilderException thrown when the segment list is closed
    */
   public void removeSegmentList(int index) throws BuilderException{
      if(addSegment){
         segArray.remove(index);
      }else{
         throw new BuilderException("Attemped to remove a segment from from an uneditable system");
      }
   }
   /**
    * Returns a segment from the list
    * @param index The index of segment
    * @return The segment 
    * @throws BuilderException thrown when the segment list is closed
    */
   public Segment getSegmentList(int index) throws BuilderException {
      if(addSegment){
         return segArray.get(index);
      }else{
         throw new BuilderException("Attemped to get a segment from from an uneditable system");
      }
   }
   /**
    * Returns a list of segments.
    * @return List of segment
    * @throws BuilderException thrown when the system is uneditable
    */
   public ArrayList<Segment> getSegmentList() throws BuilderException{
      if(addSegment){
         return segArray;
      }else{
         throw new BuilderException("Attemped to get a list of segment from from an uneditable system");
      }
   }
   
   /**
    * (Package private) Resets the list of segments
    */
   void initSegmentList(){
      segArray = new ArrayList<Segment>();
      addSegment = true;
      segments = null;
      residues = null;
      atoms = null;
   }
   
   public int getNumOfSegment(){
      return segments.length;
   }

   public Segment[] getSegmentALL(){
      return segments;
   }
   
   public Segment getSegment(int index){
      return segments[index];
   }
   
   /**
    * Returns a segment in which an atom belongs to.
    * @param atomindex Index of the atom
    * @return The segment
    */
   public Segment getSegmentByAtom(int atomindex){
      int ai = 0;
      int si = 0;
      for(int i=0; i<segments.length; i++){
         ai = ai + segments[i].getNumOfAtom();
         if(ai > atomindex){
            si = i;
            break;
         }
      }
      
      return segments[si];
   }

   public int getNumOfResidue(){
      return residues.length;
   }
   
   public Residue[] getResidueALL(){
      return residues;
   }
   
   public Residue getResidue(int index){
      return residues[index];
   }
   
   /**
    * Returns a residue in which an atom belongs to.
    * @param atomindex Index of the atom
    * @return The residue
    */
   public Residue getResidueByAtom(int atomindex){
      int ai = 0;
      int ri = 0;
      for(int i=0; i<residues.length; i++){
         ai = ai + residues[i].getNumOfAtom();
         if(ai > atomindex){
            ri = i;
            break;
         }
      }
      
      return residues[ri];
   }
   
   public int getNumOfAtom(){
      return atoms.length;
   }
   
   public AtomMD[] getAtomALL(){
      return atoms;
   }
   
   public AtomMD getAtom(int index){
      return atoms[index];
   }
   
   /**
    * Returns the periodic boundary box of the system
    * @return The periodic boundary box
    */
   public Box getBox(){
      return box;
   }
   /**
    * Sets the periodic boundary box of the system
    * @param box The periodic boundary box
    */
   public void setBox(Box box){
      this.box = box;
   }
   
   /**
    * Returns the potential of the system
    * @return The potential
    */
   public Potential getPotential() {
      return potential;
   }

   /**
    * Sets the potential of the system
    * @param potential The potential
    */
   public void setPotential(Potential potential) {
      this.potential = potential;
   }

   /**
    * Returns the trajectory of the system
    * @return The trajectory 
    */
   public Trajectory getTrajectory(){
      return traj;
   }

   /**
    * Generates and returns the trajectory of the system. The first frame of the trajectory is 
    * set from the atoms and box of the system. An empty one is returned if the atoms/box don't exist.
    * @return The trajectory
    */
   public Trajectory generateTrajectory(){
      this.traj = new Trajectory();
      if(atoms != null){
         double[][] xyz = new double[atoms.length][3];
         for(int n=0; n<atoms.length; n++){
            xyz[n] = atoms[n].getXYZCoordinates();
         }
         traj.addCoordinates(xyz);
      }
      if(box != null){
         traj.addBox(box);
      }
      
      return traj;
   }
   
   /**
    * Sets the trajectory of the system
    * @param traj The trajectory
    */
   public void setTrajectory(Trajectory traj){
      this.traj = traj;
   }
   
   /**
    * Sets the coordinates of the atoms and box to a specified frame
    * @param n the frame number
    */
   public void setFrame(int n){
      double[][] xyz = traj.getCoordinates(n);
      for(int i=0; i<xyz.length; i++){
         atoms[i].setXYZCoordinates(xyz[i]);
      }
      if(traj.isBoxes()){
         box = traj.getBox(n);
      }
   }

   /**
    * Sets the coordinates of the atoms and box to the last frame
    */
   public void setFrameLast(){
      this.setFrame(traj.getNumOfFrames()-1);
   }
   
   /**
    * Removes the trajectory of the system
    */
   public void removeTrajectory(){
      this.traj = null;
   }
}
