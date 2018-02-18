package qchem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import molecule.Molecule;
import molecule.VibUtil;
import sys.Constants;
import sys.Utilities;
import vibration.CoordProperty;
import vibration.CoordinateData;
import vibration.MolToVib;

/**
 * Write the vibrational data to a file in G03 format
 * @author kyagi
 * @version 1.0
 * @since Sindo 3.0
 *
 */
public class WriteGaussian {

   /**
    * Write the vibrational data to a file in G03 format
    * @param fname name of the file
    * @param molecule molecule to be written
    */
   public void writeFreqOutput(String fname, Molecule molecule){
      
      int Nat = molecule.getNat();
      int Nat3 = Nat*3;
      
      double[] x0 = molecule.getXYZCoordinates1();
      for(int i=0; i<Nat3; i++){
         x0[i] = x0[i]*Constants.Bohr2Angs;
      }
      double[] mass = molecule.getMass();
      for(int i=0; i<Nat; i++){
         mass[i] = mass[i]*Constants.Emu2Amu;
      }

      MolToVib m2v = new MolToVib();
      CoordinateData cdata = m2v.getCoordinateData(molecule);
      
      int Nfree= cdata.Nfree;
      double[] omega = cdata.getOmegaV();
      double[][] CL = Utilities.deepCopy(cdata.getCV());
      CoordProperty cprop = new CoordProperty(cdata);
      double[] rdmass = cprop.getReducedMass();
      for(int i=0; i<rdmass.length; i++){
         rdmass[i] = rdmass[i]*Constants.Emu2Amu;
      }
      
      for(int i=0; i<Nfree; i++){
         double aa = 0.0d;
         for(int j=0; j<Nat3; j++){
            CL[i][j]=CL[i][j]/Math.sqrt(mass[j/3]);
         }
         for(int j=0; j<Nat3; j++){
            aa=aa + CL[i][j]*CL[i][j];
         }
         //rdmass[i] = 1.0/aa;
         aa=Math.sqrt(aa);
         for(int j=0; j<Nat3; j++){
            CL[i][j] = CL[i][j]/aa;
         }
         
      }
      
      VibUtil vutil = new VibUtil(molecule);
      double[] infrared = vutil.calcIRintensity();
      if(infrared==null){
         infrared = new double[Nfree];
      }

      FileWriter fw;
      try {
         fw = new FileWriter(fname);
         PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
         pw.println(" Entering Gaussian System, Link 0=/home/yagi/pgm/g03RevE.01/g03");
         pw.println();
         pw.println(" Copyright (c) 1988,1990,1992,1993,1995,1998,2003,2004,2007, Gaussian, Inc.");
         pw.println("                  All Rights Reserved.");
         pw.println();
         pw.println(" --------------------");
         pw.println(" # CCSD(T)/cc-pVTZ Freq");
         pw.println(" --------------------");
         pw.println(" GradGradGradGradGradGradGradGradGradGradGradGradGradGradGradGradGradGrad");
         pw.println(" GradGradGradGradGradGradGradGradGradGradGradGradGradGradGradGradGradGrad");
         pw.println();
         pw.println("                          Input orientation:");
         pw.println(" ---------------------------------------------------------------------");
         pw.println(" Center     Atomic     Atomic              Coordinates (Angstroms)");
         pw.println(" Number     Number      Type              X           Y           Z");
         pw.println(" ---------------------------------------------------------------------");
         for(int i=0; i<Nat; i++){
            pw.printf(" %3d %10d             0    %12.6f%12.6f%12.6f \n",(i+1),molecule.getAtom(i).getAtomicNum(),x0[3*i],x0[3*i+1],x0[3*i+2]);
         }
         pw.println(" ---------------------------------------------------------------------");
         pw.println("                         Standard orientation:");
         pw.println(" ---------------------------------------------------------------------");
         pw.println(" Center     Atomic     Atomic              Coordinates (Angstroms)");
         pw.println(" Number     Number      Type              X           Y           Z");
         pw.println(" ---------------------------------------------------------------------");
         for(int i=0; i<Nat; i++){
            pw.printf(" %3d %10d             0    %12.6f%12.6f%12.6f \n",(i+1),molecule.getAtom(i).getAtomicNum(),x0[3*i],x0[3*i+1],x0[3*i+2]);
         }
         pw.println(" ---------------------------------------------------------------------");
         pw.println();
         pw.println("Harmonic frequencies (cm**-1), IR intensities (KM/Mole), Raman scattering ");
         pw.println("activities (A**4/AMU), depolarization ratios for plane and unpolarized ");
         pw.println("incident light, reduced masses (AMU), force constants (mDyne/A), ");
         pw.println("and normal coordinates:");
         for(int i=0; i<Nfree/3; i++){
            pw.printf("%22d %22d %22d",i*3+1,i*3+2,i*3+3);
            pw.println();
            pw.println("                     A                      A                      A");
            pw.printf(" Frequencies -- %10.4f %22.4f %22.4f",omega[i*3],omega[i*3+1],omega[i*3+2]);
            pw.println();
            pw.printf(" Red. masses -- %10.4f %22.4f %22.4f",rdmass[i*3],rdmass[i*3+1],rdmass[i*3+2]);
            pw.println();
            pw.printf(" Frc consts  -- %10.4f %22.4f %22.4f",1.0,1.0,1.0);
            pw.println();
            pw.printf(" IR Inten    -- %10.4f %22.4f %22.4f",infrared[i*3],infrared[i*3+1],infrared[i*3+2]);
            pw.println();
            pw.println(" Atom AN      X      Y      Z        X      Y      Z        X      Y      Z");
            for(int j=0; j<Nat; j++){
               pw.printf("%4d%4d  %7.2f%7.2f%7.2f  %7.2f%7.2f%7.2f  %7.2f%7.2f%7.2f",j+1,molecule.getAtom(j).getAtomicNum(),
                     CL[i*3][j*3],CL[i*3][j*3+1],CL[i*3][j*3+2],
                     CL[i*3+1][j*3],CL[i*3+1][j*3+1],CL[i*3+1][j*3+2],
                     CL[i*3+2][j*3],CL[i*3+2][j*3+1],CL[i*3+2][j*3+2]);
               pw.println();
            }
         }
         int aa = Nfree%3;
         if(aa == 1){
            pw.printf("%22d",Nfree);
            pw.println();
            pw.println("                     A");
            pw.printf(" Frequencies -- %10.4f",omega[Nfree-1]);
            pw.println();
            pw.printf(" Red. masses -- %10.4f",rdmass[Nfree-1]);
            pw.println();
            pw.printf(" Frc consts  -- %10.4f",1.0);
            pw.println();
            pw.printf(" IR Inten    -- %10.4f",infrared[Nfree-1]);
            pw.println();
            pw.println(" Atom AN      X      Y      Z");
            for(int j=0; j<Nat; j++){
               pw.printf("%4d%4d  %7.2f%7.2f%7.2f",j+1,molecule.getAtom(j).getAtomicNum(),
                     CL[Nfree-1][j*3],CL[Nfree-1][j*3+1],CL[Nfree-1][j*3+2]);
               pw.println();
            }
         }else if(aa == 2){
            pw.printf("%22d %22d",Nfree-1, Nfree);
            pw.println();
            pw.println("                     A                      A");
            pw.printf(" Frequencies -- %10.4f %22.4f",omega[Nfree-2],omega[Nfree-1]);
            pw.println();
            pw.printf(" Red. masses -- %10.4f %22.4f",rdmass[Nfree-2],rdmass[Nfree-1]);
            pw.println();
            pw.printf(" Frc consts  -- %10.4f %22.4f",1.0,1.0);
            pw.println();
            pw.printf(" IR Inten    -- %10.4f %22.4f",infrared[Nfree-2],infrared[Nfree-1]);
            pw.println();
            pw.println(" Atom AN      X      Y      Z        X      Y      Z");
            for(int j=0; j<Nat; j++){
               pw.printf("%4d%4d  %7.2f%7.2f%7.2f  %7.2f%7.2f%7.2f",j+1,molecule.getAtom(j).getAtomicNum(),
                     CL[Nfree-2][j*3],CL[Nfree-2][j*3+1],CL[Nfree-2][j*3+2],
                     CL[Nfree-1][j*3],CL[Nfree-1][j*3+1],CL[Nfree-1][j*3+2]);
               pw.println();
            }
            
         }
         pw.println();
         pw.println("-------------------");
         pw.println("- Thermochemistry -");
         pw.println("-------------------");
         for(int i=0; i<Nat; i++){
            pw.printf("Atom %2d has atomic number %2d and mass %9.5f",i+1,molecule.getAtom(i).getAtomicNum(),mass[i]);
            pw.println();
         }

         pw.println();
         pw.println("Normal termination of Gaussian 03.");
         pw.close();
         
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
