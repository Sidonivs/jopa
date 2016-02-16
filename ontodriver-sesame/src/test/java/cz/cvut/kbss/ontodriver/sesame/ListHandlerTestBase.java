/**
 * Copyright (C) 2011 Czech Technical University in Prague
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
package cz.cvut.kbss.ontodriver.sesame;

import cz.cvut.kbss.ontodriver.model.NamedResource;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.ArrayList;
import java.util.List;

class ListHandlerTestBase {

	protected static final NamedResource OWNER = NamedResource
			.create("http://krizik.felk.cvut.cz/ontologies/jopa/entityC");

	protected static final String LIST_PROPERTY = "http://krizik.felk.cvut.cz/ontologies/jopa/attributes#C-hasSequence";
	protected static final String NEXT_NODE_PROPERTY = "http://krizik.felk.cvut.cz/ontologies/jopa/attributes#C-hasNext";


	protected static ValueFactory vf;
	protected static Repository repo;
	protected static Resource owner;

	protected static void init() throws Exception {
		final MemoryStore mStore = new MemoryStore();
		repo = new SailRepository(mStore);
		repo.initialize();
		vf = repo.getValueFactory();
		owner = vf.createURI(OWNER.toString());
	}

	protected static void close() throws RepositoryException {
		repo.shutDown();
	}

	protected List<NamedResource> initList() {
		final List<NamedResource> lst = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			lst.add(NamedResource.create("http://krizik.felk.cvut.cz/ontologies/jopa/elem" + i));
		}
		return lst;
	}
}
