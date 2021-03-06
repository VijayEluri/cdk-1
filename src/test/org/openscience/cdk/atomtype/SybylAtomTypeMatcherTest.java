/* $Revision: 11293 $ $Author: rajarshi $ $Date: 2008-06-06 22:46:01 +0200 (Fri, 06 Jun 2008) $
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.atomtype;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * This class tests the perception of Sybyl atom types, which uses
 * CDK atom type perception and mapping of CDK atom types to Sybyl
 * atom types.
 *
 * @cdk.module test-atomtype
 */
public class SybylAtomTypeMatcherTest extends AbstractSybylAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    static {
        // do not complain about a few non-tested atom types
        // so, just mark them as tested
        testedAtomTypes.put("LP", 1);
        testedAtomTypes.put("Du", 1);
        testedAtomTypes.put("Du.C", 1);
        testedAtomTypes.put("Any", 1);
        testedAtomTypes.put("Hal", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("Hev", 1);
        testedAtomTypes.put("X", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("H.t3p", 1);
        testedAtomTypes.put("H.spc", 1);
        testedAtomTypes.put("O.t3p", 1);
        testedAtomTypes.put("O.spc", 1);
    }

	@Test public void testGetInstance_IChemObjectBuilder() {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(
		    NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
	}
	
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(
		    NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
		Molecule ethane = MoleculeFactory.makeAlkane(2);
		String[] expectedTypes = {"C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, ethane);
	}

  @Test public void testFindMatchingAtomType_IAtomContainer() throws Exception {
      String filename = "data/mol2/atomtyping.mol2";
      InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
      Mol2Reader reader = new Mol2Reader(ins);
      IMolecule mol = (IMolecule)reader.read(new Molecule());

      // just check consistency; other methods do perception testing
      SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(
          DefaultChemObjectBuilder.getInstance());
      IAtomType[] types = matcher.findMatchingAtomType(mol);
      for (int i=0; i<types.length; i++) {
          IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
          Assert.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
      }
  }

    @Test public void testAtomTyping() throws Exception {
        String filename = "data/mol2/atomtyping.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtoms.next().getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    /**
     * Uses findMatchingAtomType(IAtomContainer, IAtom) type.
     */
    @Test public void testBenzene() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomType(benzene);
        for (IAtomType type : types) {
            Assert.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

    @Test public void testAdenine() throws Exception {
        IMolecule mol = MoleculeFactory.makeAdenine();
          String[] expectedTypes = {"C.ar", "C.ar", "C.ar", "N.ar", "N.ar", "N.ar",
            "N.ar", "N.3", "C.ar", "C.ar"
          };
          SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(mol.getBuilder());
          IAtomType[] types = matcher.findMatchingAtomType(mol);
          for (int i=0; i<expectedTypes.length; i++) {
              assertAtomType(testedAtomTypes,
                  "Incorrect perception for atom " + i,
                  expectedTypes[i], types[i]
              );
          }
      }

    /**
     * Uses findMatchingAtomType(IAtomContainer) type.
     */
    @Test public void testBenzene_AtomContainer() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomType(benzene);
        for (IAtomType type : types) {
          Assert.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

	@Test public void testAtomTyping4() throws Exception {
        String filename = "data/mol2/atomtyping4.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

	/**
	 * @cdk.bug 2445178
	 */
	@Test public void testNonExistingType() throws Exception {
	    IMolecule mol = new Molecule();
	    IAtom atom = new Atom("Error");
	    mol.addAtom(atom);
	    SybylAtomTypeMatcher matcher =
	        SybylAtomTypeMatcher.getInstance(mol.getBuilder());
	    Assert.assertNull(matcher.findMatchingAtomType(mol, atom));
	}

	@Test public void testAtomTyping2() throws Exception {
        String filename = "data/mol2/atomtyping2.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();

        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
		Assert.assertEquals(
			"Perceived atom type does not match atom type in file",
			refAtom.getAtomTypeName(),
			atoms.next().getAtomTypeName()
		);
        }
    }

    @Test public void testAtomTyping3() throws Exception {
        String filename = "data/mol2/atomtyping3.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	IAtom refAtom = refAtoms.next();
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    private void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws Exception {
    	SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
        	IAtom atom = atoms.next();
        	atom.setAtomTypeName(null);
        	IAtomType matched = matcher.findMatchingAtomType(container, atom);
        	if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
	}

    @Test public void testForDuplicateDefinitions() {
    	super.testForDuplicateDefinitions();
    }

    @Test public void testDummy() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        String[] expectedTypes = {"X"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testEthene() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.2", "C.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testImine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.2", "N.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testPropyne() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.1", "C.1", "C.3"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHalogenatedMethane() throws Exception {
        IMolecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("I"));
        mol.addAtom(new Atom("Br"));
        mol.addBond(0,1,IBond.Order.SINGLE);
        mol.addBond(0,2,IBond.Order.SINGLE);
        mol.addBond(0,3,IBond.Order.SINGLE);
        mol.addBond(0,4,IBond.Order.SINGLE);

        String[] expectedTypes = {"C.3", "F", "Cl", "I", "Br"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMnF4() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Mn");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        mol.addAtom(atom); atom.setFormalCharge(-1);
        mol.addAtom(atom2); atom2.setFormalCharge(+2);
        mol.addAtom(atom3); atom3.setFormalCharge(-1);
        mol.addAtom(atom4); atom4.setFormalCharge(-1);
        mol.addAtom(atom5); atom5.setFormalCharge(-1);

        String[] expectedTypes = {"F", "Mn", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testAmide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.2", "C.2", "N.am"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMethylAmine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"N.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testAmmonia() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        IAtom atom5 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"H", "N.4", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMethanol() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testDMSO() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.2", "S.O", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testDMSOO() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.2", "O.2", "S.O2", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testCarbokation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("C"); atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"H", "C.cat", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    @Test public void testSilicon() throws Exception {
    	IMolecule mol = new Molecule();
    	IAtom a1 = mol.getBuilder().newInstance(IAtom.class,"Si"); mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newInstance(IAtom.class,"O"); mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newInstance(IAtom.class,"O"); mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newInstance(IAtom.class,"O"); mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newInstance(IAtom.class,"C"); mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newInstance(IAtom.class,"C"); mol.addAtom(a6);
    	IAtom a7 = mol.getBuilder().newInstance(IAtom.class,"C"); mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a8);
    	IAtom a9 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a9);
    	IAtom a10 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a10);
    	IAtom a11 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a11);
    	IAtom a12 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a12);
    	IAtom a13 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a13);
    	IAtom a14 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a14);
    	IAtom a15 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a15);
    	IAtom a16 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a16);
    	IAtom a17 = mol.getBuilder().newInstance(IAtom.class,"H"); mol.addAtom(a17);
    	IBond b1 = mol.getBuilder().newInstance(IBond.class,a1, a2, IBond.Order.SINGLE); mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newInstance(IBond.class,a1, a3, IBond.Order.SINGLE); mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newInstance(IBond.class,a1, a4, IBond.Order.SINGLE); mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newInstance(IBond.class,a2, a5, IBond.Order.SINGLE); mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newInstance(IBond.class,a3, a6, IBond.Order.SINGLE); mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newInstance(IBond.class,a4, a7, IBond.Order.SINGLE); mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newInstance(IBond.class,a5, a8, IBond.Order.SINGLE); mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newInstance(IBond.class,a5, a9, IBond.Order.SINGLE); mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newInstance(IBond.class,a5, a10, IBond.Order.SINGLE); mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newInstance(IBond.class,a6, a11, IBond.Order.SINGLE); mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newInstance(IBond.class,a6, a12, IBond.Order.SINGLE); mol.addBond(b11);
    	IBond b12 = mol.getBuilder().newInstance(IBond.class,a6, a13, IBond.Order.SINGLE); mol.addBond(b12);
    	IBond b13 = mol.getBuilder().newInstance(IBond.class,a7, a14, IBond.Order.SINGLE); mol.addBond(b13);
    	IBond b14 = mol.getBuilder().newInstance(IBond.class,a7, a15, IBond.Order.SINGLE); mol.addBond(b14);
    	IBond b15 = mol.getBuilder().newInstance(IBond.class,a7, a16, IBond.Order.SINGLE); mol.addBond(b15);
    	IBond b16 = mol.getBuilder().newInstance(IBond.class,a1, a17, IBond.Order.SINGLE); mol.addBond(b16);

    	String[] expectedTypes = {"Si", "O.3", "O.3", "O.3", "C.3", "C.3", "C.3",
    			"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testThioAmide() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"S.2", "C.2", "N.am"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testSalts() throws Exception {
        IMolecule mol = new Molecule();
          
          IAtom atom = new Atom("Na");
          atom.setFormalCharge(+1);
          mol.addAtom(atom);
          String[] expectedTypes = new String[]{"Na"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
          
          mol = new Molecule();
          atom = new Atom("K");
          atom.setFormalCharge(+1);
          mol.addAtom(atom);
          expectedTypes = new String[]{"K"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
          
          mol = new Molecule();
          atom = new Atom("Ca");
          atom.setFormalCharge(+2);
          mol.addAtom(atom);
          expectedTypes = new String[]{"Ca"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
          
          mol = new Molecule();
          atom = new Atom("Mg");
          atom.setFormalCharge(+2);
          mol.addAtom(atom);
          expectedTypes = new String[]{"Mg"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
          
          mol = new Molecule();
          atom = new Atom("Cu");
          atom.setFormalCharge(+2);
          mol.addAtom(atom);
          expectedTypes = new String[]{"Cu"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);

          mol = new Molecule();
          atom = new Atom("Al");
          atom.setFormalCharge(+3);
          mol.addAtom(atom);
          expectedTypes = new String[]{"Al"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testH2S() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class,s, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class,s, h2, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"S.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testFerrocene() throws Exception {
        IAtomContainer ferrocene = new Molecule();
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C")); ferrocene.getAtom(4).setFormalCharge(-1);
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C")); ferrocene.getAtom(9).setFormalCharge(-1);
        ferrocene.addAtom(new Atom("Fe")); ferrocene.getAtom(10).setFormalCharge(+2);
        ferrocene.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        ferrocene.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        ferrocene.addBond(2,3,CDKConstants.BONDORDER_DOUBLE);
        ferrocene.addBond(3,4,CDKConstants.BONDORDER_SINGLE);
        ferrocene.addBond(4,0,CDKConstants.BONDORDER_SINGLE);
        ferrocene.addBond(5,6,CDKConstants.BONDORDER_DOUBLE);
        ferrocene.addBond(6,7,CDKConstants.BONDORDER_SINGLE);
        ferrocene.addBond(7,8,CDKConstants.BONDORDER_DOUBLE);
        ferrocene.addBond(8,9,CDKConstants.BONDORDER_SINGLE);
        ferrocene.addBond(9,5,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = new String[]{
                "C.2","C.2","C.2","C.2","Any",
                "C.2","C.2","C.2","C.2","Any",
                "Fe"
        };
        assertAtomTypes(testedAtomTypes, expectedTypes, ferrocene);
    }

    @Test public void testHCN() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);

        String[] expectedTypes = {"N.1", "C.1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testAniline() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();
        IAtom nitrogen = benzene.getBuilder().newInstance(IAtom.class,"N");
        benzene.addAtom(nitrogen);
        benzene.addBond(benzene.getBuilder().newInstance(IBond.class,benzene.getAtom(0), nitrogen, IBond.Order.SINGLE));

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomType(benzene);
        for (int i=0; i<6; i++) {
            assertAtomType(testedAtomTypes,
                "Incorrect perception for atom " + i,
                "C.ar", types[i]
            );
        }
        assertAtomType(testedAtomTypes,
            "Incorrect perception for atom " + 6,
            "N.pl3", types[6]
        );
    }

    @Test public void testLithiumMethanoxide() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("Li");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(0,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.3", "C.3", "Li"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testTinCompound() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Sn");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.3", "Sn", "C.3", "C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testZincChloride() throws Exception {
        IMolecule mol = new Molecule();
        mol.addAtom(new Atom("Zn"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
        mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"Zn", "Cl", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/H2Se/h1H2
     */
    @Test public void testH2Se() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"Se");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class,se, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class,se, h2, IBond.Order.SINGLE);

        mol.addAtom(se);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"Se", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testPhosphate() throws Exception {
        IMolecule mol = new Molecule();
          IAtom atom = new Atom("O");
          IAtom atom2 = new Atom("P");
          IAtom atom3 = new Atom("O");
          IAtom atom4 = new Atom("O");
          IAtom atom5 = new Atom("O");
          mol.addAtom(atom);
          mol.addAtom(atom2);
          mol.addAtom(atom3);
          mol.addAtom(atom4);
          mol.addAtom(atom5);
          mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
          mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
          mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
          mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

          String[] expectedTypes = {"O.2", "P.3", "O.3", "O.3", "O.3"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
      }

    @Test public void countTestedAtomTypes() {
        super.countTestedAtomTypes(testedAtomTypes);
    }

}
