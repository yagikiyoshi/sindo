
import vibration.*;
import sindo.*;

public class VQDPTSpectrum {

   static public void main(String[] args){
      
      if(args.length<1){
         System.out.println("USAGE: java VQDPTSpectrum width [a b r]");
         System.out.println("          width ... width of Lorentz func (cm-1).");
         System.out.println("          a b r ... plot from a to b every r (cm-1). Defalt 500 4000 1");
         System.exit(-1);
      }
      
      double width = Double.parseDouble(args[0]);
      
      double start = 500.0;
      if(args.length >=3){
         start = Double.parseDouble(args[1]);
      }
      double end = 4000.0;
      if(args.length >= 4){
         end = Double.parseDouble(args[2]);
      }
      double dd = 1.0;
      if(args.length >= 5){
         dd = Double.parseDouble(args[3]);
      }
      
      VQDPTreader vqdpt = new VQDPTreader();
      vqdpt.setInfrared();
      VQDPTdata vqdpt_data = vqdpt.read();
      
      SindoUtil sutil = new SindoUtil();
      BandProfile profile = sutil.getIRspectrum(vqdpt_data, width);

      double ss = start;
      double ir = 0.0;
      while(ss <= end){
         ir = profile.getIntensity(ss);
         System.out.printf("%12.2f %12.4f \n",ss,ir);
         ss += dd;
      }
      

   }
}
