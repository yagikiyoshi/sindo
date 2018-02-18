package sys;

import java.awt.Color;

/**
 * Provides the information of atom from the periodic table
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class PeriodicTable {
   
   /**
    * Label of atoms
    */
   public static String[] label = {
      "X",
      "H",                                                                                    "He",
      "Li","Be",                                                     "B" ,"C" ,"N" ,"O" ,"F" ,"Ne",
      "Na","Mg",                                                     "Al","Si","P" ,"S" ,"Cl","Ar",
      "K","Ca",  "Sc","Ti","V" ,"Cr","Mn","Fe","Co","Ni","Cu","Zn",  "Ga","Ge","As","Se","Br","Kr",
      "Rb","Sr", "Y" ,"Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd",  "In","Sn","Sb","Te","I" ,"Xe",
      "Cs","Ba", 
         "La","Ce","Pr","Nd","Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu",
                      "Hf","Ta","W" ,"Re","Os","Ir","Pt","Au","Hg",  "Tl","Pb","Bi","Po","At","Rn",
      "Fr","Ra",
         "Ac","Th","Pa","U" ,"Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md","No","Lr",
                      "Rf","Db","Sg","Bh","Hs","Mt","Ds","Rg","Cn",  "Uut","Uuq","Uup","Uuh","Uus","Uuo" };
   
   /**
    * Mass of atoms (atomic mass unit)
    */
   public static double[][] mass = {
      {0.0},                         // X
      {1.0078250321, 2.0141018},     // H
      {4.0026032497},                // He
      {7.0160040},                   // Li
      {9.0121822},                   // Be
      {11.0053054},                  // B
      {12.0, 13.0033548},            // C
      {14.0030740052, 15.0001090},   // N
      {15.9949146221},               // O
      {18.99840322},                 // F
      {19.9924356},                  // Ne
      {22.9897677},                  // Na
      {23.9850423},                  // Mg
      {26.9815386},                  // Al
      {27.9769271},                  // Si
      {30.9737620},                  // P
      {31.97207069},                 // S
      {34.96885271},                 // Cl
      {39.9623837},                  // Ar
      {38.9637074},                  // K
      {39.9625906},                  // Ca
      {44.955910},                   // Sc
      {47.9478711},                  // Ti
      {50.9439617},                  // V
      {51.9405119},                  // Cr
      {54.938049},                   // Mn
      {55.9349393},                  // Fe
      {58.933200},                   // Co
      {57.9353462},                  // Ni
      {62.9295989},                  // Cu
      {63.9291448},                  // Zn
      {68.925581},                   // Ga
      {73.9211774},                  // Ge
      {74.92160},                    // As
      {79.9165196},                  // Se
      {78.9183376},                  // Br
      {83.911507},                   // Kr
      {84.9117893},                  // Rb
      {87.9056143},                  // Sr
      {88.90585},                    // Y
      {89.9047037},                  // Zr
      {92.90638},                    // Nb
      {97.9054078},                  // Mo
      {98.9062546},                  // Tc
      {101.9043495},                 // Ru
      {102.90550},                   // Rh
      {105.903483},                  // Pd
      {106.905093},                  // Ag
      {113.9033581},                 // Cd
      {114.903878},                  // In
      {119.9021966},                 // Sn
      {120.9038180},                 // Sb
      {129.9062228},                 // Te
      {126.90447},                   // I
      {131.9041545},                 // Xe
      {132.90545},                   // Cs
      {137.905241},                  // Ba
      {138.906348},                  // La
      {139.905434},                  // Ce
      {140.90765},                   // Pr
      {143.910083},                  // Nd
      {144.912744},                  // Pm
      {151.919728},                  // Sm
      {152.921226},                  // Eu
      {157.924101},                  // Gd
      {158.92534},                   // Tb
      {163.929171},                  // Dy
      {164.93032},                   // Ho
      {165.930290},                  // Er
      {168.93421},                   // Tm
      {173.9388581},                 // Yb
      {174.9407679},                 // Lu
      {179.9465488},                 // Hf
      {180.947996},                  // Ta
      {183.9509326},                 // W
      {186.9557508},                 // Re
      {191.961479},                  // Os
      {192.962924},                  // Ir
      {194.964774},                  // Pt
      {196.96655},                   // Au
      {201.970626},                  // Hg
      {204.974412},                  // Tl
      {207.976636},                  // Pb
      {208.98038},                   // Bi
      {209.982857},                  // Po
      {209.987131},                  // At
      {222.0175705},                 // Rn
      {223.0197307},                 // Fr
      {226.0254026},                 // Ra
      {227.0277470},                 // Ac
      {232.0381},                    // Th
      {231.03588},                   // Pa
      {238.0507826},                 // U
      {237.0481673},                 // Np
      {239.0521565},                 // Pu
      {243.0613727},                 // Am
      {247.070347},                  // Cm
      {247.070299},                  // Bk
      {252.081620},                  // Cf
      {252.082970},                  // Es
      {257.095099},                  // Fm
      {256.094050},                  // Md
      {259.101020},                  // No
      {259.101020},                  // Lr
      {261.108750},                  // Rf
      {262.114150},                  // Db
      {263.118310},                  // Sg
      {264.124730},                  // Bh
      {277.0},                       // Hs
      {268.138820},                  // Mt
      {269.145140},                  // Ds
      {272.153480},                  // Rg
      {285.0},                       // Cn
      {284.0},                       // Uut
      {289.0},                       // Unq
      {288.0},                       // Uup
      {293.0},                       // Uuh
      {294.0}                        // Uuo
   };
   
   /**
    * Atomic Radii (bohr)
    */
   public static float[] radii = {
   // X
      0.1f, 
   // H      He
      0.60f, 0.59f,
   // Li     Be     B      C      N      O      F      Ne
      2.32f, 1.68f, 1.55f, 1.46f, 1.42f, 1.38f, 1.36f, 1.34f,
   // Na     Mg     Al     Si     P      S      Cl     Ar
      2.91f, 2.57f, 2.23f, 2.10f, 2.00f, 1.93f, 1.87f, 1.85f,
   // K      Ca     Sc     Ti     V      Cr     Mn     Fe     Co     Ni     Cu     Zn     Ga     Ge     As     Se     Br     Kr
      3.84f, 3.29f, 2.72f, 2.49f, 2.31f, 2.23f, 2.21f, 2.21f, 2.19f, 2.17f, 2.21f, 2.36f, 2.38f, 2.31f, 2.27f, 2.21f, 2.15f, 2.12f,   
   // Rb     Sr     Y      Zr     Nb     Mb     Tc     Ru     Rh     Pd     Ag     Cd     In     Sn     Sb     Te     I      Xe
      4.08f, 3.61f, 3.06f, 2.74f, 2.53f, 2.46f, 2.40f, 2.36f, 2.36f, 2.42f, 2.53f, 2.80f, 2.72f, 2.65f, 2.65f, 2.57f, 2.14f, 2.48f,
   // Cs     Ba
      4.44f, 3.74f,
   // La     Ce     Pr     Nd     Pm     Sm     Eu     Gd     Td     Dy     Hb     Er     Tm     Yb     Lu
      3.19f, 3.12f, 3.10f, 3.10f, 3.08f, 3.06f, 3.50f, 3.06f, 3.04f, 3.02f, 2.99f, 2.99f, 2.99f, 3.21f, 2.95f,
   // Hf     Ta     W      Re     Os     Ir     Pt     Au     Hg     Tl     Pb     Bi     Po     At     Rn(NA)
      2.72f, 2.53f, 2.46f, 2.42f, 2.38f, 2.40f, 2.46f, 2.53f, 2.44f, 2.80f, 2.78f, 2.76f, 2.76f, 2.74f, 2.74f,
   // Fr(NA) Ra
      4.16f, 4.16f,
   // Ac     Th     Pa(NA) U      Np(NA) Pu(NA) Am(NA) Cm(NA) Bk(NA) Cf(NA) Es(NA) Fm(NA) Md(NA) No(NA) Lr(NA)
      3.78f, 3.12f, 2.83f, 2.68f, 3.08f, 3.06f, 3.50f, 3.06f, 3.04f, 3.02f, 2.99f, 2.99f, 2.99f, 3.21f, 2.95f,
   // Unq(NA)Unp(NA)Unh(NA)Uns(NA)Uno(NA)Une(NA)
      4.16f, 4.16f, 4.16f, 4.16f, 4.16f, 4.16f
   };

   /**
    * Returns the color of atom (default is white).
    * @param atomicNum Atomic number
    * @return The color of atoms
    */
   public static Color getColor(int atomicNum){
      Color color = null;
      switch (atomicNum){
      
      case 1:
         // H
         color = Color.white;
         break;
      case 2:
         // He
         color = Color.blue;
         break;
      case 3:
         // Li
         color = Color.green;
         break;
      case 6:
         // C
         color = Color.gray;
         break;
      case 7:
         // N
         color = Color.blue;
         break;
      case 8:
         // O
         color = Color.red;
         break;
      case 9:
         // F
         color = Color.cyan;
         break;
      case 10:
         // Ne
         color = Color.orange;
         break;
      case 15:
         // P
         color = Color.orange;
         break;
      case 16:
         // S
         color = Color.yellow;
         break;
      case 17:
         // Cl
         color = Color.green;
         break;
      case 18:
         // Ar
         color = Color.magenta;
         break;
      default:
         color = Color.white;
      }
      return color;
   }
   
   /**
    * Returns the atomic number for a given label of atom
    * @param label the label of atom
    * @return the atomic number
    */
   public static int getAtomicNumber(String label){
      int an = -1;
      
      // H, B, C, N, O, F, P, S, K, V, Y, I, W, U 
      if(label.length() == 1){
         String ss = label.toUpperCase();
         if(ss.charAt(0) == 'H'){
            an = 1;
         }else if(ss.charAt(0) == 'B'){
            an = 5;
         }else if(ss.charAt(0) == 'C'){
            an = 6;
         }else if(ss.charAt(0) == 'N'){
            an = 7;
         }else if(ss.charAt(0) == 'O'){
            an = 8;
         }else if(ss.charAt(0) == 'F'){
            an = 9;
         }else if(ss.charAt(0) == 'P'){
            an = 15;
         }else if(ss.charAt(0) == 'S'){
            an = 16;
         }else if(ss.charAt(0) == 'K'){
            an = 19;
         }else if(ss.charAt(0) == 'V'){
            an = 23;
         }else if(ss.charAt(0) == 'Y'){
            an = 39;
         }else if(ss.charAt(0) == 'I'){
            an = 53;
         }else if(ss.charAt(0) == 'W'){
            an = 74;
         }else if(ss.charAt(0) == 'U'){
            an = 92;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }

      String ss = label.substring(0, 2).toUpperCase();
      
      // "He","Ho","Hf","Hg"
      if(ss.charAt(0) == 'H'){
         if(ss.equals("HE")){
            an = 2;
         }else if(ss.equals("HO")){
            an = 67;  
         }else if(ss.equals("HF")){
            an = 72;  
         }else if(ss.equals("HG")){
            an = 80;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }
      
      // "Li","La","Lu","Lr",
      if(ss.charAt(0) == 'L'){
         if(ss.equals("LI")){
            an = 3;
         }else if(ss.equals("LA")){
            an = 57;
         }else if(ss.equals("LU")){
            an = 71;
         }else if(ss.equals("LR")){
            an = 103;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }

      // "Be","Br","Ba","Bi","Bk"
      if(ss.charAt(0)=='B'){
         if(ss.equals("BE")){
            an = 4;
         }else if(ss.equals("BR")){
            an = 35;
         }else if(ss.equals("BA")){
            an = 56;
         }else if(ss.equals("BI")){
            an = 83;
         }else if(ss.equals("BK")){
            an = 97;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }

      // "Cl","Ca","Cr","Co","Cu","Cd","Cs","Ce","Cm","Cf"
      if(ss.charAt(0)=='C'){
         if(ss.equals("CL")){
            an = 17;
         }else if(ss.equals("CA")){
            an = 20;
         }else if(ss.equals("CR")){
            an = 24;
         }else if(ss.equals("CO")){
            an = 27;
         }else if(ss.equals("CU")){
            an = 29;
         }else if(ss.equals("CD")){
            an = 48;
         }else if(ss.equals("CS")){
            an = 55;
         }else if(ss.equals("CE")){
            an = 58;
         }else if(ss.equals("CM")){
            an = 96;
         }else if(ss.equals("CF")){
            an = 98;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }
      
      // "Ne","Na","Ni","Nb","Nd","Np","No"
      if(ss.charAt(0)=='N'){
         if(ss.equals("NE")){
            an = 10;
         }else if(ss.equals("NA")){
            an = 11;
         }else if(ss.equals("NI")){
            an = 28;
         }else if(ss.equals("NB")){
            an = 41;
         }else if(ss.equals("ND")){
            an = 60;
         }else if(ss.equals("NP")){
            an = 93;
         }else if(ss.equals("NO")){
            an = 102;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }
      
      // "Fe","Fr","Fm"
      if(ss.charAt(0) == 'F'){
         if(ss.equals("FE")){
            an = 26;
         }else if(ss.equals("FR")){
            an = 87;
         }else if(ss.equals("FM")){
            an = 100;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }
      
      //"Mg","Mn","Mo","Md"
      if(ss.charAt(0) == 'M'){
         if(ss.equals("MG")){
            an = 12;
         }else if(ss.equals("MN")){
            an = 25;
         }else if(ss.equals("MO")){
            an = 42;
         }else if(ss.equals("MD")){
            an = 101;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;         
      }
      
      //"Pd","Pr","Pm","Pt","Pb","Po","Pa","Pu"
      if(ss.charAt(0) == 'P'){
         if(ss.equals("PD")){
            an = 46;
         }else if(ss.equals("PR")){
            an = 59;
         }else if(ss.equals("PM")){
            an = 61;
         }else if(ss.equals("PT")){
            an = 78;
         }else if(ss.equals("PB")){
            an = 82;
         }else if(ss.equals("PO")){
            an = 84;
         }else if(ss.equals("PA")){
            an = 91;
         }else if(ss.equals("PU")){
            an = 94;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }
      
      //"Si","Sc","Se","Sr","Sn","Sb","Sm"
      if(ss.charAt(0) == 'S'){
         if(ss.equals("SI")){
            an = 14;
         }else if(ss.equals("SC")){
            an = 21;
         }else if(ss.equals("SE")){
            an = 34;
         }else if(ss.equals("SR")){
            an = 38;
         }else if(ss.equals("SN")){
            an = 50;
         }else if(ss.equals("SB")){
            an = 51;
         }else if(ss.equals("SM")){
            an = 62;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }
      
      //"Al","Ar","As","Ag","Au","At","Ac","Am"
      if(ss.charAt(0) == 'A'){
         if(ss.equals("AL")){
            an = 13;
         }else if(ss.equals("AR")){
            an = 18;
         }else if(ss.equals("AS")){
            an = 33;
         }else if(ss.equals("AG")){
            an = 47;
         }else if(ss.equals("AU")){
            an = 70;
         }else if(ss.equals("AT")){
            an = 85;
         }else if(ss.equals("AC")){
            an = 89;
         }else if(ss.equals("AM")){
            an = 95;
         }else{
            System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
         }
         return an;
      }

      /*
      "Eu","Er","Es",
      "Ga","Ge","Gd",
      "In","Ir","I",
      "Rb","Ru","Rh","Re","Rn","Ra",
      "Ti","Tc","Te","Tb","Tm","Ta","Tl","Th",
      "Zn","Zr",
       */
      
      if(ss.equals("KR")){
         an = 36;
      }else if(ss.equals("XE")){
         an = 54;
      }else if(ss.equals("DY")){
         an = 66;
      }else if(ss.equals("YB")){
         an = 70;
      }else if(ss.equals("OS")){
         an = 76;
      }else{
         System.out.println("WARNING: Unknown species "+label+". Atomic number for this atom is not set.");
      }
      return an;
      
         
   }
   

}
