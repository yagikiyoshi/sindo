package ff;

import java.io.*;
import java.util.*;

import sys.Constants;
import sys.Utilities;

/**
 * CHARMM force field parameters with a reader.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class CHARMMff extends ForceField {

   /**
    * Reads the parameters from a par file
    * @param parFile the parameter file
    * @throws IOException thrown when an IO error occurred
    */
   public void readParameter(String parFile) throws IOException{
      this.readParameter(parFile, false);
   }
      
   /**
    * Reads the parameter from a par file
    * @param parFile the parameter file
    * @param append append to the existing parameter if true. It is assumed there is no overlap 
    *        with the existing parameters. 
    * @throws IOException thrown when an IO error occurred
    */
   public void readParameter(String parFile, boolean append) throws IOException{
      
      String line;

      BufferedReader par = new BufferedReader(new FileReader(parFile));

      // 0: bond, 1: angle, 2: diheral, 3: improper, 4: cmap, 5: vdw 
      boolean[] section = new boolean[6];
      for(int i=0; i<section.length; i++){
         section[i] = false;
      }
      
      ArrayList<FuncBond> bondArray = new ArrayList<FuncBond>();
      ArrayList<FuncAngle> angleArray = new ArrayList<FuncAngle>();
      ArrayList<FuncUB> ubArray = new ArrayList<FuncUB>();
      ArrayList<FuncDihed> dihedArray = new ArrayList<FuncDihed>();
      ArrayList<FuncDihed> dihedxArray = new ArrayList<FuncDihed>();
      ArrayList<VdW> vdwArray = new ArrayList<VdW>();
      
      if(append){
         if(bonds != null){
            for(int i=0; i<bonds.length; i++){
               bondArray.add(bonds[i]);
            }
         }
         if(angles != null){
            for(int i=0; i<angles.length; i++){
               angleArray.add(angles[i]);
            }
         }
         if(ureyBradley != null){
            for(int i=0; i<ureyBradley.length; i++){
               ubArray.add(ureyBradley[i]);
            }
         }
         if(diheds != null){
            for(int i=0; i<diheds.length; i++){
               dihedArray.add(diheds[i]);
            }
         }
         if(dihedx != null){
            for(int i=0; i<dihedx.length; i++){
               dihedxArray.add(dihedx[i]);
            }
         }
         if(vdws != null){
            for(int i=0; i<vdws.length; i++){
               vdwArray.add(vdws[i]);
            }
         }
      }

      while((line = this.readLine(par)) != null){
         if(line.equals("BONDS")){
            // Bond functions set up
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
            section[0] = true;
            line = this.readLine(par);
         }else if(line.equals("ANGLES")){
            // Angle functions set up
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
            section[1] = true;
            line = this.readLine(par);
         }else if(line.equals("DIHEDRALS")){
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
            section[2] = true;
            line = this.readLine(par);
         }else if(line.equals("IMPROPER")){
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
            section[3] = true;
            line = this.readLine(par);
         }else if(line.equals("CMAP")){
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
            section[4] = true;
            line = this.readLine(par);
         }else if(line.indexOf("NONBONDED") != -1){
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
            section[5] = true;
            line = this.readLine(par);
         }else if(line.indexOf("HBOND") != -1){
            for(int i=0; i<section.length; i++){
               section[i] = false;
            }
         }
         
         if(section[0]){
            // Bond functions
            String[] ss = Utilities.splitWithSpaceString(line);
            
            FuncBond funcb = new FuncBond();

            String[] atoms = new String[2];
            atoms[0] = ss[0];
            atoms[1] = ss[1];
            atoms = Utilities.sort(atoms);
            double kb = Double.parseDouble(ss[2])
                        /Constants.Hartree2kcalmol*Constants.Bohr2Angs*Constants.Bohr2Angs;
            double b0 = Double.parseDouble(ss[3])/Constants.Bohr2Angs;
            
            funcb.setAtomType(atoms);
            funcb.setKb(kb);
            funcb.setB0(b0);
            
            bondArray.add(funcb);
            
         }else if(section[1]){
            // Angle functions
            String[] ss = Utilities.splitWithSpaceString(line);

            String[] aa = Utilities.sort(ss[0],ss[2]);
            String[] atoms = new String[3];
            atoms[0] = aa[0];
            atoms[1] = ss[1];
            atoms[2] = aa[1];

            FuncAngle funca = new FuncAngle();

            double kth = Double.parseDouble(ss[3])/Constants.Hartree2kcalmol
                  /180.0*Math.PI/180.0*Math.PI;
            double th0 = Double.parseDouble(ss[4]);
            
            funca.setAtomType(atoms);
            funca.setKtheta(kth);
            funca.setTheta0(th0);
            
            angleArray.add(funca);
            
            if(ss.length > 5){
               // Urey-Bradley functions
               FuncUB funcub = new FuncUB();
               
               double kub = Double.parseDouble(ss[5])/Constants.Hartree2kcalmol
                     *Constants.Bohr2Angs*Constants.Bohr2Angs;
               double s0 = Double.parseDouble(ss[6])/Constants.Bohr2Angs;

               funcub.setAtomType(atoms);
               funcub.setKub(kub);
               funcub.setS0(s0);
               
               ubArray.add(funcub);
            }
         }else if(section[2]){
            // Dihedral functions
            String[] ss = Utilities.splitWithSpaceString(line);
            
            String[] atoms = new String[4];
            boolean withx = false;
            if(! ss[0].equals("X")){
               if(ss[0].compareTo(ss[3]) < 0){
                  atoms[0] = ss[0];
                  atoms[1] = ss[1];
                  atoms[2] = ss[2];
                  atoms[3] = ss[3];
               }else{
                  atoms[0] = ss[3];
                  atoms[1] = ss[2];
                  atoms[2] = ss[1];
                  atoms[3] = ss[0];
               }
               
            }else{
               withx = true;
               atoms[0] = ss[0];
               atoms[3] = ss[3];
               if(ss[1].compareTo(ss[2]) < 0){
                  atoms[1] = ss[1];
                  atoms[2] = ss[2];
               }else{
                  atoms[1] = ss[2];
                  atoms[2] = ss[1];
               }
               
            }
            
            double kchi  = Double.parseDouble(ss[4])/Constants.Hartree2kcalmol;
            int    nn    = Integer.parseInt(ss[5]);
            double delta = Double.parseDouble(ss[6]);

            FuncDihedChild child = new FuncDihedChild();
            child.setKchi(kchi);
            child.setMultiplicity(nn);
            child.setDelta(delta);

            FuncDihed fundihed =null;
            boolean newDihed = false;
            
            if(dihedArray.size() > 0){
               fundihed = dihedArray.get(dihedArray.size()-1);
               String[] atoms0 = fundihed.getAtomType();
               for(int i=0; i<atoms.length; i++){
                  if(! atoms[i].equals(atoms0[i])){
                     newDihed = true;
                     break;
                  }
               }
               
            }else{
               newDihed = true;
            }
            
            if(newDihed){              
               fundihed = new FuncDihed();
               fundihed.setAtomType(atoms);
               if(! withx){
                  dihedArray.add(fundihed);
               }else{
                  dihedxArray.add(fundihed);
               }
            }
            
            fundihed.setChildFunction(child);
            
         }else if(section[5]){
            // vdW paramters
            String[] ss = Utilities.splitWithSpaceString(line);
            
            VdW vdw = new VdW();
            vdw.setAtomType(ss[0]);
            vdw.setEpsilon(Double.parseDouble(ss[2])/Constants.Hartree2kcalmol);
            vdw.setRmin(Double.parseDouble(ss[3])/Constants.Bohr2Angs);
            
            if(ss.length == 7){
               vdw.setEpsilon_14(Double.parseDouble(ss[5])/Constants.Hartree2kcalmol);
               vdw.setRmin_14(Double.parseDouble(ss[6])/Constants.Bohr2Angs);
            }else{
               vdw.setEpsilon_14(Double.NaN);
               vdw.setRmin_14(Double.NaN);
            }
            
            vdwArray.add(vdw);
            
         }
         
      }

      bonds = bondArray.toArray(new FuncBond[0]);
      angles = angleArray.toArray(new FuncAngle[0]);
      ureyBradley = ubArray.toArray(new FuncUB[0]);
      diheds = dihedArray.toArray(new FuncDihed[0]);
      dihedx = dihedxArray.toArray(new FuncDihed[0]);
      vdws = vdwArray.toArray(new VdW[0]);
      
      par.close();
   }
   
   /**
    * Reads a non-comment line from CHARMM parameter file. The string after characters, "!" and "*", is 
    * ignored. null when the end of file is reached. 
    * @param par The parameter file
    * @return The next non-comment line
    * @throws IOException
    */
   private String readLine(BufferedReader par) throws IOException {
      
      String line;
      while((line = par.readLine()) != null){
         line.trim();
         
         int nn = line.indexOf("!");
         if(nn != -1){
            line = line.substring(0, nn).trim();
         }
         nn = line.indexOf("*");
         if(nn != -1){
            line = line.substring(0, nn).trim();
         }

         if(line.length() > 0) {
            while(true){
               String[] ss = Utilities.splitWithSpaceString(line);
               if(! ss[ss.length-1].equals("-")){
                  break;
               }else{
                  line = "";
                  for(int i=0; i<ss.length-1; i++){
                     line = line + ss[i] + " ";
                  }
                  line = line + par.readLine();
               }
            }
            break;
         }
         
      }
      
      return line;
      
   }
   
   @Override
   public void printParameter() {
      if(bonds != null){
         System.out.println("BONDS");
         System.out.println("!");
         System.out.println("!V(bond) = Kb(b - b0)**2");
         System.out.println("!");
         System.out.println("!Kb: kcal/mole/A**2");
         System.out.println("!b0: A");
         System.out.println("!");
         System.out.println("!atom type  Kb         b0");
         for(int i=0; i<bonds.length; i++){
            String[] atom = bonds[i].getAtomType();
            
            double kb = bonds[i].getKb()
                  *Constants.Hartree2kcalmol/Constants.Bohr2Angs/Constants.Bohr2Angs;
            double b0 = bonds[i].getB0()*Constants.Bohr2Angs;

            System.out.printf("%-5s ",atom[0]);
            System.out.printf("%-5s ",atom[1]);
            System.out.printf("%-10.3f ",kb);
            System.out.printf("%-10.5f ",b0);
            System.out.println();
         }
      }
      if(angles != null){
         System.out.println("!");
         System.out.println("ANGLES");
         System.out.println("!");
         System.out.println("!V(angle) = Ktheta(Theta - Theta0)**2");
         System.out.println("!Ktheta: kcal/mole/rad**2");
         System.out.println("!Theta0: degrees");
         System.out.println("!atom types     Ktheta    Theta0");
         System.out.println("!");
         for(int i=0; i<angles.length; i++){
            String[] atom = angles[i].getAtomType();
            
            double kth = angles[i].getKtheta()*Constants.Hartree2kcalmol;
            double th0 = angles[i].getTheta0()/Math.PI*180.0;

            System.out.printf("%-5s ",atom[0]);
            System.out.printf("%-5s ",atom[1]);
            System.out.printf("%-5s ",atom[2]);
            System.out.printf("%-10.3f ",kth);
            System.out.printf("%-10.2f ",th0);
            System.out.println();
            
         }
      }
      if(ureyBradley != null){
         System.out.println("!");
         System.out.println("UREY-BRADLEY");
         System.out.println("!");
         System.out.println("!V(Urey-Bradley) = Kub(S - S0)**2");
         System.out.println("!Kub: kcal/mole/A**2 (Urey-Bradley)");
         System.out.println("!S0: A");
         System.out.println("!");
         System.out.println("!");
         System.out.println("!atom types     Kub     S0");
         System.out.println("!");
         for(int i=0; i<ureyBradley.length; i++){
            String[] atom = ureyBradley[i].getAtomType();
            
            double kub = ureyBradley[i].getKub()
                  *Constants.Hartree2kcalmol/Constants.Bohr2Angs/Constants.Bohr2Angs;
            double s0 = ureyBradley[i].S0()*Constants.Bohr2Angs;
            
            System.out.printf("%-5s ",atom[0]);
            System.out.printf("%-5s ",atom[1]);
            System.out.printf("%-5s ",atom[2]);
            System.out.printf("%-10.3f ",kub);
            System.out.printf("%-10.5f ",s0);
            System.out.println();
            
         }
      }
      if(diheds != null){
         System.out.println("!");
         System.out.println("DIHEDRALS");
         System.out.println("!");
         System.out.println("!");
         System.out.println("!V(dihedral) = Kchi(1 + cos(n(chi) - delta))");
         System.out.println("!");
         System.out.println("!Kchi: kcal/mole");
         System.out.println("!n: multiplicity");
         System.out.println("!delta: degrees");
         System.out.println("!");
         System.out.println("!atom types             Kchi       n     delta");
         System.out.println("!");
  
         for(int i=0; i<diheds.length; i++){
            String[] atom = diheds[i].getAtomType();
            
            FuncDihedChild[] child = diheds[i].getChildFunction();
            for(int j=0; j<child.length; j++){
               double kchi = child[j].getKchi()*Constants.Hartree2kcalmol;
               int nn = child[j].getMultiplicity();
               double delta = child[j].getDelta();
               System.out.printf("%-5s ",atom[0]);
               System.out.printf("%-5s ",atom[1]);
               System.out.printf("%-5s ",atom[2]);
               System.out.printf("%-5s ",atom[3]);
               System.out.printf("%-10.3f ",kchi);
               System.out.printf("%-5d ",nn);
               System.out.printf("%-10.5f ",delta);
               System.out.printf("%-5d", j);
               System.out.println();
               
            }

         }
         
         for(int i=0; i<dihedx.length; i++){
            String[] atom = dihedx[i].getAtomType();
            
            FuncDihedChild[] child = dihedx[i].getChildFunction();
            for(int j=0; j<child.length; j++){
               double kchi = child[j].getKchi()*Constants.Hartree2kcalmol;
               int nn = child[j].getMultiplicity();
               double delta = child[j].getDelta();
               System.out.printf("%-5s ",atom[0]);
               System.out.printf("%-5s ",atom[1]);
               System.out.printf("%-5s ",atom[2]);
               System.out.printf("%-5s ",atom[3]);
               System.out.printf("%-10.3f ",kchi);
               System.out.printf("%-5d ",nn);
               System.out.printf("%-10.5f ",delta);
               System.out.printf("%-5d", j);
               System.out.println();
               
            }

         }

      }
      
      if(vdws != null){
         System.out.println("!");
         System.out.println("!V(Lennard-Jones) = Eps,i,j[(Rmin,i,j/ri,j)**12 - 2(Rmin,i,j/ri,j)**6]");
         System.out.println("!");
         System.out.println("!epsilon: kcal/mole, Eps,i,j = sqrt(eps,i * eps,j)");
         System.out.println("!Rmin/2: A, Rmin,i,j = Rmin/2,i + Rmin/2,j");
         System.out.println("!");
         System.out.println("!atom  ignored    epsilon      Rmin/2   ignored   eps,1-4       Rmin/2,1-4");
         System.out.println("!");
         for(int i=0; i<vdws.length; i++){
            double eps  = vdws[i].getEpsilon()*Constants.Hartree2kcalmol;
            double rmin2 = vdws[i].getRmin()*Constants.Bohr2Angs;
            
            System.out.printf("%-5s ", vdws[i].getAtomType());
            System.out.printf("%-11.6f", eps);
            System.out.printf("%-13.6f", rmin2);
            
            
            if(! Double.isNaN(vdws[i].getEpsilon_14())){
               double eps_14 = vdws[i].getEpsilon_14()*Constants.Hartree2kcalmol;
               double rmin2_14 = vdws[i].getRmin_14()*Constants.Bohr2Angs;
               System.out.printf("%-11.6f", eps_14);
               System.out.printf("%-13.6f", rmin2_14);
               
            }
            System.out.println();
         }
      }
   }
}
