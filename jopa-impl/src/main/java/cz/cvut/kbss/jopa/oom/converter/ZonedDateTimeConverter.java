/**
 * Copyright (C) 2020 Czech Technical University in Prague
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
package cz.cvut.kbss.jopa.oom.converter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Converts between Java 8 {@link ZonedDateTime} and {@link Date} used by OntoDriver and the underlying repository
 * access frameworks (RDF4J, Jena, OWLAPI).
 */
public class ZonedDateTimeConverter implements ConverterWrapper<ZonedDateTime, Date> {

    @Override
    public Date convertToAxiomValue(ZonedDateTime value) {
        return value != null ? Date.from(value.toInstant()) : null;
    }

    @Override
    public ZonedDateTime convertToAttribute(Date value) {
        return value != null ? value.toInstant().atZone(ZoneId.systemDefault()) : null;
    }

    @Override
    public boolean supportsAxiomValueType(Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }
}
