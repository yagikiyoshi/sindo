package makePES;

import java.io.*;

public class QFFDataTest1 {

   public static void main(String[] args){
      QFFData data = new QFFData();
      /*
      try{
         data.readhs();
      }catch(FileNotFoundException e){
         e.printStackTrace();
      }
      */
      
      data.readmop(new File("test/makePES/prop_no_1.mop"));
      
      int Nfree = data.getNfree();
      int MR = data.getMR();

      MCStrength mcs = new MCStrength();
      mcs.appendQFF(data);
      for(int i=0; i<Nfree; i++){
         for(int j=0; j<i; j++){
            double aa = mcs.get2mrMCS(i, j);
            if(aa>1.0){
               System.out.printf("%4d %4d           %15.4f %n",i,j,aa);               
            }
         }
      }
      
      if(MR > 2){
          for(int i=0; i<Nfree; i++){
              for(int j=0; j<i; j++){
                 for(int k=0; k<j; k++){
                    double aa = mcs.get3mrMCS(i, j, k);
                    if(aa>1.0){
                       System.out.printf("%4d %4d %4d      %15.4f %n",i,j,k,aa);                  
                    }
                 }
              }
           }
      }
      
      if(MR > 3){
          for(int i=0; i<Nfree; i++){
             for(int j=0; j<i; j++){
                for(int k=0; k<j; k++){
                   for(int l=0; l<k; l++){
                      double aa = mcs.get4mrMCS(i, j, k, l);
                      if(aa>1.0){
                         System.out.printf("%4d %4d %4d %4d %15.4f  %n",i,j,k,l,aa);                     
                      }
                                        
                   }
               }
             }
          }
      }
      
   }
}
