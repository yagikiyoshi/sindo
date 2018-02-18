package sys;
/**
 * Fundamental physical constants, unit conversion, and built-in constants. <br>
 * 
 * <p> The physical constants are taken from CODATA 2010 (http://physics.nist.gov/cuu/Constants/index.html). </p>
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class Constants {
   
   // CODATA 2010 http://physics.nist.gov/cuu/Constants/index.html
   
   /**
    * Planck's constant (6.62606957e-34 Js)
    */
   public static double Planck = 6.62606957e-34;
   /**
    * Planck / 2PI (1.05457173E-34 Js)
    */
   public static double hbar = Planck/2.0D/Math.PI;
   /**
    * Avogadro constant (6.02214129e+23)
    */
   public static double Avogadro = 6.02214129e+23;
   /**
    * Speed of light (299792458 m/s)
    */
   public static double vlight = 299792458;
   /**
    * Electron mass (9.10938291e-31 kg)
    */
   public static double me = 9.10938291e-31;
   /**
    * Atomic mass unit (1.660538921e-27 kg)
    */
   public static double mu = 1.660538921e-27;
   /**
    * Bohr radius (0.52917721092e-10 m)
    */
   public static double Bohr = 0.52917721092e-10;
   /**
    * Elementary charge (1.602176565e-19 C)
    */
   public static double elementaryCharge = 1.602176565e-19;
   /**
    * Thermal cal to Joule (4.184)
    */
   public static double cal2Joule = 4.184;
   
   
   /**
    * Hartree to Joule (4.35974434E-18 J)
    */
   public static double Hartree2J = (hbar/Bohr)*(hbar/Bohr)/me;
   /**
    * Hartree to kJ/mol (2.62549964E+03)
    */
   public static double Hartree2kJmol = Hartree2J*Avogadro/1000;
   /**
    * Hartree to kcal/mol (6.27509474E+02)
    */
   public static double Hartree2kcalmol = Hartree2kJmol/cal2Joule;
   /**
    * Hartree to cm-1 (2.19474631E+05)
    */
   public static double Hartree2wvn = Hartree2J/Planck/vlight/100.0;
   
   /**
    * Bohr to Angstrom (0.52917720859)
    */
   public static double Bohr2Angs = Bohr*1.0e+10;
   /**
    * Electron mass unit to atomic mass unit (5.48579909e-4)
    */
   public static double Emu2Amu = me/mu;
   
   /**
    * Time in atomic unit to second (2.41888433E-17)
    */
   public static double atu2sec = hbar/Hartree2J;
   /**
    * Time in atomic unit to femto-second (2.41888433E-12)
    */
   public static double atu2fs = atu2sec*1e+15;

   /**
    * Speed of light in atomic unit (1.3703599918E+02 au). This is equal to the inverse of the fine structure constant. 
    */
   public static double vlight_in_au = vlight/Bohr*atu2sec;

   /**
    * The remote shell used for RunProcess (rsh/ssh/null). If null, the remote 
    * shell is disabled and the process is invoked at a local host. 
    */
   public static String remoteShell = null;
   /**
    * Supported type of quantum chemistry program. All letters must be in upper case.  
    */
   //public static String[] qchemType = {"GAUSSIAN","PIMD","ACESII","MOLPRO","QCHEM"};
   public static String[] qchemType = {"GAUSSIAN"};
   
}
