package makePES;

import java.io.*;
import java.util.*;
import sys.*;

public class QFFUtil {

   private QFFData qffdata = null;
   
   public QFFData getQFFData() {
      return qffdata;
   }
   public void setQFFData(QFFData qffdata) {
      this.qffdata = qffdata;
   }
   
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
      
      if(qffdata == null) {
         qffdata = new QFFData();
      }
      
      double conv1 = Constants.Bohr2Angs*Math.sqrt(Constants.Emu2Amu);
      double conv2 = conv1*conv1;
      double conv3 = conv2*conv1;
      double conv4 = conv2*conv2;

      try{
         BufferedReader br = new BufferedReader(new FileReader(hs));
         br.readLine();
         br.readLine();
         br.readLine();
         int Nfree=0;
         while(br.readLine().indexOf("1MR")==-1){
            Nfree=Nfree+3;
         }
         qffdata.setNfree(Nfree);
         //System.out.println(Nfree);

         // 1MR terms
         String line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            double ci = Double.parseDouble(line.substring(5, line.length()).trim())*conv1;
            Integer[] m = {i};
            qffdata.putCoeff(m, ci);
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            double cii = Double.parseDouble(line.substring(5, line.length()).trim())*0.5*conv2;
            Integer[] m = {i,i};
            qffdata.putCoeff(m, cii);
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            double ciii = Double.parseDouble(line.substring(5, line.length()).trim())/6.0*conv3;
            Integer[] m = {i,i,i};
            qffdata.putCoeff(m, ciii);
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++){
            line = br.readLine();
            double ciiii = Double.parseDouble(line.substring(5, line.length()).trim())/24.0*conv4;
            Integer[] m = {i,i,i,i};
            qffdata.putCoeff(m, ciiii);
         }

         if(br.readLine() == null) {
            qffdata.setMR(1);
            br.close();
            return;
         }
         
