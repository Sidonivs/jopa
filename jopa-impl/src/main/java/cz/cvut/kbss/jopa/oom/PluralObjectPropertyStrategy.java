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
package cz.cvut.kbss.jopa.oom;

import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import cz.cvut.kbss.jopa.model.metamodel.PluralAttribute;
import cz.cvut.kbss.ontodriver.exception.NotYetImplementedException;
import cz.cvut.kbss.ontodriver.model.Assertion;
import cz.cvut.kbss.ontodriver.model.Axiom;
import cz.cvut.kbss.ontodriver.model.NamedResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

abstract class PluralObjectPropertyStrategy<X> extends FieldStrategy<Attribute<? super X, ?>, X> {

    final PluralAttribute<? super X, ?, ?> pluralAtt;
    private Collection<Object> values;

    public PluralObjectPropertyStrategy(EntityType<X> et, Attribute<? super X, ?> att,
                                        Descriptor descriptor, EntityMappingHelper mapper) {
        super(et, att, descriptor, mapper);
        this.pluralAtt = (PluralAttribute<? super X, ?, ?>) attribute;
        initCollection();
    }

    private void initCollection() {
        switch (pluralAtt.getCollectionType()) {
            case LIST:
                this.values = new ArrayList<>();
                break;
            case COLLECTION:
            case SET:
                this.values = new HashSet<>();
                break;
            default:
                throw new NotYetImplementedException("This type of collection is not supported yet.");
        }
    }

    @Override
    void addValueFromAxiom(Axiom<?> ax) {
        final NamedResource valueIdentifier = (NamedResource) ax.getValue().getValue();
        final Object value = mapper.getEntityFromCacheOrOntology(pluralAtt.getBindableJavaType(),
                valueIdentifier.getIdentifier(), attributeDescriptor);
        values.add(value);

    }

    @Override
    void buildInstanceFieldValue(Object instance) throws IllegalAccessException {
        setValueOnInstance(instance, values.isEmpty() ? null : values);
    }

    @Override
    Assertion createAssertion() {
        return Assertion.createObjectPropertyAssertion(pluralAtt.getIRI().toURI(),
                attribute.isInferred());
    }
}
