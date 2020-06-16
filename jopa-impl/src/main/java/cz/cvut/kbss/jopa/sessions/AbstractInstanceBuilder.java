/**
 * Copyright (C) 2020 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cvut.kbss.jopa.sessions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.PrivilegedActionException;

abstract class AbstractInstanceBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractInstanceBuilder.class);

    protected boolean populates;
    protected final CloneBuilderImpl builder;
    protected final UnitOfWork uow;

    AbstractInstanceBuilder(CloneBuilderImpl builder, UnitOfWork uow) {
        this.builder = builder;
        this.uow = uow;
        this.populates = false;
    }

    /**
     * Returns true if this builder instances automatically populates the created instance's attribute.
     *
     * @return boolean
     */
    boolean populatesAttributes() {
        return populates;
    }

    /**
     * Builds new instance from the original. </p>
     * <p>
     * For some implementations this may mean creating an empty object, others might choose to initialize it using the
     * original data.
     *
     * @param cloneOwner         Instance owning the clone which will be created
     * @param field              Field which will contain the clone
     * @param original           The original object
     * @param cloneConfiguration Configuration for the cloning process
     * @return The clone
     */
    abstract Object buildClone(Object cloneOwner, Field field, Object original, CloneConfiguration cloneConfiguration);

    /**
     * Merges changes from clone to the original.
     *
     * @param field         The field we are merging
     * @param target        target object on which the values are merged
     * @param originalValue The original value
     * @param cloneValue    The clone value
     */
    abstract void mergeChanges(Field field, Object target, Object originalValue, Object cloneValue);

    /**
     * Return the declared constructor for the specified class. If the constructor is not accessible, it is set
     * accessible. If there is no constructor corresponding to the specified argument list, null is returned.
     *
     * @param javaClass The class of the constructor.
     * @param args      An Array of classes, which should take the constructor as parameters.
     * @return Constructor
     * @throws SecurityException If the security check denies access to the constructor.
     */
    protected static Constructor<?> getDeclaredConstructorFor(final Class<?> javaClass, Class<?>[] args) {
        Constructor<?> c;
        try {
            c = javaClass.getDeclaredConstructor(args);
            if (!c.isAccessible()) {
                c.setAccessible(true);
            }
        } catch (NoSuchMethodException e) {
            // No constructor matching the argument types
            return null;
        }
        return c;
    }

    /**
     * This helper method returns the first declared constructor of the specified class. It may be used only in cases
     * when the caller knows exactly which constructor is the first one declared by the class. A use case may be a class
     * with only one declared constructor, which is not a zero argument one.
     *
     * @param javaClass The class whose constructors should be searched.
     * @return The first declared constructor of the specified class.
     */
    protected static Constructor<?> getFirstDeclaredConstructorFor(Class<?> javaClass) {
        Constructor<?>[] constructors = javaClass.getDeclaredConstructors();
        return constructors[0];
    }

    protected static void logConstructorAccessException(Constructor<?> constructor, Exception e) {
        LOG.warn("Exception caught when invoking constructor " + constructor + ". Exception: " + e);
    }

    protected static void logPrivilegedConstructorAccessException(Constructor<?> constructor,
                                                                  PrivilegedActionException e) {
        LOG.warn("Exception caught on privileged invocation of constructor " + constructor + ". Exception: " + e);
    }
}
