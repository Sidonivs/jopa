package cz.cvut.kbss.jopa.test.remote.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.jopa.vocabulary.SKOS;

import java.net.URI;
import java.util.Set;

@Namespace(prefix = "pdp", namespace = "http://onto.fel.cvut.cz/ontologies/slovn\u00edk/agendov\u00fd/popis-dat/pojem/")
@OWLClass(iri = SKOS.CONCEPT)
public class Term {

    @Id
    private URI uri;

    @OWLDataProperty(iri = SKOS.PREF_LABEL)
    private MultilingualString label;

    @OWLDataProperty(iri = SKOS.DEFINITION)
    private MultilingualString definition;

    @OWLDataProperty(iri = SKOS.SCOPE_NOTE)
    private MultilingualString description;

    @OWLObjectProperty(iri = SKOS.BROADER)
    private Set<Term> parents;

    @Sparql(query = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
            "SELECT ?child WHERE { ?this skos:narrower ?child . }", fetchType = FetchType.EAGER)
    private Set<TermInfo> children;

    @Sparql(query = "ASK { ?glossary <http://www.w3.org/2004/02/skos/core#hasTopConcept> ?this . }", fetchType = FetchType.EAGER)
    private Boolean root;

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

    public MultilingualString getDefinition() {
        return definition;
    }

    public void setDefinition(MultilingualString definition) {
        this.definition = definition;
    }

    public MultilingualString getDescription() {
        return description;
    }

    public void setDescription(MultilingualString description) {
        this.description = description;
    }

    public Set<Term> getParents() {
        return parents;
    }

    public void setParents(Set<Term> parents) {
        this.parents = parents;
    }

    public Set<TermInfo> getChildren() {
        return children;
    }

    public void setChildren(Set<TermInfo> children) {
        this.children = children;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public URI getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(URI vocabulary) {
        this.vocabulary = vocabulary;
    }
}
