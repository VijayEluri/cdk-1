/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-07-31 22:06:39 +0200 (Mon, 31 Jul 2006) $    
 * $Revision: 6715 $
 * 
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.interfaces;

import java.util.Collection;

/**
 * A PDBPolymer is a subclass of a BioPolymer which is supposed to store
 * additional informations about the BioPolymer which are connected to BioPolymers.
 *
 * @cdk.module  interfaces
 *
 * @author      Miguel Rojas <miguel.rojas@uni-koeln.de>
 * @cdk.created 2006-11-20 
 *
 * @cdk.keyword polymer
 * @cdk.keyword biopolymer
 * @cdk.keyword pdbpolymer
 */
public interface IPDBPolymer extends IBioPolymer {

	/**
	 * Adds the PDBStructure structure a this PDBPolymer.
	 *
	 * @param structure  The PDBStructure to add
	 */
	public void addStructure(IPDBStructure structure);
	/**
	 * Returns a Collection containing the PDBStructure in the PDBPolymer.
	 * 
	 * @return Collection containing the PDBStructure in the PDBPolymer
	 */
	public Collection getStructures();
	
}
