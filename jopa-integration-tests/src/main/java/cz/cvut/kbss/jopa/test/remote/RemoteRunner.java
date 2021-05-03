package cz.cvut.kbss.jopa.test.remote;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.test.remote.model.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RemoteRunner {

    public RemoteRunner() {
        PersistenceFactory.init(null);
    }

    @Test
    public void loadLocalityTermTest() {
        EntityManager em = PersistenceFactory.createEntityManager();
        final Term localityTerm = em.find(Term.class, "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/lokalita");
        System.out.println("LOCALITY TERM URI: " + localityTerm.getUri());
        System.out.println("LOCALITY TERM DEFINITION: " + localityTerm.getDefinition());
        System.out.println("LOCALITY TERM DESCRIPTION: " + localityTerm.getDescription());
        System.out.println(localityTerm.getRoot());
    }
}
