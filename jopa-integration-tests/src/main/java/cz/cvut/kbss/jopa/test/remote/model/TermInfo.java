package cz.cvut.kbss.jopa.test.remote.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.jopa.vocabulary.SKOS;

import java.net.URI;

@Namespace(prefix = "pdp", namespace = "http://onto.fel.cvut.cz/ontologies/slovn\u00edk/agendov\u00fd/popis-dat/pojem/")
@OWLClass(iri = SKOS.CONCEPT)
public class TermInfo {

    @Id
    private URI uri;

    @ParticipationConstraints(nonEmpty = true)
    @OWLAnnotationProperty(iri = SKOS.PREF_LABEL)
    private MultilingualString label;

    @Inferred
    @OWLObjectProperty(iri = "pdp:je-pojmem-ze-slovn\u00edku")
    private URI vocabulary;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public MultilingualString getLabel() {
        return label;
    }

    public void setLabel(MultilingualString label) {
        this.label = label;
    }

    public URI getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary) {
        this.vocabulary = vocabulary;
    }
}
