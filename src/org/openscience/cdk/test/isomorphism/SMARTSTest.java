/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.isomorphism;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test
 * @cdk.require java1.4
 */
public class SMARTSTest extends TestCase {
	
	public SMARTSTest(String name) {
		super(name);
	}
    
	public static Test suite() {
		return new TestSuite(SMARTSTest.class);
	}

	public void testStrictSMARTS() throws Exception {
		SmilesParser sp = new SmilesParser();
        AtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        QueryAtomContainer query = new QueryAtomContainer();
        SMARTSAtom atom1 = new SMARTSAtom();
        atom1.setSymbol("N");
        SMARTSAtom atom2 = new SMARTSAtom();
        atom1.setSymbol("C");
        query.addAtom(atom1);
        query.addAtom(atom2);
        query.addBond(new OrderQueryBond(atom1, atom2, 2));
        
        assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }
	
	public void testSMARTS() throws Exception {
		SmilesParser sp = new SmilesParser();
        AtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C"); // acetic acid anhydride
        QueryAtomContainer query = new QueryAtomContainer();
        SMARTSAtom atom1 = new SMARTSAtom();
        atom1.setLabel("*");
        SMARTSAtom atom2 = new SMARTSAtom();
        atom2.setSymbol("C");
        query.addAtom(atom1);
        query.addAtom(atom2);
        query.addBond(new OrderQueryBond(atom1, atom2, 2));
        
        assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }
	
}

