package md;

import atom.*;
import molecule.*;

/**
 * An interface with the molecule package
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 4.0
 *
 */
public class MolToMD {

   /**
    * Create SystemMD from Molecule. The residue ID is 1 and atoms are 
    * numbered from 1.  
    * @param molecule Molecule to be converted
    * @param segname Segment name
    * @param resname Residue name
    * @return SystemMD
    */
   public SystemMD convert(Molecule molecule, String segname, String resname) {
      SystemMD sys = new SystemMD();
      sys.openSegmentList();
      
      Segment seg = new Segment();
      seg.setName(segname);
      Residue res = new Residue();
      res.setName(resname);
      res.setID(1);
      
      try {
         seg.addResidueList(res);
         sys.addSegmentList(seg);
         
         int nat = molecule.getNat();
         for(int i=0; i<nat; i++) {
            AtomMD atom = this.convert(molecule.getAtom(i));
            res.addAtomList(atom);
         }
         
      } catch (BuilderException e) {
         e.printStackTrace();
         return null;
      }
      sys.closeSegmentList();
      
      MDUtil mdutil = new MDUtil();
      mdutil.reNumberAtom(1, sys);
      
      return sys;
      
   }

   /**
    * Create AtomMD from Atom. Note that parameters intrinsic to AtomMD will 
    * be left undefined (occ, beta, type, vdw, etc.).
    * @param atom Atom to be converted.
    * @return converted AtomMD
    */
   public AtomMD convert(Atom atom) {
      AtomMD atomMD = new AtomMD();
      atomMD.setLabel(atom.getLabel());
      atomMD.setMass(atom.getMass());
      atomMD.setAtomicNum(atom.getAtomicNum());
      atomMD.setXYZCoordinates(atom.getXYZCoordinates());
      atomMD.setAtomicCharge(atom.getAtomicCharge());
      atomMD.setID(atom.getID());
      
      return atomMD;
      
   }
}
