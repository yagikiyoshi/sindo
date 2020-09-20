package md;

import java.io.*;
import java.util.*;
import sys.*;

public class GenesisLog {

   private HashMap<String, Double[]> dataMap;
   
   public void readInfo(String fname){
      
      String[] map = null;
      ArrayList<Double>[] data = null;
      boolean first = true;
      
      try {
         BufferedReader br = new BufferedReader(new FileReader(fname));
         String line = null;
         
         double last_step = 0.0;
         double last_time = 0.0;
         
         while((line = br.readLine()) != null) {
            if (line.indexOf("INFO") < 0) continue;
            if (! first && line.indexOf("STEP") > 0) {
               last_step = data[0].get(data[0].size()-1);
               last_time = data[1].get(data[0].size()-1);
               continue;
            }
            
            String[] splitline = Utilities.splitWithSpaceString(line.substring(6));
            
            if(first) {
               first = false;
               map = splitline;
               data = new ArrayList[map.length];
               for(int i=0; i<map.length; i++) {
                  data[i] = new ArrayList<Double>();
               }
            } else {
               data[0].add(Double.parseDouble(splitline[0])+last_step);
               data[1].add(Double.parseDouble(splitline[1])+last_time);
               for(int i=2; i<map.length; i++) {
                  data[i].add(Double.parseDouble(splitline[i]));
               }
            }
         }
         br.close();
         
      }catch(IOException e) {
         e.printStackTrace();
         Utilities.terminate();
      }
      
      dataMap = new HashMap<String, Double[]>();
      for(int i=0; i<map.length; i++) {
         dataMap.put(map[i], data[i].toArray(new Double[0]));
      }
      
   }
   
   public Double[] getData(String key) {
      return dataMap.get(key);
   }
}
