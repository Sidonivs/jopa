/**
 * Copyright (C) 2020 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.ontodriver.sesame.connector;

import cz.cvut.kbss.ontodriver.config.DriverConfiguration;
import cz.cvut.kbss.ontodriver.sesame.config.SesameConfigParam;
import cz.cvut.kbss.ontodriver.sesame.environment.Generator;
import cz.cvut.kbss.ontodriver.sesame.environment.TestUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectorFactoryImplTest {

    private ConnectorFactory factory;

    @AfterEach
    public void tearDown() throws Exception {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }

    @Test
    public void setRepositoryConnectsUninitializedFactory() throws Exception {
        this.factory = new ConnectorFactoryImpl();
        final DriverConfiguration config = TestUtils.createDriverConfig("urn:test");
        config.setProperty(SesameConfigParam.USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        final Repository repo = new SailRepository(new MemoryStore());
        Generator.initTestData(repo);
        try {
            factory.setRepository(repo, config);
            final Connector connector = factory.createStorageConnector(config);
            connector.begin();
            assertFalse(connector.findStatements(null, null, null, false).isEmpty());
            connector.close();
        } finally {
            repo.shutDown();
        }
    }

    @Test
    public void setRepositoryThrowsIllegalStateWhenCalledOnClosedFactory() throws Exception {
        this.factory = new ConnectorFactoryImpl();
        final DriverConfiguration config = TestUtils.createDriverConfig("urn:test");
        config.setProperty(SesameConfigParam.USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        factory.close();
        final Repository repo = new SailRepository(new MemoryStore());
        try {
            assertThrows(IllegalStateException.class, () -> factory.setRepository(repo, config));
        } finally {
            repo.shutDown();
        }
    }

    @Test
    public void createConnectorInitializesRepository() throws Exception {
        this.factory = new ConnectorFactoryImpl();
        final DriverConfiguration config = TestUtils.createDriverConfig("urn:test");
        config.setProperty(SesameConfigParam.USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        final Connector connector = factory.createStorageConnector(config);
        assertNotNull(connector);
        assertTrue(connector.unwrap(Repository.class).isInitialized());
    }
}
