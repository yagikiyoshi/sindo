package sindo;

public class TestVQDPTreader {

   /**
    * @param args
    */
   public static void main(String[] args) {
      VQDPTreader vread = new VQDPTreader();
      vread.setInfrared("test/sindo/vqdpt-IR_eigen_18m.data");
      VQDPTdata vqdpt_data = vread.read("test/sindo/vqdpt-w_eigen_18m.wfn");
      
      int ntarget = vqdpt_data.getNtarget();
      Conf[] targetConf = vqdpt_data.getTargetConf();
      for(int i=0; i<ntarget; i++){
         System.out.println(targetConf[i].print());
      }
      
      int[] options = vqdpt_data.getOptions();
      System.out.println("maxSum = "+options[0]);
      System.out.println("nCUP   = "+options[1]);
      System.out.println("pqSum  = "+options[2]);
      System.out.println("pSet   = "+options[3]);
      
      double[] pSpace = vqdpt_data.getPspaceOptions();
      System.out.println("nGen = "+pSpace[0]);
      System.out.println("P0   = "+pSpace[1]);
      System.out.println("P1   = "+pSpace[2]);
      System.out.println("P2   = "+pSpace[3]);
      System.out.println("P3   = "+pSpace[4]);
      
      int[] npCnf = vqdpt_data.getNpCnf();
      int Ngroup = npCnf.length;
      System.out.println("The number of group: "+Ngroup);
      for(int i=0; i<Ngroup; i++){
         System.out.print(npCnf[i]+" ");
         if(i%5 == 4) System.out.println();
      }
      System.out.println();
      
      double zpe = vqdpt_data.getZPE();
      
      for(int i=0; i<Ngroup; i++){
         VQDPTgroup vgroup = vqdpt_data.getGroup(i);
         int np = vgroup.getNp();
         if(np == 1){
            System.out.println("Group = "+i);
            System.out.println("P space "+vgroup.getpConf()[0].print());
            System.out.println("Energy = "+(vgroup.getEnergy()[0]-zpe));
         }else{
            System.out.println("Group = "+i);
            System.out.println("P space");
            Conf[] pConf = vgroup.getpConf();
            for(int j=0; j<np; j++){
               System.out.println(pConf[j].print());
            }
            
            double[] energy = vgroup.getEnergy();
            double[][] coeff = vgroup.getCIcoeff();
            double[] IRintty = vgroup.getIRintensity();
            for(int j=0; j<np; j++){
               if(IRintty[j] > 1.0){
                  System.out.println("State "+j);
                  System.out.println("Energy = "+(energy[j]-zpe));
                  System.out.println("IR intensity = "+IRintty[j]);
                  System.out.println("CI coeff");
                  for(int k=0; k<np; k++){
                     System.out.printf(" %12.6f",coeff[j][k]);
                     if(k%5 == 4) System.out.println();
                  }
                  if((np-1)%5 != 4) System.out.println();
                  
               }
            }
         }
         System.out.println();
      }
      
   }

}
