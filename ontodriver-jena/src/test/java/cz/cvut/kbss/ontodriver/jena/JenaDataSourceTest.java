package cz.cvut.kbss.ontodriver.jena;

import cz.cvut.kbss.ontodriver.Connection;
import cz.cvut.kbss.ontodriver.OntologyStorageProperties;
import cz.cvut.kbss.ontodriver.jena.config.JenaOntoDriverProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class JenaDataSourceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OntologyStorageProperties storageProps;
    private final Map<String, String> properties = new HashMap<>();

    private JenaDataSource dataSource = new JenaDataSource();

    @Before
    public void setUp() {
        this.storageProps = OntologyStorageProperties.driver(JenaDataSource.class.getName())
                                                     .physicalUri(URI.create("temp:memory")).build();
        properties.put(JenaOntoDriverProperties.JENA_STORAGE_TYPE, JenaOntoDriverProperties.IN_MEMORY);
        properties.put(JenaOntoDriverProperties.JENA_ISOLATION_STRATEGY, JenaOntoDriverProperties.READ_COMMITTED);
    }

    @Test
    public void getConnectionAcquiresConnectionToUnderlyingStorage() {
        dataSource.setStorageProperties(storageProps);
        dataSource.setProperties(properties);
        final Connection connection = dataSource.getConnection();
        assertNotNull(connection);
        assertTrue(dataSource.isOpen());
        assertTrue(connection.isOpen());
    }

    @Test
    public void getConnectionThrowsIllegalStateWhenDataSourceIsClosed() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(containsString("data source is closed"));
        dataSource.close();
        dataSource.getConnection();
    }

    @Test
    public void closeClosesUnderlyingDriver() throws Exception {
        dataSource.setStorageProperties(storageProps);
        dataSource.setProperties(properties);
        final Connection connection = dataSource.getConnection();
        dataSource.close();
        // Closing driver closes connections
        assertFalse(connection.isOpen());
    }

    @Test
    public void connectingWithoutStoragePropertiesThrowsIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(containsString("cannot connect without ontology storage properties"));
        dataSource.getConnection();
    }
}