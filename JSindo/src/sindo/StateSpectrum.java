package sindo;

import java.util.*;
import java.io.*;
import sindo.*;
import vibration.*;

public class StateSpectrum {

   private ArrayList<Conf> targetArray;
   private String label;
   private ArrayList<VibState> stateArray;

   /**
    * Extracts target states from a given set of VibStates and 
    * creates IR spectrum of target states. 
    */
   public StateSpectrum() {
      stateArray = new ArrayList<VibState>();
      targetArray = new ArrayList<Conf>();
   }
   
   /**
    * Extracts target states from a given set of VibStates and 
    * creates IR spectrum of target states. 
    * @param label name of a state or spectrum
    */
   public StateSpectrum(String label) {
      this();
      this.label = label;
   }

   /**
    * Extracts target states from a given set of VibStates and 
    * creates IR spectrum of target states. 
    * @param target target state
    * @param label the name of a state or spectrum
    */
   public StateSpectrum(Conf target, String label) {
      this();
      this.targetArray.add(target);
      this.label = label;
   }

   /**
    * Sets a target state. Note that more than one 
    * states can be set to be the target.
    * @param target
    */
   public void setTarget(Conf target) {
      this.targetArray.add(target);
   }

   /**
    * Sets a label. 
    * @param label the name of a state or spectrum
    */
   public void setLabel(String label) {
      this.label = label;
   }
   
   /**
    * Returns the label
    * @return label
    */
   public String getLabel() {
      return this.label;
   }
   
   /**
    * Extract target states from a given VQDPTdata.
    * @param data_n VQDPTdata
    */
   public void extract(VQDPTdata data_n) {
      double zpe = data_n.getZPE();
      ArrayList<VibState> stateList = data_n.getAllStates();
      
      for(int m=0; m<stateList.size(); m++) {
         
         VibState state = stateList.get(m);
         Conf mainConf = state.getMainConf();
         for(Conf c: targetArray) {
            if(mainConf.equals(c)) {
               state.shiftEnergy(-zpe);
               stateArray.add(state);
               //System.out.println(mainConf.print()+"Energy = "+state.getEnergy()+" IR = "+state.getIRintensity());
            }
         }
      }
   }
   
   /**
    * Extract target states from a given set of VQDPTdata 
    * @param data a set of VQDPTdata
    */
   public void extract(VQDPTdata[] data) {
      for(int n=0; n<data.length; n++) {
         this.extract(data[n]);
      }
   }
   
   /**
    * Returns a band profile of current data set
    * @param gamma Broadening factor (in cm^{-1})
    * @return band profile
    */
   public BandProfile getBandProfile(double gamma) {
      SindoUtil sutil = new SindoUtil();
      return sutil.getIRspectrum(this.stateArray.toArray(new VibState[0]), 0.0, gamma);

   }
   
   /**
    * Returns a set of VibStates of current data set.
    * @return Returns VibStates
    */
   public VibState[] getStates() {
      return stateArray.toArray(new VibState[0]);
   }
   
   /**
    * Prints the content of VibStates (Energy, IR, Conf)
    * @return StringBuffer of the output.
    */
   public StringBuffer printStates() {
      StringBuffer sb = new StringBuffer();
      sb.append(String.format("%12s","Energy"));
      sb.append(String.format("%12s", "IR"));
      sb.append(String.format("    %s","Conf"));
      sb.append("\n");
      for(VibState state: stateArray) {
         sb.append(String.format("%12.2f", state.getEnergy()));
         sb.append(String.format("%12.2f", state.getIRintensity()));
         sb.append(String.format("    %s",   state.getMainConf().print()));
         sb.append("\n");
      }
      return sb;
   }
   
}
