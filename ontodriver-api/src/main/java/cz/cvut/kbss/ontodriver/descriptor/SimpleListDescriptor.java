/**
 * Copyright (C) 2020 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.ontodriver.descriptor;


/**
 * Represents a simple LIPS-style sequence, which is basically a singly-linked list.
 * <p>
 * Each node in the list is subject to a statement referencing the next node.
 */
public interface SimpleListDescriptor extends ListDescriptor {

    // We are just exporting the API from ListDescriptor, as ReferencedListDescriptor is not a SimpleListDescriptor,
    // but they share the basic common methods.
    // This way, we can declare the methods only once, but the list descriptor have each their own hierarchy.s
}