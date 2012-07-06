package cz.cvut.kbss.jopa.accessors;

import java.util.Map;

import cz.cvut.kbss.jopa.accessors.OntologyAccessor;
import cz.cvut.kbss.jopa.accessors.OntologyAccessorFactory;
import cz.cvut.kbss.jopa.model.metamodel.Metamodel;
import cz.cvut.kbss.jopa.owlapi.OWLAPIPersistenceProperties;
import cz.cvut.kbss.jopa.sessions.Session;

public class OntologyAccessorFactoryImpl implements OntologyAccessorFactory {

	public OntologyAccessor createOntologyAccessor(
			Map<String, String> properties, Metamodel metamodel, Session session) {
		final String physicalURI = properties
				.get(OWLAPIPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY);
		if (physicalURI == null || !physicalURI.startsWith("jdbc:")) {
			return new OWLFileOntologyAccessor(properties, metamodel, session);
		} else {
			return new OWLDBOntologyAccessor(properties, metamodel, session);
		}
		// If new types of ontology access are added (besides OWLAPI), this
		// method will need to be refactored to fit the new strategy
	}

}
