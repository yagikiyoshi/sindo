# SINDO

SINDO is a suite of programs for molecular vibrational analysis.

- JSindo: Viewer of molecules and modes  
  - Import the output of quantum chemistry program  
  - Harmonic analysis  
     - Harmonic frequencies  
     - IR intensity  
     - Normal modes  
  - Localized modes  

- RunMakePES: Generator of anharmonic potential
  - nMR expansion (n=1-4)
  - QFF generation
  - Grid-PES based on HO-DVR
  - Hybrid PES
  - Implementation for parallel execution
  - Interface with quantum chemistry programs

- SINDO: Solver of vibrational Schr&ouml;dinger equation
  - Multiresolution PES
  - optimized-coordinate-VSCF
  - VSCF
  - VCI[m]-(k)
  - VMP2-(k)
  - VQDPT2-(k)
  - Infrared intensities and spectrum

## Author
Kiyoshi Yagi  
Theoretical Molecular Science Laboratory,  
RIKEN  
kiyoshi.yagi@riken.jp  


## Copyright Notice
SINDO is distributed under the GNU General Public License version 3.

Copyright 2009 - 2022

SINDO is free software; you can redistribute it and/or  
modify it under the terms of the GNU Lesser General Public  
License as published by the Free Software Foundation; either  
version 3 of the License, or (at your option) any later version.  

SINDO is distributed in the hope that it will be useful,  
but WITHOUT ANY WARRANTY; without even the implied warranty of  
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.  

You should have received a copy of the GNU General Public  
License along with SINDO -- see the file COPYING   
If not, see https://www.gnu.org/licenses/.

## External packages
JSindo uses the following packages.

-  GlueGen, JOGL, and Java3D from JogAmp: High Performance Cross 
   Platform Java Libraries for 3D Graphics, Multimedia and Processing.
   See https://jogamp.org/

-  JAMA : A Java Matrix Package. See https://math.nist.gov/javanumerics/jama/

The license files are included in `JSindo/external_licenses`.

## Acknowledgment ##
Dr. Hiroya Asami (Gakushuin Univ.)  
Prof. Masaaki Fujii (Tokyo Institute of Technology)  
Prof. Hiroshi Fujisaki (Nippon Medical School)  
Prof. So Hirata (Univ. of Illinois at Urbana-Champaign)  
Prof. Wataru Mizukami (Osaka Univ.)  
Prof. Hiroki Otaki (Nagasaki Univ.)  
Dr. Yuji Sugita (RIKEN)  

## References

#### Review
1. (Japanese) Development of Molecular Vibrational Structure Theory with an Explicit Account of Anharmonicity,
K. Yagi, Mol. Sci. 10, A0085 (2016). [PDF]

#### Vibrational Theories
1. Vibrational quasi-degenerate perturbation theory with optimized coordinates: Applications to ethylene and trans-1,3-butadiene,
K. Yagi and H. Otaki, J. Chem. Phys. 140, 084113 (2014). [13 pages]
1. Optimized coordinates for anharmonic vibrational structure theories,
K. Yagi, M. Keçeli, and S. Hirata, J. Chem. Phys. 137, 204118 (2012). [16 pages]
1. Vibrational quasi-degenerate perturbation theory: Applications to Fermi resonance in CO2, H2CO, and C6H6,
K. Yagi, S. Hirata, and K. Hirao, Phys. Chem. Chem. Phys. 10, 1781-1788 (2008).

#### PES Generation
1. Multiresolution potential energy surfaces for vibrational state calculations,
K. Yagi, S. Hirata, and K. Hirao, Theor. Chem. Acc. 118, 681-691 (2007).
1. Ab initio vibrational state calculations with a quartic force field: Applications to H2CO, C2H4, CH3OH, CH3CCH, and C6H6,
K. Yagi, K. Hirao, T. Taketsugu, M. W. Schmidt, and M. S. Gordon, J. Chem. Phys. 121, 1383-1389 (2004).
1. Direct vibrational self-consistent field method: Application to H2O and H2CO,
K. Yagi, T. Taketsugu, K. Hirao, and M. S. Gordon, J. Chem. Phys. 113, 1005-1017 (2000).

#### Complex Systems
1. Anharmonic Vibrational Analysis of Biomolecules and Solvated Molecules Using Hybrid QM/MM Computations,
K. Yagi, K. Yamada, C. Kobayashi, and Y. Sugita, J. Chem. Theory Comput. 15, 1924 (2019).
1. Anharmonic Vibrational Calculations Based on Group-Localized Coordinates: Applications to Internal Water Molecules in Bacteriorhodopsin,
K. Yagi and Y. Sugita, J. Chem. Theory Comput. 17, 5007 (2021).
1. Towards complete assignment of the infrared spectrum of the protonated water cluster H+(H2O)21,
J. Liu, J. Yang, X. C. Zeng, S. S. Xantheas, K. Yagi, and X. He, Nat. Comm. 12, 6141 (2021).


