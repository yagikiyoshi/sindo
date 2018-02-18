package qchem;

import java.io.*;

import jobqueue.Resource;

import sys.Constants;
import sys.Utilities;
import sys.XMLHandler;
import molecule.*;
import atom.*;

public class RestartGeomOptGAUSSIAN {

   protected boolean opt_isConv;
   
   public static void main(String[] args){
      
      if(args.length < 1){
         System.out.println("USAGE: java RestartGeomOptGAUSSIAN xxx");
         System.out.println("   xxx: basename of the Gaussian job");
         System.exit(-1);
      }
      
      String basename = args[0];
      
      RestartGeomOptGAUSSIAN main = new RestartGeomOptGAUSSIAN();
      main.genRestartFiles(basename);
            
   }
   
   public boolean genRestartFiles(String basename){
      
      boolean isError = checkOutput(basename);
      
      if(isError){
         Molecule molecule = this.readOutput(basename);

         File output = new File(basename+".out");
         int num = 1;
         File save = new File(basename+".out_"+num);
         while(save.exists()){
            num++;
            save = new File(basename+".out_"+num);
         }
         output.renameTo(save);
         
         File input = new File(basename+".com");
         input.renameTo(new File(basename+".com_"+num));
         this.genInput(basename,molecule);
         
      }
      
      return isError;
      
   }
   
   public boolean checkOutput(String basename){
      
      boolean isError = false;

      try{
         File output = new File(basename+".out");
         if(! output.exists()){
            System.out.println(output+" is not found.");
            return isError;
         }
         
         BufferedReader br = new BufferedReader(new FileReader(output));
         String line = null;
         while((line=br.readLine()) != null){
            if(line.indexOf("Error termination") != -1){
               isError = true;
               break;
            }
         }
         br.close();

         if(! isError){
            System.out.println(output+" is successfully finished.");
         }
         
      }catch(IOException e){
         e.printStackTrace();
      }
      
      return isError;
      
   }
   
   public Molecule readOutput(String basename){
      
      Molecule molecule = new Molecule();
      opt_isConv = false;
      
      try{

         BufferedReader br = new BufferedReader(new FileReader(basename+".out"));
         
         String line = br.readLine();
         while(line.indexOf("Leave Link    1") == -1){
            line = br.readLine();
         }
         br.readLine();
         br.readLine();
         molecule.setTitle(br.readLine());

         int Nat=0;
         while(true){
            line = br.readLine();
            if(line.indexOf("NAtoms") != -1){
               String[] ss = Utilities.splitWithSpaceString(line);
               Nat = Integer.parseInt(ss[1]);
               break;
            }
         }
         //System.out.println(Nat);

         double[][] current_xyz = new double[Nat][3];

         while(line.indexOf("Input orientation") == -1){
            line = br.readLine();
         }
         
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         for(int n=0; n<Nat; n++){
            String[] ss = Utilities.splitWithSpaceString(br.readLine());
            Atom atom = new Atom(Integer.parseInt(ss[1]));
            double[] xyz = new double[3];
            xyz[0] = Double.parseDouble(ss[3])/Constants.Bohr2Angs;
            xyz[1] = Double.parseDouble(ss[4])/Constants.Bohr2Angs;
            xyz[2] = Double.parseDouble(ss[5])/Constants.Bohr2Angs;
            atom.setXYZCoordinates(xyz);
            molecule.addAtom(atom);
            
            current_xyz[n][0] = xyz[0];
            current_xyz[n][1] = xyz[1];
            current_xyz[n][2] = xyz[2];
            
         }
         
         double rmsforce = 100.0;
         double maxforce = 100.0;
         
         int ngeom = 0;
         int nn = 1;
         
         while((line = br.readLine()) != null){
            if(line.indexOf("Input orientation") != -1){
               br.readLine();
               br.readLine();
               br.readLine();
               br.readLine();
               for(int n=0; n<Nat; n++){
                  String[] ss = Utilities.splitWithSpaceString(br.readLine());
                  current_xyz[n][0] = Double.parseDouble(ss[3])/Constants.Bohr2Angs;
                  current_xyz[n][1] = Double.parseDouble(ss[4])/Constants.Bohr2Angs;
                  current_xyz[n][2] = Double.parseDouble(ss[5])/Constants.Bohr2Angs;
               }
               
               nn++;
               
            }

            if(line.indexOf("Maximum Force") != -1){
               String[] s1 = Utilities.splitWithSpaceString(line);
               String[] s2 = Utilities.splitWithSpaceString(br.readLine());
               if(s1[4].equals("YES") && s2[4].equals("YES")){
                  for(int n=0; n<Nat; n++){
                     Atom atom = molecule.getAtom(n);
                     double[] xyz = atom.getXYZCoordinates();
                     for(int xx=0; xx<3; xx++){
                        xyz[xx] = current_xyz[n][xx];
                     }
                  }
                  opt_isConv = true;
                  ngeom = nn;
                  break;
               }
   
               double ff = Double.parseDouble(s2[2]);
               if(ff < rmsforce){
                  rmsforce = ff;
                  maxforce = Double.parseDouble(s1[2]);
                  for(int n=0; n<Nat; n++){
                     Atom atom = molecule.getAtom(n);
                     double[] xyz = atom.getXYZCoordinates();
                     for(int xx=0; xx<3; xx++){
                        xyz[xx] = current_xyz[n][xx];
                     }
                  }
                  ngeom = nn;
               }
               
            }
         }
         
         if(ngeom == 1){
            System.out.println("The optimization is terminated at the first geometry...");
            System.out.printf(" maxforce = %12.6f  (G09threshold=0.000450) \n",maxforce);
            System.out.printf(" rmsforce = %12.6f  (G09threshold=0.000300) \n",rmsforce);
         }else if(opt_isConv){
            System.out.printf("The force is converged at the %3d-th step \n",ngeom);
            System.out.printf(" maxforce = %12.6f  (G09threshold=0.000450) \n",maxforce);
            System.out.printf(" rmsforce = %12.6f  (G09threshold=0.000300) \n",rmsforce);
         }else {
            System.out.printf("The next geometry is taken from the %3d-th step \n",ngeom);
            System.out.printf(" maxforce = %12.6f  (G09threshold=0.000450) \n",maxforce);
            System.out.printf(" rmsforce = %12.6f  (G09threshold=0.000300) \n",rmsforce);
         }
         
         br.close();
         
      }catch (IOException e){
         e.printStackTrace();
         System.exit(-1);
      }
      
      return molecule;
      
   }
   
   public void genInput(String basename, Molecule molecule){

      try{
         XMLHandler options = new XMLHandler();
         options.readXMLFile("GaussianInput.xml");

         Resource res = new Resource();
         res.setMemory(8);
         res.setScr(40);
         res.setPpn(8);

         QuantChem qchem = new QuantChem();
         InputMaker im = qchem.getInputMaker("Gaussian");
         im.setOptions(options);
         im.setBasename(basename);
         im.setMolecule(molecule);
         im.setResource(res);
         im.makeInputFile();

      }catch(IOException e){
         System.out.println(e.getMessage());
         
      }catch(Exception e){
         System.out.println(e.getMessage());
      }


   }
   
}
