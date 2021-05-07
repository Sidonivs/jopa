package cz.cvut.kbss.jopa.test.remote;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.test.remote.model.Term;
import cz.cvut.kbss.jopa.test.remote.model.TermInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RemoteRunner {

    public RemoteRunner() {
        PersistenceFactory.init(null);
    }

    @Test
    public void localityTermTest() {
        EntityManager em = PersistenceFactory.createEntityManager();

        final Term localityTerm = em.find(Term.class, "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/lokalita");

        assertEquals(localityTerm.getUri().toString(), "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/lokalita");
        assertEquals(localityTerm.getChildren().size(), 1);
        for (TermInfo termInfo : localityTerm.getChildren()) {
            assertEquals(termInfo.getUri().toString(), "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/stabilizovana-chranena-lokalita");
        }
        assertEquals(localityTerm.getRoot(), true);
    }

    @Test
    public void documentationTermTest() {
        EntityManager em = PersistenceFactory.createEntityManager();

        final Term term = em.find(Term.class, "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/dokumentace");

        assertEquals(term.getUri().toString(), "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/dokumentace");
        assertEquals(term.getChildren().size(), 1);
        for (TermInfo termInfo : term.getChildren()) {
            assertEquals(termInfo.getUri().toString(), "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/term-six");
        }
        assertEquals(term.getRoot(), true);
    }

    @Test
    public void stabilizedProtectedLocalityTermTest() {
        EntityManager em = PersistenceFactory.createEntityManager();

        final Term term = em.find(Term.class, "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/stabilizovana-chranena-lokalita");

        assertEquals(term.getUri().toString(), "http://onto.fel.cvut.cz/ontologies/slovnik/ml-test/pojem/stabilizovana-chranena-lokalita");
        assertNull(term.getChildren());
        assertEquals(term.getRoot(), false);
    }
}
