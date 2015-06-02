package cz.cvut.kbss.jopa.sessions;

import cz.cvut.kbss.jopa.adapters.IndirectCollection;
import cz.cvut.kbss.jopa.environment.*;
import cz.cvut.kbss.jopa.environment.utils.Generators;
import cz.cvut.kbss.jopa.environment.utils.TestEnvironmentUtils;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CloneBuilderTest {

	CloneBuilderImpl builder;

	private static OWLClassA entityA;
	private static OWLClassB entityB;
	private static OWLClassC entityC;
	private static OWLClassD entityD;
	private static OWLClassM entityM;
	private static Set<String> types;
	private static Set<Class<?>> managedTypes;
	private static EntityDescriptor defaultDescriptor;

	@Mock
	private UnitOfWorkImpl uow;
	@Mock
	private Metamodel metamodel;
	@Mock
	private EntityType<OWLClassA> etA;
	@Mock
	private Identifier identifierA;
	@Mock
	private EntityType<OWLClassB> etB;
	@Mock
	private Identifier identifierB;
	@Mock
	private EntityType<OWLClassC> etC;
	@Mock
	private Identifier identifierC;
	@Mock
	private EntityType<OWLClassD> etD;
	@Mock
	private Identifier identifierD;
	@Mock
	private Attribute strAttMock;
	@Mock
	private TypesSpecification typesMock;
	@Mock
	private Attribute classAAttMock;
	@Mock
	private PropertiesSpecification propertiesMock;
	@Mock
	private Attribute strAttBMock;
	@Mock
	private ListAttribute refListMock;
	@Mock
	private ListAttribute simpleListMock;
	@Mock
	private EntityType<OWLClassM> etM;
	@Mock
	private Identifier identifierM;
	@Mock
	private SingularAttribute mBooleanAttribute;
	@Mock
	private SingularAttribute mIntAttribute;
	@Mock
	private SingularAttribute mLongAttribute;
	@Mock
	private SingularAttribute mDoubleAttribute;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		entityA = new OWLClassA();
		final URI pk = URI.create("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityA");
		entityA.setUri(pk);
		entityA.setStringAttribute("TEST");
		types = new HashSet<>();
		types.add("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityA");
		types.add("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityU");
		types.add("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityX");
		entityA.setTypes(types);
		OWLClassA t2 = new OWLClassA();
		final URI pk2 = URI.create("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityAA");
		t2.setUri(pk2);
		t2.setStringAttribute("TEST2");
		entityB = new OWLClassB();
		entityB.setUri(URI.create("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityB"));
		entityB.setStringAttribute("someString");
		entityC = new OWLClassC();
		entityC.setUri(URI.create("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityC"));
		entityD = new OWLClassD();
		entityD.setUri(URI.create("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityD"));
		entityD.setOwlClassA(entityA);
		entityM = new OWLClassM();
		initManagedTypes();
		defaultDescriptor = new EntityDescriptor();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(uow.isTypeManaged(any())).thenAnswer(invocation -> {
            Class<?> cls = (Class<?>) invocation.getArguments()[0];
            return managedTypes.contains(cls);
        });
		when(uow.getMetamodel()).thenReturn(metamodel);
		when(uow.registerExistingObject(any(), any(Descriptor.class))).thenAnswer(
				invocation -> {
					Object obj = invocation.getArguments()[0];
					Descriptor desc = (Descriptor) invocation.getArguments()[1];
					return builder.buildClone(obj, desc);
				});
		when(metamodel.entity(OWLClassA.class)).thenReturn(etA);
		when(metamodel.entity(OWLClassB.class)).thenReturn(etB);
		when(metamodel.entity(OWLClassC.class)).thenReturn(etC);
		when(metamodel.entity(OWLClassD.class)).thenReturn(etD);
		when(metamodel.entity(OWLClassM.class)).thenReturn(etM);
        TestEnvironmentUtils.initOWLClassAMocks(etA, strAttMock, typesMock, identifierA);
        TestEnvironmentUtils.initOWLClassBMocks(etB, strAttBMock, propertiesMock, identifierB);
        TestEnvironmentUtils.initOWLClassCMocks(etC, simpleListMock, refListMock, identifierC);
        TestEnvironmentUtils.initOWLClassDMocks(etD, classAAttMock, identifierD);
		TestEnvironmentUtils.initOWLClassMMock(etM, mBooleanAttribute, mIntAttribute, mLongAttribute, mDoubleAttribute, identifierM);
		this.builder = new CloneBuilderImpl(uow);
		entityA.setTypes(types);
		entityB.setProperties(null);
		entityC.setReferencedList(null);
		entityC.setSimpleList(null);
        entityM.initializeTestValues(true);
	}

	@Test
	public void testBuildClone() {
		OWLClassA res = (OWLClassA) this.builder.buildClone(entityA, defaultDescriptor);
		assertEquals(res.getStringAttribute(), entityA.getStringAttribute());
		assertTrue(res.getUri().equals(entityA.getUri()));
		assertEquals(entityA.getTypes(), res.getTypes());
	}

	@Test(expected = NullPointerException.class)
	public void testBuildCloneNullOriginal() throws Exception {
		builder.buildClone(null, defaultDescriptor);
		fail("This line should not have been reached.");
	}

	@Test(expected = NullPointerException.class)
	public void testBuildCloneNullContextUri() throws Exception {
		builder.buildClone(entityA, null);
		fail("This line should not have been reached.");
	}

	@Test(expected = NullPointerException.class)
	public void testBuildCloneNullCloneOwner() throws Exception {
		builder.buildClone(null, OWLClassB.getPropertiesField(), entityB, defaultDescriptor);
		fail("This line should not have been reached.");
	}

	@Test
	public void testCloneCollection() {
		final OWLClassA clone = (OWLClassA) builder.buildClone(entityA, defaultDescriptor);
		assertEquals(entityA.getTypes().size(), clone.getTypes().size());
		for (String t : entityA.getTypes()) {
			assertTrue(clone.getTypes().contains(t));
		}
	}

	@Test
	public void testBuildCloneTwice() {
		OWLClassA res = (OWLClassA) this.builder.buildClone(entityA, defaultDescriptor);
		assertEquals(res.getStringAttribute(), entityA.getStringAttribute());
		assertTrue(res.getUri().equals(entityA.getUri()));
		assertEquals(entityA.getTypes(), res.getTypes());
		assertNotSame(entityA, res);
		final OWLClassA resTwo = (OWLClassA) builder.buildClone(entityA, defaultDescriptor);
		assertSame(res, resTwo);
	}

	@Test
	public void testBuildCloneOriginalInUoW() {
		when(uow.containsOriginal(entityA)).thenReturn(Boolean.TRUE);
		when(uow.getCloneForOriginal(entityA)).thenReturn(entityA);
		final OWLClassA res = (OWLClassA) builder.buildClone(entityA, defaultDescriptor);
		assertNotNull(res);
		assertSame(entityA, res);
	}

	@Test
	public void testCloneListCollection() {
		final List<String> testList = new ArrayList<>();
		testList.add("One");
		testList.add("Two");
		testList.add("Three");
		@SuppressWarnings("unchecked")
		List<String> clone = (List<String>) builder.buildClone(testList, defaultDescriptor);
		assertEquals(testList.size(), clone.size());
		Iterator<String> it1 = testList.iterator();
		Iterator<String> it2 = clone.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			assertEquals(it1.next(), it2.next());
		}
	}

	@Test
	public void testCloneSingletonCollection() {
		final OWLClassA obj = new OWLClassA();
		final URI pk = URI.create("http://singletonTest");
		obj.setUri(pk);
		obj.setStringAttribute("TEST");
		String type = "A_type";
		obj.setTypes(Collections.singleton(type));
		final OWLClassA result = (OWLClassA) builder.buildClone(obj, defaultDescriptor);
		assertEquals(1, result.getTypes().size());
		assertEquals(type, result.getTypes().iterator().next());
	}

	@Test
	public void testCloneEmptyList() {
		entityC.setSimpleList(Collections.<OWLClassA> emptyList());
		entityC.setReferencedList(Generators.createReferencedList(10));
		final OWLClassC res = (OWLClassC) builder.buildClone(entityC, defaultDescriptor);
		assertNotNull(res);
		assertTrue(res.getSimpleList().isEmpty());
		for (int i = 0; i < res.getReferencedList().size(); i++) {
			assertEquals(entityC.getReferencedList().get(i).getUri(), res.getReferencedList()
					.get(i).getUri());
		}
	}

	@Test
	public void testCloneEmptySet() {
		final OWLClassA obj = new OWLClassA();
		final URI pk = URI.create("http://singletonTest");
		obj.setUri(pk);
		obj.setStringAttribute("TEST");
		obj.setTypes(Collections.<String> emptySet());
		final OWLClassA res = (OWLClassA) builder.buildClone(obj, defaultDescriptor);
		assertNotNull(res);
		assertTrue(res.getTypes().isEmpty());
	}

	@Test
	public void testCloneProperties() {
		entityB.setProperties(Generators.createProperties(5));
		OWLClassB res = (OWLClassB) builder.buildClone(entityB, defaultDescriptor);
		assertNotNull(res);
		assertEquals(entityB.getUri(), res.getUri());
		assertEquals(entityB.getStringAttribute(), res.getStringAttribute());
		assertEquals(entityB.getProperties().size(), res.getProperties().size());
		assertTrue(res.getProperties() instanceof IndirectCollection);
		for (Entry<String, Set<String>> e : entityB.getProperties().entrySet()) {
			final String k = e.getKey();
			assertTrue(res.getProperties().containsKey(k));
			final Set<String> rv = res.getProperties().get(k);
			assertTrue(rv instanceof IndirectCollection);
			assertEquals(e.getValue().size(), rv.size());
			for (String s : e.getValue()) {
				assertTrue(rv.contains(s));
			}
		}
	}

	@Test
	public void testCloneEmptyMap() {
		entityB.setProperties(Collections.<String, Set<String>> emptyMap());
		OWLClassB res = (OWLClassB) builder.buildClone(entityB, defaultDescriptor);
		assertNotNull(res);
		assertTrue(res.getProperties().isEmpty());
	}

	@Test
	public void testCloneObjectProperty() throws Exception {
		final OWLClassD another = new OWLClassD();
		another.setUri(URI.create("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityDD"));
		another.setOwlClassA(entityA);
		final OWLClassD clOne = (OWLClassD) builder.buildClone(entityD, defaultDescriptor);
		assertNotSame(entityD, clOne);
		assertNotSame(entityA, clOne.getOwlClassA());
		final OWLClassD clTwo = (OWLClassD) builder.buildClone(another, defaultDescriptor);
		assertSame(clOne.getOwlClassA(), clTwo.getOwlClassA());
		assertEquals(entityA.getStringAttribute(), clOne.getOwlClassA().getStringAttribute());
		assertTrue(clOne.getOwlClassA().getTypes() instanceof IndirectCollection);
		final Set<String> tps = clOne.getOwlClassA().getTypes();
		assertEquals(entityA.getTypes().size(), tps.size());
		for (String t : entityA.getTypes()) {
			assertTrue(tps.contains(t));
		}
	}

	@Test
	public void testCloneWithNullCollection() {
		assertNull(entityB.getProperties());
		final OWLClassB res = (OWLClassB) builder.buildClone(entityB, defaultDescriptor);
		assertNotSame(entityB, res);
		assertNull(res.getProperties());
	}

	@Test
	public void testCloneSingletonSet() {
		final Set<String> singleton = Collections
				.singleton("http://krizik.felk.cvut.cz/ontologies/jopa/tests/entityY");
		entityA.setTypes(singleton);
		final OWLClassA res = (OWLClassA) builder.buildClone(entityA, defaultDescriptor);
		assertNotSame(entityA, res);
		assertTrue(res.getTypes() instanceof IndirectCollection);
		assertEquals(1, res.getTypes().size());
		assertTrue(res.getTypes().contains(singleton.iterator().next()));
	}

	@Test
	public void testCloneSingletonMap() {
		final String key = "http://krizik.felk.cvut.cz/ontologies/jopa/attributes#attr";
		final String value = "stringValue";
		final Map<String, Set<String>> m = Collections.singletonMap(key,
				Collections.singleton(value));
		entityB.setProperties(m);
		final OWLClassB res = (OWLClassB) builder.buildClone(entityB, defaultDescriptor);
		assertNotNull(res);
		assertNotSame(entityB, res);
		assertEquals(1, res.getProperties().size());
		assertTrue(res.getProperties() instanceof IndirectCollection);
		final Set<String> s = res.getProperties().get(key);
		assertTrue(s instanceof IndirectCollection);
		assertEquals(1, s.size());
		assertEquals(value, s.iterator().next());
	}

	@Test
	public void testCloneSingletonListWithReference() {
		entityC.setReferencedList(Collections.singletonList(entityA));
		final OWLClassC res = (OWLClassC) builder.buildClone(entityC, defaultDescriptor);
		assertNotSame(res, entityC);
		assertEquals(1, res.getReferencedList().size());
		assertTrue(res.getReferencedList() instanceof IndirectCollection);
		final OWLClassA a = res.getReferencedList().get(0);
		assertNotSame(entityA, a);
		assertEquals(entityA.getUri(), a.getUri());
		assertTrue(a.getTypes() instanceof IndirectCollection);
	}

	@Test
	public void testCloneReferencedList() {
		// Let's see how long this takes
		entityC.setReferencedList(Generators.createReferencedList(100));
		final OWLClassC res = (OWLClassC) builder.buildClone(entityC, defaultDescriptor);
		assertNotSame(entityC, res);
		int size = entityC.getReferencedList().size();
		assertEquals(size, res.getReferencedList().size());
		assertTrue(res.getReferencedList() instanceof IndirectCollection);
		for (int i = 0; i < size; i++) {
			final OWLClassA or = entityC.getReferencedList().get(i);
			final OWLClassA cl = res.getReferencedList().get(i);
			assertNotSame(or, cl);
			assertEquals(or.getUri(), cl.getUri());
			assertEquals(or.getStringAttribute(), cl.getStringAttribute());
			assertEquals(or.getTypes().size(), cl.getTypes().size());
			assertTrue(cl.getTypes() instanceof IndirectCollection);
		}
	}

	@Test
	public void testCloneReferencedListWithNulls() {
		final List<OWLClassA> nulls = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			nulls.add(null);
		}
		entityC.setReferencedList(nulls);
		final OWLClassC res = (OWLClassC) builder.buildClone(entityC, defaultDescriptor);
		assertNotSame(entityC, res);
		int size = entityC.getReferencedList().size();
		assertEquals(size, res.getReferencedList().size());
		assertTrue(res.getReferencedList() instanceof IndirectCollection);
		for (OWLClassA a : res.getReferencedList()) {
			assertNull(a);
		}
	}

	@Test
	public void testReset() throws Exception {
		final Field visitedObjectsField = builder.getClass().getDeclaredField("visitedObjects");
		visitedObjectsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		final Map<Object, Object> visitedObjects = (Map<Object, Object>) visitedObjectsField
				.get(builder);
		assertTrue(visitedObjects.isEmpty());
		final OWLClassA res = (OWLClassA) builder.buildClone(entityA, defaultDescriptor);
		assertNotNull(res);
		assertFalse(visitedObjects.isEmpty());
		builder.reset();
		assertTrue(visitedObjects.isEmpty());
	}

	@Test
	public void testMergeChangesOnString() throws Exception {
		final OWLClassA a = new OWLClassA();
		a.setUri(entityA.getUri());
		a.setStringAttribute("oldString");
		final OWLClassA cloneA = (OWLClassA) builder.buildClone(a, defaultDescriptor);
		final String newStrAtt = "newString";
		cloneA.setStringAttribute(newStrAtt);
		final ObjectChangeSet chSet = ChangeSetFactory.createObjectChangeSet(a, cloneA,
				defaultDescriptor);
		chSet.addChangeRecord(new ChangeRecordImpl(OWLClassA.getStrAttField().getName(), newStrAtt));
		builder.mergeChanges(a, chSet);

		assertEquals(newStrAtt, a.getStringAttribute());
	}

	@Test
	public void testMergeChangesPropertiesFromNull() throws Exception {
		final OWLClassB b = (OWLClassB) builder.buildClone(entityB, defaultDescriptor);
		assertNull(b.getProperties());
		b.setProperties(Generators.createProperties());
		final ObjectChangeSet chSet = ChangeSetFactory.createObjectChangeSet(entityB, b,
				defaultDescriptor);
		chSet.addChangeRecord(new ChangeRecordImpl(OWLClassB.getPropertiesField().getName(), b
				.getProperties()));
		builder.mergeChanges(entityB, chSet);

		assertNotNull(entityB.getProperties());
		assertEquals(b.getProperties(), entityB.getProperties());
	}

	@Test
	public void testMergeChangesRefListFromNull() throws Exception {
		final OWLClassC c = (OWLClassC) builder.buildClone(entityC, defaultDescriptor);
		assertNotSame(entityC, c);
		assertNull(entityC.getReferencedList());
		c.setReferencedList(Generators.createReferencedList(5));
		final ObjectChangeSet chSet = ChangeSetFactory.createObjectChangeSet(entityC, c,
				defaultDescriptor);
		chSet.addChangeRecord(new ChangeRecordImpl(OWLClassC.getRefListField().getName(), c
				.getReferencedList()));
		builder.mergeChanges(entityC, chSet);

		assertNotNull(entityC.getReferencedList());
		for (int i = 0; i < c.getReferencedList().size(); i++) {
			assertEquals(c.getReferencedList().get(i).getUri(), entityC.getReferencedList().get(i)
					.getUri());
		}
	}

	@Test
	public void testBuildCloneWithMultipleWrapperTypesAndStringKey() throws Exception {
		final OWLClassM m = (OWLClassM) builder.buildClone(entityM, defaultDescriptor);
		assertNotSame(entityM, m);
		assertEquals(entityM.getKey(), m.getKey());
		assertEquals(entityM.getBooleanAttribute(), m.getBooleanAttribute());
		assertEquals(entityM.getIntAttribute(), m.getIntAttribute());
		assertEquals(entityM.getLongAttribute(), m.getLongAttribute());
		assertEquals(entityM.getDoubleAttribute(), m.getDoubleAttribute());
	}

	@Test
	public void testMergeChangesWithMultipleWrapperTypesAndStringKey() throws Exception {
		final OWLClassM m = (OWLClassM) builder.buildClone(entityM, defaultDescriptor);
		assertNotSame(entityM, m);
		final ObjectChangeSet changeSet = ChangeSetFactory.createObjectChangeSet(entityM, m, defaultDescriptor);
		m.setBooleanAttribute(!m.getBooleanAttribute());
		changeSet.addChangeRecord(new ChangeRecordImpl(OWLClassM.getBooleanAttributeField().getName(), m.getBooleanAttribute()));
		m.setIntAttribute(11111);
		changeSet.addChangeRecord(new ChangeRecordImpl(OWLClassM.getIntAttributeField().getName(), m.getIntAttribute()));
		m.setLongAttribute(999L);
		changeSet.addChangeRecord(new ChangeRecordImpl(OWLClassM.getLongAttributeField().getName(), m.getLongAttribute()));
		m.setDoubleAttribute(1.1);
		changeSet.addChangeRecord(new ChangeRecordImpl(OWLClassM.getDoubleAttributeField().getName(), m.getDoubleAttribute()));

		builder.mergeChanges(entityM, changeSet);
		assertEquals(m.getBooleanAttribute(), entityM.getBooleanAttribute());
		assertEquals(m.getIntAttribute(), entityM.getIntAttribute());
		assertEquals(m.getLongAttribute(), entityM.getLongAttribute());
		assertEquals(m.getDoubleAttribute(), entityM.getDoubleAttribute());
	}

	private static void initManagedTypes() {
		managedTypes = new HashSet<>();
		managedTypes.add(OWLClassA.class);
		managedTypes.add(OWLClassB.class);
		managedTypes.add(OWLClassC.class);
		managedTypes.add(OWLClassD.class);
		managedTypes.add(OWLClassM.class);
	}
}
