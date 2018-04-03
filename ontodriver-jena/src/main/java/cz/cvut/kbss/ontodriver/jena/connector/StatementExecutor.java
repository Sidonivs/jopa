package cz.cvut.kbss.ontodriver.jena.connector;

import cz.cvut.kbss.ontodriver.ResultSet;
import cz.cvut.kbss.ontodriver.jena.exception.JenaDriverException;
import cz.cvut.kbss.ontodriver.jena.query.AbstractResultSet;
import org.apache.jena.query.Query;

public interface StatementExecutor {

    /**
     * Executes the specified SPARQL SELECT query, returning the Jena ARQ result set.
     *
     * @param query Query to execute
     * @return ARQ result set
     * @throws JenaDriverException If query execution fails
     */
    AbstractResultSet executeSelectQuery(Query query) throws JenaDriverException;

    /**
     * Executes the specified SPARQL ASK query.
     *
     * @param query Query to execute
     * @return ASK result
     * @throws JenaDriverException If query execution fails
     */
    AbstractResultSet executeAskQuery(Query query) throws JenaDriverException;

    /**
     * Executes the specified SPARQL 1.1 Update query.
     *
     * @param query Query to execute
     * @throws JenaDriverException If query execution fails
     */
    void executeUpdate(String query) throws JenaDriverException;
}
