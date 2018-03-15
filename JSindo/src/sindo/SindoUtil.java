package sindo;

import java.util.*;
import vibration.*;

public class SindoUtil {

   /**
    * Returns the infrared absorption spectrum for a given array of vibrational states. 
    * Note that the IR intensity need to be set to each state.
    * @param stateArray Vibrational states. 
    * @param zpe the zero-point energy (cm-1)
    * @param gamma FWHM of each band (cm-1)
    * @return IR spectrum
    */
   public BandProfile getIRspectrum(VibState[] stateArray, double zpe, double gamma) {
      
      ArrayList<Band> bands = new ArrayList<Band>();
      for(VibState vs: stateArray) {
         Band b = new Band();
         b.setPosition(vs.getEnergy() - zpe);
         b.setIntensity(vs.getIRintensity());
         bands.add(b);
      }
   
      BandProfile spectrum = new BandProfile(gamma);
      spectrum.setBand(bands);
      
      return spectrum;
   }
   
   /**
    * Returns the IR spectrum of VQDPT2 data. 
    * @param vqdpt_data the results of VQDPT2
    * @param gamma FWHM of each band (cm-1)
    * @return IR spectrum
    */
   public BandProfile getIRspectrum(VQDPTdata vqdpt_data, double gamma){

      // check whether the data includes IR intensities
      if(! vqdpt_data.isInfared()){
         System.out.println("IR intensity is not found in the VQDPT data!");
         return null;
      }
      
      double zpe = vqdpt_data.getZPE();
      ArrayList<VibState> stateList = vqdpt_data.getAllStates();
      VibState[] stateArray = stateList.toArray(new VibState[0]);
      return this.getIRspectrum(stateArray, zpe, gamma);
      
   }

   /**
    * Returns VQDPT states
    * @param vqdpt_data Data of VQDPT
    * @return an array of VQDPT states
    */
   public VibState[] getIntenseStates(VQDPTdata vqdpt_data){
      // check whether the data includes IR intensities
      if(! vqdpt_data.isInfared()){
         System.out.println("IR intensity is not found in the VQDPT data!");
         return null;
      }
      
      ArrayList<VibState> states_all = new ArrayList<VibState>();
      for(VQDPTgroup vgroup: vqdpt_data.getAllGroup()){
         ArrayList<VibState> states = vgroup.getVibState();
         
         for(VibState vs: states){
            states_all.add(vs);
         }
      }
      
      Collections.sort(states_all, new VibComparator(1,true));
      
      VibState[] vs_array = new VibState[states_all.size()];
      for(int n=0; n<states_all.size(); n++){
         vs_array[n] = states_all.get(n);
      }
      
      return vs_array;

   }
}
