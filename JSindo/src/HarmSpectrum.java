
import java.io.*;
import molecule.*;
import vibration.*;

public class HarmSpectrum {

   static public void main(String[] args){

      if(args.length<2){
         System.out.println("USAGE: java HarmSpectrum name width [a b r]");
         System.out.println("          name  ... name of the minfo file.");
         System.out.println("          width ... width of Lorentz func (cm-1).");
         System.out.println("          a b r ... plot from a to b every r (cm-1). Defalt 500 4000 1");
         System.exit(-1);
      }

      double width = Double.parseDouble(args[1]);
      
      double start = 500.0;
      if(args.length >=3){
         start = Double.parseDouble(args[2]);
      }
      double end = 4000.0;
      if(args.length >= 4){
         end = Double.parseDouble(args[3]);
      }
      double dd = 1.0;
      if(args.length >= 5){
         dd = Double.parseDouble(args[4]);
      }
      
      MInfoIO minfo = new MInfoIO();
      Molecule molecule = null;
      try {
         molecule = minfo.loadMOL(args[0]);
      } catch (FileNotFoundException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      } catch (IOException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

      VibUtil vutil = new VibUtil();
      vutil.appendMolecule(molecule);
      
      int nd = molecule.getNumOfVibrationalData();
      if(nd > 1){
         //VibUtil vutil = new VibUtil(molecule);
         vutil.combineAllVibData();
      }

      double[] omega = molecule.getVibrationalData().getOmegaV();
      double[] intensity = vutil.calcIRintensity();
            
      BandProfile profile = new BandProfile(width);
      profile.setBand(omega, intensity);

      double ss = start;
      double ir = 0.0;
      while(ss <= end){
         ir = profile.getIntensity(ss);
         System.out.printf("%12.2f %12.4f \n",ss,ir);
         ss += dd;
      }
      
   }
}
