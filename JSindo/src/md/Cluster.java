package md;

public class Cluster {
   
   private SystemMD sys;
   private boolean termH = true;
   private int[][] bondPair = null;
   
   /**
    * Sets a system to be clustered
    * @param sys The system to be analyzed
    */
   public void setSystemMD(SystemMD sys){
      this.sys = sys;
   }
   
   /**
    * Sets whether or not to terminate with hydrogen atoms
    * @param opt terminate if true
    */
   public void setTermHydrogen(boolean opt){
      termH = opt;
   }
   
   /**
    * Sets the bond connectivity information, which is needed to 
    * find the bonds to terminate with hydrogen atoms.
    * @param bondPair Pairs of bonds
    */
   public void setBondPairs(int[][] bondPair){
      this.bondPair = bondPair;
      termH = true;
   }
   
   /**
    * Returns a cluster which is consisted of atoms given in the
    * input.
    * @param atomindex The index of atoms. Must be ordered in an increasing order.
    * @return The cluster
    */
   public SystemMD getCluster(int[] atomindex){
      
      SystemMD cluster = new SystemMD();
      cluster.setTitle("Cluster of "+sys.getTitle());
      
      Segment seg = null;
      Residue res = null;
      int nres = 1;
      MDUtil mdutil = new MDUtil();
      
      try{
         String segname = "";
         String resname = "";
         
         for(int i=0; i<atomindex.length; i++){
            Segment segi = sys.getSegmentByAtom(atomindex[i]);
            Residue resi = sys.getResidueByAtom(atomindex[i]);
            if(! segname.equals(segi.getName())){
               segname = segi.getName();               
               seg = new Segment();
               seg.setName(segname);
               cluster.addSegmentList(seg);
            }
            
            if(! resname.equals(resi.getName())){
               resname = resi.getName();
               res = new Residue();
               res.setName(resname);
               res.setID(nres);
               seg.addResidueList(res);
               nres++;
            }

            AtomMD atom = sys.getAtom(atomindex[i]).clone();
            res.addAtomList(atom);
            
            if(termH){
               int[] ipair = new int[10];
               int npair = 0;
               
               for(int n=0; n<bondPair.length; n++){
                  if(bondPair[n][0] == atomindex[i]){
                     ipair[npair] = bondPair[n][1];
                     npair++;
                  }else if(bondPair[n][1] == atomindex[i]){
                     ipair[npair] = bondPair[n][0];
                     npair++;
                  }
               }
               
               for(int n=0; n<npair; n++){
                  
                  boolean inCluster = false;
                  for(int j=0; j<atomindex.length; j++){
                     if(ipair[n] == atomindex[j]){
                        inCluster = true;
                        break;
                     }
                  }
                  
                  if(! inCluster){
                     AtomMD termAtom = sys.getAtom(ipair[n]).clone();
                     mdutil.termWithHydrogen(atom, termAtom);
                     res.addAtomList(termAtom);
                  }
                  
               }
            }
            
         }
      }catch(BuilderException e){
         e.printStackTrace();         
      }
      
      cluster.closeSegmentList();
      mdutil.reNumberAtom(1, cluster);
      
      return cluster;
   }
}
