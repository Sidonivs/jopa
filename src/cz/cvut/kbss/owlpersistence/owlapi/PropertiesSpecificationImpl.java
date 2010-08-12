package cz.cvut.kbss.owlpersistence.owlapi;

import java.lang.reflect.Field;

import cz.cvut.kbss.owlpersistence.model.annotations.FetchType;
import cz.cvut.kbss.owlpersistence.model.metamodel.ManagedType;
import cz.cvut.kbss.owlpersistence.model.metamodel.PropertiesSpecification;

public class PropertiesSpecificationImpl<X, Y> implements
		PropertiesSpecification<X, Y> {
	private final ManagedType<X> declaringType;
	private final FetchType fetchType;
	private final Field javaField;
	private Class<Y> javaType;

	public PropertiesSpecificationImpl(final ManagedType<X> declaringType,
			final FetchType fetchType, final Field javaField,
			final Class<Y> javaType) {

		this.declaringType = declaringType;
		this.fetchType = fetchType;
		this.javaField = javaField;
		this.javaType = javaType;
	}

	@Override
	public ManagedType<X> getDeclaringType() {
		return declaringType;
	}

	@Override
	public FetchType getFetchType() {
		return fetchType;
	}

	@Override
	public Field getJavaField() {
		return javaField;
	}

	@Override
	public Class<Y> getJavaType() {
		return javaType;
	}

}
