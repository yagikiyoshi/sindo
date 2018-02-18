package molecule;

public class ElectronicDataTest1 {
   
   public static void main(String[] args){
      
      ElectronicData edata = new ElectronicData();
      // Energy
      double ene = -76.42062707601406;
      edata.setEnergy(ene);
      // Gradient
      double[] gradient = {8.17663642E-16, -8.64701208E-16, 2.45615926E-5, 4.85494867E-17, 3.19496857E-5, -1.22807963E-5, -8.66213129E-16, -3.19496857E-5, -1.22807963E-5};
      edata.setGradient(gradient);
      
      // This operation overwrites the gradient in edata
      gradient[2] = 0.0d;
      
      double[] gg = edata.getGradient();
      for(int i=0; i<gg.length; i++){
         System.out.printf("%15.6e ", gg[i]);         
      }
      System.out.println();
      
      // null if the value is not set.
      double[] hessian = edata.getHessian();
      if(hessian == null){
         System.out.println("Hessian is not set!");
      }
      
      // NaN if the value is not set.
      double charge = edata.getCharge();
      if(Double.isNaN(charge)){
         System.out.println("charge is not set!");
      }else{
         System.out.println(charge);
      }
   }

}
