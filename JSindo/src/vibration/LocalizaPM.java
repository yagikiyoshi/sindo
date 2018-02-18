package vibration;

/**
 * (Package private) Driver class for Pipek&amp;Mezey localization
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class LocalizaPM extends Localiza {

   private double[][] Mkla;
   
   /**
    * This class is package private.
    */
   LocalizaPM(){
      
   }
   public void setup(CoordinateData coordData){
      
      super.setup(coordData);
      
      int kl=0;
      Mkla = new double[Nfree*(Nfree+1)/2][Nat];
      for(int k=0; k<Nfree; k++){
         for(int l=0; l<=k; l++){
            for(int a=0; a<Nat; a++){
               Mkla[kl][a] = 0.0d;
               for(int x=0; x<3; x++){
                  Mkla[kl][a] = Mkla[kl][a] + CL[k][a*3+x]*CL[l][a*3+x];
               }
            }
            kl++;
         }
      }
   }
   
   public double getK(int i, int j, int k, int l) {
      double KK=0.0d;
      int ij = i*(i+1)/2+j;
      int kl = k*(k+1)/2+l;
      for(int a=0; a<Nat; a++){
         KK = KK + Mkla[ij][a]*Mkla[kl][a];
      }
      return KK;
   }

   @Override
   public double getZeta(int[] modes) {
      double zeta=0.0d;
      int ii;
      for(int i=0; i<modes.length; i++){
         ii=modes[i]*(modes[i]+1)/2 +modes[i];
         for(int a=0; a<Nat; a++){
            zeta = zeta + Mkla[ii][a]*Mkla[ii][a];
         }
      }
      return zeta;
   }

   @Override
   public void update(int i, int j) {
      for(int k=0; k<Nfree; k++){
         int ik = 0;
         if(i>k){
            ik = i*(i+1)/2 + k;            
         }else{
            ik = k*(k+1)/2 + i;            
         }
         int jk = 0;
         if(j>k){
            jk = j*(j+1)/2 + k;
         }else{
            jk = k*(k+1)/2 + j;
         }

         for(int a=0; a<Nat; a++){
            Mkla[ik][a] = 0.0d;
            Mkla[jk][a] = 0.0d;
            for(int x=0; x<3; x++){
               Mkla[ik][a] = Mkla[ik][a] + CL[i][a*3+x]*CL[k][a*3+x];
               Mkla[jk][a] = Mkla[jk][a] + CL[j][a*3+x]*CL[k][a*3+x];
            }
         }
      }
   }

}
