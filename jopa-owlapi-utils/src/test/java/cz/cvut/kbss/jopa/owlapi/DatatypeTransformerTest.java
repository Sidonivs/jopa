package cz.cvut.kbss.jopa.owlapi;

import cz.cvut.kbss.ontodriver.model.LangString;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatatypeTransformerTest {

    private static final OWLDataFactory DATA_FACTORY = new OWLDataFactoryImpl();

    @Test
    void transformOwlLiteralToObjectReturnsOntoDriverLangStringForRdfLangString() {
        final OWLLiteral literal = DATA_FACTORY.getOWLLiteral("test", "en");
        final Object result = DatatypeTransformer.transform(literal);
        assertThat(result, instanceOf(LangString.class));
        final LangString lsResult = (LangString) result;
        assertEquals(literal.getLiteral(), lsResult.getValue());
        assertTrue(lsResult.getLanguage().isPresent());
        assertEquals(literal.getLang(), lsResult.getLanguage().get());
    }

    @Test
    void transformOwlLiteralToObjectReturnsStringForSimpleLiteral() {
        final OWLLiteral literal = DATA_FACTORY.getOWLLiteral("test");
        final Object result = DatatypeTransformer.transform(literal);
        assertThat(result, instanceOf(String.class));
        assertEquals(literal.getLiteral(), result);
    }

    @Test
    void transformObjectToOwlLiteralReturnsRdfLangStringForOntoDriverLangStringWithLanguage() {
        final LangString ls = new LangString("test", "en");
        final OWLLiteral result = DatatypeTransformer.transform(ls, null);
        assertEquals(OWL2Datatype.RDF_LANG_STRING.getDatatype(DATA_FACTORY), result.getDatatype());
        assertEquals(ls.getValue(), result.getLiteral());
        assertEquals(ls.getLanguage().get(), result.getLang());
    }
    @Test
    void transformObjectToOwlLiteralReturnsSimpleLiteralForOntoDriverLangStringWithoutLanguage() {
        final LangString ls = new LangString("test");
        final OWLLiteral result = DatatypeTransformer.transform(ls, null);
        assertEquals(OWL2Datatype.XSD_STRING.getDatatype(DATA_FACTORY), result.getDatatype());
        assertEquals(ls.getValue(), result.getLiteral());
        assertFalse(result.hasLang());
        assertTrue(result.getLang().isEmpty());
    }
}
