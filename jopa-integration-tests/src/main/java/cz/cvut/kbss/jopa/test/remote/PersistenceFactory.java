package cz.cvut.kbss.jopa.test.remote;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.config.OntoDriverProperties;
import cz.cvut.kbss.ontodriver.sesame.SesameDataSource;
import cz.cvut.kbss.ontodriver.sesame.config.SesameOntoDriverProperties;

import java.util.HashMap;
import java.util.Map;

public class PersistenceFactory {

    private static boolean initialized = false;

    private static EntityManagerFactory emf;

    private PersistenceFactory() {
        throw new AssertionError();
    }

    public static void init(Map<String, String> properties) {
        final Map<String, String> props = new HashMap<>();
        // Here we set up basic storage access properties - driver class, physical location of the storage
        props.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, "https://graphdb.onto.fel.cvut.cz/repositories/termit-dev");
        props.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, SesameDataSource.class.getName());
        // View transactional changes during transaction
        props.put(OntoDriverProperties.USE_TRANSACTIONAL_ONTOLOGY, Boolean.TRUE.toString());
        // Ontology language
        props.put(JOPAPersistenceProperties.LANG, "en");
        if (properties != null) {
            props.putAll(properties);
        }
        // Persistence provider name
        props.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        //The package in which entity declarations reside
        props.put(JOPAPersistenceProperties.SCAN_PACKAGE, "cz.cvut.kbss.jopa.test.remote.model");
        // Don't use Sesame inference
        //props.put(SesameOntoDriverProperties.SESAME_USE_INFERENCE, Boolean.FALSE.toString());

        emf = Persistence.createEntityManagerFactory("jopaRemoteTermitExample", props);
        initialized = true;
    }

    public static EntityManager createEntityManager() {
        if (!initialized) {
            throw new IllegalStateException("Factory has not been initialized.");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        emf.close();
    }
}
