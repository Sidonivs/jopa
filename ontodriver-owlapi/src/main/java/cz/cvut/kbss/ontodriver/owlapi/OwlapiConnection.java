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
package cz.cvut.kbss.ontodriver.owlapi;

import cz.cvut.kbss.ontodriver.*;
import cz.cvut.kbss.ontodriver.Properties;
import cz.cvut.kbss.ontodriver.descriptor.AxiomDescriptor;
import cz.cvut.kbss.ontodriver.descriptor.AxiomValueDescriptor;
import cz.cvut.kbss.ontodriver.exception.OntoDriverException;
import cz.cvut.kbss.ontodriver.model.Axiom;
import cz.cvut.kbss.ontodriver.owlapi.exception.OwlapiDriverException;
import cz.cvut.kbss.ontodriver.owlapi.list.OwlapiLists;

import java.net.URI;
import java.util.*;

import static cz.cvut.kbss.ontodriver.util.ErrorUtils.npxMessage;

/**
 * Default implementation of the {@link Connection} interface for OWLAPI driver.
 */
public class OwlapiConnection implements Connection {

    private boolean open;
    private boolean autoCommit;

    private final OwlapiAdapter adapter;

    private OwlapiTypes types;
    private OwlapiProperties properties;
    private OwlapiLists lists;

    private final Set<ConnectionListener> listeners = new HashSet<>(4);

    public OwlapiConnection(OwlapiAdapter adapter) {
        this.adapter = adapter;
        this.open = true;
        this.autoCommit = false;
    }

    void addListener(ConnectionListener listener) {
        assert listener != null;
        listeners.add(listener);
    }

    void removeListener(ConnectionListener listener) {
        assert listener != null;
        listeners.remove(listener);
    }

    void setTypes(OwlapiTypes types) {
        this.types = types;
    }

    void setProperties(OwlapiProperties properties) {
        this.properties = properties;
    }

    void setLists(OwlapiLists lists) {
        this.lists = lists;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void commit() throws OntoDriverException {
        ensureOpen();
        adapter.commit();
    }

    public void ensureOpen() {
        if (!open) {
            throw new IllegalStateException("Connection is closed.");
        }
    }

    @Override
    public void rollback() throws OntoDriverException {
        ensureOpen();
        adapter.rollback();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public Statement createStatement() throws OntoDriverException {
        ensureOpen();
        return adapter.createStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sparql) throws OntoDriverException {
        ensureOpen();
        return adapter.prepareStatement(sparql, this);
    }

    @Override
    public boolean isConsistent(URI context) throws OntoDriverException {
        ensureOpen();
        return adapter.isConsistent(context);
    }

    @Override
    public List<URI> getContexts() throws OntoDriverException {
        ensureOpen();
        return adapter.getContexts();
    }

    @Override
    public boolean contains(Axiom<?> axiom, URI context) throws OntoDriverException {
        ensureOpen();
        Objects.requireNonNull(axiom, npxMessage("axiom"));
        return adapter.containsAxiom(axiom, context);
    }

    @Override
    public Collection<Axiom<?>> find(AxiomDescriptor descriptor) throws OntoDriverException {
        ensureOpen();
        Objects.requireNonNull(descriptor, npxMessage("descriptor"));
        try {
            return adapter.find(descriptor);
        } catch (RuntimeException e) {
            throw new OwlapiDriverException(e);
        }
    }

    @Override
    public void persist(AxiomValueDescriptor descriptor) throws OntoDriverException {
        ensureOpen();
        Objects.requireNonNull(descriptor);
        try {
            adapter.persist(descriptor);
            commitIfAuto();
        } catch (RuntimeException e) {
            throw new OwlapiDriverException(e);
        }
    }

    @Override
    public URI generateIdentifier(URI classUri) throws OntoDriverException {
        ensureOpen();
        Objects.requireNonNull(classUri);
        return adapter.generateIdentifier(classUri);
    }

    @Override
    public void update(AxiomValueDescriptor descriptor) throws OntoDriverException {
        ensureOpen();
        Objects.requireNonNull(descriptor);
        try {
            adapter.update(descriptor);
            commitIfAuto();
        } catch (RuntimeException e) {
            throw new OwlapiDriverException(e);
        }
    }

    @Override
    public void remove(AxiomDescriptor descriptor) throws OntoDriverException {
        ensureOpen();
        Objects.requireNonNull(descriptor);
        try {
            adapter.remove(descriptor);
            commitIfAuto();
        } catch (RuntimeException e) {
            throw new OwlapiDriverException(e);
        }
    }

    @Override
    public Lists lists() {
        ensureOpen();
        assert lists != null;
        return lists;
    }

    @Override
    public Types types() {
        ensureOpen();
        assert types != null;
        return types;
    }

    @Override
    public Properties properties() {
        ensureOpen();
        assert properties != null;
        return properties;
    }

    @Override
    public void close() throws Exception {
        if (!open) {
            return;
        }
        listeners.stream().forEach(listener -> listener.connectionClosed(this));
        this.open = false;
    }

    public void commitIfAuto() throws OntoDriverException {
        if (autoCommit) {
            adapter.commit();
        }
    }
}
