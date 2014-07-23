package cz.cvut.kbss.ontodriver;

import java.net.URI;

import cz.cvut.kbss.ontodriver.exceptions.OntoDriverException;

public interface DriverFactory extends Closeable {

	/**
	 * Creates and returns storage module. </p>
	 * 
	 * Implementations may choose to pool storage modules or create lazy loaded
	 * proxies.
	 * 
	 * @param persistenceProvider
	 *            Facade representing the persistence provider
	 * @return StorageModule
	 * @throws OntoDriverException
	 *             If called on a closed factory or if an ontology access error
	 *             occurs
	 */
	public StorageModule createStorageModule(PersistenceProviderFacade persistenceProvider)
			throws OntoDriverException;

	/**
	 * Releases the specified storage module. </p>
	 * 
	 * Releasing the module can mean closing it as well as just returning it
	 * back to the module pool. This depends on the factory implementation. </p>
	 * 
	 * Releasing a module twice results in an exception.
	 * 
	 * @param module
	 *            The module to release
	 * @throws OntoDriverException
	 *             If called on a closed factory, if the module has been already
	 *             released or if an ontology access error occurs
	 */
	public void releaseStorageModule(StorageModule module) throws OntoDriverException;

	/**
	 * Creates and returns a storage connector. </p>
	 * 
	 * Implementations may choose to pool storage modules or create lazy loaded
	 * proxies.
	 * 
	 * @return StorageConnector
	 * @throws OntoDriverException
	 *             If called on a closed factory or if an ontology access error
	 *             occurs
	 */
	public StorageConnector createStorageConnector() throws OntoDriverException;

	/**
	 * Releases the specified storage connector. </p>
	 * 
	 * Releasing the connector can mean closing it as well as just returning it
	 * back to the connector pool. This depends on the factory implementation.
	 * </p>
	 * 
	 * Releasing a connector twice results in an exception.
	 * 
	 * @param connector
	 *            The connector to release
	 * @throws OntoDriverException
	 *             If called on a closed factory, if the module has been already
	 *             released or if an ontology access error occurs
	 */
	public void releaseStorageConnector(StorageConnector connector) throws OntoDriverException;

	/**
	 * Creates internal OntoDriver statement used for SPARQL processing.
	 * 
	 * @param statement
	 *            The statement received from JOPA
	 * @return Internal statement object
	 * @throws OntoDriverException
	 *             If called on a closed factory
	 */
	public DriverStatement createStatement(JopaStatement statement) throws OntoDriverException;

	/**
	 * Gets the logical URI of the underlying ontology. </p>
	 * 
	 * Note that the ontology may not have a logical URI, e. g. Sesame
	 * repositories don't have logical URIs.
	 * 
	 * @return Logical URI or {@code null}
	 */
	public URI getOntologyUri();

	/**
	 * Gets the physical URI of the underlying ontology. </p>
	 * 
	 * This can be a remote URL or a local file URI.
	 * 
	 * @return Physical URI
	 */
	public URI getPhysicalUri();
}
