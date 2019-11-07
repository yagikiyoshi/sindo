package makePES;

import java.io.*;
import java.util.Arrays;

import sys.Constants;
import sys.Utilities;

/**
 * Reads and provides the data of QFF.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class QFFData {
   
   private int Nfree, MR;
   private double[] ci;
   private double[] cii,ciii,ciiii;
   private double[] cij,ciij,cijj,ciijj,ciiij,cijjj;
   private double[] cijk,ciijk,cijjk,cijkk;
   private double[] cijkl;

   /**
    * Read QFF data from 001.hs file.
    * @throws FileNotFoundException when 001.hs is not found
    */
   public void readhs() throws FileNotFoundException {
      File hs = new File("001.hs");
      if(! hs.exists()){
         throw new FileNotFoundException("001.hs is not found.");
      }
      readhs(hs);
   }
   /**
    * Read QFF data in hs format from a given File
    * @param hs The hs file
    */
   public void readhs(File hs){
      
      double conv1 = Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu);
      double conv2 = conv1*conv1;
      double conv3 = conv2*conv1;
      double conv4 = conv2*conv2;

      try{
         BufferedReader br = new BufferedReader(new FileReader(hs));
         br.readLine();
         br.readLine();
         br.readLine();
         Nfree=0;
         while(br.readLine().indexOf("1MR")==-1){
            Nfree=Nfree+3;
         }
         //System.out.println(Nfree);

         // 1MR terms
         String line = br.readLine();
         ci = new double[Nfree];
         cii = new double[Nfree];
         ciii = new double[Nfree];
         ciiii = new double[Nfree];
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            ci[i] = Double.parseDouble(line.substring(5, line.length()).trim());
            ci[i] = ci[i]*conv1;
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            cii[i] = Double.parseDouble(line.substring(5, line.length()).trim());
            cii[i] = cii[i]*0.5*conv2;
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            ciii[i] = Double.parseDouble(line.substring(5, line.length()).trim());
            ciii[i] = ciii[i]/6.0*conv3;
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            ciiii[i] = Double.parseDouble(line.substring(5, line.length()).trim());
            ciiii[i] = ciiii[i]/24.0*conv4;
         }

         if(br.readLine() == null) {
            MR=1;
            br.close();
            return;
         }
         // 2MR
         int nn = Nfree*(Nfree-1)/2;
         cij = new double[nn];
         ciij = new double[nn];
         cijj = new double[nn];
         ciijj = new double[nn];
         ciiij = new double[nn];
         cijjj = new double[nn];

         line = br.readLine();
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            cij[n1] = Double.parseDouble(line.substring(9, line.length()).trim());
            cij[n1] = cij[n1]*conv2;
         }
         line = br.readLine();
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            ciijj[n1] = Double.parseDouble(line.substring(9, line.length()).trim());
            ciijj[n1] = ciijj[n1]/4.0*conv4;
         }
         line = br.readLine();
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            ciij[n1] = Double.parseDouble(line.substring(9, line.length()).trim());
            line = br.readLine();
            cijj[n1] = Double.parseDouble(line.substring(9, line.length()).trim());
            ciij[n1] = ciij[n1]/2.0*conv3;
            cijj[n1] = cijj[n1]/2.0*conv3;
         }
         line = br.readLine();
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            ciiij[n1] = Double.parseDouble(line.substring(9, line.length()).trim());
            line = br.readLine();
            cijjj[n1] = Double.parseDouble(line.substring(9, line.length()).trim());
            ciiij[n1] = ciiij[n1]/6.0*conv4;
            cijjj[n1] = cijjj[n1]/6.0*conv4;
         }
         
         if(br.readLine() == null) {
            MR=2;
            br.close();
            return;
         }
         // 3MR
         nn = Nfree*(Nfree-1)*(Nfree-2)/6;
         cijk = new double[nn];
         ciijk = new double[nn];
         cijjk = new double[nn];
         cijkk = new double[nn];
         line = br.readLine();
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            cijk[n1] = Double.parseDouble(line.substring(13, line.length()).trim());
            cijk[n1] = cijk[n1]*conv3;
         }
         line = br.readLine();
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            ciijk[n1] = Double.parseDouble(line.substring(13, line.length()).trim());
            line = br.readLine();
            cijjk[n1] = Double.parseDouble(line.substring(13, line.length()).trim());
            line = br.readLine();
            cijkk[n1] = Double.parseDouble(line.substring(13, line.length()).trim());
            ciijk[n1] = ciijk[n1]*0.5*conv4;
            cijjk[n1] = cijjk[n1]*0.5*conv4;
            cijkk[n1] = cijkk[n1]*0.5*conv4;
         }
         
         if(br.readLine() == null) {
            MR=3;
            br.close();
            return;
         }
         // 4MR
         nn = Nfree*(Nfree-1)*(Nfree-2)*(Nfree-3)/24;
         cijkl = new double[nn];
         for(int n1=0; n1<nn; n1++){
            line = br.readLine();
            cijkl[n1] = Double.parseDouble(line.substring(17, line.length()).trim());
            cijkl[n1] = cijkl[n1]*conv4;
         }
         MR=4;
         br.close();
         
      }catch(IOException e){
         System.out.println(e.getMessage());
      }
      
   }
   /**
    * Read QFF data from prop_no_1.mop file.
    * @throws FileNotFoundException when prop_no_1.mop is not found
    */
   public void readmop() throws FileNotFoundException{
      File mopFile = new File("prop_no_1.mop");
      if(! mopFile.exists()){
         throw new FileNotFoundException("prop_no_1.mop is not found.");
      }
      readmop(mopFile);
   
   }
   /**
    * Read QFF data in mop format from a given File
    * @param mopFile The mop file 
    */
   public void readmop(File mopFile){
      
      try{
         BufferedReader br = new BufferedReader(new FileReader(mopFile));
         
         String line = br.readLine();
         Nfree = Integer.parseInt(line.substring(27).trim());
         
         double[] sqomg = new double[Nfree];
         for(int i=0; i<Nfree; i++){
            sqomg[i] = Double.parseDouble(br.readLine());
            //System.out.println(sqomg[i]*Constants.Hartree2wvn);
            sqomg[i] = Math.sqrt(sqomg[i]);
         }
         br.readLine();
         
         while((line = br.readLine()) != null) {
            String[] ss = Utilities.splitWithSpaceString(line);
            
            if (ss.length == 2) {
               
               // ci
               if (ci == null) {
                  ci = new double[Nfree];
               }
               int mi = Integer.parseInt(ss[1])-1;
               ci[mi] = Double.parseDouble(ss[0])*sqomg[mi];
               
            } else if (ss.length == 3) {
               
               int mi = Integer.parseInt(ss[1])-1;
               int mj = Integer.parseInt(ss[2])-1;
               double sqomg2 = sqomg[mi]*sqomg[mj];
               
               if (mi == mj) {
                  // cii
                  if (cii == null) {
                     cii = new double[Nfree];
                  }
                  cii[mi] = Double.parseDouble(ss[0])*sqomg2;
                  
               } else {
                  // cij
                  if (cij == null) {
                     int nn = Nfree*(Nfree-1)/2;
                     cij = new double[nn];
                  }
                  if (mj > mi) {
                     int tmp = mi;
                     mi = mj;
                     mj = tmp;
                  }
                  
                  int n1 = mi*(mi-1)/2 + mj;
                  cij[n1] = Double.parseDouble(ss[0])*sqomg2;
               }

            } else if (ss.length == 4) {
               
               int mi = Integer.parseInt(ss[1])-1;
               int mj = Integer.parseInt(ss[2])-1;
               int mk = Integer.parseInt(ss[3])-1;
               double sqomg3 = sqomg[mi]*sqomg[mj]*sqomg[mk];
               
               int[] mm = {mi, mj, mk};
               Arrays.sort(mm);
               
               // mi >= mj >= mk
               mk = mm[0];
               mj = mm[1];
               mi = mm[2];
               
               if (mi == mj && mj == mk) {
                  // ciii
                  if (ciii == null) {
                     ciii = new double[Nfree];
                  }
                  ciii[mi] = Double.parseDouble(ss[0])*sqomg3;

               } else if (mi == mj) {
                  // ciij
                  if (ciij == null) {
                     int nn = Nfree*(Nfree-1)/2;
                     ciij = new double[nn];
                  }
                  int n1 = mi*(mi-1)/2 + mk;
                  ciij[n1] = Double.parseDouble(ss[0])*sqomg3;

               } else if (mj == mk) {
                  // cijj
                  if (cijj == null) {
                     int nn = Nfree*(Nfree-1)/2;
                     cijj = new double[nn];
                  }
                  int n1 = mi*(mi-1)/2 + mj;
                  cijj[n1] = Double.parseDouble(ss[0])*sqomg3;

               } else {
                  // cijk
                  if (cijk == null) {
                     int nn = Nfree*(Nfree-1)*(Nfree-2)/6;
                     cijk = new double[nn];
                  }
                  int n1 = mi*(mi-1)*(mi-2)/6 + mj*(mj-1)/2 + mk;
                  cijk[n1] = Double.parseDouble(line.substring(0, 29))*sqomg3;

               }
               
            } else if (ss.length == 5) {
               int mi = Integer.parseInt(ss[1])-1;
               int mj = Integer.parseInt(ss[2])-1;
               int mk = Integer.parseInt(ss[3])-1;
               int ml = Integer.parseInt(ss[4])-1;
               double sqomg4 = sqomg[mi]*sqomg[mj]*sqomg[mk]*sqomg[ml];
               
               int[] mm = {mi, mj, mk, ml};
               Arrays.sort(mm);
               
               // mi >= mj >= mk >= ml
               ml = mm[0];
               mk = mm[1];
               mj = mm[2];
               mi = mm[3];
               
               if (mi == mj && mj == mk && mk == ml) {
                  // ciiii
                  if (ciiii == null) {
                     ciiii = new double[Nfree];
                  }
                  ciiii[mi] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else if (mi == mj && mk == ml) {
                  // ciijj
                  if (ciijj == null) {
                     int nn = Nfree*(Nfree-1)/2;
                     ciijj = new double[nn];
                  }
                  int n1 = mi*(mi-1)/2 + mk;
                  ciijj[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else if (mi == mj && mi == mk) {
                  // ciiij
                  if (ciiij == null) {
                     int nn = Nfree*(Nfree-1)/2;
                     ciiij = new double[nn];
                  }
                  int n1 = mi*(mi-1)/2 + ml;
                  ciiij[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else if (mj == mk && mj == ml) {
                  // cijjj
                  if (cijjj == null) {
                     int nn = Nfree*(Nfree-1)/2;
                     cijjj = new double[nn];
                  }
                  int n1 = mi*(mi-1)/2 + mj;
                  cijjj[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else if (mi == mj) {
                  // ciijk
                  if (ciijk == null) {
                     int nn = Nfree*(Nfree-1)*(Nfree-2)/6;
                     ciijk = new double[nn];
                  }
                  int n1 = mi*(mi-1)*(mi-2)/6 + mk*(mk-1)/2 + ml;
                  ciijk[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else if (mj == mk) {
                  // cijjk
                  if (cijjk == null) {
                     int nn = Nfree*(Nfree-1)*(Nfree-2)/6;
                     cijjk = new double[nn];
                  }
                  int n1 = mi*(mi-1)*(mi-2)/6 + mj*(mj-1)/2 + ml;
                  cijjk[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else if (mk == ml) {
                  // cijkk
                  if (cijkk == null) {
                     int nn = Nfree*(Nfree-1)*(Nfree-2)/6;
                     cijkk = new double[nn];
                  }
                  int n1 = mi*(mi-1)*(mi-2)/6 + mj*(mj-1)/2 + ml;
                  cijkk[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               } else {
                  // cijkl
                  if (cijkl == null) {
                     int nn = Nfree*(Nfree-1)*(Nfree-2)*(Nfree-3)/24;
                     cijkl = new double[nn];
                  }
                  int n1 = mi*(mi-1)*(mi-2)*(mi-3)/24 + mj*(mj-1)*(mj-2)/6
                         + mk*(mk-1)/2 + ml;
                  cijkl[n1] = Double.parseDouble(ss[0])*sqomg4;
                  
               }

            }
         }
         
         br.close();
         
         if(cijkl != null) {
            MR = 4;
         } else if (cijk != null) {
            MR = 3;
         } else if (cij != null) {
            MR = 2;
         } else {
            MR = 1;
         }
         
         return;

      }catch(IOException e){
         System.out.println(e.getMessage());
      }
      
   }
   
   /**
    * Read QFF data in mop format from a given File (old version)
    * @param mopFile The mop file 
    */
   public void readmop_v1(File mopFile){
      try{
         BufferedReader br = new BufferedReader(new FileReader(mopFile));
         
         String line=null;
         line = br.readLine();
         Nfree = Integer.parseInt(line.substring(27).trim());
         
         double[] sqomg = new double[Nfree];
         for(int i=0; i<Nfree; i++){
            sqomg[i] = Double.parseDouble(br.readLine());
            //System.out.println(sqomg[i]*Constants.Hartree2wvn);
            sqomg[i] = Math.sqrt(sqomg[i]);
         }
         br.readLine();
         
         // 1MR term
         ci = new double[Nfree];
         cii = new double[Nfree];
         ciii = new double[Nfree];
         ciiii = new double[Nfree];
         for(int i=0; i<Nfree; i++){
            double si = sqomg[i];
            double sii = si*si;
            
            ci[i] = Double.parseDouble(br.readLine().substring(0, 29))*si;
            cii[i] = Double.parseDouble(br.readLine().substring(0, 29))*sii;
            ciii[i] = Double.parseDouble(br.readLine().substring(0, 29))*sii*si;
            ciiii[i] = Double.parseDouble(br.readLine().substring(0, 29))*sii*sii;
            //System.out.println(cii[i]);
         }
         
         line = br.readLine();    
         if(line == null) {
             MR=1;
             br.close();
             return;
          }

         // 2MR term
         int nn = Nfree*(Nfree-1)/2;
         cij = new double[nn];
         ciij = new double[nn];
         cijj = new double[nn];
         ciijj = new double[nn];
         ciiij = new double[nn];
         cijjj = new double[nn];

         int n1=0;
         double si = sqomg[1];
         double sii = si*si;
         double sj = sqomg[0];
         double sjj = sj*sj;
         double sij = si*sj;
         cij[n1] = Double.parseDouble(line.substring(0, 29))*sij;
         ciij[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij*si;
         ciiij[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij*sii;
         cijj[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij*sj;
         ciijj[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sjj*sii;
         cijjj[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sjj*sij;
         n1++;

         for(int i=2; i<Nfree; i++){
            si = sqomg[i];
            sii = si*si;
            for(int j=0; j<i; j++){
               sj = sqomg[j];
               sjj = sj*sj;
               sij = si*sj;
               
               cij[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij;
               ciij[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij*si;
               ciiij[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij*sii;
               cijj[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sij*sj;
               ciijj[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sjj*sii;
               cijjj[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sjj*sij;
               
               n1++;
               
            }
         }
         
         line = br.readLine();    
         if(line == null) {
             MR=2;
             br.close();
             return;
          }

         // 3MR term
         nn = Nfree*(Nfree-1)*(Nfree-2)/6;
         cijk = new double[nn];
         ciijk = new double[nn];
         cijjk = new double[nn];
         cijkk = new double[nn];
         
         n1=0;
         si = sqomg[2];
         sj = sqomg[1];
         double sk = sqomg[0];
         double sijk = si*sj*sk;
         cijk[n1] = Double.parseDouble(line.substring(0, 29))*sijk;
         ciijk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*si;
         cijjk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*sj;
         cijkk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*sk;
         n1++;
         
         for(int i=3; i<Nfree; i++){
            si = sqomg[i];
            for(int j=0; j<i; j++){
               sj = sqomg[j];
               for(int k=0; k<j; k++){
                  sk = sqomg[k];
                  sijk = si*sj*sk;
                  
                  cijk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk;
                  ciijk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*si;
                  cijjk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*sj;
                  cijkk[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*sk;
                  
                  n1++;
               }
            }
         }

         line = br.readLine();    
         if(line == null) {
             MR=3;
             br.close();
             return;
          }

         // 4MR term
         nn = Nfree*(Nfree-1)*(Nfree-2)*(Nfree-3)/24;
         cijkl = new double[nn];
         
         
         n1=0;
         si = sqomg[3];
         sij = sqomg[2]*si;
         sijk = sqomg[1]*sij;
         cijkl[n1] = Double.parseDouble(line.substring(0, 29))*sijk*sqomg[0];
         n1++;
         
         for(int i=4; i<Nfree; i++){
            si = sqomg[i];
            for(int j=0; j<i; j++){
               sij = sqomg[j]*si;
               for(int k=0; k<j; k++){
                  sijk = sqomg[k]*sij;
                  for(int l=0; l<k; l++){
                     
                     cijkl[n1] = Double.parseDouble(br.readLine().substring(0, 29))*sijk*sqomg[l];
                     
                     n1++;
                  }
               }
            }
         }
         
         MR=4;
         br.close();
         return;

      }catch(IOException e){
         System.out.println(e.getMessage());
      }

   }
   /**
    * Return the number of degree of freedom
    * @return Nfree
    */
   public int getNfree(){
      return Nfree;
   }
   /**
    * Return the mode representation
    * @return MR
    */
   public int getMR(){
      return MR;
   }
   /**
    * Return Gi in au.
    * @param i mode
    * @return gi
    */
   public double getCi(int i){
      return ci[i];
   }
   /**
    * Return Hii/2 in au.
    * @param i mode
    * @return hii/2
    */
   public double getCii(int i){
      return cii[i];
   }
   /**
    * Return Tiii/6 in au.
    * @param i mode
    * @return tiii/6
    */
   public double getCiii(int i){
      return ciii[i];
   }
   /**
    * Return Uiiii/24 in au.
    * @param i mode
    * @return uiiii/24
    */
   public double getCiiii(int i){
      return ciiii[i];
   }
   /**
    * Return Hij (i&gt;j) in au.
    * @param i mode i
    * @param j mode j
    * @return Hij
    */
   public double getCij(int i, int j){
      return cij[i*(i-1)/2+j];
   }
   /**
    * Return Tiij/2 (i&gt;j) in au
    * @param i mode i
    * @param j mode j
    * @return Tiij/2
    */
   public double getCiij(int i, int j){
      return ciij[i*(i-1)/2+j];
   }
   /**
    * Return Tijj/2 (i&gt;j) in au
    * @param i mode i
    * @param j mode j
    * @return Tijj/2
    */
   public double getCijj(int i, int j){
      return cijj[i*(i-1)/2+j];
   }
   /**
    * Return Uiijj/4 (i&gt;j) in au
    * @param i mode i
    * @param j mode j
    * @return uiijj/4
    */
   public double getCiijj(int i, int j){
      return ciijj[i*(i-1)/2+j];
   }
   /** Return Uiiij/6 (i&gt;j) in au.
    * @param i mode i
    * @param j mode j
    * @return uiiij/6
    */
   public double getCiiij(int i, int j){
      return ciiij[i*(i-1)/2+j];
   }
   /** Return Uijjj/6 (i&gt;j) in au.
    * @param i mode i
    * @param j mode j
    * @return uijjj/6
    */
   public double getCijjj(int i, int j){
      return cijjj[i*(i-1)/2+j];
   }
   /**
    * Return Tijk (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return tijk
    */
   public double getCijk(int i, int j, int k){
      return cijk[i*(i-1)*(i-2)/6+j*(j-1)/2+k];
   }
   /**
    * Return Uiijk/2 (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return uiijk/2
    */
   public double getCiijk(int i, int j, int k){
      return ciijk[i*(i-1)*(i-2)/6+j*(j-1)/2+k];
   }
   /**
    * Return Uijjk/2 (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return uijjk/2
    */
   public double getCijjk(int i, int j, int k){
      return cijjk[i*(i-1)*(i-2)/6+j*(j-1)/2+k];
   }
   /**
    * Return Uijkk/2 (i&gt;j&gt;k) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @return uijkk/2
    */
   public double getCijkk(int i, int j, int k){
      return cijkk[i*(i-1)*(i-2)/6+j*(j-1)/2+k];
   }
   /**
    * Return Uijkl (i&gt;j&gt;k&gt;l) in au.
    * @param i mode i
    * @param j mode j
    * @param k mode k
    * @param l mode l
    * @return uijkl
    */
   public double getCijkl(int i, int j, int k, int l){
      return cijkl[i*(i-1)*(i-2)*(i-3)/24+j*(j-1)*(j-2)/6+k*(k-1)/2+l];
   }

}