         // 2MR
         line = br.readLine();
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               line = br.readLine();
               double cij = Double.parseDouble(line.substring(9, line.length()).trim())*conv2;
               Integer[] m = {i,j};
               qffdata.putCoeff(m, cij);
            }
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               line = br.readLine();
               double ciijj = Double.parseDouble(line.substring(9, line.length()).trim())/4.0*conv4;
               Integer[] m = {i,i,j,j};
               qffdata.putCoeff(m, ciijj);
            }
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               line = br.readLine();
               double ciij = Double.parseDouble(line.substring(9, line.length()).trim())/2.0*conv3;
               Integer[] m1 = {i,i,j};
               qffdata.putCoeff(m1, ciij);
               line = br.readLine();
               double cijj = Double.parseDouble(line.substring(9, line.length()).trim())/2.0*conv3;
               Integer[] m2 = {i,j,j};
               qffdata.putCoeff(m2, cijj);
            }
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               line = br.readLine();
               double ciiij = Double.parseDouble(line.substring(9, line.length()).trim())/6.0*conv4;
               Integer[] m1 = {i,i,i,j};
               qffdata.putCoeff(m1, ciiij);
               
               line = br.readLine();
               double cijjj = Double.parseDouble(line.substring(9, line.length()).trim())/6.0*conv4;
               Integer[] m2 = {i,j,j,j};
               qffdata.putCoeff(m2, cijjj);
            }
         }
         
         if(br.readLine() == null) {
            qffdata.setMR(2);
            br.close();
            return;
         }

         // 3MR
         line = br.readLine();
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               for(int k=0; k<j; k++) {
                  line = br.readLine();
                  double cijk = Double.parseDouble(line.substring(13, line.length()).trim())*conv3;
                  Integer[] m = {i,j,k};
                  qffdata.putCoeff(m, cijk);
               }
            }
         }
         line = br.readLine();
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               for(int k=0; k<j; k++) {
                  line = br.readLine();
                  double ciijk = Double.parseDouble(line.substring(13, line.length()).trim())*0.5*conv4;
                  Integer[] m1 = {i,i,j,k};
                  qffdata.putCoeff(m1, ciijk);
                  
                  line = br.readLine();
                  double cijjk = Double.parseDouble(line.substring(13, line.length()).trim())*0.5*conv4;
                  Integer[] m2 = {i,j,j,k};
                  qffdata.putCoeff(m2, cijjk);

                  line = br.readLine();
                  double cijkk = Double.parseDouble(line.substring(13, line.length()).trim())*0.5*conv4;
                  Integer[] m3 = {i,j,k,k};
                  qffdata.putCoeff(m3, cijkk);
               }
            }
         }
         
         if(br.readLine() == null) {
            qffdata.setMR(3);
            br.close();
            return;
         }

         // 4MR
         for(int i=0; i<Nfree; i++) {
            for(int j=0; j<i; j++) {
               for(int k=0; k<j; k++) {
                  for(int l=0; l<k; l++) {
                     line = br.readLine();
                     double cijkl = Double.parseDouble(line.substring(17, line.length()).trim())*conv4;
                     Integer[] m = {i,j,k,l};
                     
                     qffdata.putCoeff(m, cijkl);
                  }
               }
            }
         }
         qffdata.setMR(4);
         br.close();
         
      }catch(IOException e){
         System.out.println(e.getMessage());
      }
      
   }
   
   /**
    * Read QFF data from prop_no_1.mop file.
    * @throws FileNotFoundException when prop_no_1.mop is not found
    * @throws IOException when I/O error occurred while reading the file
    * @return Omega of Mopfile
    */
   public double[] readmop() throws FileNotFoundException, IOException{
      File mopFile = new File("prop_no_1.mop");
      return readmop(mopFile);
   
   }
   /**
    * Read QFF data in mop format from a given File
    * @param mopFile The mop file 
    * @throws FileNotFoundException when mopFile is not found
    * @throws IOException when I/O error occurred while reading the file
    * @return Omega of Mopfile
    */
   public double[] readmop(File mopFile) throws FileNotFoundException, IOException{
      
      if(qffdata == null) {
         qffdata = new QFFData();
      }
      
      double[] omega = null;
      BufferedReader br = new BufferedReader(new FileReader(mopFile));
      
      String line = br.readLine();
      int Nfree = Integer.parseInt(line.substring(27).trim());
      qffdata.setNfree(Nfree);
      
      omega = new double[Nfree];
      double[] sqomg = new double[Nfree];
      for(int i=0; i<Nfree; i++){
         omega[i] = Double.parseDouble(br.readLine());
         //System.out.println(omega[i]*Constants.Hartree2wvn);
         sqomg[i] = Math.sqrt(omega[i]);
      }
      line = br.readLine();
      qffdata.setTitle(line.substring(17).trim());
      
      int MR = 1;
      while((line = br.readLine()) != null) {
         String[] ss = Utilities.splitWithSpaceString(line);
         
         if (ss.length == 2) {
            
            // ci
            int mi = Integer.parseInt(ss[1])-1;
            double ci = Double.parseDouble(ss[0])*sqomg[mi];
            Integer[] m = {mi};
            qffdata.putCoeff(m, ci);
            
         } else if (ss.length == 3) {
            
            // cii, cij
            int mi = Integer.parseInt(ss[1])-1;
            int mj = Integer.parseInt(ss[2])-1;
            double sqomg2 = sqomg[mi]*sqomg[mj];
            double cij = Double.parseDouble(ss[0])*sqomg2;
            Integer[] m = {mi, mj};
            qffdata.putCoeff(m, cij);
            
            if(MR == 1 && mi != mj) {
               MR=2;
            }
            
         } else if (ss.length == 4) {
            
            int mi = Integer.parseInt(ss[1])-1;
            int mj = Integer.parseInt(ss[2])-1;
            int mk = Integer.parseInt(ss[3])-1;
            double sqomg3 = sqomg[mi]*sqomg[mj]*sqomg[mk];
            double cijk = Double.parseDouble(ss[0])*sqomg3;

            Integer[] mm = {mi, mj, mk};
            qffdata.putCoeff(mm, cijk);
            
            if(MR == 2 && mi != mj && mj != mk) {
               MR=3;
            }
            
         } else if (ss.length == 5) {
            int mi = Integer.parseInt(ss[1])-1;
            int mj = Integer.parseInt(ss[2])-1;
            int mk = Integer.parseInt(ss[3])-1;
            int ml = Integer.parseInt(ss[4])-1;
            double sqomg4 = sqomg[mi]*sqomg[mj]*sqomg[mk]*sqomg[ml];
            double cijkl = Double.parseDouble(ss[0])*sqomg4;

            Integer[] mm = {mi, mj, mk, ml};
            qffdata.putCoeff(mm, cijkl);
            
            if(MR == 3 && mi != mj && mj != mk && mk != ml) {
               MR=4;
            }
            
         }
      }
      
      br.close();
      qffdata.setMR(MR);

      return omega;

   }
   
   /**
    * Read QFF data in mop format from a given File (old version)
    * @param mopFile The mop file 

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
   */

   public void writeMop(String fname, double[] omega) throws IOException {
      
      int Nfree = qffdata.getNfree();
      if(Nfree != omega.length) {
         System.out.println("Error in writeMop.");
         System.out.println("Size of omega and qffdata don't match.");
         System.out.println("   omega.length  = "+omega.length);
         System.out.println("   qffdata.Nfree = "+Nfree);
         return;
      }
      
      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fname)));
      pw.println("SCALING FREQUENCIES N_FRQS="+Nfree);

      double[] sqfreq = new double[Nfree];
      for(int i=0; i<Nfree; i++){
         pw.printf("%28.22e",omega[i]);
         pw.println();
         sqfreq[i] = Math.sqrt(omega[i]);
      }
      pw.println("DALTON_FOR_MIDAS "+qffdata.getTitle());
      
      for(int i=0; i<Nfree; i++) {
         double si = sqfreq[i];
         double sii = si*si;

         int ii=i+1;
         
         Integer[] mi = {i};
         Double ci = qffdata.getCoeff(mi);
         if (ci != null) {
            pw.printf("%29.22e%5d%n",ci/si,ii);            
         }
         Integer[] mii = {i,i};
         Double cii = qffdata.getCoeff(mii);
         if (cii != null) {
            pw.printf("%29.22e%5d%<5d%n",cii/sii,ii);
         }
         Integer[] miii = {i,i,i};
         Double ciii = qffdata.getCoeff(miii);
         if (ciii != null) {
            pw.printf("%29.22e%5d%<5d%<5d%n",ciii/sii/si,ii);
         }
         Integer[] miiii = {i,i,i,i};
         Double ciiii = qffdata.getCoeff(miiii);
         if (ciiii != null) {
            pw.printf("%29.22e%5d%<5d%<5d%<5d%n",ciiii/sii/sii,ii);
         }
      }

      if(qffdata.getMR() > 1) {
         for(int i=0; i<Nfree; i++) {
            double si = sqfreq[i];
            double sii = si*si;
            int ii=i+1;
            for(int j=0; j<i; j++) {
               double sj = sqfreq[j];
               double sjj = sj*sj;
               double sij = si*sj;
               int jj=j+1;
               
               Integer[] mij = {i,j};
               Double cij = qffdata.getCoeff(mij);
               if(cij != null) {
                  pw.printf("%29.22e%5d%5d%n",cij/sij,jj,ii);
               }
               Integer[] miij = {i,i,j};
               Double ciij = qffdata.getCoeff(miij);
               if(ciij != null) {
                  pw.printf("%29.22e%5d%5d%<5d%n",ciij/sii/sj,jj,ii);
               }
               Integer[] miiij = {i,i,i,j};
               Double ciiij = qffdata.getCoeff(miiij);
               if(ciiij != null) {
                  pw.printf("%29.22e%5d%5d%<5d%<5d%n",ciiij/sii/sij,jj,ii);
               }
               Integer[] mijj = {i,j,j};
               Double cijj = qffdata.getCoeff(mijj);
               if(cijj != null) {
                  pw.printf("%29.22e%5d%<5d%5d%n",cijj/si/sjj,jj,ii);
               }
               Integer[] miijj = {i,i,j,j};
               Double ciijj = qffdata.getCoeff(miijj);
               if(ciijj != null) {
                  pw.printf("%29.22e%5d%<5d%5d%<5d%n",ciijj/sii/sjj,jj,ii);
               }
               Integer[] mijjj = {i,j,j,j};
               Double cijjj = qffdata.getCoeff(mijjj);
               if(cijjj != null) {
                  pw.printf("%29.22e%5d%<5d%<5d%5d%n",cijjj/sij/sjj,jj,ii);
               }

            }
         }
      }
      
      if(qffdata.getMR() > 2) {
         for(int i=0; i<Nfree; i++) {
            double si = sqfreq[i];
            int ii=i+1;
            for(int j=0; j<i; j++) {
               double sj = sqfreq[j];
               double sij = si*sj;
               int jj=j+1;
               for(int k=0; k<j; k++) {
                  double sk = sqfreq[k];
                  double sijk = sij*sk;
                  int kk=k+1;
                  
                  Integer[] mijk = {i,j,k};
                  Double cijk = qffdata.getCoeff(mijk);
                  if(cijk != null) {
                     pw.printf("%29.22e%5d%5d%5d%n",cijk/sijk,kk,jj,ii);
                  }
                  Integer[] miijk = {i,i,j,k};
                  Double ciijk = qffdata.getCoeff(miijk);
                  if(ciijk != null) {
                     pw.printf("%29.22e%5d%5d%5d%<5d%n",ciijk/si/sijk,kk,jj,ii);
                  }
                  Integer[] mijjk = {i,j,j,k};
                  Double cijjk = qffdata.getCoeff(mijjk);
                  if(cijjk != null) {
                     pw.printf("%29.22e%5d%5d%<5d%5d%n",cijjk/sijk/sj,kk,jj,ii);
                  }
                  Integer[] mijkk = {i,j,k,k};
                  Double cijkk = qffdata.getCoeff(mijkk);
                  if(cijkk != null) {
                     pw.printf("%29.22e%5d%<5d%5d%5d%n",cijkk/sijk/sk,kk,jj,ii);
                  }
               }
            }
         }
      }

      if(qffdata.getMR() > 3) {
         for(int i=0; i<Nfree; i++) {
            int ii=i+1;
            for(int j=0; j<i; j++) {
               int jj=j+1;
               for(int k=0; k<j; k++) {
                  int kk=k+1;
                  for(int l=0; l<k; l++) {
                     int ll=l+1;
                     double sijkl = sqfreq[i]*sqfreq[j]*sqfreq[k]*sqfreq[l];
                     Integer[] mijkl = {i,j,k,l};
                     Double cijkl = qffdata.getCoeff(mijkl);
                     pw.printf("%29.22e%5d%5d%5d%5d%n",cijkl/sijkl,ll,kk,jj,ii);

                  }
               }
            }
         }
      }
      pw.close();

   }
   
   /**
    * Merge an input qffdata into current one. Note that current data 
    * are overwritten by the input coefficients.
    * @param qffdata1 input data
    */
   public void merge(QFFData qffdata1) {
      this.merge(qffdata1, true);
   }
   /**
    * Merge an input qffdata into current one
    * @param qffdata1 input data
    * @param overwrite if true, the coefficients are overwritten
    */
   public void merge(QFFData qffdata1, boolean overwrite) {
      HashMap<String, Double> coeff1 = qffdata1.getCoeff();
      HashMap<String, Double> coeff2 = this.qffdata.getCoeff();
      
      if(qffdata1.getMR() > this.qffdata.getMR()) {
         this.qffdata.setMR(qffdata1.getMR());
      }
      
      if(overwrite) {
         for(String key : coeff1.keySet()) {
            Double cc = coeff1.get(key);
            coeff2.put(key, cc);
         }
      } else {
         for(String key : coeff1.keySet()) {
            if(coeff2.containsKey(key)) continue;
            
            Double cc = coeff1.get(key);
            coeff2.put(key, cc);
         }
      }
      
   }
}
