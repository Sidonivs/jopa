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
package cz.cvut.kbss.jopa.example01;

import cz.cvut.kbss.jopa.example01.generated.model.ConferencePaper;
import cz.cvut.kbss.jopa.example01.generated.model.Course;
import cz.cvut.kbss.jopa.example01.generated.model.UndergraduateStudent;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This example works with the entity classes generated by OWL2Java.
 */
public class GeneratedRunner implements Runner {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratedRunner.class);

    private EntityManager em;

    GeneratedRunner() {
        // Where to scan for entity classes
        PersistenceFactory.init(Collections
                .singletonMap(JOPAPersistenceProperties.SCAN_PACKAGE, "cz.cvut.kbss.jopa.example01.generated.model"));
        this.em = PersistenceFactory.createEntityManager();
    }

    @Override
    public void run() {
        try {
            LOG.info("------- JOPA + Sesame Demo - generated object model -------");
            execute();
        } finally {
            em.close();
            PersistenceFactory.close();
        }
    }

    private void execute() {
        LOG.info("Persisting example data...");
        em.getTransaction().begin();
        final UndergraduateStudent student = initStudent();
        em.persist(student);
        student.getTakesCourse().forEach(em::persist);
        student.getIsAuthorOf().forEach(em::persist);
        em.getTransaction().commit();

        LOG.info("Loading example data...");
        final UndergraduateStudent loaded = em.find(UndergraduateStudent.class, student.getId());
        assert loaded != null;
        LOG.info("Loaded {}", loaded);

        LOG.info("Updating example data...");
        em.getTransaction().begin();
        loaded.setTelephone("CTN 0452-9");
        em.getTransaction().commit();

        final UndergraduateStudent result = em.find(UndergraduateStudent.class, student.getId());
        assert loaded.getTelephone().equals(result.getTelephone());
        LOG.info("Loaded {}", result);

        LOG.info("Deleting example data...");
        em.getTransaction().begin();
        em.remove(result);
        em.getTransaction().commit();

        assert em.find(UndergraduateStudent.class, student.getId()) == null;
    }

    private UndergraduateStudent initStudent() {
        final Set<String> types = new HashSet<>();
        types.add("http://www.oni.unsc.org/types#Man");
        types.add("http://www.oni.unsc.org/types#SpartanII");
        final Set<Course> courses = new HashSet<>();
        Course course = new Course();
        course.setId("http://www.Department0.University0.edu/Course45");
        course.setName("Hand combat");
        courses.add(course);
        course = new Course();
        course.setId("http://www.Department0.University0.edu/Course41");
        course.setName("Special Weapons");
        courses.add(course);
        course = new Course();
        course.setId("http://www.Department0.University0.edu/Course23");
        course.setName("Combat tactics");
        courses.add(course);
        course = new Course();
        course.setId("http://www.Department0.University0.edu/Course11");
        course.setName("Halo");
        courses.add(course);
        final UndergraduateStudent student = new UndergraduateStudent();
        student.setId("http://www.oni.unsc.org/spartanII/John117");
        student.setFirstName("Master");
        student.setLastName("Chief");
        student.setEmailAddress("spartan-117@unsc.org");
        student.setTelephone("xxxxxxxxxxxx-xxxx");
        student.setTypes(types);
        final ConferencePaper paper = new ConferencePaper();
        paper.setName1("ConferencePaperP");
        student.setIsAuthorOf(Collections.singleton(paper));
        student.setTakesCourse(courses);
        return student;
    }
}
