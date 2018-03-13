package sindo;

import java.util.*;
import vibration.*;

public class SindoUtil {

   /**
    * Returns the IR spectrum of the VQDPT2 data. The FWHM is 10 cm-1 for each band. 
    * @param vqdpt_data the results of VQDPT2
    * @return IR spectrum
    */
   public BandProfile getIRspectrum(VQDPTdata vqdpt_data){
      return this.getIRspectrum(vqdpt_data, 10.0);
   }
   
   /**
    * Returns the IR spectrum of the VQDPT2 data. 
    * @param vqdpt_data the results of VQDPT2
    * @param gamma FWHM in cm-1
    * @return IR spectrum
    */
   public BandProfile getIRspectrum(VQDPTdata vqdpt_data, double gamma){

      // check whether the data includes IR intensities
      if(! vqdpt_data.isInfared()){
         System.out.println("IR intensity is not found in the VQDPT data!");
         return null;
      }
      
      ArrayList<Band> bands = new ArrayList<Band>();
      
      double zpe = vqdpt_data.getZPE();
      for(VQDPTgroup vgroup: vqdpt_data.getAllGroup()){
         ArrayList<VQDPTstate> states = vgroup.getVQDPTstate();
         
         for(VQDPTstate vs: states){
            Band b = new Band();
            b.setPosition(vs.getEnergy() - zpe);
            b.setIntensity(vs.getIRintensity());
            bands.add(b);

         }
      }

      BandProfile spectrum = new BandProfile(gamma);
      spectrum.setBand(bands);
      
      return spectrum;

   }
   
   /**
    * Returns VQDPT states
    * @param vqdpt_data Data of VQDPT
    * @return an array of VQDPT states
    */
   public VQDPTstate[] getIntenseStates(VQDPTdata vqdpt_data){
      // check whether the data includes IR intensities
      if(! vqdpt_data.isInfared()){
         System.out.println("IR intensity is not found in the VQDPT data!");
         return null;
      }
      
      ArrayList<VQDPTstate> states_all = new ArrayList<VQDPTstate>();
      for(VQDPTgroup vgroup: vqdpt_data.getAllGroup()){
         ArrayList<VQDPTstate> states = vgroup.getVQDPTstate();
         
         for(VQDPTstate vs: states){
            states_all.add(vs);
         }
      }
      
      Collections.sort(states_all, new VQDPTComparator(1,true));
      
      VQDPTstate[] vs_array = new VQDPTstate[states_all.size()];
      for(int n=0; n<states_all.size(); n++){
         vs_array[n] = states_all.get(n);
      }
      
      return vs_array;

   }
}
