# SINDO

SINDO is a suit of programs for molecular vibrational analysis.

o JSindo: Viewer of molecules and modes
  - Import the output of quantum chemistry program
  - Harmonic analysis 
     o Harmonic frequencies
     o IR/Raman intensity 
     o Normal modes
  - Localized modes

o RunMakePES: Generator of anharmonic potential
  - nMR expansion (n=1-4)
  - QFF generation
  - Grid-PES based on HO-DVR
  - Hybrid PES
  - Implementation for parallel execution
  - Interface with quantum chemistry programs

o SINDO: Solver of vibrational schroedinger equation
  - Multiresolution PES
  - optimized-coordinate-VSCF
  - VSCF
  - VCI[m]-(k)
  - VMP2-(k)
  - VQDPT2-(k)
  - Infrared intensities and spectrum

