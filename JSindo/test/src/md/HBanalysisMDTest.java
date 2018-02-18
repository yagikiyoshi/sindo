package md;

import java.io.IOException;

/**
 * 
Acceptor [0]: O13
Acceptor [1]: O14
Acceptor [2]: O32
Acceptor [3]: OH1
Donor [0]: N1-H1
Donor [1]: OH1-HO1

Cluster[0]: 0 2 9 7 60 17 54 16 33 
Cluster[1]: 1 35 
Cluster[2]: 3 
Cluster[3]: 4 56 34 28 
Cluster[4]: 5 30 
Cluster[5]: 6 
Cluster[6]: 8 15 25 63 23 
Cluster[7]: 10 
Cluster[8]: 11 57 12 
Cluster[9]: 13 61 39 
Cluster[10]: 14 52 53 
Cluster[11]: 18 50 
Cluster[12]: 19 20 45 29 21 
Cluster[13]: 22 32 36 26 
Cluster[14]: 24 40 
Cluster[15]: 27 
Cluster[16]: 31 
Cluster[17]: 37 46 42 47 
Cluster[18]: 38 
Cluster[19]: 41 
Cluster[20]: 43 
Cluster[21]: 44 
Cluster[22]: 48 
Cluster[23]: 49 
Cluster[24]: 51 58 59 
Cluster[25]: 55 
Cluster[26]: 62 

Cluster size distribution for the upper lipids
1: 13
2: 4
3: 4
4: 3
5: 2
6: 0
7: 0
8: 0
9: 1

Cluster size distribution for the lower lipids
1: 14
2: 4
3: 5
4: 0
5: 1
6: 1
7: 0
8: 2
9: 0
 *
 */
public class HBanalysisMDTest {

   public static void main(String[] args) {
      
      HBanalysisMD hbAnal = new HBanalysisMD();
      hbAnal.setHBdistance(3.5);
      hbAnal.setHBangle(30.0);
      
      PDBReader pdbr = new PDBReader("test/md/smbilayer.pdb");
      try{
         pdbr.readAll();
      }catch(IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      SystemMD sys = pdbr.getSystemMD();
      Segment segall = sys.getSegment(0);
      Box box = sys.getBox();
      hbAnal.setBox(box);
      
      MDUtil util = new MDUtil();

      // Upper half segment
      Segment upper = new Segment();
      upper.setName("UP");
      
      int nSM = 64;
      try{
         for(int i=0; i<nSM; i++){
            Residue residue = segall.getResidue(i);
            upper.addResidueList(residue);
         }
      }catch(BuilderException e){
         e.printStackTrace();
         System.exit(-1);
      }
      upper.closeResidueList();
      //upper.reNumber();
      util.reNumberAtom(1, upper);
      util.reNumberResidue(1, upper);

      nSM = 64;
      AtomMD[] acc = new AtomMD[4*nSM];
      AtomMD[][] don = new AtomMD[2*nSM][2];
      
      int[] aType = new int[4*nSM];
      int[] dType = new int[2*nSM];
      
      int[] ii = new int[nSM];
      for(int i=0; i<ii.length; i++){
         ii[i] = i;
      }
      
      for(int i=0; i<nSM; i++){
         Residue resi = upper.getResidue(ii[i]);

         // O13,O14,OH1,O32
         acc[i*4]   = resi.getAtom(20);
         acc[i*4+1] = resi.getAtom(21);
         acc[i*4+2] = resi.getAtom(32);
         acc[i*4+3] = resi.getAtom(81);
         aType[i*4] =  i;
         aType[i*4+1] = i;
         aType[i*4+2] = i;
         aType[i*4+3] = i;
         
         // {N1,H1},{OH1,HO1}
         don[i*2][0] = resi.getAtom(29);
         don[i*2][1] = resi.getAtom(30);
         don[i*2+1][0] = resi.getAtom(81);
         don[i*2+1][1] = resi.getAtom(82);
         
         dType[i*2] = i;
         dType[i*2+1] = i;
      }

      for(int i=0; i<4; i++){
         System.out.println("Acceptor ["+i+"]: " + acc[i].getLabel());
      }
      for(int i=0; i<2; i++){
         System.out.println("Donor ["+i+"]: " + don[i][0].getLabel() +"-"+don[i][1].getLabel());         
      }
      
      hbAnal.setAcceptorAtoms(acc);
      hbAnal.setDonorAtoms(don);
      hbAnal.setAcceptorID(aType);
      hbAnal.setDonorID(dType);
      hbAnal.setNumOfID(nSM);
      /*
      Integer[][] byType = hbAnal.getConnectivity();
      for(int i=0; i<byType.length; i++){
         if(byType[i].length > 0){
            System.out.print("ResID["+i+"]: ");
            for(int j=0; j<byType[i].length; j++){
               System.out.print(byType[i][j]+" ");
            }
            System.out.println();            
         }
      }
      */
      Integer[][] cluster = hbAnal.clusterize();
      for(int i=0; i<cluster.length; i++){
         System.out.print("Cluster["+i+"]: ");
         for(int j=0; j<cluster[i].length; j++){
            System.out.print(cluster[i][j]+" ");
         }
         System.out.println();
      }
      int[] dist = new int[10];
      for(int i=0; i<cluster.length; i++){
         dist[cluster[i].length]++;
      }
      System.out.println("Cluster size distribution for the upper lipids");
      for(int i=1; i<dist.length; i++){
         System.out.println(i+": "+dist[i]);
      }
      
      // Lower half segment
      Segment lower = new Segment();
      lower.setName("LOW");
      
      try{
         for(int i=0; i<nSM; i++){
            lower.addResidueList(segall.getResidue(i+64));
         }
      }catch(BuilderException e){
         e.printStackTrace();
         System.exit(-1);
      }
      lower.closeResidueList();
      
      util.reNumberAtom(1, lower);
      util.reNumberResidue(1, lower);
      //lower.reNumber();

      for(int i=0; i<ii.length; i++){
         Residue resi = lower.getResidue(ii[i]);

         // O13,O14,OH1,O32
         acc[i*4]   = resi.getAtom(20);
         acc[i*4+1] = resi.getAtom(21);
         acc[i*4+2] = resi.getAtom(32);
         acc[i*4+3] = resi.getAtom(81);
         
         // {N1,H1},{OH1,HO1}
         don[i*2][0] = resi.getAtom(29);
         don[i*2][1] = resi.getAtom(30);
         don[i*2+1][0] = resi.getAtom(81);
         don[i*2+1][1] = resi.getAtom(82);
      }

      hbAnal.setAcceptorAtoms(acc);
      hbAnal.setAcceptorID(aType);
      hbAnal.setDonorAtoms(don);
      hbAnal.setDonorID(dType);
      hbAnal.setNumOfID(nSM);

      cluster = hbAnal.clusterize();
      dist = new int[10];
      for(int i=0; i<cluster.length; i++){
         dist[cluster[i].length]++;
      }
      System.out.println("Cluster size distribution for the lower lipids");
      for(int i=1; i<dist.length; i++){
         System.out.println(i+": "+dist[i]);
      }

   }
   
}
